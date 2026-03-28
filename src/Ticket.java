public abstract class Ticket implements Refundable {
    protected String ticketId;
    protected String passengerName;
    protected String seatNumber;
    protected double basePrice;

    public Ticket(String passengerName, String seatNumber, double basePrice) {
        this.ticketId = "T" + System.currentTimeMillis(); // Generate a unique ticket ID
        this.passengerName = passengerName;
        this.seatNumber = seatNumber;
        this.basePrice = basePrice;
    }

    public String getTicketId() {
        return ticketId;
    }

    public abstract double getBaggageAllowance();
    public abstract double calculateRefundAmount();
    public abstract boolean isRefundable();
}
