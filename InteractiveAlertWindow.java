import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class InteractiveAlertWindow extends JWindow {
    private JLabel messageLabel;
    private JLabel timeLabel;
    private JButton resetButton;
    private JButton snoozeButton;
    private JButton dismissButton;
    private Timer animationTimer;
    private Timer countdownTimer;
    private int snoozeCountdown = 30; // 30秒倒數
    private boolean isSnoozing = false;
    
    private AlertActionCallback callback;
    
    public interface AlertActionCallback {
        void onReset();
        void onSnooze(int minutes);
        void onDismiss();
    }
    
    public InteractiveAlertWindow(AlertActionCallback callback) {
        this.callback = callback;
        initializeComponents();
        setupWindow();
        startPulseAnimation();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // 主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(255, 255, 255, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 標題
        JLabel titleLabel = new JLabel("⏰ 健康提醒", JLabel.CENTER);
        titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 80, 120));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 主要訊息
        messageLabel = new JLabel("<html><div style='text-align: center;'>" +
                                "您已經連續使用電腦超過1小時了！<br><br>" +
                                "為了保護您的健康，建議您：<br>" +
                                "• 起身活動一下<br>" +
                                "• 看看遠方放鬆眼睛<br>" +
                                "• 喝點水休息片刻" +
                                "</div></html>", JLabel.CENTER);
        messageLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        messageLabel.setForeground(new Color(60, 60, 60));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 時間顯示
        timeLabel = new JLabel("", JLabel.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timeLabel.setForeground(new Color(255, 100, 100));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 按鈕面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        
        // 重置按鈕
        resetButton = createStyledButton("🔄 重置計時器", new Color(100, 200, 100));
        resetButton.addActionListener(e -> {
            if (callback != null) callback.onReset();
            closeWindow();
        });
        
        // 稍後提醒按鈕
        snoozeButton = createStyledButton("⏰ 30分鐘後提醒", new Color(255, 180, 100));
        snoozeButton.addActionListener(e -> {
            if (callback != null) callback.onSnooze(30);
            closeWindow();
        });
        
        // 關閉提醒按鈕
        dismissButton = createStyledButton("❌ 關閉提醒", new Color(255, 120, 120));
        dismissButton.addActionListener(e -> {
            if (callback != null) callback.onDismiss();
            closeWindow();
        });
        
        buttonPanel.add(resetButton);
        buttonPanel.add(snoozeButton);
        buttonPanel.add(dismissButton);
        
        // 添加組件到主面板
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(timeLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);
        
        add(mainPanel);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 35));
        
        // 添加懸停效果
        button.addMouseListener(new MouseAdapter() {
            Color originalColor = bgColor;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(originalColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
    
    private void setupWindow() {
        setSize(400, 300);
        setAlwaysOnTop(true);
        
        // 居中顯示
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);
        
        // 設定圓角
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        
        // 點擊外部關閉
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    closeWindow();
                }
            }
        });
        
        // ESC鍵關閉
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    closeWindow();
                }
            }
        });
        
        setFocusable(true);
        requestFocus();
    }
    
    private void startPulseAnimation() {
        animationTimer = new Timer(1000, new ActionListener() {
            private boolean growing = true;
            private float opacity = 0.9f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (growing) {
                    opacity += 0.1f;
                    if (opacity >= 1.0f) {
                        opacity = 1.0f;
                        growing = false;
                    }
                } else {
                    opacity -= 0.1f;
                    if (opacity <= 0.7f) {
                        opacity = 0.7f;
                        growing = true;
                    }
                }
                
                setOpacity(opacity);
            }
        });
        animationTimer.start();
    }
    
    public void startAutoCloseCountdown() {
        isSnoozing = true;
        snoozeCountdown = 30;
        updateTimeLabel();
        
        countdownTimer = new Timer(1000, e -> {
            snoozeCountdown--;
            updateTimeLabel();
            
            if (snoozeCountdown <= 0) {
                if (callback != null) callback.onSnooze(30);
                closeWindow();
            }
        });
        countdownTimer.start();
    }
    
    private void updateTimeLabel() {
        if (isSnoozing) {
            timeLabel.setText("將在 " + snoozeCountdown + " 秒後自動選擇「30分鐘後提醒」");
        } else {
            timeLabel.setText("");
        }
    }
    
    public void showWindow() {
        setVisible(true);
        toFront();
        requestFocus();
        
        // 5秒後開始倒數
        Timer delayTimer = new Timer(5000, e -> {
            startAutoCloseCountdown();
            ((Timer)e.getSource()).stop();
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }
    
    private void closeWindow() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
        }
        setVisible(false);
        dispose();
    }
    
    // 檢查視窗是否正在顯示
    public boolean isShowing() {
        return isVisible() && isDisplayable();
    }
} 