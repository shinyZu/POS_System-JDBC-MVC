package lk.ijse.pos_system.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.pos_system.db.DBConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CashierLoginFormController {
    public AnchorPane contextCashierLoginForm;

    public TextField txtUserName;
    public TextField txtPassword;
    public PasswordField fieldPassword;
    public ImageView imgHidePwd;
    public ImageView imgShowPwd;

    public void goToAdminLoginForm(ActionEvent actionEvent) throws IOException {
        URL resource = getClass().getResource("../view/AdminLoginForm.fxml");
        Parent load = FXMLLoader.load(resource);
        contextCashierLoginForm.getChildren().clear();
        contextCashierLoginForm.getChildren().add(load);
    }

    public void hidePasswordOnAction(MouseEvent mouseEvent) {
        fieldPassword.toFront();
        imgShowPwd.toFront();
        fieldPassword.setText(txtPassword.getText());
    }

    public void showPasswordOnAction(MouseEvent mouseEvent) {
        txtPassword.toFront();
        imgHidePwd.toFront();
        txtPassword.setText(fieldPassword.getText());
    }

    public void signUpAsCashierOnAction(ActionEvent actionEvent) throws IOException {
        /*Stage window = new Stage();
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/CashierDashBoardForm.fxml"))));
        window.show();*/
    }

    public void loginAsCashierOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, IOException {
        String userName = txtUserName.getText();
        String tPassword = txtPassword.getText();
        String fPassword = fieldPassword.getText();

        if (new UserDetailController().verifyUser(userName, tPassword, fPassword)) {
            Stage window = new Stage();
            window.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/CashierDashBoardForm.fxml"))));
            window.show();

            txtUserName.clear();
            txtPassword.clear();
            fieldPassword.clear();
        } else {
            new Alert(Alert.AlertType.WARNING, "Invalid UserName or Password...").show();
        }
    }
}
