public class Airline {
    private String name;
    private Flight[] flights;
    private int numOfFlights;

    public Airline(String name, int maxFlights) {
        this.name = name;
        this.flights = new Flight[maxFlights];
        this.numOfFlights = 0;
    }

    public boolean addFlight(Flight f) { // add method
        if (searchFlight(f.getFlightNumber()) != -1 || numOfFlights == flights.length)
            return false;
        flights[numOfFlights++] = new Flight(f);
        return true;
    }

    public boolean removeFlight(String flightNumber) { // remove method
        int index = searchFlight(flightNumber);
        if (index == -1)
            return false;
        flights[index] = flights[numOfFlights - 1];
        flights[numOfFlights - 1] = null;
        numOfFlights--;
        return true;
    }

    public int searchFlight(String flightNumber) { // search method
        for (int i = 0; i < numOfFlights; i++)
            if (flights[i].getFlightNumber().equals(flightNumber))
                return i;
        return -1;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Airline: ").append(name).append("\n");
        str.append("Flights:\n");
        for (int i = 0; i < numOfFlights; i++) {
            str.append(flights[i].toString()).append("\n");
        }
        return str.toString();
    }

}