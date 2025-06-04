package com.github.software_development_methodology_study.core.layer;

import com.github.software_development_methodology_study.core.data.chunk.Chunk;
import com.github.software_development_methodology_study.core.data.chunk.header.EmptyHeader;
import com.github.software_development_methodology_study.core.data.chunk.header.Header;
import com.github.software_development_methodology_study.core.data.chunk.payload.Payload;
import org.pcap4j.core.PcapNetworkInterface;

import java.util.Arrays;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static com.github.software_development_methodology_study.common.util.TypeConverter.ByteArrayToString;
import static com.github.software_development_methodology_study.common.util.TypeConverter.StringToByteArray;

public class GUILayer extends Layer<EmptyHeader> {
    private final JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private final static String DEFAULT_FRAME_NAME = "Chat";

    private String currentIp = "";
    private String currentMac = "";


    public GUILayer() {
        frame = new JFrame(DEFAULT_FRAME_NAME);
        initFrame();
        initChatLogArea();
        initMessageAndIpMacSetArea();
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

    private void initMessageAndIpMacSetArea() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(createMessagePanel(), BorderLayout.NORTH);
        bottomPanel.add(createIpMacPanel(), BorderLayout.SOUTH);
        frame.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createMessagePanel() {
        inputField = new JTextField();
        sendButton = new JButton("send");

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(inputField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        // 메시지 전송 이벤트
        inputField.addActionListener(e -> sendMessageButtonHandler());
        sendButton.addActionListener(e -> sendMessageButtonHandler());

        return messagePanel;
    }

    private JPanel createIpMacPanel() {
        JTextField ipField = new JTextField();
        JTextField macField = new JTextField();
        JButton ipMacSetButton = new JButton("set");

        JPanel ipMacPanel = new JPanel(new GridLayout(2, 2));
        ipMacPanel.add(new JLabel("IP:"));
        ipMacPanel.add(ipField);
        ipMacPanel.add(new JLabel("MAC:"));
        ipMacPanel.add(macField);

        JPanel setPanel = new JPanel(new BorderLayout());
        setPanel.add(ipMacPanel, BorderLayout.CENTER);
        setPanel.add(ipMacSetButton, BorderLayout.SOUTH);

        // IP/MAC 설정 이벤트
        ipMacSetButton.addActionListener(e -> {
            currentIp = ipField.getText().trim();
            currentMac = macField.getText().trim();
            JOptionPane.showMessageDialog(frame, "IP/MAC 설정 완료!");
        });

        return setPanel;
    }

    private void sendMessageButtonHandler() {
        String rawMessage = inputField.getText().trim();
        if (rawMessage.isEmpty() || currentIp.isBlank() || currentMac.isBlank()) {
            JOptionPane.showMessageDialog(frame, "IP/MAC 또는 메세지가 비어있습니다");
            return;
        }

        String extraInfo = String.format("IP: %s, MAC: %s", currentIp, currentMac);
        String message = generateChatLogMessage(rawMessage, ChatLogMode.SEND, extraInfo);

        chatArea.append(message);

        Chunk chunk = new Chunk();
        chunk.setHeader(new EmptyHeader());
        chunk.setPayload(new Payload(StringToByteArray(rawMessage)));

        //@TODO:: send(chunk, nic);

        inputField.setText("");
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

    @Override
    public boolean receive(Chunk<EmptyHeader> chunk, PcapNetworkInterface nic) {
        String message = ByteArrayToString(chunk.getPayload().getBytes());
        generateChatLogMessage(message, ChatLogMode.RECEIVE);
        return true;
    }

    @Override
    public boolean send(Chunk<Header> chunk, PcapNetworkInterface nic) {
        System.out.println(Arrays.toString(chunk.getPayload().getBytes()));
        return this.lowerLayer.send(chunk, nic);
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
