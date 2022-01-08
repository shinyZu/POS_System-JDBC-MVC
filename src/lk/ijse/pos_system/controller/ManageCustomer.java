package lk.ijse.pos_system.controller;

import lk.ijse.pos_system.model.Customer;

import java.sql.SQLException;
import java.util.List;

public interface ManageCustomer {

    Customer getCustomer(String id) throws SQLException, ClassNotFoundException;

    boolean addCustomer(Customer newCust, List<String> cmbCustIDs) throws SQLException, ClassNotFoundException;
}
