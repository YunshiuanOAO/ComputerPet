import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
        setVisible(true);
        MouseDetect listener = new MouseDetect();
        panel.addMouseListener(listener);

    }

    public static void main(String[] args) {
      SwingUtilities.invokeLater(DesktopPet::new);
    }
}

class PetPanel extends JPanel {
    public PetPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillOval(0, 0, getWidth(), getHeight());
    }
}

