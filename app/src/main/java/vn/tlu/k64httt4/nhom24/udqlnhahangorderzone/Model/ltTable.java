package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model;

public class ltTable {
    private String id;
    private String name;
    private int seats;
    private String status;
    private String currentOrderId;

    // Constructor rỗng (bắt buộc cho Firestore)
    public ltTable() {
    }

    public ltTable(String id, String name, int seats, String status, String currentOrderId) {
        this.id = id;
        this.name = name;
        this.seats = seats;
        this.status = status;
        this.currentOrderId = currentOrderId;
    }

    // Getter & Setter
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

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    public void setCurrentOrderId(String currentOrderId) {
        this.currentOrderId = currentOrderId;
    }
}
