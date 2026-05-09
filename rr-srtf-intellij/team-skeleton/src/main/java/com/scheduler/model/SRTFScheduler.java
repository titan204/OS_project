package com.scheduler.model;

import java.util.*;

public class SRTFScheduler {

    public SimulationResult simulate(List<Process> input) {

        List<Process> procs = new ArrayList<>();
        for (Process p : input) procs.add(p.copy());
        procs.sort(Comparator.comparingInt(Process::getArrivalTime));

        int n = procs.size();
        int[] remaining = new int[n];
        for (int i = 0; i < n; i++) remaining[i] = procs.get(i).getBurstTime();

        List<GanttEntry> gantt = new ArrayList<>();
        boolean[] done = new boolean[n];

        int time      = 0;
        int completed = 0;
        int prev       = -1;
        int sliceStart = 0;

        
        int totalBurst = 0;
        for (Process p : procs) totalBurst += p.getBurstTime();
        int limit = procs.get(n - 1).getArrivalTime() + totalBurst + 1;

        while (completed < n && time <= limit) {

          
            int chosen = -1, minRem = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (!done[i] && procs.get(i).getArrivalTime() <= time) {
                    if (remaining[i] < minRem) { minRem = remaining[i]; chosen = i; }
                }
            }

       
            if (chosen == -1) {
                if (prev != -1 && sliceStart < time) {
                    gantt.add(new GanttEntry(procs.get(prev).getId(), sliceStart, time));
                    prev = -1;
                }
                gantt.add(new GanttEntry("IDLE", time, time + 1));
                sliceStart = time + 1;
                time++;
                continue;
            }

            Process cp = procs.get(chosen);

            if (cp.getFirstResponseTime() == -1) {
                cp.setFirstResponseTime(time);
                cp.setResponseTime(time - cp.getArrivalTime());
            }

      
            if (chosen != prev) {
                if (prev != -1 && sliceStart < time)
                    gantt.add(new GanttEntry(procs.get(prev).getId(), sliceStart, time));
                sliceStart = time;
                prev = chosen;
            }

            remaining[chosen]--;
            time++;

            if (remaining[chosen] == 0) {
                done[chosen] = true;
                completed++;
                cp.setCompletionTime(time);
                cp.setTurnaroundTime(time - cp.getArrivalTime());
                cp.setWaitingTime(cp.getTurnaroundTime() - cp.getBurstTime());
                gantt.add(new GanttEntry(cp.getId(), sliceStart, time));
                prev = -1;
                sliceStart = time;
            }
        }

        return new SimulationResult(procs, gantt, null);
    }
}
