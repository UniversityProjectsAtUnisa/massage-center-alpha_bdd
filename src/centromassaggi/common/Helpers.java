package centromassaggi.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Helpers {

    public static String getMassaggiatori(Statement stm) {
        String s = "";
        try {
            String query = "SELECT codicefiscale FROM massaggiatore ORDER BY codicefiscale;";
            ResultSet rst = stm.executeQuery(query);

            s += "\n----------------";
            s += String.format("\n%-16s", "Massaggiatori");
            s += "\n----------------";
            while(rst.next()) {
                s += String.format("\n%-16s", rst.getString("codicefiscale"));
            }
            s += "\n----------------";
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return s;
    }


    public static String getDipendenti(Statement stm) {
        String s = "";
        try {
            String query = "SELECT codicefiscale FROM massaggiatore "
                         + "UNION "
                         + "SELECT codicefiscale FROM receptionist "
                         + "ORDER BY codicefiscale;";
            ResultSet rst = stm.executeQuery(query);

            s += "\n----------------";
            s += String.format("\n%-16s", "Dipendente");
            s += "\n----------------";
            while(rst.next()) {
                s += String.format("\n%-16s", rst.getString("codicefiscale"));
            }
            s += "\n----------------";
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return s;
    }


    public static String getTipiMassaggio(Statement stm) {
        String selectTipiMassaggio = String.format("SELECT Tipo AS TipoMassaggio, Prezzo, Durata, Macchinario FROM TipoMassaggio;");

        String result = "";
        try {
            ResultSet rst = stm.executeQuery(selectTipiMassaggio);
            result += "\n---------------------------------------------------------------";
            result += String.format("\n%-20s %-8s %-12s %20s", "TipoMassaggio", "Prezzo", "Durata (min)", "Macchinario");
            result += "\n---------------------------------------------------------------";
            while(rst.next()) {
                String macchinario = rst.getString("Macchinario");
                if(macchinario == null) {
                    macchinario = "nessuno";
                }
                result += String.format("\n%-20s %-8.2f %-12d %20s",
                        rst.getString("TipoMassaggio"), rst.getDouble("Prezzo"), rst.getInt("Durata"), macchinario);
            }
            result += "\n---------------------------------------------------------------";
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return result;
    }

    // FIXME: Questa stampa non verrà mai bene
    public static String getMassaggio(Statement stm, String cliente, LocalDate data, LocalTime ora) {
        String s = "";
        try {
            String query = String.format("SELECT M.DataMassaggio, M.OraInizio, M.OraFine, M.TipoMassaggio, M.Massaggiatore, M.Sala, T.Prezzo FROM Massaggio M "
            + "INNER JOIN TipoMassaggio T "
            + "ON M.TipoMassaggio = T.Tipo "
            + "WHERE Cliente='%s' AND DataMassaggio = '%s' AND OraInizio = '%s';",
            cliente, data, ora);
            ResultSet rst = stm.executeQuery(query);

                s += "\n--------------------------------------------------------------------------------------------------------";
                s += String.format("\n%-10s %-10s %-10s %-20s %-20s %-20s %8s", "Data", "Ora Inizio", "Ora Fine", "Tipo Massaggio", "Massaggiatore", "Sala", "Prezzo");
                s += "\n--------------------------------------------------------------------------------------------------------";
                while(rst.next()) {
                    s += String.format("\n%-10s %-10s %-10s %-20s %-20s %-20s %8.2f", 
                    rst.getString("datamassaggio"), rst.getString("orainizio"), rst.getString("orafine"), 
                    rst.getString("tipomassaggio"), rst.getString("massaggiatore"), rst.getString("sala"), rst.getDouble("prezzo"));
                }
                s += "\n--------------------------------------------------------------------------------------------------------";
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return s;
    }


    public static boolean tipoMassaggioContiene(Statement stm, String tipo) {

        String queryTipoMassaggioTipo = String.format(
            "SELECT * FROM TipoMassaggio WHERE Tipo = '%s';", 
            tipo);
        try {
            ResultSet rst = stm.executeQuery(queryTipoMassaggioTipo);
            return rst.next();
        } catch (SQLException ex) {
            return false;
        }
    }


    public static boolean clienteContiene(Statement stm, String codiceFiscale) {

        String queryClienteCodiceFiscale = String.format(
            "SELECT * FROM Cliente WHERE CodiceFiscale = '%s';", 
            codiceFiscale);
        try {
            ResultSet rst = stm.executeQuery(queryClienteCodiceFiscale);
            return rst.next();
        } catch (SQLException ex) {
            return false;
        }

    }


    public static void inserisciCliente(Statement stm, String codiceFiscale, String cognome,String nome, ArrayList<String> recapiti) {
        String insertRecapitoQuery = "insert into recapitocliente(cliente, telefono) values";
        while (!recapiti.isEmpty()) {
            insertRecapitoQuery += String.format("('%s', '%s'),", codiceFiscale, recapiti.remove(0));
        }
        insertRecapitoQuery = insertRecapitoQuery.substring(0, insertRecapitoQuery.length() - 1) + ';';
        
        try {
            stm.executeUpdate(insertRecapitoQuery);
            String insertClienteQuery = String.format(
                    "insert into cliente(codicefiscale,cognome,nome) " + "values('%s', '%s', '%s');", codiceFiscale,
                    cognome, nome);
            stm.executeUpdate(insertClienteQuery);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
