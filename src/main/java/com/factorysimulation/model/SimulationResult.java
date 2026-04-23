package com.factorysimulation.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulationResult {
  private final double machineUtilization;
  private final double adjusterUtilization;
  private final int totalFailures;
  private final double averageWaitingTime;
  private final int peakQueueLength;
  private final double averageQueueLength;
  private final int unresolvedQueueCount;
  private final List<CategorySummary> categorySummaries;
  private final List<AdjusterSummary> adjusterSummaries;
  private final List<String> recommendations;

  public SimulationResult(double machineUtilization,
      double adjusterUtilization,
      int totalFailures,
      double averageWaitingTime,
      int peakQueueLength,
      double averageQueueLength,
      int unresolvedQueueCount,
      List<CategorySummary> categorySummaries,
      List<AdjusterSummary> adjusterSummaries,
      List<String> recommendations) {
    this.machineUtilization = machineUtilization;
    this.adjusterUtilization = adjusterUtilization;
    this.totalFailures = totalFailures;
    this.averageWaitingTime = averageWaitingTime;
    this.peakQueueLength = peakQueueLength;
    this.averageQueueLength = averageQueueLength;
    this.unresolvedQueueCount = unresolvedQueueCount;
    this.categorySummaries = new ArrayList<CategorySummary>(categorySummaries);
    this.adjusterSummaries = new ArrayList<AdjusterSummary>(adjusterSummaries);
    this.recommendations = new ArrayList<String>(recommendations);
  }

  public double getMachineUtilization() {
    return machineUtilization;
  }

  public double getAdjusterUtilization() {
    return adjusterUtilization;
  }

  public int getTotalFailures() {
    return totalFailures;
  }

  public double getAverageWaitingTime() {
    return averageWaitingTime;
  }

  public int getPeakQueueLength() {
    return peakQueueLength;
  }

  public double getAverageQueueLength() {
    return averageQueueLength;
  }

  public int getUnresolvedQueueCount() {
    return unresolvedQueueCount;
  }

  public List<CategorySummary> getCategorySummaries() {
    return Collections.unmodifiableList(categorySummaries);
  }

  public List<AdjusterSummary> getAdjusterSummaries() {
    return Collections.unmodifiableList(adjusterSummaries);
  }

  public List<String> getRecommendations() {
    return Collections.unmodifiableList(recommendations);
  }

  public String buildOverviewText() {
    StringBuilder builder = new StringBuilder();
    builder.append("Simulation completed successfully.\n\n");
    builder.append("Machine utilization: ").append(formatPercent(machineUtilization)).append('\n');
    builder.append("Adjuster utilization: ").append(formatPercent(adjusterUtilization)).append('\n');
    builder.append("Total failures: ").append(totalFailures).append('\n');
    builder.append("Average waiting time: ").append(formatHours(averageWaitingTime)).append('\n');
    builder.append("Peak queue length: ").append(peakQueueLength).append('\n');
    builder.append("Average queue length: ").append(formatNumber(averageQueueLength)).append('\n');
    builder.append("Unresolved queue at end: ").append(unresolvedQueueCount).append('\n');
    builder.append('\n');
    builder.append("Recommendations:\n");
    for (String recommendation : recommendations) {
      builder.append("- ").append(recommendation).append('\n');
    }
    return builder.toString();
  }

  private String formatPercent(double value) {
    return String.format("%.2f%%", value * 100.0);
  }

  private String formatHours(double value) {
    return String.format("%.2f hours", value);
  }

  private String formatNumber(double value) {
    return String.format("%.2f", value);
  }

  public static class CategorySummary {
    private final String categoryName;
    private final int machineCount;
    private final int failureCount;
    private final double utilization;

    public CategorySummary(String categoryName, int machineCount, int failureCount, double utilization) {
      this.categoryName = categoryName;
      this.machineCount = machineCount;
      this.failureCount = failureCount;
      this.utilization = utilization;
    }

    public String getCategoryName() {
      return categoryName;
    }

    public int getMachineCount() {
      return machineCount;
    }

    public int getFailureCount() {
      return failureCount;
    }

    public double getUtilization() {
      return utilization;
    }
  }

  public static class AdjusterSummary {
    private final String adjusterId;
    private final String adjusterName;
    private final String skills;
    private final int repairsCompleted;
    private final double utilization;

    public AdjusterSummary(String adjusterId, String adjusterName, String skills, int repairsCompleted,
        double utilization) {
      this.adjusterId = adjusterId;
      this.adjusterName = adjusterName;
      this.skills = skills;
      this.repairsCompleted = repairsCompleted;
      this.utilization = utilization;
    }

    public String getAdjusterId() {
      return adjusterId;
    }

    public String getAdjusterName() {
      return adjusterName;
    }

    public String getSkills() {
      return skills;
    }

    public int getRepairsCompleted() {
      return repairsCompleted;
    }

    public double getUtilization() {
      return utilization;
    }
  }
}
