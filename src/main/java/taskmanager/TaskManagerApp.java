package taskmanager;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import pet.DesktopPet;

/**
 * 任務管理應用程序
 * 作為協調器整合各個組件
 */
public class TaskManagerApp extends JFrame implements 
    SessionFlowManager.StatusListener, 
    TaskManagerUI.UIListener {

    private DesktopPet desktopPet;
    private int petIndex;
    
    // 組件
    private SessionFlowManager sessionFlowManager;
    private TaskManagerUI ui;
    private PetPositionTracker positionTracker;

    public TaskManagerApp(DesktopPet desktopPet, int petIndex) {
        this.desktopPet = desktopPet;
        this.petIndex = petIndex;
        
        initializeComponents();
        setupPositionTracking();
        startInitialization();
    }

    public TaskManagerApp() {
        this(null, -1);
    }

    private void initializeComponents() {
        // 初始化 SessionFlow 管理器
        sessionFlowManager = new SessionFlowManager(this);
        
        // 初始化 UI
        ui = new TaskManagerUI(this, this, sessionFlowManager.getPort());
        
        // 設置視窗關閉事件
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
            }
        });
    }
    
    private void setupPositionTracking() {
        if (desktopPet != null) {
            positionTracker = new PetPositionTracker(desktopPet, this, petIndex);
            positionTracker.setInitialPosition();
            positionTracker.startTracking();
        }
    }
    
    private void startInitialization() {
        sessionFlowManager.initialize();
    }

    // SessionFlowManager.StatusListener implementation
    @Override
    public void onStatusUpdate(String status) {
        ui.updateStatus(status);
    }

    @Override
    public void onError(String error) {
        ui.updateStatus("初始化失敗: " + error);
        ui.showRetryButton(true);
        ui.showErrorDialog(error);
    }

    @Override
    public void onReady() {
        try {
            openInBrowser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ui.setOpenBrowserEnabled(true);
    }

    // TaskManagerUI.UIListener implementation
    @Override
    public void onOpenBrowser() {
        openInBrowser();
    }

    @Override
    public void onRetry() {
        ui.showRetryButton(false);
        ui.setOpenBrowserEnabled(false);
        sessionFlowManager.initialize();
    }

    @Override
    public void onHide() {
        setVisible(false);
    }

    private void openInBrowser() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                String url = "http://localhost:" + sessionFlowManager.getPort();
                Desktop.getDesktop().browse(new URI(url));
                System.out.println("已在瀏覽器中打開: " + url);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "系統不支援自動開啟瀏覽器\n請手動開啟瀏覽器並前往：\nhttp://localhost:" + sessionFlowManager.getPort(), 
                    "無法開啟瀏覽器", 
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "無法開啟瀏覽器: " + e.getMessage() + "\n請手動開啟瀏覽器並前往：\nhttp://localhost:" + sessionFlowManager.getPort(), 
                "開啟瀏覽器失敗", 
                JOptionPane.ERROR_MESSAGE);
            System.err.println("開啟瀏覽器失敗: " + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        // 停止位置跟隨
        if (positionTracker != null) {
            positionTracker.stopTracking();
        }
        
        // 從 DesktopPet 的追蹤列表中移除自己
        if (desktopPet != null) {
            desktopPet.removeTaskManagerApp(this);
        }
        
        super.dispose();
    }
}
