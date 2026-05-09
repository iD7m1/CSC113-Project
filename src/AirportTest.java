import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import java.awt.*;

public class AirportTest extends JFrame {
    static final int MAX_AIRLINES = 20;
    static final int MAX_FLIGHTS = 200;
    static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static final DateTimeFormatter INPUT_FORMAT_ZERO_MINUTES_SECONDS = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:00:00");

    static final String DATA_FILE_NAME = "airport_state.data";
    static final File DATA_FILE = new File(DATA_FILE_NAME);

    static List<Airline> airlines = new List<Airline>();

    static List<Flight> flights = new List<Flight>();

    // GUI components
    private CardLayout cardLayout;
    private JPanel contentPane;
    // panels and lists for navigation
    private JPanel airlinesPanelCard;
    private JPanel airlineDetailPanelCard;
    private JPanel flightsPanelCard;
    private JPanel flightDetailPanelCard;

    private JList<String> airlinesJList;
    private DefaultListModel<String> airlinesListModel;

    private JList<String> airlineFlightsJList;
    private DefaultListModel<String> airlineFlightsListModel;

    private JList<String> flightsJList;
    private DefaultListModel<String> flightsListModel;

    private JList<String> flightTicketsJList;
    private DefaultListModel<String> flightTicketsListModel;

    private JList<String> ticketFlightsJList;
    private DefaultListModel<String> ticketFlightsListModel;

    private JList<String> ticketFlightDetailsJList;
    private DefaultListModel<String> ticketFlightDetailsListModel;

    private int currentAirlineIndex = -1;
    private int currentFlightIndex = -1;
    private String flightReturnCard = "flights";
    private int currentTicketFlightIndex = -1;

