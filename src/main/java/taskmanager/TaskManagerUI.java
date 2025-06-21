package taskmanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * 任務管理器 UI 組件
 * 負責創建和管理用戶界面
 */
public class TaskManagerUI {
    
    public interface UIListener {
        void onOpenBrowser();
        void onRetry();
        void onHide();
    }
    
    // 統一的配色方案
    private final Color PRIMARY_LIGHT = new Color(0xFEB098); // #FEB098
    private final Color PRIMARY_COLOR = new Color(0xF26B49); // #F26B49
    private final Color PRIMARY_DARK = new Color(0xCC553A); // #CC553A
    private final Color BACKGROUND_COLOR = new Color(0xFAFAF9); // stone-50
    private final Color TEXT_COLOR = new Color(0x374151); // neutral-700
    private final Color BORDER_COLOR = new Color(0xD4D4D8); // neutral-300
    
    private final JFrame frame;
    private final UIListener listener;
    private final int port;
    
    private JLabel statusLabel;
    private JButton openBrowserButton;
    private JButton retryButton;
    private JButton hideButton;
    
    public TaskManagerUI(JFrame frame, UIListener listener, int port) {
        this.frame = frame;
        this.listener = listener;
        this.port = port;
        initializeUI();
    }
    
    private void initializeUI() {
        frame.setTitle("任務管理");
        frame.setSize(350, 220);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        
        // 設定現代化的主要面板
        ModernTaskManagerPanel mainPanel = new ModernTaskManagerPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        
        // 標題面板
        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // 內容面板
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // 按鈕面板
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.setContentPane(mainPanel);
        
        // 設定整個視窗為圓角
        SwingUtilities.invokeLater(() -> {
            frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 25, 25));
        });
        
        // 視窗大小改變時自動調整圓角
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 25, 25));
            }
        });
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        // 標題標籤
        JLabel titleLabel = new JLabel("任務管理系統", JLabel.CENTER);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        // 隱藏按鈕
        hideButton = createModernButton("×", 25, 25, 20);
        hideButton.setFont(new Font("Arial", Font.BOLD, 16));
        hideButton.setBackground(new Color(255, 255, 255, 100));
        hideButton.setForeground(new Color(100, 100, 100));
        hideButton.addActionListener(e -> {
            if (listener != null) {
                listener.onHide();
            }
        });
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
    
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // 狀態標籤
        statusLabel = new JLabel("正在初始化...", JLabel.CENTER);
        statusLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        
        // URL 顯示標籤
        JLabel urlLabel = new JLabel("服務地址: http://localhost:" + port, JLabel.CENTER);
        urlLabel.setFont(new Font("SF Pro Display", Font.BOLD, 13));
        urlLabel.setForeground(PRIMARY_COLOR);
        urlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        urlLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(statusLabel);
        contentPanel.add(urlLabel);
        contentPanel.add(Box.createVerticalGlue());
        
        return contentPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        
        openBrowserButton = createModernButton("開啟瀏覽器", 100, 35, 14);
        openBrowserButton.setBackground(PRIMARY_COLOR);
        openBrowserButton.setForeground(Color.WHITE);
        openBrowserButton.setEnabled(false);
        openBrowserButton.addActionListener(e -> {
            if (listener != null) {
                listener.onOpenBrowser();
            }
        });
        
        retryButton = createModernButton("重試", 60, 35, 14);
        retryButton.setBackground(PRIMARY_DARK);
        retryButton.setForeground(Color.WHITE);
        retryButton.setVisible(false);
        retryButton.addActionListener(e -> {
            retryButton.setVisible(false);
            openBrowserButton.setEnabled(false);
            if (listener != null) {
                listener.onRetry();
            }
        });
        
        addButtonHoverEffect(openBrowserButton, PRIMARY_COLOR);
        addButtonHoverEffect(retryButton, PRIMARY_DARK);
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(openBrowserButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(retryButton);
        buttonPanel.add(Box.createHorizontalGlue());
        
        return buttonPanel;
    }
    
    private JButton createModernButton(String text, int width, int height, int fontSize) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
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
                g2.drawString(getText(), textRect.x, textRect.y);
            }
        };
        
        button.setFont(new Font("SF Pro Display", Font.BOLD, fontSize));
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void addButtonHoverEffect(JButton button, Color originalColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(PRIMARY_LIGHT);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(originalColor);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(PRIMARY_DARK);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(button.contains(e.getPoint()) ? PRIMARY_LIGHT : originalColor);
                }
            }
        });
    }
    
    public void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(message);
            }
        });
    }
    
    public void setOpenBrowserEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            if (openBrowserButton != null) {
                openBrowserButton.setEnabled(enabled);
            }
        });
    }
    
    public void showRetryButton(boolean show) {
        SwingUtilities.invokeLater(() -> {
            if (retryButton != null) {
                retryButton.setVisible(show);
            }
        });
    }
    
    public void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame,
                "SessionFlow 初始化失敗:\n" + message + 
                "\n\n請檢查網路連線或點擊重試按鈕",
                "初始化錯誤",
                JOptionPane.ERROR_MESSAGE);
        });
    }
}

class ModernTaskManagerPanel extends JPanel {
    public ModernTaskManagerPanel() {
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