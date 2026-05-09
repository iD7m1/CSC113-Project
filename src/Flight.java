import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Flight implements Serializable {
    private Airline airline;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String status;
    private double totalRevenue;
    private List<Ticket> tickets;
    private int numOfFirstClassTickets;
    private int numOfEconomyTickets;
    private static final int MAX_TICKETS = 100;

    public Flight(String flightNumber, String origin, String destination, LocalDateTime departureTime,
            LocalDateTime arrivalTime) { // constructor
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = "Scheduled";
        this.totalRevenue = 0.0;
        this.tickets = new List<Ticket>();
        this.numOfFirstClassTickets = 0;
        this.numOfEconomyTickets = 0;
    }

    public Flight(Flight t) { // constructor to the Composition relation
        this.flightNumber = t.flightNumber;
        this.origin = t.origin;
        this.destination = t.destination;
        this.departureTime = t.departureTime;
        this.arrivalTime = t.arrivalTime;
        this.status = t.status;
        this.totalRevenue = t.totalRevenue;
        this.tickets = new List<Ticket>();
        for (int i = 0; i < t.tickets.size(); i++) {
            this.tickets.insertAtBack(t.tickets.get(i));
        }
        this.numOfFirstClassTickets = t.numOfFirstClassTickets;
        this.numOfEconomyTickets = t.numOfEconomyTickets;
    }

    public String getFlightNumber() { // getter
        return flightNumber;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public Airline getAirline() {
        return airline;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public boolean addTicket(Ticket t) {
        if (tickets.size() == MAX_TICKETS || searchTicket(t.getTicketId()) != -1
                || (t instanceof FirstClassTicket && numOfFirstClassTickets == 20)
                || (t instanceof EconomyTicket && numOfEconomyTickets == 80))
            return false;
        tickets.insertAtBack(t); // aggregation relation
        if (t instanceof FirstClassTicket)
            numOfFirstClassTickets++;
        else if (t instanceof EconomyTicket)
            numOfEconomyTickets++;
        totalRevenue += t.basePrice;
        return true;
    }

    public boolean refundTicket(String id) throws InvalidTicketOperationException {
    int removedTicket = searchTicket(id);

    if (removedTicket == -1) {
        throw new InvalidTicketOperationException("Ticket not found.");
    }

    Ticket removed = tickets.get(removedTicket);

    if (!removed.isRefundable()) {
        throw new InvalidTicketOperationException("This ticket is non-refundable.");
    }

    if (removed instanceof FirstClassTicket)
        numOfFirstClassTickets--;
    else if (removed instanceof EconomyTicket)
        numOfEconomyTickets--;

    totalRevenue -= removed.calculateRefundAmount();
    tickets.remove(removedTicket);

    return true;
}

    public int searchTicket(String id) {
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getTicketId().equals(id))
                return i;
        }
        return -1;
    }

    public Ticket getTicketAt(int index) {
        return tickets.get(index);
    }

    public int getTicketCount() {
        return tickets.size();
    }

    public void depart() {
        depart(LocalDateTime.now());
    }

    public void depart(LocalDateTime dateTime) {
        if (status.equals("Scheduled"))
            status = "Departed - " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void land(LocalDateTime dateTime) {
        if (status.startsWith("Departed"))
            status = "Landed - " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void land() {
        land(LocalDateTime.now());
    }

    // recursive method to calculate the total baggage allowance for all tickets in
    // the flight
    private double totalBaggageAllowance(int index) {
        if (index == tickets.size())
            return 0;
        return tickets.get(index).getBaggageAllowance() + totalBaggageAllowance(index + 1);
    }

    public double totalBaggageAllowance() {
        return totalBaggageAllowance(0);
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Flight ").append(flightNumber).append(" (").append(airline.getName()).append(")")
                .append("\n    Route: ").append(origin).append(" -> ").append(destination)
                .append("\n    Departure: ")
                .append(departureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("\n    Arrival: ").append(arrivalTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("\n    Status: ").append(status)
                .append("\n    Total Baggage Allowance: ").append(totalBaggageAllowance()).append(" kg")
                .append("\n    Total Revenue: $").append(String.format("%.2f", totalRevenue));

        if (tickets.size() == 0) {
            str.append("\n    - No tickets booked.");
        } else {
            str.append("\n    Tickets (").append(tickets.size()).append("):");
            int ticketIndex = 1;
            for (int i = 0; i < tickets.size(); i++) {
                str.append("\n        ").append(ticketIndex++).append(") ").append(tickets.get(i));
            }
        }

        return str.toString();
    }
}
