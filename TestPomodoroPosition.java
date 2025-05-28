import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 測試番茄鐘位置修復的程序
 * 這個程序會快速測試角色移動到螢幕各個邊緣時番茄鐘的顯示情況
 */
public class TestPomodoroPosition {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("🍅 番茄鐘位置修復測試程序");
            System.out.println("==========================");
            System.out.println("這個程序將測試以下情況：");
            System.out.println("1. 角色在螢幕左上角時番茄鐘位置");
            System.out.println("2. 角色在螢幕右上角時番茄鐘位置");
            System.out.println("3. 角色在螢幕左下角時番茄鐘位置");
            System.out.println("4. 角色在螢幕右下角時番茄鐘位置");
            System.out.println("5. 角色在螢幕中央時番茄鐘位置");
            System.out.println("");
            System.out.println("📋 測試步驟：");
            System.out.println("1. 啟動桌面寵物程序");
            System.out.println("2. 設定2-3隻角色");
            System.out.println("3. 手動拖拽角色到螢幕各個邊緣");
            System.out.println("4. 在每個位置開啟番茄鐘，觀察是否可見");
            System.out.println("5. 讓角色開始移動，觀察番茄鐘跟隨情況");
            System.out.println("");
            System.out.println("✅ 預期結果：");
            System.out.println("• 番茄鐘視窗始終保持在螢幕可見範圍內");
            System.out.println("• 當角色在邊緣時，番茄鐘會自動調整到合適位置");
            System.out.println("• 番茄鐘跟隨移動流暢，不會消失");
            System.out.println("");
            
            // 創建一個簡單的測試控制面板
            JFrame testFrame = new JFrame("番茄鐘位置測試控制台");
            testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            testFrame.setSize(400, 300);
            testFrame.setLocationRelativeTo(null);
            
            JPanel panel = new JPanel(new BorderLayout());
            
            JTextArea instructions = new JTextArea(
                "測試指南：\n\n" +
                "1. 點擊「啟動桌面寵物」按鈕\n" +
                "2. 設定多個角色（建議3隻）\n" +
                "3. 拖拽角色到螢幕四個角落\n" +
                "4. 在每個位置開啟番茄鐘\n" +
                "5. 觀察番茄鐘是否始終可見\n" +
                "6. 讓角色開始移動（亂走/閃現）\n" +
                "7. 觀察番茄鐘跟隨情況\n\n" +
                "重點測試：\n" +
                "• 角色在螢幕最上方時\n" +
                "• 角色在螢幕最左邊時\n" +
                "• 角色在螢幕最右邊時\n" +
                "• 角色在螢幕最下方時"
            );
            instructions.setEditable(false);
            instructions.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
            instructions.setBackground(new Color(248, 248, 248));
            instructions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JScrollPane scrollPane = new JScrollPane(instructions);
            panel.add(scrollPane, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            
            JButton startButton = new JButton("🚀 啟動桌面寵物");
            startButton.setFont(new Font("微軟正黑體", Font.BOLD, 14));
            startButton.setBackground(new Color(100, 200, 100));
            startButton.setForeground(Color.WHITE);
            startButton.setFocusPainted(false);
            startButton.addActionListener(e -> {
                try {
                    // 啟動桌面寵物程序
                    new ProcessBuilder("java", "DesktopPet").start();
                    JOptionPane.showMessageDialog(testFrame, 
                        "桌面寵物已啟動！\n請按照測試指南進行測試。", 
                        "啟動成功", 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(testFrame, 
                        "啟動失敗：" + ex.getMessage() + "\n請手動執行：java DesktopPet", 
                        "啟動錯誤", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            JButton closeButton = new JButton("❌ 關閉測試");
            closeButton.setFont(new Font("微軟正黑體", Font.BOLD, 14));
            closeButton.setBackground(new Color(200, 100, 100));
            closeButton.setForeground(Color.WHITE);
            closeButton.setFocusPainted(false);
            closeButton.addActionListener(e -> System.exit(0));
            
            buttonPanel.add(startButton);
            buttonPanel.add(closeButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            testFrame.add(panel);
            testFrame.setVisible(true);
            
            System.out.println("🎯 測試控制台已開啟，請按照指示進行測試");
        });
    }
} 