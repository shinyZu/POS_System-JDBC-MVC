package lk.ijse.pos_system.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.pos_system.model.Discount;
import lk.ijse.pos_system.model.Item;
import lk.ijse.pos_system.view.tm.ItemDiscountTM;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ManageItemsFormController implements Navigator{
    public AnchorPane contextMangeItem;

    public ComboBox<String> cmbItemCode;

    public TextField txtItemCode;
    public TextField txtDescription;
    public TextField txtPackSize;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public TextField txtDiscount;

    public TableView<ItemDiscountTM> tblItemDiscount;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colUnitPrice;
    public TableColumn colQtyOnHand;
    public TableColumn colDiscount;

    public JFXButton btnAddNewItem;
    public JFXButton btnEditItem;
    public JFXButton btnDeleteItem;

    String itemForSearch = null;
    String itemSearched = null;

    public void initialize() {

        try {

            btnAddNewItem.setDisable(false);
            btnEditItem.setDisable(true);
            btnDeleteItem.setDisable(true);

            loadItemCodes();

            colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
            colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
            colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
            colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
            colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
            colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));

            setItemsToTable(new ItemController().getAllItems());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        cmbItemCode.getSelectionModel().selectedItemProperty().
                addListener((observable, oldValue, newValue) -> {
                    itemForSearch = newValue;
                    itemSearched = oldValue;
                });

        tblItemDiscount.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null){
                //
            }else{
                try {
                    loadItemData(newValue);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            btnAddNewItem.setDisable(true);
            btnEditItem.setDisable(false);
            btnDeleteItem.setDisable(false);
        });

        tblItemDiscount.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            rowSelectedForDelete = (int) newValue;
        });
    }

    public ArrayList<ItemDiscountTM> tmItemDiscountList;
    private void setItemsToTable(ArrayList<ItemDiscountTM> itemsWithDiscountList) {
        tmItemDiscountList = itemsWithDiscountList;
        tblItemDiscount.setItems(FXCollections.observableArrayList(tmItemDiscountList));
    }

    List<String> itemCodeList = null;
    private void loadItemCodes() throws SQLException, ClassNotFoundException {
        itemCodeList = new ItemController().getItemCodes();
        cmbItemCode.getItems().addAll(itemCodeList);
    }

    public void searchItemOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        ArrayList<ItemDiscountTM> itemsWithDiscount = new ItemController().getAllItems();

        for (ItemDiscountTM itm : itemsWithDiscount) {
            if (itemForSearch.equals(itm.getItemCode())) {
                txtItemCode.setText(itm.getItemCode());
                txtDescription.setText(itm.getDescription());
                txtPackSize.setText(itm.getPackSize());
                txtUnitPrice.setText(String.valueOf(itm.getUnitPrice()));
                txtQtyOnHand.setText(String.valueOf(itm.getQtyOnHand()));
                txtDiscount.setText(itm.getDiscount());
            }
        }
        btnAddNewItem.setDisable(true);
        btnEditItem.setDisable(false);
        btnDeleteItem.setDisable(false);

    }

    private void loadItemData(ItemDiscountTM rowSelected) { // when a row of the table is selected
        txtItemCode.setText(rowSelected.getItemCode());

        txtDescription.setText(rowSelected.getDescription());
        txtPackSize.setText(rowSelected.getPackSize());
        txtUnitPrice.setText(String.valueOf(rowSelected.getUnitPrice()));
        txtQtyOnHand.setText(String.valueOf(rowSelected.getQtyOnHand()));
        txtDiscount.setText(String.valueOf(rowSelected.getDiscount()));

        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.setPromptText("Item Code");
    }

    public void addNewItemOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, SQLIntegrityConstraintViolationException {

        if (txtItemCode.getText().equals("") || txtUnitPrice.getText().equals("") || txtQtyOnHand.getText().equals("")) {
            new Alert(Alert.AlertType.WARNING, "Please fill all the required details...").show();
            btnAddNewItem.setDisable(false);
            return;
        }

        Item newItem = new Item(
                txtItemCode.getText(),
                txtDescription.getText(),
                txtPackSize.getText(),
                Double.parseDouble(txtUnitPrice.getText()),
                Integer.parseInt(txtQtyOnHand.getText())
        );

        Discount newDiscount = new Discount(
                txtItemCode.getText(),
                txtDescription.getText(),
                txtDiscount.getText()
        );

        if(new ItemController().addItem(newItem)) {
            new Alert(Alert.AlertType.CONFIRMATION, "Item Added Successfully..",ButtonType.OK).showAndWait();

            if (new DiscountController().addDiscount(newDiscount)) {
                //new Alert(Alert.AlertType.CONFIRMATION, "Discount Added Successfully..").show();
            } else {
                //new Alert(Alert.AlertType.CONFIRMATION, "Try Again..").show();
            }

            cmbItemCode.getItems().clear();
            loadItemCodes();
            setItemsToTable(new ItemController().getAllItems());
            clearFields();

        } else {
            new Alert(Alert.AlertType.WARNING, "Duplicate Customer ID").show();
        }
    }

    public void editItemOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Item editItem = new Item(
                txtItemCode.getText(),
                txtDescription.getText(),
                txtPackSize.getText(),
                Double.parseDouble(txtUnitPrice.getText()),
                Integer.parseInt(txtQtyOnHand.getText())
        );

        Discount editDiscount = new Discount(
                txtItemCode.getText(),
                txtDescription.getText(),
                txtDiscount.getText()
        );



            ButtonType yes= new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no= new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to edit this Item?",yes,no);
            alert.setTitle("Confirmation Alert");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.orElse(no) == yes) {
                if (new ItemController().updateItem(editItem) | new DiscountController().updateDiscount(editDiscount)) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Item Updated Successfully..", ButtonType.OK).show();

                    setItemsToTable(new ItemController().getAllItems());
                    clearFields();

                } else {
                    new Alert(Alert.AlertType.WARNING, "Try Again").show();
                }
            } else {
                //
            }
    }

    int rowSelectedForDelete = -1;
    public void deleteItemOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        if(rowSelectedForDelete == -1){
            new Alert(Alert.AlertType.WARNING,"Please Select a Row to Remove").show();
        }else {

            ButtonType yes= new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no= new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to delete this Item?",yes,no);
            alert.setTitle("Confirmation Alert");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.orElse(no) == yes) {

                if (new ItemController().deleteItem(txtItemCode.getText())) {
                    tmItemDiscountList.remove(rowSelectedForDelete);
                    setItemsToTable(tmItemDiscountList);
                    tblItemDiscount.refresh();

                    new Alert(Alert.AlertType.CONFIRMATION, "Item Deleted Successfully",ButtonType.OK).show();
                    clearFields();

                } else {
                    new Alert(Alert.AlertType.WARNING, "Try Again...").show();
                }

                cmbItemCode.getItems().clear();
                loadItemCodes();

            } else {

            }
        }
    }

    private void clearFields() {
        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.setPromptText("Item Code");

        txtItemCode.clear();
        txtDescription.clear();
        txtPackSize.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtDiscount.clear();

        btnAddNewItem.setDisable(false);
        btnEditItem.setDisable(true);
        btnDeleteItem.setDisable(true);
    }

    public void clearFieldsOnAction(ActionEvent actionEvent) {
        clearFields();
    }

    public void enableBtnAddNewItem(MouseEvent mouseEvent) {
        btnAddNewItem.setDisable(false);
    }

    @Override
    public void goToPreviousPageOnAction(MouseEvent mouseEvent) throws IOException {
        URL resource = getClass().getResource("../view/AdminDashBoardForm.fxml");
        Parent load = FXMLLoader.load(resource);
        contextMangeItem.getChildren().clear();
        contextMangeItem.getChildren().add(load);
    }

    @Override
    public void logoutOnAction(MouseEvent mouseEvent) {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to Logout?",yes,no);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(no)==yes){
            Stage window = (Stage) contextMangeItem.getScene().getWindow();
            window.close();
        }else{

        }
    }
}
