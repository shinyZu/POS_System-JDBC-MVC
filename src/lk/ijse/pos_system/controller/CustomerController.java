package lk.ijse.pos_system.controller;

import lk.ijse.pos_system.db.DBConnection;
import lk.ijse.pos_system.model.Customer;
import lk.ijse.pos_system.view.tm.CustomerWiseIncomeReportTM;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerController implements ManageCustomer {

    @Override
    public boolean addCustomer(Customer newCust, List<String> customerIdList) throws SQLException, ClassNotFoundException {

        PreparedStatement stm = null;

        for (String id : customerIdList) {
            if (newCust.getCustID().equals(id)) {
                //new Alert(Alert.AlertType.WARNING, "Duplicate Customer ID").show();
                return false;
            } else {
                stm = DBConnection.getInstance().getConnection()
                        .prepareStatement("INSERT INTO Customer VALUES (?,?,?,?,?,?,?)");

                stm.setObject(1,newCust.getCustID());
                stm.setObject(2,newCust.getCustTitle());
                if (newCust.getCustName() == null) {
                    stm.setObject(3,"Unknown");
                }else{
                    stm.setObject(3,newCust.getCustName());
                }
                stm.setObject(4,newCust.getCustAddress());
                stm.setObject(5,newCust.getCity());
                stm.setObject(6,newCust.getProvince());
                stm.setObject(7,newCust.getPostalCode());

            }
        }
        return stm.executeUpdate() > 0;
    }

    @Override
    public Customer getCustomer(String id) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DBConnection.getInstance().getConnection()
                .prepareStatement("SELECT * FROM Customer WHERE custID = ?");
        stm.setObject(1,id);
        ResultSet resultSet = stm.executeQuery();

        if (resultSet.next()){
            return new Customer(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getString(6),
                    resultSet.getString(7)
            );
        }else {
            return null;
        }
    }

    public List<String> getCustomerIds() throws SQLException, ClassNotFoundException {
        ResultSet rst = DBConnection.getInstance().
                getConnection().prepareStatement("SELECT * FROM Customer").executeQuery();
        List<String> custIDList = new ArrayList<>();
        while (rst.next()){
            custIDList.add(
                    rst.getString(1)
            );
        }
        return custIDList;
    }

    public String generateCustomerID() throws SQLException, ClassNotFoundException {
        ResultSet rst = DBConnection.getInstance().getConnection().
                prepareStatement("SELECT * FROM Customer ORDER BY custID DESC LIMIT 1").executeQuery();

        if (rst.next()){
            int tempId = Integer.parseInt(rst.getString(1).split("-")[1]);
            tempId = tempId+1;

            if (tempId <= 9){
                return "C-00" + tempId;
            }else if (tempId <= 99){
                return "C-0" + tempId;
            }else {
                return "C-" + tempId;
            }

        }else {
            return "C-001";
        }
    }

    public ArrayList<CustomerWiseIncomeReportTM> getCustomerWiseIncome(LocalDate date) throws SQLException, ClassNotFoundException {

        ArrayList<CustomerWiseIncomeReportTM> custIncomeTable = new ArrayList<>();

        String query1 = "SELECT o.custID, c.custTitle, c.custName, c.city, SUM(o.orderCost)" +
                "FROM Orders o INNER JOIN Customer c\n" +
                "ON c.custID = o.custID\n" +
                "WHERE o.orderDate = ?"+
                "GROUP BY custID;";

        PreparedStatement stm = DBConnection.getInstance().getConnection().prepareStatement(query1);
        stm.setObject(1,date);

        ResultSet rst1 = stm.executeQuery();
        while (rst1.next()) {
            custIncomeTable.add(new CustomerWiseIncomeReportTM(
                    rst1.getString(1),
                    rst1.getString(2),
                    rst1.getString(3),
                    rst1.getString(4),
                    rst1.getDouble(5)
            ));
        }
        return custIncomeTable;
    }
}
