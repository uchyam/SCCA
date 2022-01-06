#!/bin/bash
#使用方法 : sh check.sh ディレクトリ名
#sh check.sh ../../../testcode
#拡張子の削除 https://qiita.com/koara-local/items/04d3efd1031ea62d8db5

#関数の利用 http://shellscript.sunone.me/function.html
AnayazeComments(){
    echo $1
    java -jar -Xms512m -Xmx512m SCCA.jar necessary -o $1
    echo ''
}

#cppファイルに対して実行
for file in $1/*.cpp; do
    AnayazeComments $file 
done

#hファイル名に対して実行
for file in $1/*.h; do
    AnayazeComments $file
done
