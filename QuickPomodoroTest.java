import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class QuickPomodoroTest {
    private JFrame testFrame;
    private JTextArea resultArea;
    private JButton startTestButton;
    private Timer testTimer;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new QuickPomodoroTest().createTestInterface();
        });
    }
    
    private void createTestInterface() {
        testFrame = new JFrame("🍅 番茄鐘跟隨測試");
        testFrame.setSize(600, 500);
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testFrame.setLocation(100, 100);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 標題和說明
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("番茄鐘跟隨角色移動測試程式", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea instructionArea = new JTextArea(
            "📋 測試說明：\n" +
            "1. 點擊 '啟動桌面寵物' 按鈕啟動主程式\n" +
            "2. 按照下方指示進行測試\n" +
            "3. 觀察番茄鐘是否正確跟隨角色移動\n" +
            "4. 在結果區域記錄測試結果"
        );
        instructionArea.setEditable(false);
        instructionArea.setBackground(new Color(245, 245, 245));
        instructionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(instructionArea, BorderLayout.CENTER);
        
        // 測試步驟面板
        JPanel stepsPanel = createTestStepsPanel();
        
        // 結果記錄區域
        resultArea = new JTextArea();
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setBorder(BorderFactory.createTitledBorder("測試結果記錄"));
        resultScrollPane.setPreferredSize(new Dimension(0, 150));
        
        // 控制按鈕
        JPanel buttonPanel = new JPanel(new FlowLayout());
        startTestButton = new JButton("🚀 啟動桌面寵物");
        startTestButton.setFont(new Font("Arial", Font.BOLD, 14));
        startTestButton.addActionListener(e -> startDesktopPet());
        
        JButton clearButton = new JButton("🗑️ 清除記錄");
        clearButton.addActionListener(e -> resultArea.setText(""));
        
        JButton helpButton = new JButton("❓ 幫助");
        helpButton.addActionListener(e -> showHelp());
        
        buttonPanel.add(startTestButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(helpButton);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(stepsPanel, BorderLayout.CENTER);
        mainPanel.add(resultScrollPane, BorderLayout.SOUTH);
        
        testFrame.add(mainPanel, BorderLayout.CENTER);
        testFrame.add(buttonPanel, BorderLayout.SOUTH);
        
        testFrame.setVisible(true);
        
        // 初始化記錄
        logResult("🔧 測試程式已啟動，等待開始測試...");
    }
    
    private JPanel createTestStepsPanel() {
        JPanel stepsPanel = new JPanel();
        stepsPanel.setLayout(new BoxLayout(stepsPanel, BoxLayout.Y_AXIS));
        stepsPanel.setBorder(BorderFactory.createTitledBorder("測試步驟"));
        
        String[] testSteps = {
            "步驟 1: 設定多個角色 (建議 3 隻)",
            "   • 右鍵點擊主屋 → 設定角色數量 → 輸入 3",
            "",
            "步驟 2: 測試第一隻狗的番茄鐘跟隨",
            "   • 右鍵點擊第一隻狗 → 角色行動 → 亂走",
            "   • 右鍵點擊移動中的狗 → 番茄鐘設定",
            "   • 觀察番茄鐘是否跟隨狗狗移動",
            "",
            "步驟 3: 測試切換跟隨目標",
            "   • 關閉番茄鐘視窗",
            "   • 右鍵點擊第二隻狗 → 角色行動 → 閃現",
            "   • 右鍵點擊閃現中的狗 → 番茄鐘設定",
            "   • 觀察番茄鐘是否跟隨新的狗狗",
            "",
            "步驟 4: 測試主屋跟隨",
            "   • 關閉番茄鐘視窗",
            "   • 右鍵點擊主屋 → 番茄鐘設定",
            "   • 拖拽主屋移動，觀察番茄鐘跟隨",
            "",
            "步驟 5: 測試移動中右鍵選單",
            "   • 確認移動中的角色能正常顯示右鍵選單",
            "   • 選單不會因移動而消失"
        };
        
        for (String step : testSteps) {
            JLabel stepLabel = new JLabel(step);
            if (step.startsWith("步驟")) {
                stepLabel.setFont(new Font("Arial", Font.BOLD, 12));
                stepLabel.setForeground(new Color(0, 100, 200));
            } else if (step.startsWith("   •")) {
                stepLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                stepLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            }
            stepLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            stepsPanel.add(stepLabel);
        }
        
        // 添加測試按鈕
        JPanel testButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton passButton = new JButton("✅ 通過");
        passButton.setBackground(new Color(200, 255, 200));
        passButton.addActionListener(e -> logResult("✅ 測試通過 - " + getCurrentTime()));
        
        JButton failButton = new JButton("❌ 失敗");
        failButton.setBackground(new Color(255, 200, 200));
        failButton.addActionListener(e -> {
            String reason = JOptionPane.showInputDialog(testFrame, "請描述失敗原因:");
            if (reason != null && !reason.trim().isEmpty()) {
                logResult("❌ 測試失敗 - " + getCurrentTime() + " - 原因: " + reason);
            }
        });
        
        JButton noteButton = new JButton("📝 記錄");
        noteButton.setBackground(new Color(255, 255, 200));
        noteButton.addActionListener(e -> {
            String note = JOptionPane.showInputDialog(testFrame, "請輸入測試記錄:");
            if (note != null && !note.trim().isEmpty()) {
                logResult("📝 " + getCurrentTime() + " - " + note);
            }
        });
        
        testButtonPanel.add(passButton);
        testButtonPanel.add(failButton);
        testButtonPanel.add(noteButton);
        
        stepsPanel.add(Box.createVerticalStrut(10));
        stepsPanel.add(testButtonPanel);
        
        return stepsPanel;
    }
    
    private void startDesktopPet() {
        startTestButton.setEnabled(false);
        startTestButton.setText("🔄 啟動中...");
        
        logResult("🚀 正在啟動桌面寵物應用程式...");
        
        // 在新線程中啟動桌面寵物
        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    DesktopPet.main(new String[0]);
                });
                
                // 等待一下讓應用程式完全載入
                Thread.sleep(2000);
                
                SwingUtilities.invokeLater(() -> {
                    logResult("✅ 桌面寵物已啟動，可以開始測試");
                    logResult("📋 請按照左側步驟進行測試，並使用下方按鈕記錄結果");
                    startTestButton.setText("✅ 已啟動");
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    logResult("❌ 啟動失敗: " + e.getMessage());
                    startTestButton.setEnabled(true);
                    startTestButton.setText("🚀 啟動桌面寵物");
                });
            }
        }).start();
    }
    
    private void showHelp() {
        String helpText = """
            🔍 測試重點說明：
            
            1. 番茄鐘跟隨功能：
               • 番茄鐘視窗應該跟隨開啟它的角色移動
               • 從不同角色開啟的番茄鐘應該跟隨不同的角色
            
            2. 移動模式測試：
               • 亂走：角色隨機移動，番茄鐘應該跟隨
               • 閃現：角色瞬移，番茄鐘應該跟隨
               • 拖拽：手動拖拽時，番茄鐘應該跟隨
            
            3. 右鍵選單測試：
               • 移動中的角色右鍵選單應該正常顯示
               • 選單不應該因為角色移動而消失
            
            4. 切換測試：
               • 關閉番茄鐘後從新角色開啟
               • 番茄鐘應該跟隨新的角色
            
            ⚠️ 注意事項：
            • 每次只能有一個番茄鐘視窗
            • 測試時請仔細觀察番茄鐘位置變化
            • 記錄任何異常行為
            """;
        
        JOptionPane.showMessageDialog(testFrame, helpText, "測試幫助", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logResult(String message) {
        SwingUtilities.invokeLater(() -> {
            resultArea.append(message + "\n");
            resultArea.setCaretPosition(resultArea.getDocument().getLength());
        });
    }
    
    private String getCurrentTime() {
        return java.time.LocalTime.now().toString().substring(0, 8);
    }
} 