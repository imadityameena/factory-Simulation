# Factory Machine Simulation System - PowerPoint Presentation Guide

---

## 1. PROJECT OVERVIEW

### Vision
A Java Swing desktop application that simulates factory machine failures, repair queues, and maintenance worker (adjuster) utilization to help identify bottlenecks and optimize resource allocation.

### Objectives
- Simulate real-world factory maintenance scenarios
- Analyze machine failure patterns and repair queues
- Measure resource utilization (machines and adjusters)
- Provide data-driven recommendations for optimization
- Support deterministic simulations for reproducible analysis

### Target Users
- Factory managers and operations planners
- Maintenance coordinators
- Supply chain optimization specialists

### Key Business Value
- Identify bottlenecks in repair processes
- Optimize adjuster allocation
- Forecast maintenance requirements
- Improve equipment uptime and factory efficiency

---

## 2. KEY FUNCTIONALITIES

### 2.1 Machine Management
- **Add/Remove Machine Categories** — Define types of machines with specific MTTF and repair times
- **Quantity Configuration** — Specify how many machines exist in each category
- **Failure Pattern Definition** — Configure Mean Time To Failure (MTTF) for each category
- **Repair Time Configuration** — Define repair duration for each machine type

### 2.2 Adjuster (Maintenance Worker) Management
- **Create Adjusters** — Register maintenance workers with unique identities
- **Skill Assignment** — Assign repair skills/expertise to each adjuster (e.g., "Mechanical", "Electrical")
- **Skill-Based Assignment** — Only qualified adjusters repair specific machine categories
- **Remove Adjusters** — Delete adjusters from the system

### 2.3 Simulation Execution
- **Configurable Duration** — Set simulation length (in hours)
- **Random Seed Control** — Use seeds for deterministic/reproducible simulations
- **Event-Driven Processing** — Process machine failures and repairs in chronological order
- **Real-Time Event Handling** — Assign available adjusters to waiting repairs immediately

### 2.4 Metrics & Analysis
- **Machine Utilization** — Percentage of time machines are operational vs. failed
- **Adjuster Utilization** — Percentage of time adjusters are busy repairing machines
- **Failure Tracking** — Total number of failures per machine category
- **Queue Analysis** — Peak queue length, average queue length over time
- **Waiting Time Analysis** — Average time a machine waits before repair begins

### 2.5 Reporting & Recommendations
- **Category Summary** — Per-machine-category failure count and utilization
- **Adjuster Summary** — Per-adjuster repairs completed and utilization metrics
- **Bottleneck Identification** — Recommendations for increasing adjuster count or skill coverage
- **Textual Insights** — Auto-generated suggestions based on utilization thresholds

---

## 3. FUNCTIONAL REQUIREMENTS

| ID | Requirement | Description |
|---|---|---|
| FR-1 | Machine Category Setup | System shall allow users to add/remove machine categories with name, quantity, MTTF, and repair time |
| FR-2 | Adjuster Setup | System shall allow users to add/remove adjusters with ID, name, and skills |
| FR-3 | Simulation Configuration | System shall allow users to set simulation duration and random seed |
| FR-4 | Run Simulation | System shall execute discrete-event simulation of machine failures and repairs |
| FR-5 | Deterministic Simulation | System shall produce identical results when given the same seed and input parameters |
| FR-6 | Event Processing | System shall process failures and repairs in chronological priority order |
| FR-7 | Skill Matching | System shall assign repairs only to adjusters with matching skills for the machine category |
| FR-8 | Queue Management | System shall maintain FIFO queue of failed machines waiting for repair |
| FR-9 | Utilization Metrics | System shall calculate machine and adjuster utilization percentages |
| FR-10 | Failure Tracking | System shall track failure counts per machine category |
| FR-11 | Waiting Time Analysis | System shall calculate average waiting time for repairs |
| FR-12 | Peak Queue Tracking | System shall identify peak queue length during simulation |
| FR-13 | Recommendations Engine | System shall generate optimization recommendations based on utilization and queue metrics |
| FR-14 | Results Visualization | System shall display results in tabular format with category and adjuster breakdowns |
| FR-15 | Multi-Tab UI | System shall provide tabbed interface for setup (machines, adjusters, simulation) and results |

---

## 4. NON-FUNCTIONAL REQUIREMENTS

