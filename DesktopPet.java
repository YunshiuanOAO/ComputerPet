import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DesktopPet extends JWindow {
    private int dx = 2, dy = 2; 
    private Timer timer;

    public DesktopPet() {
        setBackground(new Color(0, 0, 0, 0));
        setSize(100, 100);
        setLocation(100, 100);

        add(new PetPanel());

        timer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point p = getLocation();
                int x = p.x;
                int y = p.y;

                x += dx;
                y += dy;

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                if (x < 0 || x > screenSize.width - getWidth()) {
                    dx = -dx;
                }
                if (y < 0 || y > screenSize.height - getHeight()) {
                    dy = -dy;
                }

                setLocation(x, y);
            }
        });
        timer.start();

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DesktopPet());
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
        g.fillOval(0, 0, 100, 100);
    }
}
