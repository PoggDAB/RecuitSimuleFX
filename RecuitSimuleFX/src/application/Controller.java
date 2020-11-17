package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import Model.AlgoRecuitSimule;
import Model.City;
import Model.Pair;
import Model.ParserTSP;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller implements Initializable {
	@FXML
	private Button startTSPButton;
	
	@FXML
	private Button fileButton;
	
	@FXML 
	private Button showCitiesButton;
	
	@FXML
	private TextField chosenFileText;
	
	@FXML
	private Canvas canvas;
	
	@FXML 
	private Text textSolution;
	
	private HashMap<Integer, City> hashMapCities;
	
	//Min and Max for the X coordinates and Y coordinates of cities
	double minCityX;
	double minCityY;
	
	double maxCityX;
	double maxCityY;
	
	//ArrayList of Pair which represent the solution
	ArrayList<Pair> solution;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		initCanvas();
	}
	
	@FXML 
	public void openFile(ActionEvent event) throws FileNotFoundException {
		
		System.out.println("Button file clicked !");
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open a TSP file");
		
		ExtensionFilter extFilter = new ExtensionFilter("TSP Files (*.tsp)", "*.tsp");
		fileChooser.getExtensionFilters().add(extFilter);
		
		Node node = (Node) event.getSource();
		File file = fileChooser.showOpenDialog(node.getScene().getWindow());
		System.out.println("file openned : " + file.getAbsolutePath());
		
		hashMapCities = ParserTSP.read(file.getAbsolutePath());
		System.out.println("Cities imported ! ");
		
		chosenFileText.setText(file.getName());
		
		//To test if the import was well done 
		for (Map.Entry<Integer, City> pair : hashMapCities.entrySet()) {
			System.out.println(pair.getValue().toString());
		}
	}
	
	@FXML
	public void showCities(ActionEvent event) {
		if(hashMapCities != null) {
			initCanvas();
			drawCities(false);
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Missing File");
			alert.setHeaderText(null);
			alert.setContentText("You have to select a file to show the cities !");

			alert.showAndWait();
		}
	}
	
	public void drawCities(boolean drawnPaths) {
		this.initCanvas();
		
		getMinsAndMaxs();
		
		//0.98 not to draw on the border of the canvas
		double coeffX = canvas.getWidth()*0.98 / (maxCityX - minCityX); 
		double coeffY = canvas.getHeight()*0.98 / (maxCityY - minCityY);
		
		//Drawing of paths
		if(drawnPaths) {
			for(Pair p : solution) {
				drawPath(p.getLeft(), p.getRight(), coeffX, coeffY);
			}
		}
		
		//Drawing of cities
		for (Map.Entry<Integer, City> pair : hashMapCities.entrySet()) {
			drawCity(pair.getValue().getX()*coeffX, pair.getValue().getY()*coeffY);
		}
	}
	
	public void drawCity(double x, double y) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.beginPath();
		gc.setFill(Color.RED);
		gc.fillOval(x-3, y-3, 6, 6);
		gc.closePath();
	}
	
	public void drawPath(int idCity1, int idCity2, double coeffX, double coeffY) {
		
		//Recover the cities with their id
		City c1 = hashMapCities.get(idCity1+1);
		City c2 = hashMapCities.get(idCity2+1);
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.beginPath();
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(4);
		gc.moveTo(c1.getX()*coeffX, c1.getY()*coeffY);
		gc.lineTo(c2.getX()*coeffX, c2.getY()*coeffY);
		gc.stroke();
		
	}
	
	public void getMinsAndMaxs() {
		
		for (Map.Entry<Integer, City> pair : hashMapCities.entrySet()) {
			//for X
			if (pair.getValue().getX() < minCityX)
			{
				minCityX = pair.getValue().getX();
			} else if (pair.getValue().getX() > maxCityX)
			{
				maxCityX = pair.getValue().getX();
			}
			
			//for Y
			if (pair.getValue().getY() < minCityY)
			{
				minCityY = pair.getValue().getY();
			} else if (pair.getValue().getY() > maxCityY)
			{
				maxCityY = pair.getValue().getY();
			}
		}
	}
	
	public void initCanvas() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		double canvasWidth = canvas.getWidth();
		double canvasHeight = canvas.getHeight();
		
		//clear the previous drawing
		gc.clearRect(0, 0, canvasWidth, canvasHeight);
		
		//draw the border
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(4);
		gc.strokeRect(0, 0, canvasWidth, canvasHeight);
	}
	
	@FXML
	public void startTSP(ActionEvent event) {
		if(hashMapCities != null) {
			AlgoRecuitSimule algoRecuitSimule = new AlgoRecuitSimule(hashMapCities);
			solution = algoRecuitSimule.solve();
			double solutionLength = algoRecuitSimule.getSolutionLength();
			if(solution != null) {
				this.drawCities(true);
				this.textSolution.setTextAlignment(TextAlignment.CENTER);
				this.textSolution.setText("The best solution is " + solutionLength);
			}
			
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Missing File");
			alert.setHeaderText(null);
			alert.setContentText("You have to select a file to start the TSP !");

			alert.showAndWait();
		}
				
	}
	
	
}
