package application;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class radio_map {
	//deciding how many blocks a radio map will have
	final static int i_block = 10; //rows
	final static int j_block = 10; //columns
	final static int numberOfMeasurements = 5;
	public List<List<List<Double>>> radioMap = new ArrayList<List<List<Double>>>();
	public APs APs = new APs();
	
	private void mapInitialization() {
		for(int i = 0; i < i_block; i++) {
			radioMap.add(new ArrayList<List<Double>>());
			for(int j = 0; j < j_block; j++) {
				radioMap.get(i).add(new ArrayList<Double>());
			}
		}
	}
	
	public void mapping() {
		rss_formula rss = new rss_formula();
		
		location tempLocation = new location();
		
		List<Double> median = new ArrayList<Double>();
		
		APs.randomizeAPs(); //randomizes locations of 5 different APs
		mapInitialization();
		for(int i = 0; i < i_block; i++) {
			for(int j = 0; j < j_block; j++) {
				for(int n = 0; n < APs.numberOfAPs; n++) {
					for(int l = 0; l < numberOfMeasurements; l++) {
						median.add(rss.rss(tempLocation.generateLocation(i, j), APs.locationOfAP(n)));
					}
					median = median.stream().sorted().collect(Collectors.toList());
					double tempRSS = median.get(median.size()/2);
					if(median.size() % 2 == 0) tempRSS = (median.get((median.size()-1)/2) + median.get(median.size()/2))/2;
					radioMap.get(j).get(i).add(tempRSS);
					median.clear();
				}
			}
		}
	}
}

