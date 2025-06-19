import javax.swing.SwingUtilities;

import pet.DesktopPet;

public class Application {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DesktopPet desktopPet = new DesktopPet();
            desktopPet.createAndShowGUI();
            desktopPet.initializeScreenMonitoring(); // 啟動螢幕使用時間監控
        });
    }
    
}
