package lk.ijse.pos_system.controller;

import lk.ijse.pos_system.model.Item;

import java.sql.SQLException;

public interface ManageItem {

    boolean addItem(Item newItem) throws SQLException, ClassNotFoundException;

    Item getItem(String itemCode) throws SQLException, ClassNotFoundException;

    boolean updateItem(Item editItem) throws SQLException, ClassNotFoundException;

    boolean deleteItem(String itemCode) throws SQLException, ClassNotFoundException;
}
