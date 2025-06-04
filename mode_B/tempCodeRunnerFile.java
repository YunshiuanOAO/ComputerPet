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
            System.exit(1);
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - homeWidth - 20;
        int y = screenSize.height - homeHeight - 20;

        homeFrame = createHomeFrame(imgHome, x, y, homeWidth, homeHeight, true);
        
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
        wasCheering.add(false);
        wasDancing.add(false);
        wasDancing2.add(false);

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
        wasCheering.clear();
        wasDancing.clear();
        wasDancing2.clear();
    }

    private void setDogCount(int count) {
        if (count < 1) count = 1;
        if (count > 10) count = 10;
        
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
            for (int i = dogCount; i < count; i++) {
                createNewDog();
            }
        } else if (count < dogCount) {
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

        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenu menu1 = new JMenu("角色行動");
        JMenuItem subItem1 = new JMenuItem("全部回家");
        JMenuItem subItem2 = new JMenuItem("全部亂走");
        JMenuItem subItem3 = new JMenuItem("全部閃現");
        JMenuItem subItem4 = new JMenuItem("全部坐下");
        JMenuItem subItem5 = new JMenuItem("全部加油");
        JMenuItem subItem6 = new JMenuItem("全部跳舞");
        JMenuItem subItem7 = new JMenuItem("全部跳舞2");
        
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
        
        subItem5.addActionListener(e -> {
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
                wasCheering.set(i, true);
                wasMoving.set(i, false);
                wasTeleporting.set(i, false);
                wasSitting.set(i, false);
                dogLabels.get(i).setIcon(new ImageIcon(imgDogCheer.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
            }
        });
        
        subItem6.addActionListener(e -> {
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
                wasDancing.set(i, true);
                wasMoving.set(i, false);
                wasTeleporting.set(i, false);
                wasSitting.set(i, false);
                wasCheering.set(i, false);
                dogLabels.get(i).setIcon(new ImageIcon(gifDogDance.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
            }
        });
        
        subItem7.addActionListener(e -> {
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
                wasDancing2.set(i, true);
                wasMoving.set(i, false);
                wasTeleporting.set(i, false);
                wasSitting.set(i, false);
                wasCheering.set(i, false);
                wasDancing.set(i, false);
                dogLabels.get(i).setIcon(new ImageIcon(gifDogDance2.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
            }
        });
        
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

        exitItem.addActionListener(e -> {
            if (randomMoveTimers.get(0) != null && randomMoveTimers.get(0).isRunning()) {
                randomMoveTimers.get(0).stop();
            }
            if (teleportTimers.get(0) != null && teleportTimers.get(0).isRunning()) {
                teleportTimers.get(0).stop();
            }
            System.exit(0);
        });

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
        popupMenu.add(setDogCountItem);
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

        final Point[] mouseDownCompCoords = {null};
        final Timer[] animTimer = {null};

        JLabel dogLabel = new JLabel(new ImageIcon(imgDog.getScaledInstance(showWidth, showHeight, Image.SCALE_SMOOTH)));
        dogLabel.setOpaque(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(dogLabel, BorderLayout.CENTER);

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
        
        subItem1.addActionListener(e -> {
            if (randomMoveTimers.get(currentDogIndex) != null && randomMoveTimers.get(currentDogIndex).isRunning()) {
                randomMoveTimers.get(currentDogIndex).stop();
            }
            if (teleportTimers.get(currentDogIndex) != null && teleportTimers.get(currentDogIndex).isRunning()) {
                teleportTimers.get(currentDogIndex).stop();
            }
            if (transitionTimers.get(currentDogIndex) != null && transitionTimers.get(currentDogIndex).isRunning()) {
                transitionTimers.get(currentDogIndex).stop();
            }

            wasMoving.set(currentDogIndex, false);
            wasTeleporting.set(currentDogIndex, false);
            wasSitting.set(currentDogIndex, false);
            wasCheering.set(currentDogIndex, false);
            wasDancing.set(currentDogIndex, false);
            wasDancing2.set(currentDogIndex, false);

            dogLabel.setIcon(new ImageIcon(imgDog.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
            startTransitionAnimation(
                frame.getLocation(),
                new Point(homeFrame.getX(), homeFrame.getY()),
                true,
                currentDogIndex
            );
        });
        
        subItem2.addActionListener(e -> {
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
            startRandomMoveAnimation(currentDogIndex);
        });

        subItem3.addActionListener(e -> {
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
        
        subItem4.addActionListener(e -> {
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
        
        subItem5.addActionListener(e -> {
            if (randomMoveTimers.get(currentDogIndex) != null && randomMoveTimers.get(currentDogIndex).isRunning()) {
                randomMoveTimers.get(currentDogIndex).stop();
            }
            if (teleportTimers.get(currentDogIndex) != null && teleportTimers.get(currentDogIndex).isRunning()) {
                teleportTimers.get(currentDogIndex).stop();
            }
            if (transitionTimers.get(currentDogIndex) != null && transitionTimers.get(currentDogIndex).isRunning()) {
                transitionTimers.get(currentDogIndex).stop();
            }

            wasCheering.set(currentDogIndex, true);
            wasMoving.set(currentDogIndex, false);
            wasTeleporting.set(currentDogIndex, false);
            wasSitting.set(currentDogIndex, false);
            dogLabel.setIcon(new ImageIcon(imgDogCheer.getScaledInstance(dogWidth, dogHeight, Image.SCALE_SMOOTH)));
        });
        
        subItem6.addActionListener(e -> {
            if (randomMoveTimers.get(currentDogIndex) != null && randomMoveTimers.get(currentDogIndex).isRunning()) {
                randomMoveTimers.get(currentDogIndex).stop();
            }
            if (teleportTimers.get(currentDogIndex) != null && teleportTimers.get(currentDogIndex).isRunning()) {
                teleportTimers.get(currentDogIndex).stop();
            }
            if (transitionTimers.get(currentDogIndex) != null && transitionTimers.get(currentDogIndex).isRunning()) {
                transitionTimers.get(currentDogIndex).stop();
            }

            wasDancing.set(currentDogIndex, true);
            wasMoving.set(currentDogIndex, false);
            wasTeleporting.set(currentDogIndex, false);
            wasSitting.set(currentDogIndex, false);
            wasCheering.set(currentDogIndex, false);
            dogLabel.setIcon(new ImageIcon(gifDogDance.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
        });
        
        subItem7.addActionListener(e -> {
            if (randomMoveTimers.get(currentDogIndex) != null && randomMoveTimers.get(currentDogIndex).isRunning()) {
                randomMoveTimers.get(currentDogIndex).stop();
            }
            if (teleportTimers.get(currentDogIndex) != null && teleportTimers.get(currentDogIndex).isRunning()) {
                teleportTimers.get(currentDogIndex).stop();
            }
            if (transitionTimers.get(currentDogIndex) != null && transitionTimers.get(currentDogIndex).isRunning()) {
                transitionTimers.get(currentDogIndex).stop();
            }

            wasDancing2.set(currentDogIndex, true);
            wasMoving.set(currentDogIndex, false);
            wasTeleporting.set(currentDogIndex, false);
            wasSitting.set(currentDogIndex, false);
            wasCheering.set(currentDogIndex, false);
            wasDancing.set(currentDogIndex, false);
            dogLabel.setIcon(new ImageIcon(gifDogDance2.getImage().getScaledInstance(dogWidth, dogHeight, Image.SCALE_DEFAULT)));
        });
        
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

        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(panel, e.getX(), e.getY());
                } else {
                    mouseDownCompCoords[0] = e.getPoint();
                    isDragging = true;
                    if (animTimer[0] != null && animTimer[0].isRunning()) animTimer[0].stop();
                    frame.setAlwaysOnTop(true);
                    
                    if (randomMoveTimers.get(currentDogIndex) != null && randomMoveTimers.get(currentDogIndex).isRunning()) {
                        randomMoveTimers.get(currentDogIndex).stop();
                    }
                    if (teleportTimers.get(currentDogIndex) != null && teleportTimers.get(currentDogIndex).isRunning()) {
                        teleportTimers.get(currentDogIndex).stop();
                    }
                    if (transitionTimers.get(currentDogIndex) != null && transitionTimers.get(currentDogIndex).isRunning()) {
                        transitionTimers.get(currentDogIndex).stop();
                    }
                    
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
                    startTransitionAnimation(
                        now,
                        new Point(homePos.x, homePos.y),
                        true,
                        currentDogIndex
                    );
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

        wasMoving.set(dogIndex, true);
        wasTeleporting.set(dogIndex, false);
        wasSitting.set(dogIndex, false);
        wasCheering.set(dogIndex, false);
        wasDancing.set(dogIndex, false);
        wasDancing2.set(dogIndex, false);

        double initialAngle = random.nextDouble() * Math.PI * 2;
        currentSpeedXs.set(dogIndex, Math.cos(initialAngle) * MAX_SPEED * 0.7);
        currentSpeedYs.set(dogIndex, Math.sin(initialAngle) * MAX_SPEED * 0.7);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        randomMoveTimers.set(dogIndex, new Timer(16, e -> {
            if (random.nextDouble() < 0.15) {
                double angle = random.nextDouble() * Math.PI * 2;
                currentSpeedXs.set(dogIndex, currentSpeedXs.get(dogIndex) + Math.cos(angle) * ACCELERATION);
                currentSpeedYs.set(dogIndex, currentSpeedYs.get(dogIndex) + Math.sin(angle) * ACCELERATION);
            }

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

            currentSpeedXs.set(dogIndex, currentSpeedXs.get(dogIndex) * FRICTION);
            currentSpeedYs.set(dogIndex, currentSpeedYs.get(dogIndex) * FRICTION);

            int currentX = dogFrames.get(dogIndex).getX();
            int currentY = dogFrames.get(dogIndex).getY();
            int newX = currentX + (int)Math.round(currentSpeedXs.get(dogIndex));
            int newY = currentY + (int)Math.round(currentSpeedYs.get(dogIndex));

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

        wasTeleporting.set(dogIndex, true);
        wasMoving.set(dogIndex, false);
        wasSitting.set(dogIndex, false);
        wasCheering.set(dogIndex, false);
        wasDancing.set(dogIndex, false);
        wasDancing2.set(dogIndex, false);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        teleportTimers.set(dogIndex, new Timer(1000, e -> {
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
