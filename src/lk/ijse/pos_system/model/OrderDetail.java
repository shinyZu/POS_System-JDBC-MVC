package lk.ijse.pos_system.model;

public class OrderDetail {
    private String orderID;
    private String itemCode;
    private int orderQTY;
    private double discount;
    // private double orderCost;

    public OrderDetail() {
    }

    public OrderDetail(String orderID, String itemCode, int orderQTY, double discount) {
        this.setOrderID(orderID);
        this.setItemCode(itemCode);
        this.setOrderQTY(orderQTY);
        this.setDiscount(discount);
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getOrderQTY() {
        return orderQTY;
    }

    public void setOrderQTY(int orderQTY) {
        this.orderQTY = orderQTY;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderID='" + orderID + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", orderQTY=" + orderQTY +
                ", discount=" + discount +
                '}';
    }
}
