import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.Scanner;

public class AirportTest {
    static final int MAX_AIRLINES = 20;
    static final int MAX_FLIGHTS = 200;
    static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static final String DATA_FILE_NAME = "airport_state.data";
    static final File DATA_FILE = new File(DATA_FILE_NAME);

    static LinkedList<Airline> airlines = new LinkedList<Airline>();

    static LinkedList<Flight> flights = new LinkedList<Flight>();

    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        loadState();

        boolean repeat = true;
        while (repeat) {
            displayMainMenu();

            int choice = readInt("Choose an option: ", 0, 3);
            switch (choice) {
                case 1:
                    handleAirlineMenu();
                    break;
                case 2:
                    handleFlightMenu();
                    break;
                case 3:
                    handleTicketMenu();
                    break;
                case 0:
                    saveState();
                    repeat = false;
                    printlnBox("Thank you for using Airport Manager.");
                    break;
                default:
                    printlnError("Invalid option. Please choose between 0 and 3.");
                    break;
            }
        }

        input.close();
    }

    static void loadState() {
        if (!DATA_FILE.exists()) {
            return;
        }

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE));
            airlines = readAirlineList(in.readObject());
            flights = readFlightList(in.readObject());
            in.close();
        } catch (Exception e) {
            airlines = new LinkedList<Airline>();
            flights = new LinkedList<Flight>();
            printlnError("Saved data could not be loaded. Starting with a fresh workspace.");
        }
    }

    static void saveState() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE));
            out.writeObject(airlines);
            out.writeObject(flights);
            out.close();
        } catch (IOException e) {
            printlnError("Could not save data to file.");
            return;
        }
    }

    static void displayMainMenu() {
        clearScreen();

        System.out.println(
                "---------------- Main Menu -----------------"
                        + "\n1) Airlines"
                        + "\n2) Flights"
                        + "\n3) Tickets"
                        + "\n0) Exit"
                        + "\n--------------------------------------------");
    }

    static void displayAirlineMenu() {
        clearScreen();

        System.out.println(
                "-------------- Airlines Menu ---------------"
                        + "\n1) Create Airline");

        if (airlines.size() == 0) {
            System.out.println("No airlines available. Create one to see more options.");
        } else {
            for (int i = 0; i < airlines.size(); i++)
                System.out.println((i + 2) + ") " + airlines.get(i).getName());
        }

        System.out.println("0) Back to Main Menu"
                + "\n--------------------------------------------");
    }

    static void displayAirlineMenu(int airlineIndex) {
        clearScreen();

        Airline airline = airlines.get(airlineIndex);
        System.out.println(
                repeat("-", 18 - (airline.getName().length() / 2)) + " " + airline.getName() + " Menu "
                        + repeat("-",
                                (18 - (airline.getName().length() / 2)) + (airline.getName().length() % 2 == 0 ? 1 : 0))
                        + "\n1) Show Airline Details"
                        + "\n2) Add Flight to this Airline"
                        + "\n3) Remove this Airline");

        int flightCount = 0;
        int indexOffset = 4; // Start flight options from 4
        for (int i = 0; i < flights.size(); i++) {
            Flight currentFlight = flights.get(i);
            if (currentFlight.getAirline() == airline) {
                System.out.println((indexOffset++) + ") Manage " + currentFlight.getFlightNumber() + " Flight");
                flightCount++;
            }
        }
        if (flightCount == 0) {
            System.out.println("No flights for this airline. Add one to see more options.");
        }

        System.out.println("0) Back to Airlines Menu"
                + "\n--------------------------------------------");
    }

    static void handleAirlineMenu() {

        boolean repeat = true;
        while (repeat) {
            displayAirlineMenu();

            int choice = readInt("Choose an option: ", 0, airlines.size() + 1);
            switch (choice) {
                case 1:
                    createAirline();
                    break;
                case 0:
                    repeat = false;
                    break;
                default: {
                    int airlineIndex = choice - 2;
                    if (airlineIndex >= 0 && airlineIndex < airlines.size()) {
                        handleAirlineMenu(airlineIndex);
                    } else {
                        printlnError("Invalid option. Please choose between 0 and " + (airlines.size() + 1) + ".");
                    }
                    break;
                }
            }
        }
    }

    static void handleAirlineMenu(int airlineIndex) {
        Airline airline = airlines.get(airlineIndex);

        boolean repeat = true;
        while (repeat) {
            displayAirlineMenu(airlineIndex);

            int choice = readInt("Choose an option: ", 0, flights.size() + 3);
            switch (choice) {
                case 0:
                    repeat = false;
                    break;
                case 1:
                    printlnBox(airline.toString());
                    System.out.print("\nPress Enter to continue... ");
                    input.nextLine();
                    break;
                case 2:
                    createFlight(airlineIndex);
                    break;
                case 3:
                    removeAirline(airlineIndex);
                    repeat = false; // After removing, go back to main airlines menu
                    break;
                default: {
                    int flightOptionIndex = choice - 4;
                    int flightCount = 0;
                    for (int i = 0; i < flights.size(); i++) {
                        if (flights.get(i).getAirline() == airline) {
                            if (flightCount == flightOptionIndex) {
                                handleFlightMenu(i, airline.getName());
                                break;
                            }
                            flightCount++;
                        }
                    }

                    if (flightOptionIndex < 0 || flightOptionIndex > flightCount) {
                        printlnError("Invalid option. Please choose between 0 and " + (flights.size() + 3) + ".");
                    }
                    break;
                }
            }
        }
    }

    static void displayFlightMenu() {
        clearScreen();

        System.out.println(
                "--------------- Flights Menu ---------------"
                        + "\n1) Create Flight");

        if (flights.size() == 0) {
            System.out.println("No flights available. Create one to see more options.");
        } else {
            for (int i = 0; i < flights.size(); i++)
                System.out.println(
                        (i + 2) + ") " + flights.get(i).getFlightNumber() + " (" + flights.get(i).getAirline().getName()
                                + ")");
        }

        System.out.println("0) Back to Main Menu"
                + "\n--------------------------------------------");
    }

    static void displayFlightMenu(int flightIndex) {
        clearScreen();

        Flight flight = flights.get(flightIndex);
        System.out.println(
                repeat("-", 18 - (flight.getFlightNumber().length() / 2))
                        + " " + flight.getFlightNumber() + " Menu " + repeat("-",
                                (18 - (flight.getFlightNumber().length() / 2))
                                        + (flight.getFlightNumber().length() % 2 == 0 ? 1 : 0))
                        + "\n1) Show Flight Details"
                        + "\n2) Add Ticket to this Flight"
                        + "\n3) Refund Ticket from this Flight"
                        + "\n4) Depart Flight"
                        + "\n5) Land Flight"
                        + "\n0) Back to Flights Menu"
                        + "\n--------------------------------------------");
    }

    static void displayFlightMenu(int flightIndex, String airlineName) {
        clearScreen();

        Flight flight = flights.get(flightIndex);
        System.out.println(
                repeat("-", 16 - (airlineName.length() / 2) - (flight.getFlightNumber().length() / 2)) + " "
                        + airlineName + " - " + flight.getFlightNumber() + " Menu "
                        + repeat("-",
                                (16 - (airlineName.length() / 2) - (flight.getFlightNumber().length() / 2))
                                        + (airlineName.length() % 2 == 0 ? 1 : 0))
                        + "\n1) Show Flight Details"
                        + "\n2) Add Ticket to this Flight"
                        + "\n3) Refund Ticket from this Flight"
                        + "\n4) Depart Flight"
                        + "\n5) Land Flight"
                        + "\n6) Remove this Flight"
                        + "\n0) Back to " + airlineName + " Flights Menu"
                        + "\n--------------------------------------------");
    }

    static void handleFlightMenu() {
        if (airlines.size() == 0) {
            printlnError("No airlines available. Create an airline first.");
            return;
        }

        boolean repeat = true;
        while (repeat) {
            displayFlightMenu();

            int choice = readInt("Choose an option: ", 0, flights.size() + 1);
            switch (choice) {
                case 1:
                    createFlight();
                    break;
                case 0:
                    repeat = false;
                    break;
                default: {
                    int flightIndex = choice - 2;
                    if (flightIndex >= 0 && flightIndex < flights.size()) {
                        handleFlightMenu(flightIndex);
                    } else {
                        printlnError("Invalid option. Please choose between 0 and " + (flights.size() + 1) + ".");
                    }
                    break;
                }
            }
        }
    }

    static void handleFlightMenu(int flightIndex) {
        Flight flight = flights.get(flightIndex);

        boolean repeat = true;
        while (repeat) {
            displayFlightMenu(flightIndex);

            int choice = readInt("Choose an option: ", 0, 6);
            switch (choice) {
                case 1:
                    printlnBox(flight.toString());
                    System.out.print("\nPress Enter to continue... ");
                    input.nextLine();
                    break;
                case 2:
                    addTicketToFlight(flightIndex);
                    break;
                case 3:
                    refundTicketFromFlight(flightIndex);
                    break;
                case 4:
                    departFlight(flightIndex);
                    break;
                case 5:
                    landFlight(flightIndex);
                    break;
                case 6:
                    removeFlight(flightIndex, true);
                    repeat = false; // After removing, go back to airline's flight menu
                    break;
                case 0:
                    repeat = false;
                    break;
                default:
                    printlnError("Invalid option. Please choose between 0 and 6.");
                    break;
            }
        }
    }

    static void handleFlightMenu(int flightIndex, String airlineName) {
        Flight flight = flights.get(flightIndex);

        boolean repeat = true;
        while (repeat) {
            displayFlightMenu(flightIndex, airlineName);

            int choice = readInt("Choose an option: ", 0, 6);
            switch (choice) {
                case 1:
                    printlnBox(flight.toString());
                    System.out.print("\nPress Enter to continue... ");
                    input.nextLine();
                    break;
                case 2:
                    addTicketToFlight(flightIndex);
                    break;
                case 3:
                    refundTicketFromFlight(flightIndex);
                    break;
                case 4:
                    departFlight(flightIndex);
                    break;
                case 5:
                    landFlight(flightIndex);
                    break;
                case 6:
                    removeFlight(flightIndex, true);
                    repeat = false; // After removing, go back to airline's flight menu
                    break;
                case 0:
                    repeat = false;
                    break;
                default:
                    printlnError("Invalid option. Please choose between 0 and 6.");
                    break;
            }
        }
    }

    static void displayTicketMenu() {
        clearScreen();

        System.out.print("Select a flight to manage tickets for:\n");
        for (int i = 0; i < flights.size(); i++) {
            System.out.println(
                    (i + 1) + ") " + flights.get(i).getFlightNumber() + " (" + flights.get(i).getAirline().getName()
                            + ")");
        }

        System.out.println("0) Back to Main Menu"
                + "\n--------------------------------------------");

    }

    static void displayTicketMenu(int flightIndex) {
        clearScreen();

        Flight flight = flights.get(flightIndex);
        System.out.println(
                "---------------- " + flight.getFlightNumber() + " Tickets Menu -----------------"
                        + "\n1) Show All Tickets"
                        + "\n2) Add Ticket to this Flight"
                        + "\n3) Refund Ticket from this Flight"
                        + "\n0) Back to Flights Menu"
                        + "\n--------------------------------------------");
    }

    static void handleTicketMenu() {
        if (flights.size() == 0) {
            printlnError("No flights available. Create a flight first.");
            return;
        }

        boolean repeat = true;
        while (repeat) {
            displayTicketMenu();

            int choice = readInt("Choose a flight: ", 0, flights.size());
            switch (choice) {
                case 0:
                    repeat = false;
                    break;
                default: {
                    int flightIndex = choice - 1;
                    if (flightIndex >= 0 && flightIndex < flights.size()) {
                        handleTicketMenu(flightIndex);
                    } else {
                        printlnError("Invalid option. Please choose between 0 and " + flights.size() + ".");
                    }
                    break;
                }
            }
        }
    }

    static void handleTicketMenu(int flightIndex) {
        Flight flight = flights.get(flightIndex);

        boolean repeat = true;
        while (repeat) {
            displayTicketMenu(flightIndex);

            int choice = readInt("Choose an option: ", 0, 3);
            switch (choice) {
                case 1:
                    printlnBox(flight.toString());
                    System.out.print("\nPress Enter to continue... ");
                    input.nextLine();
                    break;
                case 2:
                    addTicketToFlight(flightIndex);
                    break;
                case 3:
                    refundTicketFromFlight(flightIndex);
                    break;
                case 0:
                    repeat = false;
                    break;
                default:
                    printlnError("Invalid option. Please choose between 0 and 3.");
                    break;
            }
        }
    }

    static void createAirline() {
        if (airlines.size() == MAX_AIRLINES) {
            printlnError("Cannot create more airlines. Capacity reached.");
            return;
        }

        String name = readNonEmpty("Airline name: ");
        if (findAirlineIndex(name) != -1) {
            printlnError("Airline already exists.");
            return;
        }

        int maxFlights = readInt("Maximum number of flights for this airline: ", 1, Integer.MAX_VALUE);

        airlines.add(new Airline(name, maxFlights));
        saveState();
        printlnSuccess("Airline created successfully.");
    }

    static void removeAirline(int airlineIndex) {
        // Remove all flights of this airline first
        Airline airline = airlines.get(airlineIndex);
        for (int i = 0; i < flights.size(); i++) {
            if (flights.get(i).getAirline() == airline) {
                removeFlight(i, false);
                i--; // Adjust index after removal
            }
        }

        airlines.remove(airlineIndex);

        saveState();
        printlnSuccess("Airline removed successfully.");
    }

    static void createFlight() {
        if (flights.size() == MAX_FLIGHTS) {
            printlnError("Cannot create more flights. Capacity reached.");
            return;
        }

        System.out.println("Select an airline for this flight:");
        for (int i = 0; i < airlines.size(); i++)
            System.out.println((i + 1) + ") " + airlines.get(i).getName());

        System.out.println("0) Back to previous menu");

        int airlineChoice = readInt("Choose an airline: ", 0, airlines.size());
        if (airlineChoice == 0) {
            return; // Go back to previous menu
        }
        int airlineIndex = airlineChoice - 1;

        String flightNumber = readNonEmpty("Flight number: ");
        if (airlines.get(airlineIndex).searchFlight(flightNumber) != -1) {
            printlnError("Flight number already exists.");
            return;
        }

        String origin = readNonEmpty("Origin: ");
        String destination = readNonEmpty("Destination: ");
        LocalDateTime departure = readDateTime("Planned departure (yyyy-MM-dd HH:mm:ss): ");
        LocalDateTime arrival = readDateTime("Planned arrival (yyyy-MM-dd HH:mm:ss): ");

        Flight flight = new Flight(flightNumber, origin, destination, departure, arrival);
        boolean added = airlines.get(airlineIndex).addFlight(flight);
        if (!added) {
            printlnError("Could not add flight to airline (duplicate or airline capacity reached).");
            return;
        }

        flights.add(airlines.get(airlineIndex).getFlight(flightNumber)); // Get the actual flight object from the
                                                                         // airline
        saveState();
        printlnSuccess("Flight created and assigned successfully.");
    }

    static void createFlight(int airlineIndex) {
        if (flights.size() == MAX_FLIGHTS) {
            printlnError("Cannot create more flights. Capacity reached.");
            return;
        }

        String flightNumber = readNonEmpty("Flight number: ");
        if (airlines.get(airlineIndex).searchFlight(flightNumber) != -1) {
            printlnError("Flight number already exists.");
            return;
        }

        String origin = readNonEmpty("Origin: ");
        String destination = readNonEmpty("Destination: ");
        LocalDateTime departure = readDateTime("Planned departure (yyyy-MM-dd HH:mm:ss): ");
        LocalDateTime arrival = readDateTime("Planned arrival (yyyy-MM-dd HH:mm:ss): ");

        Flight flight = new Flight(flightNumber, origin, destination, departure, arrival);
        boolean added = airlines.get(airlineIndex).addFlight(flight);
        if (!added) {
            printlnError("Could not add flight to airline (duplicate or airline capacity reached).");
            return;
        }

        flights.add(airlines.get(airlineIndex).getFlight(flightNumber)); // Get the actual flight object from the
                                                                         // airline
        saveState();
        printlnSuccess("Flight created and assigned successfully.");
    }

    static void removeFlight(int flightIndex, boolean printSuccess) {
        flights.get(flightIndex).getAirline().removeFlight(flights.get(flightIndex).getFlightNumber());

        flights.remove(flightIndex);
        saveState();

        if (printSuccess) {
            saveState();
            printlnSuccess("Flight removed successfully.");
        }
    }

    static void addTicketToFlight(int flightIndex) {
        Flight flight = flights.get(flightIndex);
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

        saveState();
        printlnSuccess("Ticket added successfully. Ticket ID: " + ticket.getTicketId());
    }

    static void refundTicketFromFlight(int flightIndex) {
        String ticketId = readNonEmpty("Ticket ID to refund: ");
        boolean removed = flights.get(flightIndex).refundTicket(ticketId);
        if (!removed) {
            printlnError("Ticket not found.");
            return;
        }

        saveState();
        printlnSuccess("Ticket refunded successfully.");
    }

    static void departFlight(int flightIndex) {
        LocalDateTime departDate = readDateTime("Actual departure (yyyy-MM-dd HH:mm:ss): ");
        flights.get(flightIndex).depart(departDate);
        saveState();
        printlnSuccess("Flight departure status updated.");
    }

    static void landFlight(int flightIndex) {
        LocalDateTime landDate = readDateTime("Actual landing (yyyy-MM-dd HH:mm:ss): ");
        flights.get(flightIndex).land(landDate);
        saveState();
        printlnSuccess("Flight landing status updated.");
    }

    static int findAirlineIndex(String name) {
        for (int i = 0; i < airlines.size(); i++) {
            if (airlines.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    static LinkedList<Airline> readAirlineList(Object data) {
        LinkedList<Airline> list = new LinkedList<>();
        if (data instanceof LinkedList<?>) {
            for (Object item : (LinkedList<?>) data) {
                list.add((Airline) item);
            }
        } else if (data instanceof Airline[]) {
            for (Airline airline : (Airline[]) data) {
                if (airline != null) {
                    list.add(airline);
                }
            }
        }
        return list;
    }

    static LinkedList<String> readStringList(Object data) {
        LinkedList<String> list = new LinkedList<>();
        if (data instanceof LinkedList<?>) {
            for (Object item : (LinkedList<?>) data) {
                list.add((String) item);
            }
        } else if (data instanceof String[]) {
            for (String value : (String[]) data) {
                if (value != null) {
                    list.add(value);
                }
            }
        }
        return list;
    }

    static LinkedList<Flight> readFlightList(Object data) {
        LinkedList<Flight> list = new LinkedList<>();
        if (data instanceof LinkedList<?>) {
            for (Object item : (LinkedList<?>) data) {
                list.add((Flight) item);
            }
        } else if (data instanceof Flight[]) {
            for (Flight flight : (Flight[]) data) {
                if (flight != null) {
                    list.add(flight);
                }
            }
        }
        return list;
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
                printlnError(
                        "Use format yyyy-MM-dd HH:mm:ss (example: " + LocalDateTime.now().format(INPUT_FORMAT) + ").");
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

    static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++)
            sb.append(str);
        return sb.toString();
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
