package lk.ijse.pos_system.controller;

import lk.ijse.pos_system.db.DBConnection;
import lk.ijse.pos_system.model.Discount;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscountController {

    public boolean addDiscount(Discount newDiscount) throws SQLException, ClassNotFoundException {

        PreparedStatement stm = DBConnection.getInstance().getConnection()
                .prepareStatement("INSERT INTO Discount (itemCode,description,discount) VALUES (?,?,?)");


        stm.setObject(1,newDiscount.getItemCode());
        stm.setObject(2,newDiscount.getDescription());
        stm.setObject(3,newDiscount.getDiscount());

        return stm.executeUpdate()>0;
    }

    public boolean updateDiscount(Discount editDiscount) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DBConnection.getInstance().getConnection().
                prepareStatement("SELECT itemCode,discount FROM Discount WHERE itemCode = ?"); ///12%
        stm.setObject(1,editDiscount.getItemCode()); //I-003

        PreparedStatement stm2 = DBConnection.getInstance().getConnection().
                prepareStatement("UPDATE Discount SET discount = ? WHERE itemCode = ?"); //11%
        stm2.setObject(1,editDiscount.getDiscount());
        stm2.setObject(2,editDiscount.getItemCode());


        ResultSet rst = stm.executeQuery();
        if (rst.next()) {
            if (rst.getString("discount").equals(editDiscount.getDiscount())) {
                return false;
            }
        }
        return stm2.executeUpdate()>0; //return true
    }

    public String getDiscount(String itemCode) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DBConnection.getInstance().getConnection()
                .prepareStatement("SELECT discount FROM Discount WHERE itemCode = ?");
        stm.setObject(1, itemCode);
        ResultSet resultSet = stm.executeQuery();

        if (resultSet.next()) {
            return resultSet.getString(1);

        } else {
            return null;
        }
    }

}