| ID | Requirement | Category | Description |
|---|---|---|---|
| NFR-1 | Performance | Performance | Simulation of 1 year (8760 hours) with 100+ machines and 10+ adjusters shall complete in < 5 seconds |
| NFR-2 | Determinism | Reliability | Identical random seed shall always produce identical results |
| NFR-3 | Accuracy | Correctness | Simulation events shall be processed in chronological order with correct priority handling |
| NFR-4 | Memory Efficiency | Performance | Application shall use < 500MB RAM for typical scenarios |
| NFR-5 | UI Responsiveness | Usability | UI shall remain responsive during and after simulation execution |
| NFR-6 | Data Integrity | Reliability | Immutable data models shall prevent unintended state changes |
| NFR-7 | Cross-Platform | Compatibility | Java 11+ compatibility; run on Windows, macOS, Linux |
| NFR-8 | No External Dependencies | Maintainability | Use only Java standard library (no external dependencies) |
| NFR-9 | Code Organization | Maintainability | Follow MVC-like separation: model (data), sim (logic), ui (presentation) |
| NFR-10 | User Documentation | Usability | Provide README with setup, run instructions, and sample data |
| NFR-11 | Error Handling | Reliability | Gracefully handle invalid inputs (negative values, empty selections) |
| NFR-12 | Accessibility | Usability | UI shall be keyboard navigable and use standard Swing components |

---

## 5. DATA FLOW DIAGRAM

```
┌─────────────────────────────────────────────────────────────┐
│                         USER INPUT                          │
├─────────────────────────────────────────────────────────────┤
│  • Machine Categories (name, qty, MTTF, repairTime)         │
│  • Adjusters (id, name, skills)                             │
│  • Simulation Config (duration, seed)                       │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
         ┌─────────────────────────────────┐
         │      DATA VALIDATION            │
         │  ✓ No negative values           │
         │  ✓ Skills defined               │
         │  ✓ Machines configured          │
         └────────────┬────────────────────┘
                      │
                      ▼
      ┌──────────────────────────────────────┐
      │   SIMULATION ENGINE INITIALIZATION   │
      ├──────────────────────────────────────┤
      │ • Create MachineInstance for each    │
      │ • Initialize AdjusterState           │
      │ • Seed Random number generator       │
      │ • Schedule initial failures          │
      └────────────┬─────────────────────────┘
                   │
                   ▼
    ┌──────────────────────────────────────────┐
    │      DISCRETE-EVENT SIMULATION LOOP      │
    ├──────────────────────────────────────────┤
    │                                          │
    │  Priority Queue: Events sorted by time   │
    │  ┌─────────────────────────────────────┐ │
    │  │ While (current_time < duration):    │ │
    │  │                                     │ │
    │  │  1. Pop next event                  │ │
    │  │  2. IF FAILURE_EVENT:               │ │
    │  │     - Mark machine failed           │ │
    │  │     - Add to repair queue           │ │
    │  │     - Update queue metrics          │ │
    │  │     - Try to assign adjuster        │ │
    │  │                                     │ │
    │  │  3. IF REPAIR_COMPLETE_EVENT:       │ │
    │  │     - Update adjuster busy time     │ │
    │  │     - Mark machine repaired         │ │
    │  │     - Schedule next failure         │ │
    │  │     - Try to assign waiting work    │ │
    │  │                                     │ │
    │  └─────────────────────────────────────┘ │
    │                                          │
    └────────────┬─────────────────────────────┘
                 │
                 ▼
    ┌──────────────────────────────────────┐
    │  STATISTICS AGGREGATION              │
    ├──────────────────────────────────────┤
    │ • Calculate utilization percentages  │
    │ • Compute queue statistics           │
    │ • Aggregate by category & adjuster   │
    │ • Generate recommendations           │
    └────────────┬─────────────────────────┘
                 │
                 ▼
    ┌──────────────────────────────────────┐
    │  SimulationResult Object             │
    ├──────────────────────────────────────┤
    │ • machineUtilization: double         │
    │ • adjusterUtilization: double        │
    │ • totalFailures: int                 │
    │ • averageWaitingTime: double         │
    │ • peakQueueLength: int               │
    │ • averageQueueLength: double         │
    │ • categorySummaries: List             │
    │ • adjusterSummaries: List             │
    │ • recommendations: List               │
    └────────────┬─────────────────────────┘
                 │
                 ▼
    ┌──────────────────────────────────────┐
    │  RESULTS DISPLAY (UI)                │
    ├──────────────────────────────────────┤
    │ • Metrics Cards (key indicators)     │
    │ • Overview Tab (summary text)        │
    │ • Category Metrics Tab (table)       │
    │ • Adjuster Metrics Tab (table)       │
    └──────────────────────────────────────┘
```

---

## 6. SYSTEM ARCHITECTURE DIAGRAM

