#!/bin/bash

clear
echo "🍅 番茄鐘跟隨功能測試"
echo "======================"
echo ""
echo "📋 測試目標："
echo "• 驗證番茄鐘跟隨特定角色移動"
echo "• 驗證移動中右鍵選單功能"
echo "• 驗證切換跟隨目標功能"
echo ""
echo "📖 測試指南："
echo "1. 右鍵主屋 → 設定角色數量 → 3"
echo "2. 右鍵狗1 → 亂走 → 右鍵狗1 → 番茄鐘設定"
echo "3. 觀察番茄鐘是否跟隨狗1移動"
echo "4. 關閉番茄鐘 → 右鍵狗2 → 閃現 → 右鍵狗2 → 番茄鐘設定"
echo "5. 觀察番茄鐘是否跟隨狗2閃現"
echo "6. 關閉番茄鐘 → 右鍵主屋 → 番茄鐘設定 → 拖拽主屋"
echo "7. 觀察番茄鐘是否跟隨主屋移動"
echo ""
echo "📝 詳細測試步驟請參考: SIMPLE_TEST_GUIDE.md"
echo ""
read -p "按 Enter 鍵開始測試..."

echo "🚀 正在啟動桌面寵物..."
java DesktopPet 