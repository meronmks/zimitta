package com.meronmks.zimitta.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;

import com.meronmks.zimitta.Listener.MonitorInputStream;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.CustomSurfaceView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageActivity extends Activity {

	private static CustomSurfaceView surfaceView;
	private RelativeLayout rootLayout;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// UncaughtExceptionHandlerを実装したクラスをセットする。
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = getIntent();
		String Image = intent.getStringExtra("Imeges");
		surfaceView = new CustomSurfaceView(getApplicationContext());
		surfaceView.setImageResourceId(R.drawable.ic_action_refresh);
		setContentView(surfaceView);
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
					final long contentSize = connection.getContentLength();
					InputStream input = connection.getInputStream();
					input = new MonitorInputStream(input) {
						@Override
						public void onStreamRead(long totalReadSize, int size) {
							//listener.onLoadingProgressUpdated(totalReadSize, contentSize);
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
				}else
				{
					showDialog("画像取得に失敗しました。");
				}
			}
		};
		task.execute();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		// Viewサイズを取得する
		rootLayout = (RelativeLayout) findViewById(R.id.relativeLayout1);
	}

	/**
	 * ぴったりになるように画像サイズ調整のつもり
	 * @param result
	 * @return
     */
	private double getFitScale(Bitmap result){
		double scale;
		if(result.getHeight() > result.getWidth()){
			scale = ((double)rootLayout.getHeight() / (double)result.getHeight())/1000.0;
		}else{
			scale = ((double)rootLayout.getWidth() / (double)result.getWidth())/1000.0;
		}
		return scale;
	}

	/**
	 * エラーが発生時のダイアログ表示
	 * @param text
     */
	private void showDialog(String text){
		AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
		alertDialog.setTitle("Error!");      //タイトル設定
        alertDialog.setMessage(text);  //内容(メッセージ)設定

        // OK(肯定的な)ボタンの設定
        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            // OKボタン押下時の処理
        });
        alertDialog.show();
	}
}
