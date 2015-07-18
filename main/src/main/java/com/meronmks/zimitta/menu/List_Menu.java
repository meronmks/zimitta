package com.meronmks.zimitta.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ShareCompat;
import android.text.ClipboardManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.meronmks.zimitta.Activity.*;
import com.meronmks.zimitta.AppCooperation.WebTwitterLoginActivity;
import com.meronmks.zimitta.core.MainActivity;
import com.meronmks.zimitta.core.TwitterActionClass;
import com.meronmks.zimitta.settings.SettingActivity;
import com.meronmks.zimitta.user.Prof_Activity;
import twitter4j.*;

public class List_Menu {

	public void Prof_Menu(final Context context, final Twitter mTwitter, final User user){
		String[] dialogItem;
		dialogItem = new String[]{"ブロック","ミュート"};	//メニューの項目作り
		AlertDialog.Builder dialogMenu = new AlertDialog.Builder(context);
		dialogMenu.setItems(dialogItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
					case 0:
						AsyncTask<Void, Void,  Boolean> task = new AsyncTask<Void, Void, Boolean>() {
							//処理をここに書く
							@Override
							protected Boolean doInBackground(Void... params) {
								try{
									mTwitter.createBlock(user.getId());
									return true;
								} catch (TwitterException e){
									e.printStackTrace();
								} catch(Exception e) {
									e.printStackTrace();
								}
								return false;
							}
							//処理が終わった後の処理
							@Override
							protected void onPostExecute(Boolean result) {
								if (result != false) {
									MainActivity.showToast("ブロックしました。");
								} else {
									MainActivity.showToast("ブロック失敗・・・");
								}
							}
						};
						task.execute();
						break;
					case 1:	//ミュート
						TwitterActionClass mtAction = new TwitterActionClass(context);
						mtAction.createMute(user.getId());
						break;
				}
			}
		}).create().show();
	}

	public void App_Menu(Context context, final String authenticity_token,final String app_oauth,final AsyncHttpClient client){
		String[] dialogItem;
		dialogItem = new String[]{"連携解除"};	//メニューの項目作り
		AlertDialog.Builder dialogMenu = new AlertDialog.Builder(context);
        dialogMenu.setItems(dialogItem, new DialogInterface.OnClickListener() {
        	@Override
            public void onClick(DialogInterface dialog, int which) {
            	switch(which) {
					case 0:
						RequestParams params = new RequestParams();
						params.put("authenticity_token", authenticity_token);
						params.put("token", app_oauth);
						//連携アプリ一覧取得開始
						client.post("https://twitter.com/oauth/revoke", params, new AsyncHttpResponseHandler() { // client.get を client.post にすれば、POST通信もできます
							@Override
							public void onStart() {
								// 通信開始時の処理
							}

							@Override
							public void onSuccess(String response) {
								// 通信成功時の処理
								MainActivity.showToast("連携解除しました");
							}

							@Override
							public void onFailure(Throwable e, String response) {
								// 通信失敗時の処理
								MainActivity.showToast("解除失敗・・");
							}

							@Override
							public void onFinish() {
								// 通信終了時の処理
							}
						});
						break;
					default:
						break;
            	}
        	}
        }).create().show();
	}
	
	public void Tweet_Menu(final Context context, final Status Tweet)
	{
		String[] dialogItem;
		if(MainActivity.DebugMode){
			dialogItem = new String[]{"詳細","返信", "リツイート", "ふぁぼ","ユーザー詳細","リンク先処理","DM送信","共有","ShowData"};	//メニューの項目作り
		}else {
			dialogItem = new String[]{"詳細", "返信", "リツイート", "ふぁぼ", "ユーザー詳細", "リンク先処理", "DM送信", "共有"};    //メニューの項目作り
		}
		AlertDialog.Builder dialogMenu = new AlertDialog.Builder(context);
        dialogMenu.setItems(dialogItem, new DialogInterface.OnClickListener() {
        	@Override
            public void onClick(DialogInterface dialog, int which) {
            	switch(which) {
					case 0:	//ツイート詳細
						Intent intent_Detail = new Intent(context,TweetDetail.class);
						intent_Detail.putExtra("Detail",Tweet);
						context.startActivity(intent_Detail);
						break;
					case 1:		//リプライ
						Intent intent = new Intent(context, MentionsActivity.class);
						intent.putExtra("mentionID", Tweet.getId());
						intent.putExtra("StatusID", Tweet.getUser().getScreenName());
						intent.putExtra("Name", Tweet.getUser().getName());
						intent.putExtra("Tweet", Tweet.getText());
						intent.putExtra("Image", Tweet.getUser().getProfileImageURL());
						String[] name = new String[20];
						int j = 0;
						for (UserMentionEntity UrlLink : Tweet.getUserMentionEntities()) {
							name[j] = UrlLink.getScreenName();
							j++;
						}
						intent.putExtra("UserMentionEntities", name);
						context.startActivity(intent);
						break;
					case 2:		//リツイート
						new AlertDialog.Builder(context)
						.setTitle("RTしてよろしいですか？")
						.setPositiveButton(
						"はい",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// OK時の処理
                                TwitterActionClass mtAction = new TwitterActionClass(context);
                                mtAction.RTPost(Tweet.getId());
							}
						})
						.setNegativeButton(
						"いいえ",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// NO時の処理
							}
						})
						.show();
						break;
					case 3:		//お気に入り

						new AlertDialog.Builder(context)
						.setTitle("ふぁぼしますか？")
						.setPositiveButton(
						"はい",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// OK時の処理
                                TwitterActionClass mtAction = new TwitterActionClass(context);
                                mtAction.FaPost(Tweet.getId());
							}
						})
						.setNegativeButton(
						"いいえ",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// NO時の処理
							}
						})
						.show();
						break;
					case 4:	//ユーザー詳細
						final Intent My_Prof_Intent = new Intent(context, Prof_Activity.class);
						if(Tweet.getRetweetedStatus() == null){
							My_Prof_Intent.putExtra("UserID", Tweet.getUser().getId());
							context.startActivity(My_Prof_Intent);
						}else{
							String[] dialogItems;
							dialogItems = new String[]{"@" + Tweet.getRetweetedStatus().getUser().getScreenName(),"@" + Tweet.getUser().getScreenName()};    //メニューの項目作り
							AlertDialog.Builder dialogMenus = new AlertDialog.Builder(context);
							dialogMenus.setItems(dialogItems, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									switch(which)
									{
										case 0:
											My_Prof_Intent.putExtra("UserID", Tweet.getRetweetedStatus().getUser().getId());
											context.startActivity(My_Prof_Intent);
											break;
										case 1:
											My_Prof_Intent.putExtra("UserID", Tweet.getUser().getId());
											context.startActivity(My_Prof_Intent);
											break;
									}
								}
							}).create().show();
						}
						break;
					case 5:	//URLを開く
						ListMenuAdon list = new ListMenuAdon(context);
						list.URLList(Tweet);
						break;
					case 6: //DM
						Intent DMintent = new Intent(context, DMSendActivity.class);
						DMintent.putExtra("mentionID", Tweet.getUser().getId());
						context.startActivity(DMintent);
						break;
					case 7:	//共有
						ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from((Activity) context);
						builder.setText(Tweet.getText());
						builder.setType("text/plain");
						// Intent を起動する
						builder.startChooser();
						break;
					case 8://デバック用
						ShowDebugStatusAll(context,Tweet);
						break;
					default:
						MainActivity.showToast("Tweetメニューで例外発生");
						break;
            	}
			}
        }).create().show();
	}

	public void DM_Menu(final Context context, final twitter4j.DirectMessage Tweet){
		String[] dialogItem;
		dialogItem = new String[]{"返信","ユーザー詳細"};	//メニューの項目作り
		AlertDialog.Builder dialogMenu = new AlertDialog.Builder(context);
		dialogMenu.setItems(dialogItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which)
				{
					case 0:
                        Intent DMintent = new Intent(context, DMSendActivity.class);
                        DMintent.putExtra("mentionID", Tweet.getSenderId());
                        context.startActivity(DMintent);
                        break;
                    case 1:
                        Intent My_Prof_Intent = new Intent(context, Prof_Activity.class);
                        My_Prof_Intent.putExtra("UserID", Tweet.getSenderId());
                        context.startActivity(My_Prof_Intent);
                        break;
					default:
						MainActivity.showToast("DMメニューにて例外発生");
						break;
				}
			}
		}).create().show();
	}

	public void Detail_Menu(final Context context, final Status Tweet)
	{
		String[] dialogItem;
		dialogItem = new String[]{"ツイートをコピー","ユーザー詳細","ツイートのリンクをコピー"};	//メニューの項目作り
		AlertDialog.Builder dialogMenu = new AlertDialog.Builder(context);
        dialogMenu.setItems(dialogItem, new DialogInterface.OnClickListener() {
        	@Override
            public void onClick(DialogInterface dialog, int which) {
        		switch(which)
            	{
					case 0:	//ツイートのコピー
						String cdTweet = Tweet.getText();
						//クリップボードにデータを格納
						ClipboardManager cmTweet = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
						cmTweet.setText(cdTweet);
						MainActivity.showToast("ツイートをコピーしました");
						break;
					case 1:	//ユーザ詳細
						Intent My_Prof_Intent = new Intent(context, Prof_Activity.class);
						if(Tweet.getRetweetedStatus() == null){
							My_Prof_Intent.putExtra("UserID", Tweet.getUser().getId());
						}else{
							My_Prof_Intent.putExtra("UserID", Tweet.getRetweetedStatus().getUser().getId());
						}
						context.startActivity(My_Prof_Intent);
						break;
					case 2:	//リンクコピー
						String cdlink = "https://twitter.com/" + Tweet.getUser().getScreenName() + "/status/" + Tweet.getId();
						//クリップボードにデータを格納
						ClipboardManager cmlink = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
						cmlink.setText(cdlink);
						MainActivity.showToast("ツイートへのリンクをコピーしました");
						break;
					default:
						MainActivity.showToast("Detailメニューにて例外発生");
						break;
            	}
        	}
        }).create().show();
	}

	/**
	 * メインメニュー
	 */
	public void Main_menu(final Activity activity, final Context context,boolean DebugMode, final long Userid) {
		String[] dialogItem;
		if(DebugMode) {
			dialogItem = new String[]{"プロフィール表示", "アカウント切替と追加", "連携アプリ", "設定","ShowLimit"};    //メニューの項目作り
		}else{
			dialogItem = new String[]{"プロフィール表示", "アカウント切替と追加", "連携アプリ", "設定"};    //メニューの項目作り
		}
		AlertDialog.Builder dialogMenu = new AlertDialog.Builder(context);
		dialogMenu.setItems(dialogItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						Intent My_Prof_Intent = new Intent(context, Prof_Activity.class);
						My_Prof_Intent.putExtra("UserID", Userid);
						context.startActivity(My_Prof_Intent);

						break;
					case 1:
						Intent Account = new Intent(context, AccountChangeActivity.class);
						context.startActivity(Account);
						activity.finish();
						break;
					case 2:
						Intent SettingsApplications = new Intent(context, WebTwitterLoginActivity.class);
						context.startActivity(SettingsApplications);
						break;
					case 3:
						Intent setting = new Intent(context, SettingActivity.class);
						context.startActivity(setting);
						break;
					case 4:
						TwitterActionClass mtAction;
						mtAction = new TwitterActionClass(context);
						mtAction.debugMode();
						break;
					default:
						MainActivity.showToast("Menuで例外発生");
						break;
				}
			}
		}).create().show();
	}

	private void ShowDebugStatusAll(Context context, Status Tweet){
		String DebugText = Tweet.toString();
		DebugText = DebugText.replaceAll(",","\n");
		DebugText = DebugText.replaceAll("=","\n");
		showDialog(context,DebugText);
	}

	private void showDialog(Context context ,String text) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("StatusAll");      //タイトル設定
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
