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

public class main {
    private JFrame frame;
    private JCheckBox dogCheckBox, catCheckBox, duckCheckBox, mouseCheckBox;
    private List<PetWindow> petWindows = new ArrayList<>();
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new main().createAndShowGUI());
    }
    
    // 寵物視窗類別
    class PetWindow {
        JFrame window;
        JLabel petLabel;
        Timer walkTimer;
        Timer directionTimer;
        Timer fallTimer;
        Timer pauseTimer;
        int currentX, currentY;
        int direction = 1; // 1為右，-1為左
        boolean isWalking = false;
        boolean isFalling = false;
        boolean isPopupMenuVisible = false;
        boolean wasWalkingBeforePopup = false;
        boolean wasFallingBeforePopup = false;
        boolean isPaused = false;
        String standImagePath;
        String walkImagePath;
        String fallImagePath;
        String petType;
        Random random = new Random();
        int groundLevel;
        int moveSpeed; // 新增：個別移動速度
        JPopupMenu popupMenu;
        
        public PetWindow(String standPath, String walkPath, String fallPath, int x, int y, String type) {
            this.standImagePath = standPath;
            this.walkImagePath = walkPath;
            this.fallImagePath = fallPath;
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
            window.setSize(200, 200);
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
                        if (!isPopupMenuVisible) { // 只有在選單不顯示時才允許拖動
                            mouseDownCompCoords[0] = e.getPoint();
                            stopWalking();
                        }
                    }
                }
                
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && !isPopupMenuVisible) {
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
                    if (mouseDownCompCoords[0] != null && SwingUtilities.isLeftMouseButton(e) && !isPopupMenuVisible) {
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
            // 記錄當前狀態
            wasWalkingBeforePopup = isWalking;
            wasFallingBeforePopup = isFalling;
            
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
            
            // 重置狀態
            isWalking = false;
            isFalling = false;
            isPaused = false;
            
            // 強制顯示站立圖片
            loadStandImage();
            
            System.out.println(petType + " 選單顯示：暫停所有動作");
        }
        
        // 新增：選單隱藏時恢復動作
        private void resumeFromPopupMenu() {
            if (!isPopupMenuVisible) {
                System.out.println(petType + " 選單隱藏：恢復動作");
                
                // 根據之前的狀態和當前位置決定恢復什麼動作
                if (wasFallingBeforePopup || currentY < groundLevel) {
                    // 如果之前在跌落或現在在空中，恢復跌落
                    startFalling();
                } else if (wasWalkingBeforePopup) {
                    // 如果之前在走路，恢復走路
                    startWalking();
                } else {
                    // 預設恢復走路
                    startWalking();
                }
                
                // 重置記錄的狀態
                wasWalkingBeforePopup = false;
                wasFallingBeforePopup = false;
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
            
            JMenuItem item1 = new JMenuItem("功能1");
            JMenuItem item2 = new JMenuItem("功能2");
            JMenuItem item3 = new JMenuItem("功能3");
            JMenuItem item4 = new JMenuItem("功能4");
            JMenuItem item5 = new JMenuItem("功能5");
            JMenuItem item6 = new JMenuItem("功能6");
            
            item1.addActionListener(e -> handleMenuAction("功能1"));
            item2.addActionListener(e -> handleMenuAction("功能2"));
            item3.addActionListener(e -> handleMenuAction("功能3"));
            item4.addActionListener(e -> handleMenuAction("功能4"));
            item5.addActionListener(e -> handleMenuAction("功能5"));
            item6.addActionListener(e -> handleMenuAction("功能6"));
            
            popupMenu.add(item1);
            popupMenu.add(item2);
            popupMenu.add(item3);
            popupMenu.addSeparator();
            popupMenu.add(item4);
            popupMenu.add(item5);
            popupMenu.add(item6);
        }
        
        private void showPopupMenu(MouseEvent e) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
        
        private void handleMenuAction(String action) {
            System.out.println(petType + " 角色執行了: " + action);
            // 在這裡可以根據不同的功能添加具體的實現
        }
        
        private void setupWalkingAnimation() {
            // 修改移動計時器：加入隨機暫停邏輯
            walkTimer = new Timer(50, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!isPaused && isWalking && !isFalling && !isPopupMenuVisible) {
                        moveHorizontally();
                        // 3%機率停0.5秒，讓走路更真實
                        if (Math.random() < 0.03) {
                            pauseMovement(500); // 500毫秒 = 0.5秒
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
        
        // 修改：使用個別的移動速度
        private void moveHorizontally() {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            
            currentX += direction * moveSpeed; // 使用個別設定的移動速度
            
            if (currentX <= 0) {
                currentX = 0;
                direction = 1;
            } else if (currentX >= screenSize.width - 200) {
                currentX = screenSize.width - 200;
                direction = -1;
            }
            
            // 確保維持在地面高度
            currentY = groundLevel;
            window.setLocation(currentX, currentY);
        }
        
        private void changeDirection() {
            direction = random.nextBoolean() ? 1 : -1;
        }
        
        private void loadStandImage() {
            try {
                File imageFile = new File(standImagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(standImagePath);
                    Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
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
                    Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    petLabel.setIcon(new ImageIcon(img));
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
                    Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
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
            window.dispose();
        }
        
        public String getPetType() {
            return petType;
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
        
        // 狗狗選項
        JPanel dogPanel = createPetPanel("狗狗", "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\dog_pic.png", "忠誠的夥伴");
        dogCheckBox = (JCheckBox) dogPanel.getComponent(2);
        selectionPanel.add(dogPanel);
        
        // 貓咪選項
        JPanel catPanel = createPetPanel("貓咪", "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\cat_pic.png", "優雅的朋友");
        catCheckBox = (JCheckBox) catPanel.getComponent(2);
        selectionPanel.add(catPanel);
        
        // 鴨子選項
        JPanel duckPanel = createPetPanel("鴨子", "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\duck_pic.jpg", "可愛的水鳥");
        duckCheckBox = (JCheckBox) duckPanel.getComponent(2);
        selectionPanel.add(duckPanel);
        
        // 老鼠選項
        JPanel mousePanel = createPetPanel("老鼠", "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\mouse_pic.jpg", "機靈的小夥伴");
        mouseCheckBox = (JCheckBox) mousePanel.getComponent(2);
        selectionPanel.add(mousePanel);
        
        // 按鈕面板 - 修改按鈕順序
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton settingsButton = new JButton("程式設定");
        JButton selectAllButton = new JButton("全部勾選");
        JButton deselectAllButton = new JButton("取消勾選");
        JButton confirmButton = new JButton("確定選擇");
        JButton hideButton = new JButton("隱藏角色");
        JButton exitButton = new JButton("離開選擇");
        
        // 按鈕事件
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
    
    private JPanel createPetPanel(String name, String imagePath, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(name));
        
        // 圖片標籤
        JLabel imageLabel = new JLabel();
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                imageLabel.setText("圖片未找到");
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
        } catch (Exception e) {
            imageLabel.setText("載入失敗");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
            if (petWindow.getPetType().equals(petType)) {
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
                "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\dog_stand.png",
                "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\dog_walk.png",
                "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\dog_fall.png",
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
                "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\cat_stand.png",
                "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\cat_walk.png",
                "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\cat_fall.png",
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
                "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\duck_stand.png",
                "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\duck_walk.png",
                "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\duck_fall.png",
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
                "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\mouse_stand.png",
                "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\mouse_walk.png",
                "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\mouse_fall.png",
                x, y, "mouse"
            );
            petWindows.add(mouseWindow);
            mouseWindow.show();
            petIndex++;
        }
    }
}
