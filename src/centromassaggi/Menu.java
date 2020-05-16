package centromassaggi;

import centromassaggi.common.CustomManager;
import centromassaggi.common.Helpers;
import centromassaggi.queries.QueryPrenotazione;
import centromassaggi.queries.TreQueries;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Menu {

    public static String tipiMassaggioDiDurataMassima(Connection conn) throws SQLException {
        Statement stm = conn.createStatement();

        Scanner sc = new Scanner(System.in);

        System.out.println("Scegliere uno tra i seguenti massaggiatori:");
        System.out.println(Helpers.getMassaggiatori(stm));
        System.out.print("Massaggiatore: ");
        String massaggiatore = sc.nextLine();

        System.out.print("Inserire una durata massima (in minuti): ");
        int durataMassima = sc.nextInt();
        sc.nextLine();
        
        return TreQueries.tipiMassaggioDiDurataMassima(stm, massaggiatore, durataMassima);
    }


    public static String saleDisponibili(Connection conn) throws SQLException {
        Statement stm = conn.createStatement();
        
        Scanner sc = new Scanner(System.in);

        System.out.print("Inserire la data in cui controllare le sale disponibili (formato gg/mm/aaaa): ");
        String dataStringa = sc.nextLine();
        // Se la data non è nel formato gg/mm/aaaa la richiede
        while(!dataStringa.matches("^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/20[0-9]{2}$")) {
            System.out.print("Inserire la data nel formato corretto (formato gg/mm/aaaa): ");
            dataStringa = sc.nextLine();
        }
        LocalDate data = LocalDate.parse(dataStringa, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        System.out.println("\nInserire l'intervallo in cui verificare la disponibilità delle sale");
        System.out.print("Ora iniziale (formato hh:mm): ");
        String oraStringa = sc.nextLine();
        while(!oraStringa.matches("^(09|1[0-9]|20):[0-5][0-9]$")) {
            System.out.print("Inserisci un'ora valida (tra le 9:00 e le 21:00 escluse) nel formato corretto (formato hh:mm): ");
            oraStringa = sc.nextLine();
        }
        LocalTime oraInizio = LocalTime.parse(oraStringa);
        
        System.out.print("Ora finale (formato hh:mm): ");
        oraStringa = sc.nextLine();
        while(!oraStringa.matches("^(09|1[0-9]|2[0-1]):[0-5][0-9]$")) {
            System.out.print("Inserisci l'ora nel formato corretto (formato hh:mm): ");
            oraStringa = sc.nextLine();
        }
        LocalTime oraFine = LocalTime.parse(oraStringa);

        return TreQueries.saleDisponibili(stm, data, oraInizio, oraFine);
    }


    public static String stipendioDipendente(Connection conn) throws SQLException {
        Statement stm = conn.createStatement();

        Scanner sc = new Scanner(System.in);

        System.out.println("Scegliere uno tra i seguenti dipendenti:\n");
        System.out.println(Helpers.getDipendenti(stm));
        System.out.print("Dipendente: ");
        String dipendente = sc.nextLine();
        
        System.out.print("Scegliere l'anno ed il mese relativamente ai quali si vuole calcolare lo stipendio (formato mm/aaaa): ");
        String dataStringa = sc.nextLine();
        // Se la data non è nel formato mm/aaaa la richiede
        while(!dataStringa.matches("^(1[0-2]|0[1-9])/20[0-9]{2}$")) {
            System.out.print("Inserire la data nel formato corretto (formato mm/aaaa): ");
            dataStringa = sc.nextLine();
        }
        LocalDate data = LocalDate.parse("01/"+dataStringa, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return TreQueries.stipendioDipendente(stm, dipendente, data);
    }

    public static void main(String[] args) {
        
        try (Connection conn = CustomManager.getConnection()) {
            Scanner sc = new Scanner(System.in);
            int ans;
            do{
                // TODO: Valutare le difficoltà
                System.out.println("Scegliere una query: ");
                System.out.println("1 - Facile. Dati un massaggiatore ed una durata, si vogliono conoscere tutti i tipi di massaggio che "
                                + "egli è in grado di eseguire e la cui durata non è superiore rispetto a quella data.");
                System.out.println("2 - Medio. Dati una data, un’ora di inizio ed un’ora di fine si vogliono conoscere i numeri di sala "
                + "per tutte le sale in cui è disponibile almeno un lettino.");
                System.out.println("3 - Difficile. Si vuole calcolare la busta paga di un dipendente a fine mese. "
                                + "Se il dipendente in questione è un Receptionist la busta paga corrisponde all'attributo 'Stipendio'; "
                                + "se il dipendente in questione è un Massaggiatore, la busta paga verrà calcolata come lo stipendio indicato nell'attributo 'StipendioBase' "
                                + "più il 30% del costo di tutti i massaggi eseguiti durante tale mese.");
                System.out.println("-1. Termina l'esecuzione");
                System.out.println("\nDefault - Estremo. Query bonus che permette di prenotare un massaggio dati un cliente, "
                                + "un tipo di massaggio, una data, un'ora di inizio del massaggio."
                                + "Permette inoltre di conoscere gli orari per i quali è possibile effettuare una prenotazione.");
                
                System.out.print("\n\nScelta: ");
                ans = sc.nextInt();
                sc.nextLine();
                switch (ans) {
                    case 1:
                        System.out.println(tipiMassaggioDiDurataMassima(conn));
                    break;
                    case 2:
                        System.out.println(saleDisponibili(conn));
                    break;
                    case 3:
                        System.out.println(stipendioDipendente(conn));
                    break;
                    default:
                        System.out.println(QueryPrenotazione.prenotaMassaggio(conn));
                }
                System.out.println("\nPremere invio per proseguire");
                sc.nextLine();
            } while (ans != -1);
        } catch (SQLException ex){
            System.err.println(ex.getMessage());
        }
    }
    
}
