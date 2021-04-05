package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Controller implements Initializable {
	Image wifi = new Image("/wifi.png");
	Image pin1 = new Image("/pin.png");
	Image pin2 = new Image("/pin2.png");
	@FXML Button start;
	@FXML Canvas canvas;
	@FXML Canvas grid;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		GraphicsContext context = grid.getGraphicsContext2D();
		GraphicsContext explanation = canvas.getGraphicsContext2D();
		drawOnCanvas(context, explanation);
		start.setOnAction(event -> {
			drawOnCanvas(context, explanation);
			buttonStartPressed();
		});
	}
	
	public void drawOnCanvas(GraphicsContext context, GraphicsContext explanation) {
		context.clearRect(0, 0, 408, 408);
		explanation.clearRect(0, 0, 220, 380);
		
		explanation.setFill(Color.WHITE);
		explanation.fillRoundRect(0, 0, 298, 380, 10, 10);
		
		for(int row = 0; row < 10; row++) {
			for(int col = 0; col < 10; col++) {
				int position_y = row * 38 + 2;
				int position_x = col * 38 + 2;
				int width = 34;
				context.setFill(Color.WHITE);
				context.fillRoundRect(position_x + 14, position_y + 14, width, width, 10, 10);
			}
		}
	}
	
	public void drawCurrentLocation(GraphicsContext context, List<Integer> currentLocation) {
		System.out.printf("\n(Actual) Current Location: %d, %d  [Controller.java]\n", 
						  currentLocation.get(0), currentLocation.get(1));
		double position_x = currentLocation.get(0) * 380.0 / 1000.0;
		double position_y = currentLocation.get(1) * 380.0 / 1000.0;

		double width = 32;
		context.drawImage(pin1, position_x - 16 + 16, position_y - 32 + 16, width, width);
	}
	
	public void drawEstimatedLocation(GraphicsContext context, List<Double> estimatedLocation) {
		//System.out.println("Estimated Location:" + estimatedLocation.get(0) + " " + estimatedLocation.get(1));
		System.out.printf("(Proposed)Estimated Location: %.2f, %.2f [Controller.java]\n", estimatedLocation.get(0), estimatedLocation.get(1));
		double position_x = estimatedLocation.get(0) * 380.0 / 1000.0;
		double position_y = estimatedLocation.get(1) * 380.0 / 1000.0;
		
		double width = 32;
		context.drawImage(pin2, position_x - 16 + 16, position_y - 32 + 16, width, width);
	}
	
	public void colorChosenBlocks(GraphicsContext context, Map<Double, String> nearestBlocks) {
		for(Map.Entry<Double, String> first: nearestBlocks.entrySet()) {
			String block = first.getValue();
			int position_y = Integer.parseInt(block.substring(block.indexOf(',') + 1)) * 38 + 2;
			int position_x = Integer.parseInt(block.substring(0, block.indexOf(','))) * 38 + 2;
			int width = 34;
			context.setFill(new Color(1.0, 0, 0, 0.5));
			context.fillRoundRect(position_x + 14, position_y + 14, width, width, 10, 10);
		}
	}
	
	public void drawAPs(GraphicsContext context, List<List<Integer>> locationOfAPs) {
		for(int i = 0; i < locationOfAPs.size(); i++) {
			//System.out.println("AP Location:" + locationOfAPs.get(i).get(0) + " " + locationOfAPs.get(i).get(1));
			double position_x = locationOfAPs.get(i).get(0) * 380.0 / 1000.0;
			double position_y = locationOfAPs.get(i).get(1) * 380.0 / 1000.0;
				
			double width = 16;
			context.drawImage(wifi, position_x - 8 + 16, position_y - 8 + 16, width, width);
		}
	}
	public double getEucDist(List<Integer> currentLocation, List<Double> estimatedLocation) {
		double realX = currentLocation.get(0) * 1.0;
		double realY = currentLocation.get(1) * 1.0;
		double estX = estimatedLocation.get(0) * 1.0;
		double estY = estimatedLocation.get(1) * 1.0;
		
		return Math.sqrt(
				 Math.pow(realX-estX, 2)
				 + Math.pow(realY-estY, 2)
			     ); 
	}
	public void buttonStartPressed() {
		RTUser_localization localization = new RTUser_localization();
		localization.localization();
		
		localization.WKNN();
		colorChosenBlocks(grid.getGraphicsContext2D(), localization.nearestBlocks);
		drawAPs(grid.getGraphicsContext2D(), localization.radio_map.APs.locationOfAPs);
		drawCurrentLocation(grid.getGraphicsContext2D(), localization.currentLocation);
		drawEstimatedLocation(grid.getGraphicsContext2D(), localization.estimatedLocation);

		double errorDistance = getEucDist(localization.currentLocation, localization.estimatedLocation);
		System.out.printf("(Est error) Estimation error: %.2f [Controller.java]\n", errorDistance);
	}
}
