## れどみと言うよりただのメモ ##

### 2016年1月19日更新 ###
このソースは現在GooglePlayで公開中のtwitterクライアントのソースになります。

## 使用ライブラリ ##
android-support-v4  
android-support-v7-appcompat  
[Android Smart Image View](http://loopj.com/android-smart-image-view/)  
[jsoup](http://jsoup.org/)  
[twitter4j](http://twitter4j.org/ja/)  
Glide  
[AsyncHttpClient](http://loopj.com/android-async-http/)  
[MaterialLoadingProgressBar](https://github.com/lsjwzh/MaterialLoadingProgressBar)  
[ZoomImageView](http://sukohi.blogspot.jp/2013/11/imageview.html)  
[RxAndroid](https://github.com/ReactiveX/RxAndroid)  
[RxJava](https://github.com/ReactiveX/RxJava)  
[RxBinding](https://github.com/JakeWharton/RxBinding)  
## 直さないといけないバグ ##
* 実機でもOutOfMemoryで落ちることがある
* レシーバー結果が何度も繰り返される（吸収すればいいか？）

## TODO ##
* ユーザー情報から見れるユーザー単体のTLからRTを排除できるようなオプションの搭載
* ListTLを複数合成して一つのTLで表示する機能を付けたい（できれば）
* ヘッダー部分にAPI残量とリセット時間の表記をする（工夫しないときつそう）
* ツイート内のリンクへの手間軽減（タップ数の軽減？）
* フォロー中の人の表示名を自由に変えれるようにする

## Ver3.4.1との変更点（メモしないと忘れそう） ##
* 通知機能の完全復活
* Rxの導入
* 一部レイアウト修正
