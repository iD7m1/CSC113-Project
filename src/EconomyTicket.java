public class EconomyTicket extends Ticket {
    protected boolean mealIncluded;

    public EconomyTicket(String passengerName, String seatNumber, boolean mealIncluded) {
        super(passengerName, seatNumber);
        this.mealIncluded = mealIncluded;
        this.basePrice = 200.0 + (mealIncluded ? 20.0 : 0.0); // Set a base price for economy tickets
    }

    public double getBaggageAllowance() {
        return 20.0;
    }

    public double calculateRefundAmount() {
        if (isRefundable()) {
            return basePrice * 0.5; // 50% refund for economy tickets
        }
        return 0.0;
    }

    public boolean isRefundable() {
        return true;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Economy Ticket\n");
        str.append(super.toString());
        str.append("Meal Included: ").append(mealIncluded ? "Yes" : "No").append("\n");
        return str.toString();
    }
}
