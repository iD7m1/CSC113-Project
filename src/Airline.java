import java.io.Serializable;

public class Airline implements Serializable {
    private String name;
    private int maxFlights;
    private List<Flight> flights;

    public Airline(String name, int maxFlights) {
        this.name = name;
        this.maxFlights = maxFlights;
        this.flights = new List<Flight>();
    }

    public boolean addFlight(Flight f) { // add method
        if (searchFlight(f.getFlightNumber()) != -1 || flights.size() == maxFlights)
            return false;
        Flight flight = new Flight(f); // composition relation
        flight.setAirline(this); // Set the airline reference in the flight
        flights.insertAtBack(flight);
        return true;
    }

    public boolean removeFlight(String flightNumber) { // remove method
        int index = searchFlight(flightNumber);
        if (index == -1)
            return false;
        flights.remove(index);
        return true;
    }

    public int searchFlight(String flightNumber) { // search method
        for (int i = 0; i < flights.size(); i++)
            if (flights.get(i).getFlightNumber().equals(flightNumber))
                return i;
        return -1;
    }

    public Flight getFlight(String flightNumber) {
        int index = searchFlight(flightNumber);
        if (index != -1)
            return flights.get(index);
        return null;
    }

    public String getName() {
        return name;
    }

    public int getFlightCount() {
        return flights.size();
    }

    public Flight getFlight(int index) {
        return flights.get(index);
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Airline: ").append(name).append("\n");
        str.append("Flights:");
        for (int i = 0; i < flights.size(); i++) {
            str.append("\n    ").append(flights.get(i).toString()).append("\n");
        }
        if (flights.size() == 0)
            str.append("\n    No flights available.");
        return str.toString();
    }

}