package lk.ijse.pos_system.view.tm;

public class CustomerWiseIncomeReportTM {
    private String customerID;
    private String title;
    private String customerName;
    private String customerCity;
    private double income;

    public CustomerWiseIncomeReportTM() {}

    public CustomerWiseIncomeReportTM(String customerID, String title, String customerName, String customerCity, double income) {
        this.setCustomerID(customerID);
        this.setTitle(title);
        this.setCustomerName(customerName);
        this.setCustomerCity(customerCity);
        this.setIncome(income);
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    @Override
    public String toString() {
        return "CustomerWiseIncomeReportTM{" +
                "customerID='" + customerID + '\'' +
                ", title='" + title + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerCity='" + customerCity + '\'' +
                ", income=" + income +
                '}';
    }
}
