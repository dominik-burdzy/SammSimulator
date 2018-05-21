
package pl.edu.agh.simulator.processor;

import java.sql.Timestamp;
import java.util.List;

import pl.edu.agh.simulator.db.MongoDatabaseManager;
import pl.edu.agh.simulator.db.HsqlDatabaseManager;
import pl.edu.agh.simulator.model.Measurement;

public class SammSimulator extends Thread {

	public static final String HSQL_SELECT_QUERY_EXPRESSION = "SELECT TIMESTAMP, VALUE FROM METRIC_VALUE " +
			"WHERE METRIC_URI = 'http://www.icsr.agh.edu.pl/samm_1.owl#ExpressionsQueueLengthMetric' AND  TIMESTAMP > ";
	public static final String RESOURCE_NAME = "expressionsQueueLength";

	public void run() {

		try {
			MongoDatabaseManager mongoManager = new MongoDatabaseManager();
			HsqlDatabaseManager hsqlDatabaseManager = new HsqlDatabaseManager();

			Timestamp lastSavedDate = new Timestamp(mongoManager.getLastSavedDate(RESOURCE_NAME).getTime());

			while(true) {

				List<Measurement> resources = hsqlDatabaseManager.query(HSQL_SELECT_QUERY_EXPRESSION + "\'" + lastSavedDate + "\'");

				if (resources.size() > 0) {
					for (Measurement resource : resources) {
						mongoManager.store(RESOURCE_NAME, resource.getDate(), resource.getValue());
						System.out.println("Inserted: " + resource.getValue() + " date: " + resource.getDate().toString());
					}

					lastSavedDate = new Timestamp(resources.get(resources.size() - 1).getDate().getTime());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Test thread for simulating SAMM
	public static void main(String[] args) {
		SammSimulator simulator2 = new SammSimulator();
		simulator2.start();
	}
}