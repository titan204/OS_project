package com.scheduler.model;

public class Process {

    private final String id;
    private final int arrivalTime;
    private final int burstTime;

    // Computed after simulation
    private int completionTime;
    private int waitingTime;
    private int turnaroundTime;
    private int responseTime;
    private int firstResponseTime = -1;

    public Process(String id, int arrivalTime, int burstTime) {
        // TODO: Implementation will be completed by team member 🧩
    }

    /** Deep copy — used so simulations don't mutate the original list. */
    public Process copy() {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getId()            { return id; }
    public int    getArrivalTime()   { return arrivalTime; }
    public int    getBurstTime()     { return burstTime; }

    public int    getCompletionTime()    { return completionTime; }
    public int    getWaitingTime()       { return waitingTime; }
    public int    getTurnaroundTime()    { return turnaroundTime; }
    public int    getResponseTime()      { return responseTime; }
    public int    getFirstResponseTime() { return firstResponseTime; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setCompletionTime(int v)  { this.completionTime  = v; }
    public void setWaitingTime(int v)     { this.waitingTime     = v; }
    public void setTurnaroundTime(int v)  { this.turnaroundTime  = v; }
    public void setResponseTime(int v)    { this.responseTime    = v; }

    /** Only records the FIRST time the process gets the CPU. */
    public void setFirstResponseTime(int v) {
        // TODO: Implementation will be completed by team member 🧩
    }

    @Override
    public String toString() {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }
}
