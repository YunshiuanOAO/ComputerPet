import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

public class DesktopPet extends JWindow implements ScreenUsedAlert.AlertCallback {
    private PetPanel panel;
    
    // 螢幕使用時間計時相關
    private ScreenUsedAlert screenAlert;
    
    // 番茄鐘應用程式實例 - 確保只有一個
    private PomodoroApp pomodoroApp;
    
    public class MouseDetect extends MouseAdapter
    {
      @Override
      public void mousePressed(MouseEvent event)
      {
        SwingUtilities.invokeLater(() -> {
            // 檢查是否已經有番茄鐘視窗開啟
            if (pomodoroApp == null || !pomodoroApp.isDisplayable()) {
                // 創建新的番茄鐘視窗
                pomodoroApp = new PomodoroApp(DesktopPet.this);
                
                // 將對話框定位在寵物的左上角
                Point petLocation = getLocation();
                pomodoroApp.setLocation(petLocation.x - pomodoroApp.getWidth() - 100, 
                                       petLocation.y - pomodoroApp.getHeight() - 100);
                pomodoroApp.setVisible(true);
            } else {
                // 如果已經開啟，將其帶到前面並確保可見
                pomodoroApp.toFront();
                pomodoroApp.setVisible(true);
                pomodoroApp.requestFocus();
            }
        });
      }
    }

    
    public DesktopPet() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setBackground(new Color(0, 0, 0, 0));
        setSize(100, 100);
        setLocation(screen.width - getWidth(), screen.height - getHeight());

        panel = new PetPanel();
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(100, 100));
        getContentPane().add(panel);
        
        setVisible(true);

        MouseDetect listener = new MouseDetect();
        panel.addMouseListener(listener);
        
        // Add drag capability
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - 50, e.getYOnScreen() - 50);
            }
        });

        // 監聽全域焦點事件，失去焦點時半透明，獲得焦點時恢復
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                setOpacity(1.0f);
            }
            
            @Override
            public void windowLostFocus(WindowEvent e) {
                setOpacity(0.3f); // 設定為更明顯的半透明效果
            }
        });
        
        // 當寵物創建後立即設為半透明（因為它不會一開始就有焦點）
        Timer initialTransparency = new Timer(100, e -> {
            setOpacity(0.3f);
            ((Timer)e.getSource()).stop();
        });
        initialTransparency.setRepeats(false);
        initialTransparency.start();
        
        // 初始化螢幕使用時間監控
        screenAlert = new ScreenUsedAlert(this);
        screenAlert.startMonitoring();
    }
    
    // 實現AlertCallback介面的方法
    @Override
    public void onAlert() {
        // 開始視覺提醒（寵物閃爍）
        panel.startAlert();
    }
    
    @Override
    public void onAlertEnd() {
        // 結束視覺提醒
        panel.stopAlert();
    }
    
    // 獲取使用時間的方法
    public String getCurrentUsageTime() {
        return screenAlert.getFormattedUsageTime();
    }

    public static void main(String[] args) {
      SwingUtilities.invokeLater(DesktopPet::new);
    }
}

class PetPanel extends JPanel {
    private Color bodyColor = new Color(255, 99, 71); // Tomato color
    private int blinkState = 0;
    private Timer blinkTimer;
    
    // 提醒閃爍相關
    private boolean isAlertMode = false;
    private Timer alertTimer;
    private Color alertColor = new Color(255, 0, 0); // 紅色提醒
    private boolean alertFlash = false;
    
    public PetPanel() {
        setOpaque(false);
        
        // Add blinking effect
        blinkTimer = new Timer(3000, e -> {
            blinkState = 3;
            repaint();
            
            // Reset after blinking
            new Timer(150, event -> {
                blinkState--;
                repaint();
                if (blinkState <= 0) {
                    ((Timer)event.getSource()).stop();
                }
            }).start();
        });
        blinkTimer.start();
    }
    
    public void startAlert() {
        isAlertMode = true;
        if (alertTimer != null) {
            alertTimer.stop();
        }
        
        alertTimer = new Timer(500, e -> {
            alertFlash = !alertFlash;
            repaint();
        });
        alertTimer.start();
    }
    
    public void stopAlert() {
        isAlertMode = false;
        alertFlash = false;
        if (alertTimer != null) {
            alertTimer.stop();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Main body - 如果在提醒模式且閃爍狀態，使用紅色
        Color currentBodyColor = bodyColor;
        if (isAlertMode && alertFlash) {
            currentBodyColor = alertColor;
        }
        
        g2d.setColor(currentBodyColor);
        g2d.fill(new Ellipse2D.Double(10, 10, 80, 80));
        
        // Leaf
        g2d.setColor(new Color(0, 128, 0));
        g2d.fillOval(40, 0, 30, 20);
        
        // Eyes
        if (blinkState > 0) {
            // Blinking
            g2d.setColor(Color.BLACK);
            g2d.fillRect(30, 40, 12, 2);
            g2d.fillRect(60, 40, 12, 2);
        } else {
            // Normal eyes
            g2d.setColor(Color.WHITE);
            g2d.fillOval(30, 35, 12, 12);
            g2d.fillOval(60, 35, 12, 12);
            
            g2d.setColor(Color.BLACK);
            g2d.fillOval(33, 38, 6, 6);
            g2d.fillOval(63, 38, 6, 6);
        }
        
        // Smile
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawArc(35, 55, 30, 15, 0, -180);
        
        // Add a cursor hand when hovering
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}

