## れどみと言うよりただのメモ ##

### 2015年8月5日更新 ###
このソースは現在GooglePlayで公開中のtwitterクライアントのソースになります。

## 使用ライブラリ ##
android-support-v4  

android-support-v7-appcompat

[Android Smart Image View](http://loopj.com/android-smart-image-view/ "Android Smart Image View")  

[jsoup](http://jsoup.org/ "jsoup")  

[twitter4j](http://twitter4j.org/ja/ "twitter4j") 

Glide

[AsyncHttpClient](http://loopj.com/android-async-http/ "AsyncHttpClient")

[MaterialDesignLibrary](https://github.com/navasmdc/MaterialDesignLibrary "MaterialDesignLibrary")

## 直さないといけないバグ ##
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
* ライブラリ変更に伴いマテリアルデザインの投入
* Build方式を大幅に変更
* 特定環境で処理落ちするバグを修正
* 引用表示に対応
* 画像のサムネイル表示（公式分のみ）対応