## れどみと言うよりただのメモ ##

### 2015年7月17日更新 ###
このソースは現在GooglePlayで公開中のtwitterクライアントの書き直し中のソースになります。

## 使用ライブラリ ##
android-support-v4  

android-support-v7-appcompat

[Android Smart Image View](http://loopj.com/android-smart-image-view/ "Android Smart Image View")  

[jsoup](http://jsoup.org/ "jsoup")  

[twitter4j](http://twitter4j.org/ja/ "twitter4j") 

Glide

[AsyncHttpClient](http://loopj.com/android-async-http/ "AsyncHttpClient")

## 直さないといけないバグ ##
* Fragment上に設置したListView以外が反応悪くなる（機種依存問題の為時間かかりそう）
* 実機でもOutOfMemoryで落ちることがある

## ToDo ##
* ユーザー情報から見れるユーザー単体のTLからRTを排除できるようなオプションの搭載
* ListTLを複数合成して一つのTLで表示する機能を付けたい（できれば）
* ヘッダー部分にAPI残量とリセット時間の表記をする（工夫しないときつそう）
* 画像表示後に戻るキーで画像一覧にもどす
* ツイート内のリンクへの手間軽減（タップ数の軽減？）
* TL上でのサムネイル表示
* フォロー中の人の表示名を自由に変えれるようにする

## Ver3.1.0との変更点（メモしないと忘れそう） ##
* ActionBarSherlockを使用ライブラリから外しandroid-support-v7-appcompatで代替
* Build方式を大幅に変更
