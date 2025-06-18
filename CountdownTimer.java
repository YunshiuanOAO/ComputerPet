import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Timer;
import java.util.TimerTask;

public class CountdownTimer extends JFrame {
    private JLabel timerLabel;
    private JButton startButton;
    private JButton resetButton;
    private JTextField timeInputField;
    
    private Timer timer;
    private int secondsLeft;
    private boolean isRunning = false;
    
    private DesktopPet parentPet; // 參考到父寵物
    private Timer positionTimer; // 用於跟隨寵物位置
    private int followingDogIndex = -1; // 跟隨的角色索引（-1表示跟隨主屋）
    
    // 根據使用者要求的配色方案 (與 Stopwatch 統一)
    private final Color PRIMARY_LIGHT = new Color(0xFEB098); // #FEB098
    private final Color PRIMARY_COLOR = new Color(0xF26B49); // #F26B49
    private final Color PRIMARY_DARK = new Color(0xCC553A); // #CC553A
    private final Color SECONDARY_COLOR = new Color(0xF26B49); // 使用 primary 作為 secondary
    private final Color SUCCESS_COLOR = new Color(0xFEB098); // 使用 primary-light
    private final Color WARNING_COLOR = new Color(0xCC553A); // 使用 primary-dark
    private final Color DANGER_COLOR = new Color(0xCC553A); // 使用 primary-dark
    private final Color BACKGROUND_COLOR = new Color(0xFAFAF9); // stone-50
    private final Color TEXT_COLOR = new Color(0x374151); // neutral-700
    private final Color BORDER_COLOR = new Color(0xD4D4D8); // neutral-300
    
    public CountdownTimer() {
        this(null, -1);
    }
    
    public CountdownTimer(DesktopPet pet) {
        this(pet, -1);
    }
    
    public CountdownTimer(DesktopPet pet, int petIndex) {
        this.parentPet = pet;
        this.followingDogIndex = petIndex;
        
        setTitle("倒數計時器");
        setSize(340, 220);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        
        // 設定現代化的主要面板
        ModernCountdownPanel mainPanel = new ModernCountdownPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        
        // 標題面板
        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // 時間輸入面板
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        
        // Timer display
        timerLabel = new JLabel("00:00", JLabel.CENTER);
        timerLabel.setFont(new Font("SF Pro Display", Font.BOLD, 48));
        timerLabel.setForeground(PRIMARY_COLOR);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        timerLabel.setOpaque(false);
        
        // 控制面板
        JPanel controlPanel = createControlPanel();
        
        // 組合中央面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(timerLabel, BorderLayout.CENTER);
        centerPanel.add(controlPanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
        
        resetTimer();
        
        // 設定整個視窗為圓角
        SwingUtilities.invokeLater(() -> {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));
        });
        
