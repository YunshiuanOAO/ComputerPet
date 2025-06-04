import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javax.swing.Timer;
import java.util.Random;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class main {
    private JFrame modeSelectionFrame;
    private JButton modeAButton, modeBButton;
    private boolean currentMode = false; // false = A模式, true = B模式
    
    // A模式相關變數
    private List<PetWindow> petWindows = new ArrayList<>();
    private JFrame petSelectionFrame;
    private JCheckBox dogCheckBox, catCheckBox, duckCheckBox, mouseCheckBox;
    
    // B模式相關變數
    private BufferedImage imgHome, imgDog, imgDogSit, imgDogCatch, imgDogCheer;
    private ImageIcon gifDog, gifDogDance, gifDogDance2;
    private JFrame homeFrame;
    private List<JFrame> dogFrames = new ArrayList<>();
    private List<JLabel> dogLabels = new ArrayList<>();
    private int dogCount = 1;
    private boolean isDogFollowingHome = true;
    private Timer returnTimer = null;
    private Point mouseOffset = new Point();
    private boolean isDragging = false;
    private List<Timer> randomMoveTimers = new ArrayList<>();
    private List<Timer> teleportTimers = new ArrayList<>();
    private List<Timer> transitionTimers = new ArrayList<>();
    private Random random = new Random();
    private List<Double> currentSpeedXs = new ArrayList<>();
    private List<Double> currentSpeedYs = new ArrayList<>();
    private static final double MAX_SPEED = 8.0;
    private static final double ACCELERATION = 1.0;
    private static final double FRICTION = 0.98;
    private static final double MIN_SPEED = 3.0;
    private List<Boolean> wasMoving = new ArrayList<>();
    private List<Boolean> wasTeleporting = new ArrayList<>();
    private List<Boolean> wasSitting = new ArrayList<>();
    private List<Boolean> wasCheering = new ArrayList<>();
    private List<Boolean> wasDancing = new ArrayList<>();
    private List<Boolean> wasDancing2 = new ArrayList<>();
    private int homeWidth = 200;
    private int homeHeight = 200;
    private int dogWidth = 200;
    private int dogHeight = 200;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new main().initialize());
    }

    private void initialize() {
        createModeSelectionWindow();
    }

    private void createModeSelectionWindow() {
        modeSelectionFrame = new JFrame("模式選擇");
        modeSelectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        modeSelectionFrame.setSize(400, 300);
        modeSelectionFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 標題
        JLabel titleLabel = new JLabel("請選擇程式模式", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        // 按鈕面板
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        modeAButton = new JButton("A模式 - 多寵物選擇系統");
        modeBButton = new JButton("B模式 - 狗狗互動系統");

        modeAButton.setFont(new Font("Dialog", Font.PLAIN, 16));
        modeBButton.setFont(new Font("Dialog", Font.PLAIN, 16));
        modeAButton.setPreferredSize(new Dimension(300, 50));
        modeBButton.setPreferredSize(new Dimension(300, 50));

        modeAButton.addActionListener(e -> switchToModeA());
        modeBButton.addActionListener(e -> switchToModeB());

        buttonPanel.add(modeAButton);
        buttonPanel.add(modeBButton);

        // 底部面板
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton exitButton = new JButton("離開程式");
        exitButton.addActionListener(e -> System.exit(0));
        bottomPanel.add(exitButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        modeSelectionFrame.add(mainPanel);
        modeSelectionFrame.setVisible(true);
    }

    private void switchToModeA() {
        currentMode = false;
        modeSelectionFrame.setVisible(false);
        cleanupModeB();
        initializeModeA();
    }

    private void switchToModeB() {
        currentMode = true;
        modeSelectionFrame.setVisible(false);
        cleanupModeA();
        initializeModeB();
    }

    private void cleanupModeA() {
        if (petSelectionFrame != null) {
            petSelectionFrame.dispose();
        }
        for (PetWindow petWindow : petWindows) {
            petWindow.dispose();
        }
        petWindows.clear();
    }

    private void cleanupModeB() {
        // 停止所有計時器
        for (Timer timer : randomMoveTimers) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        }
        for (Timer timer : teleportTimers) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        }
        for (Timer timer : transitionTimers) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        }
        if (returnTimer != null && returnTimer.isRunning()) {
            returnTimer.stop();
        }

        if (homeFrame != null) {
            homeFrame.dispose();
        }
        for (JFrame frame : dogFrames) {
            frame.dispose();
        }
        dogFrames.clear();
        dogLabels.clear();
        randomMoveTimers.clear();
        teleportTimers.clear();
        transitionTimers.clear();
        currentSpeedXs.clear();
        currentSpeedYs.clear();
        wasMoving.clear();
        wasTeleporting.clear();
        wasSitting.clear();
        wasCheering.clear();
        wasDancing.clear();
        wasDancing2.clear();
        dogCount = 1;
    }

    // A模式初始化
    private void initializeModeA() {
        createPetSelectionGUI();
    }

    // B模式初始化
    private void initializeModeB() {
        try {
            imgHome = ImageIO.read(new File("home.jpg"));
            imgDog = ImageIO.read(new File("dog.png"));
            imgDogSit = ImageIO.read(new File("dogsit.jpg"));
            imgDogCatch = ImageIO.read(new File("dogcatch.png"));
            imgDogCheer = ImageIO.read(new File("dogcheer.png"));
            gifDog = new ImageIcon("dogmove.gif");
            gifDogDance = new ImageIcon("dogdance.gif");
            gifDogDance2 = new ImageIcon("dogdance2.gif");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "找不到圖片檔案，請確認檔案路徑！");
            returnToModeSelection();
            return;
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - homeWidth - 20;
        int y = screenSize.height - homeHeight - 20;

        homeFrame = createHomeFrame(imgHome, x, y, homeWidth, homeHeight, true);
        createNewDog();
    }

    private void returnToModeSelection() {
        if (currentMode) {
            cleanupModeB();
        } else {
            cleanupModeA();
        }
        modeSelectionFrame.setVisible(true);
    }

    // A模式 - 程式設定視窗類別
    class SettingsWindow {
        private JFrame settingsFrame;
        
        public SettingsWindow() {
            createSettingsWindow();
        }
        
        private void createSettingsWindow() {
            settingsFrame = new JFrame("程式設定");
            settingsFrame.setSize(300, 200);
            settingsFrame.setLocationRelativeTo(petSelectionFrame);
            settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            
            JLabel titleLabel = new JLabel("請選擇設定選項", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            
            JButton buttonA = new JButton("選項 A");
            JButton buttonB = new JButton("選項 B");
            JButton buttonC = new JButton("選項 C");
            
            Dimension buttonSize = new Dimension(80, 30);
            buttonA.setPreferredSize(buttonSize);
            buttonB.setPreferredSize(buttonSize);
            buttonC.setPreferredSize(buttonSize);
            
            buttonA.addActionListener(e -> handleSettingAction("選項 A"));
            buttonB.addActionListener(e -> handleSettingAction("選項 B"));
            buttonC.addActionListener(e -> handleSettingAction("選項 C"));
            
            buttonPanel.add(buttonA);
            buttonPanel.add(buttonB);
            buttonPanel.add(buttonC);
            
            JPanel closePanel = new JPanel(new FlowLayout());
            JButton closeButton = new JButton("關閉");
            closeButton.addActionListener(e -> settingsFrame.dispose());
            closePanel.add(closeButton);
            
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(buttonPanel, BorderLayout.CENTER);
            mainPanel.add(closePanel, BorderLayout.SOUTH);
            
            settingsFrame.add(mainPanel);
        }
        
        private void handleSettingAction(String option) {
            System.out.println("使用者選擇了: " + option);
            JOptionPane.showMessageDialog(settingsFrame, 
                "您選擇了 " + option + "\n此功能尚未實現", 
                "設定選項", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        public void show() {
            settingsFrame.setVisible(true);
        }
    }

    // A模式 - 寵物選擇GUI
    private void createPetSelectionGUI() {
        petSelectionFrame = new JFrame("A模式 - 角色選擇");
        petSelectionFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        petSelectionFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnToModeSelection();
            }
        });
        petSelectionFrame.setSize(600, 500);
        petSelectionFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // 角色選擇面板
        JPanel selectionPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel dogPanel = createPetPanel("狗狗", "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\dog_pic.png", "忠誠的夥伴");
        dogCheckBox = (JCheckBox) dogPanel.getComponent(2);
        selectionPanel.add(dogPanel);

        JPanel catPanel = createPetPanel("貓咪", "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\cat_pic.png", "優雅的朋友");
        catCheckBox = (JCheckBox) catPanel.getComponent(2);
        selectionPanel.add(catPanel);

        JPanel duckPanel = createPetPanel("鴨子", "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\duck_pic.jpg", "可愛的水鳥");
        duckCheckBox = (JCheckBox) duckPanel.getComponent(2);
        selectionPanel.add(duckPanel);

        JPanel mousePanel = createPetPanel("老鼠", "C:\\Users\\user\\Desktop\\ComputerPet\\woo\\picture\\mouse_pic.jpg", "機靈的小夥伴");
        mouseCheckBox = (JCheckBox) mousePanel.getComponent(2);
        selectionPanel.add(mousePanel);

        // 按鈕面板
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton settingsButton = new JButton("程式設定");
        JButton selectAllButton = new JButton("全部勾選");
        JButton deselectAllButton = new JButton("取消勾選");
        JButton confirmButton = new JButton("確定選擇");
        JButton hideButton = new JButton("隱藏角色");
        JButton backButton = new JButton("返回模式選擇");

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
        hideButton.addActionListener(e -> hidePets());
        backButton.addActionListener(e -> returnToModeSelection());

        buttonPanel.add(settingsButton);
        buttonPanel.add(selectAllButton);
        buttonPanel.add(deselectAllButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(hideButton);
        buttonPanel.add(backButton);

        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        petSelectionFrame.add(mainPanel);
        petSelectionFrame.setVisible(true);
    }

    // B模式相關方法 - 完整整合版本
    private JFrame createHomeFrame(BufferedImage img, int x, int y, int showWidth, int showHeight, boolean alwaysOnTop) {
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setAlwaysOnTop(alwaysOnTop);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, showWidth, showHeight, null);
            }
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(showWidth, showHeight);
            }
        };
        panel.setOpaque(false);

        final Point[] mouseDownCompCoords = {null};
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords[0] = e.getPoint();
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                frame.setLocation(currCoords.x - mouseDownCompCoords[0].x, currCoords.y - mouseDownCompCoords[0].y);
            }
        });

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                if (isDogFollowingHome) {
                    for (JFrame dogFrame : dogFrames) {
                        dogFrame.setLocation(frame.getX(), frame.getY());
                    }
                }
            }
        });

        // 創建右鍵選單 - 包含所有B模式功能
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenu menu1 = new JMenu("角色行動");
        JMenuItem subItem1 = new JMenuItem("全部回家");
        JMenuItem subItem2 = new JMenuItem("全部亂走");
        JMenuItem subItem3 = new JMenuItem("全部閃現");
        JMenuItem subItem4 = new JMenuItem("全部坐下");
        JMenuItem subItem5 = new JMenuItem("全部加油");
        JMenuItem subItem6 = new JMenuItem("全部跳舞");
        JMenuItem subItem7 = new JMenuItem("全部跳舞2");
        
        subItem1.addActionListener(e -> allDogsGoHome());
        subItem2.addActionListener(e -> allDogsRandomMove());
        subItem3.addActionListener(e -> allDogsTeleport());
        subItem4.addActionListener(e -> allDogsSit());
        subItem5.addActionListener(e -> allDogsCheer());
        subItem6.addActionListener(e -> allDogsDance1());
        subItem7.addActionListener(e -> allDogsDance2());
        
        menu1.add(subItem1);
        menu1.add(subItem2);
        menu1.add(subItem3);
        menu1.add(subItem4);
        menu1.add(subItem5);
        menu1.add(subItem6);
        menu1.add(subItem7);
        
        JMenuItem item2 = new JMenuItem("番茄鐘設定");
        JMenuItem item3 = new JMenuItem("螢幕使用時間提醒");
        JMenuItem item4 = new JMenuItem("待辦事項");
        JMenuItem item5 = new JMenuItem("聊天詢問");
        
        JMenuItem setDogCountItem = new JMenuItem("設定角色數量");
        setDogCountItem.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(
                frame,
                "請輸入角色數量 (1-10):",
                String.valueOf(dogCount)
            );
            try {
                int newCount = Integer.parseInt(input);
                setDogCount(newCount);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    frame,
                    "請輸入有效的數字！",
                    "錯誤",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        JMenuItem backToModeItem = new JMenuItem("返回模式選擇");
        backToModeItem.addActionListener(e -> returnToModeSelection());
        
        JMenuItem exitItem = new JMenuItem("離開");
        exitItem.addActionListener(e -> System.exit(0));

        popupMenu.add(menu1);
        popupMenu.add(item2);
        popupMenu.add(item3);
        popupMenu.add(item4);
        popupMenu.add(item5);
        popupMenu.addSeparator();
        popupMenu.add(setDogCountItem);
        popupMenu.add(backToModeItem);
        popupMenu.add(exitItem);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(panel, e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(panel, e.getX(), e.getY());
                }
            }
        });

        frame.setContentPane(panel);
        frame.pack();
        frame.setLocation(x, y);
        frame.setVisible(true);
        return frame;
    }

    // B模式狗狗行為方法 - 完整功能
    private void allDogsGoHome() {
        isDogFollowingHome = true;
        for (int i = 0; i < dogFrames.size(); i++) {
            stopDogMovement(i);
            dogLabels.get(i).setIcon(new ImageIcon(imgDog.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
            startTransitionAnimation(
                dogFrames.get(i).getLocation(),
                new Point(homeFrame.getX(), homeFrame.getY()),
                true,
                i
            );
        }
    }

    private void allDogsRandomMove() {
        isDogFollowingHome = false;
        for (int i = 0; i < dogFrames.size(); i++) {
            stopDogMovement(i);
            dogLabels.get(i).setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
            startRandomMoveAnimation(i);
        }
    }

    private void allDogsTeleport() {
        for (int i = 0; i < dogFrames.size(); i++) {
            stopDogMovement(i);
            dogLabels.get(i).setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
            startRandomTeleportAnimation(i);
        }
    }

    private void allDogsSit() {
        for (int i = 0; i < dogFrames.size(); i++) {
            stopDogMovement(i);
            dogLabels.get(i).setIcon(new ImageIcon(imgDogSit.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
            wasSitting.set(i, true);
            wasMoving.set(i, false);
            wasTeleporting.set(i, false);
            wasCheering.set(i, false);
            wasDancing.set(i, false);
            wasDancing2.set(i, false);
        }
    }

    private void allDogsCheer() {
        for (int i = 0; i < dogFrames.size(); i++) {
            stopDogMovement(i);
            dogLabels.get(i).setIcon(new ImageIcon(imgDogCheer.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
            wasCheering.set(i, true);
            wasMoving.set(i, false);
            wasTeleporting.set(i, false);
            wasSitting.set(i, false);
            wasDancing.set(i, false);
            wasDancing2.set(i, false);
        }
    }

    private void allDogsDance1() {
        for (int i = 0; i < dogFrames.size(); i++) {
            stopDogMovement(i);
            dogLabels.get(i).setIcon(new ImageIcon(gifDogDance.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
            wasDancing.set(i, true);
            wasMoving.set(i, false);
            wasTeleporting.set(i, false);
            wasSitting.set(i, false);
            wasCheering.set(i, false);
            wasDancing2.set(i, false);
        }
    }

    private void allDogsDance2() {
        for (int i = 0; i < dogFrames.size(); i++) {
            stopDogMovement(i);
            dogLabels.get(i).setIcon(new ImageIcon(gifDogDance2.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
            wasDancing2.set(i, true);
            wasMoving.set(i, false);
            wasTeleporting.set(i, false);
            wasSitting.set(i, false);
            wasCheering.set(i, false);
            wasDancing.set(i, false);
        }
    }

    private void createNewDog() {
        JFrame dogFrame = createDogFrame(dogWidth, dogHeight, false);
        dogFrames.add(dogFrame);
        JLabel dogLabel = (JLabel)((JPanel)dogFrame.getContentPane()).getComponent(0);
        dogLabels.add(dogLabel);
        randomMoveTimers.add(null);
        teleportTimers.add(null);
        transitionTimers.add(null);
        currentSpeedXs.add(0.0);
        currentSpeedYs.add(0.0);
        wasMoving.add(false);
        wasTeleporting.add(false);
        wasSitting.add(false);
        wasCheering.add(false);
        wasDancing.add(false);
        wasDancing2.add(false);

        // 檢查是否有正在運行的計時器，決定顯示的圖片
        boolean hasRunningTimer = false;
        for (Timer timer : randomMoveTimers) {
            if (timer != null && timer.isRunning()) {
                hasRunningTimer = true;
                break;
            }
        }

        if (hasRunningTimer) {
            dogLabel.setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
        } else {
            dogLabel.setIcon(new ImageIcon(imgDog.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
        }
    }

    private void setDogCount(int count) {
        if (count < 1) count = 1;
        if (count > 10) count = 10;
        
        // 停止所有計時器
        for (Timer timer : randomMoveTimers) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        }
        for (Timer timer : teleportTimers) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        }
        for (Timer timer : transitionTimers) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        }
        
        if (count > dogCount) {
            // 增加狗狗
            for (int i = dogCount; i < count; i++) {
                createNewDog();
            }
        } else if (count < dogCount) {
            // 減少狗狗
            for (int i = dogCount - 1; i >= count; i--) {
                dogFrames.get(i).dispose();
                dogFrames.remove(i);
                dogLabels.remove(i);
                randomMoveTimers.remove(i);
                teleportTimers.remove(i);
                transitionTimers.remove(i);
                currentSpeedXs.remove(i);
                currentSpeedYs.remove(i);
                wasMoving.remove(i);
                wasTeleporting.remove(i);
                wasSitting.remove(i);
                wasCheering.remove(i);
                wasDancing.remove(i);
                wasDancing2.remove(i);
            }
        }
        dogCount = count;
    }

    private JFrame createDogFrame(int showWidth, int showHeight, boolean alwaysOnTop) {
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setAlwaysOnTop(false);

        final Point[] mouseDownCompCoords = {null};
        final Timer[] animTimer = {null};

        JLabel dogLabel = new JLabel(new ImageIcon(imgDog.getScaledInstance(showWidth, showHeight, Image.SCALE_SMOOTH)));
        dogLabel.setOpaque(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(dogLabel, BorderLayout.CENTER);

        // 創建個別狗狗的右鍵選單
        JPopupMenu popupMenu = new JPopupMenu();
        JMenu menu1 = new JMenu("角色行動");
        JMenuItem subItem1 = new JMenuItem("回家");
        JMenuItem subItem2 = new JMenuItem("亂走");
        JMenuItem subItem3 = new JMenuItem("閃現");
        JMenuItem subItem4 = new JMenuItem("坐下");
        JMenuItem subItem5 = new JMenuItem("加油");
        JMenuItem subItem6 = new JMenuItem("跳舞");
        JMenuItem subItem7 = new JMenuItem("跳舞2");
        
        final int currentDogIndex = dogFrames.size();
        
        subItem1.addActionListener(e -> singleDogGoHome(currentDogIndex));
        subItem2.addActionListener(e -> singleDogRandomMove(currentDogIndex));
        subItem3.addActionListener(e -> singleDogTeleport(currentDogIndex));
        subItem4.addActionListener(e -> singleDogSit(currentDogIndex));
        subItem5.addActionListener(e -> singleDogCheer(currentDogIndex));
        subItem6.addActionListener(e -> singleDogDance1(currentDogIndex));
        subItem7.addActionListener(e -> singleDogDance2(currentDogIndex));
        
        menu1.add(subItem1);
        menu1.add(subItem2);
        menu1.add(subItem3);
        menu1.add(subItem4);
        menu1.add(subItem5);
        menu1.add(subItem6);
        menu1.add(subItem7);
        
        JMenuItem item2 = new JMenuItem("番茄鐘設定");
        JMenuItem item3 = new JMenuItem("螢幕使用時間提醒");
        JMenuItem item4 = new JMenuItem("待辦事項");
        JMenuItem item5 = new JMenuItem("聊天詢問");
        JMenuItem exitItem = new JMenuItem("離開");

        exitItem.addActionListener(e -> System.exit(0));

        popupMenu.add(menu1);
        popupMenu.add(item2);
        popupMenu.add(item3);
        popupMenu.add(item4);
        popupMenu.add(item5);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);

        // 滑鼠事件處理
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(panel, e.getX(), e.getY());
                } else {
                    mouseDownCompCoords[0] = e.getPoint();
                    isDragging = true;
                    if (animTimer[0] != null && animTimer[0].isRunning()) animTimer[0].stop();
                    frame.setAlwaysOnTop(true);
                    
                    stopDogMovement(currentDogIndex);
                    dogLabel.setIcon(new ImageIcon(imgDogCatch.getScaledInstance(showWidth, showHeight, Image.SCALE_SMOOTH)));
                }
            }
            
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(panel, e.getX(), e.getY());
                    return;
                }
                
                isDragging = false;
                Point now = frame.getLocation();
                Point homePos = homeFrame.getLocation();
                
                // 根據之前的狀態恢復
                if (wasMoving.get(currentDogIndex)) {
                    dogLabel.setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(showWidth, showHeight, Image.SCALE_DEFAULT)));
                    startRandomMoveAnimation(currentDogIndex);
                } else if (wasTeleporting.get(currentDogIndex)) {
                    dogLabel.setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(showWidth, showHeight, Image.SCALE_DEFAULT)));
                    startRandomTeleportAnimation(currentDogIndex);
                } else if (wasSitting.get(currentDogIndex)) {
                    dogLabel.setIcon(new ImageIcon(imgDogSit.getScaledInstance(showWidth, showHeight, Image.SCALE_SMOOTH)));
                } else if (wasCheering.get(currentDogIndex)) {
                    dogLabel.setIcon(new ImageIcon(imgDogCheer.getScaledInstance(showWidth, showHeight, Image.SCALE_SMOOTH)));
                } else if (wasDancing.get(currentDogIndex)) {
                    dogLabel.setIcon(new ImageIcon(gifDogDance.getImage().getScaledInstance(showWidth, showHeight, Image.SCALE_DEFAULT)));
                } else if (wasDancing2.get(currentDogIndex)) {
                    dogLabel.setIcon(new ImageIcon(gifDogDance2.getImage().getScaledInstance(showWidth, showHeight, Image.SCALE_DEFAULT)));
                } else if (isDogFollowingHome && (now.x != homePos.x || now.y != homePos.y)) {
                    dogLabel.setIcon(new ImageIcon(imgDog.getScaledInstance(showWidth, showHeight, Image.SCALE_SMOOTH)));
                    startTransitionAnimation(now, new Point(homePos.x, homePos.y), true, currentDogIndex);
                } else {
                    dogLabel.setIcon(new ImageIcon(imgDog.getScaledInstance(showWidth, showHeight, Image.SCALE_SMOOTH)));
                }
                
                frame.setAlwaysOnTop(false);
                homeFrame.setAlwaysOnTop(true);
                homeFrame.toFront();
            }
        });
        
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                frame.setLocation(currCoords.x - mouseDownCompCoords[0].x, currCoords.y - mouseDownCompCoords[0].y);
            }
        });

        frame.setContentPane(panel);
        frame.pack();
        frame.setLocation(homeFrame.getX(), homeFrame.getY());
        frame.setVisible(true);
        return frame;
    }

    // 個別狗狗行為控制方法
    private void singleDogGoHome(int dogIndex) {
        if (dogIndex >= dogFrames.size()) return;
        stopDogMovement(dogIndex);
        resetDogStates(dogIndex);
        dogLabels.get(dogIndex).setIcon(new ImageIcon(imgDog.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
        startTransitionAnimation(
            dogFrames.get(dogIndex).getLocation(),
            new Point(homeFrame.getX(), homeFrame.getY()),
            true,
            dogIndex
        );
    }

    private void singleDogRandomMove(int dogIndex) {
        if (dogIndex >= dogFrames.size()) return;
        stopDogMovement(dogIndex);
        resetDogStates(dogIndex);
        wasMoving.set(dogIndex, true);
        dogLabels.get(dogIndex).setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
        startRandomMoveAnimation(dogIndex);
    }

    private void singleDogTeleport(int dogIndex) {
        if (dogIndex >= dogFrames.size()) return;
        stopDogMovement(dogIndex);
        resetDogStates(dogIndex);
        wasTeleporting.set(dogIndex, true);
        dogLabels.get(dogIndex).setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
        startRandomTeleportAnimation(dogIndex);
    }

    private void singleDogSit(int dogIndex) {
        if (dogIndex >= dogFrames.size()) return;
        stopDogMovement(dogIndex);
        resetDogStates(dogIndex);
        wasSitting.set(dogIndex, true);
        dogLabels.get(dogIndex).setIcon(new ImageIcon(imgDogSit.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
    }

    private void singleDogCheer(int dogIndex) {
        if (dogIndex >= dogFrames.size()) return;
        stopDogMovement(dogIndex);
        resetDogStates(dogIndex);
        wasCheering.set(dogIndex, true);
        dogLabels.get(dogIndex).setIcon(new ImageIcon(imgDogCheer.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
    }

    private void singleDogDance1(int dogIndex) {
        if (dogIndex >= dogFrames.size()) return;
        stopDogMovement(dogIndex);
        resetDogStates(dogIndex);
        wasDancing.set(dogIndex, true);
        dogLabels.get(dogIndex).setIcon(new ImageIcon(gifDogDance.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
    }

    private void singleDogDance2(int dogIndex) {
        if (dogIndex >= dogFrames.size()) return;
        stopDogMovement(dogIndex);
        resetDogStates(dogIndex);
        wasDancing2.set(dogIndex, true);
        dogLabels.get(dogIndex).setIcon(new ImageIcon(gifDogDance2.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
    }

    private void resetDogStates(int dogIndex) {
        if (dogIndex >= wasMoving.size()) return;
        wasMoving.set(dogIndex, false);
        wasTeleporting.set(dogIndex, false);
        wasSitting.set(dogIndex, false);
        wasCheering.set(dogIndex, false);
        wasDancing.set(dogIndex, false);
        wasDancing2.set(dogIndex, false);
    }

    // 動畫控制方法
    private void startRandomMoveAnimation(int dogIndex) {
        if (dogIndex >= randomMoveTimers.size()) return;
        
        if (randomMoveTimers.get(dogIndex) != null && randomMoveTimers.get(dogIndex).isRunning()) {
            randomMoveTimers.get(dogIndex).stop();
        }

        wasMoving.set(dogIndex, true);
        wasTeleporting.set(dogIndex, false);
        wasSitting.set(dogIndex, false);
        wasCheering.set(dogIndex, false);
        wasDancing.set(dogIndex, false);
        wasDancing2.set(dogIndex, false);

        // 初始化隨機方向和速度
        double initialAngle = random.nextDouble() * Math.PI * 2;
        currentSpeedXs.set(dogIndex, Math.cos(initialAngle) * MAX_SPEED * 0.7);
        currentSpeedYs.set(dogIndex, Math.sin(initialAngle) * MAX_SPEED * 0.7);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        Timer moveTimer = new Timer(16, e -> {
            // 隨機改變方向
            if (random.nextDouble() < 0.15) {
                double angle = random.nextDouble() * Math.PI * 2;
                currentSpeedXs.set(dogIndex, currentSpeedXs.get(dogIndex) + Math.cos(angle) * ACCELERATION);
                currentSpeedYs.set(dogIndex, currentSpeedYs.get(dogIndex) + Math.sin(angle) * ACCELERATION);
            }

            // 限制最大速度
            double speed = Math.sqrt(
                currentSpeedXs.get(dogIndex) * currentSpeedXs.get(dogIndex) + 
                currentSpeedYs.get(dogIndex) * currentSpeedYs.get(dogIndex)
            );
            if (speed > MAX_SPEED) {
                currentSpeedXs.set(dogIndex, (currentSpeedXs.get(dogIndex) / speed) * MAX_SPEED);
                currentSpeedYs.set(dogIndex, (currentSpeedYs.get(dogIndex) / speed) * MAX_SPEED);
            }
            else if (speed < MIN_SPEED) {
                currentSpeedXs.set(dogIndex, (currentSpeedXs.get(dogIndex) / speed) * MIN_SPEED);
                currentSpeedYs.set(dogIndex, (currentSpeedYs.get(dogIndex) / speed) * MIN_SPEED);
            }

            // 應用摩擦力
            currentSpeedXs.set(dogIndex, currentSpeedXs.get(dogIndex) * FRICTION);
            currentSpeedYs.set(dogIndex, currentSpeedYs.get(dogIndex) * FRICTION);

            // 更新位置
            int currentX = dogFrames.get(dogIndex).getX();
            int currentY = dogFrames.get(dogIndex).getY();
            int newX = currentX + (int)Math.round(currentSpeedXs.get(dogIndex));
            int newY = currentY + (int)Math.round(currentSpeedYs.get(dogIndex));

            // 邊界檢測
            if (newX <= 0 || newX >= screenWidth - dogWidth) {
                currentSpeedXs.set(dogIndex, currentSpeedXs.get(dogIndex) * -0.9);
                newX = Math.max(0, Math.min(newX, screenWidth - dogWidth));
            }
            if (newY <= 0 || newY >= screenHeight - dogHeight) {
                currentSpeedYs.set(dogIndex, currentSpeedYs.get(dogIndex) * -0.9);
                newY = Math.max(0, Math.min(newY, screenHeight - dogHeight));
            }

            dogFrames.get(dogIndex).setLocation(newX, newY);
        });
        
        randomMoveTimers.set(dogIndex, moveTimer);
        moveTimer.start();
    }

    private void startRandomTeleportAnimation(int dogIndex) {
        if (dogIndex >= teleportTimers.size()) return;
        
        if (teleportTimers.get(dogIndex) != null && teleportTimers.get(dogIndex).isRunning()) {
            teleportTimers.get(dogIndex).stop();
        }

        wasTeleporting.set(dogIndex, true);
        wasMoving.set(dogIndex, false);
        wasSitting.set(dogIndex, false);
        wasCheering.set(dogIndex, false);
        wasDancing.set(dogIndex, false);
        wasDancing2.set(dogIndex, false);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        Timer teleportTimer = new Timer(1000, e -> {
            int newX = random.nextInt(screenWidth - dogWidth);
            int newY = random.nextInt(screenHeight - dogHeight);
            dogFrames.get(dogIndex).setLocation(newX, newY);
        });
        
        teleportTimers.set(dogIndex, teleportTimer);
        teleportTimer.start();
    }

    private void startTransitionAnimation(Point from, Point to, boolean isReturningHome, int dogIndex) {
        if (dogIndex >= transitionTimers.size()) return;
        
        if (transitionTimers.get(dogIndex) != null && transitionTimers.get(dogIndex).isRunning()) {
            transitionTimers.get(dogIndex).stop();
        }

        if (isReturningHome) {
            dogLabels.get(dogIndex).setIcon(new ImageIcon(imgDog.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
        }

        final int duration = 500;
        final int fps = 60;
        final long startTime = System.currentTimeMillis();
        final int startX = from.x;
        final int startY = from.y;
        final int deltaX = to.x - from.x;
        final int deltaY = to.y - from.y;

        Timer transitionTimer = new Timer(1000 / fps, e -> {
            float progress = Math.min(1.0f, (System.currentTimeMillis() - startTime) / (float) duration);
            float eased = (float)(1 - Math.pow(1 - progress, 3));
            int nowX = startX + Math.round(deltaX * eased);
            int nowY = startY + Math.round(deltaY * eased);
            dogFrames.get(dogIndex).setLocation(nowX, nowY);

            if (progress >= 1.0f) {
                transitionTimers.get(dogIndex).stop();
                if (isReturningHome) {
                    isDogFollowingHome = true;
                }
            }
        });
        
        transitionTimers.set(dogIndex, transitionTimer);
        transitionTimer.start();
    }

    private void stopDogMovement(int dogIndex) {
        if (dogIndex < randomMoveTimers.size() && randomMoveTimers.get(dogIndex) != null) {
            randomMoveTimers.get(dogIndex).stop();
        }
        if (dogIndex < teleportTimers.size() && teleportTimers.get(dogIndex) != null) {
            teleportTimers.get(dogIndex).stop();
        }
        if (dogIndex < transitionTimers.size() && transitionTimers.get(dogIndex) != null) {
            transitionTimers.get(dogIndex).stop();
        }
    }

    // A模式輔助方法
    private JPanel createPetPanel(String name, String imagePath, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(name));

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

        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        JCheckBox checkBox = new JCheckBox();
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(descLabel, BorderLayout.SOUTH);
        panel.add(checkBox, BorderLayout.NORTH);

        return panel;
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

    private boolean isPetExists(String petType) {
        for (PetWindow petWindow : petWindows) {
            if (petWindow.getPetType().equals(petType)) {
                return true;
            }
        }
        return false;
    }

    private void hidePets() {
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
    }

    // A模式 - PetWindow類別（完整功能版本）
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
        int moveSpeed;
        JPopupMenu popupMenu;
        
        public PetWindow(String standPath, String walkPath, String fallPath, int x, int y, String type) {
            this.standImagePath = standPath;
            this.walkImagePath = walkPath;
            this.fallImagePath = fallPath;
            this.currentX = x;
            this.currentY = y;
            this.petType = type;
            this.moveSpeed = setMoveSpeed(type);
            
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            this.groundLevel = screenSize.height - 200 - 40;
            
            createWindow();
            createPopupMenu();
            setupWalkingAnimation();
        }
        
        private int setMoveSpeed(String petType) {
            switch (petType) {
                case "dog": return 6;
                case "cat": return 5;
                case "duck": return 4;
                case "mouse": return 7;
                default: return 5;
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
            
            final Point[] mouseDownCompCoords = {null};
            
            window.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        pauseForPopupMenu();
                        showPopupMenu(e);
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        if (!isPopupMenuVisible) {
                            mouseDownCompCoords[0] = e.getPoint();
                            stopWalking();
                        }
                    }
                }
                
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && !isPopupMenuVisible) {
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
                        
                        if (currentY < groundLevel && !isFalling) {
                            loadFallImage();
                        }
                    }
                }
            });
        }
        
        private void pauseMovement(int milliseconds) {
            isPaused = true;
            loadStandImage();
            
            if (pauseTimer != null && pauseTimer.isRunning()) {
                pauseTimer.stop();
            }
            
            pauseTimer = new Timer(milliseconds, e -> {
                isPaused = false;
                if (isWalking && !isFalling && !isPopupMenuVisible) {
                    loadWalkImage();
                }
                pauseTimer.stop();
            });
            pauseTimer.setRepeats(false);
            pauseTimer.start();
        }
        
        private void pauseForPopupMenu() {
            wasWalkingBeforePopup = isWalking;
            wasFallingBeforePopup = isFalling;
            
            if (walkTimer.isRunning()) walkTimer.stop();
            if (directionTimer.isRunning()) directionTimer.stop();
            if (fallTimer.isRunning()) fallTimer.stop();
            if (pauseTimer != null && pauseTimer.isRunning()) pauseTimer.stop();
            
            isWalking = false;
            isFalling = false;
            isPaused = false;
            
            loadStandImage();
        }
        
        private void resumeFromPopupMenu() {
            if (!isPopupMenuVisible) {
                if (wasFallingBeforePopup || currentY < groundLevel) {
                    startFalling();
                } else if (wasWalkingBeforePopup) {
                    startWalking();
                } else {
                    startWalking();
                }
                
                wasWalkingBeforePopup = false;
                wasFallingBeforePopup = false;
            }
        }
        
        private void createPopupMenu() {
            popupMenu = new JPopupMenu();
            
            popupMenu.addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    isPopupMenuVisible = true;
                }
                
                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    isPopupMenuVisible = false;
                    SwingUtilities.invokeLater(() -> resumeFromPopupMenu());
                }
                
                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {
                    isPopupMenuVisible = false;
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
        }
        
        private void setupWalkingAnimation() {
            walkTimer = new Timer(50, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!isPaused && isWalking && !isFalling && !isPopupMenuVisible) {
                        moveHorizontally();
                        if (Math.random() < 0.005) {
                            pauseMovement(500);
                        }
                        if (Math.random() < 0.01) {
                            pauseMovement(100);
                        }
                        if (Math.random() < 0.01) {
                            pauseMovement(2000);
                        }
                    }
                }
            });
            
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
                isPaused = false;
                loadWalkImage();
                walkTimer.start();
                directionTimer.start();
            }
        }
        
        private void stopWalking() {
            if (isWalking) {
                isWalking = false;
                isPaused = false;
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
                isPaused = false;
                walkTimer.stop();
                directionTimer.stop();
                if (pauseTimer != null && pauseTimer.isRunning()) {
                    pauseTimer.stop();
                }
                loadFallImage();
                fallTimer.start();
            }
        }
        
        private void stopFalling() {
            if (isFalling) {
                isFalling = false;
                fallTimer.stop();
                currentY = groundLevel;
                window.setLocation(currentX, currentY);
                
                if (!isPopupMenuVisible) {
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
                    loadStandImage();
                }
            }
        }
        
        private void fall() {
            currentY += 8;
            
            if (currentY >= groundLevel) {
                currentY = groundLevel;
                window.setLocation(currentX, currentY);
                stopFalling();
            } else {
                window.setLocation(currentX, currentY);
            }
        }
        
        private void moveHorizontally() {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            
            currentX += direction * moveSpeed;
            
            if (currentX <= 0) {
                currentX = 0;
                direction = 1;
            } else if (currentX >= screenSize.width - 200) {
                currentX = screenSize.width - 200;
                direction = -1;
            }
            
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
}
