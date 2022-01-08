package lk.ijse.pos_system.view.tm;

public class OrderListTM {
    private String itemCode;
    private String description;
    private String packSize;
    private double unitPrice; // (1Kg, 1egg, 1 bottle)
    private int orderQTY;  // no. of packs ordered
    private double subTotal; // unitPrice * packSize * orderQTY
    private double discount; // per Unit ( 1 bottle, 1 case, 1 pack...)
        // discount = subTotal * Double.valueOf(discount) / 100
    private double total; // subTotal - discount

    public OrderListTM() {}

    public OrderListTM(String itemCode, String description, String packSize, double unitPrice, int orderQTY, double subTotal, double discount, double total) {
        this.setItemCode(itemCode);
        this.setDescription(description);
        this.setPackSize(packSize);
        this.setUnitPrice(unitPrice);
        this.setOrderQTY(orderQTY);
        this.setSubTotal(subTotal);
        this.setDiscount(discount);
        this.setTotal(total);
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackSize() {
        return packSize;
    }

    public void setPackSize(String packSize) {
        this.packSize = packSize;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getOrderQTY() {
        return orderQTY;
    }

    public void setOrderQTY(int orderQTY) {
        this.orderQTY = orderQTY;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
}

    @Override
    public String toString() {
        return "OrderListTM{" +
                "itemCode='" + itemCode + '\'' +
                ", description='" + description + '\'' +
                ", packSize='" + packSize + '\'' +
                ", unitPrice=" + unitPrice +
                ", orderQTY=" + orderQTY +
                ", subTotal=" + subTotal +
                ", discount=" + discount +
                ", total=" + total +
                '}';
    }
}
