package pomodoro;
import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;

import pet.DesktopPet;

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
    private int followingDogIndex = -1; // 跟隨的角色索引（-1表示跟隨主屋）
    
    private int cycleCount = 0; // 記錄已完成幾次工作-短休息循環
    
    // 根據使用者要求的配色方案 (與 Stopwatch 統一)
    private final Color PRIMARY_LIGHT = new Color(0xFEB098); // #FEB098
    private final Color PRIMARY_COLOR = new Color(0xF26B49); // #F26B49
    private final Color PRIMARY_DARK = new Color(0xCC553A); // #CC553A
    private final Color SECONDARY_COLOR = new Color(0xF26B49); // 使用 primary 作為 secondary
    private final Color SUCCESS_COLOR = new Color(0xFEB098); // 使用 primary-light
    private final Color WARNING_COLOR = new Color(0xCC553A); // 使用 primary-dark
    private final Color BACKGROUND_COLOR = new Color(0xFAFAF9); // stone-50
    private final Color TEXT_COLOR = new Color(0x374151); // neutral-700
    private final Color BORDER_COLOR = new Color(0xD4D4D8); // neutral-300
    
    public PomodoroApp() {
        this(null, -1);
    }
    
    public PomodoroApp(DesktopPet pet) {
        this(pet, -1);
    }
    
    public PomodoroApp(DesktopPet pet, int petIndex) {
        this.parentPet = pet;
        this.followingDogIndex = petIndex;
        
        setTitle("番茄鐘");
        setSize(320, 200); // 稍微增加大小以容納更好的設計
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true); // 移除視窗邊框
        setBackground(new Color(0, 0, 0, 0)); // 設定完全透明的背景
        
        // 設定現代化的主要面板
        ModernPomodoroPanel mainPanel = new ModernPomodoroPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        
        // 標題面板
        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Timer display
        timerLabel = new JLabel("25:00", JLabel.CENTER);
        timerLabel.setFont(new Font("SF Pro Display", Font.BOLD, 52));
        timerLabel.setForeground(PRIMARY_COLOR);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 添加數字動畫效果
        timerLabel.setOpaque(false);
        
        mainPanel.add(timerLabel, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        resetTimer();
        
        // 設定整個視窗為圓角 - 確保在設定內容後再設定形狀
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
        JLabel titleLabel = new JLabel("番茄專注時間", JLabel.CENTER);
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
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setOpaque(false);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        controlPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // 下拉選單
        String[] modes = {"工作 (25分)", "短休 (5分)", "長休 (15分)"};
        modeSelector = new JComboBox<>(modes);
        modeSelector.setFont(new Font("SF Pro Display", Font.PLAIN, 13));
        modeSelector.setForeground(Color.WHITE);
        modeSelector.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 30)); // 右側多留空間給箭頭
        modeSelector.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        modeSelector.setPreferredSize(new Dimension(180, 32)); // 稍微縮小寬度
        modeSelector.setMaximumSize(new Dimension(180, 32));
        modeSelector.setMinimumSize(new Dimension(180, 32));
        modeSelector.setPrototypeDisplayValue("工作");
        modeSelector.setAlignmentY(Component.CENTER_ALIGNMENT);
        modeSelector.addActionListener(e -> resetTimer());
        // 移除系統預設的選擇行為和背景
        modeSelector.setFocusable(false);
        modeSelector.setOpaque(false); // 設定為透明
        // 自訂渲染器
        modeSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(new Font("SF Pro Display", Font.PLAIN, 13));
                label.setOpaque(true);
                if (isSelected) {
                    label.setBackground(PRIMARY_COLOR); // 橘色背景
                    label.setForeground(Color.WHITE); // 白色文字
                } else {
                    label.setBackground(new Color(255, 255, 255, 240));
                    label.setForeground(TEXT_COLOR);
                }
                label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                return label;
            }
        });
        // 圓角外觀
        modeSelector.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("\u25BC"); // ▼
                button.setFont(new Font("Arial", Font.BOLD, 13));
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setForeground(new Color(120,120,120));
                return button;
            }
            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_COLOR); // 使用橘色常數
                g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 16, 16);
            }
            @Override
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                // 先繪製背景
                paintCurrentValueBackground(g, bounds, hasFocus);
                
                // 然後繪製文字
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                String currentValue = "";
                if (comboBox.getSelectedItem() != null) {
                    currentValue = comboBox.getSelectedItem().toString();
                }
                
                g2.setColor(Color.WHITE); // 白色文字
                g2.setFont(new Font("SF Pro Display", Font.PLAIN, 13));
                
                FontMetrics fm = g2.getFontMetrics();
                int textHeight = fm.getAscent();
                int textY = bounds.y + (bounds.height + textHeight) / 2 - 2;
                int textX = bounds.x + 10; // 左側留白
                
                g2.drawString(currentValue, textX, textY);
            }
            @Override
            protected ComboPopup createPopup() {
                ComboPopup popup = super.createPopup();
                JList<?> list = popup.getList();
                list.setSelectionBackground(PRIMARY_COLOR); // 橘色選擇背景
                list.setSelectionForeground(Color.WHITE); // 白色選擇文字
                return popup;
            }
        });

        // 按鈕區
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        startButton = createModernButton(utils.FontUtils.getPlaySymbol(), 48, 48, 18);
        startButton.setBackground(PRIMARY_COLOR);
        startButton.setForeground(Color.WHITE);
        startButton.setFont(utils.FontUtils.getUnicodeFont(Font.BOLD, 18)); // 使用跨平台字體
        startButton.addActionListener(e -> toggleTimer());
        resetButton = createModernButton(utils.FontUtils.getResetSymbol(), 48, 48, 18);
        resetButton.setBackground(SECONDARY_COLOR);
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(utils.FontUtils.getUnicodeFont(Font.BOLD, 20)); // 使用跨平台字體
        resetButton.addActionListener(e -> resetTimer());
        addButtonHoverEffect(startButton, PRIMARY_COLOR);
        addButtonHoverEffect(resetButton, SECONDARY_COLOR);
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(12, 0)));
        buttonPanel.add(resetButton);

        // 組合
        controlPanel.add(modeSelector);
        controlPanel.add(Box.createHorizontalStrut(8)); // 固定間距
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
                } else if (utils.FontUtils.getResetSymbol().equals(currentText)) {
                    // 自繪重置圓形箭頭
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    utils.FontUtils.drawResetIcon(g2, centerX, centerY, iconSize, getForeground());
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
    
    private void toggleTimer() {
        if (isRunning) {
            stopTimer();
            startButton.setText(utils.FontUtils.getPlaySymbol());
            startButton.setBackground(PRIMARY_COLOR);
        } else {
            startTimer();
            startButton.setText(utils.FontUtils.getPauseSymbol());
            startButton.setBackground(WARNING_COLOR);
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
                    startButton.setText(utils.FontUtils.getPlaySymbol());
                    startButton.setBackground(PRIMARY_COLOR);
                    setVisible(true);
                    toFront();
                    
                    // 顯示現代化通知
                    showModernNotification();
                    
                    // 自動切換到下一階段
                    autoSwitchToNextPhase();
                }
            }
        }, 1000, 1000);
    }
    
    private void showModernNotification() {
        int selectedMode = modeSelector.getSelectedIndex();
        String message = "";
        String icon = "";
        
        switch (selectedMode) {
            case 0:
                message = "工作時間結束！\n該休息一下了";
                icon = "";
                break;
            case 1:
                message = "短休息結束！\n準備繼續工作";
                icon = "";
                break;
            case 2:
                message = "長休息結束！\n開始新一輪工作";
                icon = "";
                break;
        }
        
        // 創建自訂對話框
        JDialog dialog = new JDialog(this, "番茄鐘提醒", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 40));
        
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + 
            message.replace("\n", "<br>") + "</div></html>", JLabel.CENTER);
        messageLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        messageLabel.setForeground(TEXT_COLOR);
        
        JButton okButton = createModernButton("OK", 80, 35, 20);
        okButton.setBackground(PRIMARY_COLOR);
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
    
    private void autoSwitchToNextPhase() {
        int selectedMode = modeSelector.getSelectedIndex();
        
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
        
        resetTimer();
        
        // 自動開始下一階段
        SwingUtilities.invokeLater(() -> {
            startTimer();
            isRunning = true;
            startButton.setText(utils.FontUtils.getPauseSymbol());
            startButton.setBackground(WARNING_COLOR);
        });
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
        startButton.setText(utils.FontUtils.getPlaySymbol());
        startButton.setBackground(PRIMARY_COLOR);
        
        int selectedMode = modeSelector.getSelectedIndex();
        switch (selectedMode) {
            case 0: // Work time
                secondsLeft = WORK_TIME;
                timerLabel.setForeground(PRIMARY_COLOR);
                break;
            case 1: // Short break
                secondsLeft = SHORT_BREAK;
                timerLabel.setForeground(PRIMARY_COLOR); // 改為使用主要橘色
                break;
            case 2: // Long break
                secondsLeft = LONG_BREAK;
                timerLabel.setForeground(PRIMARY_COLOR); // 改為使用主要橘色
                break;
        }
        updateTimerDisplay();
    }
    
    private void updateTimerDisplay() {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;
        SwingUtilities.invokeLater(() -> {
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
            
            // 添加緊急時間的視覺提示
            if (secondsLeft <= 60 && secondsLeft > 0) {
                timerLabel.setForeground(new Color(255, 69, 0)); // 橙紅色
            } else if (secondsLeft <= 10 && secondsLeft > 0) {
                timerLabel.setForeground(Color.RED); // 紅色
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
            
            // 計算番茄鐘視窗的理想位置（角色中心正上方）
            int idealX = petCenterX - getWidth() / 2;
            int idealY = followingLocation.y - getHeight() - 10;
            
            // 確保視窗不會超出螢幕邊界（簡單的邊界貼合）
            int finalX = idealX;
            int finalY = idealY;
            
            // 水平方向邊界檢查
            if (finalX + getWidth() > screenWidth) {
                finalX = screenWidth - getWidth(); // 右邊界對齊
            }
            if (finalX < 0) {
                finalX = 0; // 左邊界對齊
            }
            
            // 垂直方向邊界檢查
            if (finalY < 0) {
                finalY = followingLocation.y + petSize.height + 10; // 如果上方空間不足，放到下方
            }
            if (finalY + getHeight() > screenHeight) {
                finalY = screenHeight - getHeight(); // 底部邊界對齊
            }
            
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
            private Point lastLocation = null; // 記錄上次位置，避免重複更新
            
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
                                return; // 位置沒變，不需要更新
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
                            
                            // 計算番茄鐘視窗的理想位置（角色中心正上方）
                            int idealX = petCenterX - getWidth() / 2;
                            int idealY = followingLocation.y - getHeight() - 10;
                            
                            // 確保視窗不會超出螢幕邊界（簡單的邊界貼合）
                            int finalX = idealX;
                            int finalY = idealY;
                            
                            // 水平方向邊界檢查
                            if (finalX + getWidth() > screenWidth) {
                                finalX = screenWidth - getWidth(); // 右邊界對齊
                            }
                            if (finalX < 0) {
                                finalX = 0; // 左邊界對齊
                            }
                            
                            // 垂直方向邊界檢查
                            if (finalY < 0) {
                                finalY = followingLocation.y + petSize.height + 10; // 如果上方空間不足，放到下方
                            }
                            if (finalY + getHeight() > screenHeight) {
                                finalY = screenHeight - getHeight(); // 底部邊界對齊
                            }
                            
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
class ModernPomodoroPanel extends JPanel {
    public ModernPomodoroPanel() {
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 繪製帶陰影的圓角矩形背景
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 25, 25);
        
        g2d.setColor(new Color(0xFAFAF9));
        g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 25, 25);
        
        // 繪製邊框
        g2d.setColor(new Color(0xD4D4D8));
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 25, 25);
    }
} 