        // 視窗大小改變時自動調整圓角
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));
            }
        });
        
        // 設置初始位置並開始位置跟隨
        if (parentPet != null) {
            setInitialPosition();
            startPositionTracking();
        }
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        // 標題標籤
        JLabel titleLabel = new JLabel("倒數計時器", JLabel.CENTER);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        // 隱藏按鈕
        JButton hideButton = createModernButton("×", 20, 20, 20);
        hideButton.setFont(new Font("Arial", Font.BOLD, 16));
        hideButton.setBackground(new Color(255, 255, 255, 100));
        hideButton.setForeground(new Color(100, 100, 100));
        hideButton.addActionListener(e -> setVisible(false));
        hideButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hideButton.setBackground(new Color(255, 0, 0, 150));
                hideButton.setForeground(Color.WHITE);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hideButton.setBackground(new Color(255, 255, 255, 100));
                hideButton.setForeground(new Color(100, 100, 100));
            }
        });
        
        JPanel hidePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        hidePanel.setOpaque(false);
        hidePanel.add(hideButton);
        
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(hidePanel, BorderLayout.EAST);
        
        return titlePanel;
    }
    
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        inputPanel.setOpaque(false);
        
        JLabel inputLabel = new JLabel("設定時間：");
        inputLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 12));
        inputLabel.setForeground(TEXT_COLOR);
        
        timeInputField = new JTextField("05:00", 6);
        timeInputField.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        timeInputField.setHorizontalAlignment(JTextField.CENTER);
        timeInputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        timeInputField.addActionListener(e -> updateTimerFromInput());
        
        JButton setButton = createModernButton("set", 50, 28, 15);
        setButton.setBackground(SECONDARY_COLOR);
        setButton.setForeground(Color.WHITE);
        setButton.addActionListener(e -> updateTimerFromInput());
        addButtonHoverEffect(setButton, SECONDARY_COLOR);
        
        inputPanel.add(inputLabel);
        inputPanel.add(timeInputField);
        inputPanel.add(setButton);
        
        return inputPanel;
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setOpaque(false);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        controlPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // 按鈕區
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        startButton = createModernButton("▶", 50, 50, 20);
        startButton.setBackground(PRIMARY_COLOR);
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(e -> toggleTimer());
        
        resetButton = createModernButton("⟳", 50, 50, 20);
        resetButton.setBackground(SECONDARY_COLOR);
        resetButton.setForeground(Color.WHITE);
        resetButton.addActionListener(e -> resetTimer());
        
        addButtonHoverEffect(startButton, PRIMARY_COLOR);
        addButtonHoverEffect(resetButton, SECONDARY_COLOR);
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        buttonPanel.add(resetButton);
        buttonPanel.add(Box.createHorizontalGlue());

        controlPanel.add(buttonPanel);
        return controlPanel;
    }
    
    private JButton createModernButton(String text, int width, int height, int iconSize) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 陰影
                g2.setColor(new Color(0,0,0,40));
                g2.fillRoundRect(3, 4, getWidth()-6, getHeight()-6, 20, 20);
                // 主體
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-3, getHeight()-3, 20, 20);
                
                // 繪製圖案
                String currentText = getText();
                g2.setColor(getForeground());
                
                if ("▶".equals(currentText)) {
                    // 自繪播放三角形
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    int triangleSize = iconSize / 2;
                    int[] xPoints = {centerX - triangleSize/2, centerX - triangleSize/2, centerX + triangleSize/2};
                    int[] yPoints = {centerY - triangleSize/2, centerY + triangleSize/2, centerY};
                    g2.fillPolygon(xPoints, yPoints, 3);
                } else if ("⏸".equals(currentText)) {
                    // 自繪暫停矩形
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    int rectSize = iconSize / 2;
                    int rectWidth = rectSize / 3;
                    int rectHeight = rectSize;
                    int gap = rectSize / 4;
                    g2.fillRect(centerX - gap - rectWidth, centerY - rectHeight/2, rectWidth, rectHeight);
                    g2.fillRect(centerX + gap, centerY - rectHeight/2, rectWidth, rectHeight);
                } else if (currentText != null && !currentText.isEmpty()) {
                    // 其他按鈕使用文字
                    g2.setFont(new Font("Arial", Font.BOLD, iconSize));
                    FontMetrics fm = g2.getFontMetrics();
                    int strWidth = fm.stringWidth(currentText);
                    int strHeight = fm.getAscent();
                    int x = (getWidth() - strWidth) / 2;
                    int y = (getHeight() + strHeight) / 2 - 4;
                    g2.drawString(currentText, x, y);
                }
                g2.dispose();
            }
        };
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setAlignmentY(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.BOLD, iconSize));
        button.setForeground(Color.WHITE);
        return button;
    }
    
    private void addButtonHoverEffect(JButton button, Color originalColor) {
        Color hoverColor = new Color(
            Math.min(255, originalColor.getRed() + 30),
            Math.min(255, originalColor.getGreen() + 30),
            Math.min(255, originalColor.getBlue() + 30)
        );
        Color pressedColor = new Color(
            Math.max(0, originalColor.getRed() - 30),
            Math.max(0, originalColor.getGreen() - 30),
            Math.max(0, originalColor.getBlue() - 30)
        );
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor);
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(pressedColor);
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(hoverColor);
            }
        });
    }
    
    private void updateTimerFromInput() {
        if (isRunning) {
            stopTimer();
            isRunning = false;
            startButton.setText("▶");
            startButton.setBackground(PRIMARY_COLOR);
        }
        
        try {
            String timeText = timeInputField.getText().trim();
            String[] parts = timeText.split(":");
            
            if (parts.length == 2) {
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                
                if (minutes >= 0 && minutes <= 99 && seconds >= 0 && seconds <= 59) {
                    secondsLeft = minutes * 60 + seconds;
                    updateTimerDisplay();
                } else {
                    showErrorMessage("請輸入有效的時間格式（分鐘:0-99，秒數:0-59）");
                }
            } else {
                showErrorMessage("請使用 MM:SS 格式（例如：05:00）");
            }
        } catch (NumberFormatException e) {
            showErrorMessage("請輸入有效的數字");
        }
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "輸入錯誤", JOptionPane.ERROR_MESSAGE);
    }
    
    private void toggleTimer() {
        if (isRunning) {
            stopTimer();
            startButton.setText("▶");
            startButton.setBackground(PRIMARY_COLOR);
        } else {
            if (secondsLeft > 0) {
                startTimer();
                startButton.setText("⏸");
                startButton.setBackground(WARNING_COLOR);
            } else {
                showErrorMessage("請先設定倒數時間");
            }
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
                    SwingUtilities.invokeLater(() -> {
                        startButton.setText("▶");
                        startButton.setBackground(PRIMARY_COLOR);
                        setVisible(true);
                        toFront();
                        showTimeUpNotification();
                    });
                }
            }
        }, 1000, 1000);
    }
    
    private void showTimeUpNotification() {
        // 創建自訂對話框
        JDialog dialog = new JDialog(this, "倒數計時完成", true);
        dialog.setSize(280, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DANGER_COLOR, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel iconLabel = new JLabel("", JLabel.CENTER); // 移除圖示
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 40));
        
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + 
            "倒數計時完成！<br>時間到了！" + "</div></html>", JLabel.CENTER);
        messageLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        messageLabel.setForeground(TEXT_COLOR);
        
        JButton okButton = createModernButton("確定", 80, 35, 20);
        okButton.setBackground(DANGER_COLOR);
        okButton.setForeground(Color.WHITE);
        okButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        
        panel.add(iconLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
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
        startButton.setBackground(PRIMARY_COLOR);
        
        // 從輸入框讀取預設時間，如果格式錯誤則使用5分鐘
        try {
            String timeText = timeInputField.getText().trim();
            String[] parts = timeText.split(":");
            if (parts.length == 2) {
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                if (minutes >= 0 && minutes <= 99 && seconds >= 0 && seconds <= 59) {
                    secondsLeft = minutes * 60 + seconds;
                } else {
                    secondsLeft = 5 * 60; // 預設5分鐘
                }
            } else {
                secondsLeft = 5 * 60; // 預設5分鐘
            }
        } catch (NumberFormatException e) {
            secondsLeft = 5 * 60; // 預設5分鐘
        }
        
        updateTimerDisplay();
    }
    
    private void updateTimerDisplay() {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;
        SwingUtilities.invokeLater(() -> {
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
            
            // 根據剩餘時間改變顏色
            if (secondsLeft <= 10 && secondsLeft > 0) {
                timerLabel.setForeground(DANGER_COLOR); // 紅色
            } else if (secondsLeft <= 60 && secondsLeft > 10) {
                timerLabel.setForeground(WARNING_COLOR); // 橙色
            } else {
                timerLabel.setForeground(PRIMARY_COLOR); // 紫色
            }
        });
    }
    
    private void setInitialPosition() {
        if (parentPet == null) return;
        
        Point followingLocation;
        boolean isVisible;
        Dimension petSize;
        
        // 根據followingDogIndex決定跟隨哪個對象
        if (followingDogIndex == -1) {
            // 跟隨石頭圖片
            followingLocation = parentPet.getStoneLocation();
            isVisible = parentPet.isStoneVisible();
            petSize = parentPet.getStoneSize();
        } else {
            // 跟隨特定寵物
            followingLocation = parentPet.getPetLocation(followingDogIndex);
            isVisible = parentPet.isPetVisible(followingDogIndex);
            petSize = parentPet.getPetSize(followingDogIndex);
        }
        
        if (isVisible && followingLocation != null) {
            // 獲取螢幕尺寸
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int screenWidth = screenSize.width;
            int screenHeight = screenSize.height;
            
            // 計算角色的中心位置
            int petCenterX = followingLocation.x + petSize.width / 2;
            int petCenterY = followingLocation.y + petSize.height / 2;
            
            // 計算倒數計時器視窗的理想位置（角色中心正上方）
            int idealX = petCenterX - getWidth() / 2;
            int idealY = followingLocation.y - getHeight() - 10;
            
            // 確保位置在螢幕範圍內
            int finalX = idealX;
            int finalY = idealY;
            
            // 水平方向邊界檢查
            if (idealX < 0) {
                finalX = followingLocation.x + petSize.width + 10;
                if (finalX + getWidth() > screenWidth) {
                    finalX = screenWidth - getWidth() - 5;
                }
            } else if (idealX + getWidth() > screenWidth) {
                finalX = followingLocation.x - getWidth() - 10;
                if (finalX < 0) {
                    finalX = 5;
                }
            }
            
            // 垂直方向邊界檢查
            if (idealY < 0) {
                finalY = followingLocation.y + petSize.height + 10;
                if (finalY + getHeight() > screenHeight) {
                    finalY = screenHeight - getHeight() - 40;
                }
            } else if (idealY + getHeight() > screenHeight) {
                finalY = screenHeight - getHeight() - 40;
            }
            
            // 最終邊界確保
            finalX = Math.max(5, Math.min(finalX, screenWidth - getWidth() - 5));
            finalY = Math.max(5, Math.min(finalY, screenHeight - getHeight() - 40));
            
            setLocation(finalX, finalY);
        } else {
            // 如果角色不可見，設置在螢幕中央
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (screenSize.width - getWidth()) / 2;
            int y = (screenSize.height - getHeight()) / 2;
            setLocation(x, y);
        }
    }
    
    private void startPositionTracking() {
        positionTimer = new Timer();
        positionTimer.scheduleAtFixedRate(new TimerTask() {
            private Point lastLocation = null;
            
            @Override
            public void run() {
                if (parentPet != null) {
                    SwingUtilities.invokeLater(() -> {
                        Point followingLocation;
                        boolean isVisible;
                        
                        // 根據followingDogIndex決定跟隨哪個對象
                        if (followingDogIndex == -1) {
                            // 跟隨石頭圖片
                            followingLocation = parentPet.getStoneLocation();
                            isVisible = parentPet.isStoneVisible();
                        } else {
                            // 跟隨特定寵物
                            followingLocation = parentPet.getPetLocation(followingDogIndex);
                            isVisible = parentPet.isPetVisible(followingDogIndex);
                        }
                        
                        if (isVisible && followingLocation != null) {
                            // 檢查位置是否改變，避免不必要的更新
                            if (lastLocation != null && 
                                lastLocation.x == followingLocation.x && 
                                lastLocation.y == followingLocation.y) {
                                return;
                            }
                            
                            lastLocation = new Point(followingLocation);
                            
                            // 獲取螢幕尺寸
                            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                            int screenWidth = screenSize.width;
                            int screenHeight = screenSize.height;
                            
                            // 獲取實際的角色尺寸
                            Dimension petSize;
                            if (followingDogIndex == -1) {
                                // 跟隨石頭時，獲取石頭大小
                                petSize = parentPet.getStoneSize();
                            } else {
                                // 跟隨寵物時，獲取寵物大小
                                petSize = parentPet.getPetSize(followingDogIndex);
                            }
                            
                            // 計算角色的中心位置
                            int petCenterX = followingLocation.x + petSize.width / 2;
                            int petCenterY = followingLocation.y + petSize.height / 2;
                            
                            // 計算倒數計時器視窗的理想位置（角色中心正上方）
                            int idealX = petCenterX - getWidth() / 2;
                            int idealY = followingLocation.y - getHeight() - 10;
                            
                            // 確保位置在螢幕範圍內
                            int finalX = idealX;
                            int finalY = idealY;
                            
                            // 水平方向邊界檢查
                            if (idealX < 0) {
                                // 如果左邊超出螢幕，移到角色右側
                                finalX = followingLocation.x + petSize.width + 10;
                                if (finalX + getWidth() > screenWidth) {
                                    // 如果右側也超出，則貼螢幕右邊
                                    finalX = screenWidth - getWidth() - 5;
                                }
                            } else if (idealX + getWidth() > screenWidth) {
                                // 如果右邊超出螢幕，移到角色左側
                                finalX = followingLocation.x - getWidth() - 10;
                                if (finalX < 0) {
                                    // 如果左側也超出，則貼螢幕左邊
                                    finalX = 5;
                                }
                            }
                            
                            // 垂直方向邊界檢查
                            if (idealY < 0) {
                                // 如果上方超出螢幕，移到角色下方
                                finalY = followingLocation.y + petSize.height + 10;
                                if (finalY + getHeight() > screenHeight) {
                                    // 如果下方也超出，則貼螢幕底部
                                    finalY = screenHeight - getHeight() - 40;
                                }
                            } else if (idealY + getHeight() > screenHeight) {
                                // 如果下方超出螢幕，貼螢幕底部
                                finalY = screenHeight - getHeight() - 40;
                            }
                            
                            // 最終邊界確保
                            finalX = Math.max(5, Math.min(finalX, screenWidth - getWidth() - 5));
                            finalY = Math.max(5, Math.min(finalY, screenHeight - getHeight() - 40));
                            
                            setLocation(finalX, finalY);
                        }
                    });
                }
            }
        }, 0, 100);
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

// 現代化對話框形狀的面板類別
class ModernCountdownPanel extends JPanel {
    public ModernCountdownPanel() {
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // 繪製陰影
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.fillRoundRect(4, 6, width-8, height-12, 25, 25);
        
        // 繪製白色背景 (與 Stopwatch 統一)
        GradientPaint gp = new GradientPaint(
            0, 0, Color.WHITE, // 純白色
            0, height, new Color(0xF8F9FA) // 淺灰白
        );
        g2d.setPaint(gp);
        g2d.fillRoundRect(0, 0, width-4, height-8, 25, 25);
        
        // 繪製對話框尾巴
        int[] xPoints = {width-70, width-35, width-55};
        int[] yPoints = {height-8, height-8, height+2};
        g2d.setPaint(gp);
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // 繪製邊框 (使用 neutral-300)
        g2d.setColor(new Color(0xD4D4D8)); // neutral-300
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(0, 0, width-4, height-8, 25, 25);
        g2d.drawPolygon(xPoints, yPoints, 3);
        
        // 添加一些裝飾性元素 (使用主要色系)
        g2d.setColor(new Color(242,107,73, 80)); // primary 半透明
        g2d.fillOval(width-30, 5, 8, 8);
        g2d.setColor(new Color(254,176,152,80)); // primary-light 半透明
        g2d.fillOval(width-45, 10, 6, 6);
        g2d.setColor(new Color(204,85,58, 80)); // primary-dark 半透明
        g2d.fillOval(width-60, 8, 4, 4);
    }
} 