import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 透明浮動層番茄鐘，新增「開始(Start)」「暫停(Pause)」「重置(Reset)」按鈕功能
 */
public class PomodoroApp extends JWindow {
    private static final int WORK_SECONDS = 25 * 60;
    private int remainingSeconds = WORK_SECONDS;

    private final JLabel timerLabel = new JLabel(formatTime(WORK_SECONDS), SwingConstants.CENTER);
    private final JButton startButton = new JButton("Start");
    private final JButton pauseButton = new JButton("Pause");
    private final JButton resetButton = new JButton("Reset");
    private final Timer swingTimer;

    public PomodoroApp() {
        super();
        // Window 設置
        setAlwaysOnTop(true);
        setBackground(new Color(0,0,0,0));
        setSize(240, 320);
        setLocationRelativeTo(null);

        // 時間顯示 Label
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setOpaque(false);

        // 按鈕面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        startButton.setFocusPainted(false);
        pauseButton.setFocusPainted(false);
        resetButton.setFocusPainted(false);
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resetButton);

        // 根面板
        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.add(Box.createVerticalGlue());
        root.add(timerLabel);
        root.add(Box.createVerticalStrut(15));
        root.add(buttonPanel);
        root.add(Box.createVerticalGlue());
        setContentPane(root);

        // Timer 初始化
        swingTimer = new Timer(1000, e -> updateTimer());

        // 按鈕事件綁定
        startButton.addActionListener(e -> startTimer());
        pauseButton.addActionListener(e -> pauseTimer());
        resetButton.addActionListener(e -> resetTimer());
    }

    private void updateTimer() {
        if (remainingSeconds > 0) {
            remainingSeconds--;
            timerLabel.setText(formatTime(remainingSeconds));
            if (remainingSeconds == 0) {
                swingTimer.stop();
                showDoneNotification();
            }
        }
    }

    private void startTimer() {
        if (!swingTimer.isRunning()) {
            swingTimer.start();
        }
    }

    private void pauseTimer() {
        if (swingTimer.isRunning()) {
            swingTimer.stop();
        }
    }

    private void resetTimer() {
        swingTimer.stop();
        remainingSeconds = WORK_SECONDS;
        timerLabel.setText(formatTime(remainingSeconds));
    }

    private void showDoneNotification() {
        JOptionPane.showMessageDialog(
            null,
            "25 分鐘到了，休息一下吧！",
            "番茄鐘完成",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static String formatTime(int sec) {
        int m = sec / 60;
        int s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PomodoroApp app = new PomodoroApp();
            app.setVisible(true);
        });
    }
}

