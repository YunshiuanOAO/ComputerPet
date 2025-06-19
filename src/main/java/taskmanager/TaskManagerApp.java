package taskmanager;

import java.awt.Desktop;
import java.net.URI;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import pet.DesktopPet;

public class TaskManagerApp extends JFrame {

    private int port;
    private DesktopPet desktopPet;
    private int petIndex;

    public TaskManagerApp(DesktopPet desktopPet, int petIndex) {
        this.desktopPet = desktopPet;
        this.petIndex = petIndex;
        this.port = 8080; // 預設端口
        
        openInBrowser();
    }

    public TaskManagerApp() {
        this(null, -1);
    }

    public TaskManagerApp(DesktopPet desktopPet, int petIndex, int port) {
        this.desktopPet = desktopPet;
        this.petIndex = petIndex;
        this.port = port;
        
        openInBrowser();
    }

    private void openInBrowser() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                String url = "http://localhost:" + port;
                Desktop.getDesktop().browse(new URI(url));
                System.out.println("已在瀏覽器中打開: " + url);
                
                // 由於我們不需要顯示視窗，可以直接關閉這個 JFrame
                this.dispose();
            } else {
                // 如果不支援 Desktop.browse，顯示錯誤訊息
                JOptionPane.showMessageDialog(null, 
                    "系統不支援自動開啟瀏覽器\n請手動開啟瀏覽器並前往：\nhttp://localhost:" + port, 
                    "無法開啟瀏覽器", 
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            // 如果開啟失敗，顯示錯誤訊息和 URL
            JOptionPane.showMessageDialog(null, 
                "無法開啟瀏覽器: " + e.getMessage() + "\n請手動開啟瀏覽器並前往：\nhttp://localhost:" + port, 
                "開啟瀏覽器失敗", 
                JOptionPane.ERROR_MESSAGE);
            System.err.println("開啟瀏覽器失敗: " + e.getMessage());
        }
    }
    
    // 設定端口的方法
    public void setPort(int port) {
        this.port = port;
    }
    
    // 獲取當前端口
    public int getPort() {
        return port;
    }
}
