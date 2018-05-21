package pl.edu.agh.simulator.db;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;

public class MongoDatabaseConfigurator {

	public static DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
	public static String MEASUREMENTS_DB_NAME = "measurements";
	public static String RESULTS_DB_NAME = "results";
		
	private int dbport;
	private String dbhost;
	private String dbname;
	
	public MongoDatabaseConfigurator() {
		// Lets get configuration settings
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = loader.getResourceAsStream("mongodb.properties");
			prop.load(input);
			dbhost = prop.getProperty("dbhost");
			dbport = Integer.parseInt(prop.getProperty("dbport"));
			dbname = prop.getProperty("dbname");
			
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
	}
	
	public int getPort() {
		return dbport;
	}
	
	public String getHost() {
		return dbhost;
	}
	
	public String getName() {
		return dbname;
	}
	
	public void setRScriptPath(String str) {
		setProperty("rscript", str);
	}
	
	public void setRExePath(String str) {
		setProperty("rexe", str);
	}
	
	private void setProperty(String name, String value) {
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		OutputStream output = null;

		try {
			URL url = loader.getResource("db.properties");
			output = new FileOutputStream(new File(url.toURI()));

			// set the properties value
			prop.setProperty(name, value);

			// save properties to project root folder
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
