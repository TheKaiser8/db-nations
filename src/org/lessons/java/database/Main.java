package org.lessons.java.database;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // parametri di connessione
        String url = "jdbc:mysql://localhost:3306/db_nations";
        String user = "";
        String password = "";

        Scanner scan = new Scanner(System.in);

        // prova apertura connessione al database
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // stampo il catalogo per verificare il nome del database a cui sono connesso
            System.out.println(connection.getCatalog());

            // chiedo all'utente di inserire una parola o parte di essa per effettuare una ricerca e filtrare i risultati
            System.out.print("Inserisci una parola o parte di essa per eseguire una ricerca: ");
            String parameter = scan.nextLine();

            // preparo lo statement SQL da eseguire
            String sql = """
                    SELECT cou.name as country_name, country_id, r.name as region_name, con.name as continent_name
                    FROM `countries` cou
                    JOIN regions r ON r.region_id = cou.region_id
                    JOIN continents con ON con.continent_id = r.continent_id
                    WHERE cou.name LIKE ?
                    ORDER BY cou.name;
                    """;
            // chiedo alla Connection di preparare lo statement
            try(PreparedStatement ps = connection.prepareStatement(sql)) {
                // settiamo il parametro della query
                ps.setString(1, "%" + parameter + "%"); // gli apici del LIKE li inserisce automaticamente il setString
                // eseguo lo statement che restituisce un ResultSet
                try(ResultSet rs = ps.executeQuery()) {
                    // ciclo sulle righe del ResultSet fino a quando esiste una riga succesiva
                    while (rs.next()) {
                        // per ogni riga prendo i valori delle singole colonne
                        String countryName = rs.getNString("country_name");
                        int countryId = rs.getInt("country_id");
                        String regionName = rs.getString("region_name");
                        String continentName = rs.getNString("continent_name");
                        // stampo l'output
                        System.out.println("Country: " + countryName + " - Id: " + countryId + " - Region: " + regionName + " - Continent: " + continentName);
                    }
                }
            }

            System.out.print("Inserisci l'Id di una nazione: ");
            String parameterId = String.valueOf(Integer.parseInt(scan.nextLine()));

            String sqlLangAndStat = """
                    SELECT cou.name as country_name, GROUP_CONCAT(DISTINCT l.language SEPARATOR ', ') AS spoken_languages, cs.year, cs.population, cs.gdp
                    FROM countries cou
                    JOIN country_stats cs ON cs.country_id = cou.country_id
                    JOIN country_languages cl ON cou.country_id = cl.country_id
                    JOIN languages l ON l.language_id = cl.language_id
                    WHERE cou.country_id = ? AND cs.year =
                        (SELECT MAX(year) as max_year
                        FROM country_stats
                        WHERE country_id = COU.country_id)
                    GROUP BY cs.year;
                    """;
            try(PreparedStatement ps = connection.prepareStatement(sqlLangAndStat)) {
                // settiamo il parametro della query
                ps.setInt(1, Integer.parseInt(parameterId)); // gli apici del LIKE li inserisce automaticamente il setString
                // eseguo lo statement che restituisce un ResultSet
                try(ResultSet rs = ps.executeQuery()) {
                    // ciclo sulle righe del ResultSet fino a quando esiste una riga succesiva
                    if (rs.next()) {
                        // per ogni riga prendo i valori delle singole colonne
                        String countryName = rs.getNString("country_name");
                        String spoken_languages = rs.getNString("spoken_languages");
                        int year = rs.getInt("year");
                        int population = rs.getInt("population");
                        BigDecimal gdp = rs.getBigDecimal("gdp");
                        // stampo l'output
                        System.out.println("Details for country: " + countryName);
                        System.out.println("Languages: " + spoken_languages);
                        System.out.println("Most recent stats");
                        System.out.println("Year: " + year);
                        System.out.println("Population: " + population);
                        System.out.println("GDP: " + gdp);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Non è stato possibile connettersi al database");
            e.printStackTrace();
        }

        scan.close();
    }
}
