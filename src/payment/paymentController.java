package payment;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
        loadProductsFromDatabase();
        try {
            scrollProducts.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Disable horizontal scrolling
            scrollProducts.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Enable vertical scrolling
            productContainer.setPrefWidth(scrollProducts.getPrefViewportWidth()); // Ensure no horizontal overflow
            loadProductsFromDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing paymentController: " + e.getMessage());
        }
    }

    private void loadProductsFromDatabase() {
        String query = "SELECT name, price, image FROM products";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            productContainer.getChildren().clear(); // Clear previous products

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                float price = resultSet.getFloat("price");
                String imagePath = resultSet.getString("image");

                HBox productBox = createProductBox(name, price, imagePath);
                productContainer.getChildren().add(productBox);
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
        if (imagePath != null && !imagePath.isEmpty()) {
            imageView.setImage(new Image("file:" + imagePath));
        }

        Label nameLabel = new Label(name);
        Label priceLabel = new Label("₱" + price);

        productBox.getChildren().addAll(imageView, nameLabel, priceLabel);
        return productBox;
    }
}
