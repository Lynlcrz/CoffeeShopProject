package products;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class productsController {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/coffeeshop"; 
    private static final String DB_USERNAME = "root"; 
    private static final String DB_PASSWORD = ""; 

    @FXML private Button addProductButton;
    @FXML private TextField productNameField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TextArea descriptionArea;
    @FXML private ImageView uploadImageButton;
    @FXML private Button saveButton;
    @FXML private Label productNameLabel;
    @FXML private Label priceLabel;
    @FXML private Label quantityLabel;
    @FXML private Label descriptionLabel;
    @FXML private ImageView productImageView;

    private File selectedImageFile;

    @FXML
    public void initialize() {
        if (addProductButton != null) {
            addProductButton.setOnAction(event -> openAddProductForm());
        } else {
            System.out.println("addProductButton is NULL! Check FXML file.");
        }
    }

    private void openAddProductForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/products/addProducts.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Product");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public productsModel getProductDetailsFromDatabase(int Id) {
        productsModel product = null;
        String query = "SELECT name, price, quantity, description, image FROM products WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            System.out.println("Connecting to database...");  // Debugging line

            statement.setInt(1, Id);  // Set the productId as a parameter in the query
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                product = new productsModel(
                        resultSet.getString("name"),
                        resultSet.getFloat("price"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("description"),
                        resultSet.getString("image")
                );
                System.out.println("Product fetched: " + product.getName());  // Debugging line
            } else {
                System.out.println("No product found with Id: " + Id);  // Debugging line
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving product details: " + e.getMessage());
            e.printStackTrace();
        }

        return product;
    }

    private void updateProductDetails(int Id) {
        System.out.println("Updating product details for ID: " + Id);  // Debugging line
        productsModel product = getProductDetailsFromDatabase(Id);  // Fetch product from database

        if (product != null) {
            System.out.println("Product found: " + product.getName());  // Debugging line
            String name = product.getName();
            String priceText = String.valueOf(product.getPrice());
            String quantityText = String.valueOf(product.getQuantity());
            String description = product.getDescription();
            String image = product.getImage();  // Retrieve the image path from the database

            // Update labels with product information
            productNameLabel.setText("Product Name: " + name);
            priceLabel.setText("Price: " + priceText);
            quantityLabel.setText("Quantity: " + quantityText);
            descriptionLabel.setText("Description: " + description);

            // Update ImageView with selected image
            if (image != null && !image.isEmpty()) {
                System.out.println("Displaying image from: " + image);  // Debugging line
                productImageView.setImage(new javafx.scene.image.Image("file:" + image));
            }
        } else {
            System.out.println("No product found for ID: " + Id);
        }
    }

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(uploadImageButton.getScene().getWindow());

        if (selectedFile != null) {
            System.out.println("Selected image: " + selectedFile.getAbsolutePath());
            this.selectedImageFile = selectedFile;  // Save the selected image file
            uploadImageButton.setImage(new javafx.scene.image.Image("file:" + selectedFile.getAbsolutePath())); // Display image in ImageView
        }
    }

    @FXML
    private void saveProductToDatabase() {
        String name = productNameField.getText();
        String priceText = priceField.getText();
        String quantityText = quantityField.getText();
        String description = descriptionArea.getText();
        String image = (selectedImageFile != null) ? selectedImageFile.getAbsolutePath() : "";

        if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty() || description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled!");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                String sql = "INSERT INTO products (name, price, quantity, description, image) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setInt(3, quantity);
                stmt.setString(4, description);
                stmt.setString(5, image);

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
            showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Call this method with the correct product ID to update the labels and image
    public void showProductDetails(int Id) {
        updateProductDetails(Id);
    }
}
