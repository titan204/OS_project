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
import java.util.List;
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
        // TODO: Implementation will be completed by team member 🧩
    }

    // =========================================================================
    // Layout
    // =========================================================================
    private void build() {
        // TODO: Implementation will be completed by team member 🧩
    }

    // =========================================================================
    // Input Panel (left)
    // =========================================================================
    private Node inputPanel() {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }

    private void addRow(String pid, String at, String bt) {
        // TODO: Implementation will be completed by team member 🧩
    }

    // =========================================================================
    // Results Panel (right)
    // =========================================================================
    private Node resultsPanel() {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }

    private ScrollPane ganttScroll(GanttChartPane pane) {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }

    // =========================================================================
    // Table builder
    // =========================================================================
    @SuppressWarnings("unchecked")
    private TableView<Process> buildTable() {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }

    private TableColumn<Process, String> tc(String title, Function<Process, String> fn) {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }

    // =========================================================================
    // Public API for controller
    // =========================================================================
    public void displayResults(SimulationResult rr, SimulationResult srtf, int quantum) {
        // TODO: Implementation will be completed by team member 🧩
    }

    private void populateTable(TableView<Process> table, SimulationResult r) {
        // TODO: Implementation will be completed by team member 🧩
    }

    /** Build the comparison text for the summary panel. */
    private String buildSummary(SimulationResult rr, SimulationResult srtf, int quantum) {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }

    /** Read process rows from the UI; throws IllegalArgumentException on bad input. */
    public List<Process> getProcesses() {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }

    public void loadProcessData(String[][] rows, String quantum) {
        // TODO: Implementation will be completed by team member 🧩
    }

    public String          getQuantumText()                    { return quantumField.getText(); }
    public void            setOnRunSimulation(Runnable r)      { this.onRunSimulation = r; }
    public void            setOnLoadScenario(Consumer<String> c) { this.onLoadScenario = c; }

    // =========================================================================
    // Small helpers
    // =========================================================================
    private Label secLabel(String t) {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }

    private Button smallBtn(String t) {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }

    private Label fixLabel(String t, double w) {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }

    private Button scBtn(String label) {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }
}
