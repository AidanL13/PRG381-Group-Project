/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Mzing
 */
public class StockIssuance {
 
    private int issuanceId;
    private int materialId;
    private int cleanerId;
    private int quantityIssued;
    private int issuedBy;       // user_id of the storekeeper who issued it
    private String issueDate;
    private String notes;
 
    public StockIssuance() {
    }
 
    public StockIssuance(int issuanceId, int materialId, int cleanerId, int quantityIssued,
                          int issuedBy, String issueDate, String notes) {
        this.issuanceId = issuanceId;
        this.materialId = materialId;
        this.cleanerId = cleanerId;
        this.quantityIssued = quantityIssued;
        this.issuedBy = issuedBy;
        this.issueDate = issueDate;
        this.notes = notes;
    }
 
    public int getIssuanceId() {
        return issuanceId;
    }
 
    public void setIssuanceId(int issuanceId) {
        this.issuanceId = issuanceId;
    }
 
    public int getMaterialId() {
        return materialId;
    }
 
    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }
 
    public int getCleanerId() {
        return cleanerId;
    }
 
    public void setCleanerId(int cleanerId) {
        this.cleanerId = cleanerId;
    }
 
    public int getQuantityIssued() {
        return quantityIssued;
    }
 
    public void setQuantityIssued(int quantityIssued) {
        if (quantityIssued <= 0) {
            throw new IllegalArgumentException("Quantity issued must be greater than zero.");
        }
        this.quantityIssued = quantityIssued;
    }
 
    public int getIssuedBy() {
        return issuedBy;
    }
 
    public void setIssuedBy(int issuedBy) {
        this.issuedBy = issuedBy;
    }
 
    public String getIssueDate() {
        return issueDate;
    }
 
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }
 
    public String getNotes() {
        return notes;
    }
 
    public void setNotes(String notes) {
        this.notes = notes;
    }
 
    @Override
    public String toString() {
        return "Issuance #" + issuanceId + ": " + quantityIssued + " unit(s) on " + issueDate;
    }
}
