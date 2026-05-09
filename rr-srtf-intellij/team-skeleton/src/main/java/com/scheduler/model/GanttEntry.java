package com.scheduler.model;

public class GanttEntry {

    private final String processId;
    private final int    start;
    private final int    end;

    public GanttEntry(String processId, int start, int end) {
        this.processId = processId;
        this.start     = start;
        this.end       = end;
    }

    public String getProcessId() { return processId; }
    public int    getStart()     { return start; }
    public int    getEnd()       { return end; }
}
