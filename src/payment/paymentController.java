package payment;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import java.io.File;
import java.sql.*;

public class paymentController {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/coffeeshop";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";

    @FXML
    private ScrollPane scrollProducts;

    @FXML
    private VBox productContainer;

    @FXML
    public void initialize() {
        System.out.println("Initializing Controller...");
        if (productContainer == null) {
            System.out.println("ERROR: productContainer is null! Check FXML.");
            return;
        }
        loadProductsFromDatabase();
    }

    private void loadProductsFromDatabase() {
        String query = "SELECT name, price, image FROM products";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            productContainer.getChildren().clear(); // Clear previous products

            boolean hasProducts = false;

            while (resultSet.next()) {
                hasProducts = true;
                String name = resultSet.getString("name");
                float price = resultSet.getFloat("price");
                String imagePath = resultSet.getString("image");

                System.out.println("Adding product: " + name);
                System.out.println("Image path from DB: " + imagePath);

                HBox productBox = createProductBox(name, price, imagePath);
                productContainer.getChildren().add(productBox);
            }

            if (!hasProducts) {
                System.out.println("No products found in the database.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createProductBox(String name, float price, String imagePath) {
        HBox productBox = new HBox(10);
        productBox.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        // Check if imagePath is valid
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            } else {
                System.out.println("WARNING: Image file not found: " + imagePath);
            }
        } else {
            System.out.println("WARNING: No image path provided for product: " + name);
        }

        Label nameLabel = new Label(name);
        Label priceLabel = new Label("₱" + price);

        productBox.getChildren().addAll(imageView, nameLabel, priceLabel);
        return productBox;
    }
}
