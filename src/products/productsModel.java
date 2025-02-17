package products;

public class productsModel {
    private String name;
    private float price;
    private int quantity;
    private String description;
    private String image;

    // Constructor
    public productsModel(String name, float price, int quantity, String description, String image) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.image = image;
    }

    // Getters
    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    // Setters (optional)
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // Optional: toString() method for debugging purposes
    @Override
    public String toString() {
        return "Product [name=" + name + ", price=" + price + ", quantity=" + quantity + 
               ", description=" + description + ", image=" + image + "]";
    }
}