```
┌─────────────────────────────────────────────────────────────────┐
│                    FACTORY SIMULATION SYSTEM                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐    │
│  │                   UI LAYER (Swing)                     │    │
│  ├────────────────────────────────────────────────────────┤    │
│  │  MainFrame                                              │    │
│  │  ├── Left Panel: Setup Tabs                            │    │
│  │  │   ├── Machine Setup Tab                             │    │
│  │  │   ├── Adjusters Tab                                 │    │
│  │  │   └── Simulation Tab                                │    │
│  │  └── Right Panel: Results Tabs                         │    │
│  │      ├── Metrics Cards                                 │    │
│  │      ├── Overview Tab                                  │    │
│  │      ├── Category Metrics Tab                          │    │
│  │      └── Adjuster Metrics Tab                          │    │
│  └────────────────────────────────────────────────────────┘    │
│                           │                                     │
│                           ▼                                     │
│  ┌────────────────────────────────────────────────────────┐    │
│  │             SIMULATION LAYER (Business Logic)          │    │
│  ├────────────────────────────────────────────────────────┤    │
│  │  SimulationEngine                                       │    │
│  │  ├── Event Queue (Priority Queue)                       │    │
│  │  ├── MachineInstance (inner class)                      │    │
│  │  ├── AdjusterState (inner class)                        │    │
│  │  ├── Event (inner class)                               │    │
│  │  ├── Stats (inner class)                               │    │
│  │  └── Core Methods:                                      │    │
│  │      ├── run() - Main simulation loop                  │    │
│  │      ├── assignAvailableWork() - Skill matching       │    │
│  │      ├── processFailureEvent()                         │    │
│  │      └── processRepairCompleteEvent()                  │    │
│  └────────────────────────────────────────────────────────┘    │
│                           │                                     │
│                           ▼                                     │
│  ┌────────────────────────────────────────────────────────┐    │
│  │               DATA LAYER (Models)                      │    │
│  ├────────────────────────────────────────────────────────┤    │
│  │  MachineCategory                                        │    │
│  │  ├── name: String                                       │    │
│  │  ├── quantity: int                                      │    │
│  │  ├── mttf: double                                       │    │
│  │  └── repairTime: double                                 │    │
│  │                                                         │    │
│  │  Adjuster                                               │    │
│  │  ├── id: int                                            │    │
│  │  ├── name: String                                       │    │
│  │  ├── skills: Set<String>                                │    │
│  │  └── canRepair(category): boolean                       │    │
│  │                                                         │    │
│  │  SimulationResult (Immutable)                           │    │
│  │  ├── machineUtilization: double                         │    │
│  │  ├── adjusterUtilization: double                        │    │
│  │  ├── totalFailures: int                                 │    │
│  │  ├── averageWaitingTime: double                         │    │
│  │  ├── peakQueueLength: int                               │    │
│  │  ├── categorySummaries: List                            │    │
│  │  ├── adjusterSummaries: List                            │    │
│  │  └── recommendations: List                              │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 7. CLASS DIAGRAM (UML)

```
┌──────────────────────────────────────────────────────────────────────┐
│                         com.factorysimulation                        │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ─┐ │
│  │        <<com.factorysimulation.model>>                       │ │
│  │ + - - - - - - - - - - - - - - - - - - - - - - - - - - - - -+ │ │
│  │                                                              │ │
│  │  ┌──────────────────────────────────────┐                   │ │
│  │  │   MachineCategory                   │                   │ │
│  │  ├──────────────────────────────────────┤                   │ │
│  │  │ - name: String                      │                   │ │
│  │  │ - quantity: int                     │                   │ │
│  │  │ - mttf: double                      │                   │ │
│  │  │ - repairTime: double                │                   │ │
│  │  ├──────────────────────────────────────┤                   │ │
│  │  │ + getName(): String                │                   │ │
│  │  │ + getQuantity(): int                │                   │ │
│  │  │ + getMTTF(): double                 │                   │ │
│  │  │ + getRepairTime(): double           │                   │ │
│  │  │ + toString(): String                │                   │ │
│  │  └──────────────────────────────────────┘                   │ │
│  │                                                              │ │
│  │  ┌──────────────────────────────────────┐                   │ │
│  │  │   Adjuster                          │                   │ │
│  │  ├──────────────────────────────────────┤                   │ │
│  │  │ - id: int                           │                   │ │
│  │  │ - name: String                      │                   │ │
│  │  │ - skills: Set<String>               │                   │ │
│  │  ├──────────────────────────────────────┤                   │ │
│  │  │ + getId(): int                      │                   │ │
│  │  │ + getName(): String                 │                   │ │
│  │  │ + getSkills(): Set<String>          │                   │ │
│  │  │ + canRepair(category): boolean      │                   │ │
│  │  │ + toString(): String                │                   │ │
│  │  └──────────────────────────────────────┘                   │ │
│  │                                                              │ │
│  │  ┌──────────────────────────────────────┐                   │ │
│  │  │   SimulationResult                  │                   │ │
│  │  ├──────────────────────────────────────┤                   │ │
│  │  │ - machineUtilization: double        │                   │ │
│  │  │ - adjusterUtilization: double       │                   │ │
│  │  │ - totalFailures: int                │                   │ │
│  │  │ - averageWaitingTime: double        │                   │ │
│  │  │ - peakQueueLength: int              │                   │ │
│  │  │ - averageQueueLength: double        │                   │ │
│  │  │ - unresolvedQueueCount: int         │                   │ │
│  │  │ - categorySummaries: List           │                   │ │
│  │  │ - adjusterSummaries: List           │                   │ │
│  │  │ - recommendations: List             │                   │ │
│  │  ├──────────────────────────────────────┤                   │ │
│  │  │ + getters for all fields            │                   │ │
│  │  │ + toString(): String                │                   │ │
│  │  └──────────────────────────────────────┘                   │ │
│  │                                                              │ │
│  │  ┌──────────────────────────────────────┐                   │ │
│  │  │   CategorySummary (nested)          │                   │ │
│  │  ├──────────────────────────────────────┤                   │ │
│  │  │ - categoryName: String              │                   │ │
│  │  │ - machineCount: int                 │                   │ │
│  │  │ - failureCount: int                 │                   │ │
│  │  │ - utilization: double               │                   │ │
│  │  └──────────────────────────────────────┘                   │ │
│  │                                                              │ │
│  │  ┌──────────────────────────────────────┐                   │ │
│  │  │   AdjusterSummary (nested)          │                   │ │
│  │  ├──────────────────────────────────────┤                   │ │
│  │  │ - adjusterId: int                   │                   │ │
│  │  │ - adjusterName: String              │                   │ │
│  │  │ - skills: Set<String>               │                   │ │
│  │  │ - repairsCompleted: int             │                   │ │
│  │  │ - utilization: double               │                   │ │
│  │  └──────────────────────────────────────┘                   │ │
│  │                                                              │ │
│  └─ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ─┘ │
│                                                                      │
│  ┌─ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ─┐ │
│  │        <<com.factorysimulation.sim>>                         │ │
│  │ + - - - - - - - - - - - - - - - - - - - - - - - - - - - - -+ │ │
│  │                                                              │ │
│  │  ┌──────────────────────────────────────────────┐            │ │
│  │  │   SimulationEngine                          │            │ │
│  │  ├──────────────────────────────────────────────┤            │ │
│  │  │ - categories: MachineCategory[]              │            │ │
│  │  │ - adjusters: Adjuster[]                      │            │ │
│  │  │ - duration: double                           │            │ │
│  │  │ - seed: long                                 │            │ │
│  │  │ - random: Random                             │            │ │
│  │  │ - eventQueue: PriorityQueue<Event>           │            │ │
│  │  │ - machineInstances: List<MachineInstance>    │            │ │
│  │  │ - adjusterStates: List<AdjusterState>        │            │ │
│  │  │ - stats: Stats                               │            │ │
│  │  ├──────────────────────────────────────────────┤            │ │
│  │  │ + run(): SimulationResult                    │            │ │
│  │  │ - assignAvailableWork(): void                │            │ │
│  │  │ - processFailureEvent(Event): void           │            │ │
│  │  │ - processRepairCompleteEvent(Event): void    │            │ │
│  │  │ - generateRecommendations(): List<String>    │            │ │
│  │  └──────────────────────────────────────────────┘            │ │
│  │                                                              │ │
│  │  [Inner Classes]                                            │ │
│  │  ┌──────────────────┐  ┌──────────────────┐                 │ │
│  │  │ Event            │  │ MachineInstance  │                 │ │
│  │  ├──────────────────┤  ├──────────────────┤                 │ │
│  │  │ - time: double   │  │ - category       │                 │ │
│  │  │ - type: int      │  │ - id: int        │                 │ │
│  │  │ - machine        │  │ - failureTime    │                 │ │
│  │  │ - adjuster       │  │ - runningSince   │                 │ │
│  │  │ - priority       │  │ - runningTime    │                 │ │
│  │  │                  │  │ - failed: boolean│                 │ │
│  │  └──────────────────┘  └──────────────────┘                 │ │
│  │                                                              │ │
│  │  ┌──────────────────┐  ┌──────────────────┐                 │ │
│  │  │ AdjusterState    │  │ Stats            │                 │ │
│  │  ├──────────────────┤  ├──────────────────┤                 │ │
│  │  │ - adjuster       │  │ - totalWaiting   │                 │ │
│  │  │ - busy: boolean  │  │ - completed      │                 │ │
│  │  │ - busyStart      │  │ - queueArea      │                 │ │
│  │  │ - busyTime       │  │ - peakQueue      │                 │ │
│  │  │ - repairsComp    │  │                  │                 │ │
│  │  └──────────────────┘  └──────────────────┘                 │ │
│  │                                                              │ │
│  └─ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ─┘ │
│                                                                      │
│  ┌─ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ─┐ │
│  │        <<com.factorysimulation.ui>>                          │ │
│  │ + - - - - - - - - - - - - - - - - - - - - - - - - - - - - -+ │ │
│  │                                                              │ │
│  │  ┌──────────────────────────────────────────────┐            │ │
│  │  │   MainFrame extends JFrame                  │            │ │
│  │  ├──────────────────────────────────────────────┤            │ │
│  │  │ - categories: List<MachineCategory>          │            │ │
│  │  │ - adjusters: List<Adjuster>                  │            │ │
│  │  │ - result: SimulationResult                   │            │ │
│  │  │ - setupTabs: JTabbedPane                     │            │ │
│  │  │ - resultsTabs: JTabbedPane                   │            │ │
│  │  ├──────────────────────────────────────────────┤            │ │
│  │  │ + MainFrame()                                │            │ │
│  │  │ + setupUI(): void                            │            │ │
│  │  │ + displayResults(result): void               │            │ │
│  │  │ + runSimulation(): void                      │            │ │
│  │  │ - createLeftPanel(): JPanel                  │            │ │
│  │  │ - createRightPanel(): JPanel                 │            │ │
│  │  │ - createMachineTab(): JPanel                 │            │ │
│  │  │ - createAdjusterTab(): JPanel                │            │ │
│  │  │ - createSimulationTab(): JPanel              │            │ │
│  │  └──────────────────────────────────────────────┘            │ │
│  │                                                              │ │
│  └─ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ─┘ │
│                                                                      │
│  ┌──────────────────────────────────────────────────┐                │
│  │   App                                            │                │
│  ├──────────────────────────────────────────────────┤                │
│  │ + main(args: String[]): void                     │                │
│  └──────────────────────────────────────────────────┘                │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 8. SEQUENCE DIAGRAM - SIMULATION FLOW

