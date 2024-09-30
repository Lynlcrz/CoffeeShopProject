package logIn;

import application.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.io.IOException;

public class logIn {

    @FXML
    private TextField userLog;

    @FXML
    private TextField passwordLog;

    @FXML
    private Button buttonPass;

    @FXML
    private Label fPass;

    @FXML
    private Button signLog;

    @FXML
    private ImageView eyeLock;

    private boolean isPasswordVisible = false; 
    private StringBuilder storedPassword = new StringBuilder(); 

    @FXML
    public void initialize() {
        // Set up eyeLock (password visibility toggle) click event
        eyeLock.setOnMouseClicked(event -> toggleIcon());
        passwordLog.setOnKeyReleased(this::handleKeyReleased);
    }

    @FXML
    private void buttonPass(ActionEvent event) {
        String username = userLog.getText();
        String password = storedPassword.toString(); 

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Login Failed", "Username or password cannot be empty.");
            clearPasswordField(); 
            return;
        }

       
        if (authenticateUser(username, password)) {
            showAlert(AlertType.INFORMATION, "Login Successful!", "Welcome " + username + "!");
            transitionToMainFrame();
        } else {
            showAlert(AlertType.ERROR, "Login Failed", "Incorrect username or password.");
            clearPasswordField();  
        }
    }

    
    private void clearPasswordField() {
        passwordLog.clear();            
        storedPassword.setLength(0);  
    }
    private boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); 

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void signLog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signUp.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) userLog.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to load sign-up page.");
        }
    }

    @FXML
    private void forgetPassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Forgot Password");
        dialog.setHeaderText("Reset Your Password");
        dialog.setContentText("Please enter your email:");

        dialog.getEditor().setText(userLog.getText());

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String email = result.get();

            if (isEmailRegistered(email)) {
                String resetToken = generateResetToken();
                showAlert(AlertType.INFORMATION, "Password Reset",
                          "A password reset link has been sent to your email.\nReset Token: " + resetToken);
            } else {
                showAlert(AlertType.ERROR, "Email Not Found",
                          "The email you entered is not registered.");
            }
        }
    }

    private boolean isEmailRegistered(String email) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String generateResetToken() {
        return "123456";
    }

    
    @FXML
    private void toggleIcon() {
        isPasswordVisible = !isPasswordVisible; 

        
        updatePasswordAndIcon();
    }

    
    private void updatePasswordAndIcon() {
    	 int caretPosition = passwordLog.getCaretPosition();
    	 
        if (isPasswordVisible) {
           
            passwordLog.setText(storedPassword.toString());
            eyeLock.setImage(new Image("/pictures/hidden.png"));
        } else {
           
            if (storedPassword.length() == 0) {
                passwordLog.clear();
            } else {
                passwordLog.setText("•".repeat(storedPassword.length()));
            }
            eyeLock.setImage(new Image("/pictures/eye.png"));
        }

        
        passwordLog.positionCaret(caretPosition);
    }

    @FXML
    private void handleKeyReleased(KeyEvent event) {
        
        String currentText = passwordLog.getText();

       
        if (isPasswordVisible) {
            storedPassword.setLength(0);  
            storedPassword.append(currentText);  
        } else {
           
            if (currentText.length() > storedPassword.length()) {
              
                storedPassword.append(currentText.charAt(currentText.length() - 1));
            } else if (currentText.length() < storedPassword.length()) {
            
                storedPassword.setLength(currentText.length());
            }
        }

       
        updatePasswordAndIcon();

       
        if (currentText.isEmpty()) {
            storedPassword.setLength(0);
        }
    }


    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void transitionToMainFrame() {
        try {
            Stage stage = (Stage) buttonPass.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/application/MainFrame.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
