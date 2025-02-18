package products;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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
    @FXML private Pane productsPane;


    private File selectedImageFile;

    @FXML
    public void initialize() {
    	if (addProductButton != null) {
            addProductButton.setOnAction(event -> openAddProductForm());
        } else {
            System.out.println("addProductButton is NULL! Check FXML file.");
        }

    
    
    System.out.println("Initializing Controller...");
    if (productNameLabel == null) System.out.println("productNameLabel is NULL");
    if (priceLabel == null) System.out.println("priceLabel is NULL");
    if (quantityLabel == null) System.out.println("quantityLabel is NULL");
    if (descriptionLabel == null) System.out.println("descriptionLabel is NULL");
    if (productImageView == null) System.out.println("productImageView is NULL");
    
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
            
            // Verify the connection is valid
            if (connection.isValid(1)) {
                System.out.println("Database connection successful.");
            } else {
                System.out.println("Database connection failed.");
                return null;
            }

            statement.setInt(1, Id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                float price = resultSet.getFloat("price");
                int quantity = resultSet.getInt("quantity");
                String description = resultSet.getString("description");
                String image = resultSet.getString("image");

                product = new productsModel(name, price, quantity, description, image);
            } else {
                System.out.println("No product found with Id: " + Id);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving product details: " + e.getMessage());
            e.printStackTrace();
        }

        return product;
    }

    // Update product details on the UI
    public void updateProductDetails(int Id) {
        System.out.println("Updating product details for ID: " + Id);
        productsModel product = getProductDetailsFromDatabase(Id);

        if (product != null) {
            Platform.runLater(() -> {
                productNameLabel.setText(product.getName());
                priceLabel.setText("" + product.getPrice());
                quantityLabel.setText("" + product.getQuantity());
                descriptionLabel.setText(product.getDescription());

                if (product.getImage() != null && !product.getImage().isEmpty()) {
                    try {
                        System.out.println("Displaying image from: " + product.getImage());
                        productImageView.setImage(new Image("file:" + product.getImage()));
                    } catch (Exception e) {
                        System.err.println("Error loading image: " + e.getMessage());
                    }
                } else {
                    System.out.println("No image available for this product.");
                    productImageView.setImage(null);
                }
            });
        } else {
            Platform.runLater(() -> {
                productNameLabel.setText("Product Name: ");
                priceLabel.setText("Price: ");
                quantityLabel.setText("Quantity: ");
                descriptionLabel.setText("Description: ");
                productImageView.setImage(null);
            });
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
            uploadImageButton.setImage(new Image("file:" + selectedFile.getAbsolutePath())); // Display image in ImageView
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
    public void loadProduct(int productId) {
        updateProductDetails(productId);
        
       
    }
}
