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
* gradle 2.10

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
* API残量表示をタップしたら何か一覧等を出すようにする

## Ver4.1.0との変更点（メモしないと忘れそう） ##
* 認証方式を変更し、より簡単にアカウントを追加できるように
* アカウント変更画面でアカウント一覧をタップしても変更できないバグを修正
* その他一部一覧表示の画面でタップしても何もおきない放置されたバグをやっと修正
* タイムラインでアカウントのサムネイルタップでユーザ詳細を見れるように
