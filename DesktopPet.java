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

public class DesktopPet {
    private JFrame frame;
    private JCheckBox dogCheckBox, catCheckBox, duckCheckBox, mouseCheckBox;
    private List<PetWindow> petWindows = new ArrayList<>();
    private ScreenUsedAlert globalScreenAlert; // 全域螢幕使用時間監控
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DesktopPet().createAndShowGUI());
    }
    
    // 新增：初始化全域螢幕使用時間監控
    private void initializeGlobalScreenAlert() {
        globalScreenAlert = new ScreenUsedAlert(new ScreenUsedAlert.AlertCallback() {
            @Override
            public void onAlert() {
                System.out.println("全域螢幕使用時間提醒：該休息了！");
                // 可以在這裡添加額外的視覺提醒，例如讓所有寵物執行特定動作
                for (PetWindow petWindow : petWindows) {
                    // 讓所有寵物做加油動作提醒使用者休息
                    petWindow.cheerUp();
                }
            }
            
            @Override
            public void onAlertEnd() {
                System.out.println("全域螢幕使用時間提醒結束");
                // 讓所有寵物恢復正常狀態
                for (PetWindow petWindow : petWindows) {
                    petWindow.stopCheeringUp();
                }
            }
        });
        
        // 自動開始監控
        globalScreenAlert.startMonitoring();
        System.out.println("全域螢幕使用時間監控已自動啟動");
    }
    
    // 新增：獲取全域螢幕使用時間監控
    public ScreenUsedAlert getGlobalScreenAlert() {
        return globalScreenAlert;
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
            JPanel settingsPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 改為 3 行
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
                
                // 套用設定到所有現有的寵物
                applySettingsToAllPets();
                
                JOptionPane.showMessageDialog(petSettingsFrame, 
                    "設定已套用！\n移動速度: " + speedSlider.getValue() + "\n寵物大小: " + sizeSlider.getValue() + "\n透明度: " + opacitySlider.getValue() + "%", 
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
                // 重新載入石頭圖片並調整大小
                try {
                    File imageFile = new File("picture/home.jpg");
                    if (imageFile.exists()) {
                        ImageIcon icon = new ImageIcon("picture/home.jpg");
                        Image originalImage = icon.getImage();
                        
                        // 使用石頭專用的大小
                        Image scaledImage = originalImage.getScaledInstance(SettingsWindow.globalStoneSize, SettingsWindow.globalStoneSize, Image.SCALE_SMOOTH);
                        
                        // 更新石頭視窗中的圖片
                        JPanel panel = (JPanel) currentStoneFrame.getContentPane();
                        JLabel imageLabel = (JLabel) panel.getComponent(0);
                        imageLabel.setIcon(new ImageIcon(scaledImage));
                        
                        // 調整視窗大小
                        currentStoneFrame.setSize(SettingsWindow.globalStoneSize, SettingsWindow.globalStoneSize);
                        
                        // 調整位置，確保不會超出螢幕邊界
                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        if (stoneX + SettingsWindow.globalStoneSize > screenSize.width) {
                            stoneX = screenSize.width - SettingsWindow.globalStoneSize;
                        }
                        if (stoneY + SettingsWindow.globalStoneSize > screenSize.height) {
                            stoneY = screenSize.height - SettingsWindow.globalStoneSize;
                        }
                        currentStoneFrame.setLocation(stoneX, stoneY);
                        
                        System.out.println("石頭大小已更新為: " + SettingsWindow.globalStoneSize + "x" + SettingsWindow.globalStoneSize);
                    }
                } catch (Exception e) {
                    System.out.println("更新石頭大小失敗: " + e.getMessage());
                }
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
                // 如果已經有石頭視窗存在，先關閉它
                if (currentStoneFrame != null && currentStoneFrame.isVisible()) {
                    currentStoneFrame.dispose();
                    currentStoneFrame = null;
                }
                
                File imageFile = new File("picture/home.jpg");
                if (imageFile.exists()) {
                    // 創建新視窗來顯示圖片
                    JFrame imageFrame = new JFrame("Home Image");
                    imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    
                    // 記錄當前石頭視窗
                    currentStoneFrame = imageFrame;
                    
                    // 添加視窗關閉監聽器
                    imageFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            currentStoneFrame = null;
                        }
                    });
                    
                    // 移除邊框和背景
                    imageFrame.setUndecorated(true);
                    imageFrame.setBackground(new Color(0, 0, 0, 0));
                    imageFrame.setAlwaysOnTop(true);
                    
                    // 載入圖片
                    ImageIcon icon = new ImageIcon("picture/home.jpg");
                    Image originalImage = icon.getImage();
                    
                    // 使用石頭專用的大小
                    Image scaledImage = originalImage.getScaledInstance(SettingsWindow.globalStoneSize, SettingsWindow.globalStoneSize, Image.SCALE_SMOOTH);
                    
                    JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    
                    // 設定面板為透明
                    JPanel panel = new JPanel();
        panel.setOpaque(false);
                    panel.add(imageLabel);
                    
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
                    imageFrame.pack();
                    
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
                        "找不到 home.jpg 圖片檔案", 
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
            SwingUtilities.invokeLater(() -> {
                // 從石頭圖片啟動，使用 -1 表示跟隨石頭
                PomodoroApp pomodoroApp = new PomodoroApp(DesktopPet.this, -1);
                pomodoroApp.setVisible(true);
                System.out.println("番茄鐘應用程式已啟動");
            });
        }
        
        // 新增：代辦事項功能
        private void showTodoList() {
            SwingUtilities.invokeLater(() -> {
                // 創建簡單的代辦事項視窗
                JFrame todoFrame = new JFrame("代辦事項");
                todoFrame.setSize(400, 500);
                todoFrame.setLocationRelativeTo(settingsFrame);
                todoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                
                JPanel mainPanel = new JPanel(new BorderLayout());
                
                // 標題
                JLabel titleLabel = new JLabel("代辦事項清單", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
                titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                
                // 代辦事項列表區域
                DefaultListModel<String> listModel = new DefaultListModel<>();
                listModel.addElement("📝 完成桌面寵物功能整合");
                listModel.addElement("⏰ 設定番茄鐘工作時間");
                listModel.addElement("💻 檢查螢幕使用時間");
                listModel.addElement("🎯 規劃今日學習目標");
                
                JList<String> todoList = new JList<>(listModel);
                todoList.setFont(new Font("Dialog", Font.PLAIN, 14));
                todoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                JScrollPane scrollPane = new JScrollPane(todoList);
                
                // 輸入區域
                JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
                inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                JTextField inputField = new JTextField();
                inputField.setFont(new Font("Dialog", Font.PLAIN, 14));
                
                JButton addButton = new JButton("新增");
                JButton removeButton = new JButton("移除");
                JButton closeButton = new JButton("關閉");
                
                // 按鈕事件
                addButton.addActionListener(e -> {
                    String newItem = inputField.getText().trim();
                    if (!newItem.isEmpty()) {
                        listModel.addElement("📋 " + newItem);
                        inputField.setText("");
                    }
                });
                
                removeButton.addActionListener(e -> {
                    int selectedIndex = todoList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        listModel.remove(selectedIndex);
                    }
                });
                
                closeButton.addActionListener(e -> todoFrame.dispose());
                
                // Enter鍵新增項目
                inputField.addActionListener(e -> addButton.doClick());
                
                JPanel buttonPanel = new JPanel(new FlowLayout());
                buttonPanel.add(addButton);
                buttonPanel.add(removeButton);
                buttonPanel.add(closeButton);
                
                inputPanel.add(new JLabel("新增項目:"), BorderLayout.WEST);
                inputPanel.add(inputField, BorderLayout.CENTER);
                inputPanel.add(buttonPanel, BorderLayout.SOUTH);
                
                mainPanel.add(titleLabel, BorderLayout.NORTH);
                mainPanel.add(scrollPane, BorderLayout.CENTER);
                mainPanel.add(inputPanel, BorderLayout.SOUTH);
                
                todoFrame.add(mainPanel);
                todoFrame.setVisible(true);
                
                System.out.println("代辦事項視窗已開啟");
            });
        }
        
        // 新增：倒數計時功能
        private void showCountdownTimer() {
            SwingUtilities.invokeLater(() -> {
                // 從石頭圖片啟動，使用 -1 表示跟隨石頭
                CountdownTimer countdownTimer = new CountdownTimer(DesktopPet.this, -1);
                countdownTimer.setVisible(true);
                System.out.println("倒數計時器已啟動");
            });
        }
        
        // 新增：碼表計時功能
        private void showStopwatch() {
            SwingUtilities.invokeLater(() -> {
                // 從石頭圖片啟動，使用 -1 表示跟隨石頭
                Stopwatch stopwatch = new Stopwatch(DesktopPet.this, -1);
                stopwatch.setVisible(true);
                System.out.println("碼表計時器已啟動");
            });
        }
        
        // 新增：螢幕使用時間提醒功能
        private void showScreenTimeReminder() {
            SwingUtilities.invokeLater(() -> {
                // 使用全域螢幕使用時間監控
                if (globalScreenAlert != null) {
                    String statusMessage;
                    if (globalScreenAlert.isMonitoring()) {
                        statusMessage = "螢幕使用時間監控正在運行中！\n\n" +
                                      "系統將在您使用電腦1小時後提醒您休息。\n" +
                                      "當前使用時間：" + globalScreenAlert.getFormattedUsageTime();
                    } else {
                        globalScreenAlert.startMonitoring();
                        statusMessage = "螢幕使用時間監控已重新啟動！\n\n" +
                                      "系統將在您使用電腦1小時後提醒您休息。";
                    }
                    
                    JOptionPane.showMessageDialog(settingsFrame, 
                        statusMessage, 
                        "螢幕使用時間提醒", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(settingsFrame, 
                        "螢幕使用時間監控尚未初始化。\n請重新啟動程式。", 
                        "錯誤", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        }
        
        public void show() {
            settingsFrame.setVisible(true);
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
        
        // 新增：追蹤當前開啟的功能視窗
        private JFrame currentFunctionWindow = null;
        private String currentFunctionType = null;
        
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
            this.groundLevel = screenSize.height - 200 - 40;
            
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
                        currentX = currCoords.x - mouseDownCompCoords[0].x;
                        currentY = currCoords.y - mouseDownCompCoords[0].y;
                        window.setLocation(currentX, currentY);
                        
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
                    SwingUtilities.invokeLater(() -> {
                        // 找到這個寵物在列表中的索引
                        int petIndex = petWindows.indexOf(this);
                        PomodoroApp pomodoroApp = new PomodoroApp(DesktopPet.this, petIndex);
                        setCurrentFunctionWindow(pomodoroApp, "番茄鐘");
                        pomodoroApp.setVisible(true);
                        System.out.println(petType + " 啟動番茄鐘應用程式");
                    });
                    break;
                case "代辦事項":
                    SwingUtilities.invokeLater(() -> {
                        // 創建簡單的代辦事項視窗
                        JFrame todoFrame = new JFrame("代辦事項");
                        todoFrame.setSize(400, 500);
                        todoFrame.setLocationRelativeTo(window);
                        todoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        
                        JPanel mainPanel = new JPanel(new BorderLayout());
                        
                        // 標題
                        JLabel titleLabel = new JLabel("代辦事項清單", SwingConstants.CENTER);
                        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
                        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                        
                        // 代辦事項列表區域
                        DefaultListModel<String> listModel = new DefaultListModel<>();
                        listModel.addElement("📝 完成桌面寵物功能整合");
                        listModel.addElement("⏰ 設定番茄鐘工作時間");
                        listModel.addElement("💻 檢查螢幕使用時間");
                        listModel.addElement("🎯 規劃今日學習目標");
                        
                        JList<String> todoList = new JList<>(listModel);
                        todoList.setFont(new Font("Dialog", Font.PLAIN, 14));
                        todoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        JScrollPane scrollPane = new JScrollPane(todoList);
                        
                        // 輸入區域
                        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
                        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                        
                        JTextField inputField = new JTextField();
                        inputField.setFont(new Font("Dialog", Font.PLAIN, 14));
                        
                        JButton addButton = new JButton("新增");
                        JButton removeButton = new JButton("移除");
                        JButton closeButton = new JButton("關閉");
                        
                        // 按鈕事件
                        addButton.addActionListener(e -> {
                            String newItem = inputField.getText().trim();
                            if (!newItem.isEmpty()) {
                                listModel.addElement("📋 " + newItem);
                                inputField.setText("");
                            }
                        });
                        
                        removeButton.addActionListener(e -> {
                            int selectedIndex = todoList.getSelectedIndex();
                            if (selectedIndex != -1) {
                                listModel.remove(selectedIndex);
                            }
                        });
                        
                        closeButton.addActionListener(e -> todoFrame.dispose());
                        
                        // Enter鍵新增項目
                        inputField.addActionListener(e -> addButton.doClick());
                        
                        JPanel buttonPanel = new JPanel(new FlowLayout());
                        buttonPanel.add(addButton);
                        buttonPanel.add(removeButton);
                        buttonPanel.add(closeButton);
                        
                        inputPanel.add(new JLabel("新增項目:"), BorderLayout.WEST);
                        inputPanel.add(inputField, BorderLayout.CENTER);
                        inputPanel.add(buttonPanel, BorderLayout.SOUTH);
                        
                        mainPanel.add(titleLabel, BorderLayout.NORTH);
                        mainPanel.add(scrollPane, BorderLayout.CENTER);
                        mainPanel.add(inputPanel, BorderLayout.SOUTH);
                        
                        todoFrame.add(mainPanel);
                        setCurrentFunctionWindow(todoFrame, "代辦事項");
                        todoFrame.setVisible(true);
                        
                        System.out.println(petType + " 開啟代辦事項視窗");
                    });
                    break;
                case "倒數計時":
                    SwingUtilities.invokeLater(() -> {
                        // 找到這個寵物在列表中的索引
                        int petIndex = petWindows.indexOf(this);
                        CountdownTimer countdownTimer = new CountdownTimer(DesktopPet.this, petIndex);
                        setCurrentFunctionWindow(countdownTimer, "倒數計時");
                        countdownTimer.setVisible(true);
                        System.out.println(petType + " 啟動倒數計時器");
                    });
                    break;
                case "碼錶計時":
                    SwingUtilities.invokeLater(() -> {
                        // 找到這個寵物在列表中的索引
                        int petIndex = petWindows.indexOf(this);
                        Stopwatch stopwatch = new Stopwatch(DesktopPet.this, petIndex);
                        setCurrentFunctionWindow(stopwatch, "碼錶計時");
                        stopwatch.setVisible(true);
                        System.out.println(petType + " 啟動碼表計時器");
                    });
                    break;
                case "螢幕使用時間提醒":
                    SwingUtilities.invokeLater(() -> {
                        // 使用全域螢幕使用時間監控
                        if (globalScreenAlert != null) {
                            String statusMessage;
                            if (globalScreenAlert.isMonitoring()) {
                                statusMessage = "螢幕使用時間監控正在運行中！\n\n" +
                                              "系統將在您使用電腦1小時後提醒您休息。\n" +
                                              "當前使用時間：" + globalScreenAlert.getFormattedUsageTime();
                            } else {
                                globalScreenAlert.startMonitoring();
                                statusMessage = "螢幕使用時間監控已重新啟動！\n\n" +
                                              "系統將在您使用電腦1小時後提醒您休息。";
                            }
                            
                            JOptionPane.showMessageDialog(window, 
                                statusMessage, 
                                "螢幕使用時間提醒", 
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(window, 
                                "螢幕使用時間監控尚未初始化。\n請重新啟動程式。", 
                                "錯誤", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    });
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
                currentY = groundLevel;
                window.setLocation(currentX, currentY);
                
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
            currentY += 8; // 跌落速度
            
            if (currentY >= groundLevel) {
                currentY = groundLevel;
                window.setLocation(currentX, currentY);
                stopFalling();
            } else {
                window.setLocation(currentX, currentY);
            }
        }
        
        // 修改：使用全域移動速度
        private void moveHorizontally() {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            
            int oldDirection = direction;  // 记录旧方向
            currentX += direction * SettingsWindow.globalMoveSpeed; // 使用全域移動速度
            
            // 確保寵物不會超出螢幕邊界
            if (currentX <= 0) {
                currentX = 0;
                direction = 1;
            } else if (currentX >= screenSize.width - SettingsWindow.globalPetSize) {
                currentX = screenSize.width - SettingsWindow.globalPetSize;
                direction = -1;
            }
            
            // 如果方向改变了，重新加载走路图片
            if (oldDirection != direction && isWalking) {
                loadWalkImage();
            }
            
            // 確保維持在地面高度
            currentY = groundLevel;
            window.setLocation(currentX, currentY);
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
            // 關閉當前開啟的功能視窗
            closeCurrentFunctionWindow();
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
            // 關閉當前開啟的功能視窗
            closeCurrentFunctionWindow();
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
                
                // 調整 Y 位置，確保維持在新的地面高度
                currentY = groundLevel;
                
                // 設定新的大小和位置
                window.setSize(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize);
                window.setLocation(currentX, currentY);
                
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
                stonePosX = screenSize.width - 300 - 0;
                stonePosY = screenSize.height - 300 - 0;
            }
            
            // 根據寵物類型設定不同的偏移位置
            int offsetX = 0;
            int offsetY = 0;
            
            switch (petType) {
                case "dog":
                    offsetX = 35;  // 石頭右邊 0 像素
                    offsetY = 5;  // 石頭下方 0 像素
                    // 狗狗回家時旋轉340度
                    rotateDogImage();
                    break;
                case "cat":
                    offsetX = 95; // 石頭右邊 100 像素
                    offsetY = 40;  // 石頭下方 50 像素
                    // 貓咪回家時旋轉90度
                    rotateCatImage();
                    break;
                case "duck":
                    offsetX = 15;  // 石頭右邊 50 像素
                    offsetY = 50; // 石頭下方 100 像素
                    // 鴨子回家時旋轉300度
                    rotateDuckImage();
                    break;
                case "mouse":
                    offsetX = 90; // 石頭右邊 100 像素
                    offsetY = 10; // 石頭下方 100 像素
                    // 老鼠回家時旋轉20度
                    rotateMouseImage();
                    break;
                default:
                    offsetX = 50;
                    offsetY = 50;
                    break;
            }
            
            // 讓寵物移動到石頭旁邊的指定位置
            int petX = stonePosX + offsetX;
            int petY = stonePosY + offsetY;
            
            currentX = petX;
            currentY = petY;
            window.setLocation(currentX, currentY);
            
            System.out.println(petType + " 回家了，位置: (" + petX + ", " + petY + ")，石頭位置: (" + stonePosX + ", " + stonePosY + ")");
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
                    Image scaledImage = rotatedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
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
                    Image scaledImage = rotatedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
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
                    Image scaledImage = rotatedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
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
                    Image scaledImage = rotatedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
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
        
        // 新增：獲取寵物視窗位置
        public Point getLocation() {
            return new Point(currentX, currentY);
        }
        
        // 新增：獲取寵物視窗
        public JFrame getWindow() {
            return window;
        }
        
        // 新增：關閉當前功能視窗的方法
        private void closeCurrentFunctionWindow() {
            if (currentFunctionWindow != null && currentFunctionWindow.isDisplayable()) {
                System.out.println(petType + " 關閉當前功能視窗: " + currentFunctionType);
                currentFunctionWindow.dispose();
            }
            currentFunctionWindow = null;
            currentFunctionType = null;
        }
        
        // 新增：設定當前功能視窗的方法
        private void setCurrentFunctionWindow(JFrame window, String functionType) {
            // 先關閉舊的功能視窗
            closeCurrentFunctionWindow();
            
            // 設定新的功能視窗
            currentFunctionWindow = window;
            currentFunctionType = functionType;
            
            // 當視窗關閉時，清除引用
            window.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    if (currentFunctionWindow == window) {
                        currentFunctionWindow = null;
                        currentFunctionType = null;
                        System.out.println(petType + " 功能視窗已關閉: " + functionType);
                    }
                }
            });
            
            System.out.println(petType + " 開啟新功能視窗: " + functionType);
        }
    }
    
    // 新增：獲取石頭圖片位置的方法
    public Point getStoneLocation() {
        if (SettingsWindow.currentStoneFrame != null && SettingsWindow.currentStoneFrame.isVisible()) {
            return SettingsWindow.currentStoneFrame.getLocation();
        }
        return null;
    }
    
    // 新增：檢查石頭圖片是否可見的方法
    public boolean isStoneVisible() {
        return SettingsWindow.currentStoneFrame != null && SettingsWindow.currentStoneFrame.isVisible();
    }
    
    // 新增：獲取指定寵物的位置
    public Point getPetLocation(int index) {
        if (index >= 0 && index < petWindows.size()) {
            return petWindows.get(index).getLocation();
        }
        return null;
    }
    
    // 新增：獲取指定寵物的視窗
    public JFrame getPetWindow(int index) {
        if (index >= 0 && index < petWindows.size()) {
            return petWindows.get(index).getWindow();
        }
        return null;
    }
    
    // 新增：檢查指定寵物是否可見
    public boolean isPetVisible(int index) {
        if (index >= 0 && index < petWindows.size()) {
            return petWindows.get(index).getWindow().isVisible();
        }
        return false;
    }
    
    // 新增：獲取寵物數量
    public int getPetCount() {
        return petWindows.size();
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
        
        // 自動啟動全域螢幕使用時間監控
        initializeGlobalScreenAlert();
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
    
    // 修改：尋找現有寵物視窗，如果存在則返回該視窗，否則返回null
    private PetWindow findExistingPet(String petType) {
        for (PetWindow petWindow : petWindows) {
            if (petWindow.getPetType().equals(petType) && !petWindow.isHome()) {
                return petWindow;
            }
        }
        return null;
    }
    
    // 保留原有的isPetExists方法以維持相容性
    private boolean isPetExists(String petType) {
        return findExistingPet(petType) != null;
    }
    
    private void addNewPets() {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int petIndex = 0; // 重新計算索引，因為可能會有關閉的視窗
        
        // 計算當前活躍寵物數量
        for (PetWindow petWindow : petWindows) {
            if (!petWindow.isHome()) {
                petIndex++;
            }
        }
        
        if (dogCheckBox.isSelected()) {
            PetWindow existingDog = findExistingPet("dog");
            if (existingDog != null) {
                // 關閉現有的狗狗視窗
                System.out.println("關閉現有的狗狗視窗");
                existingDog.dispose();
                petWindows.remove(existingDog);
                petIndex--; // 減少索引，因為移除了一個視窗
            }
            
            // 創建新的狗狗視窗
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
            System.out.println("創建新的狗狗視窗");
            petIndex++;
        }
        
        if (catCheckBox.isSelected()) {
            PetWindow existingCat = findExistingPet("cat");
            if (existingCat != null) {
                // 關閉現有的貓咪視窗
                System.out.println("關閉現有的貓咪視窗");
                existingCat.dispose();
                petWindows.remove(existingCat);
                petIndex--;
            }
            
            // 創建新的貓咪視窗
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
            System.out.println("創建新的貓咪視窗");
            petIndex++;
        }
        
        if (duckCheckBox.isSelected()) {
            PetWindow existingDuck = findExistingPet("duck");
            if (existingDuck != null) {
                // 關閉現有的鴨子視窗
                System.out.println("關閉現有的鴨子視窗");
                existingDuck.dispose();
                petWindows.remove(existingDuck);
                petIndex--;
            }
            
            // 創建新的鴨子視窗
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
            System.out.println("創建新的鴨子視窗");
            petIndex++;
        }
        
        if (mouseCheckBox.isSelected()) {
            PetWindow existingMouse = findExistingPet("mouse");
            if (existingMouse != null) {
                // 關閉現有的老鼠視窗
                System.out.println("關閉現有的老鼠視窗");
                existingMouse.dispose();
                petWindows.remove(existingMouse);
                petIndex--;
            }
            
            // 創建新的老鼠視窗
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
            System.out.println("創建新的老鼠視窗");
            petIndex++;
        }
    }
}
