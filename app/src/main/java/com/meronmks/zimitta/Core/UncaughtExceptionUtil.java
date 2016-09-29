package com.meronmks.zimitta.Core;

import com.meronmks.zimitta.Datas.ErrorLogs;

/**
 * Created by p-user on 2016/09/29.
 */
public class UncaughtExceptionUtil implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        ErrorLogs.putErrorLog("予期しない例外が発生しました。", throwable.getMessage());
    }
}
