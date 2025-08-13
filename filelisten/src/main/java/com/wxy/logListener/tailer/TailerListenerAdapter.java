package com.cjree.logListener.tailer;


/**
 * @Description:
 * @Auther:wxy
 * @Date:2020/9/18
 */
public class TailerListenerAdapter implements TailerListener {
    /**
     * The tailer will call this method during construction,
     * giving the listener a method of stopping the tailer.
     * @param tailer the tailer.
     */
    @Override
    public void init(TailerCustom tailer) {
    }

    /**
     * This method is called if the tailed file is not found.
     */
    @Override
    public void fileNotFound() {
    }

    /**
     * Called if a file rotation is detected.
     *
     * This method is called before the file is reopened, and fileNotFound may
     * be called if the new file has not yet been created.
     */
    @Override
    public void fileRotated() {
    }

    /**
     * Handles a line from a Tailer.
     * @param line the line.
     */
    @Override
    public void handle(String line) {
    }

    /**
     * Handles an Exception .
     * @param ex the exception.
     */
    @Override
    public void handle(Exception ex) {
    }
}
