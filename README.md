## じみったー(仮)の説明書っぽくもない何か ##

### 2016年7月13日更新 ###
このソースは現在GooglePlayで公開中のtwitterクライアントのソースになります。

## 使用ライブラリ ##
* android-support-v4  
* android-support-v7-appcompat  
* [Android Smart Image View](http://loopj.com/android-smart-image-view/)  
* [jsoup](http://jsoup.org/)
* [twitter4j](http://twitter4j.org/ja/)  
* Glide
* [AsyncHttpClient](http://loopj.com/android-async-http/)  
* [MaterialLoadingProgressBar](https://github.com/lsjwzh/MaterialLoadingProgressBar)  
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)  
* [RxJava](https://github.com/ReactiveX/RxJava)  
* [RxBinding](https://github.com/JakeWharton/RxBinding)
* gradle 2.5

## 参考にしたサイト等 ##
* [[Android] 様々なジェスチャーを処理する(1) 拡大縮小](http://chicketen.blog.jp/archives/1579621.html)
* [[Android] 様々なジェスチャーを処理する(2) 移動](http://chicketen.blog.jp/archives/1622120.html)

## 直さないといけないバグ ##
* 実機でもOutOfMemoryで落ちることがある
* レシーバー結果が何度も繰り返される（吸収すればいいか？）

## TODO ##
* ユーザー情報から見れるユーザー単体のTLからRTを排除できるようなオプションの搭載
* ListTLを複数合成して一つのTLで表示する機能を付けたい（できれば）
* ヘッダー部分にAPI残量とリセット時間の表記をする（工夫しないときつそう）
* ツイート内のリンクへの手間軽減（タップ数の軽減？）
* フォロー中の人の表示名を自由に変えれるようにする
* アカウント情報のバックアップ書き出し機能を付けてみる？

## Ver3.4.1との変更点（メモしないと忘れそう） ##
* 通知機能の完全復活
* Rxの導入
* レイアウト修正
* 一部画面が真っ黒になる問題の修正
* [ZoomImageView](http://sukohi.blogspot.jp/2013/11/imageview.html)からSurfaceViewへ画像表示を変更
* 画像のプレビュー時に変なところで切れてしまう現象の修正
* サムネイル画像表示での通信量削減
