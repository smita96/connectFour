package com.internshala.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;


import java.beans.Transient;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable{

	private static final int COLUMNS = 7, ROWS = 6, CIRCLE_DIAMETER = 80;
	private static String PLAYER_ONE = "Player One", PLAYER_TWO = "Player Two";
	private static final String DISCCOLOR1 = "#24303e", DISCCOLOR2 = "4caa88";
	private static boolean isPlayerOneTurn = true;
	private static int freeCells = ROWS*COLUMNS;

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscPane;

	@FXML
	public Label playerNameLabel;

	public void createPlayGround(){
		Shape rectangleWithHoles = createGameStructuralGrid();
		rootGridPane.add(rectangleWithHoles,0,1);
		List<Rectangle> rectangleList = createClickableRows();
		for(Rectangle rectangle : rectangleList){
			rootGridPane.add(rectangle,0,1);
		}
	}

	public Shape createGameStructuralGrid(){
		Shape rectangleWithHoles = new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);

		for(int col = 0; col < COLUMNS; col++){
			for(int row = 0; row < ROWS; row++){
				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER/2);
				circle.setCenterX(CIRCLE_DIAMETER/2); circle.setCenterY(CIRCLE_DIAMETER/2);
				circle.setSmooth(true);
				circle.setTranslateX(col*(CIRCLE_DIAMETER+6) + CIRCLE_DIAMETER/4); circle.setTranslateY(row*(CIRCLE_DIAMETER+6) + CIRCLE_DIAMETER/4);
				rectangleWithHoles = Shape.subtract(rectangleWithHoles,circle);
			}
		}
		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}

	private boolean isAllowedToInsert = true;

	public List<Rectangle> createClickableRows(){
		List<Rectangle> rectangleList = new ArrayList<>();
		for(int col = 0; col < COLUMNS; col++){
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
			rectangle.setTranslateX(col*(CIRCLE_DIAMETER+6) + CIRCLE_DIAMETER/4);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#EEEEEE26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

			int finalCol = col;
			rectangle.setOnMouseClicked(event -> {
				if(isAllowedToInsert){
					isAllowedToInsert = false;
					freeCells--;
					insertDisc(new Disc(isPlayerOneTurn), finalCol);
				}
			});
			rectangleList.add(rectangle);
		}
		return  rectangleList;
	}

	public Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS];

	private void insertDisc(Disc disc, int finalCol) {

		int row, freeRow = -1;
		for(row = 0; row < ROWS; row++){
			if(insertedDiscsArray[row][finalCol] == null) freeRow = row;
		}
		if(freeRow == -1) return;
		insertedDiscsArray[freeRow][finalCol] = disc;
		disc.setTranslateX(finalCol*(CIRCLE_DIAMETER+6) + CIRCLE_DIAMETER/4);

		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(.3),disc);
		translateTransition.setToY(freeRow*(CIRCLE_DIAMETER+6) + CIRCLE_DIAMETER/4);
		translateTransition.play();
		int finalFreeRow = freeRow;
		translateTransition.setOnFinished(event -> {
			if(gameEnded(finalFreeRow,finalCol)){
				gameOver(0);
			}
			else {
				if(freeCells == 0){
					gameOver(1);
				}
				else{
					isAllowedToInsert = true;
					isPlayerOneTurn = !isPlayerOneTurn;
					playerNameLabel.setText(isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO);
				}

			}
		});
		insertedDiscPane.getChildren().add(disc);
	}

	private boolean gameEnded(int finalFreeRow, int finalCol) {
		List<Point2D> verticalPoints = IntStream.rangeClosed(finalFreeRow-3,finalFreeRow+3).mapToObj(r -> new Point2D(r,finalCol)).collect(Collectors.toList());
		List<Point2D> horizontalPoints = IntStream.rangeClosed(finalCol-3,finalCol+3).mapToObj(c -> new Point2D(finalFreeRow,c)).collect(Collectors.toList());
		Point2D startPoint1 = new Point2D(finalFreeRow-3,finalCol+3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6).mapToObj(i -> startPoint1.add(i,-i)).collect(Collectors.toList());
		Point2D startPoint2 = new Point2D(finalFreeRow-3,finalCol-3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6).mapToObj(i -> startPoint2.add(i,i)).collect(Collectors.toList());
		return checkCombinations(verticalPoints) || checkCombinations(horizontalPoints) || checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;
		for (Point2D point:points) {
			int x = (int) point.getX(), y = (int) point.getY();
			Disc disc = getDiscAt(x,y);
			if((disc != null) && (disc.isPlayerOneTurn == isPlayerOneTurn)) chain += 1;
			else chain = 0;
			if(chain == 4) return  true;
		}
		return  false;
	}

	private Disc getDiscAt(int x, int y) {
		if(x < 0 || y < 0 || x >= ROWS || y >= COLUMNS) return null;
		else return insertedDiscsArray[x][y];
	}

	private void gameOver(int verdict) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setContentText("Want to play again?");
		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No, Exit");
		alert.getButtonTypes().setAll(yesBtn,noBtn);

		if(verdict == 0){
			alert.setHeaderText((isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO) + " WON");
		}
		else{
			alert.setHeaderText("Match Draw, come on ppl!");
		}

		Platform.runLater(()->{
			Optional<ButtonType> clickedBtn = alert.showAndWait();
			if(clickedBtn.isPresent() && clickedBtn.get() == yesBtn) resetGame(); else exitGame();
		});
	}

	public void resetGame() {
		insertedDiscsArray = new Disc[ROWS][COLUMNS];
		insertedDiscPane.getChildren().clear();
		isPlayerOneTurn = true; playerNameLabel.setText(PLAYER_ONE);
		isAllowedToInsert = true;
	}

	public void exitGame() {
		Platform.exit();
		System.exit(0);
	}

	public class Disc extends Circle{
		public boolean isPlayerOneTurn;
		public Disc(boolean isPlayerOneTurn) {
			this.isPlayerOneTurn = isPlayerOneTurn;
			setFill(isPlayerOneTurn ? Color.valueOf(DISCCOLOR1) : Color.valueOf(DISCCOLOR2));
			setRadius(CIRCLE_DIAMETER/2);
			setCenterX(CIRCLE_DIAMETER/2);
			setCenterY(CIRCLE_DIAMETER/2);
		}
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
