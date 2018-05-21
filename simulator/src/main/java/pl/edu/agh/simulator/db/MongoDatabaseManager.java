package pl.edu.agh.simulator.db;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MongoDatabaseManager {
	
	private MongoDatabase db;
	
	public MongoDatabaseManager() {
		MongoDatabaseConfigurator dbConfig = new MongoDatabaseConfigurator();
		//Lets try to connect
		@SuppressWarnings("resource")
		MongoClient mongoClient = new MongoClient(dbConfig.getHost(), dbConfig.getPort());
		db = mongoClient.getDatabase(dbConfig.getName());
	}
	
//	public void store(MeasurementValue value) {
//		store("", value.getTimestamp(), (double)value.getValue());
//	}
	
	public void store(String resource, Date date, double value) {
		db.getCollection(MongoDatabaseConfigurator.MEASUREMENTS_DB_NAME).insertOne(
		        new Document("resource", resource)
		        	.append("date", date)
		        	.append("val", value)
		);
	}


	public Date getLastSavedDate(String resource) {

		final List<Date> result = new ArrayList<>();
		FindIterable<Document> iterable = db.getCollection(MongoDatabaseConfigurator.MEASUREMENTS_DB_NAME)
				.find(new Document("resource", resource));

		iterable.forEach(
				new Block<Document>() {
					@Override
					public void apply(final Document document) {
						result.add(document.getDate("date"));
					}
				});
		if (result.size() == 0)
			return new Date(0);
		
		return result.get(result.size() - 1);
	}
	
}