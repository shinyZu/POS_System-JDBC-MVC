package lk.ijse.pos_system.controller;

import com.jfoenix.controls.JFXDatePicker;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.pos_system.view.tm.CustomerWiseIncomeReportTM;
import lk.ijse.pos_system.view.tm.ReportTM;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class SystemReportFormController implements Navigator{
    public AnchorPane contextReports;

    public Label lblMostMovableItem;
    public Label lblLeastMovableItem;
    public Label lblDate;
    public Label lblTime;

    public JFXDatePicker datePicker;

    public TableView<ReportTM> tblDailyReport;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colSalesQuantity;
    public TableColumn colIncome;

    public AnchorPane contextDailyReport;

    public TableView<CustomerWiseIncomeReportTM> tblCustomerWiseIncome;
    public TableColumn colCustomerId;
    public TableColumn colCustomerTitle;
    public TableColumn colCustomerName;
    public TableColumn colCustomerCity;
    public TableColumn colCustomerIncome;

    LocalDate date = null;

    public void initialize() {
        loadDateAndTime();

        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colSalesQuantity.setCellValueFactory(new PropertyValueFactory<>("salesQuantity"));
        colIncome.setCellValueFactory(new PropertyValueFactory<>("income"));

        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        colCustomerTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colCustomerCity.setCellValueFactory(new PropertyValueFactory<>("customerCity"));
        colCustomerIncome.setCellValueFactory(new PropertyValueFactory<>("income"));

        try {
            loadMostMovableItem();
            loadLeastMovableItem();



        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            date = newValue;
            try {
                loadDailyReport(newValue);
                loadCustomerWiseIncomeReport(newValue);
                //loadMonthlyReport(newValue);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    ArrayList<ReportTM> tmDailyReport = new ArrayList<>();
    ArrayList<ReportTM> tmMonthlyReport = new ArrayList<>();
    ArrayList<ReportTM> tmAnnualReport = new ArrayList<>();
    ArrayList<CustomerWiseIncomeReportTM> tmCustomerWiseIncomeReport = new ArrayList<>();

    /*private void loadMonthlyReport(LocalDate newValue) {
        tmMonthlyReport = new ItemController().getMonthlyReport(date);
        tblM.setItems(FXCollections.observableArrayList(tmDailyReport));
    }*/

    private void loadDailyReport(LocalDate date) throws SQLException, ClassNotFoundException {
        tmDailyReport = new ItemController().getDailyReport(date);
        tblDailyReport.setItems(FXCollections.observableArrayList(tmDailyReport));
    }

    private void loadCustomerWiseIncomeReport(LocalDate date) throws SQLException, ClassNotFoundException {
        tmCustomerWiseIncomeReport = new CustomerController().getCustomerWiseIncome(date);
        tblCustomerWiseIncome.setItems(FXCollections.observableArrayList(tmCustomerWiseIncomeReport));
    }

    private void loadLeastMovableItem() throws SQLException, ClassNotFoundException {
        String leastMovableItem = new OrderDetailController().getLeastMovableItem();
        lblLeastMovableItem.setText(leastMovableItem);
    }

    private void loadMostMovableItem() throws SQLException, ClassNotFoundException {
        String mostMovableItem = new OrderDetailController().getMostMovableItem();
        lblMostMovableItem.setText(mostMovableItem);
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

    @Override
    public void goToPreviousPageOnAction(MouseEvent mouseEvent) throws IOException {
        URL resource = getClass().getResource("../view/AdminDashBoardForm.fxml");
        Parent load = FXMLLoader.load(resource);
        contextReports.getChildren().clear();
        contextReports.getChildren().add(load);
    }

    @Override
    public void logoutOnAction(MouseEvent mouseEvent) {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to Logout?",yes,no);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(no)==yes){
            Stage window = (Stage) contextReports.getScene().getWindow();
            window.close();
        }else{

        }
    }

}
