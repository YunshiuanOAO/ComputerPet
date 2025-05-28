import javax.swing.*;

public class QuickTestAlert {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 創建一個簡單的測試回調
            ScreenUsedAlert.AlertCallback testCallback = new ScreenUsedAlert.AlertCallback() {
                @Override
                public void onAlert() {
                    System.out.println("視覺提醒開始");
                }
                
                @Override
                public void onAlertEnd() {
                    System.out.println("視覺提醒結束");
                }
            };
            
            // 創建螢幕使用提醒
            ScreenUsedAlert alert = new ScreenUsedAlert(testCallback);
            
            // 模擬已使用1小時以上
            try {
                java.lang.reflect.Field field = ScreenUsedAlert.class.getDeclaredField("usageSeconds");
                field.setAccessible(true);
                field.setInt(alert, 3601); // 設置為3601秒（超過1小時）
                
                java.lang.reflect.Field reminderField = ScreenUsedAlert.class.getDeclaredField("hasReminded");
                reminderField.setAccessible(true);
                reminderField.setBoolean(alert, false); // 確保可以顯示提醒
                
                // 創建一個修改過的InteractiveAlertWindow來測試
                InteractiveAlertWindow.AlertActionCallback testAlertCallback = new InteractiveAlertWindow.AlertActionCallback() {
                    @Override
                    public void onReset() {
                        System.out.println("用戶選擇重置計時器");
                        try {
                            java.lang.reflect.Method resetMethod = ScreenUsedAlert.class.getDeclaredMethod("resetTimer");
                            resetMethod.setAccessible(true);
                            resetMethod.invoke(alert);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    @Override
                    public void onSnooze(int minutes) {
                        System.out.println("用戶選擇" + minutes + "分鐘後提醒，測試中使用30秒代替");
                        try {
                            // 使用30秒代替30分鐘進行快速測試
                            java.lang.reflect.Method snoozeMethod = ScreenUsedAlert.class.getDeclaredMethod("scheduleNextReminder", int.class);
                            snoozeMethod.setAccessible(true);
                            snoozeMethod.invoke(alert, 30); // 30秒後提醒
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    @Override
                    public void onDismiss() {
                        System.out.println("用戶選擇關閉提醒");
                        try {
                            java.lang.reflect.Field reminderField = ScreenUsedAlert.class.getDeclaredField("hasReminded");
                            reminderField.setAccessible(true);
                            reminderField.setBoolean(alert, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                
                // 創建並顯示測試視窗
                InteractiveAlertWindow testWindow = new InteractiveAlertWindow(testAlertCallback);
                testWindow.showWindow();
                
                System.out.println("測試提醒已觸發");
                System.out.println("請點擊「30分鐘後提醒」按鈕，然後等待30秒看是否會重新彈出提醒");
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
} 