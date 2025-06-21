import javax.swing.SwingUtilities;

import pet.DesktopPet;

public class Application {

    public static void main(String[] args) {
        // 程序啟動時清理任何遺留的SessionFlow進程
        cleanupOrphanedSessionFlowProcesses();
        
        SwingUtilities.invokeLater(() -> {
            DesktopPet desktopPet = new DesktopPet();
            desktopPet.createAndShowGUI();
            desktopPet.initializeScreenMonitoring(); // 啟動螢幕使用時間監控
        });
    }
    
    // 清理任何遺留的SessionFlow進程
    private static void cleanupOrphanedSessionFlowProcesses() {
        try {
            System.out.println("檢查並清理遺留的SessionFlow進程...");
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;
            
            if (os.contains("win")) {
                // Windows系統
                pb = new ProcessBuilder("cmd", "/c", 
                    "wmic process where \"commandline like '%sessionflow.jar%'\" delete");
            } else if (os.contains("mac") || os.contains("darwin")) {
                // macOS系統
                pb = new ProcessBuilder("sh", "-c", 
                    "pkill -f 'java.*sessionflow.jar' || true");
            } else {
                // Linux系統
                pb = new ProcessBuilder("sh", "-c", 
                    "pkill -f 'java.*sessionflow.jar' || true");
            }
            
            Process killProcess = pb.start();
            boolean finished = killProcess.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
            
            if (finished) {
                int exitCode = killProcess.exitValue();
                if (exitCode == 0) {
                    System.out.println("已清理遺留的SessionFlow進程");
                } else {
                    System.out.println("未發現遺留的SessionFlow進程");
                }
            }
        } catch (Exception e) {
            System.err.println("清理遺留SessionFlow進程時出錯: " + e.getMessage());
        }
    }
}
