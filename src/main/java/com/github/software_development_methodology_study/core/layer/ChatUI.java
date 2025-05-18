package com.github.software_development_methodology_study.core.layer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatUI extends JFrame {

  private JTextArea chatArea;
  private JTextField inputField;
  private JButton sendButton;

  public ChatUI() {
    setTitle("Chat");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 400);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // 채팅 출력 영역
    chatArea = new JTextArea();
    chatArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(chatArea);
    scrollPane.setPreferredSize(new Dimension(300, 200));
    add(scrollPane, BorderLayout.CENTER);

    // 입력 필드와 버튼
    JPanel inputPanel = new JPanel(new BorderLayout());
    inputField = new JTextField("안녕하세요");
    sendButton = new JButton("send");

    inputPanel.add(inputField, BorderLayout.CENTER);
    inputPanel.add(sendButton, BorderLayout.EAST);
    add(inputPanel, BorderLayout.SOUTH);

    // 버튼 이벤트 처리
    sendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendMessage();
      }
    });

    // 엔터 키 입력 처리
    inputField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendMessage();
      }
    });

    setVisible(true);
  }

  private void sendMessage() {
    String message = inputField.getText().trim();
    if (!message.isEmpty()) {
      chatArea.append("[send] " + message + "\n");
      // 시뮬레이션용 수신 메시지
//      chatArea.append("[receive] 네\n");
      inputField.setText("");
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(ChatUI::new);
  }
}
