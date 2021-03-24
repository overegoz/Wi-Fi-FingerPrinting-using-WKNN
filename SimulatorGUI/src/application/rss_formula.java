package application;
import java.util.List;

public class rss_formula {
	public static double rss(List<Integer> location, List<Integer> AP) {
		double euclDistance = 1.0/Math.sqrt(Math.pow(location.get(0)-AP.get(0),2) + Math.pow(location.get(1)-AP.get(1),2)) + Math.random()/10000.0;
		return euclDistance;
	}
}
