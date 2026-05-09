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
        this.processes     = processes;
        this.gantt         = gantt;
        this.readyQueueLog = readyQueueLog;
        computeAverages();
    }

    private void computeAverages() {
        int n = processes.size();
        if (n == 0) return;
        double sumWT = 0, sumTAT = 0, sumRT = 0;
        for (Process p : processes) {
            sumWT  += p.getWaitingTime();
            sumTAT += p.getTurnaroundTime();
            sumRT  += p.getResponseTime();
        }
        avgWT  = sumWT  / n;
        avgTAT = sumTAT / n;
        avgRT  = sumRT  / n;
    }

    public List<Process>    getProcesses()     { return processes; }
    public List<GanttEntry> getGantt()         { return gantt; }
    public List<String>     getReadyQueueLog() { return readyQueueLog; }
    public double           getAvgWT()         { return avgWT; }
    public double           getAvgTAT()        { return avgTAT; }
    public double           getAvgRT()         { return avgRT; }
}
