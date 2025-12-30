package src;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.scene.input.*;
import javafx.scene.shape.*;
import javafx.scene.image.*;
import javafx.scene.media.*;
import javafx.scene.effect.*;
import javafx.scene.transform.*;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.event.*;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Queue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

//things I want improved in this app:
//-use a hashmap/dictionary for decks to be implemented into one flashcard function --> done!
//-show what the user got wrong in results screen --> WIP (maybe I just won't do this one cause u can
//already kinda see it if u get it wrong)
//-options screen should include a slider going from 1-46 to allow the user to choose
//how many unique symbols they want to go through --> done!
//-options screen should be able to choose hiragana or katakana (same as last but remember that
//only one flashcard function will be used) --> done!

//2025 12 20
//thought of something new to add
//-viewable charts of hiragana of japanese --> done!
//-code optimizaton (change scenes/visibility instead of clearing the root
//on panels) --> WIP

public class Certainly extends Application implements EventHandler<ActionEvent> {

	// private VBox root = new VBox(20);
	// GridPane grid = new GridPane();
	// private PauseTransition pause = new PauseTransition(Duration.seconds(2));
	private StackPane root = new StackPane();
	private Scene scene = new Scene(root, 500, 500);

	// panels
	private VBox menu = new VBox();
	private VBox settings = new VBox();
	private VBox flashcards = new VBox(40);
	private VBox results = new VBox(40);
	// VBox nextPanel = new VBox();

	// buttons
	private Button play = new Button("Play");
	private Button options = new Button("Options");
	private Button quit = new Button("Quit");
	private Button back = new Button("Back"); // used in both results and settings to go back to menu

	// flashcard buttons
	private Label symbol = new Label();
	private Button ans = new Button();
	private Button incor1 = new Button();
	private Button incor2 = new Button();
	private Button incor3 = new Button();
	// private List<Button> choices = Arrays.asList(ans, incor1, incor2, incor3);
	private Button next = new Button("Next");
	private HBox HNextBack = new HBox(140);// stands for horizontal next back, used for formatting

	// deck builder
	private HashMap<String, String> deck = new HashMap<>();
	private Random rand = new Random();

	// options
	private Button hiragana = new Button("Hiragana");
	private Button katakana = new Button("Katakana");
	private Label selectedLang = new Label("Selected Langage: Hiragana");
	private Slider questions = new Slider();
	private boolean lang = true;

	// results
	// private int numOfQuestionsLeft = 5;//5 will be the default
	private int max = 5;
	private int min = 0;
	private Label progress = new Label(min + "/" + max);
	private int cor = 0;

	// view charts
	private Button viewCharts = new Button("View Charts");

	public Certainly()// constructor, where I build stuff
	{
		menu();
		// settings();
		back.setOnAction(this);
		questions.setPrefHeight(200);
		questions.setPrefWidth(200);
		questions.setSnapToTicks(true);
		questions.setMin(1);
		questions.setMax(46);
		questions.setShowTickLabels(true);
		questions.setBlockIncrement(1);
		questions.setMinorTickCount(5);
		questions.setMajorTickUnit(1);
		questions.setValue(5);// default value
		// questions.setOrientation(Orientation.VERTICAL);

		// flashcards section, probably only going to use for formatting
		// HNextBack.getChildren().add(back);//needs to be in action listioner... damn

		// root stuff
		root.getChildren().add(menu);// starts with menu
		root.getStyleClass().add("root-pane");// dark theme
	}

	private void menu() {
		// menu panel
		play.setOnAction(this);
		play.setPrefWidth(200); // width in pixels
		play.setPrefHeight(100); // height in pixels

		options.setOnAction(this);
		options.setPrefWidth(200); // width in pixels
		options.setPrefHeight(100); // height in pixels

		quit.setOnAction(this);
		quit.setPrefWidth(200); // width in pixels
		quit.setPrefHeight(100); // height in pixels

		viewCharts.setOnAction(this);
		viewCharts.setPrefWidth(200); // width in pixels
		viewCharts.setPrefHeight(100); // height in pixels

		menu.setSpacing(20); // vertical space between buttons
		menu.setPadding(new Insets(10)); // space around the VBox
		menu.setAlignment(Pos.CENTER); // how buttons are aligned horizontally

		menu.getChildren().add(play);// those two int values affect placement on x and y accordingly
		menu.getChildren().add(viewCharts);
		menu.getChildren().add(options);
		menu.getChildren().add(quit);
	}

