package taskmanager;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * SessionFlow 管理器
 * 負責下載、啟動和管理 SessionFlow 服務
 */
public class SessionFlowManager {
    
    public interface StatusListener {
        void onStatusUpdate(String status);
        void onError(String error);
        void onReady();
    }
    
    private static final int PORT = 53551;
    private static final String JAR_FILE_NAME = "sessionflow.jar";
    private static final String DOWNLOAD_SCRIPT_URL = "https://raw.githubusercontent.com/l-zch/sessionflow/main/scripts/download-latest-release.sh";
    
    private static Process sessionflowProcess;
    private StatusListener statusListener;
    
    public SessionFlowManager(StatusListener statusListener) {
        this.statusListener = statusListener;
    }
    
    public void initialize() {
        new Thread(() -> {
            try {
                // 檢查是否已存在 sessionflow.jar
                File currentDir = new File(System.getProperty("user.dir"));
                Path jarPath = Paths.get(currentDir.getAbsolutePath(), JAR_FILE_NAME);
                
                System.out.println("檢查 JAR 文件: " + jarPath.toString());
                System.out.println("文件是否存在: " + Files.exists(jarPath));
                
                if (!Files.exists(jarPath)) {
                    updateStatus("正在下載 SessionFlow...");
                    try {
                        downloadSessionFlow();
                    } catch (Exception e) {
                        System.err.println("腳本下載失敗，嘗試直接下載: " + e.getMessage());
                        updateStatus("腳本下載失敗，嘗試直接下載...");
                        downloadSessionFlowDirect();
                    }
                }
                
                updateStatus("正在啟動 SessionFlow...");
                startSessionFlow();
                
                // 等待服務啟動
                waitForService();
                
                updateStatus("SessionFlow 已就緒");
                if (statusListener != null) {
                    statusListener.onReady();
                }
                
            } catch (Exception e) {
                String errorMsg = "初始化失敗: " + e.getMessage();
                System.err.println(errorMsg);
                e.printStackTrace();
                
                if (statusListener != null) {
                    statusListener.onError(errorMsg);
                }
            }
        }).start();
    }
    
    private void downloadSessionFlow() throws Exception {
        updateStatus("正在下載 SessionFlow...");
        
        File currentDir = new File(System.getProperty("user.dir"));
        Path jarPath = Paths.get(currentDir.getAbsolutePath(), JAR_FILE_NAME);
        
        System.out.println("下載目標目錄: " + currentDir.getAbsolutePath());
        System.out.println("JAR文件完整路徑: " + jarPath.toString());
        
        // 先刪除可能存在的損壞文件
        if (Files.exists(jarPath)) {
            Files.delete(jarPath);
            System.out.println("已刪除舊的 " + JAR_FILE_NAME + " 文件");
        }
        
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", 
            "curl -sL " + DOWNLOAD_SCRIPT_URL + " | bash");
        pb.directory(currentDir);
        
        System.out.println("開始執行下載腳本...");
        Process process = pb.start();
        
        StringBuilder outputBuilder = new StringBuilder();
        StringBuilder errorBuilder = new StringBuilder();
        
