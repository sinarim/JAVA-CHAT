package smu.it.socket;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.net.URL;
import java.util.Date;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker;

import netscape.javascript.JSObject;

public class RegisterForm extends JFrame {

    // --- 필드 선언 ---
    public JTextField txtId = new JTextField();
    public JPasswordField txtPw = new JPasswordField();
    public JPasswordField txtPwCheck = new JPasswordField();
    public JLabel lblPwMatch = new JLabel("0% 일치");
    public JTextField txtNick = new JTextField();
    public JTextField txtEmail1 = new JTextField();
    public JTextField txtEmail2 = new JTextField();
    public JTextField txtName = new JTextField();
    public JComboBox<String> cbGender = new JComboBox<>(new String[]{"남","여"});
    public JTextField txtBirth = new JTextField("YYYY-MM-DD");
    public JComboBox<String> cbPhone = new JComboBox<>(new String[]{"010","011","016","017","018","019"});
    public JTextField txtPhone2 = new JTextField();
    public JTextField txtPhone3 = new JTextField();
    public JTextField txtDetail = new JTextField();
    public JTextField txtZipcode = new JTextField();
    public JTextField txtAddress = new JTextField();
    public JLabel lblPhotoPreview = new JLabel("", SwingConstants.CENTER);

    public JButton btnIdCheck = new JButton("중복 확인");
    public JButton btnNickCheck = new JButton("중복 확인");
    public JButton btnPwValidate = new JButton("확인");
    public JButton btnUpload = new JButton("프로필 업로드");
    public JButton btnRegister = new JButton("회원가입");
    public JButton btnBack = new JButton("뒤로 가기");
    public JButton btnZipcodeSearch = new JButton("우편번호");

    // --- JavaFX 관련 필드 ---
    private static boolean fxInitialized = false; // Toolkit 초기화 여부
    private static JFXPanel fxPanel;
    private static WebView webView;
    private static WebEngine engine;

