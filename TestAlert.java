import javax.swing.*;

public class TestAlert {
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
                
                // 觸發提醒
                java.lang.reflect.Method showAlertMethod = ScreenUsedAlert.class.getDeclaredMethod("showAlert");
                showAlertMethod.setAccessible(true);
                showAlertMethod.invoke(alert);
                
                System.out.println("測試提醒已觸發，請測試「30分鐘後提醒」功能");
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
} 