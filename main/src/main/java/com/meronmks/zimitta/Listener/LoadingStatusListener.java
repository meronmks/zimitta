package com.meronmks.zimitta.Listener;

/**
 * Created by p-user on 2016/07/22.
 * 読み込み状況を表示するためのいんたーふぇいす
 */
public interface LoadingStatusListener {
    /**
     * 読み込み状況の更新
     * @param totalReadSize
     * @param contentSize
     */
    void onLoadingProgressUpdated(long totalReadSize, long contentSize);

    /**
     * 読み込み完了
     */
    void onLoadingSuccess();

    /**
     * エラー発生
     * @param messege
     */
    void onLoadingFailed(String messege);
}
