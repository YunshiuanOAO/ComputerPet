package pet;

// 新增：程式設定視窗類別

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Random;

import javax.swing.*;

import pomodoro.CountdownTimer;
import pomodoro.PomodoroApp;
import pomodoro.Stopwatch;
import utils.PathTool;


public class SettingsWindow {
    /**
     *
     */
    private final DesktopPet desktopPet;
    private JFrame settingsFrame;
    public static int stoneX = -1; // 記錄石頭圖片的 X 位置
    public static int stoneY = -1; // 記錄石頭圖片的 Y 位置
    public static JFrame currentStoneFrame = null; // 記錄當前石頭視窗
    
    // 新增：全域設定變數
    public static int globalMoveSpeed = 5; // 全域移動速度
    public static int globalPetSize = 200; // 全域寵物大小
    public static int globalStoneSize = 300; // 石頭大小
    public static int globalOpacity = 100; // 全域透明度
    public static boolean globalSoundEnabled = true; // 全域音效設定
    public static boolean globalNotificationEnabled = true; // 全域通知設定
    public static boolean globalRememberPosition = true; // 全域記憶位置設定
    public static JFrame currentStoneMenuFunctionWindow = null; // 新增：追蹤石頭選單開啟的功能視窗
    
    public SettingsWindow(DesktopPet desktopPet) {
        this.desktopPet = desktopPet;
        createSettingsWindow();
    }
    
