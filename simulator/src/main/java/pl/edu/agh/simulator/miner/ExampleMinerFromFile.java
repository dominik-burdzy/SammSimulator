package pl.edu.agh.simulator.miner;

import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.opencsv.CSVReader;

import pl.edu.agh.simulator.model.TimePeriod;
import pl.edu.agh.simulator.model.Value;

public class ExampleMinerFromFile extends Miner {

	private static String DATA_LOCATION = "./simulator/res/";
	public static String SIMPLE_DATA_EXAMPLE_FILE = "ALMA.csv";
	public static String SEASONAL_DATA_EXAMPLE_FILE = "SeasonalDataExample.csv";
	public static String DENVER_GASOLINE = "Weekly_Denver_Regular_All_Formulations_Retail_Gasoline_Prices.csv";
	
	private ArrayList<Double> exampleRawData;
	private int positionOfActualValues = 0; // keeps the position of probes in
											// the list
	private int actualPosition = 0; // position
	private char separator =';';
	
	private static HashMap<String,ExampleMinerFromFile> fileMinersMap= new HashMap<String,ExampleMinerFromFile>();

	public ExampleMinerFromFile(String resourceName,TimePeriod timeUnit, String fileName, int nrOfDataColumn)
			throws NumberFormatException, IOException, ParseException {
		super(resourceName, timeUnit);
		loadExampleDataFile(fileName, nrOfDataColumn);
		fileMinersMap.put(resourceName, this);
	}
	
	public ExampleMinerFromFile(String resourceName,TimePeriod timeUnit, String fileName, int nrOfDataColumn, char separator)
			throws NumberFormatException, IOException, ParseException {
		super(resourceName, timeUnit);
		this.separator=separator;
		loadExampleDataFile(fileName, nrOfDataColumn);
		fileMinersMap.put(resourceName, this);
	}
	
	public double[] getNextValues(int n) {
		double[] values = new double[n];
		for (int i = 0; i < n; i++) {
			int k = (actualPosition + i) % exampleRawData.size();
			values[i] = exampleRawData.get(k);
		}
		return values;
	}

	@Override
	public Map<Date, Value> putValuesIntoDates(Value[] rawValues) {
		// TODO Auto-generated method stub
		return null;
	}

	public double getNextValue() {
		double value = exampleRawData.get(actualPosition);
		if (actualPosition < exampleRawData.size() - 1) {
			actualPosition++;
		} else {
			actualPosition = 0;
		}
		return value;
	}

	public double[] getNextRawValues() {
		double[] values = new double[this.getNrOfObservedValues()];
		int min = Math.min(this.getNrOfObservedValues(),
				exampleRawData.size() - (this.getNrOfObservedValues() + positionOfActualValues));
		if (min < 0) {
			min = 0;
		}
		for (int i = 0; i < min; i++) {
			values[i] = exampleRawData.get(positionOfActualValues + i).doubleValue();
		}

		for (int i = min; i < this.getNrOfObservedValues(); i++) {
			values[i] = exampleRawData.get(i - min).doubleValue();
		}
		positionOfActualValues++;
		if (positionOfActualValues > exampleRawData.size()) {
			positionOfActualValues = 0;
		}
		return values;
	}

	public double[] getRealValuesForPrediction(int n) {
		int k = Math.min(n, exampleRawData.size() - (n + positionOfActualValues + this.getNrOfObservedValues()));
		double[] values = new double[n];
		if (k < 0) {
			k = 0;
		}
		for (int i = 0; i < k; i++) {
			values[i] = exampleRawData.get(positionOfActualValues + this.getNrOfObservedValues() + i).doubleValue();
		}
		for (int i = k; i < n; i++) {
			values[i] = exampleRawData.get(1 + i - k).doubleValue();
		}
		return values;
	}

	private void loadExampleDataFile(String fileName, int columnDataNumber)
			throws NumberFormatException, IOException, ParseException {
		NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
		@SuppressWarnings("resource")
		CSVReader reader = new CSVReader(new FileReader(DATA_LOCATION + fileName), separator);
		reader.readNext();// header line
		reader.readNext(); // column names
		exampleRawData = new ArrayList<Double>();
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			Number number = format.parse(nextLine[columnDataNumber]);
			double d = number.doubleValue();
			exampleRawData.add(d);
		}
	}

	public ArrayList<Double> getExampleRawData() {
		return exampleRawData;
	}

	public void setExampleRawData(ArrayList<Double> exampleRawData) {
		this.exampleRawData = exampleRawData;
	}

	@Override
	public double[] getValues() {
		return getNextRawValues();
	}

	public static HashMap<String, ExampleMinerFromFile> getFileMinersMap() {
		return fileMinersMap;
	}
	
	
}
