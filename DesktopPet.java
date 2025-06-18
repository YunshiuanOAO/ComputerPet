import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;

public class DesktopPet {
    private JFrame frame;
    private JCheckBox dogCheckBox, catCheckBox, duckCheckBox, mouseCheckBox;
    private List<PetWindow> petWindows = new ArrayList<>();
    private ScreenUsedAlert screenUsedAlert; // 新增：螢幕使用時間監控
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DesktopPet desktopPet = new DesktopPet();
            desktopPet.createAndShowGUI();
            desktopPet.initializeScreenMonitoring(); // 啟動螢幕使用時間監控
        });
    }
    
    // 新增：程式設定視窗類別
    class SettingsWindow {
        private JFrame settingsFrame;
        private static int stoneX = -1; // 記錄石頭圖片的 X 位置
        private static int stoneY = -1; // 記錄石頭圖片的 Y 位置
        private static JFrame currentStoneFrame = null; // 記錄當前石頭視窗
        
        // 新增：全域設定變數
        private static int globalMoveSpeed = 5; // 全域移動速度
        private static int globalPetSize = 200; // 全域寵物大小
        private static int globalStoneSize = 300; // 石頭大小
        private static int globalOpacity = 100; // 全域透明度
        private static boolean globalSoundEnabled = true; // 全域音效設定
        private static boolean globalNotificationEnabled = true; // 全域通知設定
        private static boolean globalRememberPosition = true; // 全域記憶位置設定
        private static JFrame currentStoneMenuFunctionWindow = null; // 新增：追蹤石頭選單開啟的功能視窗
        
        public SettingsWindow() {
            createSettingsWindow();
        }
        
        private void createSettingsWindow() {
            settingsFrame = new JFrame("程式設定");
            settingsFrame.setSize(400, 200); // 改回 400x200
            settingsFrame.setLocationRelativeTo(frame);
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
            for (PetWindow petWindow : petWindows) {
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
                    File imageFile = new File("picture/home.png");
                    if (imageFile.exists()) {
                        ImageIcon icon = new ImageIcon("picture/home.png");
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
                File imageFile = new File("picture/home.png");
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
                    ImageIcon icon = new ImageIcon("picture/home.png");
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
            for (PetWindow petWindow : petWindows) {
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
            for (PetWindow petWindow : petWindows) {
                petWindow.hide();
            }
            System.out.println("隱藏所有寵物");
        }
        
        // 新增：顯示所有寵物
        private void showAllPets() {
            for (PetWindow petWindow : petWindows) {
                petWindow.show();
            }
            System.out.println("顯示所有寵物");
        }
        
        // 新增：對所有寵物執行隨機動作
        private void randomActionOnAllPets() {
            String[] actions = {"sit", "lie", "getup", "cheer", "cheerup", "walk"};
            Random random = new Random();
            
            for (PetWindow petWindow : petWindows) {
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
            if (screenUsedAlert != null) {
                String currentTime = screenUsedAlert.getFormattedUsageTime();
                boolean isMonitoring = screenUsedAlert.isMonitoring();
                
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
                    screenUsedAlert.resetTimer();
                    screenUsedAlert.startMonitoring();
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
    
    // 寵物視窗類別
    class PetWindow {
        JFrame window;
        JLabel petLabel;
        Timer walkTimer;
        Timer directionTimer;
        Timer fallTimer;
        Timer pauseTimer;
        Timer sitTimer;
        int currentX, currentY;
        int direction = 1; // 1為右，-1為左
        boolean isWalking = false;
        boolean isFalling = false;
        boolean isPopupMenuVisible = false;
        boolean wasWalkingBeforePopup = false;
        boolean wasFallingBeforePopup = false;
        boolean isPaused = false;
        boolean isHome = false; // 新增：標記角色是否回家了
        boolean isSitting = false; // 新增：坐下狀態
        boolean isLying = false; // 新增：躺下狀態
        boolean isTeleporting = false; // 新增：閃現狀態
        boolean isCheering = false; // 新增：歡呼狀態
        boolean isCheeringUp = false; // 新增：加油狀態
        String standImagePath;
        String walkImagePath;
        String fallImagePath;
        String sitImagePath; // 新增：坐下圖片路徑
        String lieImagePath; // 新增：躺下圖片路徑
        String cheerImagePath; // 新增：歡呼圖片路徑
        String cheerUpImagePath; // 新增：加油圖片路徑
        String petType;
        Random random = new Random();
        int groundLevel;
        int moveSpeed; // 新增：個別移動速度
        JPopupMenu popupMenu;
        JFrame currentFunctionWindow; // 新增：追蹤當前開啟的功能視窗
        Timer functionWindowFollowTimer; // 新增：功能視窗跟隨計時器
        
        public PetWindow(String standPath, String walkPath, String fallPath, int x, int y, String type) {
            this.standImagePath = standPath;
            this.walkImagePath = walkPath;
            this.fallImagePath = fallPath;
            this.sitImagePath = standPath.replace("_stand.png", "_sit.png");
            this.lieImagePath = standPath.replace("_stand.png", "_lie.png");
            this.cheerImagePath = "picture/" + type + "_cheer.png";
            this.cheerUpImagePath = "picture/" + type + "_cheerup.png";
            this.currentX = x;
            this.currentY = y;
            this.petType = type;
            this.moveSpeed = setMoveSpeed(type); // 根據動物類型設定移動速度
            
            // 設定地面高度
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            this.groundLevel = screenSize.height - SettingsWindow.globalPetSize - 40;
            
            createWindow();
            createPopupMenu();
            setupWalkingAnimation();
        }
        
        // 新增：根據動物類型設定不同的移動速度
        private int setMoveSpeed(String petType) {
            switch (petType) {
                case "dog":
                    return 6; // 狗狗：中等偏快速度
                case "cat":
                    return 5; // 貓咪：中等速度
                case "duck":
                    return 4; // 鴨子：較慢速度（搖擺走路）
                case "mouse":
                    return 7; // 老鼠：最快速度（機靈敏捷）
                default:
                    return 5; // 預設速度
            }
        }
        
        private void createWindow() {
            window = new JFrame();
            window.setUndecorated(true);
            window.setSize(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize);
            window.setBackground(new Color(0, 0, 0, 0));
            window.setAlwaysOnTop(true);
            window.setLocation(currentX, currentY);
            
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new BorderLayout());
            
            petLabel = new JLabel();
            loadStandImage();
            
            panel.add(petLabel, BorderLayout.CENTER);
            window.add(panel);
            
            // 添加滑鼠事件處理
            final Point[] mouseDownCompCoords = {null};
            
            window.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        // 右鍵按下：暫停所有動作，顯示站立圖片，顯示選單
                        pauseForPopupMenu();
                        showPopupMenu(e);
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        // 左鍵拖動
                        if (!isPopupMenuVisible && !isTeleporting) { // 只有在選單不顯示且不在閃現狀態時才允許拖動
                            mouseDownCompCoords[0] = e.getPoint();
                            stopWalking();
                        }
                    }
                }
                
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && !isPopupMenuVisible && !isTeleporting) {
                        // 檢查是否在地面以上
                        if (currentY < groundLevel) {
                            startFalling();
                        } else {
                            startWalking();
                        }
                    }
                }
            });
            
            window.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (mouseDownCompCoords[0] != null && SwingUtilities.isLeftMouseButton(e) && !isPopupMenuVisible && !isTeleporting) {
                        Point currCoords = e.getLocationOnScreen();
                        int newX = currCoords.x - mouseDownCompCoords[0].x;
                        int newY = currCoords.y - mouseDownCompCoords[0].y;
                        setWindowPosition(newX, newY);
                        
                        // 如果被拖到地面以上，顯示跌落圖片
                        if (currentY < groundLevel && !isFalling) {
                            loadFallImage();
                        }
                    }
                }
            });
        }
        
        // 新增：暫停移動方法
        private void pauseMovement(int milliseconds) {
            isPaused = true;
            loadStandImage(); // 暫停時顯示站立圖片
            
            if (pauseTimer != null && pauseTimer.isRunning()) {
                pauseTimer.stop();
            }
            
            pauseTimer = new Timer(milliseconds, e -> {
                isPaused = false;
                if (isWalking && !isFalling && !isPopupMenuVisible) {
                    loadWalkImage(); // 恢復時顯示走路圖片
                }
                pauseTimer.stop();
            });
            pauseTimer.setRepeats(false);
            pauseTimer.start();
        }
        
        // 新增：選單顯示時暫停所有動作
        private void pauseForPopupMenu() {
            isPopupMenuVisible = true;
            wasWalkingBeforePopup = isWalking;
            wasFallingBeforePopup = isFalling;
            
            if (walkTimer.isRunning()) {
                walkTimer.stop();
            }
            if (directionTimer.isRunning()) {
                directionTimer.stop();
            }
            if (fallTimer.isRunning()) {
                fallTimer.stop();
            }
            if (pauseTimer != null && pauseTimer.isRunning()) {
                pauseTimer.stop();
            }
            
            isWalking = false;
            isFalling = false;
            isPaused = false;
            
            if (!isSitting && !isLying) {
                loadStandImage();
            }
            
            System.out.println(petType + " 選單顯示：暫停所有動作");
        }
        
        // 新增：選單隱藏時恢復動作
        private void resumeFromPopupMenu() {
            isPopupMenuVisible = false;
            if (wasFallingBeforePopup) {
                startFalling();
            } else if (wasWalkingBeforePopup && !isSitting && !isLying && !isCheering && !isCheeringUp) {
                startWalking();
            }
        }
        
        // 創建右鍵選單
        private void createPopupMenu() {
            popupMenu = new JPopupMenu();
            
            // 添加選單監聽器來追蹤選單顯示狀態
            popupMenu.addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    isPopupMenuVisible = true;
                    System.out.println(petType + " 選單即將顯示");
                }
                
                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    isPopupMenuVisible = false;
                    System.out.println(petType + " 選單即將隱藏");
                    // 選單隱藏時恢復動作
                    SwingUtilities.invokeLater(() -> resumeFromPopupMenu());
                }
                
                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {
                    isPopupMenuVisible = false;
                    System.out.println(petType + " 選單被取消");
                    // 選單取消時恢復動作
                    SwingUtilities.invokeLater(() -> resumeFromPopupMenu());
                }
            });
            
            // 創建動作子選單
            JMenu actionMenu = new JMenu("動作");
            
            // 動作子選單項目
            JMenuItem action1 = new JMenuItem("回家");
            JMenuItem action2 = new JMenuItem("亂走");
            JMenuItem action3 = new JMenuItem("坐下");
            JMenuItem action4 = new JMenuItem("躺下");
            JMenuItem action5 = new JMenuItem("歡呼");
            JMenuItem action6 = new JMenuItem("加油");
            
            // 為動作子選單項目添加事件監聽器
            action1.addActionListener(e -> handleMenuAction("回家"));
            action2.addActionListener(e -> handleMenuAction("亂走"));
            action3.addActionListener(e -> handleMenuAction("坐下"));
            action4.addActionListener(e -> handleMenuAction("躺下"));
            action5.addActionListener(e -> handleMenuAction("歡呼"));
            action6.addActionListener(e -> handleMenuAction("加油"));
            
            // 將子選單項目添加到動作選單
            actionMenu.add(action1);
            actionMenu.add(action2);
            actionMenu.add(action3);
            actionMenu.add(action4);
            actionMenu.add(action5);
            actionMenu.add(action6);
            
            // 其他選單項目
            JMenuItem item2 = new JMenuItem("番茄鐘");
            JMenuItem item3 = new JMenuItem("代辦事項");
            JMenuItem item4 = new JMenuItem("倒數計時");
            JMenuItem item5 = new JMenuItem("碼錶計時");
            JMenuItem item6 = new JMenuItem("螢幕使用時間提醒");
            
            item2.addActionListener(e -> handleMenuAction("番茄鐘"));
            item3.addActionListener(e -> handleMenuAction("代辦事項"));
            item4.addActionListener(e -> handleMenuAction("倒數計時"));
            item5.addActionListener(e -> handleMenuAction("碼錶計時"));
            item6.addActionListener(e -> handleMenuAction("螢幕使用時間提醒"));
            
            // 將所有選單項目添加到主選單
            popupMenu.add(actionMenu);
            popupMenu.add(item2);
            popupMenu.add(item3);
            popupMenu.add(item4);
            popupMenu.add(item5);
            popupMenu.add(item6);
        }
        
        private void showPopupMenu(MouseEvent e) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
        
        private void handleMenuAction(String action) {
            System.out.println(petType + " 角色執行了: " + action);
            
            switch (action) {
                case "回家":
                    System.out.println(petType + " 回家了，隱藏角色");
                    isHome = true;
                    hide();
                    // 從寵物列表中移除，這樣就可以重新召喚了
                    petWindows.remove(this);
                    break;
                case "亂走":
                    System.out.println(petType + " 開始亂走");
                    if (isSitting) {
                        standUp();
                    }
                    if (isLying) {
                        getUp();
                    }
                    if (isCheering) {
                        stopCheering();
                    }
                    if (isCheeringUp) {
                        stopCheeringUp();
                    }
                    startWalking();
                    break;
                case "坐下":
                    if (isLying) {
                        getUp();
                    }
                    if (isCheering) {
                        stopCheering();
                    }
                    if (isCheeringUp) {
                        stopCheeringUp();
                    }
                    if (isSitting) {
                        standUp();
                    } else {
                        sit();
                    }
                    break;
                case "躺下":
                    if (isSitting) {
                        standUp();
                    }
                    if (isCheering) {
                        stopCheering();
                    }
                    if (isCheeringUp) {
                        stopCheeringUp();
                    }
                    if (isLying) {
                        getUp();
                    } else {
                        lie();
                    }
                    break;
                case "歡呼":
                    if (isSitting) {
                        standUp();
                    }
                    if (isLying) {
                        getUp();
                    }
                    if (isCheeringUp) {
                        stopCheeringUp();
                    }
                    if (isCheering) {
                        stopCheering();
                    } else {
                        cheer();
                    }
                    break;
                case "加油":
                    if (isSitting) {
                        standUp();
                    }
                    if (isLying) {
                        getUp();
                    }
                    if (isCheering) {
                        stopCheering();
                    }
                    if (isCheeringUp) {
                        stopCheeringUp();
                    } else {
                        cheerUp();
                    }
                    break;
                case "番茄鐘":
                    openFunctionWindow(() -> {
                        PomodoroApp pomodoroApp = new PomodoroApp(DesktopPet.this, getCurrentPetIndex());
                        return pomodoroApp;
                    });
                    break;
                case "代辦事項":
                    JOptionPane.showMessageDialog(window, 
                        "代辦事項功能\n\n管理您的任務清單，\n追蹤待完成的工作項目。\n\n功能即將推出！", 
                        "代辦事項", 
                        JOptionPane.INFORMATION_MESSAGE);
                    break;
                case "倒數計時":
                    openFunctionWindow(() -> {
                        CountdownTimer countdownTimer = new CountdownTimer(DesktopPet.this, getCurrentPetIndex());
                        return countdownTimer;
                    });
                    break;
                case "碼錶計時":
                    openFunctionWindow(() -> {
                        Stopwatch stopwatch = new Stopwatch(DesktopPet.this, getCurrentPetIndex());
                        return stopwatch;
                    });
                    break;
                case "螢幕使用時間提醒":
                    if (screenUsedAlert != null) {
                        String currentTime = screenUsedAlert.getFormattedUsageTime();
                        boolean isMonitoring = screenUsedAlert.isMonitoring();
                        
                        String message = "螢幕使用時間監控\n\n" +
                                       "目前使用時間：" + currentTime + "\n" +
                                       "監控狀態：" + (isMonitoring ? "運行中" : "已停止") + "\n" +
                                       "提醒設定：使用1小時後提醒休息\n\n" +
                                       "點擊「重置」可重新開始計時";
                        
                        int option = JOptionPane.showOptionDialog(
                            window,
                            message,
                            "螢幕使用時間提醒",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            new String[]{"重置計時", "關閉"},
                            "關閉"
                        );
                        
                        if (option == 0) { // 重置計時
                            screenUsedAlert.resetTimer();
                            screenUsedAlert.startMonitoring();
                            JOptionPane.showMessageDialog(window, 
                                "螢幕使用時間計時器已重置並重新開始監控！", 
                                "重置成功", 
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(window, 
                            "螢幕使用時間監控尚未初始化", 
                            "錯誤", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                default:
                    System.out.println(petType + " 執行了: " + action);
                    break;
            }
        }
        
        private void setupWalkingAnimation() {
            // 修改移動計時器：加入隨機暫停邏輯
            walkTimer = new Timer(50, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!isPaused && isWalking && !isFalling && !isPopupMenuVisible) {
                        moveHorizontally();
                        // 3%機率停0.5秒，讓走路更真實
                        if (Math.random() < 0.005) {
                            pauseMovement(500); // 500毫秒 = 0.5秒
                        }

                        if (Math.random() < 0.01) {
                            pauseMovement(100); // 500毫秒 = 0.5秒
                        }

                        if (Math.random() < 0.01) {
                            pauseMovement(2000); // 500毫秒 = 0.5秒
                        }
                    }
                }
            });
            
            // 方向改變計時器
            directionTimer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!isPopupMenuVisible && !isPaused) {
                        changeDirection();
                        int nextDelay = 2000 + random.nextInt(3000);
                        directionTimer.setDelay(nextDelay);
                    }
                }
            });
            
            // 跌落計時器
            fallTimer = new Timer(30, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isFalling && !isPopupMenuVisible) {
                        fall();
                    }
                }
            });
        }
        
        private void startWalking() {
            if (!isWalking && !isFalling && !isPopupMenuVisible) {
                isWalking = true;
                isPaused = false; // 確保暫停狀態重置
                loadWalkImage();
                walkTimer.start();
                directionTimer.start();
            }
        }
        
        private void stopWalking() {
            if (isWalking) {
                isWalking = false;
                isPaused = false; // 重置暫停狀態
                walkTimer.stop();
                directionTimer.stop();
                if (pauseTimer != null && pauseTimer.isRunning()) {
                    pauseTimer.stop();
                }
                if (!isFalling && !isPopupMenuVisible) {
                    loadStandImage();
                }
            }
        }
        
        private void startFalling() {
            if (!isFalling && !isPopupMenuVisible) {
                isFalling = true;
                isWalking = false;
                isPaused = false; // 重置暫停狀態
                walkTimer.stop();
                directionTimer.stop();
                if (pauseTimer != null && pauseTimer.isRunning()) {
                    pauseTimer.stop();
                }
                loadFallImage();
                fallTimer.start();
            }
        }
        
        // 修正：跌落完成後確保正確恢復圖片狀態
        private void stopFalling() {
            if (isFalling) {
                isFalling = false;
                fallTimer.stop();
                setWindowPosition(currentX, groundLevel);
                
                if (!isPopupMenuVisible) {
                    // 先載入站立圖片，然後開始走路
                    loadStandImage();
                    Timer delayTimer = new Timer(100, e -> {
                        if (!isPopupMenuVisible && !isFalling) {
                            startWalking();
                        }
                        ((Timer) e.getSource()).stop();
                    });
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                } else {
                    // 如果選單顯示中，只載入站立圖片
                    loadStandImage();
                }
            }
        }
        
        private void fall() {
            int newY = currentY + 8; // 跌落速度
            
            if (newY >= groundLevel) {
                setWindowPosition(currentX, groundLevel);
                stopFalling();
            } else {
                setWindowPosition(currentX, newY);
            }
        }
        
        // 修改：使用全域移動速度
        // 新增：統一的位置設置方法，確保 currentX, currentY 和 window.setLocation() 同步
        private void setWindowPosition(int x, int y) {
            currentX = x;
            currentY = y;
            if (window != null) {
                window.setLocation(currentX, currentY);
            }
        }
        
        private void moveHorizontally() {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            
            int oldDirection = direction;  // 记录旧方向
            int newX = currentX + direction * SettingsWindow.globalMoveSpeed; // 使用全域移動速度
            
            // 確保寵物不會超出螢幕邊界
            if (newX <= 0) {
                newX = 0;
                direction = 1;
            } else if (newX >= screenSize.width - SettingsWindow.globalPetSize) {
                newX = screenSize.width - SettingsWindow.globalPetSize;
                direction = -1;
            }
            
            // 如果方向改变了，重新加载走路图片
            if (oldDirection != direction && isWalking) {
                loadWalkImage();
            }
            
            // 確保維持在地面高度並使用統一的位置設置方法
            setWindowPosition(newX, groundLevel);
        }
        
        private void changeDirection() {
            int oldDirection = direction;
            direction = random.nextBoolean() ? 1 : -1;
            
            // 如果方向改变了，重新加载走路图片
            if (oldDirection != direction && isWalking) {
                loadWalkImage();
            }
        }
        
        private void loadStandImage() {
            try {
                File imageFile = new File(standImagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(standImagePath);
                    Image img = icon.getImage().getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                    petLabel.setIcon(new ImageIcon(img));
                } else {
                    petLabel.setText("圖片未找到");
                    petLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    petLabel.setForeground(Color.RED);
                }
            } catch (Exception e) {
                petLabel.setText("載入失敗");
                petLabel.setHorizontalAlignment(SwingConstants.CENTER);
                petLabel.setForeground(Color.RED);
            }
        }
        
        private void loadWalkImage() {
            try {
                File imageFile = new File(walkImagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(walkImagePath);
                    Image originalImg = icon.getImage();
                    
                    // 先创建原始大小的图片
                    BufferedImage bufferedImage = new BufferedImage(
                        originalImg.getWidth(null),
                        originalImg.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB
                    );
                    Graphics2D g2d = bufferedImage.createGraphics();
                    g2d.drawImage(originalImg, 0, 0, null);
                    g2d.dispose();
                    
                    // 如果需要翻转
                    if (direction == -1) {
                        // 水平翻转
                        BufferedImage flipped = new BufferedImage(
                            bufferedImage.getWidth(),
                            bufferedImage.getHeight(),
                            BufferedImage.TYPE_INT_ARGB
                        );
                        Graphics2D g2d2 = flipped.createGraphics();
                        g2d2.translate(bufferedImage.getWidth(), 0);
                        g2d2.scale(-1, 1);
                        g2d2.drawImage(bufferedImage, 0, 0, null);
                        g2d2.dispose();
                        bufferedImage = flipped;
                    }
                    
                    // 最后缩放到需要的大小
                    Image scaledImg = bufferedImage.getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                    petLabel.setIcon(new ImageIcon(scaledImg));
                } else {
                    petLabel.setText("走路圖片未找到");
                    petLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    petLabel.setForeground(Color.RED);
                }
            } catch (Exception e) {
                petLabel.setText("走路圖片載入失敗");
                petLabel.setHorizontalAlignment(SwingConstants.CENTER);
                petLabel.setForeground(Color.RED);
            }
        }
        
        private void loadFallImage() {
            try {
                File imageFile = new File(fallImagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(fallImagePath);
                    Image img = icon.getImage().getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                    petLabel.setIcon(new ImageIcon(img));
                } else {
                    petLabel.setText("跌落圖片未找到");
                    petLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    petLabel.setForeground(Color.RED);
                }
            } catch (Exception e) {
                petLabel.setText("跌落圖片載入失敗");
                petLabel.setHorizontalAlignment(SwingConstants.CENTER);
                petLabel.setForeground(Color.RED);
            }
        }
        
        // 新增：加载坐下图片
        private void loadSitImage() {
            try {
                File imageFile = new File(sitImagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(sitImagePath);
                    Image img = icon.getImage().getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                    petLabel.setIcon(new ImageIcon(img));
                } else {
                    // 如果坐下图片不存在，使用站立图片
                    loadStandImage();
                }
            } catch (Exception e) {
                loadStandImage();
            }
        }

        // 新增：坐下方法
        private void sit() {
            if (!isSitting && !isPopupMenuVisible) {
                // 停止所有動作
                stopAllActions();
                
                // 設定坐下狀態
                isSitting = true;
                
                // 載入坐下圖片
                loadSitImage();
                
                System.out.println(petType + " 坐下了");
            }
        }

        // 新增：站起方法
        private void standUp() {
            if (isSitting && !isPopupMenuVisible) {
                // 停止所有動作
                stopAllActions();
                
                // 載入站立圖片
                loadStandImage();
                
                // 開始走路
                startWalking();
                
                System.out.println(petType + " 站起來了");
            }
        }
        
        // 新增：加载躺下图片
        private void loadLieImage() {
            try {
                File imageFile = new File(lieImagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(lieImagePath);
                    Image img = icon.getImage().getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                    petLabel.setIcon(new ImageIcon(img));
                } else {
                    // 如果躺下图片不存在，使用站立图片
                    loadStandImage();
                }
            } catch (Exception e) {
                loadStandImage();
            }
        }

        // 新增：躺下方法
        private void lie() {
            if (!isLying && !isPopupMenuVisible) {
                // 停止所有動作
                stopAllActions();
                
                // 設定躺下狀態
                isLying = true;
                
                // 載入躺下圖片
                loadLieImage();
                
                System.out.println(petType + " 躺下了");
            }
        }

        // 新增：起身方法
        private void getUp() {
            if (isLying && !isPopupMenuVisible) {
                // 停止所有動作
                stopAllActions();
                
                // 載入站立圖片
                loadStandImage();
                
                // 開始走路
                startWalking();
                
                System.out.println(petType + " 起身了");
            }
        }
        
        // 修改：歡呼方法
        private void cheer() {
            if (!isCheering && !isPopupMenuVisible) {
                // 停止所有動作
                stopAllActions();
                
                // 設定歡呼狀態
                isCheering = true;
                
                // 顯示歡呼圖片
                try {
                    System.out.println("petType=" + petType + "，歡呼圖片路徑=" + cheerImagePath);
                    System.out.println("當前工作目錄: " + System.getProperty("user.dir"));
                    File imageFile = new File(cheerImagePath);
                    if (imageFile.exists()) {
                        ImageIcon icon = new ImageIcon(cheerImagePath);
                        Image img = icon.getImage().getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                        petLabel.setIcon(new ImageIcon(img));
                    } else {
                        System.out.println("歡呼圖片未找到: " + cheerImagePath);
                        loadStandImage();
                    }
                } catch (Exception e) {
                    System.out.println("歡呼圖片載入失敗: " + e.getMessage());
                    loadStandImage();
                }
                
                System.out.println(petType + " 開始歡呼");
            }
        }

        // 修改：加油方法
        private void cheerUp() {
            if (!isCheeringUp && !isPopupMenuVisible) {
                // 停止所有動作
                stopAllActions();
                
                // 設定加油狀態
                isCheeringUp = true;
                
                // 顯示加油圖片
                try {
                    File imageFile = new File(cheerUpImagePath);
                    if (imageFile.exists()) {
                        ImageIcon icon = new ImageIcon(cheerUpImagePath);
                        Image img = icon.getImage().getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                        petLabel.setIcon(new ImageIcon(img));
                    } else {
                        System.out.println("加油圖片未找到: " + cheerUpImagePath);
                        loadStandImage();
                    }
                } catch (Exception e) {
                    System.out.println("加油圖片載入失敗: " + e.getMessage());
                    loadStandImage();
                }
                
                System.out.println(petType + " 開始加油");
            }
        }

        // 新增：停止欢呼方法
        private void stopCheering() {
            if (isCheering) {
                isCheering = false;
                
                // 如果不在選單中，開始走路
                if (!isPopupMenuVisible) {
                    if (currentY < groundLevel) {
                        startFalling();
                    } else {
                        startWalking();
                    }
                }
                
                System.out.println(petType + " 停止欢呼");
            }
        }

        // 新增：停止加油方法
        private void stopCheeringUp() {
            if (isCheeringUp) {
                isCheeringUp = false;
                
                // 如果不在選單中，開始走路
                if (!isPopupMenuVisible) {
                    if (currentY < groundLevel) {
                        startFalling();
                    } else {
                        startWalking();
                    }
                }
                
                System.out.println(petType + " 停止加油");
            }
        }
        
        public void show() {
            window.setVisible(true);
            startWalking();
        }
        
        public void hide() {
            stopWalking();
            if (isFalling) {
                fallTimer.stop();
                isFalling = false;
            }
            window.setVisible(false);
        }
        
        public void dispose() {
            stopWalking();
            if (fallTimer != null) {
                fallTimer.stop();
            }
            if (pauseTimer != null) {
                pauseTimer.stop();
            }
            if (functionWindowFollowTimer != null && functionWindowFollowTimer.isRunning()) {
                functionWindowFollowTimer.stop();
            }
            if (currentFunctionWindow != null && currentFunctionWindow.isDisplayable()) {
                currentFunctionWindow.dispose();
            }
            window.dispose();
        }
        
        public String getPetType() {
            return petType;
        }
        
        // 新增：停止所有動作
        public void stopAllActions() {
            isWalking = false;
            isFalling = false;
            isSitting = false;
            isLying = false;
            isCheering = false;
            isCheeringUp = false;
            isPaused = false;
            
            // 停止所有計時器
            if (walkTimer.isRunning()) {
                walkTimer.stop();
            }
            if (directionTimer.isRunning()) {
                directionTimer.stop();
            }
            if (fallTimer.isRunning()) {
                fallTimer.stop();
            }
            if (pauseTimer != null && pauseTimer.isRunning()) {
                pauseTimer.stop();
            }
            
            System.out.println(petType + " 停止所有動作");
        }
        
        // 新增：套用全域設定
        public void applyGlobalSettings() {
            // 記錄舊的大小
            int oldSize = window.getWidth();
            
            // 套用移動速度設定
            moveSpeed = SettingsWindow.globalMoveSpeed;
            
            // 套用透明度設定
            if (window != null) {
                float opacity = SettingsWindow.globalOpacity / 100.0f;
                window.setOpacity(opacity);
            }
            
            // 套用大小設定
            if (window != null) {
                // 計算大小變化
                int sizeChange = SettingsWindow.globalPetSize - oldSize;
                
                // 調整位置，讓寵物保持在螢幕內
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                
                // 調整 X 位置，確保不會超出螢幕邊界
                if (currentX + SettingsWindow.globalPetSize > screenSize.width) {
                    currentX = screenSize.width - SettingsWindow.globalPetSize;
                }
                if (currentX < 0) {
                    currentX = 0;
                }
                
                // 重新計算地面高度，讓寵物的腳底接觸地面
                // 地面高度 = 螢幕高度 - 寵物大小 - 底部邊距
                groundLevel = screenSize.height - SettingsWindow.globalPetSize - 40;
                
                // 設定新的大小和位置
                window.setSize(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize);
                setWindowPosition(currentX, groundLevel);
                
                // 重新載入當前圖片以套用新大小
                if (isSitting) {
                    loadSitImage();
                } else if (isLying) {
                    loadLieImage();
                } else if (isCheering) {
                    // 重新載入歡呼圖片
                    try {
                        File imageFile = new File(cheerImagePath);
                        if (imageFile.exists()) {
                            ImageIcon icon = new ImageIcon(cheerImagePath);
                            Image img = icon.getImage().getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                            petLabel.setIcon(new ImageIcon(img));
                        }
                    } catch (Exception e) {
                        loadStandImage();
                    }
                } else if (isCheeringUp) {
                    // 重新載入加油圖片
                    try {
                        File imageFile = new File(cheerUpImagePath);
                        if (imageFile.exists()) {
                            ImageIcon icon = new ImageIcon(cheerUpImagePath);
                            Image img = icon.getImage().getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                            petLabel.setIcon(new ImageIcon(img));
                        }
                    } catch (Exception e) {
                        loadStandImage();
                    }
                } else if (isFalling) {
                    loadFallImage();
                } else if (isWalking) {
                    loadWalkImage();
                } else {
                    loadStandImage();
                }
            }
            
            System.out.println(petType + " 套用全域設定 - 速度:" + moveSpeed + " 大小:" + SettingsWindow.globalPetSize + " 透明度:" + SettingsWindow.globalOpacity + "%");
        }
        
        // 新增：回家功能
        public void goHome() {
            // 停止所有動作
            stopAllActions();
            
            // 設定回家狀態
            isHome = true;
            
            // 移動到石頭圖片的實際位置
            int stonePosX = SettingsWindow.stoneX;
            int stonePosY = SettingsWindow.stoneY;
            
            // 如果石頭位置未初始化，使用預設的右下角位置
            if (stonePosX == -1 || stonePosY == -1) {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                stonePosX = screenSize.width - SettingsWindow.globalStoneSize - 0;
                stonePosY = screenSize.height - SettingsWindow.globalStoneSize - 0;
            }
            
            // 根據寵物類型設定不同的偏移位置（按石頭大小比例計算）
            double offsetRatioX = 0.0;
            double offsetRatioY = 0.0;
            
            switch (petType) {
                case "dog":
                    offsetRatioX = 0.117;  // 35/300 = 0.117
                    offsetRatioY = 0.017;  // 5/300 = 0.017
                    // 狗狗回家時旋轉340度
                    rotateDogImage();
                    break;
                case "cat":
                    offsetRatioX = 0.317;  // 95/300 = 0.317
                    offsetRatioY = 0.133;  // 40/300 = 0.133
                    // 貓咪回家時旋轉90度
                    rotateCatImage();
                    break;
                case "duck":
                    offsetRatioX = 0.050;  // 15/300 = 0.050
                    offsetRatioY = 0.167;  // 50/300 = 0.167
                    // 鴨子回家時旋轉300度
                    rotateDuckImage();
                    break;
                case "mouse":
                    offsetRatioX = 0.300;  // 90/300 = 0.300
                    offsetRatioY = 0.033;  // 10/300 = 0.033
                    // 老鼠回家時旋轉20度
                    rotateMouseImage();
                    break;
                default:
                    offsetRatioX = 0.167;  // 50/300 = 0.167
                    offsetRatioY = 0.167;  // 50/300 = 0.167
                    break;
            }
            
            // 根據當前石頭大小計算實際偏移量
            int offsetX = (int) (SettingsWindow.globalStoneSize * offsetRatioX);
            int offsetY = (int) (SettingsWindow.globalStoneSize * offsetRatioY);
            
            // 讓寵物移動到石頭旁邊的指定位置
            int petX = stonePosX + offsetX;
            int petY = stonePosY + offsetY;
            
            setWindowPosition(petX, petY);
            
            System.out.println(petType + " 回家了，位置: (" + petX + ", " + petY + ")，石頭位置: (" + stonePosX + ", " + stonePosY + ")，石頭大小: " + SettingsWindow.globalStoneSize + "，偏移: (" + offsetX + ", " + offsetY + ")");
        }
        
        // 新增：貓咪旋轉90度的方法
        private void rotateCatImage() {
            try {
                // 載入貓咪的站立圖片
                File imageFile = new File(standImagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(standImagePath);
                    Image originalImage = icon.getImage();
                    
                    // 創建旋轉後的圖片
                    BufferedImage rotatedImage = new BufferedImage(
                        originalImage.getHeight(null),
                        originalImage.getWidth(null),
                        BufferedImage.TYPE_INT_ARGB
                    );
                    
                    Graphics2D g2d = rotatedImage.createGraphics();
                    
                    // 設定旋轉中心點和角度
                    double centerX = originalImage.getWidth(null) / 2.0;
                    double centerY = originalImage.getHeight(null) / 2.0;
                    g2d.rotate(Math.toRadians(60), centerX, centerY);
                    
                    // 繪製旋轉後的圖片
                    g2d.drawImage(originalImage, 0, 0, null);
                    g2d.dispose();
                    
                    // 縮放到需要的大小並設定
                    Image scaledImage = rotatedImage.getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                    petLabel.setIcon(new ImageIcon(scaledImage));
                    
                    System.out.println(petType + " 旋轉了90度");
                }
            } catch (Exception e) {
                System.out.println("貓咪旋轉失敗: " + e.getMessage());
                // 如果旋轉失敗，使用原始圖片
                loadStandImage();
            }
        }
        
        // 新增：鴨子旋轉270度的方法
        private void rotateDuckImage() {
            try {
                // 載入鴨子的站立圖片
                File imageFile = new File(standImagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(standImagePath);
                    Image originalImage = icon.getImage();
                    
                    // 創建旋轉後的圖片
                    BufferedImage rotatedImage = new BufferedImage(
                        originalImage.getHeight(null),
                        originalImage.getWidth(null),
                        BufferedImage.TYPE_INT_ARGB
                    );
                    
                    Graphics2D g2d = rotatedImage.createGraphics();
                    
                    // 設定旋轉中心點和角度
                    double centerX = originalImage.getWidth(null) / 2.0;
                    double centerY = originalImage.getHeight(null) / 2.0;
                    g2d.rotate(Math.toRadians(300), centerX, centerY);
                    
                    // 繪製旋轉後的圖片
                    g2d.drawImage(originalImage, 0, 0, null);
                    g2d.dispose();
                    
                    // 縮放到需要的大小並設定
                    Image scaledImage = rotatedImage.getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                    petLabel.setIcon(new ImageIcon(scaledImage));
                    
                    System.out.println(petType + " 旋轉了300度");
                }
            } catch (Exception e) {
                System.out.println("鴨子旋轉失敗: " + e.getMessage());
                // 如果旋轉失敗，使用原始圖片
                loadStandImage();
            }
        }

        // 新增：老鼠旋轉20度的方法
        private void rotateMouseImage() {
            try {
                // 載入老鼠的站立圖片
                File imageFile = new File(standImagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(standImagePath);
                    Image originalImage = icon.getImage();
                    
                    // 創建旋轉後的圖片
                    BufferedImage rotatedImage = new BufferedImage(
                        originalImage.getHeight(null),
                        originalImage.getWidth(null),
                        BufferedImage.TYPE_INT_ARGB
                    );
                    
                    Graphics2D g2d = rotatedImage.createGraphics();
                    
                    // 設定旋轉中心點和角度
                    double centerX = originalImage.getWidth(null) / 2.0;
                    double centerY = originalImage.getHeight(null) / 2.0;
                    g2d.rotate(Math.toRadians(20), centerX, centerY);
                    
                    // 繪製旋轉後的圖片
                    g2d.drawImage(originalImage, 0, 0, null);
                    g2d.dispose();
                    
                    // 縮放到需要的大小並設定
                    Image scaledImage = rotatedImage.getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                    petLabel.setIcon(new ImageIcon(scaledImage));
                    
                    System.out.println(petType + " 旋轉了20度");
                }
            } catch (Exception e) {
                System.out.println("老鼠旋轉失敗: " + e.getMessage());
                // 如果旋轉失敗，使用原始圖片
                loadStandImage();
            }
        }
        
        // 新增：狗狗旋轉340度的方法
        private void rotateDogImage() {
            try {
                // 載入狗狗的站立圖片
                File imageFile = new File(standImagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(standImagePath);
                    Image originalImage = icon.getImage();
                    
                    // 創建旋轉後的圖片
                    BufferedImage rotatedImage = new BufferedImage(
                        originalImage.getHeight(null),
                        originalImage.getWidth(null),
                        BufferedImage.TYPE_INT_ARGB
                    );
                    
                    Graphics2D g2d = rotatedImage.createGraphics();
                    
                    // 設定旋轉中心點和角度
                    double centerX = originalImage.getWidth(null) / 2.0;
                    double centerY = originalImage.getHeight(null) / 2.0;
                    g2d.rotate(Math.toRadians(340), centerX, centerY);
                    
                    // 繪製旋轉後的圖片
                    g2d.drawImage(originalImage, 0, 0, null);
                    g2d.dispose();
                    
                    // 縮放到需要的大小並設定
                    Image scaledImage = rotatedImage.getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                    petLabel.setIcon(new ImageIcon(scaledImage));
                    
                    System.out.println(petType + " 旋轉了340度");
                }
            } catch (Exception e) {
                System.out.println("狗狗旋轉失敗: " + e.getMessage());
                // 如果旋轉失敗，使用原始圖片
                loadStandImage();
            }
        }
        
        // 新增：獲取回家狀態
        public boolean isHome() {
            return isHome;
        }
        
        // 新增：獲取當前寵物在列表中的索引
        private int getCurrentPetIndex() {
            return petWindows.indexOf(this);
        }
        
        // 新增：開啟功能視窗的統一方法
        private void openFunctionWindow(java.util.function.Supplier<JFrame> windowSupplier) {
            // 關閉現有的功能視窗和跟隨計時器
            if (currentFunctionWindow != null && currentFunctionWindow.isDisplayable()) {
                currentFunctionWindow.dispose();
            }
            if (functionWindowFollowTimer != null && functionWindowFollowTimer.isRunning()) {
                functionWindowFollowTimer.stop();
            }
            
            SwingUtilities.invokeLater(() -> {
                try {
                    // 創建新視窗
                    currentFunctionWindow = windowSupplier.get();
                    
                    // 記錄上次視窗位置，避免不必要的更新和閃爍
                    final Point[] lastWindowLocation = {null};
                    
                    // 更新視窗位置的方法
                    Runnable updateWindowPosition = () -> {
                        if (currentFunctionWindow != null && currentFunctionWindow.isVisible() && window.isVisible()) {
                            Point petLocation = window.getLocation();
                            
                            // 計算視窗應該放置的位置（始終在寵物正上方）
                            int idealWindowX = petLocation.x + (window.getWidth() - currentFunctionWindow.getWidth()) / 2; // 水平置中
                            int idealWindowY = petLocation.y - currentFunctionWindow.getHeight() - 10; // 在寵物上方10像素
                            
                            // 確保視窗不會超出螢幕邊界
                            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                            int newWindowX = idealWindowX;
                            int newWindowY = idealWindowY;
                            
                            // 垂直邊界檢查
                            if (newWindowY < 0) {
                                newWindowY = petLocation.y + window.getHeight() + 10; // 如果上方放不下，就放下方
                            }
                            
                            // 水平邊界檢查
                            if (newWindowX + currentFunctionWindow.getWidth() > screenSize.width) {
                                newWindowX = screenSize.width - currentFunctionWindow.getWidth();
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
                                currentFunctionWindow.setLocation(newWindowX, newWindowY);
                                lastWindowLocation[0] = newLocation;
                            }
                        }
                    };
                    
                    // 設定初始位置
                    updateWindowPosition.run();
                    currentFunctionWindow.setVisible(true);
                    
                    // 啟動位置跟隨計時器（降低更新頻率以減少閃爍）
                    functionWindowFollowTimer = new Timer(100, e -> updateWindowPosition.run());
                    functionWindowFollowTimer.start();
                    
                    // 添加視窗關閉監聽器，清理引用和停止計時器
                    currentFunctionWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent e) {
                            if (currentFunctionWindow == e.getWindow()) {
                                currentFunctionWindow = null;
                                if (functionWindowFollowTimer != null && functionWindowFollowTimer.isRunning()) {
                                    functionWindowFollowTimer.stop();
                                }
                            }
                        }
                    });
                    
                } catch (Exception e) {
                    System.err.println("開啟功能視窗失敗: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }
    
    private void createAndShowGUI() {
        frame = new JFrame("角色選擇");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 角色選擇面板
        JPanel selectionPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 烏薩奇選項
        JPanel dogPanel = createPetPanel("烏薩奇", "picture/dog_pic.png", "無窮活力  有點瘋癲");
        dogCheckBox = (JCheckBox) dogPanel.getComponent(2);
        selectionPanel.add(dogPanel);
        
        // 吉伊卡哇選項
        JPanel catPanel = createPetPanel("吉伊卡哇", "picture/cat_pic.png", "內向小鼠  勇於挑戰");
        catCheckBox = (JCheckBox) catPanel.getComponent(2);
        selectionPanel.add(catPanel);
        
        // 小八貓選項
        JPanel duckPanel = createPetPanel("小八貓", "picture/duck_pic.png", "個性開朗  八字瀏海");
        duckCheckBox = (JCheckBox) duckPanel.getComponent(2);
        selectionPanel.add(duckPanel);
        
        // 栗子饅頭選項
        JPanel mousePanel = createPetPanel("栗子饅頭", "picture/mouse_pic.png", "愛吃美食  像個大叔");
        mouseCheckBox = (JCheckBox) mousePanel.getComponent(2);
        selectionPanel.add(mousePanel);
        
        // 按鈕面板 - 修改按鈕順序
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton settingsButton = new JButton("設定");
        JButton selectAllButton = new JButton("全部勾選");
        JButton deselectAllButton = new JButton("取消勾選");
        JButton confirmButton = new JButton("確定選擇");
        JButton hideButton = new JButton("隱藏角色");
        JButton exitButton = new JButton("離開");
        
        // 按鈕事件
        // 修改程式設定按鈕事件：開啟設定視窗
        settingsButton.addActionListener(e -> {
            SettingsWindow settingsWindow = new SettingsWindow();
            settingsWindow.show();
        });
        
        selectAllButton.addActionListener(e -> {
            dogCheckBox.setSelected(true);
            catCheckBox.setSelected(true);
            duckCheckBox.setSelected(true);
            mouseCheckBox.setSelected(true);
        });
        
        deselectAllButton.addActionListener(e -> {
            dogCheckBox.setSelected(false);
            catCheckBox.setSelected(false);
            duckCheckBox.setSelected(false);
            mouseCheckBox.setSelected(false);
        });
        
        confirmButton.addActionListener(e -> addNewPets());
        
        hideButton.addActionListener(e -> {
            List<PetWindow> toRemove = new ArrayList<>();
            
            for (PetWindow petWindow : petWindows) {
                String petType = petWindow.getPetType();
                boolean shouldHide = false;
                
                switch (petType) {
                    case "dog":
                        shouldHide = dogCheckBox.isSelected();
                        break;
                    case "cat":
                        shouldHide = catCheckBox.isSelected();
                        break;
                    case "duck":
                        shouldHide = duckCheckBox.isSelected();
                        break;
                    case "mouse":
                        shouldHide = mouseCheckBox.isSelected();
                        break;
                }
                
                if (shouldHide) {
                    petWindow.dispose();
                    toRemove.add(petWindow);
                }
            }
            
            petWindows.removeAll(toRemove);
        });
        
        // 修改離開選擇按鈕功能：終止整個程式
        exitButton.addActionListener(e -> {
            // 清理所有寵物視窗
            for (PetWindow petWindow : petWindows) {
                petWindow.dispose();
            }
            // 終止整個程式
            System.exit(0);
        });
        
        // 按照新順序添加按鈕
        buttonPanel.add(settingsButton);
        buttonPanel.add(selectAllButton);
        buttonPanel.add(deselectAllButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(hideButton);
        buttonPanel.add(exitButton);
        
        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        frame.setVisible(true);
    }
    
    // 新增：初始化螢幕使用時間監控
    private void initializeScreenMonitoring() {
        screenUsedAlert = new ScreenUsedAlert(new ScreenUsedAlert.AlertCallback() {
            @Override
            public void onAlert() {
                // 當提醒觸發時，讓所有寵物執行特殊動作
                for (PetWindow petWindow : petWindows) {
                    petWindow.cheer(); // 讓寵物歡呼提醒使用者
                }
                System.out.println("螢幕使用時間提醒：寵物開始歡呼");
            }
            
            @Override
            public void onAlertEnd() {
                // 當提醒結束時，讓寵物回復正常
                for (PetWindow petWindow : petWindows) {
                    petWindow.stopCheering(); // 停止歡呼
                    petWindow.startWalking(); // 開始走路
                }
                System.out.println("螢幕使用時間提醒結束：寵物回復正常");
            }
        });
        
        // 啟動監控
        screenUsedAlert.startMonitoring();
        System.out.println("螢幕使用時間監控已啟動");
    }
    
    // 新增：根據索引獲取寵物位置的方法
    public Point getPetLocation(int petIndex) {
        if (petIndex >= 0 && petIndex < petWindows.size()) {
            PetWindow petWindow = petWindows.get(petIndex);
            if (petWindow != null && petWindow.window != null) {
                // 使用內部的 currentX, currentY 變數而不是 window.getLocation()
                // 這確保了位置的一致性，特別是在移動和停止時
                return new Point(petWindow.currentX, petWindow.currentY);
            }
        }
        // 如果索引無效或寵物不存在，返回螢幕中央
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Point(screenSize.width / 2, screenSize.height / 2);
    }
    
    // 新增：獲取石頭位置的方法
    public Point getStoneLocation() {
        if (SettingsWindow.currentStoneFrame != null && SettingsWindow.currentStoneFrame.isVisible()) {
            // 使用內部的 stoneX, stoneY 變數而不是 getLocation()
            // 這確保了位置的一致性，特別是在拖拽時
            if (SettingsWindow.stoneX != -1 && SettingsWindow.stoneY != -1) {
                return new Point(SettingsWindow.stoneX, SettingsWindow.stoneY);
            } else {
                return SettingsWindow.currentStoneFrame.getLocation();
            }
        }
        // 如果石頭不存在，返回螢幕右下角的預設位置
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Point(
            screenSize.width - SettingsWindow.globalStoneSize - 0, 
            screenSize.height - SettingsWindow.globalStoneSize - 0
        );
    }
    
    // 新增：檢查石頭是否可見的方法
    public boolean isStoneVisible() {
        return SettingsWindow.currentStoneFrame != null && SettingsWindow.currentStoneFrame.isVisible();
    }
    
    // 新增：檢查指定寵物是否可見的方法
    public boolean isPetVisible(int petIndex) {
        if (petIndex >= 0 && petIndex < petWindows.size()) {
            PetWindow petWindow = petWindows.get(petIndex);
            return petWindow != null && petWindow.window != null && petWindow.window.isVisible();
        }
        return false;
    }
    
    // 新增：獲取寵物的實際大小
    public Dimension getPetSize(int petIndex) {
        if (petIndex >= 0 && petIndex < petWindows.size()) {
            PetWindow petWindow = petWindows.get(petIndex);
            if (petWindow != null && petWindow.window != null) {
                return petWindow.window.getSize();
            }
        }
        // 返回預設大小
        return new Dimension(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize);
    }
    
    // 新增：獲取石頭的實際大小
    public Dimension getStoneSize() {
        if (SettingsWindow.currentStoneFrame != null && SettingsWindow.currentStoneFrame.isVisible()) {
            return SettingsWindow.currentStoneFrame.getSize();
        }
        // 返回預設大小
        return new Dimension(SettingsWindow.globalStoneSize, SettingsWindow.globalStoneSize);
    }
    
    private JPanel createPetPanel(String name, String imagePath, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(name));
        
        // 圖片標籤 - 設置為完全置中
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                imageLabel.setText("圖片未找到");
            }
        } catch (Exception e) {
            imageLabel.setText("載入失敗");
        }
        
        // 描述標籤
        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        
        // 勾選框
        JCheckBox checkBox = new JCheckBox();
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(descLabel, BorderLayout.SOUTH);
        panel.add(checkBox, BorderLayout.NORTH);
        
        return panel;
    }
    
    private boolean isPetExists(String petType) {
        for (PetWindow petWindow : petWindows) {
            if (petWindow.getPetType().equals(petType) && !petWindow.isHome()) {
                return true;
            }
        }
        return false;
    }
    
    private void addNewPets() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int petIndex = petWindows.size();
        
        if (dogCheckBox.isSelected() && !isPetExists("dog")) {
            int x = 50 + (petIndex * 220);
            int y = screenSize.height - 200 - 40;
            PetWindow dogWindow = new PetWindow(
                "picture/dog_stand.png",
                "picture/dog_walk.png",
                "picture/dog_fall.png",
                x, y, "dog"
            );
            petWindows.add(dogWindow);
            dogWindow.show();
            petIndex++;
        }
        
        if (catCheckBox.isSelected() && !isPetExists("cat")) {
            int x = 50 + (petIndex * 220);
            int y = screenSize.height - 200 - 40;
            PetWindow catWindow = new PetWindow(
                "picture/cat_stand.png",
                "picture/cat_walk.png",
                "picture/cat_fall.png",
                x, y, "cat"
            );
            petWindows.add(catWindow);
            catWindow.show();
            petIndex++;
        }
        
        if (duckCheckBox.isSelected() && !isPetExists("duck")) {
            int x = 50 + (petIndex * 220);
            int y = screenSize.height - 200 - 40;
            PetWindow duckWindow = new PetWindow(
                "picture/duck_stand.png",
                "picture/duck_walk.png",
                "picture/duck_fall.png",
                x, y, "duck"
            );
            petWindows.add(duckWindow);
            duckWindow.show();
            petIndex++;
        }
        
        if (mouseCheckBox.isSelected() && !isPetExists("mouse")) {
            int x = 50 + (petIndex * 220);
            int y = screenSize.height - 200 - 40;
            PetWindow mouseWindow = new PetWindow(
                "picture/mouse_stand.png",
                "picture/mouse_walk.png",
                "picture/mouse_fall.png",
                x, y, "mouse"
            );
            petWindows.add(mouseWindow);
            mouseWindow.show();
            petIndex++;
        }
    }

    // 新增：可等比例縮放圖片的 JLabel
    class ScaledImageLabel extends JLabel {
        private Image image;
        public ScaledImageLabel(Image image) {
            this.image = image;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                int w = getWidth();
                int h = getHeight();
                int imgW = image.getWidth(null);
                int imgH = image.getHeight(null);
                double scale = Math.min((double)w/imgW, (double)h/imgH);
                int drawW = (int)(imgW * scale);
                int drawH = (int)(imgH * scale);
                int x = (w - drawW) / 2;
                int y = (h - drawH) / 2;
                g.drawImage(image, x, y, drawW, drawH, null);
            }
        }
        public void setImage(Image image) {
            this.image = image;
            repaint();
        }
    }
}