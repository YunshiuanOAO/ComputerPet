import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Timer;
import java.util.TimerTask;

public class Stopwatch extends JFrame {
    private JLabel timerLabel;
    private JButton startButton;
    private JButton resetButton;
    
    private Timer timer;
    private long startTime;
    private long elapsedTime = 0;
    private boolean isRunning = false;
    
    private DesktopPet parentPet; // 參考到父寵物
    private Timer positionTimer; // 用於跟隨寵物位置
    private int followingDogIndex = -1; // 跟隨的角色索引（-1表示跟隨主屋）
    
    // 根據使用者要求的配色方案
    private final Color PRIMARY_LIGHT = new Color(0xFEB098); // #FEB098
    private final Color PRIMARY_COLOR = new Color(0xF26B49); // #F26B49
    private final Color PRIMARY_DARK = new Color(0xCC553A); // #CC553A (用戶要求 #CC5A3，假設是 #CC553A)
    private final Color BACKGROUND_COLOR = new Color(0xFAFAF9); // stone-50
    private final Color BACKGROUND_DARKER = new Color(0xE2E8F0); // slate-200
    private final Color BORDER_COLOR = new Color(0xD4D4D8); // neutral-300 / slate-300
    private final Color TEXT_COLOR = new Color(0x374151); // neutral-700
    private final Color TEXT_DARKER = new Color(0x1F2937); // neutral-800
    
    public Stopwatch() {
        this(null, -1);
    }
    
    public Stopwatch(DesktopPet pet) {
        this(pet, -1);
    }
    
    public Stopwatch(DesktopPet pet, int petIndex) {
        this.parentPet = pet;
        this.followingDogIndex = petIndex;
        
        setTitle("碼錶");
        setSize(320, 180);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        
        // 設定現代化的主要面板
        ModernStopwatchPanel mainPanel = new ModernStopwatchPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        
        // 標題面板
        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Timer display
        timerLabel = new JLabel("00:00:00", JLabel.CENTER);
        timerLabel.setFont(new Font("SF Pro Display", Font.BOLD, 42));
        timerLabel.setForeground(PRIMARY_COLOR);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        timerLabel.setOpaque(false);
        // 優化渲染性能
        timerLabel.setDoubleBuffered(true);
        timerLabel.putClientProperty("html.disable", Boolean.TRUE); // 禁用HTML渲染以提升性能
        
        mainPanel.add(timerLabel, BorderLayout.CENTER);
        
        // 控制面板
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // 如果有父寵物，設定位置跟隨
        if (parentPet != null) {
            startPositionTracking();
        }
        
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
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        // 標題標籤
        JLabel titleLabel = new JLabel("碼錶", JLabel.CENTER);
        // 使用系統默認字體來支援emoji
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
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
        resetButton.setBackground(PRIMARY_COLOR);
        resetButton.setForeground(Color.WHITE);
        resetButton.addActionListener(e -> resetTimer());
        
        addButtonHoverEffect(startButton, PRIMARY_COLOR, PRIMARY_LIGHT);
        addButtonHoverEffect(resetButton, PRIMARY_COLOR, PRIMARY_LIGHT);
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(resetButton);
        buttonPanel.add(Box.createHorizontalGlue());
        
        controlPanel.add(buttonPanel);
        
        return controlPanel;
    }
    
    private JButton createModernButton(String text, int width, int height, int iconSize) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
                } else if (getModel().isRollover()) {
                    // 懸停效果由 addButtonHoverEffect 處理
                    g2.setColor(getBackground());
                } else {
                    g2.setColor(getBackground());
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // 繪製文字
                FontMetrics fm = g2.getFontMetrics();
                Rectangle textRect = new Rectangle(
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent(),
                    fm.stringWidth(getText()),
                    fm.getHeight()
                );
                
                g2.setColor(getForeground());
                g2.setFont(getFont());
                g2.drawString(getText(), textRect.x, textRect.y);
            }
        };
        
        button.setPreferredSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setFont(new Font("SF Pro Display", Font.BOLD, iconSize));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void addButtonHoverEffect(JButton button, Color originalColor, Color hoverColor) {
        button.addMouseListener(new MouseAdapter() {
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
                button.setBackground(originalColor.darker());
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
        } else {
            startTimer();
        }
    }
    
    private void startTimer() {
        isRunning = true;
        startButton.setText("⏸");
        startButton.setBackground(PRIMARY_COLOR);
        
        startTime = System.currentTimeMillis() - elapsedTime;
        
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    updateTimerDisplay();
                });
            }
        }, 0, 10); // 每10毫秒更新一次，顯示流暢的centiseconds
    }
    
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        isRunning = false;
        startButton.setText("▶");
        startButton.setBackground(PRIMARY_COLOR);
    }
    
    private void resetTimer() {
        stopTimer();
        elapsedTime = 0;
        updateTimerDisplay();
    }
    
    private void updateTimerDisplay() {
        long totalMillis = elapsedTime;
        int hours = (int) (totalMillis / 3600000);
        int minutes = (int) ((totalMillis % 3600000) / 60000);
        int seconds = (int) ((totalMillis % 60000) / 1000);
        int centiseconds = (int) ((totalMillis % 1000) / 10);
        
        // 生成新的顯示文字
        String newDisplayText;
        if (hours > 0) {
            newDisplayText = String.format("%02d:%02d.%02d", hours, minutes, centiseconds);
        } else {
            newDisplayText = String.format("%02d:%02d.%02d", minutes, seconds, centiseconds);
        }
        
        // 直接更新UI以確保centiseconds能正常顯示
        timerLabel.setText(newDisplayText);
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
                            
                            // 計算碼錶視窗的理想位置（正上方）
                            int idealX = followingLocation.x + 100 - getWidth() / 2; // 角色中心正上方
                            int idealY = followingLocation.y - getHeight() - 10;
                            
                            // 確保位置在螢幕範圍內
                            int finalX = idealX;
                            if (idealX < 0) {
                                finalX = followingLocation.x + 200 + 10;
                                if (finalX + getWidth() > screenWidth) {
                                    finalX = screenWidth - getWidth() - 5;
                                }
                            } else if (idealX + getWidth() > screenWidth) {
                                finalX = screenWidth - getWidth() - 5;
                            }
                            
                            int finalY = idealY;
                            if (idealY < 0) {
                                finalY = followingLocation.y + 200 + 10;
                                if (finalY + getHeight() > screenHeight) {
                                    finalY = screenHeight - getHeight() - 40;
                                }
                            } else if (idealY + getHeight() > screenHeight) {
                                finalY = screenHeight - getHeight() - 40;
                            }
                            
                            finalX = Math.max(0, finalX);
                            finalY = Math.max(0, finalY);
                            
                            setLocation(finalX, finalY);
                        }
                    });
                }
            }
        }, 0, 10);
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
class ModernStopwatchPanel extends JPanel {
    public ModernStopwatchPanel() {
        setOpaque(false);
        // 啟用雙緩衝渲染以減少閃爍
        setDoubleBuffered(true);
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
        
        // 繪製白色背景
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