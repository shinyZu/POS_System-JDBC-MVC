package lk.ijse.pos_system.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import lk.ijse.pos_system.view.tm.ItemDiscountTM;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.net.URL;

public class AdminDashBoardFormController {
    public AnchorPane contextAdminDashBoard;

    public void goToManageItemsFormOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = getClass().getResource("../view/ManageItemsForm.fxml");
        Parent load = FXMLLoader.load(resource);
        contextAdminDashBoard.getChildren().clear();
        contextAdminDashBoard.getChildren().add(load);
    }

    public void goToSystemReportsOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = getClass().getResource("../view/SystemReportForm.fxml");
        Parent load = FXMLLoader.load(resource);
        contextAdminDashBoard.getChildren().clear();
        contextAdminDashBoard.getChildren().add(load);
    }
}