    public RegisterForm() {
        // JavaFX Toolkit 초기화
        if (!fxInitialized) {
            new JFXPanel(); // 초기화용
            fxInitialized = true;
        }

        setTitle("회원가입");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // --- 아이디 ---
        JLabel lblId = new JLabel("아이디:");
        lblId.setBounds(30, 30, 80, 25);
        txtId.setBounds(120, 30, 150, 25);
        btnIdCheck.setBounds(280, 30, 100, 25);

        // --- 비밀번호 ---
        JLabel lblPw = new JLabel("비밀번호:");
        lblPw.setBounds(30, 70, 80, 25);
        txtPw.setBounds(120, 70, 150, 25);
        btnPwValidate.setBounds(280, 70, 100, 25);

        JLabel lblPwCheck = new JLabel("비밀번호 확인:");
        lblPwCheck.setBounds(30, 110, 100, 25);
        txtPwCheck.setBounds(120, 110, 150, 25);
        lblPwMatch.setBounds(280, 110, 80, 25);
        lblPwMatch.setForeground(Color.RED);

        DocumentListener pwListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateMatch(); }
            public void removeUpdate(DocumentEvent e) { updateMatch(); }
            public void changedUpdate(DocumentEvent e) { updateMatch(); }
        };
        txtPw.getDocument().addDocumentListener(pwListener);
        txtPwCheck.getDocument().addDocumentListener(pwListener);

        panel.add(lblPw); panel.add(txtPw);
        panel.add(lblPwCheck); panel.add(txtPwCheck); panel.add(lblPwMatch);

        // --- 닉네임 ---
        JLabel lblNick = new JLabel("닉네임:");
        lblNick.setBounds(30, 150, 80, 25);
        txtNick.setBounds(120, 150, 150, 25);
        btnNickCheck.setBounds(280, 150, 100, 25);

        // --- 이메일 ---
        JLabel lblEmail = new JLabel("이메일:");
        lblEmail.setBounds(30, 190, 80, 25);
        txtEmail1.setBounds(120, 190, 100, 25);
        JLabel lblAt = new JLabel("@");
        lblAt.setBounds(225, 190, 20, 25);
        txtEmail2.setBounds(250, 190, 120, 25);

        // --- 이름 ---
        JLabel lblName = new JLabel("이름:");
        lblName.setBounds(30, 230, 80, 25);
        txtName.setBounds(120, 230, 150, 25);

        // --- 성별 ---
        JLabel lblGender = new JLabel("성별:");
        lblGender.setBounds(30, 270, 80, 25);
        cbGender.setBounds(120, 270, 80, 25);

        // --- 생년월일 ---
        JLabel lblBirth = new JLabel("생년월일:");
        lblBirth.setBounds(30, 310, 80, 25);
        txtBirth.setBounds(120, 310, 150, 25);

        JButton btnPickDate = new JButton("선택");
        btnPickDate.setBounds(280, 310, 80, 25);
        btnPickDate.addActionListener(ev -> {
            UtilDateModel model = new UtilDateModel();
            JDatePanelImpl datePanel = new JDatePanelImpl(model);
            JDatePickerImpl datePicker = new JDatePickerImpl(datePanel);

            int option = JOptionPane.showConfirmDialog(
                    this, datePicker, "날짜 선택", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if(option == JOptionPane.OK_OPTION) {
                Date selectedDate = (Date) datePicker.getModel().getValue();
                if(selectedDate != null) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    txtBirth.setText(sdf.format(selectedDate));
                }
            }
        });

        // --- 전화번호 ---
        JLabel lblPhone = new JLabel("전화번호:");
        lblPhone.setBounds(30, 350, 80, 25);
        cbPhone.setBounds(120, 350, 60, 25);
        txtPhone2.setBounds(190, 350, 80, 25);
        txtPhone3.setBounds(280, 350, 80, 25);

        // --- 우편번호 ---
        JLabel lblZipcode = new JLabel("우편번호:");
        lblZipcode.setBounds(30, 390, 80, 25);
        txtZipcode.setBounds(120, 390, 100, 25);
        btnZipcodeSearch.setBounds(230, 390, 100, 25);

        // --- 주소 ---
        JLabel lblAddress = new JLabel("주소:");
        lblAddress.setBounds(30, 430, 80, 25);
        txtAddress.setBounds(120, 430, 300, 25);

        // --- 상세주소 ---
        JLabel lblDetail = new JLabel("상세주소:");
        lblDetail.setBounds(30, 470, 80, 25);
        txtDetail.setBounds(120, 470, 300, 25);

        // --- 프로필 사진 ---
        JPanel photoPanel = new JPanel(new BorderLayout());
        photoPanel.setBounds(400, 30, 150, 180);
        lblPhotoPreview.setText("사진 없음");
        lblPhotoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblPhotoPreview.setVerticalAlignment(SwingConstants.CENTER);
        lblPhotoPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoPanel.add(lblPhotoPreview, BorderLayout.CENTER);
        btnUpload.setBounds(420, 220, 110, 25);

        panel.add(photoPanel);
        panel.add(btnUpload);

        // --- 회원가입/뒤로가기 버튼 ---
        btnRegister.setBounds(180, 520, 100, 30);
        btnBack.setBounds(300, 520, 100, 30);

        btnBack.addActionListener(e -> {
            this.setVisible(false); // dispose() 대신 숨김
            SwingUtilities.invokeLater(() -> new LoginActivity().setVisible(true));
        });

        // --- panel에 추가 ---
        panel.add(lblId); panel.add(txtId); panel.add(btnIdCheck);
        panel.add(lblPw); panel.add(txtPw);
        panel.add(lblPwCheck); panel.add(txtPwCheck);
        panel.add(lblNick); panel.add(txtNick); panel.add(btnNickCheck);
        panel.add(lblEmail); panel.add(txtEmail1); panel.add(lblAt); panel.add(txtEmail2);
        panel.add(lblName); panel.add(txtName);
        panel.add(lblGender); panel.add(cbGender);
        panel.add(lblBirth); panel.add(txtBirth);
        panel.add(lblPhone); panel.add(cbPhone); panel.add(txtPhone2); panel.add(txtPhone3);
        panel.add(lblZipcode); panel.add(txtZipcode); panel.add(btnZipcodeSearch);
        panel.add(lblAddress); panel.add(txtAddress);
        panel.add(lblDetail); panel.add(txtDetail);
        panel.add(btnPwValidate);
        panel.add(btnRegister); panel.add(btnBack);
        panel.add(btnPickDate);

        add(panel);

        // --- 우편번호 버튼 클릭 시 팝업 ---
        btnZipcodeSearch.addActionListener(e -> openZipcodePopup());

        setVisible(true);
    }

    private void updateMatch() {
        String pw1 = new String(txtPw.getPassword());
        String pw2 = new String(txtPwCheck.getPassword());
        int matchPercent = calculateMatchPercentage(pw1, pw2);
        lblPwMatch.setText(matchPercent + "%");
        lblPwMatch.setForeground(matchPercent == 100 ? Color.GREEN : Color.RED);
    }

    private int calculateMatchPercentage(String a, String b) {
        if(a.isEmpty() || b.isEmpty()) return 0;
        int len = Math.min(a.length(), b.length());
        int matchCount = 0;
        for(int i=0; i<len; i++) if(a.charAt(i) == b.charAt(i)) matchCount++;
        int maxLen = Math.max(a.length(), b.length());
        return (int)((matchCount / (double) maxLen) * 100);
    }

    private void openZipcodePopup() {
        JDialog dialog = new JDialog(this, "우편번호 검색", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JFXPanel fxPanel = new JFXPanel(); // 매번 새로 생성
        dialog.add(fxPanel);

        Platform.runLater(() -> {
            WebView webView = new WebView();       // 새 WebView 생성
            WebEngine engine = webView.getEngine();
            engine.setJavaScriptEnabled(true);

            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("javaConnector", new JavaConnector(dialog, this));
                    engine.executeScript("execDaumPostcode();");
                }
            });

            URL url = getClass().getResource("/smu/it/socket/zip.html");
            engine.load(url.toExternalForm() + "?t=" + System.currentTimeMillis());

            fxPanel.setScene(new Scene(webView));
        });

        dialog.setVisible(true);
    }

    public class JavaConnector {
        private JDialog dialog;
        private RegisterForm parentForm;

        public JavaConnector(JDialog dialog, RegisterForm parentForm) {
            this.dialog = dialog;
            this.parentForm = parentForm;
        }

        public void setZipcodeAndAddress(String zonecode, String address) {
            SwingUtilities.invokeLater(() -> {
                parentForm.txtZipcode.setText(zonecode);
                parentForm.txtAddress.setText(address);
                dialog.dispose();
            });
        }

        public void log(String msg) {
            System.out.println(msg);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterForm().setVisible(true));
    }
}
