package lk.ijse.pos_system.controller;

import com.jfoenix.controls.JFXButton;
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

public class AdminLoginFormController {
    public AnchorPane contextAdminLoginForm;

    public TextField txtUserName;
    public TextField txtPassword;
    public PasswordField fieldPassword;
    public ImageView imgHidePwd;
    public ImageView imgShowPwd;

    public JFXButton btnSignUp;
    public JFXButton btnLogin;


    public void goToCashierLoginForm(ActionEvent actionEvent) throws IOException {
        URL resource = getClass().getResource("../view/CashierLoginForm.fxml");
        Parent load = FXMLLoader.load(resource);
        contextAdminLoginForm.getChildren().clear();
        contextAdminLoginForm.getChildren().add(load);
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

    public void signUpAsAdminOnAction(ActionEvent actionEvent) throws IOException {
        /*Stage window = new Stage();
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/AdminDashBoardForm.fxml"))));
        window.show();*/
    }

    public void loginAsAdminOnAction(ActionEvent actionEvent) throws IOException, SQLException, ClassNotFoundException {

        String userName = txtUserName.getText();
        String tPassword = txtPassword.getText();
        String fPassword = fieldPassword.getText();

        if (new UserDetailController().verifyUser(userName,tPassword,fPassword)) {
            Stage window = new Stage();
            window.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/AdminDashBoardForm.fxml"))));
            window.show();

            txtUserName.clear();
            txtPassword.clear();
            fieldPassword.clear();
        }
        else {
            new Alert(Alert.AlertType.WARNING, "Invalid UserName or Password...").show();
        }
    }
}
