package lk.ijse.pos_system.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.pos_system.model.Customer;
import lk.ijse.pos_system.model.OrderDetail;
import lk.ijse.pos_system.model.Orders;
import lk.ijse.pos_system.view.tm.OrderListTM;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ManageOrdersFormController implements Navigator {
    public AnchorPane contextManageOrder;

    public Label lblTime;
    public Label lblDate;

    public TextField txtCustomerID;
    public JFXButton btnSearchOrders;
    public Label lblCustomerID;
    public ListView<String> orderListView;

    public TextField txtItemCode;
    public TextField txtDescription;
    public TextField txtPackSize;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public TextField txtDiscount;
    public TextField txtOrderQty;

    public JFXCheckBox chkBoxToTrash;
    public JFXCheckBox chkBoxToStock;
    public JFXCheckBox chkBoxFromStock;

    public JFXButton btnRemoveItem;
    public JFXButton btnEditOrderItem;

    public TableView<OrderListTM> tblManageOrder;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colUnitPrice;
    public TableColumn colOrderQty;
    public TableColumn colSubtotal;
    public TableColumn colDiscount;
    public TableColumn colTotal;

    public Label lblOrderSubtotal;
    public Label lblOrderTotalDiscount;
    public Label lblOrderCost;

    public Label lblOrderNewCost;
    public Label lblRefund;
    public Label lblAmountToPay;

    public Label lblModifiedDate;
    public Label lblModifiedTime;

    public JFXButton btnConfirmEdits;
    public JFXButton btnCancel;


    OrderListTM itemSelected = null; // from Table
    String orderIdSelected = null; // from ListView

    public void initialize() {

        loadDateAndTime();

        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colOrderQty.setCellValueFactory(new PropertyValueFactory<>("orderQTY"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        orderListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {

                orderIdSelected = newValue; // selected orderID from list
                setItemDetailsToTable(orderIdSelected);
                // loadOldPaymentInfo(orderIdSelected);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });


        tblManageOrder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue == null) {
                    //
                } else {

                    itemSelected = newValue;
                    loadItemDataToFields(newValue);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadDateAndTime() {
        // load Date
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        lblDate.setText(f.format(date));

        // load Time
        Timeline time = new Timeline(new KeyFrame(Duration.ZERO, e -> {

            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm:ss a");

            Calendar cal = new GregorianCalendar();
            Date date1 = cal.getTime();
            lblTime.setText(simpleTimeFormat.format(date1));
        }),
                new KeyFrame(Duration.seconds(1))
        );
        time.setCycleCount(Animation.INDEFINITE);
        time.play();
    }

    List<String> listOfOrders = null;

    public void searchOrdersOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String validCustID = txtCustomerID.getText().split("-")[0];

        if (validCustID.equals("C")) {
            orderListView.getItems().clear();
            listOfOrders = new OrderController().searchOrdersByCustID(txtCustomerID.getText());
            for (String orderID : listOfOrders) {
                orderListView.getItems().add(orderID);
            }

            if (listOfOrders.size() != 0) {
                lblCustomerID.setText(txtCustomerID.getText());
            } else {
                //
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Invalid Customer ID...").show();
        }
    }

    ArrayList<OrderListTM> listOfOrderedItems = null;

    private void setItemDetailsToTable(String orderSelected) throws SQLException, ClassNotFoundException {
        loadOldPaymentInfo(orderSelected);
        clearFields();
        clearNewPaymentInfo();

        listOfOrderedItems = new OrderDetailController().getOrderedItems(orderSelected, itemSelected);
        tblManageOrder.setItems(FXCollections.observableArrayList(listOfOrderedItems));
    }

    private void clearOldPaymentInfo() {
        lblOrderSubtotal.setText("0.00");
        lblOrderTotalDiscount.setText("0.00");
        lblOrderCost.setText("0.00");
    }

    private void clearNewPaymentInfo() {
        lblOrderNewCost.setText("0.00");
        lblRefund.setText("0.00");
        lblAmountToPay.setText("0.00");
    }

    private void clearFields() {
        txtItemCode.clear();
        txtDescription.clear();
        txtUnitPrice.clear();
        txtPackSize.clear();
        txtDiscount.clear();
        txtQtyOnHand.clear();
        txtOrderQty.clear();

    }

    //when an item is selected from the table
    private void loadItemDataToFields(OrderListTM itemSelected) throws SQLException, ClassNotFoundException {

        btnEditOrderItem.setDisable(false);
        btnRemoveItem.setDisable(false);

        txtItemCode.setText(itemSelected.getItemCode());
        txtDescription.setText(itemSelected.getDescription());
        txtPackSize.setText(itemSelected.getPackSize());
        txtUnitPrice.setText(String.valueOf(itemSelected.getUnitPrice()));
        txtOrderQty.setText(String.valueOf(itemSelected.getOrderQTY()));
        txtQtyOnHand.setText(new ItemController().getQtyOnHand(itemSelected.getItemCode()));
        txtDiscount.setText(String.valueOf(itemSelected.getDiscount()));

        uncheckCheckBox();
    }

    ArrayList<Double> oldPaymentInfo = null;

    private void loadOldPaymentInfo(String orderSelected) throws SQLException, ClassNotFoundException {
        oldPaymentInfo = new OrderDetailController().getOldPaymentInfo(orderSelected);

        lblOrderSubtotal.setText(String.valueOf(oldPaymentInfo.get(0)));
        lblOrderTotalDiscount.setText(String.valueOf(oldPaymentInfo.get(1)));
        lblOrderCost.setText(String.valueOf(oldPaymentInfo.get(2)));
    }


   /* public void editOrderItemsOnAction(ActionEvent actionEvent) throws IOException, SQLException, ClassNotFoundException {
        System.out.println(itemSelected);
        btnConfirmEdits.setDisable(false);
        //qtyOnHand in Item table should be updated
        //OrderDetail's orderQty & discount should be updated
        //Orders table's orderCost should be updated

        String itemCode = txtItemCode.getText();
        int oldOrderQty = itemSelected.getOrderQTY();
        int oldQtyOnHand = Integer.parseInt(new ItemController().getQtyOnHand(itemSelected.getItemCode()));
        int newOrderQty = 0;
        int newQtyOnHand = 0;
        int qtyBackToStock = 0;
        int qtyToReduceFromStock = 0;

        if ((Integer.parseInt(txtOrderQty.getText())) == oldOrderQty) {
            //btnEditOrderItem.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "Please enter the OrderQTY to be updated...").show();

        } else { // if txtOrderQty is less than or greater than oldOrderQty
            newOrderQty = Integer.parseInt(txtOrderQty.getText());
            btnEditOrderItem.setDisable(false);

            if (chkBoxToStock.isSelected()) {

                if (newOrderQty < oldOrderQty) { //2<3

                    qtyBackToStock = (oldOrderQty - newOrderQty); // 3-2 = 1
                    newQtyOnHand = oldQtyOnHand + qtyBackToStock; // 10 + 1 = 11

                    if (new ItemController().editQtyOnHand(itemCode, newQtyOnHand)) { // if qtyOnHand of Item Table is updated
                        new Alert(Alert.AlertType.INFORMATION, "Returned Quantity Added To Stock Successfully.", ButtonType.OK).show();
                        //new OrderDetailController().updateDiscount(itemCode,orderIdSelected,newOrderQty,txtUnitPrice.getText());
                        updateTableAndField(itemCode, newOrderQty, newQtyOnHand);

                    } else {
                        new Alert(Alert.AlertType.WARNING, "Error Occurred.Stock couldn't update.", ButtonType.OK).show();
                    }
                } else {

                }

            } else if (chkBoxFromStock.isSelected()) {
                if (newOrderQty > oldOrderQty) {

                    qtyToReduceFromStock = newOrderQty - oldOrderQty;
                    newQtyOnHand = oldQtyOnHand - qtyToReduceFromStock;

                    if (new ItemController().editQtyOnHand(itemCode, newQtyOnHand)) {
                        new Alert(Alert.AlertType.INFORMATION, "Quantity Deducted From Stock Successfully.", ButtonType.OK).show();
                        updateTableAndField(itemCode, newOrderQty, newQtyOnHand);

                    } else {
                        new Alert(Alert.AlertType.WARNING, "Error Occurred.Stock couldn't update.", ButtonType.OK).show();
                    }
                }

            } else if (chkBoxToTrash.isSelected()) {

                double unitPrice = Double.parseDouble(txtUnitPrice.getText());
                int packSize =  Integer.parseInt(txtPackSize.getText().split(" ")[0]);
                int discountPerUnit = Integer.parseInt(new DiscountController().getDiscount(itemCode));

                if (new OrderDetailController().updateOrderQty(orderIdSelected, itemCode, newOrderQty, unitPrice, packSize,discountPerUnit)) {
                    new Alert(Alert.AlertType.INFORMATION, "Quantity AddedTo Trash Successfully.", ButtonType.OK).show();

                    // setItemDetailsToTable(orderIdSelected);
                    clearFields();
                    listOfOrderedItems = new OrderDetailController().getOrderedItems(orderIdSelected, itemSelected);
                    tblManageOrder.setItems(FXCollections.observableArrayList(listOfOrderedItems));

                } else {
                    new Alert(Alert.AlertType.WARNING, "Error : Order Detail didn't saved successfully.", ButtonType.OK).show();
                }

            } else {
                new Alert(Alert.AlertType.WARNING, "Please choose the Transfer Mode of the Update Order", ButtonType.OK).show();
            }
        }
        double oldOrderCost = Double.parseDouble(lblOrderCost.getText());
        double newOrderCost = 0;
        double newOrderDiscount = 0;
        for (OrderListTM otm : listOfOrderedItems) {
            newOrderCost += otm.getTotal();
            newOrderDiscount += otm.getDiscount();
        }
        loadNewPaymentInfo(newOrderCost, oldOrderCost);
        System.out.println(itemSelected);
    }*/  // undo

//---------------------------------------------------------------------------------------------------------------------------------------------

    int oldOrderQty = 0; //orderQty before editing
    int newOrderQty = 0; // new orderQty requested
    double discount = 0;

   public void editOrderItemsOnAction(ActionEvent actionEvent) throws IOException, SQLException, ClassNotFoundException {
       btnConfirmEdits.setDisable(false);
       oldOrderQty = itemSelected.getOrderQTY();

       if ((Integer.parseInt(txtOrderQty.getText())) == oldOrderQty) {
           //btnEditOrderItem.setDisable(true);
           new Alert(Alert.AlertType.INFORMATION, "Please enter the OrderQTY to be updated...").show();
           return;
       }

        /*if ( chkBoxFromStock.isSelected() || chkBoxToStock.isSelected() || chkBoxToTrash.isSelected()) {
            new Alert(Alert.AlertType.WARNING, "Please choose the Transfer Status of the Updated Order", ButtonType.OK).show();
            return;
        }*/

       if (txtOrderQty.getText().equals("")) {
           new Alert(Alert.AlertType.INFORMATION, "No changes have been done to update...", ButtonType.OK).show();
           return;
       } else {

           int packSize = new PlaceCustomerOrderFormController().splitPackSize(txtItemCode.getText(), txtPackSize.getText());
           int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
           newOrderQty = Integer.parseInt(txtOrderQty.getText());  // new orderQty
           //int tempUpdatedOrderQty = oldOrderQty + oldOrderQty;
           int discountPerUnit = Integer.parseInt(new DiscountController().getDiscount(txtItemCode.getText()));
           double unitPrice = Double.parseDouble(txtUnitPrice.getText());
           double subTotal = unitPrice * packSize * newOrderQty;
           discount = subTotal * discountPerUnit / 100;
           double total = subTotal - discount;

           OrderListTM tm = new OrderListTM( // Item details to be updated in the table (only in the table not in database)
                   txtItemCode.getText(),
                   txtDescription.getText(),
                   txtPackSize.getText(),
                   unitPrice,
                   newOrderQty,
                   subTotal,
                   discount,
                   total
           );


           int rowNumber = isExists(tm);

           if (itemSelected == null) {
               // orderList.add(tm);
               //

           } else { // updates the existing record
               OrderListTM tmItemForUpdate = listOfOrderedItems.get(rowNumber); // get the OrderListTM object / element in this particular rowNumber

               if (newOrderQty < oldOrderQty) {
                   OrderListTM tmUpdatedItem = new OrderListTM(
                           tmItemForUpdate.getItemCode(),
                           tmItemForUpdate.getDescription(),
                           tmItemForUpdate.getPackSize(),
                           unitPrice,
                           tmItemForUpdate.getOrderQTY() - (tmItemForUpdate.getOrderQTY() - newOrderQty), // new + old
                           tmItemForUpdate.getSubTotal()- (tmItemForUpdate.getSubTotal() - subTotal),
                           tmItemForUpdate.getDiscount() - (tmItemForUpdate.getDiscount() - discount),
                           tmItemForUpdate.getTotal() - (tmItemForUpdate.getTotal() - total)
                   );

                   listOfOrderedItems.remove(rowNumber);
                   listOfOrderedItems.add(tmUpdatedItem);

               } else if (newOrderQty > oldOrderQty) {
                   OrderListTM tmUpdatedItem = new OrderListTM(
                           tmItemForUpdate.getItemCode(),
                           tmItemForUpdate.getDescription(),
                           tmItemForUpdate.getPackSize(),
                           unitPrice,
                           tmItemForUpdate.getOrderQTY() + (newOrderQty - tmItemForUpdate.getOrderQTY()) , // new + old
                           //subTotal + tmItemForUpdate.getSubTotal(),
                           subTotal,
                           //discount + tmItemForUpdate.getDiscount(),
                           discount,
                           //total + tmItemForUpdate.getTotal()
                           total
                   );

                   listOfOrderedItems.remove(rowNumber);
                   listOfOrderedItems.add(tmUpdatedItem);
               }
           }
       }
       lblModifiedDate.setText(String.valueOf(lblDate.getText()));
       lblModifiedTime.setText(String.valueOf(lblTime.getText()));

       tblManageOrder.setItems(FXCollections.observableArrayList(listOfOrderedItems));
       calculateNewOrderTotalCost();
   }

    double oldOrderCost = 0;
    double newOrderCost = 0;

    public void calculateNewOrderTotalCost() {
        oldOrderCost = Double.parseDouble(lblOrderCost.getText());

        for (OrderListTM otm : listOfOrderedItems) {
            newOrderCost += otm.getTotal();
        }
        lblOrderNewCost.setText(String.valueOf(newOrderCost));

        if (newOrderCost > oldOrderCost) {

            double amountToPay = newOrderCost - oldOrderCost;
            String toPay = String.format("%.1f",amountToPay);
            lblAmountToPay.setText(toPay);

        } else if (newOrderCost < oldOrderCost) {

            double refund = oldOrderCost - newOrderCost;
            String toRefund = String.format("%.1f",refund);
            lblRefund.setText(toRefund);
        }
    }

    private int isExists(OrderListTM tm) {
        for (int i = 0 ; i < listOfOrderedItems.size(); i++ ) {
            if (tm.getItemCode().equals(listOfOrderedItems.get(i).getItemCode())) {
                return i; // return the row number if Item to be updated
            }
        }
        return -1;
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------

    private void loadNewPaymentInfo(double newOrderCost, double oldOrderCost) throws SQLException, ClassNotFoundException {
        lblOrderNewCost.setText(String.valueOf(newOrderCost));

        if (newOrderCost > oldOrderCost) {

            double amountToPay = newOrderCost - oldOrderCost;
            String toPay = String.format("%.1f",amountToPay);
            lblAmountToPay.setText(toPay);

        } else if (newOrderCost < oldOrderCost) {

            double refund = oldOrderCost - newOrderCost;
            String toRefund = String.format("%.1f",refund);
            lblRefund.setText(toRefund);
        }
    }

    private void updateTableAndField(String itemCode, int newOrderQty, int newQtyOnHand) throws SQLException, ClassNotFoundException {
        txtQtyOnHand.setText(String.valueOf(newQtyOnHand));

        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int packSize =  Integer.parseInt(txtPackSize.getText().split(" ")[0]);
        int discountPerUnit = Integer.parseInt(new DiscountController().getDiscount(itemCode));

        if (new OrderDetailController().updateOrderQty(orderIdSelected, itemCode, newOrderQty, unitPrice, packSize,discountPerUnit)) {
            //setItemDetailsToTable(orderIdSelected);
            clearFields();
            listOfOrderedItems = new OrderDetailController().getOrderedItems(orderIdSelected, itemSelected);
            tblManageOrder.setItems(FXCollections.observableArrayList(listOfOrderedItems));

        } else {
           // System.out.println("-------_Error-------");
        }
    }

    int rowSelectedForDelete = -1;

    public void removeItemFromOrderOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        double currentOrderCost = Double.parseDouble(lblOrderNewCost.getText());
        double costAfterRemove = 0;

        if (itemSelected == null) {
            new Alert(Alert.AlertType.WARNING, "Please Select a Row to Remove").show();
        } else {

            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this Item?", yes, no);
            alert.setTitle("Confirmation Alert");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.orElse(no) == yes) {

                int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText()); //61
                int qtyToBeRemoved = Integer.parseInt(txtOrderQty.getText()); //5
                int qtyToRestock = qtyOnHand + qtyToBeRemoved;

                if (new OrderDetailController().removeItem(txtItemCode.getText(),orderIdSelected)) {
                    if (new ItemController().editQtyOnHand(itemSelected.getItemCode(),qtyToRestock)) {

                        costAfterRemove = currentOrderCost - itemSelected.getTotal();
                        listOfOrderedItems.remove(itemSelected);
                        tblManageOrder.setItems(FXCollections.observableArrayList(listOfOrderedItems));
                        tblManageOrder.refresh();


                        clearNewPaymentInfo();
                        clearOldPaymentInfo();

                        if (tblManageOrder.getItems().isEmpty()) {
                            new OrderController().deleteOrder(orderIdSelected);
                            orderListView.getItems().clear();
                        }

                        new Alert(Alert.AlertType.CONFIRMATION, "Item Deleted Successfully", ButtonType.OK).show();
                        clearFields();

                    }

                } else {
                    new Alert(Alert.AlertType.WARNING, "Try Again...").show();
                }

            } else {

            }
        }
    }


    /*public void confirmEditsOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        lblModifiedDate.setText(String.valueOf(lblDate.getText()));
        lblModifiedTime.setText(String.valueOf(lblTime.getText()));

        double orderNewCost = Double.parseDouble(lblOrderNewCost.getText());

        if (new OrderController().updateOrderCost(orderIdSelected, orderNewCost)) {
            //new Alert(Alert.AlertType.INFORMATION, "Order Updated Successfully...").show();
        } else {
            new Alert(Alert.AlertType.WARNING, "Order Update Unsuccessful...").show();
            return;
        }

        clearNewPaymentInfo();
        clearFields();
        clearOldPaymentInfo();
        uncheckCheckBox();
        clearDateAndTime();

        orderListView.getItems().clear();
        tblManageOrder.getItems().clear();

        new Alert(Alert.AlertType.CONFIRMATION, "Order Updated Successfully",ButtonType.OK).show();
        clearFields();
    }*/ //undo



//---------------------------------------------------------------------------------------------------------------------------------------------
    public void confirmEditsOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException { //according to the ConfirmOrderOnAction of PlaceCustomerOrderFormController

        // To update the Orders table
        Orders orderToBeUpdated = new Orders(
                orderIdSelected,
                java.sql.Date.valueOf(lblDate.getText()),
                lblCustomerID.getText(),
                Double.parseDouble(lblOrderNewCost.getText())
        );

        // details to update OrderDetail table - should update only the details of the updated Item --> itemSelected
        ArrayList<OrderDetail> items = new ArrayList<>();
        // int newOrderQty = 0;

        double updatedDiscount = 0;
        for (OrderListTM otm : listOfOrderedItems) {
            if (itemSelected.getItemCode().equals(otm.getItemCode())) {
                newOrderQty = otm.getOrderQTY();
                updatedDiscount = otm.getDiscount();
            }
           /* items.add(new OrderDetail(
                    orderIdSelected,
                    otm.getItemCode(),
                    otm.getOrderQTY(),
                    otm.getDiscount()
                    //Double.parseDouble(lblOrderCost.getText())
            ));*/
        }

        System.out.println("newOrderQTy - "+newOrderQty);
        System.out.println("updatedDiscount - "+updatedDiscount);

        OrderDetail orderDetailToBeUpdated = new OrderDetail(
                orderIdSelected,
                itemSelected.getItemCode(),
                newOrderQty,
                updatedDiscount
        );

        String itemCode = itemSelected.getItemCode();
        //int newQtyOnHand = itemSelected.getOrderQTY();
        int currentQtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
        int newQtyOnHand = 0;

        if (chkBoxToStock.isSelected()) { //newOrderQty < oldOrderQty
            newQtyOnHand = currentQtyOnHand + (oldOrderQty - newOrderQty);
            System.out.println("itemCode, qtyOnHand and newOrderCost to be update in DB - "+itemCode+", "+newQtyOnHand+", "+lblOrderNewCost.getText());

            if (new OrderController().updateOrder(orderToBeUpdated,orderDetailToBeUpdated,newQtyOnHand)) { // return true if all 3 order table, order detail table and Item table gets updated.
                if (confirmUpdate()) {

                    new Alert(Alert.AlertType.CONFIRMATION, "Order Updated Successfully").show();
                    return;
                } else {
                    return;
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Update Failed...Try Again...").show();
            }

        } else if (chkBoxFromStock.isSelected()) { //newOrderQty > oldOrderQty

            newQtyOnHand = currentQtyOnHand - (newOrderQty - oldOrderQty);

            System.out.println("itemCode, qtyOnHand and newOrderCost to be update in DB - "+itemCode+", "+newQtyOnHand+", "+lblOrderNewCost.getText());

            if (new OrderController().updateOrder(orderToBeUpdated,orderDetailToBeUpdated,newQtyOnHand)) { // return true if all 3 order table, order detail table and Item table gets updated.
                if (confirmUpdate()) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Order Updated Successfully").show();
                    return;
                } else {
                    return;
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Update Failed...Try Again...").show();
            }

        } else if (chkBoxToTrash.isSelected()) { //newOrderQty > oldOrderQty

            newQtyOnHand = currentQtyOnHand - (newOrderQty - oldOrderQty);

            System.out.println("itemCode, qtyOnHand and newOrderCost to be update in DB - " + itemCode + ", " + newQtyOnHand + ", " + lblOrderNewCost.getText());

            if (new OrderController().updateOrderAndOrderDetail(orderToBeUpdated, orderDetailToBeUpdated)) { // return true if all 3 order table, order detail table and Item table gets updated.
                if (confirmUpdate()) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Order Updated Successfully").show();
                    return;
                } else {
                    return;
                }
                //new Alert(Alert.AlertType.CONFIRMATION, "Order Updated Successfully").show();
            } else {
                new Alert(Alert.AlertType.WARNING, "Update Failed...Try Again...").show();
            }
        }  else {
            new Alert(Alert.AlertType.WARNING, "Please choose the Transfer Mode of the Update Order", ButtonType.OK).show();
        }
        lblModifiedDate.setText(String.valueOf(lblDate.getText()));
        lblModifiedTime.setText(String.valueOf(lblTime.getText()));

    }

    private boolean confirmUpdate() {
        ButtonType yes= new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no= new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to continue Confirmation?",yes,no);
        alert.setTitle("Confirmation Alert");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(no) == yes) {
           // return true;
        } else {
            return false;
        }
        return true;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------

    private void clearDateAndTime() {
        lblModifiedDate.setText("");
        lblModifiedTime.setText("");
    }

    private void uncheckCheckBox() {
        chkBoxToTrash.setSelected(false);
        chkBoxToStock.setSelected(false);
        chkBoxFromStock.setSelected(false);
    }

    @Override
    public void goToPreviousPageOnAction(MouseEvent mouseEvent) throws IOException {
        URL resource = getClass().getResource("../view/CashierDashBoardForm.fxml");
        Parent load = FXMLLoader.load(resource);
        contextManageOrder.getChildren().clear();
        contextManageOrder.getChildren().add(load);
    }

    @Override
    public void logoutOnAction(MouseEvent mouseEvent) {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to Logout?",yes,no);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(no)==yes){
            Stage window = (Stage) contextManageOrder.getScene().getWindow();
            window.close();
        }else{

        }
    }
}
