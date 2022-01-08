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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lk.ijse.pos_system.db.DBConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainFormController {

    public AnchorPane contextMainForm;
    public Pane gridPane1_0;

    public ImageView imgHidePwd;
    public ImageView imgShowPwd;
    public TextField txtPassword;
    public PasswordField fieldPassword;
    public TextField txtUserName;
    public JFXButton btnAdmin;
    public JFXButton btnCashier;

    public void goToCashierLoginForm(ActionEvent actionEvent) throws IOException {
        String btn = ((JFXButton) actionEvent.getSource()).getText();
        System.out.println(btn);

        URL resource = getClass().getResource("../view/CashierLoginForm.fxml");
        Parent load = FXMLLoader.load(resource);
        gridPane1_0.getChildren().clear();
        gridPane1_0.getChildren().add(load);
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

        if (new UserDetailController().verifyUser(userName, tPassword, fPassword)) {
            Stage window = new Stage();
            window.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/AdminDashBoardForm.fxml"))));
            window.show();

            txtUserName.clear();
            txtPassword.clear();
            fieldPassword.clear();
        } else {
            new Alert(Alert.AlertType.WARNING, "Invalid UserName or Password...").show();
        }

    }
}