	private void flashcards() {
		// flashcards panel
		GridPane grid = new GridPane();
		// grid.setHgap(1);
		// grid.setVgap(1);
		grid.setAlignment(Pos.CENTER);
		
		//bandaid fix for a bug that happens at question 44 because
		//deck size is less than 4 and therefore needs to be rebuilt
		//to accompany the 4 buttons
		if(deck.size() < 4)
		{
			deckBuilder();
		}
		
		List<String> keys = new ArrayList<>(deck.keySet());

		// System.out.println(deck);
		// Pick a random key
		String question = keys.get(rand.nextInt(keys.size()));

		symbol = new Label(deck.get(question));
		ans = new Button(question);

		deck.remove(question);// for unique questions
		keys.remove(question);

		String incorLabel1 = keys.get(rand.nextInt(keys.size()));
		keys.remove(incorLabel1);

		String incorLabel2 = keys.get(rand.nextInt(keys.size()));
		keys.remove(incorLabel2);

		String incorLabel3 = keys.get(rand.nextInt(keys.size()));
		keys.remove(incorLabel3);

		incor1 = new Button(incorLabel1);
		incor2 = new Button(incorLabel2);
		incor3 = new Button(incorLabel3);

		symbol.setFont(Font.font("Arial", 24));
		symbol.setPrefWidth(250);
		symbol.setPrefHeight(250);

		ans.setOnAction(this);
		ans.setPrefWidth(250); // width in pixels
		ans.setPrefHeight(250);

		incor1.setOnAction(this);
		incor1.setPrefWidth(250); // width in pixels
		incor1.setPrefHeight(250);

		incor2.setOnAction(this);
		incor2.setPrefWidth(250); // width in pixels
		incor2.setPrefHeight(250);

		incor3.setOnAction(this);
		incor3.setPrefWidth(250); // width in pixels
		incor3.setPrefHeight(250);

		next.setOnAction(this);

		// shuffle the button positions
		List<int[]> positions = new ArrayList<>();
		positions.add(new int[] { 0, 0 });
		positions.add(new int[] { 1, 0 });
		positions.add(new int[] { 0, 1 });
		positions.add(new int[] { 1, 1 });

		Collections.shuffle(positions);

		Button[] buttons = { ans, incor1, incor2, incor3 };

		for (int i = 0; i < buttons.length; i++) {
			int col = positions.get(i)[0];
			int row = positions.get(i)[1];
			grid.add(buttons[i], col, row);
		}

		// grid.add(ans, 0, 0);
		// grid.add(incor1, 1, 0);
		// grid.add(incor2, 0, 1);
		// grid.add(incor3, 1, 1);

		// root.setAlignment(symbol, Pos.CENTER);
		symbol.setAlignment(Pos.CENTER);

		flashcards.getChildren().clear();
		flashcards.setAlignment(Pos.CENTER);
		flashcards.getChildren().addAll(symbol, grid);
	}

