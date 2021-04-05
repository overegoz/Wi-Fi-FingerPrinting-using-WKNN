package application;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RTUser_localization {
	//private final int numberOfMeasurements = 5;
	private final int K = Configuration.K; //K in WKNN
	private final double INF = 100000;
	private List<Double> currentRSS = new ArrayList<Double>();
	public List<Integer> currentLocation = new ArrayList<Integer>();
	public List<Double> estimatedLocation = new ArrayList<Double>();
	private List<List<Double>> coefficient = new ArrayList<List<Double>>();
	public Map<Double, String> nearestBlocks = new HashMap<Double, String>();
	private Map<Double, String> WKNN = new HashMap<Double, String>();
	private location location = new location();
	public radio_map radio_map = new radio_map();
	
	
	public void localization() {
		radio_map.mapping();
		currentLocation = location.randomizeLocation();
		for(int n = 0; n < radio_map.APs.numberOfAPs; n++) {
			currentRSS.add(rss_formula.rss(currentLocation, radio_map.APs.locationOfAPs.get(n)));
		}
		
		for(int i = 0; i < radio_map.i_block; i++) {
			for(int j = 0; j < radio_map.j_block; j++) {
				coefficient.add(new ArrayList<Double>());
				double tempCoefficient = 0;
				for(int n = 0; n < radio_map.APs.numberOfAPs; n++) {
					tempCoefficient += Math.abs(radio_map.radioMap.get(j).get(i).get(n) - currentRSS.get(n)); 
				}
				coefficient.get(i).add(tempCoefficient);
			}
		}
	}
	
	public boolean skip(int i, int j) {
		Map<Double, String> tempBlocks = new HashMap<Double, String>();
		tempBlocks.putAll(nearestBlocks);
		int counter = nearestBlocks.size();
		//iterate through each block in nearestBlocks
		for(Map.Entry<Double, String> first: tempBlocks.entrySet()) {
			if(counter <= 1) break;
			counter--;
			String temp1 = first.getValue();
			for(Map.Entry<Double, String> second: tempBlocks.entrySet()) {
				String temp2 = second.getValue();
				if(temp1 == temp2 || temp2 == null) continue;
				int indexI1 = Integer.parseInt(temp1.substring(0, temp1.indexOf(',')));
				int indexI2 = Integer.parseInt(temp2.substring(0, temp2.indexOf(',')));
				int indexJ1 = Integer.parseInt(temp1.substring(temp1.indexOf(',') + 1));
				int indexJ2 = Integer.parseInt(temp2.substring(temp2.indexOf(',') + 1));
				//case 1: there are two repeating cell numbers (ex. {1, 2} and {1, 3} OR {3, 4} and {2, 4})
				if(indexI1 == indexI2 || indexJ1 == indexJ2) {
					if(indexI1 == i || indexJ1 == j) {
						return true;
					}
				}
				
				//case 2: third point makes a descending or ascending line
				//ascending
				if(indexI1 + indexJ1 == indexI2 + indexJ2) {
					if(j == indexI1 + indexJ1 - i) {
						return true;
					}
				}
				//descending
				if(indexI1 - indexJ1 == indexI2 - indexJ2) {
					if(j == Math.abs(indexI1 - indexJ1 - i)) {
						return true;
					}
				}
				
				//case 3: a non-straightforward line
				List<Integer> firstLocation = location.generateLocation(indexI1, indexJ1);
				List<Integer> secondLocation = location.generateLocation(indexI2, indexJ2);
				List<Integer> currentCellLocation = location.generateLocation(i, j);
				//formula: ax + by = c
				int a, b, c = 0;
				a = firstLocation.get(1) - secondLocation.get(1);
				b = firstLocation.get(0) - secondLocation.get(0);
				c = a * firstLocation.get(0) + b * firstLocation.get(1);
				if(a * currentCellLocation.get(0) + b * currentCellLocation.get(1) == c) {
					return true;
				}
				
			}
			tempBlocks.replace(first.getKey(), null);
		}
		return false;
	}
	
	public void WKNN() {
		//finding closest blocks
		for(int k = 0; k < K; k++) {
			double min = INF;
			int indexI = 0;
			int indexJ = 0;
			for(int i = 0; i < radio_map.i_block; i++) {
				for(int j = 0; j < radio_map.j_block; j++) {
					if(min > coefficient.get(i).get(j)) {
						//check if on the same line
						if (nearestBlocks.size() >= 2) {
							if(Configuration.SKIP_SOME_POINTS_ON_THE_LINE) if(skip(i, j) == true) continue;
						}
						min = coefficient.get(i).get(j);
						indexI = i;
						indexJ = j;
					}
				}
			}
			nearestBlocks.put(min, indexI + "," + indexJ);
			coefficient.get(indexI).set(indexJ, INF);
		}
		
//		System.out.println("\nClosest blocks:");
//		System.out.println(nearestBlocks);
		
		//estimate the position
		double sumOfW = 0;
		double tempW = 0;
		String tempBlock = "";
		List<Integer> tempLocation = new ArrayList<Integer>();
		
		for(Map.Entry<Double, String> entry: nearestBlocks.entrySet())
		sumOfW += 1.0 / entry.getKey();
		
		for(Map.Entry<Double, String> entry: nearestBlocks.entrySet()) {
			tempW = (1.0/entry.getKey()) / sumOfW;
			tempBlock = entry.getValue();
			
			int x = Integer.parseInt(tempBlock.substring(0, tempBlock.indexOf(',')));
			int y = Integer.parseInt(tempBlock.substring(tempBlock.indexOf(',') + 1));
			tempLocation = location.generateLocation(x, y);
			WKNN.put(tempW, tempLocation.get(0) + "," + tempLocation.get(1));
		}
		
//		System.out.println("\n WKNN: ");
//		System.out.println(WKNN);
		String tempPosition = "";
		double tempX = 0, tempY = 0;
		
		for(Map.Entry<Double, String> entry: WKNN.entrySet()) {
			tempW = entry.getKey();
			tempPosition = entry.getValue();
			double positionX = Double.parseDouble(tempPosition.substring(0, tempPosition.indexOf(',')));
			double positionY = Double.parseDouble(tempPosition.substring(tempPosition.indexOf(',') + 1));
			tempX += tempW * positionX;
			tempY += tempW * positionY;
		}
		estimatedLocation.add(tempX);
		estimatedLocation.add(tempY);
//		System.out.println("\nEstimated location: ");
//		System.out.println(estimatedLocation[0] + " " + estimatedLocation[1]);
	}
	
	public void print() {
		location.printLocation(currentLocation);
		
		System.out.println("\nCurrent RSS: ");
		for(int i = 0; i < currentRSS.size(); i++) {
			System.out.printf("%-15f", currentRSS.get(i));
		} System.out.println("\n\n\n");
		
		System.out.println("\nCoefficients corresponding to each block:");
		for(int i = 0; i < radio_map.i_block; i++) {
			for(int j = 0; j < radio_map.j_block; j++) {
				System.out.printf("%-15f", coefficient.get(j).get(i));
			}
			System.out.println();
		}
	}
	
}
