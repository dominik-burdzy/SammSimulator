package pl.edu.agh.simulator.processor;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import pl.edu.agh.simulator.db.MongoDatabaseManager;
import pl.edu.agh.simulator.miner.ExampleMinerFromFile;
import pl.edu.agh.simulator.model.TimePeriod;


public class ExampleDataSimulator extends Thread {

	public void run() {

		MongoDatabaseManager manager = new MongoDatabaseManager();
		Random rand = new Random();
		double data = 10.0;

		try {
			String resource1 = "memory";
			String resource2 = "cpu usage";
			ExampleMinerFromFile exampleMinerFromFile1 = new ExampleMinerFromFile(resource1,
					new TimePeriod(TimeUnit.DAYS, 1), ExampleMinerFromFile.SIMPLE_DATA_EXAMPLE_FILE, 0);
			ExampleMinerFromFile exampleMinerFromFile2 = new ExampleMinerFromFile(resource2,
					new TimePeriod(TimeUnit.DAYS, 1), ExampleMinerFromFile.DENVER_GASOLINE, 1);
			// insert 500 memory values to the database, 200 cpu usage
			int n = 500;
			int k = 200;
			Date time = new Date();
			for (int i = n; i >= 1; i--) {
				double d = exampleMinerFromFile1.getNextValue();
				Date t = new Date(time.getTime() - i * 1000);
				manager.store(resource1, t, d);
				 System.out.println("Inserted: "+resource1+" : " + d + " date: " + t.toString());
			}
			for (int i = k; i >= 1; i--) {
				double d2 = exampleMinerFromFile2.getNextValue();
				Date t2 = new Date(time.getTime() - i * 1000);
				manager.store(resource2, t2, d2);
				 System.out.println("Inserted: "+resource2+" : "  + d2 + " date: " + t2.toString());
			}
			while (true) {
				// double d= data + 60.0*rand.nextDouble();
				double d = exampleMinerFromFile1.getNextValue();
				Date t = new Date();
				manager.store(resource1, t, d);
				System.out.println("Inserted: "+resource1+" : " + d + " date: " + t.toString());
				double d2 = exampleMinerFromFile2.getNextValue();
				manager.store(resource2, t, d2);
				System.out.println("Inserted: "+resource2+" : "  + d2 + " date: " + t.toString());
				try {
					sleep(950);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (NumberFormatException | IOException | ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// Test thread for simulating SAMM
	public static void main(String[] args) {
		ExampleDataSimulator simulator = new ExampleDataSimulator();
		simulator.start();
	}
}
