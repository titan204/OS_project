package com.scheduler.view;

import com.scheduler.model.Process;
import com.scheduler.model.SimulationResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class MainView extends BorderPane {

    // ── Input widgets ─────────────────────────────────────────────────────────
    private final TextField quantumField   = new TextField("3");
    private final VBox      processRows    = new VBox(4);
    private final TextArea  readyQueueArea = new TextArea();

    // ── Gantt charts ──────────────────────────────────────────────────────────
    private final GanttChartPane rrGantt   = new GanttChartPane();
    private final GanttChartPane srtfGantt = new GanttChartPane();

    // ── Result tables ─────────────────────────────────────────────────────────
    private final TableView<Process> rrTable   = buildTable();
    private final TableView<Process> srtfTable = buildTable();

    // ── Summary area ──────────────────────────────────────────────────────────
    private final TextArea summaryArea = new TextArea();

    // ── Callbacks set by controller ───────────────────────────────────────────
    private Runnable         onRunSimulation;
    private Consumer<String> onLoadScenario;

    public MainView() {
        setPadding(new Insets(10));
        setStyle("-fx-background-color:#F4F4F4;");
        build();
    }

    // =========================================================================
    // Layout
    // =========================================================================
    private void build() {
        // Title
        Label title = new Label("Round Robin  vs  SRTF  —  CPU Scheduling Simulator");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 17));

        // Scenario bar
        HBox scenarioBar = new HBox(6,
                new Label("Scenarios:"),
                scBtn("A – Mixed Workload"),
                scBtn("B – Quantum Effect"),
                scBtn("C – Short Jobs"),
                scBtn("D – Fairness"),
                scBtn("E – Validation")
        );
        scenarioBar.setAlignment(Pos.CENTER_LEFT);

        VBox topBox = new VBox(6, title, scenarioBar);
        topBox.setPadding(new Insets(0, 0, 8, 0));

        SplitPane split = new SplitPane(inputPanel(), resultsPanel());
        split.setDividerPositions(0.30);
        VBox.setVgrow(split, Priority.ALWAYS);

        setTop(topBox);
        setCenter(split);
    }

    // =========================================================================
    // Input Panel (left)
    // =========================================================================
    private Node inputPanel() {
        // Column headers
        HBox hdr = new HBox(4,
                fixLabel("PID",     58),
                fixLabel("Arrival", 62),
                fixLabel("Burst",   62)
        );
        hdr.setStyle("-fx-font-weight:bold; -fx-font-size:12;");

        // Default rows
        addRow("P1", "0", "8");
        addRow("P2", "1", "4");
        addRow("P3", "2", "9");

        Button addBtn   = smallBtn("＋ Add Process");
        Button clearBtn = smallBtn("✕ Clear All");
        addBtn.setOnAction(e ->
                addRow("P" + (processRows.getChildren().size() + 1), "", ""));
        clearBtn.setOnAction(e -> processRows.getChildren().clear());

        ScrollPane rowScroll = new ScrollPane(processRows);
        rowScroll.setFitToWidth(true);
        rowScroll.setPrefHeight(210);
        rowScroll.setStyle("-fx-background-color:transparent;");

        // Quantum
        quantumField.setPrefWidth(65);
        HBox qRow = new HBox(8, new Label("Time Quantum:"), quantumField);
        qRow.setAlignment(Pos.CENTER_LEFT);

        // Run button
        Button runBtn = new Button("▶   Run Simulation");
        runBtn.setStyle(
            "-fx-background-color:#3A7BD5; -fx-text-fill:white;" +
            "-fx-font-weight:bold; -fx-font-size:13; -fx-padding:7 20;");
        runBtn.setMaxWidth(Double.MAX_VALUE);
        runBtn.setOnAction(e -> { if (onRunSimulation != null) onRunSimulation.run(); });

        // Ready queue log
        readyQueueArea.setEditable(false);
        readyQueueArea.setPrefHeight(160);
        readyQueueArea.setStyle("-fx-font-family:Courier New; -fx-font-size:11;");
        readyQueueArea.setWrapText(false);

        VBox panel = new VBox(8,
                secLabel("⬛  Input Panel"),
                hdr,
                rowScroll,
                new HBox(6, addBtn, clearBtn),
                new Separator(),
                qRow,
                runBtn,
                new Separator(),
                secLabel("⬛  Ready Queue Log  (Round Robin)"),
                readyQueueArea
        );
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color:white;");
        return panel;
    }

    private void addRow(String pid, String at, String bt) {
        TextField pf = new TextField(pid); pf.setPrefWidth(58);
        TextField af = new TextField(at);  af.setPrefWidth(62);
        TextField bf = new TextField(bt);  bf.setPrefWidth(62);
        Button del   = new Button("✕");
        del.setStyle("-fx-background-color:transparent; -fx-text-fill:#C00; -fx-cursor:hand;");
        HBox row = new HBox(4, pf, af, bf, del);
        row.setAlignment(Pos.CENTER_LEFT);
        del.setOnAction(e -> processRows.getChildren().remove(row));
        processRows.getChildren().add(row);
    }

    // =========================================================================
    // Results Panel (right)
    // =========================================================================
    private Node resultsPanel() {

        // ── Round Robin ──────────────────────────────────────────────────────
        Label rrHdr = secLabel("⬛  Round Robin  (quantum = ?)");
        rrHdr.setId("rrHdr");

        ScrollPane rrGanttScroll = ganttScroll(rrGantt);
        rrTable.setPrefHeight(170);

        VBox rrBox = new VBox(6,
                rrHdr,
                new Label("Gantt Chart:"),
                rrGanttScroll,
                new Label("Metrics Table:"),
                rrTable
        );
        rrBox.setPadding(new Insets(8));
        rrBox.setStyle("-fx-background-color:white; -fx-border-color:#DDD; -fx-border-width:1;");

        // ── SRTF ─────────────────────────────────────────────────────────────
        ScrollPane srtfGanttScroll = ganttScroll(srtfGantt);
        srtfTable.setPrefHeight(170);

        VBox srtfBox = new VBox(6,
                secLabel("⬛  SRTF  (Shortest Remaining Time First)"),
                new Label("Gantt Chart:"),
                srtfGanttScroll,
                new Label("Metrics Table:"),
                srtfTable
        );
        srtfBox.setPadding(new Insets(8));
        srtfBox.setStyle("-fx-background-color:white; -fx-border-color:#DDD; -fx-border-width:1;");

        // ── Comparison summary ────────────────────────────────────────────────
        summaryArea.setEditable(false);
        summaryArea.setPrefHeight(200);
        summaryArea.setStyle("-fx-font-family:Courier New; -fx-font-size:11;");
        summaryArea.setWrapText(true);

        VBox sumBox = new VBox(6,
                secLabel("⬛  Comparison Summary & Conclusion"),
                summaryArea
        );
        sumBox.setPadding(new Insets(8));
        sumBox.setStyle("-fx-background-color:white; -fx-border-color:#DDD; -fx-border-width:1;");

        VBox right = new VBox(8, rrBox, srtfBox, sumBox);
        right.setPadding(new Insets(10));

        ScrollPane outer = new ScrollPane(right);
        outer.setFitToWidth(true);
        outer.setStyle("-fx-background-color:#F4F4F4;");
        return outer;
    }

    private ScrollPane ganttScroll(GanttChartPane pane) {
        ScrollPane sp = new ScrollPane(pane.getCanvas());
        sp.setPrefHeight(76);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color:transparent;");
        return sp;
    }

    // =========================================================================
    // Table builder
    // =========================================================================
    @SuppressWarnings("unchecked")
    private TableView<Process> buildTable() {
        TableView<Process> t = new TableView<>();
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        t.getColumns().addAll(
                tc("PID",      p -> p.getId()),
                tc("Arrival",  p -> "AVG".equals(p.getId()) ? "—" : String.valueOf(p.getArrivalTime())),
                tc("Burst",    p -> "AVG".equals(p.getId()) ? "—" : String.valueOf(p.getBurstTime())),
                tc("Finish",   p -> "AVG".equals(p.getId()) ? "—" : String.valueOf(p.getCompletionTime())),
                // Fix 2: AVG row displays turnaround time formatted to 2 decimal places
                tc("TAT",      p -> "AVG".equals(p.getId()) ? p.getFormattedAvgTAT() : String.valueOf(p.getTurnaroundTime())),
                // Fix 2: AVG row displays waiting time formatted to 2 decimal places
                tc("WT",       p -> "AVG".equals(p.getId()) ? p.getFormattedAvgWT()  : String.valueOf(p.getWaitingTime())),
                // Fix 2: AVG row displays response time formatted to 2 decimal places
                tc("RT",       p -> "AVG".equals(p.getId()) ? p.getFormattedAvgRT()  : String.valueOf(p.getResponseTime()))
        );
        return t;
    }

    private TableColumn<Process, String> tc(String title, Function<Process, String> fn) {
        TableColumn<Process, String> c = new TableColumn<>(title);
        c.setCellValueFactory(cell -> new SimpleStringProperty(fn.apply(cell.getValue())));
        c.setStyle("-fx-alignment:CENTER;");
        return c;
    }

    // =========================================================================
    // Public API for controller
    // =========================================================================
    public void displayResults(SimulationResult rr, SimulationResult srtf, int quantum) {

        // Update RR header label
        Label rrHdr = (Label) lookup("#rrHdr");
        if (rrHdr != null)
            rrHdr.setText("⬛  Round Robin  (quantum = " + quantum + ")");

        // Draw Gantt charts
        rrGantt.draw(rr.getGantt());
        srtfGantt.draw(srtf.getGantt());

        // Populate tables
        populateTable(rrTable,   rr);
        populateTable(srtfTable, srtf);

        // Ready queue log
        if (rr.getReadyQueueLog() != null)
            readyQueueArea.setText(String.join("\n", rr.getReadyQueueLog()));

        // Summary
        summaryArea.setText(buildSummary(rr, srtf, quantum));
    }

    private void populateTable(TableView<Process> table, SimulationResult r) {
        // Add a synthetic AVG row
        // Fix 2: pass raw double averages so values display with 2 decimal places
        Process avg = new Process("AVG", 0, 0);
        avg.setAvgWT(r.getAvgWT());
        avg.setAvgTAT(r.getAvgTAT());
        avg.setAvgRT(r.getAvgRT());

        ObservableList<Process> items = FXCollections.observableArrayList(r.getProcesses());
        items.add(avg);
        table.setItems(items);

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Process item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null && "AVG".equals(item.getId()))
                    setStyle("-fx-background-color:#DDE8FF; -fx-font-weight:bold;");
                else
                    setStyle("");
            }
        });
    }

    /** Build the comparison text for the summary panel. */
    private String buildSummary(SimulationResult rr, SimulationResult srtf, int quantum) {
        StringBuilder sb = new StringBuilder();
        sb.append("══════════════  COMPARISON SUMMARY  ══════════════\n\n");
        sb.append(String.format("%-26s  RR (q=%-2d)   SRTF\n", "Metric", quantum));
        sb.append("─".repeat(48)).append("\n");
        sb.append(String.format("%-26s  %-11.2f  %.2f\n", "Avg Waiting Time",    rr.getAvgWT(),  srtf.getAvgWT()));
        sb.append(String.format("%-26s  %-11.2f  %.2f\n", "Avg Turnaround Time", rr.getAvgTAT(), srtf.getAvgTAT()));
        sb.append(String.format("%-26s  %-11.2f  %.2f\n", "Avg Response Time",   rr.getAvgRT(),  srtf.getAvgRT()));
        sb.append("\n");

        sb.append("► Better Avg Waiting Time   : ")
          .append(rr.getAvgWT() <= srtf.getAvgWT() ? "Round Robin" : "SRTF").append("\n");
        sb.append("► Better Avg Turnaround     : ")
          .append(rr.getAvgTAT() <= srtf.getAvgTAT() ? "Round Robin" : "SRTF").append("\n");
        sb.append("► Better Avg Response Time  : ")
          .append(rr.getAvgRT() <= srtf.getAvgRT() ? "Round Robin" : "SRTF").append("\n\n");

        sb.append("══════════════  ANALYSIS  ══════════════\n\n");
        sb.append("Fairness:\n");
        sb.append("  Round Robin cycles processes every q=").append(quantum)
          .append(" units, ensuring\n");
        sb.append("  no process waits more than (n-1)×q before its next turn.\n");
        sb.append("  SRTF can starve long processes when short ones keep arriving.\n\n");

        sb.append("Quantum Effect (q=").append(quantum).append("):\n");
        sb.append("  Smaller q → more context switches, better response time.\n");
        sb.append("  Larger q → fewer switches, approaches FCFS behaviour.\n\n");

        sb.append("Short-job preference:\n");
        sb.append("  SRTF minimises average waiting time by always running the\n");
        sb.append("  process closest to completion.\n\n");

        sb.append("══════════════  CONCLUSION  ══════════════\n\n");
        if (srtf.getAvgWT() < rr.getAvgWT())
            sb.append("✔ SRTF achieved lower avg waiting time — more efficient.\n");
        else
            sb.append("✔ Round Robin achieved equal or lower avg waiting time.\n");

        if (rr.getAvgRT() <= srtf.getAvgRT())
            sb.append("✔ Round Robin gave equal or better first response time.\n");
        else
            sb.append("✔ SRTF gave better first response time here.\n");

        sb.append("✔ Round Robin is fairer: every process runs within q=")
          .append(quantum).append(" ticks.\n\n");
        sb.append("Recommendation:\n");
        sb.append("  → SRTF  for batch / throughput-oriented workloads.\n");
        sb.append("  → Round Robin  for interactive / time-sharing systems.\n");

        return sb.toString();
    }

    /** Read process rows from the UI; throws IllegalArgumentException on bad input. */
    public List<Process> getProcesses() {
        List<Process> list = new ArrayList<>();
        // Fix 3: track seen PIDs to prevent duplicates (case-sensitive)
        Set<String> seenPids = new HashSet<>();
        for (Node n : processRows.getChildren()) {
            if (!(n instanceof HBox)) continue;
            HBox row = (HBox) n;
            String pid = ((TextField) row.getChildren().get(0)).getText().trim();
            String ats = ((TextField) row.getChildren().get(1)).getText().trim();
            String bts = ((TextField) row.getChildren().get(2)).getText().trim();
            if (pid.isEmpty() && ats.isEmpty() && bts.isEmpty()) continue;
            if (pid.isEmpty())
                throw new IllegalArgumentException("A process is missing its PID.");
            // Fix 3: reject duplicate Process IDs before parsing other fields
            if (!seenPids.add(pid))
                throw new IllegalArgumentException("Duplicate Process ID '" + pid + "'. Each process must have a unique ID.");
            int at, bt;
            try { at = Integer.parseInt(ats); }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Arrival time for '" + pid + "' must be a non-negative integer.");
            }
            try { bt = Integer.parseInt(bts); }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Burst time for '" + pid + "' must be a positive integer.");
            }
            if (at < 0)  throw new IllegalArgumentException("Arrival time for '" + pid + "' cannot be negative.");
            if (bt <= 0) throw new IllegalArgumentException("Burst time for '" + pid + "' must be greater than 0.");
            list.add(new Process(pid, at, bt));
        }
        return list;
    }

    public void loadProcessData(String[][] rows, String quantum) {
        processRows.getChildren().clear();
        for (String[] r : rows) addRow(r[0], r[1], r[2]);
        quantumField.setText(quantum);
    }

    public String          getQuantumText()                { return quantumField.getText(); }
    public void            setOnRunSimulation(Runnable r)  { this.onRunSimulation = r; }
    public void            setOnLoadScenario(Consumer<String> c) { this.onLoadScenario = c; }

    // =========================================================================
    // Small helpers
    // =========================================================================
    private Label secLabel(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        return l;
    }
    private Button smallBtn(String t) {
        Button b = new Button(t);
        b.setStyle("-fx-font-size:11;");
        return b;
    }
    private Label fixLabel(String t, double w) {
        Label l = new Label(t);
        l.setPrefWidth(w);
        return l;
    }
    private Button scBtn(String label) {
        Button b = new Button(label);
        b.setStyle("-fx-font-size:11; -fx-background-color:#E8E8E8; -fx-cursor:hand;");
        b.setOnAction(e -> { if (onLoadScenario != null) onLoadScenario.accept(label.substring(0, 1)); });
        return b;
    }
}
