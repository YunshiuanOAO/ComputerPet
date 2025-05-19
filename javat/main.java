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

public class main {
    private BufferedImage imgHome, imgDog, imgDogSit, imgDogCatch;
    private ImageIcon gifDog;
    private JFrame homeFrame; // home 視窗
    private List<JFrame> dogFrames = new ArrayList<>();  // 存储多个dog窗口
    private List<JLabel> dogLabels = new ArrayList<>();  // 存储多个dog标签
    private int dogCount = 1;  // 默认一只dog

    // 狀態控制
    private boolean isDogFollowingHome = true; // 是否跟隨 home
    private Timer returnTimer = null;      // 返回動畫計時器
    private Point mouseOffset = new Point(); // 保存鼠标点击位置与窗口左上角的偏移量
    private boolean isDragging = false; // 标记是否正在拖动

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

    // 状态记录变量
    private List<Boolean> wasMoving = new ArrayList<>();
    private List<Boolean> wasTeleporting = new ArrayList<>();
    private List<Boolean> wasSitting = new ArrayList<>();

    // 視窗大小
    private int homeWidth = 200;
    private int homeHeight = 200;
    private int dogWidth = 200;
    private int dogHeight = 200;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new main().initialize());
    }

    private void initialize() {
        try {
            imgHome = ImageIO.read(new File("home.jpg"));
            imgDog = ImageIO.read(new File("dog.png"));
            imgDogSit = ImageIO.read(new File("dogsit.jpg"));
            imgDogCatch = ImageIO.read(new File("dogcatch.png"));
            gifDog = new ImageIcon("dogmove.gif");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "找不到圖片檔案，請確認檔案路徑！");
            System.exit(1);
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - homeWidth) / 2;
        int y = (screenSize.height - homeHeight) / 2;

        // 建立 home 視窗
        homeFrame = createHomeFrame(imgHome, x, y, homeWidth, homeHeight, true);
        
        // 建立初始的dog
        createNewDog();
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

        // 检查是否有正在运行的计时器
        boolean hasRunningTimer = false;
        for (Timer timer : randomMoveTimers) {
            if (timer != null && timer.isRunning()) {
                hasRunningTimer = true;
                break;
            }
        }

        // 如果有正在运行的计时器，新创建的狗也应该使用动画图标
        if (hasRunningTimer) {
            dogLabel.setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
        } else {
            dogLabel.setIcon(new ImageIcon(imgDog.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
        }
    }

    private void removeAllDogs() {
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
    }

    private void setDogCount(int count) {
        if (count < 1) count = 1;
        if (count > 10) count = 10;  // 限制最大数量为10只
        
        // 停止所有现有的计时器
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
            // 增加dog
            for (int i = dogCount; i < count; i++) {
                createNewDog();
            }
        } else if (count < dogCount) {
            // 减少dog
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
            }
        }
        dogCount = count;
    }

    private JFrame createHomeFrame(BufferedImage img, int x, int y, int showWidth, int showHeight, boolean alwaysOnTop) {
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        // 拖曳功能
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

        // 新增 ComponentListener 監聽 home 移動
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                // 只有跟隨模式才移動 dog
                if (isDogFollowingHome) {
                    for (JFrame dogFrame : dogFrames) {
                        dogFrame.setLocation(frame.getX(), frame.getY());
                    }
                }
            }
        });

        // 右鍵選單
        JPopupMenu popupMenu = new JPopupMenu();
        
        // 创建功能1的子菜单
        JMenu menu1 = new JMenu("角色行動");
        JMenuItem subItem1 = new JMenuItem("回家");
        JMenuItem subItem2 = new JMenuItem("亂走");
        JMenuItem subItem3 = new JMenuItem("閃現");
        JMenuItem subItem4 = new JMenuItem("坐下");
        JMenuItem subItem5 = new JMenuItem("1.5");
        
        // 添加1.1的点击事件
        subItem1.addActionListener(e -> {
            for (int i = 0; i < dogCount; i++) {
                if (randomMoveTimers.get(i) != null && randomMoveTimers.get(i).isRunning()) {
                    randomMoveTimers.get(i).stop();
                }
                if (teleportTimers.get(i) != null && teleportTimers.get(i).isRunning()) {
                    teleportTimers.get(i).stop();
                }
                dogLabels.get(i).setIcon(new ImageIcon(imgDog.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
                startTransitionAnimation(
                    dogFrames.get(i).getLocation(),
                    new Point(homeFrame.getX(), homeFrame.getY()),
                    true,
                    i
                );
            }
        });
        
        // 添加1.2的点击事件
        subItem2.addActionListener(e -> {
            for (int i = 0; i < dogCount; i++) {
                if (randomMoveTimers.get(i) != null && randomMoveTimers.get(i).isRunning()) {
                    randomMoveTimers.get(i).stop();
                }
                if (teleportTimers.get(i) != null && teleportTimers.get(i).isRunning()) {
                    teleportTimers.get(i).stop();
                }
            }
            isDogFollowingHome = false;
            for (int i = 0; i < dogCount; i++) {
                dogLabels.get(i).setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
                startRandomMoveAnimation(i);
            }
        });

        // 添加1.3的点击事件
        subItem3.addActionListener(e -> {
            for (int i = 0; i < dogCount; i++) {
                if (randomMoveTimers.get(i) != null && randomMoveTimers.get(i).isRunning()) {
                    randomMoveTimers.get(i).stop();
                }
                if (teleportTimers.get(i) != null && teleportTimers.get(i).isRunning()) {
                    teleportTimers.get(i).stop();
                }
                if (transitionTimers.get(i) != null && transitionTimers.get(i).isRunning()) {
                    transitionTimers.get(i).stop();
                }
                dogLabels.get(i).setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
                startRandomTeleportAnimation(i);
            }
        });
        
        // 添加1.4的点击事件
        subItem4.addActionListener(e -> {
            for (int i = 0; i < dogCount; i++) {
                if (randomMoveTimers.get(i) != null && randomMoveTimers.get(i).isRunning()) {
                    randomMoveTimers.get(i).stop();
                }
                if (teleportTimers.get(i) != null && teleportTimers.get(i).isRunning()) {
                    teleportTimers.get(i).stop();
                }
                if (transitionTimers.get(i) != null && transitionTimers.get(i).isRunning()) {
                    transitionTimers.get(i).stop();
                }
                dogLabels.get(i).setIcon(new ImageIcon(imgDogSit.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
            }
        });
        
        menu1.add(subItem1);
        menu1.add(subItem2);
        menu1.add(subItem3);
        menu1.add(subItem4);
        menu1.add(subItem5);
        
        JMenuItem item2 = new JMenuItem("番茄鐘設定");
        JMenuItem item3 = new JMenuItem("螢幕使用時間提醒");
        JMenuItem item4 = new JMenuItem("待辦事項");
        JMenuItem item5 = new JMenuItem("聊天詢問");
        JMenuItem exitItem = new JMenuItem("離開");

        // 添加离开功能
        exitItem.addActionListener(e -> {
            if (randomMoveTimers.get(0) != null && randomMoveTimers.get(0).isRunning()) {
                randomMoveTimers.get(0).stop();
            }
            if (teleportTimers.get(0) != null && teleportTimers.get(0).isRunning()) {
                teleportTimers.get(0).stop();
            }
            System.exit(0);
        });

        // 在菜单中添加设置dog数量的选项
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

        popupMenu.add(menu1);
        popupMenu.add(item2);
        popupMenu.add(item3);
        popupMenu.add(item4);
        popupMenu.add(item5);
        popupMenu.addSeparator();
        popupMenu.add(setDogCountItem);  // 添加设置数量的选项
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

    private JFrame createDogFrame(int showWidth, int showHeight, boolean alwaysOnTop) {
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setAlwaysOnTop(false);

        // 狀態控制變數
        final Point[] mouseDownCompCoords = {null};
        final Timer[] animTimer = {null};

        JLabel dogLabel = new JLabel(new ImageIcon(imgDog.getScaledInstance(showWidth, showHeight, Image.SCALE_SMOOTH)));
        dogLabel.setOpaque(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(dogLabel, BorderLayout.CENTER);

        // 添加右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        JMenu menu1 = new JMenu("角色行動");
        JMenuItem subItem1 = new JMenuItem("回家");
        JMenuItem subItem2 = new JMenuItem("亂走");
        JMenuItem subItem3 = new JMenuItem("閃現");
        JMenuItem subItem4 = new JMenuItem("1.4");
        JMenuItem subItem5 = new JMenuItem("1.5");
        
        // 获取当前狗的索引
        final int currentDogIndex = dogFrames.size();
        
        // 添加1.1的点击事件（只对当前狗生效）
        subItem1.addActionListener(e -> {
            // 停止当前狗的所有计时器
            if (randomMoveTimers.get(currentDogIndex) != null && randomMoveTimers.get(currentDogIndex).isRunning()) {
                randomMoveTimers.get(currentDogIndex).stop();
            }
            if (teleportTimers.get(currentDogIndex) != null && teleportTimers.get(currentDogIndex).isRunning()) {
                teleportTimers.get(currentDogIndex).stop();
            }
            if (transitionTimers.get(currentDogIndex) != null && transitionTimers.get(currentDogIndex).isRunning()) {
                transitionTimers.get(currentDogIndex).stop();
            }

            dogLabel.setIcon(new ImageIcon(imgDog.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
            startTransitionAnimation(
                frame.getLocation(),
                new Point(homeFrame.getX(), homeFrame.getY()),
                true,
                currentDogIndex
            );
        });
        
        // 添加1.2的点击事件（只对当前狗生效）
        subItem2.addActionListener(e -> {
            // 停止当前狗的所有计时器
            if (randomMoveTimers.get(currentDogIndex) != null && randomMoveTimers.get(currentDogIndex).isRunning()) {
                randomMoveTimers.get(currentDogIndex).stop();
            }
            if (teleportTimers.get(currentDogIndex) != null && teleportTimers.get(currentDogIndex).isRunning()) {
                teleportTimers.get(currentDogIndex).stop();
            }
            if (transitionTimers.get(currentDogIndex) != null && transitionTimers.get(currentDogIndex).isRunning()) {
                transitionTimers.get(currentDogIndex).stop();
            }

            wasMoving.set(currentDogIndex, true);
            wasTeleporting.set(currentDogIndex, false);
            wasSitting.set(currentDogIndex, false);
            dogLabel.setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
            // 直接从当前位置开始随机移动
            startRandomMoveAnimation(currentDogIndex);
        });

        // 添加1.3的点击事件（只对当前狗生效）
        subItem3.addActionListener(e -> {
            // 停止当前狗的所有计时器
            if (randomMoveTimers.get(currentDogIndex) != null && randomMoveTimers.get(currentDogIndex).isRunning()) {
                randomMoveTimers.get(currentDogIndex).stop();
            }
            if (teleportTimers.get(currentDogIndex) != null && teleportTimers.get(currentDogIndex).isRunning()) {
                teleportTimers.get(currentDogIndex).stop();
            }
            if (transitionTimers.get(currentDogIndex) != null && transitionTimers.get(currentDogIndex).isRunning()) {
                transitionTimers.get(currentDogIndex).stop();
            }

            wasTeleporting.set(currentDogIndex, true);
            wasMoving.set(currentDogIndex, false);
            wasSitting.set(currentDogIndex, false);
            dogLabel.setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
            startRandomTeleportAnimation(currentDogIndex);
        });
        
        // 添加1.4的点击事件（只对当前狗生效）
        subItem4.addActionListener(e -> {
            // 停止当前狗的所有计时器
            if (randomMoveTimers.get(currentDogIndex) != null && randomMoveTimers.get(currentDogIndex).isRunning()) {
                randomMoveTimers.get(currentDogIndex).stop();
            }
            if (teleportTimers.get(currentDogIndex) != null && teleportTimers.get(currentDogIndex).isRunning()) {
                teleportTimers.get(currentDogIndex).stop();
            }
            if (transitionTimers.get(currentDogIndex) != null && transitionTimers.get(currentDogIndex).isRunning()) {
                transitionTimers.get(currentDogIndex).stop();
            }

            wasSitting.set(currentDogIndex, true);
            wasMoving.set(currentDogIndex, false);
            wasTeleporting.set(currentDogIndex, false);
            dogLabel.setIcon(new ImageIcon(imgDogSit.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
        });
        
        menu1.add(subItem1);
        menu1.add(subItem2);
        menu1.add(subItem3);
        menu1.add(subItem4);
        menu1.add(subItem5);
        
        JMenuItem item2 = new JMenuItem("番茄鐘設定");
        JMenuItem item3 = new JMenuItem("螢幕使用時間提醒");
        JMenuItem item4 = new JMenuItem("待辦事項");
        JMenuItem item5 = new JMenuItem("聊天詢問");
        JMenuItem exitItem = new JMenuItem("離開");

        // 添加离开功能
        exitItem.addActionListener(e -> {
            System.exit(0);
        });

        popupMenu.add(menu1);
        popupMenu.add(item2);
        popupMenu.add(item3);
        popupMenu.add(item4);
        popupMenu.add(item5);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);

        // 滑鼠監聽：拖曳開始
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(panel, e.getX(), e.getY());
                } else {
                    mouseDownCompCoords[0] = e.getPoint();
                    isDragging = true;
                    if (animTimer[0] != null && animTimer[0].isRunning()) animTimer[0].stop();
                    frame.setAlwaysOnTop(true);
                    
                    // 停止当前狗的所有计时器
                    if (randomMoveTimers.get(currentDogIndex) != null && randomMoveTimers.get(currentDogIndex).isRunning()) {
                        randomMoveTimers.get(currentDogIndex).stop();
                    }
                    if (teleportTimers.get(currentDogIndex) != null && teleportTimers.get(currentDogIndex).isRunning()) {
                        teleportTimers.get(currentDogIndex).stop();
                    }
                    if (transitionTimers.get(currentDogIndex) != null && transitionTimers.get(currentDogIndex).isRunning()) {
                        transitionTimers.get(currentDogIndex).stop();
                    }
                    
                    // 开始拖动时显示被抓住的图片
                    dogLabel.setIcon(new ImageIcon(imgDogCatch.getScaledInstance(showWidth, showHeight, Image.SCALE_SMOOTH)));
                }
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(panel, e.getX(), e.getY());
                } else {
                    isDragging = false;
                    Point now = frame.getLocation();
                    Point homePos = homeFrame.getLocation();
                    
                    // 如果是乱走状态，直接恢复乱走
                    if (wasMoving.get(currentDogIndex)) {
                        dogLabel.setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(showWidth, showHeight, Image.SCALE_DEFAULT)));
                        startRandomMoveAnimation(currentDogIndex);
                    }
                    // 如果是闪现状态，直接恢复闪现
                    else if (wasTeleporting.get(currentDogIndex)) {
                        dogLabel.setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(showWidth, showHeight, Image.SCALE_DEFAULT)));
                        startRandomTeleportAnimation(currentDogIndex);
                    }
                    // 如果是坐下状态，直接恢复坐下
                    else if (wasSitting.get(currentDogIndex)) {
                        dogLabel.setIcon(new ImageIcon(imgDogSit.getScaledInstance(showWidth, showHeight, Image.SCALE_SMOOTH)));
                    }
                    // 其他情况（包括跟随模式）才回家
                    else if (isDogFollowingHome && (now.x != homePos.x || now.y != homePos.y)) {
                        dogLabel.setIcon(new ImageIcon(gifDog.getImage().getScaledInstance(showWidth, showHeight, Image.SCALE_DEFAULT)));
                        startMoveBackAnimation(frame, dogLabel, now.x, now.y, homePos.x, homePos.y, showWidth, showHeight, animTimer);
                    } else {
                        dogLabel.setIcon(new ImageIcon(imgDog.getScaledInstance(showWidth, showHeight, Image.SCALE_SMOOTH)));
                    }
                    frame.setAlwaysOnTop(false);
                    homeFrame.setAlwaysOnTop(true);
                    homeFrame.toFront();
                }
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
        frame.setLocation(homeFrame.getX(), homeFrame.getY()); // 初始位置與home同步
        frame.setVisible(true);
        return frame;
    }

    private void startMoveBackAnimation(JFrame frame, JLabel label, int fromX, int fromY, int toX, int toY, int width, int height, Timer[] animTimer) {
        final int duration = 1500;
        final int fps = 60;
        final long startTime = System.currentTimeMillis();
        final int startX = fromX;
        final int startY = fromY;
        final int deltaX = toX - fromX;
        final int deltaY = toY - fromY;

        animTimer[0] = new Timer(1000 / fps, e -> {
            float progress = Math.min(1.0f, (System.currentTimeMillis() - startTime) / (float) duration);
            float eased = (float)(1 - Math.pow(1 - progress, 3));
            int nowX = startX + Math.round(deltaX * eased);
            int nowY = startY + Math.round(deltaY * eased);
            frame.setLocation(nowX, nowY);

            if (progress >= 1.0f) {
                animTimer[0].stop();
                label.setIcon(new ImageIcon(imgDog.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
                frame.setAlwaysOnTop(false);
                homeFrame.setAlwaysOnTop(true);
                homeFrame.toFront();
            }
        });
        animTimer[0].start();
    }

    private void startRandomMoveAnimation(int dogIndex) {
        if (randomMoveTimers.get(dogIndex) != null && randomMoveTimers.get(dogIndex).isRunning()) {
            randomMoveTimers.get(dogIndex).stop();
        }

        // 为这只狗设置独立的初始速度
        double initialAngle = random.nextDouble() * Math.PI * 2;
        currentSpeedXs.set(dogIndex, Math.cos(initialAngle) * MAX_SPEED * 0.7);
        currentSpeedYs.set(dogIndex, Math.sin(initialAngle) * MAX_SPEED * 0.7);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // 为这只狗创建独立的计时器
        randomMoveTimers.set(dogIndex, new Timer(16, e -> {
            // 随机改变加速度（独立改变）
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

            // 应用摩擦力
            currentSpeedXs.set(dogIndex, currentSpeedXs.get(dogIndex) * FRICTION);
            currentSpeedYs.set(dogIndex, currentSpeedYs.get(dogIndex) * FRICTION);

            // 计算新位置
            int currentX = dogFrames.get(dogIndex).getX();
            int currentY = dogFrames.get(dogIndex).getY();
            int newX = currentX + (int)Math.round(currentSpeedXs.get(dogIndex));
            int newY = currentY + (int)Math.round(currentSpeedYs.get(dogIndex));

            // 碰到边界时反弹
            if (newX <= 0 || newX >= screenWidth - dogWidth) {
                currentSpeedXs.set(dogIndex, currentSpeedXs.get(dogIndex) * -0.9);
                newX = Math.max(0, Math.min(newX, screenWidth - dogWidth));
            }
            if (newY <= 0 || newY >= screenHeight - dogHeight) {
                currentSpeedYs.set(dogIndex, currentSpeedYs.get(dogIndex) * -0.9);
                newY = Math.max(0, Math.min(newY, screenHeight - dogHeight));
            }

            dogFrames.get(dogIndex).setLocation(newX, newY);
        }));
        
        randomMoveTimers.get(dogIndex).start();
    }

    private void startRandomTeleportAnimation(int dogIndex) {
        if (teleportTimers.get(dogIndex) != null && teleportTimers.get(dogIndex).isRunning()) {
            teleportTimers.get(dogIndex).stop();
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // 为这只狗创建独立的计时器
        teleportTimers.set(dogIndex, new Timer(1000, e -> {
            // 每只狗独立闪现到随机位置
            int newX = random.nextInt(screenWidth - dogWidth);
            int newY = random.nextInt(screenHeight - dogHeight);
            dogFrames.get(dogIndex).setLocation(newX, newY);
        }));
        
        teleportTimers.get(dogIndex).start();
    }

    private void startTransitionAnimation(Point from, Point to, boolean isReturningHome, int dogIndex) {
        if (transitionTimers.get(dogIndex) != null && transitionTimers.get(dogIndex).isRunning()) {
            transitionTimers.get(dogIndex).stop();
        }

        // 如果是回家，使用静态图片
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

        // 为这只狗创建独立的过渡动画
        transitionTimers.set(dogIndex, new Timer(1000 / fps, e -> {
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
        }));
        transitionTimers.get(dogIndex).start();
    }
}
