package lk.ijse.pos_system.controller;

import lk.ijse.pos_system.db.DBConnection;
import lk.ijse.pos_system.model.Customer;
import lk.ijse.pos_system.model.OrderDetail;
import lk.ijse.pos_system.model.Orders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderController {

    public String getOrderId() throws SQLException, ClassNotFoundException {
        ResultSet rst = DBConnection.getInstance().getConnection().
                prepareStatement("SELECT * FROM Orders ORDER BY orderId DESC LIMIT 1").executeQuery();

        if (rst.next()){
            int tempId = Integer.parseInt(rst.getString(1).split("-")[1]);
            tempId = tempId+1;

            if (tempId <= 9){
                return "O-00" + tempId;
            }else if (tempId <= 99){
                return "O-0" + tempId;
            }else {
                return "O-" + tempId;
            }

        }else {
            return "O-001";
        }
    }

    public boolean placeOrder(Customer placeOrderCustomer, Orders newOrder, ArrayList<OrderDetail> items) throws SQLException {
        Connection con = null;

        try {
            con = DBConnection.getInstance().getConnection();
            con.setAutoCommit(false); // to pause the action of saving data to tables

            PreparedStatement stm = con.prepareStatement("INSERT INTO Orders VALUES (?,?,?,?)");

            stm.setObject(1, newOrder.getOrderID());
            stm.setObject(2, newOrder.getDate());
            stm.setObject(3, placeOrderCustomer.getCustID());
            stm.setObject(4, newOrder.getOrderCost());

            if (stm.executeUpdate() > 0) { // will be true if the order table is updated, but doesnt save bcz setAutoCommit is false

                if (new OrderDetailController().saveOrderDetail(items)) { // will be true if the order detail table gets updated (but not visible bcz it doesnt actually save, bcz setAutoCommit is false) takes the data to the table to save but doesnt save
                    con.commit();
                    return true;
                } else { // will be false if some issue is raised during updating Order Detail Table
                    con.rollback(); // send back the sent data bundle bcz of of some security issues/error
                    return false;
                }

            } else {
                con.rollback();
                return false;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }
        return false;
    }

    public List<String> searchOrdersByCustID(String custID) throws SQLException, ClassNotFoundException {

        ArrayList<String> listOfOrders = new ArrayList();

        PreparedStatement stm = DBConnection.getInstance().getConnection()
                .prepareStatement("SELECT orderId FROM Orders WHERE custID = ?");
        stm.setObject(1,custID);

        ResultSet rst = stm.executeQuery();
        while (rst.next()) {
            listOfOrders.add(rst.getString(1));
        }
        return listOfOrders;
    }

    public boolean updateOrderCost(String orderIdSelected, double newOrderCost) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DBConnection.getInstance().getConnection()
                .prepareStatement("UPDATE Orders SET orderCost = ? WHERE orderID = ?");

        stm.setObject(1,newOrderCost);
        stm.setObject(2,orderIdSelected);

        return stm.executeUpdate() > 0;

    }

    public boolean deleteOrder(String orderIdSelected) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DBConnection.getInstance().getConnection()
                .prepareStatement("DELETE FROM Orders WHERE  orderID = ?");

        stm.setObject(1,orderIdSelected);

        return stm.executeUpdate() > 0;
    }

    //----------------------------------------------------------------------------------------------------------------------
    public boolean updateOrder(Orders orderToBeUpdated, OrderDetail orderDetailToBeUpdated, int newQtyOnHand) throws SQLException {

        Connection con = null;

        try {
            con = DBConnection.getInstance().getConnection();
            con.setAutoCommit(false); // to pause the action of saving data to tables

            PreparedStatement stm = con.prepareStatement("UPDATE Orders SET orderCost = ? WHERE orderID = ?");

            stm.setObject(1,orderToBeUpdated.getOrderCost());
            stm.setObject(2,orderToBeUpdated.getOrderID());

            if (stm.executeUpdate() > 0) {  // will be true if the Orders table is updated, but doesnt save bcz setAutoCommit is false

                if (new OrderDetailController().updateOrderDetail(orderDetailToBeUpdated,newQtyOnHand)) { // will be true if the order detail table and Item table gets updated (but not visible bcz it doesnt actually save, bcz setAutoCommit is false) takes the data to the table to save but doesnt save
                    con.commit();
                    return true;
                } else { // will be false if some issue is raised during updating Order Detail Table or Item table
                    con.rollback(); // send back the sent data bundle bcz of of some security issues/error
                    return false;
                }
            } else {
                con.rollback();
                return false;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }
        return false;
    }


    public boolean updateOrderAndOrderDetail(Orders orderToBeUpdated, OrderDetail orderDetailToBeUpdated) throws SQLException {

        Connection con = null;

        try {
            con = DBConnection.getInstance().getConnection();
            con.setAutoCommit(false); // to pause the action of saving data to tables

            PreparedStatement stm = con.prepareStatement("UPDATE Orders SET orderCost = ? WHERE orderID = ?");

            stm.setObject(1,orderToBeUpdated.getOrderCost());
            stm.setObject(2,orderToBeUpdated.getOrderID());

            if (stm.executeUpdate() > 0) {  // will be true if the Orders table is updated, but doesnt save bcz setAutoCommit is false

                if (new OrderDetailController().updateDetail(orderDetailToBeUpdated)) { // will be true if the order detail table and Item table gets updated (but not visible bcz it doesnt actually save, bcz setAutoCommit is false) takes the data to the table to save but doesnt save
                    con.commit();
                    return true;
                } else { // will be false if some issue is raised during updating Order Detail Table or Item table
                    con.rollback(); // send back the sent data bundle bcz of of some security issues/error
                    return false;
                }
            } else {
                con.rollback();
                return false;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }
        return false;

    }
}
