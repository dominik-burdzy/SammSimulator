package pl.edu.agh.simulator.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.simulator.model.Measurement;

import java.util.Date;

/**
 * Created by Dominik on 05.12.15.
 */
public class HsqlDatabaseManager {

    Connection connection;
    HsqlConfigurator hsqlConfigurator = new HsqlConfigurator();

    public HsqlDatabaseManager() throws Exception {

        Class.forName(hsqlConfigurator.getJdbc());
        connection = DriverManager.getConnection(hsqlConfigurator.getUrl(),
                hsqlConfigurator.getUsername(), hsqlConfigurator.getPassword());
    }

    public void shutdown() throws SQLException {

        Statement st = connection.createStatement();

        st.execute("SHUTDOWN");
        connection.close();
    }

    //use for SQL command SELECT
    public synchronized List<Measurement> query(String expression) throws SQLException {

        Statement st = null;
        ResultSet rs = null;

        st = connection.createStatement();
        rs = st.executeQuery(expression);

        List<Measurement> resources = getResources(rs);

        st.close();

        return resources;
    }

    //use for SQL commands CREATE, DROP, INSERT and UPDATE
    public synchronized void update(String expression) throws SQLException {

        Statement st = null;

        st = connection.createStatement();

        int i = st.executeUpdate(expression);

        if (i == -1) {
            System.out.println("db error : " + expression);
        }

        st.close();
    }

    private List<Measurement> getResources(ResultSet rs) throws SQLException {

        List<Measurement> resources = new ArrayList<>();

        while (rs.next()) {
            Date date = new Date(rs.getTimestamp("TIMESTAMP").getTime());
            Double value = (double) rs.getFloat("VALUE");

            resources.add(new Measurement(date, value));
        }

        return resources;
    }
}
