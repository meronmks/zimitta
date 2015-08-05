package com.meronmks.zimitta.menu;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import twitter4j.Status;
import twitter4j.URLEntity;

public class ListMenuAdon {
	private Context context;
	private  SharedPreferences sp;

	public ListMenuAdon(Context c) {
		this.context = c;
	}

	public void URLList(Status Tweet){
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		if(Tweet.getRetweetedStatus() == null){
			//RTされておれず
			final URLEntity[] UrlLink = Tweet.getURLEntities();
			if(UrlLink.length !=0)
			{
				//WebURLのみ
				String[] urlItem = new String[UrlLink.length];
				for(int i = 0;i < UrlLink.length; i++)
        		{
					urlItem[i] = UrlLink[i].getExpandedURL();
        		}
    			AlertDialog.Builder dialogMenu = new AlertDialog.Builder(context);
                dialogMenu.setItems(urlItem, new DialogInterface.OnClickListener() {
                	@Override
                    public void onClick(DialogInterface dialog, int which) {
                		Uri uri = Uri.parse(UrlLink[which].getURL().toString());
        				Intent i = new Intent(Intent.ACTION_VIEW,uri);
        				context.startActivity(i);
					}
                }).create().show();
			}
			else
			{
				Toast.makeText(context, "リンクが見つかりません", Toast.LENGTH_SHORT).show();
			}
		}
		else if(Tweet.getRetweetedStatus().getURLEntities() != null){
			//RTされていて
			final URLEntity[] UrlLink = Tweet.getRetweetedStatus().getURLEntities();

			if(UrlLink.length !=0)
			{
				//WebURLのみ
				String[] urlItem = new String[UrlLink.length];
    			for(int i = 0;i < UrlLink.length; i++)
        		{
    				urlItem[i] = UrlLink[i].getURL().toString();	//メニューの項目作り
        		}
    			AlertDialog.Builder dialogMenu = new AlertDialog.Builder(context);
                dialogMenu.setItems(urlItem, new DialogInterface.OnClickListener() {
                	@Override
                    public void onClick(DialogInterface dialog, int which) {
                		Uri uri = Uri.parse(UrlLink[which].getURL().toString());
        				Intent i = new Intent(Intent.ACTION_VIEW,uri);
        				context.startActivity(i);
					}
                }).create().show();
			}
			else
			{
				Toast.makeText(context, "リンクが見つかりません", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