        Thread outputThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Download: " + line);
                    outputBuilder.append(line).append("\n");
                    if (line.contains("%") || line.contains("MB") || line.contains("KB")) {
                        updateStatus("下載中: " + line.trim());
                    }
                }
            } catch (IOException e) {
                System.err.println("讀取輸出時發生錯誤: " + e.getMessage());
            }
        });
        
        Thread errorThread = new Thread(() -> {
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.err.println("Download Error: " + line);
                    errorBuilder.append(line).append("\n");
                }
            } catch (IOException e) {
                System.err.println("讀取錯誤輸出時發生錯誤: " + e.getMessage());
            }
        });
        
        outputThread.start();
        errorThread.start();
        
        System.out.println("等待下載腳本完成...");
        int exitCode = process.waitFor();
        System.out.println("下載腳本結束，退出碼: " + exitCode);
        
        try {
            outputThread.join(10000);
            errorThread.join(10000);
            System.out.println("輸出讀取完成");
        } catch (InterruptedException e) {
            System.err.println("等待輸出線程時被中斷: " + e.getMessage());
        }
        
        if (exitCode != 0) {
            String errorMsg = "下載腳本執行失敗，退出碼: " + exitCode;
            if (errorBuilder.length() > 0) {
                errorMsg += "\n錯誤詳情: " + errorBuilder.toString();
            }
            throw new RuntimeException(errorMsg);
        }
        
        Thread.sleep(2000);
        
        validateDownloadedFile(jarPath);
        
        System.out.println("SessionFlow 下載並驗證完成，文件大小: " + Files.size(jarPath) + " bytes");
    }
    
    private void downloadSessionFlowDirect() throws Exception {
        updateStatus("使用直接下載方式...");
        
        File currentDir = new File(System.getProperty("user.dir"));
        Path jarPath = Paths.get(currentDir.getAbsolutePath(), JAR_FILE_NAME);
        
        System.out.println("直接下載到: " + jarPath.toString());
        
        if (Files.exists(jarPath)) {
            Files.delete(jarPath);
        }
        
        String[] downloadUrls = {
            "https://github.com/l-zch/sessionflow/releases/latest/download/sessionflow.jar",
        };
        
        Exception lastException = null;
        
        for (String url : downloadUrls) {
            try {
                updateStatus("嘗試從 " + url + " 下載...");
                
                ProcessBuilder pb = new ProcessBuilder("curl", "-L", "-o", jarPath.toString(), url);
                pb.directory(currentDir);
                
                Process process = pb.start();
                
                Thread outputThread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("DirectDownload: " + line);
                        }
                    } catch (IOException e) {
                        System.err.println("讀取下載輸出時發生錯誤: " + e.getMessage());
                    }
                });
                outputThread.start();
                
                int exitCode = process.waitFor();
                outputThread.join(5000);
                
                if (exitCode == 0 && Files.exists(jarPath)) {
                    validateDownloadedFile(jarPath);
                    System.out.println("直接下載成功，文件大小: " + Files.size(jarPath) + " bytes");
                    return;
                }
                
            } catch (Exception e) {
                lastException = e;
                System.err.println("從 " + url + " 下載失敗: " + e.getMessage());
                
                if (Files.exists(jarPath)) {
                    try {
                        Files.delete(jarPath);
                    } catch (Exception ignored) {}
                }
            }
        }
        
        throw new RuntimeException("所有下載方式都失敗了。最後的錯誤: " + 
            (lastException != null ? lastException.getMessage() : "未知錯誤"));
    }
    
    private void validateDownloadedFile(Path jarPath) throws Exception {
        if (!Files.exists(jarPath)) {
            throw new RuntimeException("下載完成後找不到 " + JAR_FILE_NAME + " 文件");
        }
        
        long fileSize = Files.size(jarPath);
        System.out.println("檢查文件大小: " + fileSize + " bytes");
        
        if (fileSize < 1024) {
            String content = "";
            try {
                content = Files.readString(jarPath);
            } catch (Exception e) {
                // 忽略讀取錯誤
            }
            Files.delete(jarPath);
            throw new RuntimeException("下載的文件無效（大小: " + fileSize + " bytes）\n內容: " + content);
        }
        
        System.out.println("驗證 JAR 文件格式...");
        byte[] fileHeader = new byte[4];
        try (InputStream is = Files.newInputStream(jarPath)) {
            int bytesRead = is.read(fileHeader);
            if (bytesRead < 4 || 
                fileHeader[0] != 0x50 || fileHeader[1] != 0x4B || 
                fileHeader[2] != 0x03 || fileHeader[3] != 0x04) {
                Files.delete(jarPath);
                throw new RuntimeException("下載的文件不是有效的 JAR 文件（檔案頭不正確）");
            }
        }
    }
    
    private void startSessionFlow() throws Exception {
        if (sessionflowProcess != null) return;

        updateStatus("啟動 SessionFlow 服務...");
        
        File currentDir = new File(System.getProperty("user.dir"));
        File jarFile = new File(currentDir, JAR_FILE_NAME);
        String jarAbsolutePath = jarFile.getAbsolutePath();
        
        System.out.println("當前工作目錄: " + currentDir.getAbsolutePath());
        System.out.println("JAR文件路徑: " + jarAbsolutePath);
        System.out.println("JAR文件是否存在: " + jarFile.exists());
        
        if (!jarFile.exists()) {
            throw new RuntimeException("找不到 SessionFlow JAR 文件: " + jarAbsolutePath);
        }
        
        String javaVersion = System.getProperty("java.version");
        System.out.println("Java版本: " + javaVersion);
        
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarAbsolutePath);
        pb.directory(currentDir);
        
        sessionflowProcess = pb.start();
        
        // 在後台讀取進程輸出
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(sessionflowProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("SessionFlow: " + line);
                }
            } catch (IOException e) {
                // 進程結束時會拋出異常，這是正常的
            }
        }).start();
        
        new Thread(() -> {
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(sessionflowProcess.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.err.println("SessionFlow Error: " + line);
                }
            } catch (IOException e) {
                // 進程結束時會拋出異常，這是正常的
            }
        }).start();
    }
    
    private void waitForService() throws Exception {
        updateStatus("等待服務啟動...");
        
        for (int i = 0; i < 30; i++) {
            try {
                URL url = new URL("http://localhost:" + PORT);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                
                int responseCode = connection.getResponseCode();
                if (responseCode == 200 || responseCode == 404) {
                    return;
                }
            } catch (Exception e) {
                // 服務還未啟動，繼續等待
            }
            
            Thread.sleep(1000);
            updateStatus("等待服務啟動... (" + (i + 1) + "/30)");
        }
        
        throw new RuntimeException("服務啟動超時");
    }
    
    private void updateStatus(String message) {
        if (statusListener != null) {
            statusListener.onStatusUpdate(message);
        }
    }
    
    public void terminate() {
        if (sessionflowProcess != null && sessionflowProcess.isAlive()) {
            System.out.println("正在終止SessionFlow進程...");
            
            sessionflowProcess.destroy();
            
            try {
                if (sessionflowProcess.waitFor(3, TimeUnit.SECONDS)) {
                    System.out.println("SessionFlow進程已正常終止");
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            System.out.println("正常終止失敗，嘗試強制終止SessionFlow進程...");
            sessionflowProcess.destroyForcibly();
            
            try {
                if (sessionflowProcess.waitFor(2, TimeUnit.SECONDS)) {
                    System.out.println("SessionFlow進程已強制終止");
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        try {
            System.out.println("嘗試使用系統命令終止SessionFlow進程...");
            killSessionFlowProcessByName();
        } catch (Exception e) {
            System.err.println("使用系統命令終止SessionFlow進程失敗: " + e.getMessage());
        }
    }
    
    private void killSessionFlowProcessByName() throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;
        
        if (os.contains("win")) {
            pb = new ProcessBuilder("cmd", "/c", 
                "for /f \"tokens=2\" %i in ('tasklist /fi \"imagename eq java.exe\" /fo csv ^| findstr sessionflow') do taskkill /f /pid %i");
        } else if (os.contains("mac") || os.contains("darwin")) {
            pb = new ProcessBuilder("sh", "-c", 
                "pkill -f 'java.*sessionflow.jar' || true");
        } else {
            pb = new ProcessBuilder("sh", "-c", 
                "pkill -f 'java.*sessionflow.jar' || true");
        }
        
        Process killProcess = pb.start();
        boolean finished = killProcess.waitFor(5, TimeUnit.SECONDS);
        
        if (finished) {
            int exitCode = killProcess.exitValue();
            if (exitCode == 0) {
                System.out.println("成功使用系統命令終止SessionFlow進程");
            } else {
                System.out.println("系統命令執行完成，退出碼: " + exitCode);
            }
        } else {
            System.out.println("系統命令執行超時");
            killProcess.destroyForcibly();
        }
    }
    
    public int getPort() {
        return PORT;
    }
} 