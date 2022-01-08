package lk.ijse.pos_system.model;

import java.util.Date;

public class Orders {
    private String orderID;
    private Date date;
    private String custID;
    private double orderCost;

    public Orders() {}

    public Orders(String orderID, Date date, String custID, double orderCost) {
        this.setOrderID(orderID);
        this.setDate(date);
        this.setCustID(custID);
        this.setOrderCost(orderCost);
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public double getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(double orderCost) {
        this.orderCost = orderCost;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "orderID='" + orderID + '\'' +
                ", date=" + date +
                ", custID='" + custID + '\'' +
                ", orderCost=" + orderCost +
                '}';
    }
}
