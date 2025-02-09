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
}
