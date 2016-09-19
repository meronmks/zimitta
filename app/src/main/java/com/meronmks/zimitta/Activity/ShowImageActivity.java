package com.meronmks.zimitta.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.ImageMoveUtil.CustomSurfaceView;
import com.meronmks.zimitta.Listener.MonitorInputStream;
import com.meronmks.zimitta.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by meron on 2016/09/18.
 */
public class ShowImageActivity extends AppCompatActivity {
    private static CustomSurfaceView surfaceView;
    private RelativeLayout rootLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // UncaughtExceptionHandlerを実装したクラスをセットする。
        Intent intent = getIntent();
        String Image = intent.getStringExtra("Images");

        rootLayout = new RelativeLayout(this);
        surfaceView = new CustomSurfaceView(getApplicationContext());
        surfaceView.setImageResourceId(R.mipmap.clear);

        progressBar = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        setContentView(rootLayout);
        rootLayout.addView(surfaceView);
        rootLayout.addView(progressBar, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        getBitmapFromURL(Image);
    }

    public Object fetch(String address) throws MalformedURLException,IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }

    public void getBitmapFromURL(final String src) {
        AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    URL url = new URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    long contentSize = connection.getContentLength();
                    progressBar.setMax((int)contentSize);
                    InputStream input = connection.getInputStream();
                    input = new MonitorInputStream(input) {
                        @Override
                        public void onStreamRead(long totalReadSize, int size) {
                            progressBar.setProgress((int)totalReadSize);
                        }
                    };
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if(result != null)
                {
                    surfaceView.setImageBitmap(result);
                    progressBar.setVisibility(View.GONE);
                }else
                {
                    showDialog("画像取得に失敗しました。");
                }
            }
        };
        task.execute();
    }

    /**
     * エラーが発生時のダイアログ表示
     * @param text
     */
    private void showDialog(String text){
        android.app.AlertDialog.Builder alertDialog=new android.app.AlertDialog.Builder(this);
        alertDialog.setTitle("Error!");      //タイトル設定
        alertDialog.setMessage(text);  //内容(メッセージ)設定

        // OK(肯定的な)ボタンの設定
        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            // OKボタン押下時の処理
        });
        alertDialog.show();
    }
}
