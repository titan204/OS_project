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
        // TODO: Implementation will be completed by team member 🧩
    }

    // ── Run ──────────────────────────────────────────────────────────────────
    private void runSimulation() {
        // TODO: Implementation will be completed by team member 🧩
    }

    // ── Preset scenarios ─────────────────────────────────────────────────────
    private void loadScenario(String key) {
        // TODO: Implementation will be completed by team member 🧩
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void showError(String title, String msg) {
        // TODO: Implementation will be completed by team member 🧩
    }

    private void showInfo(String title, String msg) {
        // TODO: Implementation will be completed by team member 🧩
    }
}
