package pet;
import javax.swing.*;

import pomodoro.ScreenUsedAlert;
import window.PetWindow;
import window.SettingsWindow;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DesktopPet {
    public JFrame frame;
    private JCheckBox dogCheckBox, catCheckBox, duckCheckBox, mouseCheckBox;
    public List<PetWindow> petWindows = new ArrayList<>();
    public ScreenUsedAlert screenUsedAlert; // 新增：螢幕使用時間監控
    
    public void createAndShowGUI() {
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
            SettingsWindow settingsWindow = new SettingsWindow(this);
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
    public void initializeScreenMonitoring() {
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
    
    // FIXME: patch picture path
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
                this, "picture/dog_stand.png",
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
                this, "picture/cat_stand.png",
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
                this, "picture/duck_stand.png",
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
                this, "picture/mouse_stand.png",
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
    public class ScaledImageLabel extends JLabel {
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