```
User          MainFrame        SimulationEngine       EventQueue       Statistics
│                │                    │                    │                │
│─ Configure ──►│                    │                    │                │
│   Machines &  │                    │                    │                │
│   Adjusters   │                    │                    │                │
│                │                    │                    │                │
│─ Start Sim ──►│                    │                    │                │
│                │                    │                    │                │
│                │─── run() ────────►│                    │                │
│                │                    │                    │                │
│                │                    │─ initialize() ────►│                │
│                │                    │◄─ events created ──│                │
│                │                    │                    │                │
│                │                    │──► while (time < duration)          │
│                │                    │                    │                │
│                │                    │─── popEvent() ────►│                │
│                │                    │◄─ next event ──────│                │
│                │                    │                    │                │
│                │                    │─── IF FAILURE ────►│                │
│                │                    │  • Add to queue    │                │
│                │                    │  • Try assign adj. │                │
│                │                    │  • Push event ────►│                │
│                │                    │                    │                │
│                │                    │─ IF REPAIR_DONE ──►│                │
│                │                    │  • Update state    │                │
│                │                    │  • Schedule failure│                │
│                │                    │  • Push event ────►│                │
│                │                    │                    │                │
│                │                    │                    │── Update ─────►│
│                │                    │                    │   Statistics   │
│                │                    │                    │                │
│                │                    │ [Repeat until time exhausted]        │
│                │                    │                    │                │
│                │◄─── return Result ─────────────────────┼────────────────│
│                │                    │                    │                │
│                │─ Display Results ──────────────────────────────────────►│
│                │  • Metrics Cards   │                    │                │
│                │  • Tables & Charts │                    │                │
│                │                    │                    │                │
│◄─ Show Results ─│                    │                    │                │
```

