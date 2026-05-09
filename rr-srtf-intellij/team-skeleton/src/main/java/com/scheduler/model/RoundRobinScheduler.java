//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.scheduler.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RoundRobinScheduler {
    public SimulationResult simulate(List<Process> input, int quantum) {
        List<Process> procs = new ArrayList();

        for(Process p : input) {
            procs.add(p.copy());
        }

        procs.sort(Comparator.comparingInt(Process::getArrivalTime));
        int n = procs.size();
        int[] remaining = new int[n];

        for(int i = 0; i < n; ++i) {
            remaining[i] = ((Process)procs.get(i)).getBurstTime();
        }

        List<GanttEntry> gantt = new ArrayList();
        List<String> rqLog = new ArrayList();
        Queue<Integer> ready = new LinkedList();
        boolean[] inQueue = new boolean[n];
        boolean[] done = new boolean[n];
        int time = 0;
        int completed = 0;

        for(int i = 0; i < n; ++i) {
            if (((Process)procs.get(i)).getArrivalTime() == 0) {
                ready.add(i);
                inQueue[i] = true;
            }
        }

        if (ready.isEmpty()) {
            time = ((Process)procs.get(0)).getArrivalTime();
            ready.add(0);
            inQueue[0] = true;
        }

        while(completed < n) {
            if (ready.isEmpty()) {
                int next = Integer.MAX_VALUE;

                for(int i = 0; i < n; ++i) {
                    if (!done[i] && !inQueue[i]) {
                        next = Math.min(next, ((Process)procs.get(i)).getArrivalTime());
                    }
                }

                time = next;

                for(int i = 0; i < n; ++i) {
                    if (!done[i] && !inQueue[i] && ((Process)procs.get(i)).getArrivalTime() <= time) {
                        ready.add(i);
                        inQueue[i] = true;
                    }
                }
            } else {
                int idx = (Integer)ready.poll();
                Process p = (Process)procs.get(idx);
                if (p.getFirstResponseTime() == -1) {
                    p.setFirstResponseTime(time);
                    p.setResponseTime(time - p.getArrivalTime());
                }

                int runFor = Math.min(quantum, remaining[idx]);
                int start = time;
                time += runFor;
                remaining[idx] -= runFor;
                gantt.add(new GanttEntry(p.getId(), start, time));
                rqLog.add("t=" + start + " → Run " + p.getId() + "  |  Queue after: " + this.snapshot(ready, procs));

                for(int i = 0; i < n; ++i) {
                    if (!done[i] && !inQueue[i] && ((Process)procs.get(i)).getArrivalTime() <= time) {
                        ready.add(i);
                        inQueue[i] = true;
                    }
                }

                if (remaining[idx] == 0) {
                    done[idx] = true;
                    inQueue[idx] = false;
                    ++completed;
                    p.setCompletionTime(time);
                    p.setTurnaroundTime(time - p.getArrivalTime());
                    p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
                } else {
                    inQueue[idx] = false;
                    ready.add(idx);
                    inQueue[idx] = true;
                }
            }
        }

        return new SimulationResult(procs, gantt, rqLog);
    }

    private String snapshot(Queue<Integer> q, List<Process> procs) {
        if (q.isEmpty()) {
            return "[ ]";
        } else {
            StringBuilder sb = new StringBuilder("[ ");

            for(int i : q) {
                sb.append(((Process)procs.get(i)).getId()).append(" ");
            }

            sb.append("]");
            return sb.toString();
        }
    }
}
