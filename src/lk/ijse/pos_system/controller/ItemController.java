package lk.ijse.pos_system.controller;

import lk.ijse.pos_system.db.DBConnection;
import lk.ijse.pos_system.model.Item;
import lk.ijse.pos_system.view.tm.ItemDiscountTM;
import lk.ijse.pos_system.view.tm.ReportTM;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ItemController implements ManageItem {

    @Override
    public boolean addItem(Item newItem) throws SQLException, ClassNotFoundException {

        Connection con= DBConnection.getInstance().getConnection();
        String insertQuery="INSERT INTO Item VALUES(?,?,?,?,?)";

        PreparedStatement stm1 = con.prepareStatement(insertQuery);

        if (duplicateEntryExists(con,newItem)) {
            return false;
        }

        stm1.setObject(1, newItem.getItemCode());
        stm1.setObject(2, newItem.getDescription());
        stm1.setObject(3, newItem.getPackSize());
        stm1.setObject(4, newItem.getUnitPrice());
        stm1.setObject(5, newItem.getQtyOnHand());

        return stm1.executeUpdate()>0;
    }

    @Override
    public Item getItem(String itemCode) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DBConnection.getInstance().getConnection()
                .prepareStatement("SELECT * FROM Item WHERE itemCode = ?");
        stm.setObject(1, itemCode);
        ResultSet resultSet = stm.executeQuery();

        if (resultSet.next()) {
            return new Item(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getDouble(4),
                    resultSet.getInt(5)
            );
        } else {
            return null;
        }
    }

    @Override
    public boolean updateItem(Item editItem) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DBConnection.getInstance().getConnection().
                prepareStatement("UPDATE Item SET description=?, packSize=?, unitPrice=?, qtyOnHand=? WHERE itemCode=?");

        stm.setObject(1,editItem.getDescription());
        stm.setObject(2,editItem.getPackSize());
        stm.setObject(3,editItem.getUnitPrice());
        stm.setObject(4,editItem.getQtyOnHand());
        stm.setObject(5,editItem.getItemCode());

        return stm.executeUpdate()>0;
    }

    @Override
    public boolean deleteItem(String itemCode) throws SQLException, ClassNotFoundException {

        if (DBConnection.getInstance().getConnection().
                prepareStatement("DELETE FROM Item WHERE itemCode = '"+itemCode+"'").executeUpdate()>0){
            return true;
        }else{
            return false;
        }
    }

    public ArrayList<String> getItemCodes() throws SQLException, ClassNotFoundException {
        ResultSet rst = DBConnection.getInstance().
                getConnection().prepareStatement("SELECT * FROM Item").executeQuery();
        ArrayList<String> itemCodeList = new ArrayList<>();

        if (rst == null) {
            return null;
        } else {
            while (rst.next()) {
                itemCodeList.add(
                        rst.getString(1)
                );
            }
        }

        return itemCodeList;
    }

    public List<String> getItemDescriptions() throws SQLException, ClassNotFoundException {

        ResultSet rst = DBConnection.getInstance().
                getConnection().prepareStatement("SELECT description FROM Item").executeQuery();
        ArrayList<String> itemDescrpList = new ArrayList<>();

        if (rst == null) {
            return null;
        } else {
            while (rst.next()) {
                itemDescrpList.add(
                        rst.getString(1)
                );
            }
        }

        return itemDescrpList;
    }

    public String getDescription(String description) throws SQLException, ClassNotFoundException {

        PreparedStatement stm = DBConnection.getInstance().getConnection()
                .prepareStatement("SELECT itemCode FROM Item WHERE description = ?");
        stm.setObject(1, description);
        ResultSet resultSet = stm.executeQuery();

        if (resultSet.next()) {
            return resultSet.getString(1);

        } else {
            return null;
        }
    }

    public ArrayList<ItemDiscountTM> getAllItems() throws SQLException, ClassNotFoundException {  // INNER JOIN

        PreparedStatement stm = DBConnection.getInstance().getConnection().
                prepareStatement("SELECT i.itemCode, i.description,i.packSize,i.unitPrice,i.qtyOnHand,d.discount\n" +
                        "FROM Item i INNER JOIN Discount d\n" +
                        "ON i.itemCode = d.itemCode; ");
        ResultSet rst = stm.executeQuery();

        //ArrayList<ItemDiscountTM> itemsInDBList = new ArrayList<>();
        ArrayList<ItemDiscountTM> itemsWithDiscountList = new ArrayList<>();

        while (rst.next()) {
            itemsWithDiscountList.add(new ItemDiscountTM(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getDouble(4),
                    rst.getInt(5),
                    rst.getString(6)
            ));
        }
        return itemsWithDiscountList;
    }

    private boolean duplicateEntryExists(Connection con, Item newItem) throws SQLException {
        String selectQuery="SELECT * FROM Item WHERE itemCode = ?";
        PreparedStatement stm2 = con.prepareStatement(selectQuery);

        stm2.setObject(1,newItem.getItemCode());

        ResultSet rst = stm2.executeQuery();
        if (rst.next()) {
            return true;
        }
        return false;
    }

    public boolean updateQtyOnHand(String itemCode, int orderQty) throws SQLException, ClassNotFoundException {

        Connection con = DBConnection.getInstance().getConnection();
       // con.setAutoCommit(false);

        String selectQuery = "SELECT qtyOnHand FROM Item WHERE itemCode = ?";
        String updateQuery = "UPDATE Item SET qtyOnHand = ? WHERE itemCode = ?";

        PreparedStatement stm1 = con.prepareStatement(selectQuery);
        stm1.setObject(1,itemCode); //I-006

        ResultSet rst = stm1.executeQuery();

        int currentQtyOnHand = 0;
        if (rst.next()) {
            currentQtyOnHand = Integer.parseInt(rst.getString(1));
        }

        PreparedStatement stm = con.prepareStatement(updateQuery);
        stm.setObject(1,(currentQtyOnHand - orderQty));
        stm.setObject(2,itemCode);

        return stm.executeUpdate() > 0;

    }

    public String getQtyOnHand(String itemCode) throws SQLException, ClassNotFoundException {

        PreparedStatement stm = DBConnection.getInstance().getConnection()
                .prepareStatement("SELECT qtyOnHand FROM Item WHERE itemCode = ?");
        stm.setObject(1, itemCode);
        ResultSet resultSet = stm.executeQuery();

        if (resultSet.next()) {
            return resultSet.getString(1);

        } else {
            return null;
        }
    }

    public boolean editQtyOnHand(String itemCode, int qtyBackToStock) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DBConnection.getInstance().getConnection()
                .prepareStatement("UPDATE Item SET qtyOnHand = ? WHERE itemCode = ?");

        stm.setObject(1, qtyBackToStock);
        stm.setObject(2, itemCode);

        return stm.executeUpdate() > 0;
    }

    public ArrayList<ReportTM> getDailyReport(LocalDate date) throws SQLException, ClassNotFoundException {

        String str = String.valueOf(date);
        String[] arrOfStr = str.split("-", 3);
        String d = arrOfStr[2];
        ArrayList<ReportTM> dailyReportTable = new ArrayList<>();

        Connection con = DBConnection.getInstance().getConnection();

        String query1 = "SELECT i.itemCode, SUM(od.orderQTY)\n" +
                "FROM Item i INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "INNER JOIN Orders o\n" +
                "ON o.orderID = od.orderId "+
                "WHERE o.orderDate = ? "+
                "GROUP BY od.itemCode";

        String query2 = "SELECT i.itemCode, i.description, SUM(i.unitPrice * i.packSize * od.orderQTY * (100-d.discount) / 100)" +
                "FROM Item i INNER JOIN Discount d\n" +
                "ON i.itemCode = d.itemCode\n" +
                "INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "INNER JOIN Orders o\n" +
                "ON o.orderID = od.orderId "+
                "WHERE o.orderDate = ? "+
                "GROUP BY i.itemCode";


        PreparedStatement stm1 = con.prepareStatement(query1);
        PreparedStatement stm2 = con.prepareStatement(query2);

        stm1.setObject(1,date);
        stm2.setObject(1,date);

        ResultSet rst1 = stm1.executeQuery();
        ResultSet rst2 = stm2.executeQuery();

        while (rst2.next()) {
            if (rst1.next()) {
               if (rst1.getString(1).equals(rst2.getString(1))) {
                   // dailyReportTable.add(rst2.getString(1), rst2.getString(2), rst1.getInt(2),rst2.getDouble(3));
                   String itemCode = rst2.getString(1);
                   String description = rst2.getString(2);
                   int qtySold = rst1.getInt(2);
                   double income = rst2.getDouble(3);

                    dailyReportTable.add( new ReportTM(
                            itemCode,
                            description,
                            qtySold,
                            income
                    ));
                }
            }
        }
        return dailyReportTable;
    }

    //----------------------------------------------------------------------------------------------------------------------------

    public boolean updateEditedQtyOnHand(String itemCode, int newQtyOnHand) throws SQLException, ClassNotFoundException {

        PreparedStatement stm = DBConnection.getInstance().getConnection().
                prepareStatement("UPDATE Item SET qtyOnHand = ?  WHERE itemCode = ?");

        stm.setObject(1,newQtyOnHand);
        stm.setObject(2,itemCode);

        return stm.executeUpdate() > 0;
    }
}
