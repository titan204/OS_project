package com.scheduler.model;

import java.util.List;

public class SimulationResult {

    private final List<Process>    processes;
    private final List<GanttEntry> gantt;
    private final List<String>     readyQueueLog;   // RR only, null for SRTF

    private double avgWT;
    private double avgTAT;
    private double avgRT;

    public SimulationResult(List<Process> processes,
                            List<GanttEntry> gantt,
                            List<String> readyQueueLog) {
        // TODO: Implementation will be completed by team member 🧩
    }

    private void computeAverages() {
        // TODO: Implementation will be completed by team member 🧩
    }

    public List<Process>    getProcesses()     { return processes; }
    public List<GanttEntry> getGantt()         { return gantt; }
    public List<String>     getReadyQueueLog() { return readyQueueLog; }
    public double           getAvgWT()         { return avgWT; }
    public double           getAvgTAT()        { return avgTAT; }
    public double           getAvgRT()         { return avgRT; }
}
