import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class AirportTest {
    static final int MAX_AIRLINES = 20;
    static final int MAX_FLIGHTS = 200;
    static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static Airline[] airlines = new Airline[MAX_AIRLINES];
    static String[] airlineNames = new String[MAX_AIRLINES];
    static int numOfAirlines = 0;

    static Flight[] flights = new Flight[MAX_FLIGHTS];
    static int numOfFlights = 0;

    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        clearScreen();
        System.out.println("============================================");
        System.out.println("        Airport Management Console");
        System.out.println("============================================");
        pause(1000);

        boolean repeat = true;
        while (repeat) {
            System.out.println(
                    "\n---------------- Main Menu -----------------"
                            + "\n1) Create Airline"
                            + "\n2) Create Flight and Assign to Airline"
                            + "\n3) Remove Flight"
                            + "\n4) Add Ticket to Flight"
                            + "\n5) Remove Ticket from Flight"
                            + "\n6) Depart Flight (with date/time)"
                            + "\n7) Land Flight (with date/time)"
                            + "\n8) Show One Flight Details"
                            + "\n9) Show All Flights"
                            + "\n10) Show One Airline Details"
                            + "\n11) Show All Airlines"
                            + "\n0) Exit"
                            + "\n--------------------------------------------");

            int choice = readInt("Choose an option: ", 0, 11);
            switch (choice) {
                case 1:
                    createAirline();
                    break;
                case 2:
                    createFlight();
                    break;
                case 3:
                    removeFlight();
                    break;
                case 4:
                    addTicketToFlight();
                    break;
                case 5:
                    removeTicketFromFlight();
                    break;
                case 6:
                    departFlight();
                    break;
                case 7:
                    landFlight();
                    break;
                case 8:
                    showFlight();
                    break;
                case 9:
                    showAllFlights();
                    break;
                case 10:
                    showAirline();
                    break;
                case 11:
                    showAllAirlines();
                    break;
                case 0:
                    repeat = false;
                    printlnBox("Thank you for using Airport Manager.");
                    break;
                default:
                    printlnError("Invalid option. Please choose between 0 and 11.");
            }
        }

        input.close();
    }

    static void createAirline() {
        if (numOfAirlines == MAX_AIRLINES) {
            printlnError("Cannot create more airlines. Capacity reached.");
            return;
        }

        String name = readNonEmpty("Airline name: ");
        if (findAirlineIndex(name) != -1) {
            printlnError("Airline already exists.");
            return;
        }

        int maxFlights = readInt("Maximum number of flights for this airline: ", 1, Integer.MAX_VALUE);

        airlines[numOfAirlines] = new Airline(name, maxFlights);
        airlineNames[numOfAirlines] = name;
        numOfAirlines++;
        printlnSuccess("Airline created successfully.");
    }

    static void createFlight() {
        if (numOfAirlines == 0) {
            printlnError("Create an airline first.");
            return;
        }
        if (numOfFlights == MAX_FLIGHTS) {
            printlnError("Cannot create more flights. Capacity reached.");
            return;
        }

        String owner = readNonEmpty("Airline name: ");
        int airlineIndex = findAirlineIndex(owner);
        if (airlineIndex == -1) {
            printlnError("Airline not found.");
            return;
        }

        String flightNumber = readNonEmpty("Flight number: ");
        if (findFlightIndex(flightNumber) != -1) {
            printlnError("Flight number already exists.");
            return;
        }

        String origin = readNonEmpty("Origin: ");
        String destination = readNonEmpty("Destination: ");
        LocalDateTime departure = readDateTime("Planned departure (yyyy-MM-dd HH:mm:ss): ");
        LocalDateTime arrival = readDateTime("Planned arrival (yyyy-MM-dd HH:mm:ss): ");

        Flight flight = new Flight(flightNumber, origin, destination, departure, arrival);
        boolean added = airlines[airlineIndex].addFlight(flight);
        if (!added) {
            printlnError("Could not add flight to airline (duplicate or airline capacity reached).");
            return;
        }

        flights[numOfFlights] = airlines[airlineIndex].getFlight(flightNumber); // Get the actual flight object from the
                                                                                // airline
        numOfFlights++;
        printlnSuccess("Flight created and assigned successfully.");
    }

    static void removeFlight() {
        int flightIndex = chooseFlight();
        if (flightIndex < 0)
            return;

        flights[flightIndex].getAirline().removeFlight(flights[flightIndex].getFlightNumber());

        flights[flightIndex] = flights[numOfFlights - 1];
        flights[numOfFlights - 1] = null;
        numOfFlights--;

        printlnSuccess("Flight removed successfully.");
    }

    static void addTicketToFlight() {
        int flightIndex = chooseFlight();
        if (flightIndex < 0)
            return;

        Flight flight = flights[flightIndex];
        System.out.println("Ticket Types: 1) Economy  2) Basic Economy  3) First Class");
        int type = readInt("Select ticket type: ", 1, 3);

        String passenger = readNonEmpty("Passenger name: ");
        String seat = readNonEmpty("Seat number: ");
        Ticket ticket;

        if (type == 1) {
            boolean mealIncluded = readYesNo("Meal included? (y/n): ");
            ticket = new EconomyTicket(passenger, seat, mealIncluded);
        } else if (type == 2) {
            boolean mealIncluded = readYesNo("Meal included? (y/n): ");
            boolean allowedCarryOn = readYesNo("Carry-on allowed? (y/n): ");
            ticket = new BasicEconomyTicket(passenger, seat, mealIncluded, allowedCarryOn);
        } else if (type == 3) {
            boolean spa = readYesNo("Spa access included? (y/n): ");
            ticket = new FirstClassTicket(passenger, seat, spa);
        } else {
            printlnError("Invalid ticket type.");
            return;
        }

        if (readYesNo("Add lounge pass? (y/n): ")) {
            int level = readInt("Lounge access level (1 or 2): ", 1, 2);
            ticket.setLoungePass(new LoungePass(level));
        }

        boolean added = flight.addTicket(ticket);
        if (!added) {
            printlnError("Could not add ticket (duplicate, full flight, or class capacity reached).");
            return;
        }

        printlnSuccess("Ticket added successfully. Ticket ID: " + ticket.getTicketId());
    }

    static void removeTicketFromFlight() {
        int flightIndex = chooseFlight();
        if (flightIndex < 0)
            return;

        String ticketId = readNonEmpty("Ticket ID to remove: ");
        boolean removed = flights[flightIndex].removeTicket(ticketId);
        if (!removed) {
            printlnError("Ticket not found.");
            return;
        }

        printlnSuccess("Ticket removed successfully.");
    }

    static void departFlight() {
        int flightIndex = chooseFlight();
        if (flightIndex < 0)
            return;

        LocalDateTime departDate = readDateTime("Actual departure (yyyy-MM-dd HH:mm:ss): ");
        flights[flightIndex].depart(departDate);
        printlnSuccess("Flight departure status updated.");
    }

    static void landFlight() {
        int flightIndex = chooseFlight();
        if (flightIndex < 0)
            return;

        LocalDateTime landDate = readDateTime("Actual landing (yyyy-MM-dd HH:mm:ss): ");
        flights[flightIndex].land(landDate);
        printlnSuccess("Flight landing status updated.");
    }

    static void showFlight() {
        int flightIndex = chooseFlight();
        if (flightIndex < 0)
            return;

        printlnBox(flights[flightIndex].toString());
    }

    static void showAllFlights() {
        if (numOfFlights == 0) {
            printlnError("No flights available.");
            return;
        }

        for (int i = 0; i < numOfFlights; i++) {
            printlnBox(flights[i].toString());
        }
    }

    static void showAirline() {
        if (numOfAirlines == 0) {
            printlnError("No airlines available.");
            return;
        }

        String name = readNonEmpty("Airline name: ");
        int airlineIndex = findAirlineIndex(name);
        if (airlineIndex == -1) {
            printlnError("Airline not found.");
            return;
        }

        printlnBox(airlines[airlineIndex].toString());
    }

    static void showAllAirlines() {
        if (numOfAirlines == 0) {
            printlnError("No airlines available.");
            return;
        }

        for (int i = 0; i < numOfAirlines; i++) {
            printlnBox(airlines[i].toString());
        }
    }

    static int chooseFlight() {
        if (numOfFlights == 0) {
            printlnError("No flights available.");
            return -2;
        }

        String flightNumber = readNonEmpty("Flight number: ");
        int index = findFlightIndex(flightNumber);
        if (index == -1) {
            printlnError("Flight not found.");
        }
        return index;
    }

    static int findAirlineIndex(String name) {
        for (int i = 0; i < numOfAirlines; i++) {
            if (airlineNames[i].equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    static int findFlightIndex(String flightNumber) {
        for (int i = 0; i < numOfFlights; i++) {
            if (flights[i].getFlightNumber().equalsIgnoreCase(flightNumber)) {
                return i;
            }
        }
        return -1;
    }

    static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            printlnError("Input cannot be empty.");
        }
    }

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                printlnError("Please enter a valid number.");
            }
        }
    }

    static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            } else {
                printlnError("Please enter a number between " + min + " and " + max + ".");
            }
        }
    }

    static LocalDateTime readDateTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            try {
                return LocalDateTime.parse(value, INPUT_FORMAT);
            } catch (DateTimeParseException e) {
                printlnError("Use format yyyy-MM-dd HH:mm:ss (example: 2026-03-29 14:30:00).");
            }
        }
    }

    static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim().toLowerCase();
            if (value.equals("y") || value.equals("yes"))
                return true;
            if (value.equals("n") || value.equals("no"))
                return false;
            printlnError("Please type y or n.");
        }
    }

    static void printlnSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
        pause(1000);
    }

    static void printlnError(String message) {
        System.out.println("[ERROR] " + message);
        pause(1000);
    }

    static void printlnBox(String message) {
        System.out.println();
        System.out.println("============================================");
        System.out.println(message);
        System.out.println("============================================");
    }

    // Pause helper: sleeps for ms milliseconds, handles interruption properly
    static void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Clear screen helper: tries platform clear commands, then ANSI, then newlines
    static void clearScreen() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Process p;
            if (os.contains("win")) {
                // use cmd /c cls on Windows
                p = new ProcessBuilder("cmd", "/c", "cls").inheritIO().start();
                p.waitFor();
            } else {
                // try unix clear command
                p = new ProcessBuilder("clear").inheritIO().start();
                p.waitFor();
            }
        } catch (Exception e) {
            // fallback to ANSI escape sequence
            try {
                final String ANSI_CLS = "\u001b[2J\u001b[H";
                System.out.print(ANSI_CLS);
                System.out.flush();
            } catch (Exception ex) {
                // last resort: print several newlines
                for (int i = 0; i < 50; i++)
                    System.out.println();
            }
        }
    }
}
