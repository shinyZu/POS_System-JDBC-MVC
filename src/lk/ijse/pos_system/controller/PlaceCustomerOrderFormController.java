package lk.ijse.pos_system.controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import lk.ijse.pos_system.db.DBConnection;
import lk.ijse.pos_system.model.Customer;
import lk.ijse.pos_system.model.Item;
import lk.ijse.pos_system.model.OrderDetail;
import lk.ijse.pos_system.model.Orders;
import lk.ijse.pos_system.view.tm.OrderListTM;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlaceCustomerOrderFormController implements Navigator{
    public AnchorPane contextPlaceOrder;

    public Label lblOrderID;
    public Label lblDate;
    public Label lblTime;

    public ComboBox<String> cmbCustIDs;

    public TextField txtCustId;
    public TextField txtCustTitle;
    public TextField txtCustName;
    public TextField txtCustAddress;
    public TextField txtCustCity;
    public TextField txtCustProvince;
    public TextField txtCustPostalCode;
    public JFXButton btnAddNewCustomer;
    public JFXButton btnClearFields;

    public ComboBox<String> cmbItemCode;
    public ComboBox<String> cmbItemDescription;

    public TextField txtPackSize;
    public TextField txtQtyOnHand;
    public TextField txtUnitPrice;
    public TextField txtDiscount;
    public TextField txtOrderQTY;

    public TableView<OrderListTM> tblOrderItem;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colUnitPrice;
    public TableColumn colOrderQTY;
    public TableColumn colSubTotal;
    public TableColumn colDiscount;
    public TableColumn colTotal;
    
    public Label lblOrderSubTotal;
    public Label lblOrderTotalDiscount;
    public Label lblOrderCost;
    public Label lblAmountPaid;
    public Label lblBalance;
    public Label lblPaymentDate;
    public Label lblPaymentTime;

    public TextField txtPayment;
    public Button btnPayNow;
    public JFXButton btnAddToList;

    public JFXButton btnConfirmOrder;
    public JFXButton btnCancelOrder;

    String custIdForSearch = null;
    OrderListTM itemSelected = null;

    public void initialize() {
        loadDateAndTime();
        try {

            setOrderId();
            loadCustomerIds();
            loadItemCodes();
            loadItemDescriptions();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        cmbCustIDs.getSelectionModel().selectedItemProperty().
                addListener((observable, oldValue, newValue) -> {
                    custIdForSearch = newValue;
                });

        cmbItemCode.getSelectionModel().selectedItemProperty().
                addListener((observable, oldValue, newValue) -> {
                    try {
                        setItemDataOnCode(newValue);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
        });

        cmbItemDescription.getSelectionModel().selectedItemProperty().
                addListener((observable, oldValue, newValue) -> {
                    try {
                        setItemDataOnDescription(newValue);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });

        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colOrderQTY.setCellValueFactory(new PropertyValueFactory<>("orderQTY"));
        colSubTotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        btnConfirmOrder.setDisable(true);

        tblOrderItem.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == null) {
                //
            } else {
                itemSelected = newValue;
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

    private void setOrderId() throws SQLException, ClassNotFoundException {
        lblOrderID.setText(new OrderController().getOrderId());
    }

    List<String> customerIdList;
    private void loadCustomerIds() throws SQLException, ClassNotFoundException { //load custIDs to combo box
        customerIdList = new CustomerController().getCustomerIds();
        cmbCustIDs.getItems().clear();
        cmbCustIDs.getItems().addAll(customerIdList);
    }

    List<String> itemCodeList = null;
    private void loadItemCodes() throws SQLException, ClassNotFoundException {
        itemCodeList = new ItemController().getItemCodes();
        cmbItemCode.getItems().addAll(itemCodeList);
    }

    List<String> descriptionList = null;
    private void loadItemDescriptions() throws SQLException, ClassNotFoundException {
        descriptionList = new ItemController().getItemDescriptions();
        cmbItemDescription.getItems().addAll(descriptionList);
    }

    private void setItemDataOnCode(String itemCode) throws SQLException, ClassNotFoundException { //load item details to fields when itemCode is selected
        Item item = new ItemController().getItem(itemCode);
        if (item == null) {
            new Alert(Alert.AlertType.WARNING,"Empty Result Set");
        } else {
            cmbItemDescription.setValue(item.getDescription());
            txtPackSize.setText(item.getPackSize());
            txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
            txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));

            String discount = new DiscountController().getDiscount(itemCode);
            txtDiscount.setText(discount);
        }
    }

    private void setItemDataOnDescription(String description) throws SQLException, ClassNotFoundException {
        String itemCode = new ItemController().getDescription(description);

        Item item = new ItemController().getItem(itemCode);
        if (item == null) {
            new Alert(Alert.AlertType.WARNING,"Empty Result Set");
        } else {
            cmbItemCode.setValue(itemCode);
            txtPackSize.setText(item.getPackSize());
            txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
            txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));

            String discount = new DiscountController().getDiscount(itemCode);
            txtDiscount.setText(discount);
        }
    }

    public void searchCustomerOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        btnAddNewCustomer.setDisable(true);

        Customer customer = new CustomerController().getCustomer(custIdForSearch);
        if (customer == null) {
            new Alert(Alert.AlertType.WARNING, "Empty Result Set");
        } else {
            txtCustId.setText(customer.getCustID());
            txtCustTitle.setText(customer.getCustTitle());
            txtCustName.setText(customer.getCustName());
            txtCustAddress.setText(customer.getCustAddress());
            txtCustCity.setText(customer.getCity());
            txtCustProvince.setText(customer.getProvince());
            txtCustPostalCode.setText(customer.getPostalCode());
        }
    }

    public void addNewCustomerOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        if (txtCustId.getText() == "" || txtCustTitle.getText() == "" || txtCustName.getText() == "" ||  txtCustAddress.getText() == ""
                ||txtCustCity.getText() == "" || txtCustProvince.getText() == "" || txtCustPostalCode.getText() == "")
        {
            new Alert(Alert.AlertType.WARNING,"Please fill all required data",ButtonType.OK).show();
        }

        Customer newCust = new Customer(
                txtCustId.getText(),
                txtCustTitle.getText(),
                txtCustName.getText(),
                txtCustAddress.getText(),
                txtCustCity.getText(),
                txtCustProvince.getText(),
                txtCustPostalCode.getText()
        );

        if (new CustomerController().addCustomer(newCust,customerIdList)) {
            new Alert(Alert.AlertType.CONFIRMATION, "Customer Added Successfully...").show();

            cmbCustIDs.getItems().clear();
            loadCustomerIds();

            clearCustomerFields();

        } else {
            new Alert(Alert.AlertType.WARNING, "Duplicate Customer ID").show();
        }
    }

    public void enableBtnAddNewCustomerOnClick(MouseEvent mouseEvent) {
        btnAddNewCustomer.setDisable(false);
    }

    public void clearFieldsOnAction(ActionEvent actionEvent) {
        clearCustomerFields();
    }

    private void clearCustomerFields() {
        cmbCustIDs.getSelectionModel().clearSelection();
        cmbCustIDs.setPromptText("Customer ID");

        txtCustId.clear();
        txtCustTitle.clear();
        txtCustName.clear();
        txtCustAddress.clear();
        txtCustCity.clear();
        txtCustProvince.clear();
        txtCustPostalCode.clear();

        btnAddNewCustomer.setDisable(false);
    }

    private void clearItemFields() {

        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.setPromptText("Item Code");

        cmbItemDescription.getSelectionModel().clearSelection();
        cmbItemDescription.setPromptText("Description");

        txtPackSize.clear();
        txtQtyOnHand.clear();
        txtOrderQTY.clear();
        txtUnitPrice.clear();
        txtDiscount.clear();
    }

    ObservableList<OrderListTM> orderList = FXCollections.observableArrayList();

    public void addToListOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        if (txtOrderQTY.getText().equals("")) {
            new Alert(Alert.AlertType.INFORMATION, "Please enter Order Quantity", ButtonType.OK).show();
            return;
        }

        btnAddToList.setDisable(false);

        int packSize = splitPackSize(cmbItemCode.getValue(),txtPackSize.getText());
        int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
        int orderQty = Integer.parseInt(txtOrderQTY.getText());
        int discountPerUnit = Integer.parseInt(txtDiscount.getText());
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        double subTotal = unitPrice * packSize * orderQty;
        double discount = subTotal * discountPerUnit / 100;
        double total = subTotal - discount;

        if ( qtyOnHand < orderQty ) {
            new Alert(Alert.AlertType.WARNING,"Invalid Order QTY...").show();
            return;
        }

        OrderListTM tm = new OrderListTM(
                cmbItemCode.getValue(),
                cmbItemDescription.getValue(),
                txtPackSize.getText(),
                unitPrice,
                orderQty,
                subTotal,
                discount,
                total
        );

        int rowNumber = isExists(tm);

        if (rowNumber == -1) { // adds a new record
            orderList.add(tm);

        } else { // updates the existing record

            OrderListTM tmItemForUpdate = orderList.get(rowNumber); // get the OrderListTM object / element in this particular rowNumber
            OrderListTM tmUpdatedItem = new OrderListTM(
                    tmItemForUpdate.getItemCode(),
                    tmItemForUpdate.getDescription(),
                    tmItemForUpdate.getPackSize(),
                    unitPrice,
                     orderQty + tmItemForUpdate.getOrderQTY(), // new + old
                    subTotal + tmItemForUpdate.getSubTotal(),
                    discount +tmItemForUpdate.getDiscount(),
                    total + tmItemForUpdate.getTotal()
            );

            orderList.remove(rowNumber);
            orderList.add(tmUpdatedItem);
        }
        tblOrderItem.setItems(orderList);
        btnConfirmOrder.setDisable(false);
        calculateOrderTotalCost();
        clearItemFields();
    }

    public void calculateOrderTotalCost() {
        double orderSubTotal = 0;
        double orderDiscount = 0;
        double orderCost = 0;

        for (OrderListTM otm : orderList) {
            orderSubTotal += otm.getSubTotal();
            orderDiscount += otm.getDiscount();
            orderCost += otm.getTotal();
        }

        lblOrderSubTotal.setText(orderSubTotal +"");
        lblOrderTotalDiscount.setText(orderDiscount + "");
        lblOrderCost.setText(orderCost + "");
        lblAmountPaid.setText("0.00");
        lblBalance.setText("0.00");
    }

    public int splitPackSize(String itemCode, String txtPackSize) throws SQLException, ClassNotFoundException {
         PreparedStatement stm  = DBConnection.getInstance().getConnection().
                prepareStatement("SELECT packSize FROM Item WHERE itemCode = ? ");

         stm.setObject(1,itemCode);

         ResultSet rst = stm.executeQuery();

        int tempPckSize;
        if (rst.next()) {
            tempPckSize = Integer.parseInt(rst.getString(1).split(" ")[0]);
            return tempPckSize;
        }
        return 0;
    }

    private int isExists(OrderListTM tm) { // checks whether already there has been placed an order from the given itemCode by the particular customer
        for (int i = 0 ; i < orderList.size(); i++ ) {
            if (tm.getItemCode().equals(orderList.get(i).getItemCode())) {
                return i;
            }
        }
        return -1;
    }

    public void payOrderOnAction(ActionEvent actionEvent) {
        if (txtPayment.getText().equals("")) {
            new Alert(Alert.AlertType.INFORMATION,"Please do the Payment to Place the Order... ",ButtonType.OK).show();
            return;
        }

        double payment = Double.parseDouble(txtPayment.getText());
        double balance = payment - Double.parseDouble(lblOrderCost.getText());

        if (payment < Double.parseDouble(lblOrderCost.getText())) {
            new Alert(Alert.AlertType.WARNING, "Insufficient Amount.\nOrder Cost is " + lblOrderCost.getText(), ButtonType.OK).show();
            return;
        }

        String balanceFormatted = String.format("%.1f",balance);
        lblAmountPaid.setText(String.valueOf(payment));
        //lblBalance.setText(String.format("%.1f",balance));
        lblBalance.setText(balanceFormatted);
        lblPaymentDate.setText(lblDate.getText());
        lblPaymentTime.setText(lblTime.getText());

        if (!lblAmountPaid.getText().equals("0.00")) {

            new Alert(Alert.AlertType.INFORMATION, "Payment Successful.\nYour Balance is Rs " + balanceFormatted, ButtonType.OK).show();
            txtPayment.clear();

        } else {
            new Alert(Alert.AlertType.WARNING, "Payment Unsuccessful.\nPlease Insert the Amount to make the Payment.", ButtonType.OK).show();

        }
    }

    public void editItemOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Item item = new ItemController().getItem(itemSelected.getItemCode());

        cmbItemCode.setValue(itemSelected.getItemCode());
        cmbItemDescription.setValue(itemSelected.getDescription());
        txtPackSize.setText(itemSelected.getPackSize());
        txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
        txtOrderQTY.setText(String.valueOf(itemSelected.getOrderQTY()));
        txtUnitPrice.setText(String.valueOf(itemSelected.getUnitPrice()));
        txtDiscount.setText(new DiscountController().getDiscount(itemSelected.getItemCode()));
    }

    public void removeItemFromListOnAction(ActionEvent actionEvent) {
        orderList.remove(itemSelected);
        tblOrderItem.setItems(orderList);
        tblOrderItem.refresh();

        calculateOrderTotalCost();
        clearItemFields();
        clearPaymentInfo();
    }

    public void confirmOrderOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        //--selected Customer to place the Order
        Customer placeOrderCustomer = new Customer(
                txtCustId.getText(),
                txtCustTitle.getText(),
                txtCustName.getText(),
                txtCustAddress.getText(),
                txtCustCity.getText(),
                txtCustProvince.getText(),
                txtCustPostalCode.getText()
        );

        Orders newOrder = new Orders(
                lblOrderID.getText(),
                java.sql.Date.valueOf(lblDate.getText()),
                txtCustId.getText(),
                Double.parseDouble(lblOrderCost.getText())
        );

        ArrayList<OrderDetail> items = new ArrayList<>();

        for (OrderListTM otm : orderList) {
            items.add(new OrderDetail(
                    lblOrderID.getText(),
                    otm.getItemCode(),
                    otm.getOrderQTY(),
                    otm.getDiscount()
                    //Double.parseDouble(lblOrderCost.getText())
            ));
        }

        ButtonType yes= new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no= new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        if (lblAmountPaid.getText().equals("0.00")){
            new Alert(Alert.AlertType.WARNING,"No Payment done.\nPlease make the Payment to Place Order.",ButtonType.OK).show();

        } else if (txtCustId.getText().equals("")) {

            //Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Want to Continue Without a Customer?\nIf 'NO' please select a Customer",yes,no);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"No Customer Selected.\nDo you want to continue Confirmation?",yes,no);
            alert.setTitle("Confirmation Alert");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.orElse(no) == yes) {

                Customer custNoDetails = new Customer();
                placeOrderWithoutCustomer(custNoDetails,newOrder,items);

            } else {
                //placeOrderWithCustomer(placeOrderCustomer,newOrder,items);
            }
        } else if (!txtCustId.getText().equals("")) {
            placeOrderWithCustomer(placeOrderCustomer,newOrder,items);
        }
    }

    private void placeOrderWithCustomer(Customer placeOrderCustomer, Orders newOrder, ArrayList<OrderDetail> items) throws SQLException, ClassNotFoundException {
        if (!lblAmountPaid.getText().equals("0.00")) {
            if (new OrderController().placeOrder(placeOrderCustomer, newOrder, items)) { // return true if both order table and order detail table gets updated.
                new Alert(Alert.AlertType.CONFIRMATION, "Order Confirmation Successful").show();
                setOrderId();
                clearCustomerFields();
                clearItemFields();
                clearTable();
                clearPaymentInfo();

            } else {
                new Alert(Alert.AlertType.WARNING, "Error Occurred During Order Confirmation.\nTry Again...").show();
            }
        } else if (lblAmountPaid.getText().equals("0.00")){
            new Alert(Alert.AlertType.WARNING,"No Payment done.\nPlease make the Payment to Place Order.",ButtonType.OK).show();
        }

    }

    private void placeOrderWithoutCustomer(Customer custNoDetails, Orders newOrder, ArrayList<OrderDetail> items) throws SQLException, ClassNotFoundException {

        String custID =  new CustomerController().generateCustomerID();
        custNoDetails.setCustID(custID);

        new CustomerController().addCustomer(custNoDetails,customerIdList);
        loadCustomerIds();

        if (!lblAmountPaid.getText().equals("0.00")) {
            if (new OrderController().placeOrder(custNoDetails, newOrder, items)) { // return true if both order table and order detail table gets updated.
                new Alert(Alert.AlertType.CONFIRMATION, "Order Confirmation Successful").show();
                setOrderId();
                clearCustomerFields();
                clearItemFields();
                clearTable();
                clearPaymentInfo();

            } else {
                new Alert(Alert.AlertType.WARNING, "Error Occurred During Order Confirmation.\nTry Again...").show();
            }
        } else if (lblAmountPaid.getText().equals("0.00")){
            new Alert(Alert.AlertType.WARNING,"No Payment done.\nPlease make the Payment to Place Order.",ButtonType.OK).show();
        }
    }

    public void clearTable() {
        tblOrderItem.getItems().clear();
        tblOrderItem.refresh();
    }

    private void clearPaymentInfo() {
        if (orderList.isEmpty()) {
            btnConfirmOrder.setDisable(true);

            lblOrderSubTotal.setText("0.00");
            lblOrderTotalDiscount.setText("0.00");
            lblOrderCost.setText("0.00");

            lblAmountPaid.setText("0.00");
            lblBalance.setText("0.00");

            lblPaymentDate.setText("");
            lblPaymentTime.setText("");
        }
    }

    public void cancelOrderOnAction(ActionEvent actionEvent) {
        ButtonType yes= new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no= new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to Cancel this Order?",yes,no);
        alert.setTitle("Confirmation Alert");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(no) == yes) {
            clearCustomerFields();
            clearItemFields();
            clearTable();
            clearPaymentInfo();

        } else {
            //
        }
    }

    @Override
    public void goToPreviousPageOnAction(MouseEvent mouseEvent) throws IOException {
        URL resource = getClass().getResource("../view/CashierDashBoardForm.fxml");
        Parent load = FXMLLoader.load(resource);
        contextPlaceOrder.getChildren().clear();
        contextPlaceOrder.getChildren().add(load);
    }

    @Override
    public void logoutOnAction(MouseEvent mouseEvent) throws IOException {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to Logout?",yes,no);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(no)==yes){
            Stage window = (Stage) contextPlaceOrder.getScene().getWindow();
            window.close();
        }else{

        }
    }

}
