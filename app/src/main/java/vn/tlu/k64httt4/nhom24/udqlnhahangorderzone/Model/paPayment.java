package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model;

import com.google.firebase.Timestamp;

import java.util.List;

public class paPayment {
    private String id;
    private String billId;
    private String tableId;
    private String tableName;
    private List<paFood> foodList;
    private List<Integer> quantities;
    private String orderPerson;
    private Timestamp timestamp; // Thay đổi từ String thành Timestamp
    private long totalPrice;

    // Constructor
    public paPayment() {}

    public paPayment(String id, String billId, String tableId, String tableName, List<paFood> foodList, List<Integer> quantities, String orderPerson, Timestamp timestamp, long totalPrice) {
        this.id = id;
        this.billId = billId;
        this.tableId = tableId;
        this.tableName = tableName;
        this.foodList = foodList;
        this.quantities = quantities;
        this.orderPerson = orderPerson;
        this.timestamp = timestamp;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public List<paFood> getFoodList() { return foodList; }
    public void setFoodList(List<paFood> foodList) { this.foodList = foodList; }

    public List<Integer> getQuantities() { return quantities; }
    public void setQuantities(List<Integer> quantities) { this.quantities = quantities; }

    public String getOrderPerson() { return orderPerson; }
    public void setOrderPerson(String orderPerson) { this.orderPerson = orderPerson; }

    public Timestamp getTimestamp() { return timestamp; } // Cập nhật getter
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; } // Cập nhật setter

    public long getTotalPrice() { return totalPrice; }
    public void setTotalPrice(long totalPrice) { this.totalPrice = totalPrice; }
}