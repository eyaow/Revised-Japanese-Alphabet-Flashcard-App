package com.hirakata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Hirakata extends Application implements EventHandler<ActionEvent> {

    private StackPane root = new StackPane();
    private Scene scene = new Scene(root, 500, 600);

    private VBox menu = new VBox();
    private VBox settings = new VBox(20);
    private VBox flashcards = new VBox(40);
    private VBox results = new VBox(40);

    private Button play = new Button("Play");
    private Button options = new Button("Options");
    private Button back = new Button("Back");
    private Button viewCharts = new Button("View Charts");

    private Label symbol = new Label();
    private Button ans = new Button();
    private Button incor1 = new Button();
    private Button incor2 = new Button();
    private Button incor3 = new Button();
    private Button next = new Button("Next");
    private HBox HNextBack = new HBox(10);

    private HashMap<String, String> deck = new HashMap<>();
    private Random rand = new Random();

    private Button hiragana = new Button("Hiragana");
    private Button katakana = new Button("Katakana");
    private Label selectedLang = new Label("Selected Language: Hiragana");
    private Slider questions = new Slider();
    private boolean lang = true;

    private int max = 5;
    private int min = 0;
    private Label progress = new Label(min + "/" + max);
    private int cor = 0;

    @Override
    public void init() {
        setupMenu();
        setupSettingsBase();
        back.setOnAction(this);
        root.getChildren().add(menu);
        root.getStyleClass().add("root-pane");
    }

    private void setupMenu() {
        play.setOnAction(this);
        play.setPrefSize(200, 80);
        options.setOnAction(this);
        options.setPrefSize(200, 80);
        viewCharts.setOnAction(this);
        viewCharts.setPrefSize(200, 80);

        menu.setSpacing(20);
        menu.setPadding(new Insets(20));
        menu.setAlignment(Pos.CENTER);
        menu.getChildren().addAll(play, viewCharts, options);
    }

    private void setupSettingsBase() {
        questions.setMin(1);
        questions.setMax(46);
        questions.setValue(5);
        questions.setShowTickLabels(true);
        questions.setSnapToTicks(true);
        questions.setMajorTickUnit(5);
    }

    private void flashcards() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        
        if(deck.size() < 4) deckBuilder();
        
        List<String> keys = new ArrayList<>(deck.keySet());
        String question = keys.get(rand.nextInt(keys.size()));

        symbol.setText(deck.get(question));
        ans.setText(question);
        deck.remove(question);
        keys.remove(question);

        incor1.setText(getRandomKey(keys));
        incor2.setText(getRandomKey(keys));
        incor3.setText(getRandomKey(keys));

        symbol.setFont(Font.font("Arial", 60));
        
        Button[] buttons = { ans, incor1, incor2, incor3 };
        for (Button b : buttons) {
            b.setOnAction(this);
            b.setPrefSize(140, 140);
            b.setDisable(false);
            b.setStyle(""); 
        }

        next.setOnAction(this);
        List<Integer> indices = new ArrayList<>(List.of(0, 1, 2, 3));
        Collections.shuffle(indices);

        grid.add(buttons[indices.get(0)], 0, 0);
        grid.add(buttons[indices.get(1)], 1, 0);
        grid.add(buttons[indices.get(2)], 0, 1);
        grid.add(buttons[indices.get(3)], 1, 1);

        flashcards.getChildren().clear();
        flashcards.setAlignment(Pos.CENTER);
        flashcards.getChildren().addAll(symbol, grid);
    }

    private String getRandomKey(List<String> keys) {
        String k = keys.get(rand.nextInt(keys.size()));
        keys.remove(k);
        return k;
    }

    private void deckBuilder() { 
        String path = lang ? "/com/hirakata/hiragana.txt" : "/com/hirakata/katakana.txt";
        deck.clear();
        
        var stream = Hirakata.class.getResourceAsStream(path);
        if (stream == null) {
            System.err.println("❌ Resource not found: " + path);
            return;
        }

        try (Scanner reader = new Scanner(stream, "UTF-8")) {
            while (reader.hasNext()) {
                String p = reader.next();
                String k = reader.next();
                deck.put(p, k);
            }
        } catch (Exception e) { 
            e.printStackTrace();
        }
    }

    public void settings() {
        settings.setAlignment(Pos.CENTER);
        Label qLabel = new Label("Questions: " + (int)questions.getValue());
        questions.valueProperty().addListener((obs, old, newVal) -> {
            qLabel.setText("Questions: " + newVal.intValue());
            max = newVal.intValue();
        });

        hiragana.setOnAction(this);
        katakana.setOnAction(this);
        HBox langButtons = new HBox(15, hiragana, katakana);
        langButtons.setAlignment(Pos.CENTER);

        ensureBackBtnDetached();
        settings.getChildren().setAll(qLabel, questions, langButtons, selectedLang, back);
    }

    public void results() {
        results.getChildren().clear();
        results.setAlignment(Pos.CENTER);
        double accuracy = ((double) cor / max) * 100;
        Label acc = new Label(String.format("Accuracy: %.2f%%", accuracy));
        acc.setFont(Font.font("Arial", 30));
        ensureBackBtnDetached();
        results.getChildren().addAll(acc, back);
    }

    public void viewCharts() {
        root.getChildren().clear();
        VBox format = new VBox(25);
        format.setAlignment(Pos.CENTER);
        
        GridPane charts = new GridPane();
        charts.setAlignment(Pos.CENTER);
        charts.setHgap(20); 
        charts.setVgap(10); 

        String[] vowels = {"", "a", "i", "u", "e", "o"};
        // We add "" padding to push consonant headers to start at Column 3
        String[] consonants = {"", "", "", "k-", "s-", "t-", "n-", "h-", "m-", "y-", "r-", "w-", "n"};

        // 1. TOP HEADERS (Consonants) - Headers start at Col 3
        for (int i = 3; i < consonants.length; i++) {
            Label header = new Label(consonants[i]);
            header.setStyle("-fx-font-weight: bold; -fx-text-fill: #888;");
            GridPane.setHalignment(header, HPos.CENTER);
            charts.add(header, i, 0); 
        }

        // 2. SIDE HEADERS (Vowels) - Column 1
        for (int i = 1; i < vowels.length; i++) {
            Label header = new Label(vowels[i]);
            header.setStyle("-fx-font-weight: bold; -fx-text-fill: #888;");
            charts.add(header, 1, i);
        }

        // 3. THE DATA (Symbols)
        String path = lang ? "/com/hirakata/hiragana.txt" : "/com/hirakata/katakana.txt";
        var stream = Hirakata.class.getResourceAsStream(path);
        
        if (stream != null) {
            try (Scanner reader = new Scanner(stream, "UTF-8")) {
                for (int col = 1; col < consonants.length - 1; col++) {
                    for (int row = 1; row < vowels.length; row++) {
                        if(!reader.hasNext()) break;

                        // Modern Chart Logic: skip empty slots
                        if (col == 8 && (row == 2 || row == 4)) continue; // skip yi, ye
                        if (col == 10 && (row == 2 || row == 3 || row == 4)) continue; // skip wi, wu, we
                        if (col == 11 && row > 1) break; // 'n' is Row 1 only

                        reader.next(); // skip romaji
                        Label symbolLabel = new Label(reader.next());
                        symbolLabel.setFont(Font.font("Arial", 22));
                        GridPane.setHalignment(symbolLabel, HPos.CENTER);
                        
                        // col=1 (pure vowels) goes to Column 2
                        // col=2 (k- group) goes to Column 3
                        charts.add(symbolLabel, col + 1, row);
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        ensureBackBtnDetached();
        Label title = new Label(lang ? "Hiragana" : "Katakana");
        title.setFont(Font.font("Arial", 36));
        format.getChildren().addAll(title, charts, back);
        root.getChildren().add(format);
    }

    private void ensureBackBtnDetached() {
        if (back.getParent() instanceof Pane pane) {
            pane.getChildren().remove(back);
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            var css = Hirakata.class.getResource("/com/hirakata/dark-theme.css");
            if(css != null) scene.getStylesheets().add(css.toExternalForm());
        } catch (Exception e) { e.printStackTrace(); }

        stage.setScene(scene);
        stage.setTitle("Hirakata 2.0 Web");
        stage.show();
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == play) {
            deckBuilder(); min = 0; cor = 0; flashcards();
            root.getChildren().setAll(flashcards);
        } else if (event.getSource() == options) {
            settings(); root.getChildren().setAll(settings);
        } else if (event.getSource() == back) {
            root.getChildren().setAll(menu);
        } else if (event.getSource() == viewCharts) {
            viewCharts();
        } else if (event.getSource() == hiragana) {
            lang = true; selectedLang.setText("Selected Language: Hiragana");
        } else if (event.getSource() == katakana) {
            lang = false; selectedLang.setText("Selected Language: Katakana");
        } else if (event.getSource() == next) {
            if (min >= max) { results(); root.getChildren().setAll(results); }
            else { flashcards(); root.getChildren().setAll(flashcards); }
        } else {
            handleAnswer(event.getSource());
        }
    }

    private void handleAnswer(Object source) {
        min++;
        if (source == ans) { 
            cor++; 
            ans.setStyle("-fx-background-color: green; -fx-text-fill: white;"); 
        } else if (source instanceof Button btn) { 
            btn.setStyle("-fx-background-color: red; -fx-text-fill: white;"); 
            ans.setStyle("-fx-background-color: green; -fx-text-fill: white;"); 
        }
        
        ans.setDisable(true); incor1.setDisable(true); incor2.setDisable(true); incor3.setDisable(true);
        progress.setText(min + "/" + max);
        ensureBackBtnDetached();
        HNextBack.getChildren().setAll(back, progress, next);
        HNextBack.setAlignment(Pos.CENTER);
        flashcards.getChildren().add(HNextBack);
    }

    public static void main(String[] args) { launch(args); }
}
