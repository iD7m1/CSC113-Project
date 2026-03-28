public class Flight {
    private String flightNumber;
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private String status;
    private Ticket[] tickets;
    private int numOfTickets;

    public Flight(String flightNumber, String origin, String destination, String departureTime, String arrivalTime) { // constructor
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = "Scheduled";
        this.tickets = new Ticket[100]; // Assuming a maximum of 100 tickets per flight
        this.numOfTickets = 0;
    }

    public Flight(Flight t) { // constructor to the Composition relation
        this.flightNumber = t.flightNumber;
        this.origin = t.origin;
        this.destination = t.destination;
        this.departureTime = t.departureTime;
        this.arrivalTime = t.arrivalTime;
        this.status = "Scheduled";
        this.tickets = new Ticket[100];
        for (int i = 0; i < t.numOfTickets; i++)
            this.tickets[i] = t.tickets[i];
        this.numOfTickets = t.numOfTickets;
    }

    public String getFlightNumber() { // getter
        return flightNumber;
    }

    public boolean addTicket(Ticket t) {
        if (numOfTickets == tickets.length || searchTicket(t) != -1)
            return false;
        tickets[numOfTickets++] = t;
        return true;
    }

    public boolean removeTicket(Ticket t) {
        int removedTicket = searchTicket(t);
        if (removedTicket == -1)
            return false;
        tickets[removedTicket] = tickets[numOfTickets - 1];
        tickets[numOfTickets - 1] = null;
        numOfTickets--;
        return true;
    }

    public int searchTicket(Ticket id) {
        for (int i = 0; i < numOfTickets; i++)
            if (tickets[i].getTicketId().equals(id.getTicketId()))
                return i;
        return -1;
    }

    public String toString() {
        String str = "flightNumber :" + flightNumber + " origin :" + origin + " destination :" + destination
                + " departureTime :" + departureTime + " arrivalTime :" + arrivalTime + " status:" + status;
        for (int i = 0; i < numOfTickets; i++) {
            str += (i + 1) + tickets[i].toString() + "\n";
        }
        return str;
    }
}
