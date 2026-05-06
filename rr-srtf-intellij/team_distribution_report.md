# 📋 Team Distribution Report
## Round Robin vs SRTF — CPU Scheduling Simulator (Java/JavaFX MVC)

---

## 🗂️ Project Structure Overview

```
team-skeleton/
├── pom.xml                                          ← Maven build (no changes needed)
└── src/main/java/com/scheduler/
    ├── App.java                                     ← Entry point  [Member 1]
    ├── model/
    │   ├── Process.java                             ← Data model   [Member 1]
    │   ├── GanttEntry.java                          ← Data model   [Member 1]
    │   ├── SimulationResult.java                    ← Data model   [Member 2]
    │   ├── RoundRobinScheduler.java                 ← Algorithm    [Member 3]
    │   └── SRTFScheduler.java                       ← Algorithm    [Member 4]
    ├── controller/
    │   └── MainController.java                      ← Controller   [Member 5]
    └── view/
        ├── GanttChartPane.java                      ← View         [Member 6]
        └── MainView.java                            ← View         [Member 6]
```

---

## 📌 Execution Order & Dependency Flow

```
Member 1 → Member 2 → Member 3 ┐
                      Member 4 ┘→ Member 5 → Member 6
```

> **Member 1 goes first.** All others can work in parallel once Member 1 is done.
> Members 3, 4, and 6 are **fully independent** of each other.

---

## 👤 Member 1 — Core Data Models (Foundation Layer)

### Files:
- `src/main/java/com/scheduler/App.java`
- `src/main/java/com/scheduler/model/Process.java`
- `src/main/java/com/scheduler/model/GanttEntry.java`

### Tasks:

#### `Process.java` — CPU Process Data Model
| Method | Responsibility |
|--------|---------------|
| `Process(String id, int arrivalTime, int burstTime)` | Store `id`, `arrivalTime`, `burstTime` into fields; initialize `firstResponseTime = -1` |
| `copy()` | Return `new Process(id, arrivalTime, burstTime)` — a fresh copy with no computed fields |
| `getId()` | Return `id` |
| `getArrivalTime()` | Return `arrivalTime` |
| `getBurstTime()` | Return `burstTime` |
| `getCompletionTime()` | Return `completionTime` |
| `getWaitingTime()` | Return `waitingTime` |
| `getTurnaroundTime()` | Return `turnaroundTime` |
| `getResponseTime()` | Return `responseTime` |
| `getFirstResponseTime()` | Return `firstResponseTime` |
| `setCompletionTime(int v)` | Assign `this.completionTime = v` |
| `setWaitingTime(int v)` | Assign `this.waitingTime = v` |
| `setTurnaroundTime(int v)` | Assign `this.turnaroundTime = v` |
| `setResponseTime(int v)` | Assign `this.responseTime = v` |
| `setFirstResponseTime(int v)` | Only set if `firstResponseTime == -1` (record FIRST response only) |
| `toString()` | Return `id + "(AT=" + arrivalTime + ", BT=" + burstTime + ")"` |

#### `GanttEntry.java` — Single Gantt Chart Block
| Method | Responsibility |
|--------|---------------|
| `GanttEntry(String processId, int start, int end)` | Store all 3 fields |
| `getProcessId()` | Return `processId` |
| `getStart()` | Return `start` |
| `getEnd()` | Return `end` |

#### `App.java` — JavaFX Entry Point
| Method | Responsibility |
|--------|---------------|
| `start(Stage primaryStage)` | Create `MainView view`, then `new MainController(view)`. Build `Scene(view, 1150, 740)`, set title `"Round Robin vs SRTF — Scheduling Simulator"`, set `minWidth=900`, `minHeight=600`, then call `primaryStage.show()` |

---

## 👤 Member 2 — SimulationResult Model

### File:
- `src/main/java/com/scheduler/model/SimulationResult.java`

### Dependency:
> ⚠️ Requires **Member 1's** `Process.java` and `GanttEntry.java` to be complete.

### Tasks:

