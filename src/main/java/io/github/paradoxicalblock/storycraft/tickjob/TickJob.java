package io.github.paradoxicalblock.storycraft.tickjob;

import java.util.Random;

public class TickJob {
    private final int baseTicks;
    private final int randomTicks;
    private final boolean repeat;
    private final Random rand;
    private final Runnable runner;
    private int tick;

    public TickJob(int baseTicks, int randomTicks, boolean repeat, Runnable runner) {
        this.baseTicks = baseTicks;
        this.randomTicks = randomTicks;
        this.repeat = repeat;
        this.runner = runner;
        this.rand = new Random();
        refreshTicks();
    }

    private void refreshTicks() {
        this.tick = this.baseTicks;
        if (this.randomTicks > 0)
            this.tick += this.rand.nextInt(this.randomTicks);
    }

    public void tick() {
        this.tick--;
        if (this.tick == 0) {
            this.runner.run();

            if (this.repeat) {
                refreshTicks();
            }
        }
    }

    public boolean isComplete() {
        return (this.tick <= 0);
    }
}
