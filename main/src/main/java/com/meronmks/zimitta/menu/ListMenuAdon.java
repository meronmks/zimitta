package com.meronmks.zimitta.menu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.meronmks.zimitta.Activity.ImageActivity;
import twitter4j.MediaEntity;
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
			final MediaEntity[] ImgLink;
			final URLEntity[] UrlLink = Tweet.getURLEntities();
			ImgLink = Tweet.getExtendedMediaEntities();
			if((UrlLink.length !=0) && (ImgLink.length != 0))
			{
				//WebURLと画像の両方があった場合
				String[] urlItem = new String[ImgLink.length + UrlLink.length];
				int i;
				final int j;
    			for(i = 0;i < ImgLink.length; i++)
        		{
    				urlItem[i] = ImgLink[i].getMediaURL();	//メニューの項目作り
        		}
    			j = i;
    			for(i = 0;i < UrlLink.length; i++)
        		{
    				urlItem[i + j] = UrlLink[i].getExpandedURL();
        		}
    			AlertDialog.Builder dialogMenu = new AlertDialog.Builder(context);
                dialogMenu.setItems(urlItem, new DialogInterface.OnClickListener() {

                	@Override
                    public void onClick(DialogInterface dialog, int which) {
                		if(which < j){
	                		Intent image = new Intent(context, ImageActivity.class);
	        				image.putExtra("Imeges", ImgLink[which].getMediaURL());
	        				context.startActivity(image);
                		}else{
	        				Uri uri = Uri.parse(UrlLink[which-j].getURL().toString());
	        				Intent i = new Intent(Intent.ACTION_VIEW,uri);
	        				context.startActivity(i);
                		}
					}

                }).create().show();
			}else if((UrlLink.length ==0) && (ImgLink.length != 0))
			{
				//画像URLのみ
				String[] imageItem = new String[ImgLink.length];
    			for(int i = 0;i < ImgLink.length; i++)
        		{
        			imageItem[i] = ImgLink[i].getMediaURL();	//メニューの項目作り
        		}
    			AlertDialog.Builder dialogMenu = new AlertDialog.Builder(context);
                dialogMenu.setItems(imageItem, new DialogInterface.OnClickListener() {

                	@Override
                    public void onClick(DialogInterface dialog, int which) {
                		Intent image = new Intent(context, ImageActivity.class);
        				image.putExtra("Imeges", ImgLink[which].getMediaURL());
        				context.startActivity(image);
					}

                }).create().show();
			}else if((UrlLink.length !=0) && (ImgLink.length == 0))
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
			final MediaEntity[] ImgLink;
			ImgLink = Tweet.getRetweetedStatus().getExtendedMediaEntities();

			if((UrlLink.length !=0) && (ImgLink.length != 0))
			{
				//WebURLと画像の両方があった場合
				String[] urlItem = new String[ImgLink.length + UrlLink.length];
				int i;
				final int j;
    			for(i = 0;i < ImgLink.length; i++)
        		{
    				urlItem[i] = ImgLink[i].getMediaURL();	//メニューの項目作り
        		}
    			j = i;
    			for(i = 0;i < UrlLink.length; i++)
        		{
    				urlItem[i+j] = UrlLink[i].getURL().toString();	//メニューの項目作り
        		}
    			AlertDialog.Builder dialogMenu = new AlertDialog.Builder(context);
                dialogMenu.setItems(urlItem, new DialogInterface.OnClickListener() {

                	@Override
                    public void onClick(DialogInterface dialog, int which) {
                		if(which < j){
	                		Intent image = new Intent(context, ImageActivity.class);
	        				image.putExtra("Imeges", ImgLink[which].getMediaURL());
	        				context.startActivity(image);
                		}else{
	        				Uri uri = Uri.parse(UrlLink[which-j].getURL().toString());
	        				Intent i = new Intent(Intent.ACTION_VIEW,uri);
	        				context.startActivity(i);
                		}
					}

                }).create().show();
			}else if((UrlLink.length ==0) && (ImgLink.length != 0))
			{
				//画像URLのみ
				String[] imageItem = new String[ImgLink.length];
    			for(int i = 0;i < ImgLink.length; i++)
        		{
        			imageItem[i] = ImgLink[i].getMediaURL();	//メニューの項目作り
        		}
    			AlertDialog.Builder dialogMenu = new AlertDialog.Builder(context);
                dialogMenu.setItems(imageItem, new DialogInterface.OnClickListener() {

                	@Override
                    public void onClick(DialogInterface dialog, int which) {
                		Intent image = new Intent(context, ImageActivity.class);
        				image.putExtra("Imeges", ImgLink[which].getMediaURL());
        				context.startActivity(image);
					}

                }).create().show();
			}else if((UrlLink.length !=0) && (ImgLink.length == 0))
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
