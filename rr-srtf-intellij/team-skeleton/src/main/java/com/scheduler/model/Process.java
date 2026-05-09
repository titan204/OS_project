package com.scheduler.model;

public class Process {

    private final String id;
    private final int arrivalTime;
    private final int burstTime;

    private int completionTime;
    private int waitingTime;
    private int turnaroundTime;
    private int responseTime;
    private int firstResponseTime = -1;

    public Process(String id, int arrivalTime, int burstTime) {
        this.id          = id;
        this.arrivalTime = arrivalTime;
        this.burstTime   = burstTime;
    }

    public Process copy() {
        return new Process(id, arrivalTime, burstTime);
    }

    public String getId()            { return id; }
    public int    getArrivalTime()   { return arrivalTime; }
    public int    getBurstTime()     { return burstTime; }

    public int    getCompletionTime()  { return completionTime; }
    public int    getWaitingTime()     { return waitingTime; }
    public int    getTurnaroundTime()  { return turnaroundTime; }
    public int    getResponseTime()    { return responseTime; }
    public int    getFirstResponseTime() { return firstResponseTime; }

    public void setCompletionTime(int v)  { this.completionTime  = v; }
    public void setWaitingTime(int v)     { this.waitingTime     = v; }
    public void setTurnaroundTime(int v)  { this.turnaroundTime  = v; }
    public void setResponseTime(int v)    { this.responseTime    = v; }

    public void setFirstResponseTime(int v) {
        if (this.firstResponseTime == -1) this.firstResponseTime = v;
    }

    @Override
    public String toString() {
        return id + "(AT=" + arrivalTime + ", BT=" + burstTime + ")";
    }
}
