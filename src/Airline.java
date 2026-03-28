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
        if (searchFlight(f) != -1 || numOfFlights == flights.length)
            return false;
        flights[numOfFlights++] = new Flight(f);
        return true;
    }

    public boolean removeFlight(Flight f) { // remove method
        int index = searchFlight(f);
        if (index == -1)
            return false;
        flights[index] = flights[numOfFlights - 1];
        flights[numOfFlights - 1] = null;
        numOfFlights--;
        return true;
    }

    public int searchFlight(Flight id) { // searth method
        for (int i = 0; i < numOfFlights; i++)
            if (flights[i].getFlightNumber().equals(id.getFlightNumber()))
                return i;
        return -1;
    }

    public String toString() {
        String str = "Name: " + name + "\n";
        for (int i = 0; i < numOfFlights; i++)
            str += flights[i].toString();
        return str;
    }

}