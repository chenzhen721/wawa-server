package com.wawa.api.event;

import com.wawa.model.ActionResult;
import com.wawa.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/2/27.
 */
public class Task implements Callable<Response<ActionResult>> {
    private static Logger logger = LoggerFactory.getLogger(Task.class);
    private FutureTask<Response<ActionResult>> futureTask = new FutureTask<>(this);
    private Response<ActionResult> result;

    @Override
    public Response<ActionResult> call() throws Exception {
        while(result == null) {
            Thread.sleep(100);
        }
        return result;
    }

    public void setResult(Response<ActionResult> result) {
        this.result = result;
    }

    public Response<ActionResult> get() {
        try {
            return futureTask.get(10000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("Task waiting result timeout error.");
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
