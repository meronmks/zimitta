/** じみったー（仮）における、連携アプリ一覧画面のソース
 * 　使用外部ライブラリ
 * AsyncHttpClient
 * Jsoup **/

package com.meronmks.zimitta.AppCooperation;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.loopj.android.http.*;
import com.meronmks.zimitta.Adapter.AppListAdapter;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.CoreActivity;
import com.meronmks.zimitta.menu.List_Menu;
import cz.msebera.android.httpclient.Header;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ApplicationsCooperationActivity extends AppCompatActivity {

	private AsyncHttpClient client;
	private PersistentCookieStore myCookieStore;
	private AppListAdapter mAdapter;
	private ArrayList<String> app_oauth = new ArrayList<String>();	//アプリごとの連携解除に必要な英数字を格納しておくやつ
	private ListView lv;
	private String authenticity_token = null;
	private String mail,pass;
	private List_Menu list = new List_Menu();	//メニュー表示処理を書いてるクラス読み込み
	private ProgressDialog progressDialog;
    private RequestParams params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);

		setContentView(R.layout.listview_base);

		getSupportActionBar().setDisplayShowHomeEnabled(false);	//ActionBarからアイコンを消す

		progressDialog = new ProgressDialog(this);
		// プログレスダイアログのメッセージを設定します
		progressDialog.setMessage("ログイン中");
		// プログレスダイアログの確定（false）／不確定（true）を設定します
		progressDialog.setIndeterminate(false);
		//プログレスダイアログのスタイルを円スタイルに設定します
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// プログレスダイアログのキャンセルが可能かどうかを設定します
		progressDialog.setCancelable(false);
		// プログレスダイアログを表示します
		progressDialog.show();

        //ListView関連
    	lv =  (ListView)findViewById(R.id.listView_base);
        mAdapter = new AppListAdapter(this);
        lv.setAdapter(mAdapter);

        //情報受け取り
        Intent intent = getIntent();
        mail = (String)intent.getSerializableExtra("meil");
        pass = (String)intent.getSerializableExtra("pass");

		//ListViewのクリックリスナー登録
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			//通常押し
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				list.App_Menu(ApplicationsCooperationActivity.this,authenticity_token, app_oauth.get(position), client);
			}

		});

      	lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      			//長押し
      			@Override
      			public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
					return true;

      			}
      		});
        params = new RequestParams();
        //Web版ログイン処理
        params.put("session[username_or_email]", mail);	//post or get時に付与する情報の設定
        params.put("session[password]", pass);
        client = new AsyncHttpClient();
        myCookieStore  =  new  PersistentCookieStore ( this );
        myCookieStore.clear();	//一応クッキーの初期化

        //連携アプリ一覧取得開始
        client.setUserAgent("Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko");	//エージェント設定、PCに設定しないと自動でスマホ扱いになる
		client.setCookieStore (myCookieStore);	//クッキーの使用に必要、あとは勝手にやってくれてるっぽい？
		client.get("https://twitter.com/login", new AsyncHttpResponseHandler(){ // client.get を client.post にすれば、POST通信もできます
			@Override
			public void onStart(){
				// 通信開始時の処理
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response){
                String string = null;
                try {
                    string = new String(response, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                int index = string.indexOf("<input type=\"hidden\" value=\"");
				if(index != -1){
                    string = string.substring(index + 28);
                }
				index = string.indexOf("\" name=\"authenticity_token\"/>");
				if(index != -1){
                    string = string.substring(0,index);
				}
				authenticity_token = string;
				params.put("authenticity_token", response);
				params.put("remember_me", "0");
		        client.get("https://twitter.com/sessions", params, new AsyncHttpResponseHandler() { // client.get を client.post にすれば、POST通信もできます
                    @Override
                    public void onStart() {
                        // 通信開始時の処理
                    }

                    @Override
                    public void onFinish() {
                        // 通信終了時の処理
                    }

                    @Override
                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                        progressDialog.dismiss();
                        // プログレスダイアログのメッセージを設定します
                        progressDialog.setMessage("読み込み中");
                        // プログレスダイアログの確定（false）／不確定（true）を設定します
                        progressDialog.setIndeterminate(false);
                        //プログレスダイアログのスタイルを円スタイルに設定します
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        // プログレスダイアログのキャンセルが可能かどうかを設定します
                        progressDialog.setCancelable(false);
                        // プログレスダイアログを表示します
                        progressDialog.show();
                        String string = null;
                        try {
                            string = new String(bytes, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        string = string;
                        getapplications();
                    }

                    @Override
                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        CoreActivity.showToast("ログインエラー");
                        if (CoreActivity.isDebugMode) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationsCooperationActivity.this);
                            builder.setTitle("ERROR!");
                            try {
                                builder.setMessage(new String(bytes, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        progressDialog.dismiss();
                    }
                });
			}

			@Override
			public void onFinish(){
				// 通信終了時の処理
			}

			@Override
			public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {

			}
		});
	}

	//アプリ一覧取得メソッド
	protected void getapplications(){
		client.get("https://twitter.com/settings/applications", new JsonHttpResponseHandler(){ // client.get を client.post にすれば、POST通信もできます

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// 通信成功時の処理
				Document document = Jsoup.parse(response.toString(), "UTF-8");
				Elements elements = document.select(".stream .app");
				for (Element element : elements) {
					Elements elementss = element.select("strong");
					//名前抜出
					StringBuilder sb = new StringBuilder();
					for (Element element2 : elementss) {
						sb.append("アプリ名：" + element2.text());
						Elements elementsss = element.select("button");
						for (Element element3 : elementsss) {
							String str = new String(sb);
							mAdapter.add(str);
							str = element3.id().replaceAll("btn_oauth_application_", "");
							app_oauth.add(str);
						}
					}
				}
				progressDialog.dismiss();
			}

		});
	}
}
