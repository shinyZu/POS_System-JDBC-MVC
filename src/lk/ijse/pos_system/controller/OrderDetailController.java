package lk.ijse.pos_system.controller;

import lk.ijse.pos_system.db.DBConnection;
import lk.ijse.pos_system.model.OrderDetail;
import lk.ijse.pos_system.view.tm.OrderListTM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDetailController {

    public boolean saveOrderDetail(ArrayList<OrderDetail> items) throws SQLException, ClassNotFoundException {

        for ( OrderDetail odt : items ) {

            PreparedStatement stm = DBConnection.getInstance().getConnection().
                    prepareStatement("INSERT INTO OrderDetail VALUES (?,?,?,?)");
                    //prepareStatement("INSERT INTO OrderDetail VALUES (?,?,?,?,?)");

            stm.setObject(1,odt.getOrderID()); // O-008
            stm.setObject(2,odt.getItemCode()); // I-006
            stm.setObject(3,odt.getOrderQTY()); //3
            stm.setObject(4,odt.getDiscount()); // 270

            if (stm.executeUpdate() > 0) { // will be true if the OrderDetail table  gets updated(doesnt save bcz setAutoCommit is still false)
                if (new ItemController().updateQtyOnHand(odt.getItemCode(),odt.getOrderQTY())) { // will be true if qtyOnHand of Item table is updated
                    //
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public int getOrderQTY(String itemCode) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DBConnection.getInstance().getConnection().
                prepareStatement("SELECT orderQTY FROM OrderDetail WHERE itemCode = ?");
        stm.setObject(1,itemCode);

        ResultSet rst = stm.executeQuery();

        if (rst.next()) {
            return rst.getInt(1);
        }
        return 0;
    }

    public ArrayList<OrderListTM> getOrderedItems(String orderSelected, OrderListTM itemSelected) throws SQLException, ClassNotFoundException {

        ArrayList<OrderListTM> listOfOrderedItems = new ArrayList<>();

        Connection con = DBConnection.getInstance().getConnection();
        String selectQuery = "SELECT i.itemCode, i.description, i.packSize, i.unitPrice, od.orderQTY, od.discount " +
                "FROM Item i INNER JOIN OrderDetail od " +
                "ON i.itemCode = od.itemCode\n" +
                "where orderId = ?";


        String query = "SELECT discount FROM Discount WHERE itemCode = ?"; // %
        int discountAsPercentage = 0;

        if (itemSelected != null) {
            PreparedStatement stm1 = con.prepareStatement(query);
            stm1.setObject(1,itemSelected.getItemCode());

            ResultSet rst1 = stm1.executeQuery();
            if (rst1.next()) {
                discountAsPercentage = rst1.getInt(1);
            }
        }

        PreparedStatement stm2 = con.prepareStatement(selectQuery);
        stm2.setObject(1,orderSelected);

        ResultSet rst2 = stm2.executeQuery();

        double discount = 0;

        while (rst2.next()) {
            int packSize =  new PlaceCustomerOrderFormController().splitPackSize(rst2.getString(1),rst2.getString(3));
            double unitPrice = rst2.getDouble(4);
            int orderQty = rst2.getInt(5);
            double subTotal = unitPrice * packSize * orderQty;

            if (discountAsPercentage != 0 ){
                discount = subTotal * discountAsPercentage / 100 ;
            } else {
                discount = rst2.getDouble(6);
            }

            double total = subTotal - discount;

            listOfOrderedItems.add(new OrderListTM(
                    rst2.getString(1),
                    rst2.getString(2),
                    rst2.getString(3),
                    unitPrice,
                    orderQty,
                    subTotal,
                    discount,
                    total
            ));
        }
        return listOfOrderedItems;
    }

    public ArrayList<Double> getOldPaymentInfo(String orderSelected) throws SQLException, ClassNotFoundException {

        ArrayList<Double> oldPaymentInfo = new ArrayList<>();

        String selectQuery = "SELECT od.orderId, i.itemCode, i.unitPrice, i.packSize, od.orderQTY, od.discount\n" +
                "FROM Item i INNER JOIN OrderDetail od \n" +
                "ON i.itemCode = od.itemCode\n" +
                "WHERE od.orderID = ? AND i.itemCode IN (SELECT itemCode FROM OrderDetail WHERE orderId = ? )";

        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(selectQuery);

        stm.setObject(1,orderSelected);
        stm.setObject(2,orderSelected);

        double orderSubtotal =0;
        double orderDiscount = 0;
        double orderTotal = 0;

        double unitPrice = 0;
        int packSize = 0;
        int orderQty = 0;


        ResultSet rst = stm.executeQuery();

        while(rst.next()){

            unitPrice = rst.getDouble(3);
            packSize =  Integer.parseInt(rst.getString(4).split(" ")[0]);
            orderQty = rst.getInt(5);

            orderDiscount += rst.getDouble(6);
            orderSubtotal += (unitPrice * packSize * orderQty);
            orderTotal += (orderSubtotal - orderDiscount);

        }

        PreparedStatement stm2 = con.prepareStatement("SELECT orderCost FROM Orders WHERE orderId = ?");
        stm2.setObject(1,orderSelected);
        ResultSet rst2 = stm2.executeQuery();

        if (rst2.next()) {
            orderTotal = rst2.getDouble(1);
        }

        oldPaymentInfo.add(0,orderSubtotal);
        oldPaymentInfo.add(1,orderDiscount);
        oldPaymentInfo.add(2,orderTotal);

        return oldPaymentInfo;
    }


    public boolean updateOrderQty(String orderId, String itemCode, int newOrderQty, double unitPrice, int packSize, int dicountPerUnit) throws SQLException, ClassNotFoundException {

        double newDiscount = unitPrice * packSize * newOrderQty * dicountPerUnit / 100;

        PreparedStatement stm = DBConnection.getInstance().getConnection().
                prepareStatement("UPDATE OrderDetail SET orderQTY = ?, discount = ? WHERE orderId = ? AND itemCode = ?");

        stm.setObject(1,newOrderQty);
        stm.setObject(2,newDiscount);
        stm.setObject(3,orderId);
        stm.setObject(4,itemCode);

        return stm.executeUpdate() > 0;
    }

    public boolean removeItem(String itemCode, String orderId) throws SQLException, ClassNotFoundException {

        PreparedStatement stm = DBConnection.getInstance().getConnection().
                prepareStatement("DELETE FROM OrderDetail WHERE itemCode = ? AND orderID = ?");

        stm.setObject(1,itemCode);
        stm.setObject(2,orderId);

        return stm.executeUpdate() > 0;
    }

    public String getMostMovableItem() throws SQLException, ClassNotFoundException {

        String mostMovableItem = "I-000";
        String query1 = "SELECT DISTINCT itemCode, count(itemCode), SUM(orderQty) FROM OrderDetail GROUP BY itemCode " +
                "ORDER BY SUM(orderQty) DESC LIMIT 1";

        PreparedStatement stm = DBConnection.getInstance().getConnection().prepareStatement(query1);
        ResultSet rst1 = stm.executeQuery();

        if (rst1.next()) {
            mostMovableItem = rst1.getString(1);
            return mostMovableItem;
        }
        return mostMovableItem;
    }

    public String getLeastMovableItem() throws SQLException, ClassNotFoundException {
        String leastMovableItem = "I-000";
        String query1 = "SELECT DISTINCT itemCode, count(itemCode), SUM(orderQty) FROM OrderDetail GROUP BY itemCode " +
                "ORDER BY SUM(orderQty) ASC LIMIT 1";

        PreparedStatement stm = DBConnection.getInstance().getConnection().prepareStatement(query1);
        ResultSet rst1 = stm.executeQuery();

        if (rst1.next()) {
            leastMovableItem = rst1.getString(1);
            return leastMovableItem;
        }
        return leastMovableItem;
    }


    // ---------------------------------------------------------------------------------------------------------------------------
    public boolean updateOrderDetail(OrderDetail orderDetailToBeUpdated, int newQtyOnHand) throws SQLException, ClassNotFoundException {

        System.out.println("new orderQty in OrderDetails table should be - "+orderDetailToBeUpdated.getOrderQTY());
        System.out.println("new discount in OrderDetails table should be - "+orderDetailToBeUpdated.getDiscount());

        PreparedStatement stm = DBConnection.getInstance().getConnection().
                prepareStatement("UPDATE OrderDetail SET orderQTY = ? AND discount = ? WHERE orderId = ? AND itemCode = ?");

        stm.setObject(1,orderDetailToBeUpdated.getOrderQTY());
        stm.setObject(2,orderDetailToBeUpdated.getDiscount());
        stm.setObject(3,orderDetailToBeUpdated.getOrderID());
        stm.setObject(4,orderDetailToBeUpdated.getItemCode());

        if (stm.executeUpdate() > 0) {
            if (new ItemController().updateEditedQtyOnHand(orderDetailToBeUpdated.getItemCode(),newQtyOnHand)) {
                //
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean updateDetail(OrderDetail orderDetailToBeUpdated) throws SQLException, ClassNotFoundException { // when moved to Trash

        PreparedStatement stm = DBConnection.getInstance().getConnection().
                prepareStatement("UPDATE OrderDetail SET orderQTY = ? AND discount = ? WHERE orderId = ? AND itemCode = ?");

        stm.setObject(1,orderDetailToBeUpdated.getOrderQTY());
        stm.setObject(2,orderDetailToBeUpdated.getDiscount());
        stm.setObject(3,orderDetailToBeUpdated.getOrderID());
        stm.setObject(4,orderDetailToBeUpdated.getItemCode());

        return stm.executeUpdate() > 0;

    }
}
