package pet;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JLabel;

// 新增：可等比例縮放圖片的 JLabel
public class ScaledImageLabel extends JLabel {
    private Image image;
    public ScaledImageLabel(Image image) {
        this.image = image;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int w = getWidth();
            int h = getHeight();
            int imgW = image.getWidth(null);
            int imgH = image.getHeight(null);
            double scale = Math.min((double)w/imgW, (double)h/imgH);
            int drawW = (int)(imgW * scale);
            int drawH = (int)(imgH * scale);
            int x = (w - drawW) / 2;
            int y = (h - drawH) / 2;
            g.drawImage(image, x, y, drawW, drawH, null);
        }
    }
    public void setImage(Image image) {
        this.image = image;
        repaint();
    }
}