---

## 9. ACTIVITY DIAGRAM - SIMULATION LOGIC

```
START
  │
  ▼
┌─────────────────────────────────┐
│ Load Configuration              │
│ • Machines & Categories         │
│ • Adjusters & Skills            │
│ • Duration & Seed               │
└──────────────┬──────────────────┘
               │
               ▼
       ┌───────────────────┐
       │ Initialize Random │
       │ Seed & Events     │
       └────────┬──────────┘
                │
                ▼
         ┌──────────────┐
         │ Generate     │
         │ Initial      │
         │ Failures     │
         └──────┬───────┘
                │
                ▼
         ┌──────────────────────────┐
         │ Add Events to Queue      │
         │ (Priority by time, type) │
         └──────┬───────────────────┘
                │
                ▼
         ┌──────────────────────┐
      ┌──┤ Is Queue Empty?      │
      │  └──────────┬───────────┘
      │             │
      │          NO │
      │             │
      │             ▼
      │      ┌─────────────────┐
      │      │ Pop Next Event  │
      │      └────────┬────────┘
      │             │
      │             ▼
      │      ┌──────────────────┐
      │    ┌─┤ Is time >= end?  │
      │    │ └──┬────────────┬──┘
      │    │    │            │
      │    │   NO           YES
      │    │    │            │
      │    │    ▼            └──────┐
      │    │ ┌──────────────────┐   │
      │    │ │ Process Event    │   │
      │    │ └────┬─────────┬──┘   │
      │    │      │         │      │
      │    │    ┌─┴─────────┴─┐    │
      │    │    │             │    │
      │    │    ▼             ▼    │
      │    │ FAILURE?    REPAIR?   │
      │    │    │             │    │
      │    │    ▼             ▼    │
      │    │ • Add to      • Update │
      │    │   queue       adjuster │
      │    │ • Update      • Mark   │
      │    │   metrics       repaired│
      │    │ • Try assign  • Schedule│
      │    │   adjuster     failure  │
      │    │ • Update      • Try new │
      │    │   waiting      work     │
      │    │                        │
      │    └────────┬───────────────┘
      │             │
      │             ▼
      │      ┌─────────────────┐
      │      │ Add New Events  │
      │      │ to Queue        │
      │      └────────┬────────┘
      │             │
      └─────────────┘ (back to while loop)
                │
              YES│
                │
                ▼
      ┌──────────────────────┐
      │ Aggregate Results    │
      │ • Utilization Stats  │
      │ • Queue Metrics      │
      │ • Recommendations    │
      └────────┬─────────────┘
               │
               ▼
      ┌────────────────────┐
      │ Return Result      │
      │ Object             │
      └────────┬───────────┘
               │
               ▼
         Display to UI
               │
               ▼
             END
```

