package pl.edu.agh.simulator.processor;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import pl.edu.agh.simulator.db.MongoDatabaseManager;
import pl.edu.agh.simulator.db.HsqlDatabaseManager;
import pl.edu.agh.simulator.miner.ExampleMinerFromFile;
import pl.edu.agh.simulator.model.Measurement;
import pl.edu.agh.simulator.model.TimePeriod;

public class MargedSimulator extends Thread {

	public static final String HSQL_SELECT_QUERY_EXPRESSION = "SELECT TIMESTAMP, VALUE FROM METRIC_VALUE " +
			"WHERE METRIC_URI = 'http://www.icsr.agh.edu.pl/samm_1.owl#ExpressionsQueueLengthMetric' AND  TIMESTAMP > ";
	public static final String RESOURCE_NAME = "expressionsQueueLength";

	private Timestamp lastSavedDate;

	public void run() {

		MongoDatabaseManager mongoManager = new MongoDatabaseManager();
		Random rand = new Random();
		double data = 10.0;

		try {
			String resource1 = "memory";
			String resource2 = "cpu usage";
			ExampleMinerFromFile exampleMinerFromFile1 = new ExampleMinerFromFile(resource1,
					new TimePeriod(TimeUnit.DAYS, 1), ExampleMinerFromFile.SIMPLE_DATA_EXAMPLE_FILE, 0);
			ExampleMinerFromFile exampleMinerFromFile2 = new ExampleMinerFromFile(resource2,
					new TimePeriod(TimeUnit.DAYS, 1), ExampleMinerFromFile.DENVER_GASOLINE, 1);

			HsqlDatabaseManager hsqlDatabaseManager = new HsqlDatabaseManager();

			setLastSavedDate(new Timestamp(mongoManager.getLastSavedDate(RESOURCE_NAME).getTime()));

			initDataExample(mongoManager, exampleMinerFromFile1, resource1, exampleMinerFromFile2, resource2);

			while (true) {

				processDataExample(mongoManager, exampleMinerFromFile1, resource1, exampleMinerFromFile2, resource2);

				processSammData(hsqlDatabaseManager, mongoManager);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initDataExample(MongoDatabaseManager mongoManager, ExampleMinerFromFile exampleMinerFromFile1, String resource1,
								 ExampleMinerFromFile exampleMinerFromFile2, String resource2) throws ParseException, IOException {
		// insert 500 memory values to the database, 200 cpu usage
		int n = 500;
		int k = 200;
		Date time = new Date();
		for (int i = n; i >= 1; i--) {
			double d = exampleMinerFromFile1.getNextValue();
			Date t = new Date(time.getTime() - i * 1000);
			mongoManager.store(resource1, t, d);
			System.out.println("[EXAMPLE] Inserted: "+resource1+" : " + d + " date: " + t.toString());
		}
		for (int i = k; i >= 1; i--) {
			double d2 = exampleMinerFromFile2.getNextValue();
			Date t2 = new Date(time.getTime() - i * 1000);
			mongoManager.store(resource2, t2, d2);
			System.out.println("Inserted: "+resource2+" : "  + d2 + " date: " + t2.toString());
		}

	}

	private void processDataExample(MongoDatabaseManager mongoManager, ExampleMinerFromFile exampleMinerFromFile1,
									String resource1, ExampleMinerFromFile exampleMinerFromFile2, String resource2) {
		// double d= data + 60.0*rand.nextDouble();
		double d = exampleMinerFromFile1.getNextValue();
		Date t = new Date();
		mongoManager.store(resource1, t, d);
		System.out.println("[EXAMPLE] Inserted: " + d + " date: " + t.toString());
		double d2 = exampleMinerFromFile2.getNextValue();
		mongoManager.store(resource2, t, d2);
		try {
			sleep(950);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void processSammData(HsqlDatabaseManager hsqlDatabaseManager, MongoDatabaseManager mongoManager) throws SQLException {
		List<Measurement> resources = hsqlDatabaseManager.query(HSQL_SELECT_QUERY_EXPRESSION + "\'" + getLastSavedDate() + "\'");

		if (resources.size() > 0) {
			for (Measurement resource : resources) {
				mongoManager.store(RESOURCE_NAME, resource.getDate(), resource.getValue());
				System.out.println("[SAMM] Inserted: " + resource.getValue() + " date: " + resource.getDate().toString());
			}

			setLastSavedDate(new Timestamp(resources.get(resources.size() - 1).getDate().getTime()));
		}
	}

	// Test thread for simulating SAMM
	public static void main(String[] args) {
		MargedSimulator simulator = new MargedSimulator();
		simulator.start();
	}

	public Timestamp getLastSavedDate() {
		return lastSavedDate;
	}

	public void setLastSavedDate(Timestamp lastSavedDate) {
		this.lastSavedDate = lastSavedDate;
	}
}
