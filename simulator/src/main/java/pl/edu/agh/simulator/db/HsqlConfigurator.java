package pl.edu.agh.simulator.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Dominik on 05.12.15.
 */
public class HsqlConfigurator {

    private String jdbc;
    private String url;
    private String username;
    private String password;

    public HsqlConfigurator() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties prop = new Properties();
        InputStream input = null;
        try {

            input = loader.getResourceAsStream("hsqldb.properties");
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        jdbc = prop.getProperty("jdbc");
        url = prop.getProperty("url");
        username = prop.getProperty("username");
        password = prop.getProperty("password");
    }

    public String getJdbc() {
        return jdbc;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
