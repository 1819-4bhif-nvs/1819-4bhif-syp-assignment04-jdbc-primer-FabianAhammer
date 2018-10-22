package at.htl.vehicle;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sun.security.util.Password;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.fail;

public class VehicleTest {
    public static final String DRIVER_STRING = "org.apache.derby.jdbc.ClientDriver";
    public static final String CONNECTION_STRING = "jdbc:derby://localhost:1527/db";
    public static final String USER = "app";
    public static final String PASSWORD = "app";
    private static Connection conn;

    @BeforeClass
    public static void initJDBC(){
        try {
            Class.forName(DRIVER_STRING);
            conn = DriverManager.getConnection(CONNECTION_STRING,USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Verbindung zu Datenbank nicht möglich\n" + e.getMessage() + "\n");
            System.exit(1);
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
    public void ddl(){
        try {
            Statement stmt = conn.createStatement();

        String sql = "CREATE TABLE vehicle (" +
                "id INT CONSTRAINT vehicle_pk PRIMARY KEY," +
                "brand VARCHAR(255) NOT NULL," +
                "type VARCHAR(255) NOT NULL" +
                ")";

        stmt.execute(sql);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


}
