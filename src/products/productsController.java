package products;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class productsController {
    
    @FXML private Button addProductButton;
    @FXML private TextField productNameField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TextArea descriptionArea;
    @FXML private Button uploadImageButton;
    @FXML private Button saveButton;

    private File selectedImageFile;
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/coffeeshop";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @FXML
    public void initialize() {
        addProductButton.setOnAction(event -> openAddProductForm());
    }

    private void openAddProductForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/products/addProducts.fxml"));
            loader.setController(this); // Use the same controller
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Add Product");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Block main window
            stage.showAndWait(); // Wait until closed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(null);

        if (selectedImageFile != null) {
            System.out.println("Selected image: " + selectedImageFile.getAbsolutePath());
        }
    }

    @FXML
    private void saveProductToDatabase() {
        String name = productNameField.getText();
        String priceText = priceField.getText();
        String quantityText = quantityField.getText();
        String description = descriptionArea.getText();
        String imagePath = (selectedImageFile != null) ? selectedImageFile.getAbsolutePath() : "";

        if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty() || description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled!");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "INSERT INTO products (name, price, quantity, description, image) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setInt(3, quantity);
                stmt.setString(4, description);
                stmt.setString(5, imagePath);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully!");
                    ((Stage) saveButton.getScene().getWindow()).close(); // Close the window
                }
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Price and quantity must be numbers.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