    private void createSettingsWindow() {
        settingsFrame = new JFrame("程式設定");
        settingsFrame.setSize(400, 200); // 改回 400x200
        settingsFrame.setLocationRelativeTo(this.desktopPet.frame);
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 標題
        JLabel titleLabel = new JLabel("程式設定", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // 按鈕面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton showStoneButton = new JButton("呼叫石頭");
        JButton buttonB = new JButton("寵物設定");
        // 移除系統設定按鈕
        // JButton buttonC = new JButton("系統設定");
        
        // 設定按鈕大小
        Dimension buttonSize = new Dimension(120, 40); // 原本是 80, 30
        showStoneButton.setPreferredSize(buttonSize);
        buttonB.setPreferredSize(buttonSize);
        // 移除系統設定按鈕大小設定
        // buttonC.setPreferredSize(buttonSize);
        
        // 按鈕事件
        showStoneButton.addActionListener(e -> showHomeImage());
        buttonB.addActionListener(e -> showPetSettings());
        // 移除系統設定按鈕事件
        // buttonC.addActionListener(e -> showSystemSettings());
        
        buttonPanel.add(showStoneButton);
        buttonPanel.add(buttonB);
        // 移除系統設定按鈕加入面板
        // buttonPanel.add(buttonC);
        
        // 關閉按鈕
        JPanel closePanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("關閉");
        closeButton.addActionListener(e -> settingsFrame.dispose());
        closePanel.add(closeButton);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(closePanel, BorderLayout.SOUTH);
        
        settingsFrame.add(mainPanel);
    }
    
    // 新增：寵物設定視窗
    private void showPetSettings() {
        JFrame petSettingsFrame = new JFrame("寵物設定");
        petSettingsFrame.setSize(800, 600); // 改為 800x600
        petSettingsFrame.setLocationRelativeTo(settingsFrame);
        petSettingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 標題
        JLabel titleLabel = new JLabel("寵物設定", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // 設定面板
        JPanel settingsPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 改回 3 行
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 移動速度設定
        JLabel speedLabel = new JLabel("移動速度:");
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, globalMoveSpeed);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        
        // 寵物大小設定
        JLabel sizeLabel = new JLabel("寵物大小:");
        JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 100, 300, globalPetSize);
        sizeSlider.setMajorTickSpacing(50);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        
        // 透明度設定
        JLabel opacityLabel = new JLabel("透明度:");
        JSlider opacitySlider = new JSlider(JSlider.HORIZONTAL, 50, 100, globalOpacity);
        opacitySlider.setMajorTickSpacing(10);
        opacitySlider.setPaintTicks(true);
        opacitySlider.setPaintLabels(true);
        
        settingsPanel.add(speedLabel);
        settingsPanel.add(speedSlider);
        settingsPanel.add(sizeLabel);
        settingsPanel.add(sizeSlider);
        settingsPanel.add(opacityLabel);
        settingsPanel.add(opacitySlider);
        
        // 按鈕面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton applyButton = new JButton("套用設定");
        JButton resetButton = new JButton("重置預設");
        JButton closeButton = new JButton("關閉");
        
        applyButton.addActionListener(e -> {
            // 套用設定的邏輯
            globalMoveSpeed = speedSlider.getValue();
            globalPetSize = sizeSlider.getValue();
            globalOpacity = opacitySlider.getValue();
            
            // 自動調整石頭大小為寵物大小的1.5倍
            globalStoneSize = (int)(globalPetSize * 1.5);
            
            // 套用設定到所有現有的寵物
            applySettingsToAllPets();
            
            JOptionPane.showMessageDialog(petSettingsFrame, 
                "設定已套用！\n移動速度: " + speedSlider.getValue() + "\n寵物大小: " + sizeSlider.getValue() + "\n石頭大小: " + globalStoneSize + "\n透明度: " + opacitySlider.getValue() + "%", 
                "設定套用", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        resetButton.addActionListener(e -> {
            speedSlider.setValue(5);
            sizeSlider.setValue(200);
            opacitySlider.setValue(100);
            
            // 重置全域設定
            globalMoveSpeed = 5;
            globalPetSize = 200;
            globalOpacity = 100;
            globalStoneSize = 300; // 200 * 1.5 = 300
            
            // 套用重置的設定到所有寵物
            applySettingsToAllPets();
        });
        
        closeButton.addActionListener(e -> petSettingsFrame.dispose());
        
        buttonPanel.add(applyButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(settingsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        petSettingsFrame.add(mainPanel);
        petSettingsFrame.setVisible(true);
    }
    
    // 新增：套用設定到所有寵物
    private void applySettingsToAllPets() {
        for (PetWindow petWindow : this.desktopPet.petWindows) {
            petWindow.applyGlobalSettings();
        }
        
        // 更新石頭大小
        if (currentStoneFrame != null && currentStoneFrame.isVisible()) {
            updateStoneSize();
        }
        
        System.out.println("已套用全域設定到所有寵物和石頭");
    }
    
    // 新增：更新石頭大小的方法
    private void updateStoneSize() {
        if (currentStoneFrame != null) {
            try {
                String imagePath = PathTool.patchPicturePath("picture/home.png");
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(imagePath);
                    Image img = icon.getImage();
                    JPanel panel = (JPanel) currentStoneFrame.getContentPane();
                    if (panel.getComponentCount() > 0) {
                        Component component = panel.getComponent(0);
                        if (component instanceof ScaledImageLabel) {
                            ScaledImageLabel imageLabel = (ScaledImageLabel) component;
                            imageLabel.setPreferredSize(new Dimension(SettingsWindow.globalStoneSize, SettingsWindow.globalStoneSize));
                            imageLabel.setImage(img);
                            imageLabel.revalidate();
                            imageLabel.repaint();
                        }
                    }
                    currentStoneFrame.setSize(SettingsWindow.globalStoneSize, SettingsWindow.globalStoneSize);
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    if (stoneX + SettingsWindow.globalStoneSize > screenSize.width) {
                        stoneX = screenSize.width - SettingsWindow.globalStoneSize;
                    }
                    if (stoneY + SettingsWindow.globalStoneSize > screenSize.height) {
                        stoneY = screenSize.height - SettingsWindow.globalStoneSize;
                    }
                    currentStoneFrame.setLocation(stoneX, stoneY);
                    currentStoneFrame.revalidate();
                    currentStoneFrame.repaint();
                    System.out.println("石頭大小已更新為: " + SettingsWindow.globalStoneSize + "x" + SettingsWindow.globalStoneSize);
                } else {
                    System.out.println("找不到石頭圖片檔案");
                }
            } catch (Exception e) {
                System.out.println("更新石頭大小失敗: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("石頭視窗不存在，無法更新大小");
        }
    }
    
    // 新增：系統設定視窗
    private void showSystemSettings() {
        JFrame systemSettingsFrame = new JFrame("系統設定");
        systemSettingsFrame.setSize(400, 350);
        systemSettingsFrame.setLocationRelativeTo(settingsFrame);
        systemSettingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 標題
        JLabel titleLabel = new JLabel("系統設定", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // 設定面板
        JPanel settingsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 開機自動啟動
        JLabel autoStartLabel = new JLabel("開機自動啟動:");
        JCheckBox autoStartCheckBox = new JCheckBox("啟用");
        
        // 開機自動顯示寵物
        JLabel autoShowLabel = new JLabel("開機自動顯示寵物:");
        JCheckBox autoShowCheckBox = new JCheckBox("啟用");
        
        // 音效設定
        JLabel soundLabel = new JLabel("音效:");
        JCheckBox soundCheckBox = new JCheckBox("啟用");
        soundCheckBox.setSelected(globalSoundEnabled);
        
        // 通知設定
        JLabel notificationLabel = new JLabel("桌面通知:");
        JCheckBox notificationCheckBox = new JCheckBox("啟用");
        notificationCheckBox.setSelected(globalNotificationEnabled);
        
        // 記憶位置
        JLabel rememberPosLabel = new JLabel("記憶寵物位置:");
        JCheckBox rememberPosCheckBox = new JCheckBox("啟用");
        rememberPosCheckBox.setSelected(globalRememberPosition);
        
        // 主題設定
        JLabel themeLabel = new JLabel("主題:");
        String[] themes = {"預設", "深色", "淺色", "自訂"};
        JComboBox<String> themeComboBox = new JComboBox<>(themes);
        
        settingsPanel.add(autoStartLabel);
        settingsPanel.add(autoStartCheckBox);
        settingsPanel.add(autoShowLabel);
        settingsPanel.add(autoShowCheckBox);
        settingsPanel.add(soundLabel);
        settingsPanel.add(soundCheckBox);
        settingsPanel.add(notificationLabel);
        settingsPanel.add(notificationCheckBox);
        settingsPanel.add(rememberPosLabel);
        settingsPanel.add(rememberPosCheckBox);
        settingsPanel.add(themeLabel);
        settingsPanel.add(themeComboBox);
        
        // 按鈕面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton applyButton = new JButton("套用設定");
        JButton resetButton = new JButton("重置預設");
        JButton exportButton = new JButton("匯出設定");
        JButton importButton = new JButton("匯入設定");
        JButton closeButton = new JButton("關閉");
        
        applyButton.addActionListener(e -> {
            // 套用系統設定
            globalSoundEnabled = soundCheckBox.isSelected();
            globalNotificationEnabled = notificationCheckBox.isSelected();
            globalRememberPosition = rememberPosCheckBox.isSelected();
            
            StringBuilder settings = new StringBuilder();
            settings.append("開機自動啟動: ").append(autoStartCheckBox.isSelected() ? "啟用" : "停用").append("\n");
            settings.append("開機自動顯示寵物: ").append(autoShowCheckBox.isSelected() ? "啟用" : "停用").append("\n");
            settings.append("音效: ").append(soundCheckBox.isSelected() ? "啟用" : "停用").append("\n");
            settings.append("桌面通知: ").append(notificationCheckBox.isSelected() ? "啟用" : "停用").append("\n");
            settings.append("記憶寵物位置: ").append(rememberPosCheckBox.isSelected() ? "啟用" : "停用").append("\n");
            settings.append("主題: ").append(themeComboBox.getSelectedItem());
            
            JOptionPane.showMessageDialog(systemSettingsFrame, 
                "系統設定已套用！\n\n" + settings.toString(), 
                "設定套用", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        resetButton.addActionListener(e -> {
            autoStartCheckBox.setSelected(false);
            autoShowCheckBox.setSelected(false);
            soundCheckBox.setSelected(true);
            notificationCheckBox.setSelected(true);
            rememberPosCheckBox.setSelected(true);
            themeComboBox.setSelectedIndex(0);
            
            // 重置全域設定
            globalSoundEnabled = true;
            globalNotificationEnabled = true;
            globalRememberPosition = true;
        });
        
        exportButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(systemSettingsFrame, 
                "設定匯出功能\n\n此功能將把您的設定匯出為設定檔，\n方便在其他電腦上使用相同的設定。", 
                "匯出設定", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        importButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(systemSettingsFrame, 
                "設定匯入功能\n\n此功能將從設定檔匯入設定，\n快速套用預先儲存的設定。", 
                "匯入設定", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        closeButton.addActionListener(e -> systemSettingsFrame.dispose());
        
        buttonPanel.add(applyButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(settingsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        systemSettingsFrame.add(mainPanel);
        systemSettingsFrame.setVisible(true);
    }
    
    private void handleSettingAction(String option) {
        System.out.println("使用者選擇了: " + option);
        
        // 其他選項保持原有功能
        JOptionPane.showMessageDialog(settingsFrame, 
            "您選擇了 " + option + "\n此功能尚未實現", 
            "設定選項", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // 新增：顯示 home.jpg 圖片的方法
    private void showHomeImage() {
        try {
            if (currentStoneFrame != null && currentStoneFrame.isVisible()) {
                currentStoneFrame.dispose();
                currentStoneFrame = null;
            }
            String imagePath = PathTool.patchPicturePath("picture/home.png");
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                JFrame imageFrame = new JFrame("Home Image");
                imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                currentStoneFrame = imageFrame;
                imageFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        currentStoneFrame = null;
                    }
                });
                imageFrame.setUndecorated(true);
                imageFrame.setBackground(new Color(0, 0, 0, 0));
                imageFrame.setAlwaysOnTop(true);
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage();
                ScaledImageLabel imageLabel = new ScaledImageLabel(img);
                imageLabel.setPreferredSize(new Dimension(SettingsWindow.globalStoneSize, SettingsWindow.globalStoneSize));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                JPanel panel = new JPanel();
                panel.setOpaque(false);
                panel.setLayout(new BorderLayout());
                panel.add(imageLabel, BorderLayout.CENTER);
                // 創建右鍵選單
                JPopupMenu homePopupMenu = new JPopupMenu();
                
                // 統一動作子選單
                JMenu actionMenu = new JMenu("統一動作");
                
                JMenuItem allSit = new JMenuItem("全部坐下");
                JMenuItem allLie = new JMenuItem("全部躺下");
                // JMenuItem allGetUp = new JMenuItem("全部起身"); // 刪除
                JMenuItem allCheer = new JMenuItem("全部歡呼");
                JMenuItem allCheerUp = new JMenuItem("全部加油");
                JMenuItem allStop = new JMenuItem("全部停止");
                JMenuItem allWalk = new JMenuItem("全部走路");
                JMenuItem allGoHome = new JMenuItem("全部回家");
                
                // 添加動作事件
                allSit.addActionListener(e -> executeActionOnAllPets("sit"));
                allLie.addActionListener(e -> executeActionOnAllPets("lie"));
                // allGetUp.addActionListener(e -> executeActionOnAllPets("getup")); // 已刪除
                allCheer.addActionListener(e -> executeActionOnAllPets("cheer"));
                allCheerUp.addActionListener(e -> executeActionOnAllPets("cheerup"));
                allStop.addActionListener(e -> executeActionOnAllPets("stop"));
                allWalk.addActionListener(e -> executeActionOnAllPets("walk"));
                allGoHome.addActionListener(e -> executeActionOnAllPets("home"));
                
                // 組裝選單
                actionMenu.add(allSit);
                actionMenu.add(allLie);
                // actionMenu.add(allGetUp); // 已刪除
                actionMenu.add(allCheer);
                actionMenu.add(allCheerUp);
                actionMenu.add(allStop);
                actionMenu.add(allWalk);
                actionMenu.add(allGoHome);
                
                // 新增功能選項（直接放在主選單中）
                JMenuItem tomatoTimer = new JMenuItem("番茄鐘");
                JMenuItem todoList = new JMenuItem("代辦事項");
                JMenuItem countdownTimer = new JMenuItem("倒數計時");
                JMenuItem stopwatch = new JMenuItem("碼表計時");
                JMenuItem screenTimeReminder = new JMenuItem("螢幕使用時間提醒");
                JMenuItem closeHome = new JMenuItem("關閉石頭");
                tomatoTimer.addActionListener(e -> showTomatoTimer());
                todoList.addActionListener(e -> showTodoList());
                countdownTimer.addActionListener(e -> showCountdownTimer());
                stopwatch.addActionListener(e -> showStopwatch());
                screenTimeReminder.addActionListener(e -> showScreenTimeReminder());
                closeHome.addActionListener(e -> imageFrame.dispose());
                
                // 將統一動作選單和五個功能直接添加到主選單中
                homePopupMenu.add(actionMenu);
                homePopupMenu.addSeparator();
                homePopupMenu.add(tomatoTimer);
                homePopupMenu.add(todoList);
                homePopupMenu.add(countdownTimer);
                homePopupMenu.add(stopwatch);
                homePopupMenu.add(screenTimeReminder);
                homePopupMenu.addSeparator();
                homePopupMenu.add(closeHome);
                
                // 添加拖動功能
                final Point[] mouseDownCompCoords = {null};
                
                panel.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            // 右鍵顯示選單
                            homePopupMenu.show(panel, e.getX(), e.getY());
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            // 左鍵拖動
                            mouseDownCompCoords[0] = e.getPoint();
                        }
                    }
                    
                    public void mouseReleased(MouseEvent e) {
                        mouseDownCompCoords[0] = null;
                    }
                });
                
                panel.addMouseMotionListener(new MouseMotionAdapter() {
                    public void mouseDragged(MouseEvent e) {
                        if (mouseDownCompCoords[0] != null) {
                            Point currCoords = e.getLocationOnScreen();
                            int newX = currCoords.x - mouseDownCompCoords[0].x;
                            int newY = currCoords.y - mouseDownCompCoords[0].y;
                            imageFrame.setLocation(newX, newY);
                            
                            // 更新石頭圖片的位置記錄
                            stoneX = newX;
                            stoneY = newY;
                        }
                    }
                });
                
                imageFrame.add(panel);
                // 移除 pack() 調用，改為手動設定視窗大小
                imageFrame.setSize(SettingsWindow.globalStoneSize, SettingsWindow.globalStoneSize);
                
                // 設定在右下角顯示
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int rightX = screenSize.width - SettingsWindow.globalStoneSize - 0; // 螢幕寬度 - 圖片寬度 - 邊距
                int bottomY = screenSize.height - SettingsWindow.globalStoneSize - 0; // 螢幕高度 - 圖片高度 - 邊距
                imageFrame.setLocation(rightX, bottomY);
                
                // 記錄石頭圖片的初始位置
                stoneX = rightX;
                stoneY = bottomY;
                
                imageFrame.setVisible(true);
                
            } else {
                JOptionPane.showMessageDialog(settingsFrame, 
                    "找不到 home.png 圖片檔案", 
                    "錯誤", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(settingsFrame, 
                "載入圖片時發生錯誤: " + e.getMessage(), 
                "錯誤", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 新增：對所有寵物執行統一動作
    private void executeActionOnAllPets(String action) {
        for (PetWindow petWindow : this.desktopPet.petWindows) {
            switch (action) {
                case "sit":
                    petWindow.sit();
                    break;
                case "lie":
                    petWindow.lie();
                    break;
                case "getup":
                    petWindow.getUp();
                    break;
                case "cheer":
                    petWindow.cheer();
                    break;
                case "cheerup":
                    petWindow.cheerUp();
                    break;
                case "stop":
                    petWindow.stopAllActions();
                    break;
                case "walk":
                    petWindow.startWalking();
                    break;
                case "home":
                    petWindow.goHome();
                    break;
            }
        }
        System.out.println("對所有寵物執行動作: " + action);
    }
    
    // 新增：隱藏所有寵物
    private void hideAllPets() {
        for (PetWindow petWindow : this.desktopPet.petWindows) {
            petWindow.hide();
        }
        System.out.println("隱藏所有寵物");
    }
    
    // 新增：顯示所有寵物
    private void showAllPets() {
        for (PetWindow petWindow : this.desktopPet.petWindows) {
            petWindow.show();
        }
        System.out.println("顯示所有寵物");
    }
    
    // 新增：對所有寵物執行隨機動作
    private void randomActionOnAllPets() {
        String[] actions = {"sit", "lie", "getup", "cheer", "cheerup", "walk"};
        Random random = new Random();
        
        for (PetWindow petWindow : this.desktopPet.petWindows) {
            String randomAction = actions[random.nextInt(actions.length)];
            executeActionOnAllPets(randomAction);
        }
        System.out.println("對所有寵物執行隨機動作");
    }
    
    // 新增：番茄鐘功能
    private void showTomatoTimer() {
        openStoneMenuFunctionWindow(() -> {
            PomodoroApp pomodoroApp = new PomodoroApp();
            return pomodoroApp;
        });
    }
    
    // 新增：代辦事項功能
    private void showTodoList() {
        JOptionPane.showMessageDialog(settingsFrame, 
            "代辦事項功能\n\n管理您的任務清單，\n追蹤待完成的工作項目。\n\n功能即將推出！", 
            "代辦事項", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // 新增：倒數計時功能
    private void showCountdownTimer() {
        openStoneMenuFunctionWindow(() -> {
            CountdownTimer countdownTimer = new CountdownTimer();
            return countdownTimer;
        });
    }
    
    // 新增：碼表計時功能
    private void showStopwatch() {
        openStoneMenuFunctionWindow(() -> {
            Stopwatch stopwatch = new Stopwatch();
            return stopwatch;
        });
    }
    
    // 新增：螢幕使用時間提醒功能
    private void showScreenTimeReminder() {
        if (this.desktopPet.screenUsedAlert != null) {
            String currentTime = this.desktopPet.screenUsedAlert.getFormattedUsageTime();
            boolean isMonitoring = this.desktopPet.screenUsedAlert.isMonitoring();
            
            String message = "螢幕使用時間監控\n\n" +
                           "目前使用時間：" + currentTime + "\n" +
                           "監控狀態：" + (isMonitoring ? "運行中" : "已停止") + "\n" +
                           "提醒設定：使用1小時後提醒休息\n\n" +
                           "點擊「重置」可重新開始計時";
            
            int option = JOptionPane.showOptionDialog(
                settingsFrame,
                message,
                "螢幕使用時間提醒",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"重置計時", "關閉"},
                "關閉"
            );
            
            if (option == 0) { // 重置計時
                this.desktopPet.screenUsedAlert.resetTimer();
                this.desktopPet.screenUsedAlert.startMonitoring();
                JOptionPane.showMessageDialog(settingsFrame, 
                    "螢幕使用時間計時器已重置並重新開始監控！", 
                    "重置成功", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(settingsFrame, 
                "螢幕使用時間監控尚未初始化", 
                "錯誤", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void show() {
        settingsFrame.setVisible(true);
    }
    
    // 新增：石頭選單開啟功能視窗的統一方法
    private void openStoneMenuFunctionWindow(java.util.function.Supplier<JFrame> windowSupplier) {
        // 關閉現有的功能視窗
        if (currentStoneMenuFunctionWindow != null && currentStoneMenuFunctionWindow.isDisplayable()) {
            currentStoneMenuFunctionWindow.dispose();
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                // 創建新視窗
                currentStoneMenuFunctionWindow = windowSupplier.get();
                
                // 記錄上次視窗位置，避免不必要的更新和閃爍
                final Point[] lastWindowLocation = {null};
                
                // 更新視窗位置的方法
                Runnable updateWindowPosition = () -> {
                    if (currentStoneMenuFunctionWindow != null && currentStoneMenuFunctionWindow.isVisible() && 
                        currentStoneFrame != null && currentStoneFrame.isVisible()) {
                        
                        // 獲取石頭當前位置
                        Point stoneLocation = currentStoneFrame.getLocation();
                        
                        // 計算視窗應該放置的位置（始終在石頭正上方）
                        int idealWindowX = stoneLocation.x + (globalStoneSize - currentStoneMenuFunctionWindow.getWidth()) / 2; // 水平置中
                        int idealWindowY = stoneLocation.y - currentStoneMenuFunctionWindow.getHeight() - 10; // 在石頭上方10像素
                        
                        // 確保視窗不會超出螢幕邊界
                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        int newWindowX = idealWindowX;
                        int newWindowY = idealWindowY;
                        
                        // 垂直邊界檢查
                        if (newWindowY < 0) {
                            newWindowY = stoneLocation.y + globalStoneSize + 10; // 如果上方放不下，就放下方
                        }
                        
                        // 水平邊界檢查
                        if (newWindowX + currentStoneMenuFunctionWindow.getWidth() > screenSize.width) {
                            newWindowX = screenSize.width - currentStoneMenuFunctionWindow.getWidth();
                        }
                        if (newWindowX < 0) {
                            newWindowX = 0;
                        }
                        
                        // 只有當位置變化超過閾值時才更新，避免微小變化導致閃爍
                        Point newLocation = new Point(newWindowX, newWindowY);
                        boolean shouldUpdate = false;
                        
                        if (lastWindowLocation[0] == null) {
                            shouldUpdate = true;
                        } else {
                            // 計算位置變化的距離
                            int deltaX = Math.abs(lastWindowLocation[0].x - newLocation.x);
                            int deltaY = Math.abs(lastWindowLocation[0].y - newLocation.y);
                            
                            // 只有當變化超過3像素時才更新（防止微小閃爍）
                            if (deltaX > 3 || deltaY > 3) {
                                shouldUpdate = true;
                            }
                        }
                        
                        if (shouldUpdate) {
                            currentStoneMenuFunctionWindow.setLocation(newWindowX, newWindowY);
                            lastWindowLocation[0] = newLocation;
                        }
                    }
                };
                
                // 設定初始位置
                updateWindowPosition.run();
                currentStoneMenuFunctionWindow.setVisible(true);
                
                // 啟動位置跟隨計時器，讓功能視窗跟隨石頭移動
                Timer stoneFollowTimer = new Timer(100, e -> updateWindowPosition.run());
                stoneFollowTimer.start();
                
                // 添加視窗關閉監聽器，清理引用和停止計時器
                currentStoneMenuFunctionWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        if (currentStoneMenuFunctionWindow == e.getWindow()) {
                            currentStoneMenuFunctionWindow = null;
                            stoneFollowTimer.stop(); // 停止跟隨計時器
                        }
                    }
                });
                
            } catch (Exception e) {
                System.err.println("從石頭選單開啟功能視窗失敗: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}