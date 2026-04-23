package com.factorysimulation.sim;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import com.factorysimulation.model.Adjuster;
import com.factorysimulation.model.MachineCategory;
import com.factorysimulation.model.SimulationResult;

public class SimulationEngine {
  private static final int FAILURE_EVENT = 0;
  private static final int REPAIR_COMPLETE_EVENT = 1;

  public SimulationResult run(List<MachineCategory> categories, List<Adjuster> adjusters, double simulationHours,
      long seed) {
    if (categories == null || categories.isEmpty()) {
      throw new IllegalArgumentException("At least one machine category is required");
    }
    if (adjusters == null || adjusters.isEmpty()) {
      throw new IllegalArgumentException("At least one adjuster is required");
    }
    if (simulationHours <= 0.0) {
      throw new IllegalArgumentException("Simulation duration must be greater than zero");
    }

    Random random = new Random(seed);
    List<MachineInstance> machines = new ArrayList<MachineInstance>();
    for (MachineCategory category : categories) {
      for (int i = 1; i <= category.getQuantity(); i++) {
        machines.add(new MachineInstance(category, category.getName() + "-" + i));
      }
    }

    List<AdjusterState> adjusterStates = new ArrayList<AdjusterState>();
    for (Adjuster adjuster : adjusters) {
      adjusterStates.add(new AdjusterState(adjuster));
    }

    PriorityQueue<Event> events = new PriorityQueue<Event>(Comparator
        .comparingDouble(Event::getTime)
        .thenComparingInt(Event::getPriority));

    for (MachineInstance machine : machines) {
      double firstFailureTime = sampleExponential(random, machine.category.getMeanTimeToFailureHours());
      if (firstFailureTime <= simulationHours) {
        events.add(new Event(firstFailureTime, FAILURE_EVENT, machine, null));
      }
    }

    Deque<MachineInstance> waitingQueue = new ArrayDeque<MachineInstance>();
    Map<String, Integer> failuresByCategory = new LinkedHashMap<String, Integer>();
    for (MachineCategory category : categories) {
      failuresByCategory.put(normalize(category.getName()), Integer.valueOf(0));
    }

    Stats stats = new Stats();
    while (!events.isEmpty()) {
      Event event = events.poll();
      if (event.getTime() > simulationHours) {
        break;
      }

      if (event.getType() == FAILURE_EVENT) {
        MachineInstance machine = event.getMachine();
        machine.markFailed(event.getTime());
        String key = normalize(machine.category.getName());
        failuresByCategory.put(key, failuresByCategory.get(key) + 1);

        recordQueueChange(stats, waitingQueue.size(), event.getTime());
        waitingQueue.addLast(machine);
        stats.peakQueueLength = Math.max(stats.peakQueueLength, waitingQueue.size());

        assignAvailableWork(event.getTime(), waitingQueue, adjusterStates, events, stats);
      } else {
        AdjusterState adjusterState = event.getAdjusterState();
        adjusterState.finishBusyPeriod(event.getTime());
        MachineInstance machine = event.getMachine();
        machine.markRepaired(event.getTime());

        double nextFailureTime = event.getTime()
            + sampleExponential(random, machine.category.getMeanTimeToFailureHours());
        if (nextFailureTime <= simulationHours) {
          events.add(new Event(nextFailureTime, FAILURE_EVENT, machine, null));
        }

        assignAvailableWork(event.getTime(), waitingQueue, adjusterStates, events, stats);
      }
    }

    for (MachineInstance machine : machines) {
      machine.closeAt(simulationHours);
    }
    for (AdjusterState adjusterState : adjusterStates) {
      adjusterState.closeAt(simulationHours);
    }

    stats.queueArea += waitingQueue.size() * Math.max(0.0, simulationHours - stats.lastQueueChangeTime);
    double averageQueueLength = stats.queueArea / simulationHours;
    double machineUtilization = calculateMachineUtilization(machines, simulationHours);
    double adjusterUtilization = calculateAdjusterUtilization(adjusterStates, simulationHours);
    double averageWaitingTime = stats.completedRepairs == 0 ? 0.0 : stats.totalWaitingTime / stats.completedRepairs;

    List<SimulationResult.CategorySummary> categorySummaries = new ArrayList<SimulationResult.CategorySummary>();
    for (MachineCategory category : categories) {
      categorySummaries.add(new SimulationResult.CategorySummary(
          category.getName(),
          category.getQuantity(),
          failuresByCategory.get(normalize(category.getName())),
          calculateCategoryUtilization(machines, category.getName(), simulationHours)));
    }

    List<SimulationResult.AdjusterSummary> adjusterSummaries = new ArrayList<SimulationResult.AdjusterSummary>();
    for (AdjusterState adjusterState : adjusterStates) {
      adjusterSummaries.add(new SimulationResult.AdjusterSummary(
          adjusterState.adjuster.getId(),
          adjusterState.adjuster.getName(),
          adjusterState.adjuster.skillsAsText(),
          adjusterState.repairsCompleted,
          adjusterState.getUtilization(simulationHours)));
    }

    List<String> recommendations = buildRecommendations(
        machineUtilization,
        adjusterUtilization,
        averageWaitingTime,
        stats.peakQueueLength,
        waitingQueue.size(),
        adjusterStates.size(),
        machines.size());

    return new SimulationResult(
        machineUtilization,
        adjusterUtilization,
        stats.completedRepairs,
        averageWaitingTime,
        stats.peakQueueLength,
        averageQueueLength,
        waitingQueue.size(),
        categorySummaries,
        adjusterSummaries,
        recommendations);
  }