#### `SimulationResult.java` — Aggregated Simulation Output
| Method | Responsibility |
|--------|---------------|
| `SimulationResult(List<Process>, List<GanttEntry>, List<String>)` | Store all 3 fields, then call `computeAverages()` |
| `computeAverages()` | private. Loop through `processes`, sum `waitingTime`, `turnaroundTime`, `responseTime`. Divide each sum by `n`. Store in `avgWT`, `avgTAT`, `avgRT`. Guard against `n == 0`. |
| `getProcesses()` | Return `processes` |
| `getGantt()` | Return `gantt` |
| `getReadyQueueLog()` | Return `readyQueueLog` (may be `null` for SRTF) |
| `getAvgWT()` | Return `avgWT` |
| `getAvgTAT()` | Return `avgTAT` |
| `getAvgRT()` | Return `avgRT` |

---

## 👤 Member 3 — Round Robin Scheduling Algorithm

### File:
- `src/main/java/com/scheduler/model/RoundRobinScheduler.java`

### Dependency:
> ⚠️ Requires **Member 1** (`Process`, `GanttEntry`) and **Member 2** (`SimulationResult`) to be complete.

### Tasks:

#### `RoundRobinScheduler.java`
| Method | Responsibility |
|--------|---------------|
| `simulate(List<Process> input, int quantum)` | Full Round Robin implementation: deep-copy processes, sort by arrival, use a `LinkedList` as ready queue, run time-sliced CPU in a loop tracking `remaining[]` time per process, record `firstResponseTime`, log ready queue state, build `GanttEntry` list, compute completion/TAT/WT/RT per process, return `new SimulationResult(procs, gantt, rqLog)` |
| `snapshot(Queue<Integer> q, List<Process> procs)` | private. Serialize queue state as `"[ P1 P2 P3 ]"` string by mapping each queue index to its process ID |

**Algorithm notes:**
- Seed queue with processes arriving at `t=0`; if none, jump `time` to first arrival
- On idle CPU (empty queue), scan for the nearest un-done, un-queued process and advance `time`
- After each slice, admit any process whose `arrivalTime <= time`
- If `remaining[idx] == 0` after slice: mark done, set `completionTime`, `turnaroundTime`, `waitingTime`
- Otherwise: re-enqueue at back of queue

---

## 👤 Member 4 — SRTF Scheduling Algorithm

### File:
- `src/main/java/com/scheduler/model/SRTFScheduler.java`

### Dependency:
> ⚠️ Requires **Member 1** (`Process`, `GanttEntry`) and **Member 2** (`SimulationResult`) to be complete.

### Tasks:

#### `SRTFScheduler.java`
| Method | Responsibility |
|--------|---------------|
| `simulate(List<Process> input)` | Full SRTF (preemptive SJF) implementation: deep-copy processes, sort by arrival, tick-based simulation loop. Each tick: scan all arrived and un-done processes, pick the one with minimum `remaining[]` time. Handle idle CPU (no arrived process). Track context switches (flush Gantt entry when `chosen != prev`). Record `firstResponseTime`. When `remaining[chosen] == 0`: mark done, set `completionTime`, `turnaroundTime`, `waitingTime`, add final Gantt entry. Return `new SimulationResult(procs, gantt, null)` — note: `readyQueueLog` is always `null` for SRTF |

**Algorithm notes:**
- Use `sliceStart` and `prev` to batch consecutive same-process ticks into one `GanttEntry`
- Compute simulation upper bound as `lastArrival + totalBurst + 1`
- On idle tick: add `GanttEntry("IDLE", time, time+1)`, increment `time`, continue
- Final Gantt entry for completed process: `add(new GanttEntry(cp.getId(), sliceStart, time))`

---

## 👤 Member 5 — Controller (Wiring & Validation)

### File:
- `src/main/java/com/scheduler/controller/MainController.java`

### Dependency:
> ⚠️ Requires **Members 1-4** (all models and schedulers) to be complete.
> ⚠️ Requires **Member 6's** `MainView` public API signatures (can proceed using skeleton).

### Tasks:

