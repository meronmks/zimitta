package com.meronmks.zimitta.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.CustomImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageActivity extends Activity {

	public static CustomImageView imageView;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // UncaughtExceptionHandlerを実装したクラスをセットする。
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
        String Image = intent.getStringExtra("Imeges");
        imageView = (CustomImageView) this.findViewById(R.id.T_Image);
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
			        InputStream input = connection.getInputStream();
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
	        		imageView.setImageBitmap(result);
	        	}else
	        	{
	        		showDialog("画像取得エラー");
	        	}
	        }
	    };
	    task.execute();
	}

	//エラーダイアログ
	public void showDialog(String text){
		AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
		alertDialog.setTitle("Error!");      //タイトル設定
        alertDialog.setMessage(text);  //内容(メッセージ)設定

        // OK(肯定的な)ボタンの設定
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // OKボタン押下時の処理
            }
        });
        alertDialog.show();
	}
}
