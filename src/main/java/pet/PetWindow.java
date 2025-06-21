package pet;

// 寵物視窗類別

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Random;

import javax.swing.*;
import javax.swing.event.*;

import utils.PathTool;
import pomodoro.CountdownTimer;
import pomodoro.PomodoroApp;
import pomodoro.Stopwatch;
import taskmanager.TaskManagerApp;

public class PetWindow {
    /**
     *
     */
    private final DesktopPet desktopPet;
    public JFrame window;
    JLabel petLabel;
    Timer walkTimer;
    Timer directionTimer;
    Timer fallTimer;
    Timer pauseTimer;
    Timer sitTimer;
    public int currentX;
    public int currentY;
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
    
    public PetWindow(DesktopPet desktopPet, String standPath, String walkPath, String fallPath, int x, int y, String type) {

        this.desktopPet = desktopPet;
        this.standImagePath = PathTool.patchPicturePath(standPath);
        this.walkImagePath = PathTool.patchPicturePath(walkPath);
        this.fallImagePath = PathTool.patchPicturePath(fallPath);
        this.sitImagePath = PathTool.patchPicturePath(standPath.replace("_stand.png", "_sit.png"));
        this.lieImagePath = PathTool.patchPicturePath(standPath.replace("_stand.png", "_lie.png"));
        this.cheerImagePath = PathTool.patchPicturePath("picture/" + type + "_cheer.png");
        this.cheerUpImagePath = PathTool.patchPicturePath("picture/" + type + "_cheerup.png");
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
                this.desktopPet.petWindows.remove(this);
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
                    PomodoroApp pomodoroApp = new PomodoroApp(this.desktopPet, getCurrentPetIndex());
                    return pomodoroApp;
                });
                break;
            case "代辦事項":
                openFunctionWindow(() -> {
                    TaskManagerApp taskManagerApp = new TaskManagerApp(this.desktopPet, getCurrentPetIndex());
                    return taskManagerApp;
                });
                break;
            case "倒數計時":
                openFunctionWindow(() -> {
                    CountdownTimer countdownTimer = new CountdownTimer(this.desktopPet, getCurrentPetIndex());
                    return countdownTimer;
                });
                break;
            case "碼錶計時":
                openFunctionWindow(() -> {
                    Stopwatch stopwatch = new Stopwatch(this.desktopPet, getCurrentPetIndex());
                    return stopwatch;
                });
                break;
            case "螢幕使用時間提醒":
                if (this.desktopPet.screenUsedAlert != null) {
                    String currentTime = this.desktopPet.screenUsedAlert.getFormattedUsageTime();
                    boolean isMonitoring = this.desktopPet.screenUsedAlert.isMonitoring();
                    
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
                        this.desktopPet.screenUsedAlert.resetTimer();
                        this.desktopPet.screenUsedAlert.startMonitoring();
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
    
    public void startWalking() {
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
            URL imageUrl = getClass().getResource(standImagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
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
            URL imageUrl = getClass().getResource(walkImagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
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
            URL imageUrl = getClass().getResource(fallImagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
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
            URL imageUrl = getClass().getResource(sitImagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
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
    public void sit() {
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
            URL imageUrl = getClass().getResource(lieImagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
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
    public void lie() {
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
    public void getUp() {
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
    public void cheer() {
        if (!isCheering && !isPopupMenuVisible) {
            // 停止所有動作
            stopAllActions();
            
            // 設定歡呼狀態
            isCheering = true;
            
            // 顯示歡呼圖片
            try {
                System.out.println("petType=" + petType + "，歡呼圖片路徑=" + cheerImagePath);
                System.out.println("當前工作目錄: " + System.getProperty("user.dir"));
                URL imageUrl = getClass().getResource(cheerImagePath);
                if (imageUrl != null) {
                    ImageIcon icon = new ImageIcon(imageUrl);
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
    public void cheerUp() {
        if (!isCheeringUp && !isPopupMenuVisible) {
            // 停止所有動作
            stopAllActions();
            
            // 設定加油狀態
            isCheeringUp = true;
            
            // 顯示加油圖片
            try {
                URL imageUrl = getClass().getResource(cheerUpImagePath);
                if (imageUrl != null) {
                    ImageIcon icon = new ImageIcon(imageUrl);
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
    public void stopCheering() {
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
                    URL imageUrl = getClass().getResource(cheerImagePath);
                    if (imageUrl != null) {
                        ImageIcon icon = new ImageIcon(imageUrl);
                        Image img = icon.getImage().getScaledInstance(SettingsWindow.globalPetSize, SettingsWindow.globalPetSize, Image.SCALE_SMOOTH);
                        petLabel.setIcon(new ImageIcon(img));
                    }
                } catch (Exception e) {
                    loadStandImage();
                }
            } else if (isCheeringUp) {
                // 重新載入加油圖片
                try {
                    URL imageUrl = getClass().getResource(cheerUpImagePath);
                    if (imageUrl != null) {
                        ImageIcon icon = new ImageIcon(imageUrl);
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
            URL imageUrl = getClass().getResource(standImagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
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
            URL imageUrl = getClass().getResource(standImagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
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
            URL imageUrl = getClass().getResource(standImagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
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
            URL imageUrl = getClass().getResource(standImagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
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
        return this.desktopPet.petWindows.indexOf(this);
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