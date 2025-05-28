import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScreenUsedAlert {
    private Timer usageTimer;
    private int usageSeconds = 0;
    private boolean hasReminded = false;
    private AlertCallback callback;
    private InteractiveAlertWindow currentAlertWindow = null; // 防止多視窗
    
    // 回調介面，用於通知桌面寵物進行視覺提醒
    public interface AlertCallback {
        void onAlert();
        void onAlertEnd();
    }
    
    public ScreenUsedAlert(AlertCallback callback) {
        this.callback = callback;
    }
    
    // 開始計時
    public void startMonitoring() {
        if (usageTimer != null) {
            usageTimer.stop();
        }
        
        usageTimer = new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usageSeconds++;
                
                // 每10分鐘顯示一次當前使用時間
                if (usageSeconds % 600 == 0) {
                    int minutes = usageSeconds / 60;
                    System.out.println("螢幕使用時間: " + minutes + " 分鐘");
                }
                
                // 超過1小時（3600秒）提醒使用者
                if (!hasReminded && usageSeconds >= 3600) {
                    hasReminded = true;
                    showAlert();
                }
            }
        });
        usageTimer.start();
        System.out.println("螢幕使用時間監控已啟動");
    }
    
    // 停止計時
    public void stopMonitoring() {
        if (usageTimer != null) {
            usageTimer.stop();
            System.out.println("螢幕使用時間監控已停止");
        }
    }
    
    // 重置計時器
    public void resetTimer() {
        usageSeconds = 0;
        hasReminded = false;
        System.out.println("螢幕使用時間計時器已重置");
    }
    
    // 顯示提醒
    private void showAlert() {
        SwingUtilities.invokeLater(() -> {
            // 如果已經有提醒視窗在顯示，就不再創建新的
            if (currentAlertWindow != null && currentAlertWindow.isDisplayable()) {
                return;
            }
            
            // 通知回調開始視覺提醒
            if (callback != null) {
                callback.onAlert();
            }
            
            // 創建互動式提醒視窗
            currentAlertWindow = new InteractiveAlertWindow(
                new InteractiveAlertWindow.AlertActionCallback() {
                    @Override
                    public void onReset() {
                        resetTimer();
                        currentAlertWindow = null; // 清除引用
                        // 停止視覺提醒
                        if (callback != null) {
                            callback.onAlertEnd();
                        }
                    }
                    
                    @Override
                    public void onSnooze(int minutes) {
                        scheduleNextReminder(minutes * 60); // 轉換為秒
                        currentAlertWindow = null; // 清除引用
                        // 停止視覺提醒
                        if (callback != null) {
                            callback.onAlertEnd();
                        }
                    }
                    
                    @Override
                    public void onDismiss() {
                        hasReminded = true; // 不再提醒
                        currentAlertWindow = null; // 清除引用
                        // 停止視覺提醒
                        if (callback != null) {
                            callback.onAlertEnd();
                        }
                    }
                }
            );
            
            // 顯示提醒視窗
            currentAlertWindow.showWindow();
        });
    }
    
    // 排程下次提醒
    private void scheduleNextReminder(int seconds) {
        hasReminded = true; // 暫時標記為已提醒，防止在等待期間重複提醒
        
        Timer reminderTimer = new Timer(seconds * 1000, e -> {
            hasReminded = false; // 重新啟用提醒
            // 檢查是否仍然超過1小時，如果是則顯示提醒
            if (usageSeconds >= 3600) {
                showAlert();
            }
            ((Timer)e.getSource()).stop();
        });
        reminderTimer.setRepeats(false);
        reminderTimer.start();
        
        System.out.println("將在 " + (seconds / 60) + " 分鐘後再次提醒");
    }
    
    // 獲取當前使用分鐘數
    public int getUsageMinutes() {
        return usageSeconds / 60;
    }
    
    // 獲取當前使用秒數
    public int getUsageSeconds() {
        return usageSeconds;
    }
    
    // 格式化使用時間為字串
    public String getFormattedUsageTime() {
        int hours = usageSeconds / 3600;
        int minutes = (usageSeconds % 3600) / 60;
        int seconds = usageSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d小時%d分鐘%d秒", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d分鐘%d秒", minutes, seconds);
        } else {
            return String.format("%d秒", seconds);
        }
    }
    
    // 檢查是否正在監控
    public boolean isMonitoring() {
        return usageTimer != null && usageTimer.isRunning();
    }
}
