import javax.swing.*;
import java.awt.*;
import java.io.File;

public class main {
    public static void main(String[] args) {
        // 使用 SwingUtilities.invokeLater 确保在 EDT 线程中创建和显示 GUI
        SwingUtilities.invokeLater(() -> {
            // 创建主窗口
            JFrame frame = new JFrame("選擇你的角色");
            
            // 设置窗口大小
            frame.setSize(800, 450);
            
            // 设置窗口关闭操作
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // 设置窗口居中显示
            frame.setLocationRelativeTo(null);

            // 创建主面板，使用BorderLayout
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(new Color(240, 240, 240));
            
            // 创建2x2的网格布局面板
            JPanel gridPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            gridPanel.setBackground(new Color(240, 240, 240));
            
            // 创建角色显示面板
            JPanel characterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            characterPanel.setBackground(new Color(240, 240, 240));
            characterPanel.setPreferredSize(new Dimension(800, 100));
            
            // 创建角色标签
            JLabel dogStandLabel = new JLabel();
            JLabel catStandLabel = new JLabel();
            JLabel duckStandLabel = new JLabel();
            JLabel mouseStandLabel = new JLabel();
            
            // 设置所有标签初始为不可见
            dogStandLabel.setVisible(false);
            catStandLabel.setVisible(false);
            duckStandLabel.setVisible(false);
            mouseStandLabel.setVisible(false);
            
            // 添加标签到角色面板
            characterPanel.add(dogStandLabel);
            characterPanel.add(catStandLabel);
            characterPanel.add(duckStandLabel);
            characterPanel.add(mouseStandLabel);
            
            // 创建4个面板
            JPanel panel1 = new JPanel();
            panel1.setBackground(new Color(240, 240, 240));
            panel1.setLayout(new BorderLayout(0, 0));
            JPanel square1 = new JPanel();
            square1.setPreferredSize(new Dimension(120, 80));
            square1.setMaximumSize(new Dimension(120, 80));
            square1.setMinimumSize(new Dimension(120, 80));
            square1.setBackground(Color.WHITE);
            square1.setLayout(new BoxLayout(square1, BoxLayout.X_AXIS));
            // 添加狗狗图片
            ImageIcon dogIcon = new ImageIcon("picture/dog_pic.png");
            Image dogImg = dogIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            JLabel dogLabel = new JLabel(new ImageIcon(dogImg));
            dogLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JCheckBox checkBox1 = new JCheckBox();
            checkBox1.setBackground(Color.WHITE);
            square1.add(checkBox1);
            square1.add(Box.createHorizontalStrut(5));
            square1.add(dogLabel);
            panel1.add(square1, BorderLayout.WEST);
            
            // 添加狗狗说明文字
            JTextArea dogText = new JTextArea("狗狗\n活潑可愛\n忠誠可靠\n喜歡玩耍");
            dogText.setEditable(false);
            dogText.setBackground(Color.WHITE);
            dogText.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
            JPanel dogTextPanel = new JPanel(new BorderLayout());
            dogTextPanel.setBackground(new Color(240, 240, 240));
            JPanel dogTextWrapper = new JPanel(new GridBagLayout());
            dogTextWrapper.setBackground(Color.WHITE);
            dogTextWrapper.add(dogText);
            dogTextPanel.add(dogTextWrapper, BorderLayout.CENTER);
            panel1.add(dogTextPanel, BorderLayout.CENTER);

            JPanel panel2 = new JPanel();
            panel2.setBackground(new Color(240, 240, 240));
            panel2.setLayout(new BorderLayout(0, 0));
            JPanel square2 = new JPanel();
            square2.setPreferredSize(new Dimension(120, 80));
            square2.setMaximumSize(new Dimension(120, 80));
            square2.setMinimumSize(new Dimension(120, 80));
            square2.setBackground(Color.WHITE);
            square2.setLayout(new BoxLayout(square2, BoxLayout.X_AXIS));
            // 添加猫咪图片
            ImageIcon catIcon = new ImageIcon("picture/cat_pic.png");
            Image catImg = catIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            JLabel catLabel = new JLabel(new ImageIcon(catImg));
            catLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JCheckBox checkBox2 = new JCheckBox();
            checkBox2.setBackground(Color.WHITE);
            square2.add(checkBox2);
            square2.add(Box.createHorizontalStrut(5));
            square2.add(catLabel);
            panel2.add(square2, BorderLayout.WEST);
            
            // 添加猫咪说明文字
            JTextArea catText = new JTextArea("貓咪\n優雅獨立\n聰明機智\n喜歡安靜");
            catText.setEditable(false);
            catText.setBackground(Color.WHITE);
            catText.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
            JPanel catTextPanel = new JPanel(new BorderLayout());
            catTextPanel.setBackground(new Color(240, 240, 240));
            JPanel catTextWrapper = new JPanel(new GridBagLayout());
            catTextWrapper.setBackground(Color.WHITE);
            catTextWrapper.add(catText);
            catTextPanel.add(catTextWrapper, BorderLayout.CENTER);
            panel2.add(catTextPanel, BorderLayout.CENTER);

            JPanel panel3 = new JPanel();
            panel3.setBackground(new Color(240, 240, 240));
            panel3.setLayout(new BorderLayout(0, 0));
            JPanel square3 = new JPanel();
            square3.setPreferredSize(new Dimension(120, 80));
            square3.setMaximumSize(new Dimension(120, 80));
            square3.setMinimumSize(new Dimension(120, 80));
            square3.setBackground(Color.WHITE);
            square3.setLayout(new BoxLayout(square3, BoxLayout.X_AXIS));
            // 添加鸭子图片
            ImageIcon duckIcon = new ImageIcon("picture/duck_pic.jpg");
            Image duckImg = duckIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            JLabel duckLabel = new JLabel(new ImageIcon(duckImg));
            duckLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JCheckBox checkBox3 = new JCheckBox();
            checkBox3.setBackground(Color.WHITE);
            square3.add(checkBox3);
            square3.add(Box.createHorizontalStrut(5));
            square3.add(duckLabel);
            panel3.add(square3, BorderLayout.WEST);
            
            // 添加鸭子说明文字
            JTextArea duckText = new JTextArea("鴨子\n活潑好動\n喜歡游泳\n群居生活");
            duckText.setEditable(false);
            duckText.setBackground(Color.WHITE);
            duckText.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
            JPanel duckTextPanel = new JPanel(new BorderLayout());
            duckTextPanel.setBackground(new Color(240, 240, 240));
            JPanel duckTextWrapper = new JPanel(new GridBagLayout());
            duckTextWrapper.setBackground(Color.WHITE);
            duckTextWrapper.add(duckText);
            duckTextPanel.add(duckTextWrapper, BorderLayout.CENTER);
            panel3.add(duckTextPanel, BorderLayout.CENTER);

            JPanel panel4 = new JPanel();
            panel4.setBackground(new Color(240, 240, 240));
            panel4.setLayout(new BorderLayout(0, 0));
            JPanel square4 = new JPanel();
            square4.setPreferredSize(new Dimension(120, 80));
            square4.setMaximumSize(new Dimension(120, 80));
            square4.setMinimumSize(new Dimension(120, 80));
            square4.setBackground(Color.WHITE);
            square4.setLayout(new BoxLayout(square4, BoxLayout.X_AXIS));
            // 添加老鼠图片
            ImageIcon mouseIcon = new ImageIcon("picture/mouse_pic.jpg");
            Image mouseImg = mouseIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            JLabel mouseLabel = new JLabel(new ImageIcon(mouseImg));
            mouseLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JCheckBox checkBox4 = new JCheckBox();
            checkBox4.setBackground(Color.WHITE);
            square4.add(checkBox4);
            square4.add(Box.createHorizontalStrut(5));
            square4.add(mouseLabel);
            panel4.add(square4, BorderLayout.WEST);
            
            // 添加老鼠说明文字
            JTextArea mouseText = new JTextArea("老鼠\n機智靈活\n適應力強\n喜歡探索");
            mouseText.setEditable(false);
            mouseText.setBackground(Color.WHITE);
            mouseText.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
            JPanel mouseTextPanel = new JPanel(new BorderLayout());
            mouseTextPanel.setBackground(new Color(240, 240, 240));
            JPanel mouseTextWrapper = new JPanel(new GridBagLayout());
            mouseTextWrapper.setBackground(Color.WHITE);
            mouseTextWrapper.add(mouseText);
            mouseTextPanel.add(mouseTextWrapper, BorderLayout.CENTER);
            panel4.add(mouseTextPanel, BorderLayout.CENTER);

            // 将面板添加到网格面板中
            gridPanel.add(panel1);
            gridPanel.add(panel2);
            gridPanel.add(panel3);
            gridPanel.add(panel4);
            
            // 创建按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            buttonPanel.setBackground(new Color(240, 240, 240));
            
            // 创建三个按钮
            JButton button5 = new JButton("程式設定");
            JButton button1 = new JButton("全部勾選");
            JButton button2 = new JButton("取消勾選");
            JButton button4 = new JButton("離開選擇");
            JButton button3 = new JButton("確定選擇");
            JButton button6 = new JButton("關閉程式");  // 新增關閉所有按鈕
            JButton button7 = new JButton("隱藏角色");  // 新增隱藏角色按鈕
            
            // 设置按钮大小
            Dimension buttonSize = new Dimension(90, 30);
            button5.setPreferredSize(buttonSize);
            button1.setPreferredSize(buttonSize);
            button2.setPreferredSize(buttonSize);
            button4.setPreferredSize(buttonSize);
            button3.setPreferredSize(buttonSize);
            button6.setPreferredSize(buttonSize);  // 設置新按鈕大小
            button7.setPreferredSize(buttonSize);  // 設置隱藏角色按鈕大小
            
            // 添加全部选择按钮的事件监听器
            button1.addActionListener(e -> {
                checkBox1.setSelected(true);
                checkBox2.setSelected(true);
                checkBox3.setSelected(true);
                checkBox4.setSelected(true);
            });
            
            // 添加全部取消按钮的事件监听器
            button2.addActionListener(e -> {
                checkBox1.setSelected(false);
                checkBox2.setSelected(false);
                checkBox3.setSelected(false);
                checkBox4.setSelected(false);
            });
            
            // 添加确定按钮的事件监听器
            button3.addActionListener(e -> {
                if (checkBox1.isSelected()) {
                    try {
                        // 创建新窗口
                        JFrame dogFrame = new JFrame("狗狗");
                        dogFrame.setSize(200, 200);
                        dogFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        dogFrame.setLocationRelativeTo(null);
                        dogFrame.setUndecorated(true);  // 移除窗口边框
                        dogFrame.setBackground(new Color(0, 0, 0, 0));  // 设置窗口背景为透明
                        dogFrame.setContentPane(new JPanel() {
                            {
                                setOpaque(false);
                            }
                        });
                        
                        // 添加窗口拖动功能
                        final Point[] dogFramePoint = new Point[1];
                        dogFrame.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mousePressed(java.awt.event.MouseEvent e) {
                                dogFramePoint[0] = e.getPoint();
                            }
                        });
                        dogFrame.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                            public void mouseDragged(java.awt.event.MouseEvent e) {
                                Point p = dogFrame.getLocation();
                                dogFrame.setLocation(p.x + e.getX() - dogFramePoint[0].x, p.y + e.getY() - dogFramePoint[0].y);
                            }
                        });
                        
                        // 创建面板
                        JPanel dogPanel = new JPanel(new GridBagLayout());
                        dogPanel.setBackground(new Color(0, 0, 0, 0));  // 设置透明背景
                        dogPanel.setOpaque(false);  // 确保面板透明
                        
                        // 加载狗狗图片
                        String dogStandPath = "C:\\Users\\user\\Desktop\\woo\\picture\\dog_stand.png";
                        File dogStandFile = new File(dogStandPath);
                        if (dogStandFile.exists()) {
                            ImageIcon dogStandIcon = new ImageIcon(dogStandPath);
                            Image dogStandImg = dogStandIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                            JLabel dogStandImageLabel = new JLabel(new ImageIcon(dogStandImg));
                            dogStandImageLabel.setOpaque(false);  // 确保标签透明
                            dogPanel.add(dogStandImageLabel);
                            
                            // 添加面板到窗口
                            dogFrame.add(dogPanel);
                            
                            // 显示窗口
                            dogFrame.setVisible(true);
                        } else {
                            System.out.println("找不到狗狗图片文件：" + dogStandPath);
                        }
                    } catch (Exception ex) {
                        System.out.println("显示狗狗图片时出错：" + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                
                if (checkBox2.isSelected()) {
                    try {
                        // 创建新窗口
                        JFrame catFrame = new JFrame("貓咪");
                        catFrame.setSize(200, 200);
                        catFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        catFrame.setLocationRelativeTo(null);
                        catFrame.setUndecorated(true);  // 移除窗口边框
                        catFrame.setBackground(new Color(0, 0, 0, 0));  // 设置窗口背景为透明
                        catFrame.setContentPane(new JPanel() {
                            {
                                setOpaque(false);
                            }
                        });
                        
                        // 添加窗口拖动功能
                        final Point[] catFramePoint = new Point[1];
                        catFrame.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mousePressed(java.awt.event.MouseEvent e) {
                                catFramePoint[0] = e.getPoint();
                            }
                        });
                        catFrame.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                            public void mouseDragged(java.awt.event.MouseEvent e) {
                                Point p = catFrame.getLocation();
                                catFrame.setLocation(p.x + e.getX() - catFramePoint[0].x, p.y + e.getY() - catFramePoint[0].y);
                            }
                        });
                        
                        // 创建面板
                        JPanel catPanel = new JPanel(new GridBagLayout());
                        catPanel.setBackground(new Color(0, 0, 0, 0));  // 设置透明背景
                        catPanel.setOpaque(false);  // 确保面板透明
                        
                        // 加载猫咪图片
                        String catStandPath = "C:\\Users\\user\\Desktop\\woo\\picture\\cat_stand.png";
                        File catStandFile = new File(catStandPath);
                        if (catStandFile.exists()) {
                            ImageIcon catStandIcon = new ImageIcon(catStandPath);
                            Image catStandImg = catStandIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                            JLabel catStandImageLabel = new JLabel(new ImageIcon(catStandImg));
                            catStandImageLabel.setOpaque(false);  // 确保标签透明
                            catPanel.add(catStandImageLabel);
                            
                            // 添加面板到窗口
                            catFrame.add(catPanel);
                            
                            // 显示窗口
                            catFrame.setVisible(true);
                        } else {
                            System.out.println("找不到猫咪图片文件：" + catStandPath);
                        }
                    } catch (Exception ex) {
                        System.out.println("显示猫咪图片时出错：" + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                
                if (checkBox3.isSelected()) {
                    try {
                        // 创建新窗口
                        JFrame duckFrame = new JFrame("鴨子");
                        duckFrame.setSize(200, 200);
                        duckFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        duckFrame.setLocationRelativeTo(null);
                        duckFrame.setUndecorated(true);  // 移除窗口边框
                        duckFrame.setBackground(new Color(0, 0, 0, 0));  // 设置窗口背景为透明
                        duckFrame.setContentPane(new JPanel() {
                            {
                                setOpaque(false);
                            }
                        });
                        
                        // 添加窗口拖动功能
                        final Point[] duckFramePoint = new Point[1];
                        duckFrame.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mousePressed(java.awt.event.MouseEvent e) {
                                duckFramePoint[0] = e.getPoint();
                            }
                        });
                        duckFrame.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                            public void mouseDragged(java.awt.event.MouseEvent e) {
                                Point p = duckFrame.getLocation();
                                duckFrame.setLocation(p.x + e.getX() - duckFramePoint[0].x, p.y + e.getY() - duckFramePoint[0].y);
                            }
                        });
                        
                        // 创建面板
                        JPanel duckPanel = new JPanel(new GridBagLayout());
                        duckPanel.setBackground(new Color(0, 0, 0, 0));  // 设置透明背景
                        duckPanel.setOpaque(false);  // 确保面板透明
                        
                        // 加载鸭子图片
                        String duckStandPath = "C:\\Users\\user\\Desktop\\woo\\picture\\duck_stand.png";
                        File duckStandFile = new File(duckStandPath);
                        if (duckStandFile.exists()) {
                            ImageIcon duckStandIcon = new ImageIcon(duckStandPath);
                            Image duckStandImg = duckStandIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                            JLabel duckStandImageLabel = new JLabel(new ImageIcon(duckStandImg));
                            duckStandImageLabel.setOpaque(false);  // 确保标签透明
                            duckPanel.add(duckStandImageLabel);
                            
                            // 添加面板到窗口
                            duckFrame.add(duckPanel);
                            
                            // 显示窗口
                            duckFrame.setVisible(true);
                        } else {
                            System.out.println("找不到鸭子图片文件：" + duckStandPath);
                        }
                    } catch (Exception ex) {
                        System.out.println("显示鸭子图片时出错：" + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                
                if (checkBox4.isSelected()) {
                    try {
                        // 创建新窗口
                        JFrame mouseFrame = new JFrame("老鼠");
                        mouseFrame.setSize(200, 200);
                        mouseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        mouseFrame.setLocationRelativeTo(null);
                        mouseFrame.setUndecorated(true);  // 移除窗口边框
                        mouseFrame.setBackground(new Color(0, 0, 0, 0));  // 设置窗口背景为透明
                        mouseFrame.setContentPane(new JPanel() {
                            {
                                setOpaque(false);
                            }
                        });
                        
                        // 添加窗口拖动功能
                        final Point[] mouseFramePoint = new Point[1];
                        mouseFrame.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mousePressed(java.awt.event.MouseEvent e) {
                                mouseFramePoint[0] = e.getPoint();
                            }
                        });
                        mouseFrame.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                            public void mouseDragged(java.awt.event.MouseEvent e) {
                                Point p = mouseFrame.getLocation();
                                mouseFrame.setLocation(p.x + e.getX() - mouseFramePoint[0].x, p.y + e.getY() - mouseFramePoint[0].y);
                            }
                        });
                        
