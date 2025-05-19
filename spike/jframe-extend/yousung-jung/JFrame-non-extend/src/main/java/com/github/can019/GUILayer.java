package com.github.can019;

import javax.swing.*;
import java.awt.*;

public class GUILayer extends Layer{
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private final static String DEFAULT_FRAME_NAME = "Chat";

    public GUILayer() {
        initFrame();
        initChatLogArea();
        initMessageInputArea();
        frame.setVisible(true);
    }

    private void initFrame() {
        frame = new JFrame(DEFAULT_FRAME_NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
    }

    private void initChatLogArea () {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        frame.add(scrollPane, BorderLayout.CENTER);
    }

    private void initMessageInputArea() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("send");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // 버튼 이벤트 처리
        sendButton.addActionListener(e -> sendMessage());

        // 엔터 키 입력 처리
        inputField.addActionListener(e -> sendMessage());
    }


    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            chatArea.append("[send] " + message + "\n");
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUILayer());
    }
}
