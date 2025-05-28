#!/bin/bash

echo "正在編譯 ComputerPet 專案..."

# 編譯所有 Java 檔案
javac *.java

# 檢查編譯結果
if [ $? -eq 0 ]; then
    echo "✅ 編譯成功！"
    echo ""
    echo "你可以使用以下命令執行程式："
    echo "  java DesktopPet     # 啟動桌面寵物"
    echo "  java PomodoroApp    # 直接啟動番茄鐘"
    echo ""
    echo "生成的檔案："
    ls -la *.class
else
    echo "❌ 編譯失敗，請檢查錯誤訊息"
fi 