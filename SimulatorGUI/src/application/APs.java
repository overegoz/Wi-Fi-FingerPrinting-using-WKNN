package application;
import java.util.ArrayList;
import java.util.List;

public class APs {
	public final int numberOfAPs = Configuration.NUM_WIFIs;
	public List<List<Integer>> locationOfAPs = new ArrayList<List<Integer>>();

	public int randomizeLocation() {
		return (int)(Math.random()*(location.maxLocation + 1)); //int or double???
	}
	
	public void randomizeAPs() {
		for(int n = 0; n < numberOfAPs; n++) {
			locationOfAPs.add(new ArrayList<Integer>());
			locationOfAPs.get(n).add(randomizeLocation()); //x
			locationOfAPs.get(n).add(randomizeLocation()); //y
		}
		//print();
	}
	
	public List<Integer> locationOfAP (int n) { return locationOfAPs.get(n); }
	
	public void print() {
		System.out.println("Locations of APs");
		for(int i = 0; i < numberOfAPs; i++) {
			for(int j = 0; j < 2; j++) System.out.printf("%5d", locationOfAPs.get(i).get(j));
			System.out.println();
		}
		System.out.println("\n");
	}
}
