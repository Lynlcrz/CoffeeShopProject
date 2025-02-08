package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import java.io.IOException;

public class MainFrameController {

    @FXML
    private Pane contentPane;

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
        loadPage("/views/Home.fxml"); // Load Home.fxml
    }

    @FXML
    private void openProducts() {
        loadPage("/views/Products.fxml");
    }

    @FXML
    private void openOrders() {
        loadPage("/views/Orders.fxml");
    }

    @FXML
    private void openPayment() {
        loadPage("/views/Payment.fxml");
    }

    @FXML
    private void openWallet() {
        loadPage("/views/Wallet.fxml");
    }

    @FXML
    private void openHistory() {
        loadPage("/views/History.fxml");
    }
}
