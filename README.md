## じみったー(仮)の説明書っぽくもない何か ##
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
* [Android (Java) で InputStream / OutputStream の進捗状況の取得・表示](http://foreignkey.toyao.net/archives/1386)
* [TextViewのリンク以外をクリックした時はListViewのクリックに行く方法](http://oigami.hatenablog.com/entry/2014/11/08/082615)

## 直さないといけないバグ ##
* 実機でもOutOfMemoryで落ちることがある
* レシーバー結果が何度も繰り返される（吸収すればいいか？）

## TODO ##
* ユーザー情報から見れるユーザー単体のTLからRTを排除できるようなオプションの搭載
* ListTLを複数合成して一つのTLで表示する機能を付けたい（できれば）
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
* ふぁぼ+RTを同時にする機能の追加
* メニュー部分にリミット情報の表示始めました
* レシーバーがエラー吐いてるのをどうにかした
* 画像の読み込み時に進捗を表示するようにした
* 画像の選択時に権限を要求するようにした
* @ID 以外のリンクをタップで各動作をするようになった