	private void deckBuilder()// goes into play action listener, loads the deck
	{
		String deckFileName = "";

		if (lang) {
			deckFileName = "hiragana.txt";
		} else {
			deckFileName = "katakana.txt";
		}

		try (Scanner reader = new Scanner(Certainly.class.getResourceAsStream(deckFileName))) {
			while (reader.hasNext()) {
				String pronunciation = reader.next();
				String kana = reader.next();
				deck.put(pronunciation, kana);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void settings() {

		Label questionsLabel = new Label(max + "");

		questions.valueProperty().addListener((obs, oldVal, newVal) -> {
			// System.out.println("Slider changed to: " + newVal.intValue());
			questionsLabel.setText("" + newVal.intValue());
			// numOfQuestionsLeft = newVal.intValue();
			max = newVal.intValue();
		});

		hiragana.setOnAction(this);
		hiragana.setPrefHeight(200);
		hiragana.setPrefWidth(200);

		katakana.setOnAction(this);
		katakana.setPrefHeight(200);
		katakana.setPrefWidth(200);

		HBox langButtons = new HBox();
		langButtons.setSpacing(15);

		langButtons.getChildren().add(hiragana);
		langButtons.getChildren().add(katakana);

		settings.getChildren().clear();
		settings.getChildren().add(questionsLabel);
		settings.getChildren().add(questions);
		settings.getChildren().add(langButtons);
		settings.getChildren().add(selectedLang);
		settings.getChildren().add(back);
	}

	public void results() {
		results.getChildren().clear();
		results.setAlignment(Pos.CENTER);

		Label acc = new Label();
		acc.setText(String.format(
			    "Accuracy: %.2f%%",
			    ((double) cor / max) * 100
			));

		Parent parent = back.getParent();
		if (parent instanceof Pane pane) {
			pane.getChildren().remove(back);
		}

		// formatting
		// VBox resultBox = new VBox(50);
		acc.setFont(Font.font("Arial", 30));
		// resultBox.getChildren().add(acc);
		// resultBox.getChildren().add(back);

		results.getChildren().add(acc);
		results.getChildren().add(back);

	}

	public void Chart(GridPane charts, String lang) {
		// initial line
		String[] line = "k- s- t- n- h- m- y- r- w- n".split(" ");

		// Label usefulassSpace = new Label(" ");
		// charts.add(usefulassSpace, 0, 0);

		for (int i = 0; i < line.length; i++) {
			Label temp = new Label();
			temp.setFont(Font.font("Arial", 20));

			temp.setText(line[i] + " ");

			charts.add(temp, i + 2, 0);
		}

		// reuse deckbuilder for this I think (nevermind but same concept)
		try (Scanner reader = new Scanner(Certainly.class.getResourceAsStream(lang))) {
			// while (reader.hasNext()) {
			for (int row = 1; row < 13; row++)// idk how this row col stuff works but it works
			{
				for (int col = 1; col < 6; col++) {
					String pronunciation = reader.next();
					String kana = reader.next();
					Label temp = new Label();
					temp.setFont(Font.font("Arial", 20));

					// adjustments
					if (row == 1)// for a i u e o
					{
						temp.setText(pronunciation + "  " + kana + " ");
					} else {
						temp.setText(kana + " ");
					}

					// some words needed to be adjusted to make sense
					if (pronunciation.equals("yu")) {
						col++;
						charts.add(temp, row, col);
						col++;
					} else if (pronunciation.equals("wo")) {
						col = 5;
						charts.add(temp, row, col);
					} else {
						charts.add(temp, row, col);
					}
				}
			}

			reader.close();
			// deck.put(pronunciation, kana);
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void viewCharts() {
		root.getChildren().clear();

		VBox format = new VBox(20);
		GridPane charts = new GridPane();
		charts.setAlignment(Pos.CENTER);
		String langWord = "";

		if (lang) {
			Chart(charts, "hiragana.txt");
			langWord = "Hiragana";
		} else {
			Chart(charts, "katakana.txt");
			langWord = "Katakana";
		}
		// Label ptemp = new Label("test");

		// charts.getChildren().add(ptemp, 0, 2);

		// reuse back
		Parent parent = back.getParent();
		if (parent instanceof Pane pane) {
			pane.getChildren().remove(back);
		}

		// label
		Label language = new Label(langWord);
		language.setFont(Font.font("Arial", 20));
		format.getChildren().add(language);

		format.setAlignment(Pos.CENTER);
		format.getChildren().add(charts);
		format.getChildren().add(back);

		root.getChildren().add(format);
	}

	@Override
	public void start(Stage stage) throws Exception {
		// set the scene
		// Scene scene = new Scene(root, 500, 500);

		// Load CSS file
		scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());

		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
		// scene.setRoot(menu);
		// center on screen
		double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
		double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
		double stageWidth = stage.getWidth();
		double stageHeight = stage.getHeight();
		stage.setX((screenWidth - stageWidth) / 2);
		stage.setY((screenHeight - stageHeight) / 2);

		// Platform.runLater(() -> stage.centerOnScreen()); // this doesn't work for me
		stage.setTitle("Hirakata Revision");

	}

	@Override
	public void handle(ActionEvent event) {
		// menu section
		if (event.getSource() == play) {
			deckBuilder();
			flashcards();
			min = 0;
			cor = 0;
			root.getChildren().clear();
			root.getChildren().add(flashcards);
		}

		if (event.getSource() == quit) {
			System.exit(0);
		}

		if (event.getSource() == back) {
			root.getChildren().clear();
			root.getChildren().add(menu);
		}

		// flashcard section
		if (event.getSource() == ans) {
			/*
			 * ans.setDisable(true); incor1.setDisable(true); incor2.setDisable(true);
			 * incor3.setDisable(true); ans.setStyle("-fx-background-color: green;");
			 * PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
			 * pause.setOnFinished(e -> { root.getChildren().clear(); if(numOfQuestionsLeft
			 * == 0) { root.getChildren().add(results); } else {
			 * ans.setStyle("-fx-background-color: #2d2d30;"); flashcards();
			 * root.getChildren().add(flashcards); ans.setDisable(false);
			 * incor1.setDisable(false); incor2.setDisable(false); incor3.setDisable(false);
			 * } }); pause.play();
			 */
			ans.setStyle("-fx-background-color: green;");
			// incor1.setStyle("-fx-background-color: red;");
			// progress section, have to do this cause back cannot be used in multiple boxes
			// at the same time
			min++;
			cor++;
			progress.setText(min + "/" + max);
			HNextBack.getChildren().clear();
			HNextBack.getChildren().add(back);
			HNextBack.getChildren().add(progress);
			HNextBack.getChildren().add(next);
			flashcards.getChildren().add(HNextBack);

			// disable buttons to prevent pressing more than once
			ans.setDisable(true);
			incor1.setDisable(true);
			incor2.setDisable(true);
			incor3.setDisable(true);
		}

		if (event.getSource() == next) {
			root.getChildren().clear();
			if (min == max) {
				results();
				root.getChildren().add(results);
			} else {
				ans.setStyle("-fx-background-color: #2d2d30;");
				flashcards();
				root.getChildren().add(flashcards);
				ans.setDisable(false);
				incor1.setDisable(false);
				incor2.setDisable(false);
				incor3.setDisable(false);
			}
		}

		if (event.getSource() == incor1) {
			ans.setStyle("-fx-background-color: green;");
			incor1.setStyle("-fx-background-color: red;");
			// progress section, have to do this cause back cannot be used in multiple boxes
			// at the same time
			min++;
			progress.setText(min + "/" + max);
			HNextBack.getChildren().clear();
			HNextBack.getChildren().add(back);
			HNextBack.getChildren().add(progress);
			HNextBack.getChildren().add(next);
			flashcards.getChildren().add(HNextBack);

			ans.setDisable(true);
			incor1.setDisable(true);
			incor2.setDisable(true);
			incor3.setDisable(true);
		}

		if (event.getSource() == incor2) {
			ans.setStyle("-fx-background-color: green;");
			incor2.setStyle("-fx-background-color: red;");
			// progress section, have to do this cause back cannot be used in multiple boxes
			// at the same time
			min++;
			progress.setText(min + "/" + max);
			HNextBack.getChildren().clear();
			HNextBack.getChildren().add(back);
			HNextBack.getChildren().add(progress);
			HNextBack.getChildren().add(next);
			flashcards.getChildren().add(HNextBack);

			ans.setDisable(true);
			incor1.setDisable(true);
			incor2.setDisable(true);
			incor3.setDisable(true);
		}

		if (event.getSource() == incor3) {
			ans.setStyle("-fx-background-color: green;");
			incor3.setStyle("-fx-background-color: red;");
			// progress section, have to do this cause back cannot be used in multiple boxes
			// at the same time
			min++;
			progress.setText(min + "/" + max);
			HNextBack.getChildren().clear();
			HNextBack.getChildren().add(back);
			HNextBack.getChildren().add(progress);
			HNextBack.getChildren().add(next);
			flashcards.getChildren().add(HNextBack);

			ans.setDisable(true);
			incor1.setDisable(true);
			incor2.setDisable(true);
			incor3.setDisable(true);
		}

		// settings section
		if (event.getSource() == options) {
			root.getChildren().clear();
			settings();
			root.getChildren().add(settings);
		}

		if (event.getSource() == hiragana) {
			lang = true;
			selectedLang.setText("Selected Langage: Hiragana");
		}

		if (event.getSource() == katakana) {
			lang = false;
			selectedLang.setText("Selected Langage: Katakana");
		}

		// viewCharts section
		if (event.getSource() == viewCharts) {
			viewCharts();
		}
	}

	public static void main(String[] args) {
		launch();
	}
}
