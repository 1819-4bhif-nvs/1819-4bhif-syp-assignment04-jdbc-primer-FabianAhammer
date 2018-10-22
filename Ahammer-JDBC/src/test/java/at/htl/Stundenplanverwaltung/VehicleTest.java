package at.htl.vehicle;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sun.security.util.Password;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class VehicleTest {
    public static final String DRIVER_STRING = "org.apache.derby.jdbc.ClientDriver";
    public static final String CONNECTION_STRING = "jdbc:derby://localhost:1527/db";
    public static final String USER = "app";
    public static final String PASSWORD = "app";
    private static Connection conn;

    @BeforeClass
    public static void initJDBC() {
        try {
            Class.forName(DRIVER_STRING);
            conn = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Verbindung zu Datenbank nicht möglich\n" + e.getMessage() + "\n");
            System.exit(1);
        }

        try {
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE lehrer (" +
                    "id INT CONSTRAINT pk_lehrer PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL" +
                    ")";
            stmt.execute(sql);
            sql = "INSERT INTO lehrer (id, name) VALUES (1, 'Prof. Martin Klein')";
            stmt.execute(sql);
            sql = "INSERT INTO lehrer (id, name) VALUES (2, 'Dipl. Ing. Peter Bauer')";
            stmt.execute(sql);
            sql = "INSERT INTO lehrer (id, name) VALUES (3, 'Prof. Stephan Weiss')";
            stmt.execute(sql);

            System.out.println("Lehrer Tabelle initialisiert");


            sql = "CREATE TABLE schueler (" +
                    "id INT CONSTRAINT pk_schueler PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL" +
                    ")";
            stmt.execute(sql);
            sql = "INSERT INTO schueler (id, name) VALUES (1, 'Mayerhofer Max')";
            stmt.execute(sql);
            sql = "INSERT INTO schueler (id, name) VALUES (2, 'Schoenegger Paul')";
            stmt.execute(sql);
            sql = "INSERT INTO schueler (id, name) VALUES (3, 'Winter Elias')";
            stmt.execute(sql);

            System.out.println("Schueler Tabelle initialisiert");

            sql = "CREATE TABLE stunde (" +
                    "id INT CONSTRAINT pk_schueler PRIMARY KEY," +
                    "fach VARCHAR(255)," +
                    "anfang date," +
                    "ende date," +
                    "lehrer_id INT CONSTRAINT FK_lehrer_id REFERENCES lehrer(id)," +
                    "abwesenderschueler INT FK_schueler_id REFERENCES schueler(id)"+
                    ")";
            stmt.execute(sql);

            Date dt = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DATE, 1);
            Date da = c.getTime();

            sql = "INSERT INTO stunde (id, fach,anfang,ende,lehrer_id,abwesenderschueler) VALUES (1, 'Mathematik',dt,da,1,1)";
            stmt.execute(sql);
            sql = "INSERT INTO stunde (id, fach,anfang,ende,lehrer_id,abwesenderschueler) VALUES (2, 'NVS',dt,da,3,1)";
            stmt.execute(sql);
            sql = "INSERT INTO stunde (id, fach,anfang,ende,lehrer_id,abwesenderschueler) VALUES (3, 'SYP',dt,da,2,1)";
            stmt.execute(sql);

            System.out.println("Stunde Tabelle initialisiert");
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    @AfterClass
    public static void teardownJDBC(){
        try {
            if(conn != null && !conn.isClosed()){
                conn.close();
                System.out.println("Good Bye");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLehrer(){
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT  name FROM lehrer");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            assertThat(rs.getString("name"),is("Prof. Martin Klein"));
            rs.next();
            assertThat(rs.getString("name"),is("Dipl. Ing. Peter Bauer"));
            rs.next();
            assertThat(rs.getString("name"),is("Prof. Stephan Weiss"));

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    @Test
    public void testSchueler(){
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT  name FROM schueler");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            assertThat(rs.getString("name"),is("Mayerhofer Max"));
            rs.next();
            assertThat(rs.getString("name"),is("Schoenegger Paul"));
            rs.next();
            assertThat(rs.getString("name"),is("Winter Elias"));

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testStunde(){
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT  fach FROM stunde");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            assertThat(rs.getString("name"),is("Mathematik"));
            rs.next();
            assertThat(rs.getString("name"),is("NVS"));
            rs.next();
            assertThat(rs.getString("name"),is("SYP"));

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testMetaDataLehrer() {
        try {
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            String catalog = null;
            String schemaPattern = null;
            String tableNamePattern = "Lehrer";
            String columnNamePattern = null;
            ResultSet rs = databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
            rs.next();
            String columnName = rs.getString(4);
            int columnType = rs.getInt(5);
            assertThat(columnName, is("ID"));
            assertThat(columnType, is(Types.INTEGER));
            rs.next();
            columnName = rs.getString(4);
            columnType = rs.getInt(5);
            assertThat(columnName, is("NAME"));
            assertThat(columnType, is(Types.VARCHAR));
            String schema = null;
            String tableName = "Lehrer";
            rs = databaseMetaData.getPrimaryKeys(
                    catalog, schema, tableName);
            rs.next();
            columnName = rs.getString(4);
            assertThat(columnName, is("ID"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMetaDataSchueler() {
        try {
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            String catalog = null;
            String schemaPattern = null;
            String tableNamePattern = "Schueler";
            String columnNamePattern = null;
            ResultSet rs = databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
            rs.next();
            String columnName = rs.getString(4);
            int columnType = rs.getInt(5);
            assertThat(columnName, is("ID"));
            assertThat(columnType, is(Types.INTEGER));
            rs.next();
            columnName = rs.getString(4);
            columnType = rs.getInt(5);
            assertThat(columnName, is("NAME"));
            assertThat(columnType, is(Types.VARCHAR));
            String schema = null;
            String tableName = "Schueler";
            rs = databaseMetaData.getPrimaryKeys(
                    catalog, schema, tableName);
            rs.next();
            columnName = rs.getString(4);
            assertThat(columnName, is("ID"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
