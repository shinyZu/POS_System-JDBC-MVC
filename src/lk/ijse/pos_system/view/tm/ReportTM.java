package lk.ijse.pos_system.view.tm;

public class ReportTM {
    private String itemCode;
    private String description;
    private int salesQuantity;
    private double income;

    public ReportTM() {}

    public ReportTM(String itemCode, String description, int salesQuantity, double income) {
        this.setItemCode(itemCode);
        this.setDescription(description);
        this.setSalesQuantity(salesQuantity);
        this.setIncome(income);
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSalesQuantity() {
        return salesQuantity;
    }

    public void setSalesQuantity(int salesQuantity) {
        this.salesQuantity = salesQuantity;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    @Override
    public String toString() {
        return "ReportTM{" +
                "itemCode='" + itemCode + '\'' +
                ", description='" + description + '\'' +
                ", salesQuantity=" + salesQuantity +
                ", income=" + income +
                '}';
    }
}
