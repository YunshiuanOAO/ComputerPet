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

    private MouseListener toggleListener;

    public DesktopPet() {
        setBackground(new Color(0, 0, 0, 0));
        setSize(100, 100);
        setLocation(100, 100);

        panel = new PetPanel();
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(100, 100));
        getContentPane().add(panel);

        toggleListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
              FollowMouseMod();
            }

            @Override
            public void mouseReleased(MouseEvent e){
              WalkMod();
            }
        };
        panel.addMouseListener(toggleListener);
        WalkMod();
    }

    public void WalkMod() {
      System.out.println("Now is WalkMod");  
      if (timer != null && timer.isRunning()) timer.stop();
        timer = new Timer(30, e -> {
            Point p = getLocation();
            int x = p.x + dx;
            int y = p.y + dy;
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            if (x < 0 || x > screen.width - getWidth()) dx = -dx;
            if (y < 0 || y > screen.height - getHeight()) dy = -dy;
            setLocation(x, y);
        });
        timer.start();
        setVisible(true);
    }

    public void FollowMouseMod() {
        System.out.println("Now is FollowMouseMod");
        if (timer != null && timer.isRunning()) timer.stop();
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseOffset = e.getPoint();
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point screenPt = e.getLocationOnScreen();
                setLocation(screenPt.x - mouseOffset.x, screenPt.y - mouseOffset.y);
            }
        });
        setVisible(true);
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

