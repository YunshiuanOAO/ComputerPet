package pomodoro;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class InteractiveAlertWindow extends JWindow {
    private JLabel messageLabel;
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
        // 移除脈衝動畫，避免視窗閃爍
        // startPulseAnimation();
        setOpacity(0.95f); // 設定固定透明度
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // 主面板 (與 Stopwatch 配色統一)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE); // 純白背景
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 標題 (使用 Stopwatch 配色)
        JLabel titleLabel = new JLabel("健康提醒", JLabel.CENTER);
        titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0xF26B49)); // PRIMARY_COLOR
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 主要訊息
        messageLabel = new JLabel("<html><div style='text-align: center;'>" +
                                "您已經連續使用電腦超過1小時了！<br><br>" +
                                "為了保護您的健康，建議您：<br>" +
                                "• 起身活動一下<br>" +
                                "• 看看遠方放鬆眼睛<br>" +
                                "• 喝點水休息片刻<br><br>" +
                                "點擊OK重新開始計時" +
                                "</div></html>", JLabel.CENTER);
        messageLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        messageLabel.setForeground(new Color(0x374151)); // TEXT_COLOR
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 按鈕面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        
        // 確認按鈕 (使用 Stopwatch 配色)
        JButton confirmButton = createStyledButton("OK", new Color(0xF26B49)); // PRIMARY_COLOR
        confirmButton.setPreferredSize(new Dimension(160, 45));
        confirmButton.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        confirmButton.addActionListener(e -> {
            if (callback != null) callback.onReset();
            closeWindow();
        });
        
        buttonPanel.add(confirmButton);
        
        // 添加組件到主面板
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(25));
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
    
    public void showWindow() {
        setVisible(true);
        toFront();
        requestFocus();
    }
    
    private void closeWindow() {
        setVisible(false);
        dispose();
    }
    
    // 檢查視窗是否正在顯示
    public boolean isShowing() {
        return isVisible() && isDisplayable();
    }
} 