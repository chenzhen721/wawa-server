package com.wawa.api.event;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/2/27.
 */
public class Task implements Callable<Map> {
    private FutureTask<Map> futureTask = new FutureTask<>(this);
    private Map result;

    @Override
    public Map call() throws Exception {
        return result;
    }

    public void setResult(Map result) {
        this.result = result;
    }

    public Map get() {
        try {
            return futureTask.get(10000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void execute(ExecutorService executor) {
        executor.submit(futureTask);
    }

    public void cancel() {
        futureTask.cancel(false);
    }

}
