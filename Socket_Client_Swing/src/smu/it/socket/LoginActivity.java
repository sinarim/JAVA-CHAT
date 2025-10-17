package smu.it.socket;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LoginActivity extends JFrame {

    private JTextField idInput = new JTextField(12);
    private JPasswordField pwInput = new JPasswordField(12);
    private JButton loginButton = new JButton("로그인");
    private JButton goRegisterButton = new JButton("회원가입");
    private JLabel weatherLabel = new JLabel("날씨 정보를 불러오는 중...", SwingConstants.CENTER);

    public LoginActivity() {
        setTitle("로그인 - ChatApp");
        setSize(410, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // 배경색
        getContentPane().setBackground(new Color(245, 247, 250));

        // 폰트
        Font titleFont = new Font("맑은 고딕", Font.BOLD, 20);
        Font labelFont = new Font("맑은 고딕", Font.PLAIN, 14);
        Font btnFont = new Font("맑은 고딕", Font.BOLD, 13);

        // 타이틀
        JLabel title = new JLabel("로그인", SwingConstants.CENTER);
        title.setFont(titleFont);
        title.setBounds(50, 30, 300, 30);
        add(title);

        // ID
        JLabel idLabel = new JLabel("아이디:");
        idLabel.setFont(labelFont);
        idLabel.setBounds(70, 90, 80, 25);
        add(idLabel);
        idInput.setBounds(150, 90, 180, 25);
        add(idInput);

        // PW
        JLabel pwLabel = new JLabel("비밀번호:");
        pwLabel.setFont(labelFont);
        pwLabel.setBounds(70, 130, 80, 25);
        add(pwLabel);
        pwInput.setBounds(150, 130, 180, 25);
        add(pwInput);

        // 로그인 버튼
        loginButton.setBounds(80, 180, 100, 30);
        loginButton.setBackground(new Color(200, 200, 200));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(btnFont);
        loginButton.setFocusPainted(false);
        add(loginButton);

        // 회원가입 버튼
        goRegisterButton.setBounds(220, 180, 100, 30);
        goRegisterButton.setBackground(new Color(200, 200, 200));
        goRegisterButton.setForeground(Color.BLACK);
        goRegisterButton.setFont(btnFont);
        goRegisterButton.setFocusPainted(false);
        add(goRegisterButton);

        // 날씨 정보 라벨
        weatherLabel.setBounds(50, 240, 300, 150);
        weatherLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        weatherLabel.setHorizontalAlignment(SwingConstants.CENTER);
        weatherLabel.setVerticalAlignment(SwingConstants.TOP);
        add(weatherLabel);

        // 로그인 버튼 액션
        loginButton.addActionListener(e -> {
            String id = idInput.getText().trim();
            String pw = new String(pwInput.getPassword()).trim();
            String nickName = login(id, pw);
            if(nickName != null) {
                JOptionPane.showMessageDialog(this, "로그인 성공!");
                // new ChatActivity(nickName); // 채팅 화면 실행
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "로그인 실패: ID 또는 비밀번호 확인!");
            }
        });

        // 회원가입 버튼 액션
        goRegisterButton.addActionListener(e -> {
             RegisterForm form = new RegisterForm();   
            // new RegisterActivity(form);            
            this.dispose();
        });

        // 날씨 불러오기 (비동기)
        new Thread(this::loadWeather).start();

        setVisible(true);
    }

    /** 날씨 불러오기 */
    private void loadWeather() {
        try {
            String city = "Seoul";
            String apiKey = "d337dcdc222387a3a7b9f4c8683af607";
            String urlStr = "https://api.openweathermap.org/data/2.5/weather?q="
                            + city + "&appid=" + apiKey + "&units=metric&lang=kr";

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line; 
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();

            String json = sb.toString();

            // JSON.simple로 파싱
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(json);

            JSONArray weatherArr = (JSONArray) obj.get("weather");
            JSONObject weather0 = (JSONObject) weatherArr.get(0);

            String iconCode = (String) weather0.get("icon");
            String description = (String) weather0.get("description");

            JSONObject mainObj = (JSONObject) obj.get("main");
            double temp = ((Number) mainObj.get("temp")).doubleValue();


         // ✅ 'n' 버전이 없으니까, d로 통일
         if (iconCode.endsWith("n")) {
             iconCode = iconCode.substring(0, 2) + "d";
         }

         // 로컬 images 폴더에서 아이콘 불러오기
         String iconPath = "images/" + iconCode + ".png";
         java.awt.Image image = javax.imageio.ImageIO.read(new java.io.File(iconPath));
         ImageIcon weatherIcon = new ImageIcon(image.getScaledInstance(100, 100, Image.SCALE_SMOOTH));


            String html = String.format(
                "<html><div style='text-align:center;'>"
                + "<b style='font-size:18px;'>오늘의 날씨</b><br>"   
                + "<span style='font-size:16px;'>%.1f°C</span><br>"
                + "<span style='font-size:14px;color:gray;'>%s</span>"
                + "</div></html>",
                temp, description
            );

            SwingUtilities.invokeLater(() -> {
                weatherLabel.setIcon(weatherIcon);
                weatherLabel.setText(html);
            });

        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> weatherLabel.setText("날씨 정보를 불러오지 못했습니다."));
        }
    }

    /** 로그인 처리 */
    private String login(String id, String password) {
        String sql = "SELECT NICKNAME FROM USERS WHERE ID=? AND PASSWORD=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                return rs.getString("NICKNAME");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginActivity::new);
    }
}

