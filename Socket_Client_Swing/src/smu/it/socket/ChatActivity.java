package smu.it.socket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatActivity extends JFrame {

    private JTextArea chatView = new JTextArea();
    private JTextField sendText = new JTextField(20);
    private JButton sendButton = new JButton("전송");
    private JButton exitButton = new JButton("종료");
    private JLabel showNickName = new JLabel();

    private Socket socket;
    private PrintWriter pwrite;
    private BufferedReader receiveRead;

    private String serverIp = "127.0.0.1"; // 로컬 서버 IP
    private int port = 8888;
    private String nickName;
    private boolean connection = false;

    public ChatActivity(String nickName) {
        this.nickName = nickName;

        setTitle("Java Chat Client");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        showNickName.setText("닉네임: " + nickName);
        JPanel topPanel = new JPanel();
        topPanel.add(showNickName);
        add(topPanel, BorderLayout.NORTH);

        chatView.setEditable(false);
        add(new JScrollPane(chatView), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(sendText);
        bottomPanel.add(sendButton);
        bottomPanel.add(exitButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);

        connectToServer();
        setupActions();
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket(serverIp, port);
                pwrite = new PrintWriter(socket.getOutputStream(), true);
                receiveRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                if(!connection) {
                    pwrite.println("[" + nickName + "] 님이 입장했습니다.");
                    connection = true;
                }

                String receiveMessage;
                while((receiveMessage = receiveRead.readLine()) != null) {
                    chatView.append(receiveMessage + "\n");
                }

            } catch(IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "서버 연결 실패!");
            }
        }).start();
    }

    private void setupActions() {
        sendButton.addActionListener(e -> {
            String sendMessage = sendText.getText();
            if(!sendMessage.isEmpty()) {
                pwrite.println("[" + nickName + "] " + sendMessage);
                sendText.setText("");
            }
        });

        exitButton.addActionListener(e -> {
            try {
                pwrite.println("[" + nickName + "] 님이 퇴장했습니다.");
                pwrite.close();
                socket.close();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });

        sendText.addActionListener(e -> sendButton.doClick()); // Enter 키 전송
    }
}