                        // 创建面板
                        JPanel mousePanel = new JPanel(new GridBagLayout());
                        mousePanel.setBackground(new Color(0, 0, 0, 0));  // 设置透明背景
                        mousePanel.setOpaque(false);  // 确保面板透明
                        
                        // 加载老鼠图片
                        String mouseStandPath = "C:\\Users\\user\\Desktop\\woo\\picture\\mouse_stand.png";
                        File mouseStandFile = new File(mouseStandPath);
                        if (mouseStandFile.exists()) {
                            ImageIcon mouseStandIcon = new ImageIcon(mouseStandPath);
                            Image mouseStandImg = mouseStandIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                            JLabel mouseStandImageLabel = new JLabel(new ImageIcon(mouseStandImg));
                            mouseStandImageLabel.setOpaque(false);  // 确保标签透明
                            mousePanel.add(mouseStandImageLabel);
                            
                            // 添加面板到窗口
                            mouseFrame.add(mousePanel);
                            
                            // 显示窗口
                            mouseFrame.setVisible(true);
                        } else {
                            System.out.println("找不到老鼠图片文件：" + mouseStandPath);
                        }
                    } catch (Exception ex) {
                        System.out.println("显示老鼠图片时出错：" + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });
            
            // 添加按钮到按钮面板
            buttonPanel.add(button5);  // 程式設定
            buttonPanel.add(button1);  // 全部勾選
            buttonPanel.add(button2);  // 取消勾選
            buttonPanel.add(button7);  // 隱藏角色
            buttonPanel.add(button4);  // 離開選擇
            buttonPanel.add(button3);  // 確定選擇
            buttonPanel.add(button6);  // 關閉程式
            
            // 離開按鈕
            button4.addActionListener(e -> {
                frame.dispose();
            });
            
            // 關閉所有按鈕的事件監聽器
            button6.addActionListener(e -> {
                // 關閉所有窗口
                Window[] windows = Window.getWindows();
                for (Window window : windows) {
                    window.dispose();
                }
                // 關閉主程序
                System.exit(0);
            });
            
            // 隱藏角色按鈕的事件監聽器
            button7.addActionListener(e -> {
                // 獲取所有窗口
                Window[] windows = Window.getWindows();
                // 遍歷所有窗口
                for (Window window : windows) {
                    // 如果不是主窗口，則隱藏
                    if (!(window instanceof JFrame) || !((JFrame)window).getTitle().equals("選擇你的角色")) {
                        window.setVisible(false);
                    }
                }
            });
            
            // 将网格面板和按钮面板添加到主面板
            mainPanel.add(gridPanel, BorderLayout.CENTER);
            mainPanel.add(characterPanel, BorderLayout.SOUTH);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            // 将主面板添加到窗口
            frame.add(mainPanel);
            
            // 显示窗口
            frame.setVisible(true);
        });
    }
}
