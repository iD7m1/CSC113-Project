import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Flight {
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String status;
    private Ticket[] tickets;
    private int numOfTickets;
    private int numOfFirstClassTickets;
    private int numOfEconomyTickets;

    public Flight(String flightNumber, String origin, String destination, LocalDateTime departureTime,
            LocalDateTime arrivalTime) { // constructor
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = "Scheduled";
        this.tickets = new Ticket[100]; // Assuming a maximum of 100 tickets per flight
        this.numOfTickets = 0;
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
        this.tickets = new Ticket[100];
        for (int i = 0; i < t.numOfTickets; i++)
            this.tickets[i] = t.tickets[i];
        this.numOfTickets = t.numOfTickets;
        this.numOfFirstClassTickets = t.numOfFirstClassTickets;
        this.numOfEconomyTickets = t.numOfEconomyTickets;
    }

    public String getFlightNumber() { // getter
        return flightNumber;
    }

    public boolean addTicket(Ticket t) {
        if (numOfTickets == tickets.length || searchTicket(t.getTicketId()) != -1
                || (t instanceof FirstClassTicket && numOfFirstClassTickets == 20)
                || (t instanceof EconomyTicket && numOfEconomyTickets == 80))
            return false;
        tickets[numOfTickets++] = t;
        if (t instanceof FirstClassTicket)
            numOfFirstClassTickets++;
        else if (t instanceof EconomyTicket)
            numOfEconomyTickets++;
        return true;
    }

    public boolean removeTicket(String id) {
        int removedTicket = searchTicket(id);
        if (removedTicket == -1)
            return false;

        if (tickets[removedTicket] instanceof FirstClassTicket)
            numOfFirstClassTickets--;
        else if (tickets[removedTicket] instanceof EconomyTicket)
            numOfEconomyTickets--;
        tickets[removedTicket] = tickets[numOfTickets - 1];
        tickets[numOfTickets - 1] = null;
        numOfTickets--;
        return true;
    }

    public int searchTicket(String id) {
        for (int i = 0; i < numOfTickets; i++) {
            if (tickets[i].getTicketId().equals(id))
                return i;
        }
        return -1;
    }

    public void depart() {
        depart(LocalDateTime.now());
    }

    public void depart(LocalDateTime dateTime) {
        if (status.equals("Scheduled"))
            status = "Departed - " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void land(LocalDateTime dateTime) {
        if (status.startsWith("In Air"))
            status = "Landed - " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void land() {
        land(LocalDateTime.now());
    }

    // recursive method to calculate the total baggage allowance for all tickets in
    // the flight
    private double totalBaggageAllowance(int index) {
        if (index == numOfTickets)
            return 0;
        return tickets[index].getBaggageAllowance() + totalBaggageAllowance(index + 1);
    }

    public double totalBaggageAllowance() {
        return totalBaggageAllowance(0);
    }

    // recursive method to calculate the total revenue for all tickets in the flight
    private double totalRevenue(int index) {
        if (index == numOfTickets)
            return 0;
        return tickets[index].basePrice + totalRevenue(index + 1);
    }

    public double totalRevenue() {
        return totalRevenue(0);
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Flight ").append(flightNumber)
                .append("\nRoute: ").append(origin).append(" -> ").append(destination)
                .append("\nDeparture: ").append(departureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("\nArrival: ").append(arrivalTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("\nStatus: ").append(status)
                .append("\nTotal Baggage Allowance: ").append(totalBaggageAllowance()).append(" kg")
                .append("\nTotal Revenue: $").append(String.format("%.2f", totalRevenue()));

        if (numOfTickets == 0) {
            str.append("\n- No tickets booked.");
        } else {
            str.append("\nTickets (").append(numOfTickets).append("):");
            for (int i = 0; i < numOfTickets; i++) {
                str.append("\n").append(i + 1).append(") ").append(tickets[i]);
            }
        }

        return str.toString();
    }
}