---

## 10. USE CASE DIAGRAM

```
┌─────────────────────────────────────────────────────────┐
│                                                         │
│                    FACTORY SYSTEM                       │
│                                                         │
│    ┌──────────────────────────────────────────────┐    │
│    │                                              │    │
│    │   ┌─────────────────────────────────────┐    │    │
│    │   │  Configure Machine Categories       │    │    │
│    │   │  (Add/Remove/Edit MTTF, repairTime) │    │    │
│    │   └─────────────────────────────────────┘    │    │
│    │                                              │    │
│    │   ┌─────────────────────────────────────┐    │    │
│    │   │  Manage Adjusters                   │    │    │
│    │   │  (Add/Remove, Assign Skills)        │    │    │
│    │   └─────────────────────────────────────┘    │    │
│    │                                              │    │
│    │   ┌─────────────────────────────────────┐    │    │
│    │   │  Set Simulation Parameters          │    │    │
│    │   │  (Duration, Random Seed)            │    │    │
│    │   └─────────────────────────────────────┘    │    │
│    │                                              │    │
│    │   ┌─────────────────────────────────────┐    │    │
│    │   │  Run Simulation                     │    │    │
│    │   │  (Execute DES Algorithm)            │    │    │
│    │   └─────────────────────────────────────┘    │    │
│    │                                              │    │
│    │   ┌─────────────────────────────────────┐    │    │
│    │   │  View Results                       │    │    │
│    │   │  (Metrics, Tables, Recommendations)│    │    │
│    │   └─────────────────────────────────────┘    │    │
│    │                                              │    │
│    └──────────────────────────────────────────────┘    │
│            ▲                                           │
│            │ Uses                                      │
│            │                                           │
│            │                                           │
│    ┌──────┴──────────┐                                │
│    │    Factory      │                                │
│    │   Operations    │                                │
│    │   Coordinator   │                                │
│    └─────────────────┘                                │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## 11. TECHNOLOGY STACK

| Component | Technology | Details |
|---|---|---|
| **Language** | Java 11 | Object-oriented, platform-independent |
| **Build Tool** | Apache Maven 3 | Dependency management, project structure |
| **UI Framework** | Java Swing | Cross-platform desktop GUI |
| **Simulation Type** | Discrete-Event Simulation (DES) | Event-driven, time-stepped |
| **Data Structures** | PriorityQueue, Lists, Sets | Standard Java Collections |
| **Random Generation** | java.util.Random | Seeded for determinism |
| **External Dependencies** | None | Only Java standard library |
| **Target Runtime** | Java 11+ | JDK 11 or later |
| **Platforms** | Windows, macOS, Linux | Full Java compatibility |

---

## 12. KEY ALGORITHMS

### 12.1 Failure Generation (Exponential Distribution)
```
Random U ~ Uniform(0, 1)
First Failure Time = -MTTF * ln(1 - U)
```
This ensures realistic failure patterns based on machine reliability.

### 12.2 Event Ordering
- **Priority 0:** Repair completion events (processed first)
- **Priority 1:** Failure events
- Ensures that repairs complete before new failures are processed at the same time

### 12.3 Skill-Based Assignment
```
For each idle adjuster:
    For each waiting machine:
        If adjuster.canRepair(machine.category):
            Assign machine to adjuster
            Break
