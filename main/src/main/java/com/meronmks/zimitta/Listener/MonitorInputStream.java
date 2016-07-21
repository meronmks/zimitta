package com.meronmks.zimitta.Listener;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by meron on 2016/07/21.
 */
public class MonitorInputStream extends FilterInputStream implements MonitorInputStreamListener {
    private int mReadSize = 0;
    private MonitorInputStreamListener mListener;

    public MonitorInputStream(InputStream in) {
        super(in);
        mReadSize = 0;
        mListener = this;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        if (markSupported()) {
            mReadSize = 0;
        }
    }

    @Override
    public int read() throws IOException {
        int size = super.read();
        publishProgress(1);
        return size;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int size = super.read(b);
        publishProgress(size);
        return size;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int size = super.read(b, off, len);
        publishProgress(size);
        return size;
    }

    /**
     * リスナーをセットする
     * @param listener
     */
    public void setStreamMonitor(MonitorInputStreamListener listener) {
        mListener = listener;
    }

    private void publishProgress(int size) {
        if (0 < size) {
            mReadSize = mReadSize + size;
            mListener.onStreamRead(mReadSize, size);
        }
        if (-1 == size) {
            mListener.onStreamEnd(mReadSize);
        }
    }

    @Override
    public void onStreamRead(long totalReadSize, int read) {

    }

    @Override
    public void onStreamEnd(long totalReadSize) {

    }
}
