package taskmanager;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import pet.DesktopPet;

/**
 * 寵物位置跟隨器
 * 負責管理視窗跟隨寵物的位置邏輯
 */
public class PetPositionTracker {
    
    private final DesktopPet desktopPet;
    private final JFrame followingWindow;
    private final int followingPetIndex;
    private Timer positionTimer;
    
    public PetPositionTracker(DesktopPet desktopPet, JFrame followingWindow, int petIndex) {
        this.desktopPet = desktopPet;
        this.followingWindow = followingWindow;
        this.followingPetIndex = petIndex;
    }
    
    /**
     * 設置初始位置
     */
    public void setInitialPosition() {
        if (desktopPet == null || followingWindow == null) {
            return;
        }
        
        Point petLocation;
        Dimension petSize;
        
        if (followingPetIndex >= 0) {
            // 跟隨特定寵物
            petLocation = desktopPet.getPetLocation(followingPetIndex);
            petSize = desktopPet.getPetSize(followingPetIndex);
        } else {
            // 跟隨石頭（主屋）
            petLocation = desktopPet.getStoneLocation();
            petSize = desktopPet.getStoneSize();
        }
        
        // 計算視窗應該出現的位置（寵物正上方）
        int x = petLocation.x + (petSize.width - followingWindow.getWidth()) / 2;
        int y = petLocation.y - followingWindow.getHeight() - 10;
        
        // 確保視窗不會超出螢幕邊界
        Point adjustedLocation = adjustForScreenBounds(x, y, petLocation, petSize);
        followingWindow.setLocation(adjustedLocation.x, adjustedLocation.y);
    }
    
    /**
     * 開始位置跟隨
     */
    public void startTracking() {
        if (positionTimer != null) {
            positionTimer.cancel();
        }
        
        positionTimer = new Timer();
        positionTimer.scheduleAtFixedRate(new TimerTask() {
            private Point lastLocation = null;
            
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> updatePosition());
            }
            
            private void updatePosition() {
                if (!followingWindow.isVisible() || desktopPet == null) {
                    return;
                }
                
                Point currentPetLocation;
                Dimension petSize;
                
                if (followingPetIndex >= 0) {
                    // 跟隨特定寵物
                    currentPetLocation = desktopPet.getPetLocation(followingPetIndex);
                    petSize = desktopPet.getPetSize(followingPetIndex);
                } else {
                    // 跟隨石頭（主屋）
                    currentPetLocation = desktopPet.getStoneLocation();
                    petSize = desktopPet.getStoneSize();
                }
                
                // 只有當寵物位置改變時才更新視窗位置
                if (lastLocation == null || !lastLocation.equals(currentPetLocation)) {
                    lastLocation = new Point(currentPetLocation);
                    
                    int x = currentPetLocation.x + (petSize.width - followingWindow.getWidth()) / 2;
                    int y = currentPetLocation.y - followingWindow.getHeight() - 10;
                    
                    Point adjustedLocation = adjustForScreenBounds(x, y, currentPetLocation, petSize);
                    followingWindow.setLocation(adjustedLocation.x, adjustedLocation.y);
                }
            }
        }, 0, 50); // 每50毫秒檢查一次
    }
    
    /**
     * 停止位置跟隨
     */
    public void stopTracking() {
        if (positionTimer != null) {
            positionTimer.cancel();
            positionTimer = null;
        }
    }
    
    /**
     * 調整位置以確保不超出螢幕邊界
     */
    private Point adjustForScreenBounds(int x, int y, Point petLocation, Dimension petSize) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        // 水平邊界檢查
        if (x + followingWindow.getWidth() > screenSize.width) {
            x = screenSize.width - followingWindow.getWidth();
        }
        if (x < 0) {
            x = 0;
        }
        
        // 垂直邊界檢查
        if (y < 0) {
            // 如果上方空間不足，放到下方
            y = petLocation.y + petSize.height + 10;
        }
        if (y + followingWindow.getHeight() > screenSize.height) {
            y = screenSize.height - followingWindow.getHeight();
        }
        
        return new Point(x, y);
    }
} 