#### `MainController.java`
| Method | Responsibility |
|--------|---------------|
| `MainController(MainView view)` | Store `view` field. Register callbacks: `view.setOnRunSimulation(this::runSimulation)` and `view.setOnLoadScenario(this::loadScenario)` |
| `runSimulation()` | private. (1) Parse `view.getQuantumText()` as positive int — call `showError(...)` and `return` if invalid. (2) Call `view.getProcesses()` — catch `IllegalArgumentException`, show error. Return if empty. (3) Run `rrScheduler.simulate(processes, quantum)` and `srtfScheduler.simulate(processes)`. (4) Call `view.displayResults(rrResult, srtfResult, quantum)` |
| `loadScenario(String key)` | private. `switch(key)`: cases "A" through "E". Each case calls `view.loadProcessData(String[][], quantumString)`. Case "E" also calls `showInfo(...)` to warn that quantum `-1` is intentionally invalid |
| `showError(String title, String msg)` | Create `Alert(Alert.AlertType.ERROR)`, set title, set `headerText = null`, set `contentText = msg`, call `showAndWait()` |
| `showInfo(String title, String msg)` | Create `Alert(Alert.AlertType.INFORMATION)`, set title, set `headerText = null`, set `contentText = msg`, call `showAndWait()` |

**Scenario Data:**
| Key | Processes [PID, AT, BT] | Quantum |
|-----|-------------------------|---------|
| A | P1/0/8, P2/1/4, P3/2/9, P4/3/5 | 3 |
| B | P1/0/10, P2/0/5, P3/0/3, P4/0/7 | 4 |
| C | P1/0/2, P2/0/1, P3/1/3, P4/1/1, P5/2/2, P6/2/1 | 2 |
| D | P1/0/6, P2/0/6, P3/0/6, P4/0/6 | 2 |
| E | P1/0/5, P2/2/3 | -1 (invalid for test) |

---

## 👤 Member 6 — View Layer (UI & Gantt Chart)

### Files:
- `src/main/java/com/scheduler/view/GanttChartPane.java`
- `src/main/java/com/scheduler/view/MainView.java`

### Dependency:
> ⚠️ Requires **Member 1's** `Process`, `GanttEntry`, and **Member 2's** `SimulationResult`.
> ✅ **Independent** from Members 3, 4, and 5.

### Tasks:

#### `GanttChartPane.java` — Canvas-based Gantt Renderer
| Method | Responsibility |
|--------|---------------|
| `GanttChartPane()` | Initialize `canvas = new Canvas(600, BLOCK_H + TOP_PAD + TICK_H + 4)`. Call `getChildren().add(canvas)` |
| `draw(List<GanttEntry> entries)` | Guard null/empty. Compute `totalTime` from last entry's end. Compute `wpu = max(MIN_W_UNIT, 580.0 / totalTime)`. Resize canvas. Clear with `gc.clearRect(...)`. For each entry: compute `x`, `w`, get color, fill + stroke rounded rect, draw label (centered), draw start tick. After loop draw final end-tick. |
| `colorFor(String pid)` | If "IDLE" return `Color.web("#CCCCCC")`. Otherwise `PALETTE[Math.abs(pid.hashCode()) % PALETTE.length]` |
| `getCanvas()` | Return `canvas` |

