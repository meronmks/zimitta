## れどみと言うよりただのメモ ##

### 2015年8月10日更新 ###
このソースは現在GooglePlayで公開中のtwitterクライアントのソースになります。

## 使用ライブラリ ##
android-support-v4  
android-support-v7-appcompat  
[Android Smart Image View](http://loopj.com/android-smart-image-view/ "Android Smart Image View")  
[jsoup](http://jsoup.org/ "jsoup")  
[twitter4j](http://twitter4j.org/ja/ "twitter4j")  
Glide  
[AsyncHttpClient](http://loopj.com/android-async-http/ "AsyncHttpClient")  
[MaterialLoadingProgressBar](https://github.com/lsjwzh/MaterialLoadingProgressBar)  

## 直さないといけないバグ ##
* 実機でもOutOfMemoryで落ちることがある
* レシーバー結果が何度も繰り返される（吸収すればいいか？）

## ToDo ##
* ユーザー情報から見れるユーザー単体のTLからRTを排除できるようなオプションの搭載
* ListTLを複数合成して一つのTLで表示する機能を付けたい（できれば）
* ヘッダー部分にAPI残量とリセット時間の表記をする（工夫しないときつそう）
* 画像表示後に戻るキーで画像一覧にもどす
* ツイート内のリンクへの手間軽減（タップ数の軽減？）
* フォロー中の人の表示名を自由に変えれるようにする
* 起動時にTLを一番上へ
* ツイートボタンを設定から選択制へ

## Ver3.2.0との変更点（メモしないと忘れそう） ##
* SDK更新によって消えたクラスの影響でMaterialDesignLibraryが使用不能に
* MaterialLoadingProgressBar使用にあたりAPI12以下のサポートを切ることに