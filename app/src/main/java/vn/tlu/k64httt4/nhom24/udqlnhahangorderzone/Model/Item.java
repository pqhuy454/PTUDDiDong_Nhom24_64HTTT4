package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model;


public class Item {
    private String id;
    private String name;
    private double price;

    // Constructor mặc định (bắt buộc cho Firestore)
    public Item() {}

    public Item(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getters và setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