```
Ensures only qualified adjusters repair specific machines.

### 12.4 Utilization Calculation
```
Machine Utilization = Total Running Time / Simulation Duration
Adjuster Utilization = Total Busy Time / Simulation Duration
```

### 12.5 Queue Metrics (Riemann Integration)
```
Average Queue Length = ∫ Queue Length dt / Total Time
Peak Queue Length = Max Queue Length observed
```

---

## 13. BUSINESS RULES & CONSTRAINTS

| Rule | Description |
|---|---|
| BR-1 | Only adjusters with matching skills can repair machines in a category |
| BR-2 | Machines fail according to exponential distribution with configured MTTF |
| BR-3 | Failed machines are added to a FIFO repair queue |
| BR-4 | Adjusters work on one repair at a time |
| BR-5 | Repairs complete in a time equal to the category's repair time |
| BR-6 | After repair, a machine's failure countdown is reset |
| BR-7 | Events are processed in chronological order (no time travel) |
| BR-8 | Simulations with identical seeds produce identical results |
| BR-9 | Unutilized adjusters are immediately assigned available work |
| BR-10 | Peak queue length is tracked as a bottleneck indicator |

---

## 14. METRICS & KPIs

| Metric | Formula | Significance |
|---|---|---|
| **Machine Utilization** | Running Time / Total Simulation Time | % of time machines are operational |
| **Adjuster Utilization** | Busy Time / Total Simulation Time | % of time maintenance staff is working |
| **Failure Rate** | Total Failures / Category | Average failures per machine type |
| **Avg Waiting Time** | Sum(Repair Start - Failure Time) / Completed Repairs | Average queue wait |
| **Peak Queue Length** | Max Queue Size observed | Worst-case backlog |
| **Avg Queue Length** | ∫ Queue(t) dt / Duration | Average backlog during simulation |
| **Repairs per Adjuster** | Total Repairs / Number of Adjusters | Workload per maintenance worker |

---

## 15. RECOMMENDATIONS ENGINE LOGIC

The system generates recommendations based on thresholds:

```
IF adjusterUtilization > 90%:
    RECOMMEND: "Consider hiring more adjusters or redistributing workload"

IF adjusterUtilization < 30%:
    RECOMMEND: "Adjuster capacity can be optimized; consider reducing staff"

IF averageQueueLength > 5:
    RECOMMEND: "High repair queue; prioritize adjuster hiring or skill development"

IF peakQueueLength > 10:
    RECOMMEND: "Critical bottleneck detected; emergency resource allocation needed"

IF categoryUtilization < 50%:
    RECOMMEND: "Low machine utilization for category; review maintenance intervals"

IF unresolvedQueue > 0:
    RECOMMEND: "Unresolved repairs remain at simulation end; adjust MTTF or skill coverage"
