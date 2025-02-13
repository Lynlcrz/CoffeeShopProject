package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.Optional;

public class MainFrameController {

    @FXML
    private Pane contentPane;
    
    @FXML
    private void initialize() {
        openHome(); 
    }

    // Method to load different views dynamically
    private void loadPage(String fxmlFile) {
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentPane.getChildren().clear(); // Clear existing content
            contentPane.getChildren().add(newLoadedPane); // Load new content
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Call this method when a menu button is clicked
    @FXML
    private void openHome() {
        loadPage("/home/home.fxml"); // Load Home.fxml
    }

    @FXML
    private void openProducts() {
        loadPage("/products/products.fxml");
    }

    @FXML
    private void openOrders() {
        loadPage("/orders/orders.fxml");
    }

    @FXML
    private void openPayment() {
        loadPage("/payment/payment.fxml");
    }

    @FXML
    private void openWallet() {
        loadPage("/wallet/wallet.fxml");
    }

    @FXML
    private void openHistory() {
        loadPage("/history/history.fxml");
        
    }
    
    @FXML
    public void logout(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to log out?");
        alert.setContentText("Click OK to log out or Cancel to stay.");

        // Show the alert and wait for user response
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/logIn/Main.fxml"));
                Parent loginPane = loader.load();

                // Get the current stage
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the login scene
                currentStage.setScene(new Scene(loginPane));
                currentStage.setTitle("Login");
                currentStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @FXML
    public void cancel(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/logIn/Main.fxml"));
            Parent loginPane = loader.load();

            // Get the current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Close the current stage
            currentStage.close();

            // Create a new stage for the login scene
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(loginPane));
            loginStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    @FXML
    public void minimizeWindow(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true); 
    }
    
    
    @FXML
    public void toggleMaximize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setMaximized(!stage.isMaximized()); // Toggle between maximized and normal state
    }
    

}