#### `MainView.java` — Main Application View (BorderPane)
| Method | Responsibility |
|--------|---------------|
| `MainView()` | Call `setPadding(new Insets(10))`, `setStyle("-fx-background-color:#F4F4F4;")`, then `build()` |
| `build()` | Build title Label, scenario HBox with 5 `scBtn(...)` buttons, combine in VBox `topBox`. Create `SplitPane(inputPanel(), resultsPanel())` at divider `0.30`. Call `setTop(topBox)` and `setCenter(split)` |
| `inputPanel()` | Build column headers HBox, add default rows (P1/0/8, P2/1/4, P3/2/9), create `addBtn`/`clearBtn`, `ScrollPane` for `processRows`, quantum HBox, run Button (blue style), `readyQueueArea`. Assemble into VBox with white background. Return. |
| `addRow(String pid, String at, String bt)` | Create 3 TextFields (widths 58, 62, 62), delete Button (red transparent), assemble HBox, add delete action that removes row from `processRows` |
| `resultsPanel()` | Build RR box (header label id=`"rrHdr"`, gantt scroll, table), SRTF box (same pattern), summary box. Stack in outer VBox inside `ScrollPane`. Return. |
| `ganttScroll(GanttChartPane pane)` | Return `ScrollPane` wrapping `pane.getCanvas()`, height 76, horizontal scroll `AS_NEEDED`, vertical `NEVER` |
| `buildTable()` | Create `TableView<Process>` with `CONSTRAINED_RESIZE_POLICY`, add 7 columns: PID, Arrival, Burst, Finish (shows "—" for AVG row), TAT, WT, RT |
| `tc(String title, Function<Process,String> fn)` | Create `TableColumn`, set `cellValueFactory` using `SimpleStringProperty(fn.apply(...))`, center-align, return |
| `displayResults(SimulationResult rr, SimulationResult srtf, int quantum)` | Look up `#rrHdr` label, update text. Call `rrGantt.draw(...)`, `srtfGantt.draw(...)`. Call `populateTable(...)` for both. Set `readyQueueArea` text from RR log. Set `summaryArea` text from `buildSummary(...)` |
| `populateTable(TableView<Process> table, SimulationResult r)` | Create synthetic `Process("AVG", 0, 0)`, set rounded avg WT/TAT/RT. Build observable list from processes + AVG row. Set row factory to color AVG row `#DDE8FF` bold |
| `buildSummary(SimulationResult rr, SimulationResult srtf, int quantum)` | Build formatted comparison text: header, metric table (WT/TAT/RT), better-algorithm bullets, fairness analysis, quantum effect note, conclusion with recommendation |
| `getProcesses()` | Loop through `processRows` children (each is HBox). Skip empty rows. Validate PID not empty, arrival/burst parseable, arrival >= 0, burst > 0. Throw `IllegalArgumentException` with descriptive message on any violation. Return list. |
| `loadProcessData(String[][] rows, String quantum)` | Clear `processRows`, call `addRow(...)` for each row array, set `quantumField.setText(quantum)` |
| `getQuantumText()` | Return `quantumField.getText()` |
| `setOnRunSimulation(Runnable r)` | Assign `this.onRunSimulation = r` |
| `setOnLoadScenario(Consumer<String> c)` | Assign `this.onLoadScenario = c` |
| `secLabel(String t)` | Return bold Arial 12 Label |
| `smallBtn(String t)` | Return Button with `font-size:11` style |
| `fixLabel(String t, double w)` | Return Label with `setPrefWidth(w)` |
| `scBtn(String label)` | Return Button with `#E8E8E8` background; on action: call `onLoadScenario.accept(label.substring(0,1))` if not null |

---

## ✅ Integration Checklist (Post-Implementation)

After all members finish, merge in this order:

1. **Member 1** → Merge `Process.java`, `GanttEntry.java`, `App.java` first
2. **Member 2** → Merge `SimulationResult.java`
3. **Members 3 & 4** → Merge `RoundRobinScheduler.java` and `SRTFScheduler.java` (independent)
4. **Member 6** → Merge `GanttChartPane.java` and `MainView.java`
5. **Member 5** → Merge `MainController.java` last (depends on everything)

### Build & Run:
```bash
mvn javafx:run
```

### Verification Scenarios:
| Test | Expected |
|------|----------|
| Run Scenario A (q=3) | Both Gantt charts populate, tables show WT/TAT/RT, summary appears |
| Run Scenario B (q=4) | Same — all 4 processes at arrival=0 |
| Run Scenario E | Quantum `-1` shows validation error dialog |
| Enter empty processes, click Run | Error: "Please add at least one process" |
| Set burst=0, click Run | Error: "Burst time must be greater than 0" |

---

## 📊 Workload Balance Summary

| Member | Files | Methods to Implement | Difficulty |
|--------|-------|---------------------|------------|
| 1 | 3 files | 17 methods | Medium (foundation — must go first) |
| 2 | 1 file | 7 methods | Easy |
| 3 | 1 file | 2 methods | Hard (complex algorithm) |
| 4 | 1 file | 1 method | Hard (complex algorithm) |
| 5 | 1 file | 5 methods | Medium (validation + wiring) |
| 6 | 2 files | 18 methods | Hard (complex UI + Canvas rendering) |
