import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Timer;
import java.util.TimerTask;

public class PomodoroApp extends JFrame {
    private JLabel timerLabel;
    private JButton startButton;
    private JButton resetButton;
    private JComboBox<String> modeSelector;
    
    private Timer timer;
    private int secondsLeft;
    private boolean isRunning = false;
    
    private final int WORK_TIME = 25 * 60; // 25 minutes in seconds
    private final int SHORT_BREAK = 5 * 60; // 5 minutes in seconds
    private final int LONG_BREAK = 15 * 60; // 15 minutes in seconds
    
    private DesktopPet parentPet; // 參考到父寵物
    private Timer positionTimer; // 用於跟隨寵物位置
    
    private int cycleCount = 0; // 記錄已完成幾次工作-短休息循環
    
    public PomodoroApp() {
        this(null);
    }
    
    public PomodoroApp(DesktopPet pet) {
        this.parentPet = pet;
        
        setTitle("番茄鐘");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true); // 移除視窗邊框
        
        // 設定對話框樣式的面板
        SpeechBubblePanel mainPanel = new SpeechBubblePanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(255, 255, 255, 240)); // 半透明白色背景
        
        // Timer display
        timerLabel = new JLabel("25:00", JLabel.CENTER);
        timerLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 60));
        timerLabel.setForeground(new Color(220, 80, 120));
        mainPanel.add(timerLabel, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel();
        
        // Mode selector
        String[] modes = {"工作時間 (25分鐘)", "短休息 (5分鐘)", "長休息 (15分鐘)"};
        modeSelector = new JComboBox<>(modes);
        modeSelector.addActionListener(e -> resetTimer());
        
        // Buttons
        startButton = new JButton("▶");
        startButton.setFont(new Font("Arial", Font.BOLD, 28));
        startButton.setBackground(new Color(255, 180, 200));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        startButton.setOpaque(true);
        startButton.setBorderPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(e -> toggleTimer());
        startButton.setPreferredSize(new Dimension(60, 60));
        startButton.setContentAreaFilled(false);
        startButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        // 圓形
        startButton.setBorder(BorderFactory.createLineBorder(new Color(220, 120, 180), 2, true));
        
        resetButton = new JButton("⟳");
        resetButton.setFont(new Font("Arial", Font.BOLD, 28));
        resetButton.setBackground(new Color(200, 220, 255));
        resetButton.setForeground(new Color(120, 120, 220));
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        resetButton.setOpaque(true);
        resetButton.setBorderPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.setPreferredSize(new Dimension(60, 60));
        resetButton.setContentAreaFilled(false);
        resetButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        resetButton.setBorder(BorderFactory.createLineBorder(new Color(120, 180, 220), 2, true));
        
        controlPanel.add(modeSelector);
        controlPanel.add(startButton);
        controlPanel.add(resetButton);
        
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // 隱藏按鈕
        JButton hideButton = new JButton("👁");
        hideButton.setFont(new Font("Arial", Font.PLAIN, 18));
        hideButton.setBackground(new Color(255,255,255,0));
        hideButton.setBorderPainted(false);
        hideButton.setFocusPainted(false);
        hideButton.setContentAreaFilled(false);
        hideButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hideButton.addActionListener(e -> setVisible(false));
        JPanel hidePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        hidePanel.setOpaque(false);
        hidePanel.add(hideButton);
        mainPanel.add(hidePanel, BorderLayout.NORTH);
        
        add(mainPanel);
        resetTimer();
        
        // 如果有父寵物，設定位置跟隨
        if (parentPet != null) {
            startPositionTracking();
        }
        // 設定整個視窗為圓角
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 40, 40));
        // 視窗大小改變時自動調整圓角
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 40, 40));
            }
        });
        
        resetButton.addActionListener(e -> resetTimer());
    }
    
    private void toggleTimer() {
        if (isRunning) {
            stopTimer();
            startButton.setText("▶");
        } else {
            startTimer();
            startButton.setText("■");
        }
        isRunning = !isRunning;
    }
    
    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (secondsLeft > 0) {
                    secondsLeft--;
                    updateTimerDisplay();
                } else {
                    stopTimer();
                    isRunning = false;
                    startButton.setText("▶");
                    setVisible(true);
                    toFront();
                    // 先顯示通知，等使用者關掉後再切換狀態
                    int selectedMode = modeSelector.getSelectedIndex();
                    String message = "時間到了！";
                    if (selectedMode == 0) message = "工作結束，休息一下！";
                    else if (selectedMode == 1) message = "休息結束，開始工作！";
                    else if (selectedMode == 2) message = "長休息結束，開始新一輪工作！";
                    JOptionPane.showMessageDialog(PomodoroApp.this, message, "番茄鐘", JOptionPane.INFORMATION_MESSAGE);
                    // 通知關閉後才切換狀態
                    if (selectedMode == 0) { // 工作結束
                        cycleCount++;
                        if (cycleCount < 3) {
                            modeSelector.setSelectedIndex(1); // 短休息
                        } else {
                            modeSelector.setSelectedIndex(2); // 長休息
                        }
                    } else if (selectedMode == 1) { // 短休息結束
                        modeSelector.setSelectedIndex(0); // 工作
                    } else if (selectedMode == 2) { // 長休息結束
                        cycleCount = 0;
                        modeSelector.setSelectedIndex(0); // 工作
                    }
                    resetTimer(); // 只重設，不自動開始
                }
            }
        }, 1, 1);
    }
    
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    private void resetTimer() {
        stopTimer();
        isRunning = false;
        startButton.setText("▶");
        int selectedMode = modeSelector.getSelectedIndex();
        switch (selectedMode) {
            case 0: // Work time
                secondsLeft = WORK_TIME;
                break;
            case 1: // Short break
                secondsLeft = SHORT_BREAK;
                break;
            case 2: // Long break
                secondsLeft = LONG_BREAK;
                break;
        }
        updateTimerDisplay();
    }
    
    private void updateTimerDisplay() {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;
        SwingUtilities.invokeLater(() -> {
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        });
    }
    
    private void startPositionTracking() {
        positionTimer = new Timer();
        positionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (parentPet != null && parentPet.isVisible()) {
                    SwingUtilities.invokeLater(() -> {
                        Point petLocation = parentPet.getLocation();
                        // 將對話框定位在寵物的左上角，和 DesktopPet 點擊時一致
                        setLocation(petLocation.x - getWidth() - 100, petLocation.y - getHeight() - 100);
                    });
                }
            }
        }, 0, 50); // 每50ms更新一次位置
    }
    
    @Override
    public void dispose() {
        if (positionTimer != null) {
            positionTimer.cancel();
        }
        if (timer != null) {
            timer.cancel();
        }
        super.dispose();
    }
}

// 對話框形狀的面板類別
class SpeechBubblePanel extends JPanel {
    public SpeechBubblePanel() {
        setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        // 陰影
        g2d.setColor(new Color(200, 150, 200, 80));
        g2d.fillRoundRect(8, 18, width-16, height-28, 40, 40);
        // 漸層背景
        GradientPaint gp = new GradientPaint(0, 0, new Color(255, 200, 220, 230), 0, height, new Color(255,255,255,230));
        g2d.setPaint(gp);
        g2d.fillRoundRect(0, 10, width-16, height-28, 40, 40);
        // 泡泡尾巴
        int[] xPoints = {width-60, width-30, width-50};
        int[] yPoints = {height-18, height-18, height-2};
        g2d.setPaint(gp);
        g2d.fillPolygon(xPoints, yPoints, 3);
        // 邊框
        g2d.setColor(new Color(220, 120, 180));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(0, 10, width-16, height-28, 40, 40);
        g2d.drawPolygon(xPoints, yPoints, 3);
    }
} 