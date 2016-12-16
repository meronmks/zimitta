package com.meronmks.zimitta.Listener;

/**
 * Created by meron on 2016/07/21.
 * 読み込むサイズを見るためのｲﾝﾀｰふぇぇぇイス
 */
public interface MonitorInputStreamListener {
    /**
     * @param totalReadSize 読み込まれた全サイズ
     * @param read 今回読み込まれたサイズ
     */
    void onStreamRead(long totalReadSize, int read);

    /**
     * @param totalReadSize 読み込まれた全サイズ
     */
    void onStreamEnd(long totalReadSize);
}
