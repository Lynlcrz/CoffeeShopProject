package products;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class productsController {
    @FXML
    private TextField productNameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField quantityField;
    @FXML
    private ImageView productImageView;
    @FXML
    private Button addProductButton;

    private File selectedImageFile;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            productImageView.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }

    @FXML
    private void addProduct() {
        String name = productNameField.getText();
        String price = priceField.getText();
        String description = descriptionArea.getText();
        String quantity = quantityField.getText();
        
        if (name.isEmpty() || price.isEmpty() || description.isEmpty() || quantity.isEmpty() || selectedImageFile == null) {
            System.out.println("Please fill all fields and select an image.");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO products (name, price, description, quantity, image_path) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setDouble(2, Double.parseDouble(price));
            stmt.setString(3, description);
            stmt.setInt(4, Integer.parseInt(quantity));
            stmt.setString(5, selectedImageFile.getAbsolutePath());
            
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Product added successfully!");
                clearFields();
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        productNameField.clear();
        priceField.clear();
        descriptionArea.clear();
        quantityField.clear();
        productImageView.setImage(null);
        selectedImageFile = null;
    }
}
