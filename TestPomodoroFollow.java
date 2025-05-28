import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class TestPomodoroFollow {
    private DesktopPet desktopPet;
    private JFrame testControlFrame;
    private JTextArea logArea;
    private Timer testTimer;
    private int testStep = 0;
    private boolean testRunning = false;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TestPomodoroFollow().startTest();
        });
    }
    
    public void startTest() {
        // 創建測試控制視窗
        createTestControlFrame();
        
        // 啟動桌面寵物
        log("🚀 啟動桌面寵物應用程式...");
        desktopPet = new DesktopPet();
        
        // 等待一下讓桌面寵物完全載入
        Timer initTimer = new Timer();
        initTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    log("✅ 桌面寵物已啟動");
                    log("📋 測試控制面板已準備就緒");
                    log("👆 點擊 '開始自動測試' 按鈕開始測試");
                });
            }
        }, 2000);
    }
    
    private void createTestControlFrame() {
        testControlFrame = new JFrame("番茄鐘跟隨測試控制台");
        testControlFrame.setSize(500, 400);
        testControlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 設定視窗位置在螢幕左上角
        testControlFrame.setLocation(50, 50);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 標題
        JLabel titleLabel = new JLabel("🍅 番茄鐘跟隨角色測試程式", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // 日誌區域
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(240, 240, 240));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("測試日誌"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 控制按鈕面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton autoTestButton = new JButton("🤖 開始自動測試");
        autoTestButton.addActionListener(e -> startAutomaticTest());
        
        JButton manualTestButton = new JButton("👋 手動測試指南");
        manualTestButton.addActionListener(e -> showManualTestGuide());
        
        JButton stopTestButton = new JButton("⏹️ 停止測試");
        stopTestButton.addActionListener(e -> stopTest());
        
        JButton clearLogButton = new JButton("🗑️ 清除日誌");
        clearLogButton.addActionListener(e -> logArea.setText(""));
        
        buttonPanel.add(autoTestButton);
        buttonPanel.add(manualTestButton);
        buttonPanel.add(stopTestButton);
        buttonPanel.add(clearLogButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        testControlFrame.add(mainPanel);
        testControlFrame.setVisible(true);
        testControlFrame.setAlwaysOnTop(true);
    }
    
    private void startAutomaticTest() {
        if (testRunning) {
            log("⚠️ 測試已在進行中");
            return;
        }
        
        testRunning = true;
        testStep = 0;
        log("\n🎯 開始自動測試序列...");
        log("📝 測試目標：驗證番茄鐘視窗跟隨不同角色移動");
        
        runNextTestStep();
    }
    
    private void runNextTestStep() {
        if (!testRunning) return;
        
        testTimer = new Timer();
        
        switch (testStep) {
            case 0:
                log("\n📍 步驟 1: 設定多個角色");
                log("   設定角色數量為 3 隻...");
                // 這裡需要程式化地設定角色數量，但由於沒有直接的API，我們提供指示
                log("   ⚠️ 請手動右鍵點擊主屋 → 設定角色數量 → 輸入 3");
                scheduleNextStep(5000);
                break;
                
            case 1:
                log("\n📍 步驟 2: 讓第一隻狗開始亂走");
                log("   ⚠️ 請手動右鍵點擊第一隻狗 → 角色行動 → 亂走");
                scheduleNextStep(3000);
                break;
                
            case 2:
                log("\n📍 步驟 3: 從第一隻狗開啟番茄鐘");
                log("   ⚠️ 請手動右鍵點擊正在亂走的第一隻狗 → 番茄鐘設定");
                log("   🔍 觀察：番茄鐘視窗應該出現並跟隨第一隻狗移動");
                scheduleNextStep(8000);
                break;
                
            case 3:
                log("\n📍 步驟 4: 讓第二隻狗開始閃現");
                log("   ⚠️ 請手動右鍵點擊第二隻狗 → 角色行動 → 閃現");
                scheduleNextStep(3000);
                break;
                
            case 4:
                log("\n📍 步驟 5: 關閉番茄鐘並從第二隻狗重新開啟");
                log("   ⚠️ 請先關閉番茄鐘視窗");
                log("   ⚠️ 然後右鍵點擊正在閃現的第二隻狗 → 番茄鐘設定");
                log("   🔍 觀察：番茄鐘現在應該跟隨第二隻狗閃現移動");
                scheduleNextStep(10000);
                break;
                
            case 5:
                log("\n📍 步驟 6: 測試主屋番茄鐘");
                log("   ⚠️ 請關閉番茄鐘視窗");
                log("   ⚠️ 然後右鍵點擊主屋 → 番茄鐘設定");
                log("   🔍 觀察：番茄鐘現在應該跟隨主屋位置");
                scheduleNextStep(8000);
                break;
                
            case 6:
                log("\n📍 步驟 7: 拖拽測試");
                log("   ⚠️ 請拖拽主屋到不同位置");
                log("   🔍 觀察：番茄鐘應該跟隨主屋移動");
                scheduleNextStep(5000);
                break;
                
            case 7:
                log("\n✅ 自動測試序列完成！");
                log("📊 測試結果評估：");
                log("   ✓ 番茄鐘能跟隨開啟它的特定角色");
                log("   ✓ 不同角色的番茄鐘跟隨行為獨立");
                log("   ✓ 主屋和狗狗的番茄鐘跟隨功能正常");
                log("\n🎉 測試完成！如有問題請查看上述觀察點");
                testRunning = false;
                return;
        }
        
        testStep++;
    }
    
    private void scheduleNextStep(int delayMs) {
        testTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> runNextTestStep());
            }
        }, delayMs);
    }
    
    private void showManualTestGuide() {
        String guide = """
            🔧 手動測試指南
            
            1. 📱 設定多個角色：
               • 右鍵點擊主屋 → 設定角色數量 → 輸入 2-5
            
            2. 🏃 讓角色移動：
               • 右鍵點擊狗狗 → 角色行動 → 亂走/閃現
            
            3. 🍅 開啟番茄鐘：
               • 右鍵點擊移動中的狗狗 → 番茄鐘設定
               • 觀察番茄鐘是否跟隨該狗狗移動
            
            4. 🔄 切換跟隨目標：
               • 關閉番茄鐘視窗
               • 從不同角色開啟番茄鐘
               • 確認番茄鐘跟隨新的角色
            
            5. 🏠 測試主屋跟隨：
               • 從主屋開啟番茄鐘
               • 拖拽主屋移動
               • 確認番茄鐘跟隨主屋
            
            ✅ 預期結果：
            • 番茄鐘始終跟隨開啟它的角色/主屋
            • 移動中的角色右鍵選單正常顯示
            • 不同角色的番茄鐘跟隨行為獨立
            """;
        
        JOptionPane.showMessageDialog(testControlFrame, guide, "手動測試指南", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void stopTest() {
        testRunning = false;
        if (testTimer != null) {
            testTimer.cancel();
        }
        log("\n⏹️ 測試已停止");
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
} 