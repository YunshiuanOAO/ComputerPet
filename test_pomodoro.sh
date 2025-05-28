#!/bin/bash

echo "🍅 番茄鐘跟隨測試程式"
echo "========================"
echo ""

# 檢查是否有編譯好的檔案
if [ -f "DesktopPet.class" ]; then
    echo "✅ 找到編譯好的檔案"
    echo "🚀 啟動桌面寵物進行測試..."
    echo ""
    echo "📋 測試指南："
    echo "1. 設定多個角色 (建議3隻)"
    echo "2. 讓角色開始移動 (亂走/閃現)"
    echo "3. 從移動中的角色開啟番茄鐘"
    echo "4. 觀察番茄鐘是否跟隨角色移動"
    echo "5. 測試切換不同角色的跟隨功能"
    echo ""
    echo "詳細測試步驟請參考 SIMPLE_TEST_GUIDE.md"
    echo ""
    echo "正在啟動..."
    java DesktopPet
else
    echo "❌ 找不到編譯好的檔案"
    echo "請先執行: bash compile.sh"
    echo "或直接執行: java DesktopPet"
fi 