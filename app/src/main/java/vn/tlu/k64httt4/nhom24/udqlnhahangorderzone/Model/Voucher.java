package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model;

public class Voucher {
    private String id;
    private String name;
    private double discountPercent;
    private String dayStart;
    private String dayEnd;
    private double originalPrice;
    private double discountedPrice;

    public Voucher() {
    }

    public Voucher(String id, String name, double discountPercent, String dayStart, String dayEnd, double originalPrice) {
        this.id = id;
        this.name = name;
        this.discountPercent = discountPercent;
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
        this.originalPrice = originalPrice;
        this.discountedPrice = originalPrice * (1 - discountPercent / 100);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getDayStart() {
        return dayStart;
    }

    public void setDayStart(String dayStart) {
        this.dayStart = dayStart;
    }

    public String getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(String dayEnd) {
        this.dayEnd = dayEnd;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
}