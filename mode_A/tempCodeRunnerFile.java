
            // JButton buttonC = new JButton("系統設定");
            
            // 設定按鈕大小
            Dimension buttonSize = new Dimension(120, 40); // 原本是 80, 30
            showStoneButton.setPreferredSize(buttonSize);
            buttonB.setPreferredSize(buttonSize);
            // 移除系統設定按鈕大小設定
            // buttonC.setPreferredSize(buttonSize);
            
            // 按鈕事件
            showStoneButton.addActionListener(e -> showHomeImage());
            buttonB.addActionListener(e -> showPetSettings());
            // 移除系統設定按鈕事件
            // buttonC.addActionListener(e -> showSystemSettings());
            
            buttonPanel.add(showStoneButton);
            buttonPanel.add(buttonB);
            // 移除系統設定按鈕加入面板
            // buttonPanel.add(buttonC);
            
            // 關閉按鈕
            JPanel closePanel = new JPanel(new FlowLayout());
            JButton closeButton = new JButton("關閉");
            closeButton.addActionListener(e -> settingsFrame.dispose());
            closePanel.add(closeButton);
            
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(buttonPanel, BorderLayout.CENTER);
            mainPanel.add(closePanel, BorderLayout.SOUTH);
            
            settingsFrame.add(mainPanel);
        }
        
        // 新增：寵物設定視窗
        private void showPetSettings() {
            JFrame petSettingsFrame = new JFrame("寵物設定");
            petSettingsFrame.setSize(800, 600); // 改為 800x600
            petSettingsFrame.setLocationRelativeTo(settingsFrame);
            petSettingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            
            // 標題
            JLabel titleLabel = new JLabel("寵物設定", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            
            // 設定面板
            JPanel settingsPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 改為 3 行
            settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));