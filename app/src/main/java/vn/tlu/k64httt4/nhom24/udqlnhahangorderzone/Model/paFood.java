package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model;

import java.io.Serializable;

public class paFood implements Serializable {
    private String id;
    private String name;
    private int price;
    private String imageUrl;

    public paFood() {}

    public paFood(String id, String name, int price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}