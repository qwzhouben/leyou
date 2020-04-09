package com.leyou.goods.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/4/8 0008 20:12
 */
public class ThreadUtils {

    private static final ExecutorService service = Executors.newFixedThreadPool(10);

    public static void execute(Runnable runnable) {
        service.submit(runnable);
    }
}
