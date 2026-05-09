import java.io.Serializable;

public class LoungePass implements Refundable, Serializable {
    private String passId;
    private String holderTicketId;
    private int accessLevel; // e.g., 1 for basic, 2 for premium
    private double price;

    public LoungePass(int accessLevel) {
        this.passId = "LP" + System.currentTimeMillis(); // Generate a unique pass ID
        this.holderTicketId = "-1"; // Default to -1, can be set when assigned to a ticket
        this.accessLevel = accessLevel;
        if (accessLevel == 1) {
            this.price = 50.0; // Price for basic lounge access
        } else if (accessLevel == 2) {
            this.price = 100.0; // Price for premium lounge access
        } else {
            this.accessLevel = 1; // Default to basic if invalid access level is provided
            this.price = 50.0;
        }
    }

    public String getPassId() {
        return passId;
    }

    public String getHolderTicketId() {
        return holderTicketId;
    }

    public void setHolderTicketId(String holderTicketId) {
        this.holderTicketId = holderTicketId;
    }

    public double getPrice() {
        return price;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public double calculateRefundAmount() {
        return price * 0.5; // 50% refund for lounge passes
    }

    public boolean isRefundable() {
        return true;
    }

}