  private void assignAvailableWork(double currentTime,
      Deque<MachineInstance> waitingQueue,
      List<AdjusterState> adjusterStates,
      PriorityQueue<Event> events,
      Stats stats) {
    for (AdjusterState adjusterState : adjusterStates) {
      if (adjusterState.isBusy()) {
        continue;
      }

      MachineInstance machine = findAssignableMachine(waitingQueue, adjusterState.adjuster);
      if (machine == null) {
        continue;
      }

      recordQueueChange(stats, waitingQueue.size(), currentTime);
      waitingQueue.remove(machine);

      stats.totalWaitingTime += Math.max(0.0, currentTime - machine.failureTime);
      stats.completedRepairs += 1;

      double repairCompletionTime = currentTime + machine.category.getRepairTimeHours();
      adjusterState.startBusyPeriod(currentTime);
      adjusterState.repairsCompleted += 1;
      events.add(new Event(repairCompletionTime, REPAIR_COMPLETE_EVENT, machine, adjusterState));
    }
  }

  private MachineInstance findAssignableMachine(Deque<MachineInstance> waitingQueue, Adjuster adjuster) {
    for (MachineInstance machine : waitingQueue) {
      if (adjuster.canRepair(machine.category.getName())) {
        return machine;
      }
    }
    return null;
  }

  private void recordQueueChange(Stats stats, int queueLengthBeforeChange, double currentTime) {
    stats.queueArea += queueLengthBeforeChange * Math.max(0.0, currentTime - stats.lastQueueChangeTime);
    stats.lastQueueChangeTime = currentTime;
  }

  private double sampleExponential(Random random, double mean) {
    double u = Math.max(1.0e-12, random.nextDouble());
    return -mean * Math.log(1.0 - u);
  }

  private double calculateMachineUtilization(List<MachineInstance> machines, double simulationHours) {
    double runningTime = 0.0;
    for (MachineInstance machine : machines) {
      runningTime += machine.runningTime;
    }
    return machines.isEmpty() ? 0.0 : runningTime / (simulationHours * machines.size());
  }

  private double calculateAdjusterUtilization(List<AdjusterState> adjusterStates, double simulationHours) {
    double busyTime = 0.0;
    for (AdjusterState adjusterState : adjusterStates) {
      busyTime += adjusterState.busyTime;
    }
    return adjusterStates.isEmpty() ? 0.0 : busyTime / (simulationHours * adjusterStates.size());
  }

  private double calculateCategoryUtilization(List<MachineInstance> machines, String categoryName,
      double simulationHours) {
    double runningTime = 0.0;
    int machineCount = 0;
    String normalizedName = normalize(categoryName);
    for (MachineInstance machine : machines) {
      if (normalize(machine.category.getName()).equals(normalizedName)) {
        runningTime += machine.runningTime;
        machineCount += 1;
      }
    }
    return machineCount == 0 ? 0.0 : runningTime / (simulationHours * machineCount);
  }

