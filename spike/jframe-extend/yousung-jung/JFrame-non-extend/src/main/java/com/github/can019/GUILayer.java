package com.github.can019;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GUILayer extends Layer{
    private final JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private final static String DEFAULT_FRAME_NAME = "Chat";

    public GUILayer() {
        frame = new JFrame(DEFAULT_FRAME_NAME);
        initFrame();
        initChatLogArea();
        initMessageInputArea();
        frame.setVisible(true);
    }

    private void initFrame() {
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
        sendButton.addActionListener(e -> sendMessageButtonHandler());

        // 엔터 키 입력 처리
        inputField.addActionListener(e -> sendMessageButtonHandler());
    }


    private void sendMessageButtonHandler() {
        String rawMessage = inputField.getText().trim();
        String message = generateChatLogMessage(rawMessage, ChatLogMode.SEND, "(맥 주소)");

        // 채팅 로그 append
        chatArea.append(message);
        inputField.setText("");

        send(rawMessage);
    }

    void send(String message) {

    }

    void receive(String message) {
        generateChatLogMessage(message, ChatLogMode.SEND);
    }


    private String generateChatLogMessage(String rawMessage, ChatLogMode chatLogMode) {
        return generateChatLogMessage(rawMessage, chatLogMode, null);
    }

    private String generateChatLogMessage(String rawMessage, ChatLogMode chatLogMode, String extraMessage) {
        if(Objects.isNull(rawMessage)) throw new IllegalArgumentException("raw message is null");

        StringBuilder sb = new StringBuilder()
                .append("[").append(chatLogMode).append("]").append(" ");

        if (Objects.nonNull(extraMessage) && !extraMessage.isBlank()) {
            sb.append(extraMessage).append(": ");
        }

        sb.append(rawMessage).append("\n");
        return sb.toString();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUILayer());
    }


    private enum ChatLogMode {
        SEND("send"),
        RECEIVE("receive");

        private final String label;

        ChatLogMode(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
