package gui;

import chunk.Chunk;
import header.Header;
import header.HeaderCreator;
import layer.Layer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Gui{
    private JFrame frame;
    private JTextField inputField;
    private JButton sendButton;
    private JTextArea outputArea;

    private Layer networkInterfaceLayer;

    public Gui(Layer upperLayer) {
        this.networkInterfaceLayer = upperLayer;
        initComponets();
    }

    public void initComponets() {
        frame = new JFrame("GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new BorderLayout());

        frame.setLocationRelativeTo(null);

        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(300, 30)); // 너비 300, 높이 30으로 설정

        sendButton = new JButton("Send");
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendRequest();
            }
        });

        JPanel panel = new JPanel();
        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    public void show(){
        frame.setVisible(true);
    }

    public void sendRequest() {
        String message = inputField.getText();
        if (message.isEmpty()) return;

        byte[] payload = message.getBytes();

        Header header = HeaderCreator.createHeader("SEGMENT", payload.length, "12345");
        Chunk chunk = new Chunk(header, payload);

        // 확인용 출력
        System.out.println("==== GUI에서 만든 Chunk ====");
        System.out.println("Header Type: " + header.getType());
        System.out.println("Payload: " + new String(payload));

        outputArea.append("Sent: " + new String(payload) + "\n");

        networkInterfaceLayer.send(chunk);
    }
}