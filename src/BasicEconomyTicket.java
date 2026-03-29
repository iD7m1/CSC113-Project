public class BasicEconomyTicket extends EconomyTicket {
    private boolean allowedCarryOn;

    public BasicEconomyTicket(String passengerName, String seatNumber, boolean mealIncluded, boolean allowedCarryOn) {
        super(passengerName, seatNumber, mealIncluded);
        this.allowedCarryOn = allowedCarryOn;
        this.basePrice = 150.0 + (allowedCarryOn ? 20.0 : 0.0) + (mealIncluded ? 20.0 : 0.0); // Set a base price for basic economy tickets
    }

    public double getBaggageAllowance() {
        return 10.0;
    }

    public boolean isRefundable() {
        return false; // Basic economy tickets are non-refundable
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Basic ");
        str.append(super.toString());
        str.append("Allowed Carry-On: ").append(allowedCarryOn ? "Yes" : "No").append("\n\t\t");
        return str.toString();
    }
}
