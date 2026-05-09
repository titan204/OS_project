package com.scheduler.controller;

import com.scheduler.model.Process;
import com.scheduler.model.RoundRobinScheduler;
import com.scheduler.model.SRTFScheduler;
import com.scheduler.model.SimulationResult;
import com.scheduler.view.MainView;
import javafx.scene.control.Alert;

import java.util.List;

public class MainController {

    private final MainView            view;
    private final RoundRobinScheduler rrScheduler   = new RoundRobinScheduler();
    private final SRTFScheduler       srtfScheduler = new SRTFScheduler();

    public MainController(MainView view) {
        this.view = view;
        view.setOnRunSimulation(this::runSimulation);
        view.setOnLoadScenario(this::loadScenario);
    }

    
    private void runSimulation() {

       
        int quantum;
        try {
            quantum = Integer.parseInt(view.getQuantumText().trim());
            if (quantum <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Invalid Quantum",
                    "Time quantum must be a positive integer (e.g. 2, 3, 4).\n" +
                    "Zero, negative, or non-numeric values are not allowed.");
            return;
        }

       
        List<Process> processes;
        try {
            processes = view.getProcesses();
        } catch (IllegalArgumentException e) {
            showError("Invalid Process Input", e.getMessage());
            return;
        }
        if (processes.isEmpty()) {
            showError("No Processes", "Please add at least one process before running.");
            return;
        }

       
        SimulationResult rrResult   = rrScheduler.simulate(processes, quantum);
        SimulationResult srtfResult = srtfScheduler.simulate(processes);

        view.displayResults(rrResult, srtfResult, quantum);
    }

   
    private void loadScenario(String key) {
        switch (key) {
            case "A" -> view.loadProcessData(new String[][]{
                {"P1","0","8"}, {"P2","1","4"}, {"P3","2","9"}, {"P4","3","5"}}, "3");

            case "B" -> view.loadProcessData(new String[][]{
                {"P1","0","10"}, {"P2","0","5"}, {"P3","0","3"}, {"P4","0","7"}}, "4");

            case "C" -> view.loadProcessData(new String[][]{
                {"P1","0","2"}, {"P2","0","1"}, {"P3","1","3"},
                {"P4","1","1"}, {"P5","2","2"}, {"P6","2","1"}}, "2");

            case "D" -> view.loadProcessData(new String[][]{
                {"P1","0","6"}, {"P2","0","6"}, {"P3","0","6"}, {"P4","0","6"}}, "2");

            case "E" -> {
                view.loadProcessData(new String[][]{
                    {"P1","0","5"}, {"P2","2","3"}}, "-1");
                showInfo("Validation Scenario E",
                        "Quantum is intentionally set to -1 (invalid).\n" +
                        "Press ▶ Run Simulation to see the validation error.");
            }
        }
    }

    
    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
