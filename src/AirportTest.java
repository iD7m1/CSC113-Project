import java.time.LocalDateTime;

public class AirportTest {
    public static void main(String[] args) {
        Airline Saudia = new Airline("Saudia", 10);
        Airline Flynas = new Airline("Flynas", 10);

        Flight SV123 = new Flight("SV123", "Jeddah", "Riyadh", LocalDateTime.of(2026, 3, 1, 10, 0), LocalDateTime.of(2026, 3, 1, 11, 0));
        
        EconomyTicket economyTicket1 = new EconomyTicket("Abdulrahman", "10A", true);
        economyTicket1.setLoungePass(new LoungePass(1));
        SV123.addTicket(economyTicket1);
        FirstClassTicket firstClassTicket1 = new FirstClassTicket("Sara", "1A", true);
        firstClassTicket1.setLoungePass(new LoungePass(2));
        SV123.addTicket(firstClassTicket1);
        BasicEconomyTicket basicEconomyTicket1 = new BasicEconomyTicket("Fahad", "20A", true, false);
        SV123.addTicket(basicEconomyTicket1);

        Saudia.addFlight(SV123);


        Flight SV456 = new Flight("SV456", "Riyadh", "Jeddah", LocalDateTime.of(2026, 3, 2, 15, 0), LocalDateTime.of(2026, 3, 2, 16, 0));

        EconomyTicket economyTicket2 = new EconomyTicket("Noura", "10B", false);
        SV456.addTicket(economyTicket2);
        FirstClassTicket firstClassTicket2 = new FirstClassTicket("Omar", "1B", false);
        SV456.addTicket(firstClassTicket2);
        BasicEconomyTicket basicEconomyTicket2 = new BasicEconomyTicket("Layla", "20B", false, true);
        SV456.addTicket(basicEconomyTicket2);

        Flynas.addFlight(SV456);


        System.out.println(Saudia);
        System.out.println(Flynas);
    }
}
