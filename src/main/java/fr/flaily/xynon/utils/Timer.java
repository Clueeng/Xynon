package fr.flaily.xynon.utils;

public class Timer {
    public long lastExecution = System.currentTimeMillis();

    private boolean canRun(long delay) {
        return System.currentTimeMillis() - this.lastExecution >= delay;
    }

    public void execute(Runnable r, long delay, boolean repeat) {
        if(canRun(delay)) {
            r.run();
            
            if(repeat)
                this.lastExecution = System.currentTimeMillis();
        }
    }

}
