package com.meronmks.zimitta.core;

import android.os.Handler;
import android.os.Looper;

/**
 * コード参考(http://darutk-oboegaki.blogspot.jp/2012/01/androidhandler.html)2015年5月31日
 */
public class UiHandler extends Handler implements Runnable
{
    public UiHandler()
    {
        // Looper.getMainLooper() で UI スレッドの Looper を取得する。
        super(Looper.getMainLooper());
    }

    public UiHandler(Handler.Callback callback)
    {
        // Looper.getMainLooper() で UI スレッドの Looper を取得する。
        super(Looper.getMainLooper(), callback);
    }

    public boolean post()
    {
        // 自分で Runnable インターフェースを実装しているので、
        // post メソッドに this を渡すことができる。
        return post(this);
    }

    public boolean postAtFrontOfQueue()
    {
        return postAtFrontOfQueue(this);
    }

    public boolean postAtTime(Object token, long uptimeMillis)
    {
        return postAtTime(this, token, uptimeMillis);
    }

    public boolean postAtTime(long uptimeMillis)
    {
        return postAtTime(this, uptimeMillis);
    }

    public boolean postDelayed(long delayMillis)
    {
        return postDelayed(this, delayMillis);
    }

    public void run()
    {
        // UI スレッドで実行する処理を記述する。
        // サブクラスでオーバーライドすべき。
    }
}