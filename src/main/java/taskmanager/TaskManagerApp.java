package taskmanager;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import pet.DesktopPet;

public class TaskManagerApp extends JFrame {

    private static final int PORT = 53551;
    private static final String JAR_FILE_NAME = "sessionflow.jar";
    private static final String DOWNLOAD_SCRIPT_URL = "https://raw.githubusercontent.com/l-zch/sessionflow/main/scripts/download-latest-release.sh";
    
    private DesktopPet desktopPet;
    private int petIndex;
    private JLabel statusLabel;
    private JButton openBrowserButton;
    private JButton retryButton;
    private JButton hideButton;
    private Process sessionflowProcess;
    
    // 與 PomodoroApp 和 Stopwatch 統一的配色方案
    private final Color PRIMARY_LIGHT = new Color(0xFEB098); // #FEB098
    private final Color PRIMARY_COLOR = new Color(0xF26B49); // #F26B49
    private final Color PRIMARY_DARK = new Color(0xCC553A); // #CC553A
    private final Color BACKGROUND_COLOR = new Color(0xFAFAF9); // stone-50
    private final Color TEXT_COLOR = new Color(0x374151); // neutral-700
    private final Color BORDER_COLOR = new Color(0xD4D4D8); // neutral-300

    // 位置跟隨相關
    private Timer positionTimer;
    private int followingDogIndex = -1;

    public TaskManagerApp(DesktopPet desktopPet, int petIndex) {
        this.desktopPet = desktopPet;
        this.petIndex = petIndex;
        this.followingDogIndex = petIndex;
        
        initializeUI();
        
        // 設置初始位置並開始位置跟隨
        if (desktopPet != null) {
            setInitialPosition();
            startPositionTracking();
        }
        
        // 開始檢查和啟動流程
        initializeSessionFlow();
    }

    public TaskManagerApp() {
        this(null, -1);
    }

