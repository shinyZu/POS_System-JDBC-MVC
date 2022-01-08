package lk.ijse.pos_system.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lk.ijse.pos_system.db.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDetailController {

    public boolean verifyUser(String userName, String tPassword, String fPassword) throws SQLException, ClassNotFoundException {

        PreparedStatement stm = DBConnection.getInstance().getConnection()
                .prepareStatement("SELECT * FROM UserDetail WHERE userName = ?");
        stm.setObject(1, userName);

        ResultSet resultSet = stm.executeQuery();

        if (userName.equals("") || fPassword.equals("")) {
            //new Alert(Alert.AlertType.WARNING,"Please fill the required fields...", ButtonType.OK).show();
            return false;
        }

        if (resultSet.next()) {
            if (resultSet.getObject("userType").equals("ADMIN") | resultSet.getObject("userType").equals("CASHIER")) {

                if (resultSet.getString("userName").equals(userName)
                        && (resultSet.getString("userPassword").equals(fPassword)) || resultSet.getString("userPassword").equals(tPassword)){
                    //
                } else {
                    return false;
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Invalid User...").show();
                return false;
            }

        }
        return true;
    }
}
