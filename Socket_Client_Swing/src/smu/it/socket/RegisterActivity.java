package smu.it.socket;

import javax.swing.*;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;

public class RegisterActivity {

    private RegisterForm form;
    private String photoPath = ""; // 업로드된 사진 경로
    private File uploadedFile = null; // 실제 선택된 파일

    public RegisterActivity(RegisterForm form) {
        this.form = form;

        // 사진 업로드 + 미리보기
        form.btnUpload.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(form);

            if(result == JFileChooser.APPROVE_OPTION) {
                uploadedFile = chooser.getSelectedFile(); // 실제 파일 저장
                photoPath = uploadedFile.getName();       // DB에 저장할 경로

                // 미리보기
                ImageIcon icon = new ImageIcon(uploadedFile.getAbsolutePath());
                Image scaledImage = icon.getImage().getScaledInstance(
                    form.lblPhotoPreview.getWidth(),
                    form.lblPhotoPreview.getHeight(),
                    Image.SCALE_SMOOTH
                );
                form.lblPhotoPreview.setIcon(new ImageIcon(scaledImage));
                form.lblPhotoPreview.setText("");
            }
        });



        // ID / 닉네임 중복 체크
        form.btnIdCheck.addActionListener(e -> checkDuplicate("ID", form.txtId.getText().trim()));
        form.btnNickCheck.addActionListener(e -> checkDuplicate("NICKNAME", form.txtNick.getText().trim()));

        form.btnPwValidate.addActionListener(e -> {
            String pw = new String(form.txtPw.getPassword()).trim();
            if(isValidPassword(pw)) {
                JOptionPane.showMessageDialog(form, "사용가능한 비밀번호 입니다.");
            } else {
                JOptionPane.showMessageDialog(form, "비밀번호는 6자리 이상이고, 영문자와 숫자를 포함해야 합니다.");
            }
        });
        
        
        // 회원가입 버튼
        form.btnRegister.addActionListener(e -> registerUser());

        
        

     // 뒤로가기
        form.btnBack.addActionListener(e -> {
            form.dispose();          // 회원가입 창 닫기
            new LoginActivity();     // 로그인 창 열기
        });

        
        form.setVisible(true);
        
        


        
    }

    private void checkDuplicate(String column, String value) {
        if(value.isEmpty()) return;
        String sql = "SELECT COUNT(*) FROM USERS WHERE " + column + " = ?";
        try(Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if(rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(form, column + " 중복입니다!");
            } else {
                JOptionPane.showMessageDialog(form, column + " 사용 가능합니다.");
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) return false;
        boolean hasLetter = false, hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            else if (Character.isDigit(c)) hasDigit = true;
            if (hasLetter && hasDigit) return true;
        }
        return false;
    }

    private void registerUser() {
        String id = form.txtId.getText().trim();
        String pw = new String(form.txtPw.getPassword()).trim();
        String pwCheck = new String(form.txtPwCheck.getPassword()).trim();
        String nick = form.txtNick.getText().trim();
        String email = form.txtEmail1.getText().trim() + "@" + form.txtEmail2.getText().trim();
        String name = form.txtName.getText().trim();
        String gender = form.cbGender.getSelectedItem().equals("남") ? "M" : "F";
        String birth = form.txtBirth.getText().trim();
        String phone = form.cbPhone.getSelectedItem() + "-" + form.txtPhone2.getText().trim() + "-" + form.txtPhone3.getText().trim();


        if (!isValidPassword(pw)) {
            JOptionPane.showMessageDialog(form, "비밀번호는 6자리 이상이며, 영문자와 숫자를 모두 포함해야 합니다.");
            return;  // 검사 실패 시 함수 종료
        }
        
        if(!pw.equals(pwCheck)) {
            JOptionPane.showMessageDialog(form, "비밀번호가 일치하지 않습니다.");
            return;
        }

        String sql = "INSERT INTO USERS (ID, PASSWORD, NICKNAME, EMAIL, NAME, GENDER, BIRTH, PHONE, PHOTO) " +
                     "VALUES (?, ?, ?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?)";

        try(Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.setString(2, pw);
            pstmt.setString(3, nick);
            pstmt.setString(4, email);
            pstmt.setString(5, name);
            pstmt.setString(6, gender);
            pstmt.setString(7, birth);
            pstmt.setString(8, phone);
            pstmt.setString(9, photoPath);

            
           
            
            int result = pstmt.executeUpdate();
            if(result > 0) {
            	
            	if (photoPath == null || photoPath.trim().isEmpty()) {
            	    photoPath = "images/default.jpg";
            	} else {
            	    try {
            	        File imagesDir = new File("images");
            	        if (!imagesDir.exists()) imagesDir.mkdirs();

            	        File destFile = new File("images/" + photoPath);
            	        Files.copy(uploadedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            	    } catch (IOException ex) {
            	        ex.printStackTrace();
            	        JOptionPane.showMessageDialog(form, "사진 복사 실패: " + ex.getMessage());
            	    }
            	}

            	
            	
                JOptionPane.showMessageDialog(form, "회원가입 성공!");
                form.dispose();
                new LoginActivity();
            } else {
                JOptionPane.showMessageDialog(form, "회원가입 실패!");
            }

        } catch(SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(form, "회원가입 실패: " + e.getMessage());
        }
    }
    
  
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegisterForm form = new RegisterForm();
            new RegisterActivity(form);
        });
    }
}
