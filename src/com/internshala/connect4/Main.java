package com.internshala.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;



public class Main extends Application {


	public static void main(String[] args) {
        launch(args);
    }
	private Controller controller;

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));

		GridPane rootGridPane = loader.load();
		controller = loader.getController();
		controller.createPlayGround();

		MenuBar menuBar = createMenu();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().addAll(menuBar);

		Scene scene = new Scene(rootGridPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect Four");
		primaryStage.setResizable(false);
		primaryStage.show();


		int a = new int[10];
		// the line may break code
	}

	private MenuBar createMenu() {
		Menu fileMenu = new Menu("File");
		MenuItem newMenuItem = new MenuItem("New");
		newMenuItem.setOnAction(event -> controller.resetGame());
		MenuItem resetMenuItem = new MenuItem("Reset");
		resetMenuItem.setOnAction(event -> controller.resetGame());
		SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
		MenuItem quitMenuItem = new MenuItem("Quit");
		quitMenuItem.setOnAction(event -> controller.exitGame());
		fileMenu.getItems().addAll(newMenuItem,resetMenuItem,separatorMenuItem1,quitMenuItem);

		Menu aboutMenu = new Menu("About");
		MenuItem aboutGameMenuItem = new MenuItem("About Game");
		aboutGameMenuItem.setOnAction(event -> aboutGame());
		MenuItem aboutAuthorMenuItem = new MenuItem("About Me");
		aboutAuthorMenuItem.setOnAction(event -> aboutAuthor());
		SeparatorMenuItem separatorMenuItem2 = new SeparatorMenuItem();
		aboutMenu.getItems().addAll(aboutGameMenuItem,separatorMenuItem2,aboutAuthorMenuItem);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu,aboutMenu);
		return menuBar;
	}

	private void aboutAuthor() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About the Developer");
		alert.setHeaderText("Sandeep (s4nd33p)");
		alert.setContentText("Change is the only constant, and boltzman number too. :p");
		alert.show();
	}

	private void aboutGame() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About the Game");
		alert.setHeaderText("How to Play?");
		alert.setContentText("Google you lazy! :p");
		alert.show();
	}
}
