public class FirstClassTicket extends Ticket {
    private boolean includesSpaAccess;

    public FirstClassTicket(String passengerName, String seatNumber, boolean includesSpaAccess) {
        super(passengerName, seatNumber);
        this.includesSpaAccess = includesSpaAccess;
        this.basePrice = 500.0 + (includesSpaAccess ? 100.0 : 0.0); // Set a base price for first class tickets
    }

    public double getBaggageAllowance() {
        return 50.0;
    }

    public boolean getSpaAccess() {
        return includesSpaAccess;
    }

    public double calculateRefundAmount() {
        if (isRefundable()) {
            return basePrice * 0.8; // 80% refund for first class tickets
        }
        return 0.0;
    }

    public boolean isRefundable() {
        return true;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("First Class Ticket\n\t\t");
        str.append(super.toString());
        str.append("Includes Spa Access: ").append(includesSpaAccess ? "Yes" : "No").append("\n\t\t");
        return str.toString();
    }
}
