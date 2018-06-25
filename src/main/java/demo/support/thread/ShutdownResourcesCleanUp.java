package demo.support.thread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShutdownResourcesCleanUp extends Thread {

    private final ThreadRunner[] stoppable;

    public ShutdownResourcesCleanUp(ThreadRunner... stoppable) {
        this.stoppable = stoppable;
    }

    @Override
    public void run() {
        log.info("Stopping the Backend...");
        for (ThreadRunner stoppable : stoppable) {
            stoppable.stop();
        }
    }
}