    public static void main(String[] args) {

        loadState();

        AirportTest frame = new AirportTest();
        frame.setVisible(true);

        // Add window listener to save state on close
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveState();
                System.out.println("State saved. Exiting.");
                System.exit(0);
            }
        });
    }

    public AirportTest() {
        setTitle("Airport Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);
        setContentPane(contentPane);

        // build and add panels (cards)
        contentPane.add(buildMainPanel(), "main");
        airlinesPanelCard = buildAirlinesPanel();
        contentPane.add(airlinesPanelCard, "airlines");
        airlineDetailPanelCard = buildAirlineDetailPanel();
        contentPane.add(airlineDetailPanelCard, "airlineDetail");
        flightsPanelCard = buildFlightsPanel();
        contentPane.add(flightsPanelCard, "flights");
        flightDetailPanelCard = buildFlightDetailPanel();
        contentPane.add(flightDetailPanelCard, "flightDetail");
        JPanel ticketsPanelCard = buildTicketsPanel();
        contentPane.add(ticketsPanelCard, "tickets");
        JPanel ticketDetailPanelCard = buildTicketDetailPanel();
        contentPane.add(ticketDetailPanelCard, "ticketDetail");

        cardLayout.show(contentPane, "main");
    }

    private JPanel buildMainPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(200, 230, 255));

        JLabel title = new JLabel("Airport Manager");
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setBounds(380, 30, 400, 50);
        panel.add(title);

        JButton airlinesBtn = new JButton("Airlines");
        airlinesBtn.setBounds(80, 150, 250, 100);
        airlinesBtn.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.add(airlinesBtn);

        JButton flightsBtn = new JButton("Flights");
        flightsBtn.setBounds(370, 150, 250, 100);
        flightsBtn.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.add(flightsBtn);

        JButton ticketsBtn = new JButton("Tickets");
        ticketsBtn.setBounds(660, 150, 250, 100);
        ticketsBtn.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.add(ticketsBtn);

        JButton exitBtn = new JButton("Exit");
        exitBtn.setBounds(370, 330, 250, 100);
        exitBtn.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.add(exitBtn);

        airlinesBtn.addActionListener(e -> showAirlinesDialog());
        flightsBtn.addActionListener(e -> showFlightsDialog());
        ticketsBtn.addActionListener(e -> showTicketsDialog());
        exitBtn.addActionListener(e -> {
            saveState();
            System.exit(0);
        });

        return panel;
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
            airlines = new List<Airline>();
            flights = new List<Flight>();
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
            printlnError("Could not save data to file.\n" + e.getMessage());
            return;
        }
    }

    /* GUI dialog handlers */
    private void showAirlinesDialog() {
        // switch to airlines card
        refreshAirlinesList();
        cardLayout.show(contentPane, "airlines");
    }

    private void showFlightsDialog() {
        refreshFlightsList();
        cardLayout.show(contentPane, "flights");
    }

    private void showTicketsDialog() {
        refreshTicketsFlightsList();
        cardLayout.show(contentPane, "tickets");
    }

    private Airline chooseAirline() {
        if (airlines.size() == 0) {
            JOptionPane.showMessageDialog(this, "No airlines available. Create one first.");
            return null;
        }
        String[] names = new String[airlines.size()];
        for (int i = 0; i < airlines.size(); i++)
            names[i] = airlines.get(i).getName();
        String sel = (String) JOptionPane.showInputDialog(this, "Choose airline:", "Airlines",
                JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
        if (sel == null)
            return null;
        for (int i = 0; i < airlines.size(); i++)
            if (airlines.get(i).getName().equals(sel))
                return airlines.get(i);
        return null;
    }

    private void guiCreateFlightForAirline(int airlineIndex) {
        if (airlineIndex < 0 || airlineIndex >= airlines.size()) {
            JOptionPane.showMessageDialog(this, "Select an airline first.");
            return;
        }

        if (flights.size() == MAX_FLIGHTS) {
            JOptionPane.showMessageDialog(this, "Cannot create more flights. Capacity reached.");
            return;
        }

        JTextField flightNumberField = new JTextField();
        JTextField originField = new JTextField();
        JTextField destinationField = new JTextField();
        JTextField departureField = new JTextField(LocalDateTime.now().format(INPUT_FORMAT_ZERO_MINUTES_SECONDS));
        JTextField arrivalField = new JTextField(
                LocalDateTime.now().plusHours(2).format(INPUT_FORMAT_ZERO_MINUTES_SECONDS));

        Object[] fields = {
                "Flight number:", flightNumberField,
                "Origin:", originField,
                "Destination:", destinationField,
                "Planned departure: (yyyy-MM-dd HH:mm:ss)", departureField,
                "Planned arrival: (yyyy-MM-dd HH:mm:ss)", arrivalField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Create Flight", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String flightNumber = flightNumberField.getText().trim();
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        if (flightNumber.isEmpty() || origin.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }

        if (airlines.get(airlineIndex).searchFlight(flightNumber) != -1) {
            JOptionPane.showMessageDialog(this, "Flight number already exists.");
            return;
        }

        try {
            LocalDateTime departure = LocalDateTime.parse(departureField.getText().trim(), INPUT_FORMAT);
            LocalDateTime arrival = LocalDateTime.parse(arrivalField.getText().trim(), INPUT_FORMAT);
            Flight flight = new Flight(flightNumber, origin, destination, departure, arrival);
            if (!airlines.get(airlineIndex).addFlight(flight)) {
                JOptionPane.showMessageDialog(this, "Could not add flight to airline.");
                return;
            }
            flights.insertAtBack(airlines.get(airlineIndex).getFlight(flightNumber));
            refreshAirlineFlightsList();
            refreshFlightsList();
            JOptionPane.showMessageDialog(this, "Flight created successfully.");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Use format yyyy-MM-dd HH:mm:ss (example: " + LocalDateTime.now().format(INPUT_FORMAT) + ").");
        }
    }

    private void guiAddTicketToSpecificFlight(int flightIndex) {
        if (flightIndex < 0 || flightIndex >= flights.size()) {
            JOptionPane.showMessageDialog(this, "Select a flight first.");
            return;
        }

        Flight flight = flights.get(flightIndex);
        String[] types = { "Economy", "Basic Economy", "First Class" };
        String type = (String) JOptionPane.showInputDialog(this, "Choose ticket type:", "Ticket Type",
                JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
        if (type == null)
            return;

        String passenger = JOptionPane.showInputDialog(this, "Passenger name:");
        if (passenger == null || passenger.trim().isEmpty())
            return;

        String seat = JOptionPane.showInputDialog(this, "Seat number:");
        if (seat == null || seat.trim().isEmpty())
            return;

        Ticket ticket;
        if (type.equals(types[0])) {
            boolean mealIncluded = JOptionPane.showConfirmDialog(this, "Meal included?", "Meal",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            ticket = new EconomyTicket(passenger.trim(), seat.trim(), mealIncluded);
        } else if (type.equals(types[1])) {
            boolean mealIncluded = JOptionPane.showConfirmDialog(this, "Meal included?", "Meal",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            boolean allowedCarryOn = JOptionPane.showConfirmDialog(this, "Carry-on allowed?", "Carry-on",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            ticket = new BasicEconomyTicket(passenger.trim(), seat.trim(), mealIncluded, allowedCarryOn);
        } else {
            boolean spa = JOptionPane.showConfirmDialog(this, "Spa access included?", "Spa",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            ticket = new FirstClassTicket(passenger.trim(), seat.trim(), spa);
        }

        if (JOptionPane.showConfirmDialog(this, "Add lounge pass?", "Lounge",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            String[] levels = { "1", "2" };
            String level = (String) JOptionPane.showInputDialog(this, "Choose lounge level:", "Lounge Pass",
                    JOptionPane.QUESTION_MESSAGE, null, levels, levels[0]);
            if (level != null) {
                ticket.setLoungePass(new LoungePass(Integer.parseInt(level)));
            }
        }

        if (!flight.addTicket(ticket)) {
            JOptionPane.showMessageDialog(this, "Could not add ticket.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Ticket added successfully. Ticket ID: " + ticket.getTicketId(), "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void guiRefundTicketFromSpecificFlight(int flightIndex) {
        if (flightIndex < 0 || flightIndex >= flights.size()) {
            JOptionPane.showMessageDialog(this, "Select a flight first.");
            return;
        }

        String[] ticketIds = new String[flights.get(flightIndex).getTicketCount()];
        for (int i = 0; i < flights.get(flightIndex).getTicketCount(); i++)
            ticketIds[i] = flights.get(flightIndex).getTicketAt(i).getTicketId();

        String ticketId = (String) JOptionPane.showInputDialog(this, "Select ticket to refund:", "Refund Ticket",
                JOptionPane.QUESTION_MESSAGE, null, ticketIds, ticketIds[0]);
        if (ticketId == null || ticketId.trim().isEmpty())
            return;

        if (!flights.get(flightIndex).refundTicket(ticketId.trim())) {
            JOptionPane.showMessageDialog(this, "Ticket not found.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Ticket refunded successfully.", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void guiDepartFlight() {
        if (currentFlightIndex < 0 || currentFlightIndex >= flights.size()) {
            JOptionPane.showMessageDialog(this, "Select a flight first.");
            return;
        }

        while (true) {
            String value = JOptionPane.showInputDialog(this, "Actual departure (yyyy-MM-dd HH:mm:ss):", LocalDateTime.now().format(INPUT_FORMAT_ZERO_MINUTES_SECONDS));
            if (value == null || value.trim().isEmpty())
                return;

            try {
                flights.get(currentFlightIndex).depart(LocalDateTime.parse(value.trim(), INPUT_FORMAT));
                JOptionPane.showMessageDialog(this, "Flight departure status updated.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshFlightTicketsList();
                refreshTicketFlightDetailsList();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Use format yyyy-MM-dd HH:mm:ss (example: " + LocalDateTime.now().format(INPUT_FORMAT) + ").",
                        "Invalid Date Format", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guiLandFlight() {
        if (currentFlightIndex < 0 || currentFlightIndex >= flights.size()) {
            JOptionPane.showMessageDialog(this, "Select a flight first.");
            return;
        }

        while (true) {
            String value = JOptionPane.showInputDialog(this, "Actual landing (yyyy-MM-dd HH:mm:ss):", LocalDateTime.now().plusHours(2).format(INPUT_FORMAT_ZERO_MINUTES_SECONDS));
            if (value == null || value.trim().isEmpty())
                return;
            try {
                flights.get(currentFlightIndex).land(LocalDateTime.parse(value.trim(), INPUT_FORMAT));
                JOptionPane.showMessageDialog(this, "Flight landing status updated.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshFlightTicketsList();
                refreshTicketFlightDetailsList();
                return;
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Use format yyyy-MM-dd HH:mm:ss (example: " + LocalDateTime.now().format(INPUT_FORMAT) + ").",
                        "Invalid Date Format", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private void guiCreateAirline() {
        String name = JOptionPane.showInputDialog(this, "Airline name:");
        if (name == null || name.trim().isEmpty())
            return;
        String max = JOptionPane.showInputDialog(this, "Maximum number of flights:");
        if (max == null || max.trim().isEmpty())
            return;
        try {
            int m = Integer.parseInt(max.trim());
            if (findAirlineIndex(name) != -1) {
                JOptionPane.showMessageDialog(this, "Airline already exists.");
                return;
            }
            airlines.insertAtBack(new Airline(name, m));
            JOptionPane.showMessageDialog(this, "Airline created.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildAirlinesPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(220, 240, 255));

        JLabel title = new JLabel("Airlines");
        title.setFont(new Font("Tahoma", Font.BOLD, 24));
        title.setBounds(420, 20, 200, 40);
        panel.add(title);

        airlinesListModel = new DefaultListModel<>();
        airlinesJList = new JList<>(airlinesListModel);
        airlinesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(airlinesJList);
        scroll.setBounds(80, 90, 400, 350);
        panel.add(scroll);

        JButton createBtn = new JButton("Create Airline");
        createBtn.setBounds(520, 120, 300, 60);
        panel.add(createBtn);

        JButton openBtn = new JButton("Open Airline");
        openBtn.setBounds(520, 200, 300, 60);
        panel.add(openBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(520, 380, 300, 60);
        panel.add(backBtn);

        createBtn.addActionListener(e -> {
            guiCreateAirline();
            refreshAirlinesList();
        });
        openBtn.addActionListener(e -> {
            int sel = airlinesJList.getSelectedIndex();
            if (sel == -1) {
                JOptionPane.showMessageDialog(this, "Select an airline first.");
                return;
            }
            currentAirlineIndex = sel;
            refreshAirlineFlightsList();
            cardLayout.show(contentPane, "airlineDetail");
        });
        backBtn.addActionListener(e -> cardLayout.show(contentPane, "main"));

        return panel;
    }

    private JPanel buildAirlineDetailPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(230, 245, 255));

        JLabel title = new JLabel("Airline");
        title.setFont(new Font("Tahoma", Font.BOLD, 24));
        title.setBounds(420, 10, 300, 40);
        panel.add(title);

        airlineFlightsListModel = new DefaultListModel<>();
        airlineFlightsJList = new JList<>(airlineFlightsListModel);
        JScrollPane scroll = new JScrollPane(airlineFlightsJList);
        scroll.setBounds(80, 90, 400, 350);
        panel.add(scroll);

        JButton detailsBtn = new JButton("Show Airline Details");
        detailsBtn.setBounds(520, 90, 300, 50);
        panel.add(detailsBtn);

        JButton addFlightBtn = new JButton("Add Flight to this Airline");
        addFlightBtn.setBounds(520, 160, 300, 50);
        panel.add(addFlightBtn);

        JButton openFlightBtn = new JButton("Open Flight");
        openFlightBtn.setBounds(520, 230, 300, 50);
        panel.add(openFlightBtn);

        JButton removeBtn = new JButton("Remove this Airline");
        removeBtn.setBounds(520, 300, 300, 50);
        panel.add(removeBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(520, 380, 300, 50);
        panel.add(backBtn);

        detailsBtn.addActionListener(e -> {
            if (currentAirlineIndex < 0)
                return;
            JOptionPane.showMessageDialog(this, airlines.get(currentAirlineIndex).toString(), "Airline Details",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        addFlightBtn.addActionListener(e -> {
            guiCreateFlightForAirline(currentAirlineIndex);
            refreshAirlineFlightsList();
            refreshFlightsList();
        });
        openFlightBtn.addActionListener(e -> {
            int sel = airlineFlightsJList.getSelectedIndex();
            if (sel == -1) {
                JOptionPane.showMessageDialog(this, "Select a flight first.");
                return;
            }
            // find global flight index
            Flight f = airlines.get(currentAirlineIndex).getFlight(sel);
            currentFlightIndex = findGlobalFlightIndex(f);
            flightReturnCard = "airlineDetail";
            refreshFlightTicketsList();
            cardLayout.show(contentPane, "flightDetail");
        });
        removeBtn.addActionListener(e -> {
            if (currentAirlineIndex < 0)
                return;
            int r = JOptionPane.showConfirmDialog(this, "Remove airline and its flights?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                removeAirline(currentAirlineIndex);
                currentAirlineIndex = -1;
                refreshAirlinesList();
                refreshFlightsList();
                cardLayout.show(contentPane, "airlines");
            }
        });
        backBtn.addActionListener(e -> cardLayout.show(contentPane, "airlines"));

        return panel;
    }

    private JPanel buildFlightsPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(220, 240, 255));

        JLabel title = new JLabel("Flights");
        title.setFont(new Font("Tahoma", Font.BOLD, 24));
        title.setBounds(420, 20, 200, 40);
        panel.add(title);

        flightsListModel = new DefaultListModel<>();
        flightsJList = new JList<>(flightsListModel);
        flightsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(flightsJList);
        scroll.setBounds(80, 90, 400, 350);
        panel.add(scroll);

        JButton createBtn = new JButton("Create Flight");
        createBtn.setBounds(520, 120, 300, 60);
        panel.add(createBtn);

        JButton openBtn = new JButton("Open Flight");
        openBtn.setBounds(520, 200, 300, 60);
        panel.add(openBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(520, 380, 300, 60);
        panel.add(backBtn);

        createBtn.addActionListener(e -> {
            guiCreateFlight();
            refreshFlightsList();
            refreshAirlinesList();
        });
        openBtn.addActionListener(e -> {
            int sel = flightsJList.getSelectedIndex();
            if (sel == -1) {
                JOptionPane.showMessageDialog(this, "Select a flight first.");
                return;
            }
            currentFlightIndex = sel;
            flightReturnCard = "flights";
            refreshFlightTicketsList();
            cardLayout.show(contentPane, "flightDetail");
        });
        backBtn.addActionListener(e -> cardLayout.show(contentPane, "main"));

        return panel;
    }

    private JPanel buildFlightDetailPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(230, 245, 255));

        JLabel title = new JLabel("Flight");
        title.setFont(new Font("Tahoma", Font.BOLD, 24));
        title.setBounds(420, 10, 300, 40);
        panel.add(title);

        flightTicketsListModel = new DefaultListModel<>();
        flightTicketsJList = new JList<>(flightTicketsListModel);
        JScrollPane scroll = new JScrollPane(flightTicketsJList);
        scroll.setBounds(80, 90, 400, 350);
        panel.add(scroll);

        JButton detailsBtn = new JButton("Show Flight Details");
        detailsBtn.setBounds(520, 90, 300, 50);
        panel.add(detailsBtn);
        
        JButton addTicketBtn = new JButton("Add Ticket to this Flight");
        addTicketBtn.setBounds(520, 160, 300, 50);
        panel.add(addTicketBtn);

        JButton departBtn = new JButton("Depart Flight");
        departBtn.setBounds(520, 230, 300, 50);
        panel.add(departBtn);

        JButton landBtn = new JButton("Land Flight");
        landBtn.setBounds(520, 300, 300, 50);
        panel.add(landBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(520, 380, 300, 50);
        panel.add(backBtn);

        addTicketBtn.addActionListener(e -> {
            guiAddTicketToSpecificFlight(currentFlightIndex);
            refreshFlightTicketsList();
        });
        detailsBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, flights.get(currentFlightIndex).toString(), "Flight Details",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        departBtn.addActionListener(e -> {
            guiDepartFlight();
            refreshFlightsList();
        });
        landBtn.addActionListener(e -> {
            guiLandFlight();
            refreshFlightsList();
        });
        backBtn.addActionListener(e -> cardLayout.show(contentPane, flightReturnCard));

        return panel;
    }

    private JPanel buildTicketsPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(220, 240, 255));

        JLabel title = new JLabel("Tickets");
        title.setFont(new Font("Tahoma", Font.BOLD, 24));
        title.setBounds(420, 20, 200, 40);
        panel.add(title);

        ticketFlightsListModel = new DefaultListModel<>();
        ticketFlightsJList = new JList<>(ticketFlightsListModel);
        ticketFlightsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(ticketFlightsJList);
        scroll.setBounds(80, 90, 400, 350);
        panel.add(scroll);

        JButton openBtn = new JButton("Open Flight Tickets");
        openBtn.setBounds(520, 120, 300, 60);
        panel.add(openBtn);

        JButton showBtn = new JButton("Show Flight Details");
        showBtn.setBounds(520, 200, 300, 60);
        panel.add(showBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(520, 380, 300, 60);
        panel.add(backBtn);

        refreshTicketsFlightsList();

        openBtn.addActionListener(e -> {
            int sel = ticketFlightsJList.getSelectedIndex();
            if (sel == -1) {
                JOptionPane.showMessageDialog(this, "Select a flight first.");
                return;
            }
            currentTicketFlightIndex = sel;
            refreshTicketFlightDetailsList();
            cardLayout.show(contentPane, "ticketDetail");
        });
        showBtn.addActionListener(e -> {
            int sel = ticketFlightsJList.getSelectedIndex();
            if (sel == -1) {
                JOptionPane.showMessageDialog(this, "Select a flight first.");
                return;
            }
            JOptionPane.showMessageDialog(this, flights.get(sel).toString(), "Flight Details",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        backBtn.addActionListener(e -> cardLayout.show(contentPane, "main"));

        return panel;
    }

    private JPanel buildTicketDetailPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(230, 245, 255));

        JLabel title = new JLabel("Ticket Flight");
        title.setFont(new Font("Tahoma", Font.BOLD, 24));
        title.setBounds(400, 10, 300, 40);
        panel.add(title);

        ticketFlightDetailsListModel = new DefaultListModel<>();
        ticketFlightDetailsJList = new JList<>(ticketFlightDetailsListModel);
        JScrollPane scroll = new JScrollPane(ticketFlightDetailsJList);
        scroll.setBounds(80, 90, 400, 350);
        panel.add(scroll);

        JButton addTicketBtn = new JButton("Add Ticket");
        addTicketBtn.setBounds(520, 90, 300, 50);
        panel.add(addTicketBtn);

        JButton refundBtn = new JButton("Refund Ticket");
        refundBtn.setBounds(520, 160, 300, 50);
        panel.add(refundBtn);

        JButton detailsBtn = new JButton("Show Flight Details");
        detailsBtn.setBounds(520, 230, 300, 50);
        panel.add(detailsBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(520, 380, 300, 50);
        panel.add(backBtn);

        refreshTicketFlightDetailsList();

        addTicketBtn.addActionListener(e -> {
            guiAddTicketToSpecificFlight(currentTicketFlightIndex);
            refreshTicketFlightDetailsList();
        });
        refundBtn.addActionListener(e -> {
            guiRefundTicketFromSpecificFlight(currentTicketFlightIndex);
            refreshTicketFlightDetailsList();
        });
        detailsBtn.addActionListener(e -> {
            if (currentTicketFlightIndex < 0)
                return;
            JOptionPane.showMessageDialog(this, flights.get(currentTicketFlightIndex).toString(), "Flight Details",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        backBtn.addActionListener(e -> cardLayout.show(contentPane, "tickets"));

        return panel;
    }

    private void refreshAirlinesList() {
        if (airlinesListModel == null)
            return;
        airlinesListModel.clear();
        for (int i = 0; i < airlines.size(); i++)
            airlinesListModel.addElement(airlines.get(i).getName());
    }

    private void refreshAirlineFlightsList() {
        if (airlineFlightsListModel == null)
            return;
        airlineFlightsListModel.clear();
        if (currentAirlineIndex < 0)
            return;
        Airline a = airlines.get(currentAirlineIndex);
        for (int i = 0; i < a.getFlightCount(); i++)
            airlineFlightsListModel.addElement(a.getFlight(i).getFlightNumber());
    }

    private void refreshFlightsList() {
        if (flightsListModel == null)
            return;
        flightsListModel.clear();
        for (int i = 0; i < flights.size(); i++)
            flightsListModel
                    .addElement(flights.get(i).getFlightNumber() + " (" + flights.get(i).getAirline().getName() + ")");
    }

    private void refreshFlightTicketsList() {
        if (flightTicketsListModel == null)
            return;
        flightTicketsListModel.clear();
        if (currentFlightIndex < 0)
            return;
        Flight f = flights.get(currentFlightIndex);

        for (int i = 0; i < f.getTicketCount(); i++)
            flightTicketsListModel
                    .addElement(f.getTicketAt(i).getTicketId() + " - " + f.getTicketAt(i).getPassengerName());
    }

    private void refreshTicketsFlightsList() {
        if (ticketFlightsListModel == null)
            return;
        ticketFlightsListModel.clear();
        for (int i = 0; i < flights.size(); i++) {
            ticketFlightsListModel
                    .addElement(flights.get(i).getFlightNumber() + " (" + flights.get(i).getAirline().getName() + ")");
        }
    }

    private void refreshTicketFlightDetailsList() {
        if (ticketFlightDetailsListModel == null)
            return;
        ticketFlightDetailsListModel.clear();
        if (currentTicketFlightIndex < 0 || currentTicketFlightIndex >= flights.size())
            return;

        Flight f = flights.get(currentTicketFlightIndex);

        for (int i = 0; i < f.getTicketCount(); i++)
            ticketFlightDetailsListModel
                    .addElement(f.getTicketAt(i).getTicketId() + " - " + f.getTicketAt(i).getPassengerName());
    }

    private int findGlobalFlightIndex(Flight f) {
        for (int i = 0; i < flights.size(); i++)
            if (flights.get(i) == f)
                return i;
        return -1;
    }

    private void guiCreateFlight() {
        if (flights.size() == MAX_FLIGHTS) {
            JOptionPane.showMessageDialog(this, "Cannot create more flights. Capacity reached.");
            return;
        }

        Airline a = chooseAirline();
        if (a == null)
            return;

        JTextField flightNumberField = new JTextField();
        JTextField originField = new JTextField();
        JTextField destinationField = new JTextField();
        JTextField departureField = new JTextField(LocalDateTime.now().format(INPUT_FORMAT_ZERO_MINUTES_SECONDS));
        JTextField arrivalField = new JTextField(
                LocalDateTime.now().plusHours(2).format(INPUT_FORMAT_ZERO_MINUTES_SECONDS));

        Object[] fields = {
                "Flight number:", flightNumberField,
                "Origin:", originField,
                "Destination:", destinationField,
                "Planned departure: (yyyy-MM-dd HH:mm:ss)", departureField,
                "Planned arrival: (yyyy-MM-dd HH:mm:ss)", arrivalField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Create Flight", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION)
            return;

        String flightNumber = flightNumberField.getText().trim();
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        if (flightNumber.isEmpty() || origin.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }

        if (a.searchFlight(flightNumber) != -1) {
            JOptionPane.showMessageDialog(this, "Flight already exists.");
            return;
        }

        try {
            LocalDateTime departure = LocalDateTime.parse(departureField.getText().trim(), INPUT_FORMAT);
            LocalDateTime arrival = LocalDateTime.parse(arrivalField.getText().trim(), INPUT_FORMAT);
            Flight flight = new Flight(flightNumber, origin, destination, departure, arrival);
            if (a.addFlight(flight)) {
                JOptionPane.showMessageDialog(this, "Could not add flight to airline.");
                return;
            }
            flights.insertAtBack(a.getFlight(flightNumber));
            refreshAirlineFlightsList();
            refreshFlightsList();
            JOptionPane.showMessageDialog(this, "Flight created successfully.");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Use format yyyy-MM-dd HH:mm:ss (example: " + LocalDateTime.now().format(INPUT_FORMAT) + ").");
        }
    }

    static void removeAirline(int airlineIndex) {
        // Remove all flights of this airline first
        Airline airline = airlines.get(airlineIndex);
        for (int i = 0; i < flights.size(); i++) {
            if (flights.get(i).getAirline() == airline) {
                removeFlight(i);
                i--; // Adjust index after removal
            }
        }

        airlines.remove(airlineIndex);
    }

    static void removeFlight(int flightIndex) {
        flights.get(flightIndex).getAirline().removeFlight(flights.get(flightIndex).getFlightNumber());

        flights.remove(flightIndex);
    }

    static int findAirlineIndex(String name) {
        for (int i = 0; i < airlines.size(); i++) {
            if (airlines.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    static List<Airline> readAirlineList(Object data) {
        List<Airline> list = new List<Airline>();
        if (data instanceof List<?>) {
            for (int i = 0; i < ((List<?>) data).size(); i++) {
                list.insertAtBack((Airline) ((List<?>) data).get(i));
            }
        } else if (data instanceof Airline[]) {
            for (Airline airline : (Airline[]) data) {
                if (airline != null) {
                    list.insertAtBack(airline);
                }
            }
        }
        return list;
    }

    static List<Flight> readFlightList(Object data) {
        List<Flight> list = new List<Flight>();
        if (data instanceof List<?>) {
            for (int i = 0; i < ((List<?>) data).size(); i++) {
                list.insertAtBack((Flight) ((List<?>) data).get(i));
            }
        } else if (data instanceof Flight[]) {
            for (Flight flight : (Flight[]) data) {
                if (flight != null) {
                    list.insertAtBack(flight);
                }
            }
        }
        return list;
    }

    static void printlnSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    static void printlnError(String message) {
        System.err.println("[ERROR] " + message);
    }
}
