package org.lessons.java.database;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        // parametri di connessione
        String url = "jdbc:mysql://localhost:3306/db_nations";
        String user = "";
        String password = "";

        // prova apertura connessione al database
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // stampo il catalogo per verificare il nome del database a cui sono connesso
            System.out.println(connection.getCatalog());

            // preparo lo statement SQL da eseguire
            String sql = """
                    SELECT cou.name as country_name, country_id, r.name as region_name, con.name as continent_name
                    FROM `countries` cou
                    JOIN regions r ON r.region_id = cou.region_id
                    JOIN continents con ON con.continent_id = r.continent_id
                    ORDER BY cou.name;
                    """;
            // chiedo alla Connection di preparare lo statement
            try(PreparedStatement ps = connection.prepareStatement(sql)) {
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
        } catch (SQLException e) {
            System.out.println("Non è stato possibile connettersi al database");
            e.printStackTrace();
        }
    }
}
