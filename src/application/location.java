package application;
import java.util.ArrayList;
import java.util.List;

public class location {
	private int x; //int or double???
	private int y;
	public static int maxLocation = 1000;
	
	public location() {
		x = 0; 
		y = 0;
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public List<Integer> getLocation() {
		List<Integer> location = new ArrayList<Integer>();
		location.add(x);
		location.add(y);
		return location;
	}
	
	public List<Integer> generateLocation(int i, int j) {
		List<Integer> location = new ArrayList<Integer>();
		location.add(i * 100 + 50);
		location.add(j * 100 + 50);
		return location;
	}
	
	public static List<Integer> randomizeLocation() {
		List<Integer> location = new ArrayList<Integer>();
		location.add((int)(Math.random()*(maxLocation + 1)));
		location.add((int)(Math.random()*(maxLocation + 1)));
		return location;
	}
	
	public static void printLocation(List<Integer> location) {
		System.out.println("Currect location:");
		System.out.println(location.get(0));
		System.out.println(location.get(1));
		System.out.println();
	}

}