    private void initializeUI() {
        setTitle("任務管理");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        
        // 設定現代化的主要面板
        ModernTaskManagerPanel mainPanel = new ModernTaskManagerPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        
        // 標題面板
        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // 內容面板
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // 按鈕面板
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // 設定整個視窗為圓角
        SwingUtilities.invokeLater(() -> {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));
        });
        
        // 視窗大小改變時自動調整圓角
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));
            }
        });
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        // 標題標籤
        JLabel titleLabel = new JLabel("任務管理系統", JLabel.CENTER);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        // 隱藏按鈕
        hideButton = createModernButton("×", 25, 25, 20);
        hideButton.setFont(new Font("Arial", Font.BOLD, 16));
        hideButton.setBackground(new Color(255, 255, 255, 100));
        hideButton.setForeground(new Color(100, 100, 100));
        hideButton.addActionListener(e -> setVisible(false));
        hideButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hideButton.setBackground(new Color(255, 0, 0, 150));
                hideButton.setForeground(Color.WHITE);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hideButton.setBackground(new Color(255, 255, 255, 100));
                hideButton.setForeground(new Color(100, 100, 100));
            }
        });
        
        JPanel hidePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        hidePanel.setOpaque(false);
        hidePanel.add(hideButton);
        
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(hidePanel, BorderLayout.EAST);
        
        return titlePanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // 狀態標籤
        statusLabel = new JLabel("正在初始化...", JLabel.CENTER);
        statusLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        
        // URL 顯示標籤
        JLabel urlLabel = new JLabel("服務地址: http://localhost:" + PORT, JLabel.CENTER);
        urlLabel.setFont(new Font("SF Pro Display", Font.BOLD, 13));
        urlLabel.setForeground(PRIMARY_COLOR);
        urlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        urlLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(statusLabel);
        contentPanel.add(urlLabel);
        contentPanel.add(Box.createVerticalGlue());
        
        return contentPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        
        openBrowserButton = createModernButton("開啟瀏覽器", 100, 35, 14);
        openBrowserButton.setBackground(PRIMARY_COLOR);
        openBrowserButton.setForeground(Color.WHITE);
        openBrowserButton.setEnabled(false); // 初始時禁用
        openBrowserButton.addActionListener(e -> openInBrowser());
        
        retryButton = createModernButton("重試", 60, 35, 14);
        retryButton.setBackground(PRIMARY_DARK);
        retryButton.setForeground(Color.WHITE);
        retryButton.setVisible(false); // 初始時隐藏
        retryButton.addActionListener(e -> {
            retryButton.setVisible(false);
            openBrowserButton.setEnabled(false);
            initializeSessionFlow();
        });
        
        addButtonHoverEffect(openBrowserButton, PRIMARY_COLOR);
        addButtonHoverEffect(retryButton, PRIMARY_DARK);
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(openBrowserButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(retryButton);
        buttonPanel.add(Box.createHorizontalGlue());
        
        return buttonPanel;
    }

    private JButton createModernButton(String text, int width, int height, int fontSize) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
                } else {
                    g2.setColor(getBackground());
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // 繪製文字
                FontMetrics fm = g2.getFontMetrics();
                Rectangle textRect = new Rectangle(
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent(),
                    fm.stringWidth(getText()),
                    fm.getHeight()
                );
                
                g2.setColor(getForeground());
                g2.drawString(getText(), textRect.x, textRect.y);
            }
        };
        
        button.setFont(new Font("SF Pro Display", Font.BOLD, fontSize));
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void addButtonHoverEffect(JButton button, Color originalColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(PRIMARY_LIGHT);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(originalColor);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(PRIMARY_DARK);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(button.contains(e.getPoint()) ? PRIMARY_LIGHT : originalColor);
                }
            }
        });
    }

    private void initializeSessionFlow() {
        SwingUtilities.invokeLater(() -> {
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
                    SwingUtilities.invokeLater(() -> {
                        openBrowserButton.setEnabled(true);
                    });
                    
                } catch (Exception e) {
                    String errorMsg = "初始化失敗: " + e.getMessage();
                    updateStatus(errorMsg);
                    e.printStackTrace();
                    
                    // 顯示重試按鈕
                    SwingUtilities.invokeLater(() -> {
                        if (retryButton != null) {
                            retryButton.setVisible(true);
                        }
                        
                        JOptionPane.showMessageDialog(TaskManagerApp.this,
                            "SessionFlow 初始化失敗:\n" + e.getMessage() + 
                            "\n\n請檢查網路連線或點擊重試按鈕",
                            "初始化錯誤",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        });
    }

    private void downloadSessionFlow() throws Exception {
        updateStatus("正在下載 SessionFlow...");
        
        // 確保在正確的目錄中工作
        File currentDir = new File(System.getProperty("user.dir"));
        Path jarPath = Paths.get(currentDir.getAbsolutePath(), JAR_FILE_NAME);
        
        System.out.println("下載目標目錄: " + currentDir.getAbsolutePath());
        System.out.println("JAR文件完整路徑: " + jarPath.toString());
        
        // 先刪除可能存在的損壞文件
        if (Files.exists(jarPath)) {
            Files.delete(jarPath);
            System.out.println("已刪除舊的 " + JAR_FILE_NAME + " 文件");
        }
        
        // 使用原始的直接管道方式，確保完整執行
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", 
            "curl -sL " + DOWNLOAD_SCRIPT_URL + " | bash");
        pb.directory(currentDir);
        
        System.out.println("開始執行下載腳本...");
        Process process = pb.start();
        
        // 收集輸出信息
        StringBuilder outputBuilder = new StringBuilder();
        StringBuilder errorBuilder = new StringBuilder();
        
        // 讀取輸出 - 使用同步方式確保完整讀取
        Thread outputThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Download: " + line);
                    outputBuilder.append(line).append("\n");
                    // 更新狀態顯示下載進度
                    if (line.contains("%") || line.contains("MB") || line.contains("KB")) {
                        updateStatus("下載中: " + line.trim());
                    }
                }
            } catch (IOException e) {
                System.err.println("讀取輸出時發生錯誤: " + e.getMessage());
            }
        });
        
        // 讀取錯誤輸出
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
        
        // 等待進程完全結束
        System.out.println("等待下載腳本完成...");
        int exitCode = process.waitFor();
        System.out.println("下載腳本結束，退出碼: " + exitCode);
        
        // 確保所有輸出都被讀取完畢
        try {
            outputThread.join(10000); // 等待最多10秒
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
        
        // 給文件系統一些時間來完成寫入操作
        System.out.println("等待文件寫入完成...");
        Thread.sleep(2000);
        
        // 驗證文件是否存在且有效
        if (!Files.exists(jarPath)) {
            throw new RuntimeException("下載完成後找不到 " + JAR_FILE_NAME + " 文件");
        }
        
        // 檢查文件大小
        long fileSize = Files.size(jarPath);
        System.out.println("檢查文件大小: " + fileSize + " bytes");
        
        if (fileSize < 1024) { // 小於1KB，可能是錯誤文件
            String content = "";
            try {
                content = Files.readString(jarPath);
            } catch (Exception e) {
                // 忽略讀取錯誤
            }
            Files.delete(jarPath); // 刪除無效文件
            throw new RuntimeException("下載的文件無效（大小: " + fileSize + " bytes）\n內容: " + content);
        }
        
        // 驗證 JAR 文件頭
        System.out.println("驗證 JAR 文件格式...");
        try {
            byte[] fileHeader = new byte[4];
            try (InputStream is = Files.newInputStream(jarPath)) {
                int bytesRead = is.read(fileHeader);
                if (bytesRead < 4 || 
                    fileHeader[0] != 0x50 || fileHeader[1] != 0x4B || 
                    fileHeader[2] != 0x03 || fileHeader[3] != 0x04) {
                    Files.delete(jarPath); // 刪除無效文件
                    throw new RuntimeException("下載的文件不是有效的 JAR 文件（檔案頭不正確）");
                }
            }
        } catch (Exception e) {
            if (Files.exists(jarPath)) {
                Files.delete(jarPath);
            }
            throw new RuntimeException("驗證 JAR 文件時發生錯誤: " + e.getMessage());
        }
        
        System.out.println("SessionFlow 下載並驗證完成，文件大小: " + fileSize + " bytes");
    }

    private void downloadSessionFlowDirect() throws Exception {
        updateStatus("使用直接下載方式...");
        
        // 確保在正確的目錄中工作
        File currentDir = new File(System.getProperty("user.dir"));
        Path jarPath = Paths.get(currentDir.getAbsolutePath(), JAR_FILE_NAME);
        
        System.out.println("直接下載到: " + jarPath.toString());
        
        // 刪除可能存在的損壞文件
        if (Files.exists(jarPath)) {
            Files.delete(jarPath);
        }
        
        // 嘗試直接從 GitHub releases 下載最新版本
        String[] downloadUrls = {
            "https://github.com/l-zch/sessionflow/releases/latest/download/sessionflow.jar",
            "https://github.com/l-zch/sessionflow/releases/download/v0.1.0/sessionflow.jar"
        };
        
        Exception lastException = null;
        
        for (String url : downloadUrls) {
            try {
                updateStatus("嘗試從 " + url + " 下載...");
                
                ProcessBuilder pb = new ProcessBuilder("curl", "-L", "-o", jarPath.toString(), url);
                pb.directory(currentDir);
                
                Process process = pb.start();
                
                // 讀取輸出
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
                    // 驗證下載的文件
                    long fileSize = Files.size(jarPath);
                    if (fileSize > 1024) {
                        // 驗證 JAR 文件頭
                        byte[] fileHeader = new byte[4];
                        try (InputStream is = Files.newInputStream(jarPath)) {
                            if (is.read(fileHeader) == 4 &&
                                fileHeader[0] == 0x50 && fileHeader[1] == 0x4B &&
                                fileHeader[2] == 0x03 && fileHeader[3] == 0x04) {
                                System.out.println("直接下載成功，文件大小: " + fileSize + " bytes");
                                return; // 下載成功
                            }
                        }
                    }
                    
                    // 文件無效，刪除並嘗試下一個URL
                    Files.delete(jarPath);
                    throw new RuntimeException("下載的文件無效");
                }
                
            } catch (Exception e) {
                lastException = e;
                System.err.println("從 " + url + " 下載失敗: " + e.getMessage());
                
                // 清理可能的無效文件
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

    private void startSessionFlow() throws Exception {
        updateStatus("啟動 SessionFlow 服務...");
        
        // 使用絕對路徑確保找到正確的文件
        File currentDir = new File(System.getProperty("user.dir"));
        File jarFile = new File(currentDir, JAR_FILE_NAME);
        String jarAbsolutePath = jarFile.getAbsolutePath();
        
        System.out.println("當前工作目錄: " + currentDir.getAbsolutePath());
        System.out.println("JAR文件路徑: " + jarAbsolutePath);
        System.out.println("JAR文件是否存在: " + jarFile.exists());
        
        if (!jarFile.exists()) {
            throw new RuntimeException("找不到 SessionFlow JAR 文件: " + jarAbsolutePath);
        }
        
        // 檢查 Java 版本
        String javaVersion = System.getProperty("java.version");
        System.out.println("Java版本: " + javaVersion);
        
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarAbsolutePath);
        pb.directory(currentDir);
        
        sessionflowProcess = pb.start();
        
        // 在後台讀取進程輸出，避免緩衝區滿
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
        
        // 等待最多30秒讓服務啟動
        for (int i = 0; i < 30; i++) {
            try {
                // 嘗試連接到服務
                java.net.URL url = new java.net.URL("http://localhost:" + PORT);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                
                int responseCode = connection.getResponseCode();
                if (responseCode == 200 || responseCode == 404) { // 404也表示服務在運行
                    return; // 服務已啟動
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
        SwingUtilities.invokeLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(message);
            }
        });
    }

    private void openInBrowser() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                String url = "http://localhost:" + PORT;
                Desktop.getDesktop().browse(new URI(url));
                System.out.println("已在瀏覽器中打開: " + url);
            } else {
                // 如果不支援 Desktop.browse，顯示錯誤訊息
                JOptionPane.showMessageDialog(this, 
                    "系統不支援自動開啟瀏覽器\n請手動開啟瀏覽器並前往：\nhttp://localhost:" + PORT, 
                    "無法開啟瀏覽器", 
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            // 如果開啟失敗，顯示錯誤訊息和 URL
            JOptionPane.showMessageDialog(this, 
                "無法開啟瀏覽器: " + e.getMessage() + "\n請手動開啟瀏覽器並前往：\nhttp://localhost:" + PORT, 
                "開啟瀏覽器失敗", 
                JOptionPane.ERROR_MESSAGE);
            System.err.println("開啟瀏覽器失敗: " + e.getMessage());
        }
    }

    private void setInitialPosition() {
        if (desktopPet != null) {
            Point petLocation;
            Dimension petSize;
            
            if (followingDogIndex >= 0) {
                // 跟隨特定寵物
                petLocation = desktopPet.getPetLocation(followingDogIndex);
                petSize = desktopPet.getPetSize(followingDogIndex);
            } else {
                // 跟隨石頭（主屋）
                petLocation = desktopPet.getStoneLocation();
                petSize = desktopPet.getStoneSize();
            }
            
            // 計算視窗應該出現的位置（寵物正上方）
            int x = petLocation.x + (petSize.width - getWidth()) / 2; // 水平居中對齊
            int y = petLocation.y - getHeight() - 10; // 放在寵物上方，留10像素間距
            
            // 確保視窗不會超出螢幕邊界
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (x + getWidth() > screenSize.width) {
                x = screenSize.width - getWidth(); // 右邊界對齊
            }
            if (x < 0) {
                x = 0; // 左邊界對齊
            }
            if (y < 0) {
                y = petLocation.y + petSize.height + 10; // 如果上方空間不足，放到下方
            }
            if (y + getHeight() > screenSize.height) {
                y = screenSize.height - getHeight(); // 底部邊界對齊
            }
            
            setLocation(x, y);
        }
    }

    private void startPositionTracking() {
        if (positionTimer != null) {
            positionTimer.cancel();
        }
        
        positionTimer = new Timer();
        positionTimer.scheduleAtFixedRate(new TimerTask() {
            private Point lastLocation = null;
            
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (!isVisible() || desktopPet == null) {
                        return;
                    }
                    
                                         Point currentPetLocation;
                     Dimension petSize;
                     
                     if (followingDogIndex >= 0) {
                         // 跟隨特定寵物
                         currentPetLocation = desktopPet.getPetLocation(followingDogIndex);
                         petSize = desktopPet.getPetSize(followingDogIndex);
                     } else {
                         // 跟隨石頭（主屋）
                         currentPetLocation = desktopPet.getStoneLocation();
                         petSize = desktopPet.getStoneSize();
                     }
                     
                     // 只有當寵物位置改變時才更新視窗位置
                     if (lastLocation == null || !lastLocation.equals(currentPetLocation)) {
                         lastLocation = new Point(currentPetLocation);
                                                 int x = currentPetLocation.x + (petSize.width - getWidth()) / 2; // 水平居中對齊
                         int y = currentPetLocation.y - getHeight() - 10; // 放在寵物上方，留10像素間距
                         
                         // 確保視窗不會超出螢幕邊界
                         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                         if (x + getWidth() > screenSize.width) {
                             x = screenSize.width - getWidth(); // 右邊界對齊
                         }
                         if (x < 0) {
                             x = 0; // 左邊界對齊
                         }
                         if (y < 0) {
                             y = currentPetLocation.y + petSize.height + 10; // 如果上方空間不足，放到下方
                         }
                         if (y + getHeight() > screenSize.height) {
                             y = screenSize.height - getHeight(); // 底部邊界對齊
                         }
                        
                        setLocation(x, y);
                    }
                });
            }
        }, 0, 50); // 每50毫秒檢查一次
    }

    @Override
    public void dispose() {
        // 停止位置跟隨計時器
        if (positionTimer != null) {
            positionTimer.cancel();
            positionTimer = null;
        }
        
        // 終止 SessionFlow 進程
        terminateSessionFlowProcess();
        
        // 從DesktopPet的追蹤列表中移除自己
        if (desktopPet != null) {
            desktopPet.removeTaskManagerApp(this);
        }
        
        super.dispose();
    }
    
    // 新增：強化的SessionFlow進程終止方法
    private void terminateSessionFlowProcess() {
        if (sessionflowProcess != null && sessionflowProcess.isAlive()) {
            System.out.println("正在終止SessionFlow進程...");
            
            // 方法1：嘗試正常終止
            sessionflowProcess.destroy();
            
            try {
                // 等待進程結束，最多等待3秒
                if (sessionflowProcess.waitFor(3, java.util.concurrent.TimeUnit.SECONDS)) {
                    System.out.println("SessionFlow進程已正常終止");
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // 方法2：強制終止
            System.out.println("正常終止失敗，嘗試強制終止SessionFlow進程...");
            sessionflowProcess.destroyForcibly();
            
            try {
                // 再等待2秒
                if (sessionflowProcess.waitFor(2, java.util.concurrent.TimeUnit.SECONDS)) {
                    System.out.println("SessionFlow進程已強制終止");
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 方法3：使用系統命令查找並終止所有sessionflow相關進程
        try {
            System.out.println("嘗試使用系統命令終止SessionFlow進程...");
            killSessionFlowProcessByName();
        } catch (Exception e) {
            System.err.println("使用系統命令終止SessionFlow進程失敗: " + e.getMessage());
        }
    }
    
    // 新增：使用系統命令查找並終止SessionFlow進程
    private void killSessionFlowProcessByName() throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;
        
        if (os.contains("win")) {
            // Windows系統
            pb = new ProcessBuilder("cmd", "/c", 
                "for /f \"tokens=2\" %i in ('tasklist /fi \"imagename eq java.exe\" /fo csv ^| findstr sessionflow') do taskkill /f /pid %i");
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
                System.out.println("成功使用系統命令終止SessionFlow進程");
            } else {
                System.out.println("系統命令執行完成，退出碼: " + exitCode);
            }
        } else {
            System.out.println("系統命令執行超時");
            killProcess.destroyForcibly();
        }
    }
}

class ModernTaskManagerPanel extends JPanel {
    public ModernTaskManagerPanel() {
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 繪製帶陰影的圓角矩形背景
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 25, 25);
        
        g2d.setColor(new Color(0xFAFAF9));
        g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 25, 25);
        
        // 繪製邊框
        g2d.setColor(new Color(0xD4D4D8));
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 25, 25);
    }
}
