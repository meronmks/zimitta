## じみったー(仮)の説明書っぽくもない何か ##
このソースは現在GooglePlayで公開中のtwitterクライアントのソースになります。

## 使用ライブラリ ##
* android-support-v4  
* android-support-v7-appcompat  
* [jsoup](http://jsoup.org/)
* [twitter4j](http://twitter4j.org/ja/)  
* Glide
* [AsyncHttpClient](http://loopj.com/android-async-http/)  
* [MaterialLoadingProgressBar](https://github.com/lsjwzh/MaterialLoadingProgressBar)  
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)  
* [RxJava](https://github.com/ReactiveX/RxJava)  
* [RxBinding](https://github.com/JakeWharton/RxBinding)
* gradle 2.10

## 参考にしたサイト等 ##
* [[Android] 様々なジェスチャーを処理する(1) 拡大縮小](http://chicketen.blog.jp/archives/1579621.html)
* [[Android] 様々なジェスチャーを処理する(2) 移動](http://chicketen.blog.jp/archives/1622120.html)
* [Android (Java) で InputStream / OutputStream の進捗状況の取得・表示](http://foreignkey.toyao.net/archives/1386)
* [TextViewのリンク以外をクリックした時はListViewのクリックに行く方法](http://oigami.hatenablog.com/entry/2014/11/08/082615)
* [[Android Tips] SwipeRefreshLayout で Pull to Refresh を実装する](http://dev.classmethod.jp/smartphone/swiperefreshlayout/)
* [イベントの通知にインテントのブロードキャストを使う話](http://qiita.com/kazhida/items/91a15a1cf8ec0c443dbb)
* [ListViewの挙動をいい感じにした](http://saku-na63.hatenablog.com/entry/2014/06/06/013014)

## 直さないといけないバグ ##
* 実機でもOutOfMemoryで落ちることがある
* レシーバー結果が何度も繰り返される（吸収すればいいか？）

## TODO ##
* ユーザー情報から見れるユーザー単体のTLからRTを排除できるようなオプションの搭載
* ListTLを複数合成して一つのTLで表示する機能を付けたい（できれば）
* フォロー中の人の表示名を自由に変えれるようにする
* アカウント情報のバックアップ書き出し機能を付けてみる？
* API残量表示をタップしたら何か一覧等を出すようにする

## Ver4.2.0との変更点（メモしないと忘れそう） ##
*
