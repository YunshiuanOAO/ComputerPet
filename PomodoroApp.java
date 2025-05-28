import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class PomodoroApp extends JFrame {
    private JLabel timerLabel;
    private JButton startButton;
    private JButton resetButton;
    private JComboBox<String> modeSelector;
    
    private Timer timer;
    private int secondsLeft;
    private boolean isRunning = false;
    
    private final int WORK_TIME = 25 * 60; // 25 minutes in seconds
    private final int SHORT_BREAK = 5 * 60; // 5 minutes in seconds
    private final int LONG_BREAK = 15 * 60; // 15 minutes in seconds
    
    public PomodoroApp() {
        setTitle("番茄鐘");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Timer display
        timerLabel = new JLabel("25:00", JLabel.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 48));
        mainPanel.add(timerLabel, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel();
        
        // Mode selector
        String[] modes = {"工作時間 (25分鐘)", "短休息 (5分鐘)", "長休息 (15分鐘)"};
        modeSelector = new JComboBox<>(modes);
        modeSelector.addActionListener(e -> resetTimer());
        
        // Buttons
        startButton = new JButton("開始");
        startButton.addActionListener(e -> toggleTimer());
        
        resetButton = new JButton("重置");
        resetButton.addActionListener(e -> resetTimer());
        
        controlPanel.add(modeSelector);
        controlPanel.add(startButton);
        controlPanel.add(resetButton);
        
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        resetTimer();
    }
    
    private void toggleTimer() {
        if (isRunning) {
            stopTimer();
            startButton.setText("開始");
        } else {
            startTimer();
            startButton.setText("暫停");
        }
        isRunning = !isRunning;
    }
    
    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (secondsLeft > 0) {
                    secondsLeft--;
                    updateTimerDisplay();
                } else {
                    stopTimer();
                    isRunning = false;
                    startButton.setText("開始");
                    JOptionPane.showMessageDialog(PomodoroApp.this, "時間到了！", "番茄鐘", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }, 1000, 1000);
    }
    
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    private void resetTimer() {
        stopTimer();
        isRunning = false;
        startButton.setText("開始");
        
        int selectedMode = modeSelector.getSelectedIndex();
        switch (selectedMode) {
            case 0: // Work time
                secondsLeft = WORK_TIME;
                break;
            case 1: // Short break
                secondsLeft = SHORT_BREAK;
                break;
            case 2: // Long break
                secondsLeft = LONG_BREAK;
                break;
        }
        
        updateTimerDisplay();
    }
    
    private void updateTimerDisplay() {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;
        SwingUtilities.invokeLater(() -> {
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        });
    }
} 