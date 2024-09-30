package logIn;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import application.DatabaseManager;

public class signUp {

    @FXML
    private TextField userSign;

    @FXML
    private PasswordField Cpass;

    @FXML
    private PasswordField ConPass;

    
    @FXML
    private Button logInSign;
    
    @FXML
    private Button conPass;
    
    @FXML
    public void initialize() {
        // Clear the initial focus on userSign
        Platform.runLater(() -> userSign.getParent().requestFocus());
    }
    
    @FXML
    private void OnlogInSignClick() {
        try {
           
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = loader.load();

            
            Stage stage = (Stage) userSign.getScene().getWindow(); 
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to load login page.");
        }
    }

    @FXML
    private void conPass() {
        String username = userSign.getText();
        String password = Cpass.getText();
        String confirmPassword = ConPass.getText();
        
        
        if (password.isEmpty() || username.isEmpty() || confirmPassword.isEmpty() ) {
        	showAlert(AlertType.ERROR, "Form Error!", "Fill in to create new Account!");
        	return;
         }
        //VALIDATION FOR PASSWORD
        if (password.length() < 6) {
            showAlert(AlertType.ERROR, "Form Error!", "Password too short, must be at least 6 characters long");
            return;
        } else if (!password.matches(".*[A-Z].*")) {
            showAlert(AlertType.ERROR, "Form Error!", "Password must contain at least one uppercase letter");
            return;
        } else if (!password.matches(".*[0-9].*")) {
            showAlert(AlertType.ERROR, "Form Error!", "Password must contain at least one number");
            return;
        } else if (!password.matches(".*[\\[@#$%&*!_\\-=+/?><';:`^\\]].*")) {
            showAlert(AlertType.ERROR, "Form Error!", "Password must contain at least one special character (@#$%&*!_-=+/?><';:`^)");
            return;	
        	 
        }
        
        
        
        //VALIDATION FOR CONFIRMING PASSWORD
      if  (!password.equals(confirmPassword) ) {
        	
            showAlert(AlertType.ERROR, "Password Mismatch", "Passwords do not match!");
            return;
        }

        
        //VALIDATION FOR USERNAME
        if (username.length() < 6) {
            showAlert(AlertType.ERROR, "Form Error!", "Username too short, must be at least 6 characters long");
            return;   
        } else if (!username.matches(".*[0-9].*")) {
            showAlert(AlertType.ERROR, "Form Error!", "Username must contain at least one number");
            return;
        } else if (!username.matches(".*[\\[@#$%&*!_\\-=+/?><';:`^\\]].*")) {
            showAlert(AlertType.ERROR, "Form Error!", "Username must contain at least one special character (@#$%&*!_-=+/?><';:`^)");
            return;	
        }
        
        
        
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            int result = statement.executeUpdate();
            if (result > 0) {
                showAlert(AlertType.INFORMATION, "Sign Up Successful", "Welcome, " + username + "!");
            } else {
                showAlert(AlertType.ERROR, "Sign Up Failed", "Please try again later.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Error connecting to the database.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