```

---

## 16. SAMPLE DATA (Pre-loaded on Startup)

### Machines
| Category | Quantity | MTTF (hrs) | Repair Time (hrs) |
|---|---|---|---|
| CNC Machines | 10 | 500 | 4 |
| Assembly Bots | 8 | 800 | 3 |
| Hydraulic Presses | 5 | 400 | 5 |
| Conveyor Systems | 12 | 600 | 2 |

### Adjusters
| ID | Name | Skills |
|---|---|---|
| 1 | Alice Johnson | Mechanical, CNC |
| 2 | Bob Smith | Electrical, Assembly |
| 3 | Carol White | Hydraulic, Mechanical |
| 4 | David Brown | Conveyor, Electrical |

### Simulation Defaults
- Duration: 8760 hours (1 year)
- Seed: 42 (deterministic for reproducibility)

---

## 17. ERROR HANDLING & VALIDATION

| Error Scenario | Handling |
|---|---|
| Negative machine quantity | Input validation; reject via UI |
| Zero MTTF | Input validation; reject via UI |
| Negative repair time | Input validation; reject via UI |
| No machines configured | Show warning; prevent simulation |
| No adjusters configured | Show warning; machines queue indefinitely |
| No adjuster skills match category | Machine waits in queue (unresolved) |
| Invalid seed | Use default seed (42) |
| Zero simulation duration | Show error; require positive duration |

---

## 18. PERFORMANCE CHARACTERISTICS

| Scenario | Expected Performance |
|---|---|
| 1 year simulation (100 machines, 10 adjusters) | < 5 seconds |
| 10 year simulation (200 machines, 20 adjusters) | < 30 seconds |
| Maximum practical machines | ~500 (depends on MTTF distribution) |
| Memory footprint | 200-500 MB for typical scenarios |
| UI responsiveness | Remains responsive during and after simulation |

---

## 19. DEPLOYMENT & DISTRIBUTION

### Options
1. **Direct Execution** — `run.cmd` (Windows)
2. **Maven Build** — `mvn clean package`
3. **JAR Packaging** — Executable JAR with manifest pointing to `App.main()`
4. **IDE Launch** — Run from VS Code, IntelliJ, Eclipse (JDK 11+)

### System Requirements
- **OS** — Windows, macOS, Linux
- **JRE/JDK** — Java 11 or later
- **RAM** — Minimum 256 MB; recommended 512 MB
- **Disk** — ~50 MB for compiled application

---

## 20. FUTURE ENHANCEMENTS

| Enhancement | Description |
|---|---|
| **Visualization** | Real-time graphing of queue length, failures over time |
| **Export** | Export results to CSV, PDF, Excel |
| **Scheduler Algorithms** | Priority-based or round-robin scheduling instead of FIFO |
| **Machine State History** | Track detailed failure/repair logs per machine |
| **Multi-threaded Simulation** | Parallel event processing for large scenarios |
| **Scenario Comparison** | Run multiple simulations and compare side-by-side |
| **Optimization Module** | Auto-tune adjuster count to meet KPI targets |
| **Database Storage** | Persist historical simulations and comparisons |
| **REST API** | Expose simulation engine as web service |
| **Advanced Analytics** | Machine learning for failure prediction |

---

## 21. GLOSSARY

| Term | Definition |
|---|---|
| **Adjuster** | Maintenance worker who repairs broken machines |
| **Category** | Type of machine (e.g., CNC, Assembly) |
| **Failure** | Event when a machine stops working |
| **MTTF** | Mean Time To Failure; expected hours until next failure |
| **Repair Queue** | FIFO list of failed machines waiting for repair |
| **Skill** | Expertise area (e.g., "Mechanical", "Electrical") |
| **Utilization** | Percentage of time a resource is actively in use |
| **DES** | Discrete-Event Simulation; time-stepped event processing |
| **Seed** | Initial value for random number generator (ensures determinism) |
| **Bottleneck** | Constraint limiting system throughput (e.g., high queue length) |

---

## 22. QUICK REFERENCE FOR POWERPOINT

### Slide Structure Recommendation
1. **Title Slide** — Project name, date, team
2. **Executive Summary** — Overview, objectives, key value
3. **System Context** — Use case diagram, stakeholders
4. **Architecture Overview** — Layered architecture diagram
5. **Data Models** — Class diagram (high-level)
6. **Simulation Engine** — Discrete-event logic, algorithm
7. **Data Flow** — Complete DFD
8. **User Interface** — UI mockup/screenshots
9. **Key Features** — Functional capabilities
10. **Functional Requirements** — FR table
11. **Non-Functional Requirements** — NFR table
12. **Technical Stack** — Technology details
13. **Sample Results** — Example metrics and recommendations
14. **Performance** — Benchmarks and scalability
15. **Deployment** — Build and run instructions
16. **Future Enhancements** — Roadmap
17. **Q&A** — Contact, references

---

**End of Presentation Guide**
