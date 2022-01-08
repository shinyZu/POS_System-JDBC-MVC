package lk.ijse.pos_system.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

public class CashierDashBoardFormController {
    public AnchorPane contextCashierDashBoard;

    public void goToPlaceCustomerOrderFormOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = getClass().getResource("../view/PlaceCustomerOrderForm.fxml");
        Parent load = FXMLLoader.load(resource);
        contextCashierDashBoard.getChildren().clear();
        contextCashierDashBoard.getChildren().add(load);
    }

    public void goToManageOrderFormOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = getClass().getResource("../view/ManageOrdersForm.fxml");
        Parent load = FXMLLoader.load(resource);
        contextCashierDashBoard.getChildren().clear();
        contextCashierDashBoard.getChildren().add(load);
    }
}
