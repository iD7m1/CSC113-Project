public abstract class Ticket implements Refundable {
    protected String ticketId;
    protected String passengerName;
    protected String seatNumber;
    protected double basePrice;
    protected LoungePass loungePass;

    public Ticket(String passengerName, String seatNumber) {
        this.ticketId = "T" + System.currentTimeMillis(); // Generate a unique ticket ID
        this.passengerName = passengerName;
        this.seatNumber = seatNumber;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public LoungePass getLoungePass() {
        return loungePass;
    }

    public void setLoungePass(LoungePass loungePass) {
        loungePass.setHolderTicketId(this.ticketId); // Link the lounge pass to this ticket
        this.loungePass = loungePass;
    }

    public abstract double getBaggageAllowance();

    public abstract double calculateRefundAmount();

    public abstract boolean isRefundable();

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Ticket ID: ").append(ticketId).append("\n");
        str.append("Passenger: ").append(passengerName).append("\n");
        str.append("Seat: ").append(seatNumber).append("\n");
        str.append("Base Price: $").append(basePrice).append("\n");

        if (loungePass != null) {
            str.append("Lounge Pass ID: ").append(loungePass.getPassId()).append("\n");
            str.append("Lounge Access Level: ").append(loungePass.getAccessLevel()).append("\n");
            str.append("Lounge Pass Price: $").append(loungePass.getPrice()).append("\n");
        } else {
            str.append("No Lounge Pass\n");
        }
        return str.toString();

    }
}
