package products;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
    private ScrollPane productScrollPane;
    @FXML
    private HBox productContainer; // Make sure this is added in your FXML

    private static final int COLUMNS = 4;



    private File selectedImageFile;

    @FXML
    public void initialize() {
    	if (addProductButton != null) {
            addProductButton.setOnAction(event -> openAddProductForm());
        } else {
            System.out.println("addProductButton is NULL! Check FXML file.");
        }

    
    	    System.out.println("Initializing Controller...");

    	    if (addProductButton == null) {
    	        System.out.println("ERROR: addProductButton is NULL! Check FXML file.");
    	    } else {
    	        addProductButton.setOnAction(event -> openAddProductForm());
    	    }

    	    if (productContainer == null) {
    	        System.out.println("ERROR: productContainer is NULL! Check FXML file.");
    	    } else {
    	        loadProductsFromDatabase();
    	    }
    	    
    	    
    	    
    	    if (productScrollPane == null) {
    	        System.out.println("ERROR: productScrollPane is NULL! Check FXML file.");
    	    } else {
    	        Platform.runLater(() -> {
    	            productScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Enable vertical scrolling
    	            productScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);  // Disable horizontal scrolling
    	            productScrollPane.setFitToHeight(true);
    	        });
    	    }

    	    if (productContainer == null) {
    	        System.out.println("ERROR: productContainer is NULL! Check FXML file.");
    	    } else {
    	        productContainer.setPrefWidth(Region.USE_COMPUTED_SIZE); // Allow height to expand
    	        loadProductsFromDatabase();
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

    
    
    
    private void loadProductsFromDatabase() {
        if (productContainer == null) {
            System.out.println("ERROR: productContainer is NULL! Cannot load products.");
            return;
        }

        String query = "SELECT name, price, quantity, description, image FROM products";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            productContainer.getChildren().clear(); // Clear previous products
            VBox mainContainer = new VBox(10); // Main container (VBox) to hold multiple rows
            productContainer.getChildren().add(mainContainer); // Add VBox to root

            HBox rowBox = new HBox(10); // First row
            mainContainer.getChildren().add(rowBox); // Add first row to VBox

            int count = 0;
            while (resultSet.next()) {
                // Retrieve product details
                String name = resultSet.getString("name");
                float price = resultSet.getFloat("price");
                int quantity = resultSet.getInt("quantity");
                String description = resultSet.getString("description");
                String imagePath = resultSet.getString("image");

                // Create product panel
                VBox productPane = createProductPane(name, price, quantity, description, imagePath);
                rowBox.getChildren().add(productPane); // Add panel to current row
                count++;

                // **If 4 items are in this row, create a new row below**
                if (count % 4 == 0) { 
                    rowBox = new HBox(10); // Create new row
                    mainContainer.getChildren().add(rowBox); // Add new row to VBox
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private VBox createProductPane(String name, float price, int quantity, String description, String imagePath) {
        VBox productPane = new VBox();
        productPane.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-spacing: 5; -fx-alignment: center;");
        productPane.setPrefSize(200, 250); // Set a reasonable size

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label priceLabel = new Label("₱" + price);
        Label quantityLabel = new Label("Stock: " + quantity);
        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        if (imagePath != null && !imagePath.isEmpty()) {
            imageView.setImage(new Image("file:" + imagePath));
        }

        productPane.getChildren().addAll(imageView, nameLabel, priceLabel, quantityLabel, descriptionLabel);
        return productPane;
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
    
    
  
}
