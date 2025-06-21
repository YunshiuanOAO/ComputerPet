package utils;

import java.awt.*;
import java.awt.geom.*;

/**
 * 字體工具類，處理跨平台字體相容性問題
 */
public class FontUtils {
    
    /**
     * 獲取支援Unicode字符的字體
     * 針對不同操作系統選擇最佳字體
     */
    public static Font getUnicodeFont(int style, int size) {
        String os = System.getProperty("os.name").toLowerCase();
        String[] fontNames;
        
        if (os.contains("win")) {
            // Windows系統字體優先級
            fontNames = new String[]{
                "Segoe UI Symbol",      // Windows 10/11 系統字體，支援Unicode
                "Arial Unicode MS",     // 微軟Unicode字體
                "MS Gothic",           // 日文字體，Unicode支援良好
                "SimSun",              // 中文字體
                "Arial",               // 回退選項
                Font.SANS_SERIF        // 系統預設
            };
        } else if (os.contains("mac")) {
            // macOS系統字體優先級
            fontNames = new String[]{
                "SF Pro Display",
                "Helvetica Neue",
                "Arial Unicode MS",
                Font.SANS_SERIF
            };
        } else {
            // Linux系統字體優先級
            fontNames = new String[]{
                "DejaVu Sans",
                "Liberation Sans",
                "Arial Unicode MS",
                Font.SANS_SERIF
            };
        }
        
        // 嘗試每個字體，找到系統支援的第一個
        for (String fontName : fontNames) {
            Font font = new Font(fontName, style, size);
            if (isFontAvailable(fontName)) {
                return font;
            }
        }
        
        // 如果都不可用，返回系統預設字體
        return new Font(Font.SANS_SERIF, style, size);
    }
    
    /**
     * 檢查字體是否在系統中可用
     */
    private static boolean isFontAvailable(String fontName) {
        String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String available : availableFonts) {
            if (available.equals(fontName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 獲取適合各系統的重置符號
     * 使用文字替代 Unicode 符號以確保跨平台相容性
     */
    public static String getResetSymbol() {
        // 使用簡單的文字符號，在所有系統上都能正常顯示
        return "⟲";  // 使用這個更通用的重設符號
    }
    
    /**
     * 繪製重置圖案（圓形箭頭）
     * 提供自定義繪製方法以確保跨平台相容性
     */
    public static void drawResetIcon(Graphics2D g2, int centerX, int centerY, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // 繪製圓弧 - 縮小到原來的 0.7 倍
        int arcSize = (int)(size * 0.7);
        int arcX = centerX - arcSize/2;
        int arcY = centerY - arcSize/2;
        
        // 主要的圓弧（約270度）
        g2.drawArc(arcX, arcY, arcSize, arcSize, 45, 270);
        
        // 繪製箭頭 - 也相應縮小
        int arrowSize = arcSize / 5;
        int arrowX = centerX + (int)(arcSize/2 * Math.cos(Math.toRadians(45)));
        int arrowY = centerY - (int)(arcSize/2 * Math.sin(Math.toRadians(45)));
        
        // 箭頭的兩條線
        int[] xPoints = {arrowX, arrowX - arrowSize, arrowX - arrowSize/2};
        int[] yPoints = {arrowY, arrowY - arrowSize/2, arrowY + arrowSize/2};
        g2.drawPolyline(xPoints, yPoints, 3);
    }
    
    /**
     * 獲取播放/暫停符號
     */
    public static String getPlaySymbol() {
        return "▶";
    }
    
    public static String getPauseSymbol() {
        return "⏸";
    }
} 