  private List<String> buildRecommendations(double machineUtilization,
      double adjusterUtilization,
      double averageWaitingTime,
      int peakQueueLength,
      int unresolvedQueueCount,
      int adjusterCount,
      int machineCount) {
    List<String> recommendations = new ArrayList<String>();
    if (machineUtilization < 0.70) {
      recommendations.add("Machine utilization is low; review failure rates and repair delays.");
    }
    if (adjusterUtilization > 0.85) {
      recommendations.add("Adjusters are heavily loaded; consider hiring more adjusters or expanding skills.");
    }
    if (averageWaitingTime > 1.0) {
      recommendations.add("Average waiting time is high; reduce queue delays by balancing skills across adjusters.");
    }
    if (peakQueueLength > Math.max(1, adjusterCount)) {
      recommendations.add("Peak queue length exceeds the number of adjusters; more maintenance capacity may help.");
    }
    if (unresolvedQueueCount > 0) {
      recommendations
          .add("Some machines were still waiting at the end of the run; extend the duration or add capacity.");
    }
    if (recommendations.isEmpty()) {
      recommendations.add("The current setup is balanced for the selected simulation window.");
    }
    if (machineCount > 100 && adjusterCount < 3) {
      recommendations.add("Large fleets usually need more adjusters or broader repair expertise.");
    }
    return recommendations;
  }

  private String normalize(String value) {
    return value.trim().toLowerCase(Locale.ENGLISH);
  }

  private static class Stats {
    private double totalWaitingTime;
    private int completedRepairs;
    private double queueArea;
    private double lastQueueChangeTime;
    private int peakQueueLength;
  }

  private static class MachineInstance {
    private final MachineCategory category;
    private final String id;
    private double failureTime;
    private double runningSince = 0.0;
    private double runningTime = 0.0;
    private boolean failed = false;

    private MachineInstance(MachineCategory category, String id) {
      this.category = category;
      this.id = id;
    }

    private void markFailed(double time) {
      if (!failed) {
        runningTime += Math.max(0.0, time - runningSince);
      }
      failed = true;
      failureTime = time;
    }

    private void markRepaired(double time) {
      failed = false;
      runningSince = time;
    }

    private void closeAt(double simulationHours) {
      if (!failed) {
        runningTime += Math.max(0.0, simulationHours - runningSince);
        runningSince = simulationHours;
      }
    }
  }

  private static class AdjusterState {
    private final Adjuster adjuster;
    private boolean busy;
    private double busyStart;
    private double busyTime;
    private int repairsCompleted;

    private AdjusterState(Adjuster adjuster) {
      this.adjuster = adjuster;
    }

    private boolean isBusy() {
      return busy;
    }

    private void startBusyPeriod(double time) {
      busy = true;
      busyStart = time;
    }

    private void finishBusyPeriod(double time) {
      if (busy) {
        busyTime += Math.max(0.0, time - busyStart);
      }
      busy = false;
    }

    private void closeAt(double simulationHours) {
      if (busy) {
        busyTime += Math.max(0.0, simulationHours - busyStart);
        busy = false;
      }
    }

    private double getUtilization(double simulationHours) {
      return simulationHours == 0.0 ? 0.0 : busyTime / simulationHours;
    }
  }

  private static class Event {
    private final double time;
    private final int type;
    private final MachineInstance machine;
    private final AdjusterState adjusterState;

    private Event(double time, int type, MachineInstance machine, AdjusterState adjusterState) {
      this.time = time;
      this.type = type;
      this.machine = machine;
      this.adjusterState = adjusterState;
    }

    private double getTime() {
      return time;
    }

    private int getType() {
      return type;
    }

    private int getPriority() {
      return type == REPAIR_COMPLETE_EVENT ? 0 : 1;
    }

    private MachineInstance getMachine() {
      return machine;
    }

    private AdjusterState getAdjusterState() {
      return adjusterState;
    }
  }
}
