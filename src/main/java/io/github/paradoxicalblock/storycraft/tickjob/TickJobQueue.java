package io.github.paradoxicalblock.storycraft.tickjob;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public class TickJobQueue {
    private List<TickJob> jobs = new ArrayList<>();
    private List<TickJob> pendingJobs = new ArrayList<>();

    public void addJob(TickJob job) {
        this.pendingJobs.add(job);
    }

    public void tick() {
        this.jobs.addAll(this.pendingJobs);
        this.pendingJobs.clear();

        synchronized (this.jobs) {
            ListIterator<TickJob> itr = this.jobs.listIterator();
            while (itr.hasNext()) {
                TickJob job = itr.next();
                job.tick();
                if (job.isComplete())
                    itr.remove();
            }
        }
    }

    public void clear() {
        this.jobs.clear();
        this.pendingJobs.clear();
    }
}
