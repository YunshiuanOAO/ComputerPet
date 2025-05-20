import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

public class DesktopPet extends JWindow {
    private int dx = 2, dy = 2;
    private Timer timer;
    private PetPanel panel;
    private Point mouseOffset = new Point();

    // mod：0 = autowalk, 1 = floowmouse
    private int petMode = 0;
    
    public class MouseDetect extends MouseAdapter
    {
      @Override
      public void mousePressed(MouseEvent event)
      {
        SwingUtilities.invokeLater(() -> {
            PomodoroApp app = new PomodoroApp();
            app.setLocationRelativeTo(null);
            app.setVisible(true);
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
        
        // Add random movement
        timer = new Timer(50, e -> movePet());
        timer.start();
        
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
    }
    
    private void movePet() {
        if (petMode == 0) { // Auto walk mode
            Point location = getLocation();
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            
            // Change direction if hitting screen edges
            if (location.x <= 0 || location.x >= screen.width - getWidth()) {
                dx = -dx;
            }
            if (location.y <= 0 || location.y >= screen.height - getHeight()) {
                dy = -dy;
            }
            
            // Randomly change direction occasionally
            if (Math.random() < 0.02) {
                dx = (Math.random() > 0.5) ? 2 : -2;
                dy = (Math.random() > 0.5) ? 2 : -2;
            }
            
            setLocation(location.x + dx, location.y + dy);
        }
    }

    public static void main(String[] args) {
      SwingUtilities.invokeLater(DesktopPet::new);
    }
}

class PetPanel extends JPanel {
    private Color bodyColor = new Color(255, 99, 71); // Tomato color
    private int blinkState = 0;
    private Timer blinkTimer;
    
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Main body
        g2d.setColor(bodyColor);
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

