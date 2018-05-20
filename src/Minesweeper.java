import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Random;

public class Minesweeper extends Application {
    private Stage window;
    private boolean[][] mined, uncovered, flagged, questioned;
    private int numRemainingTiles, numRemainingMines;
    private double elapsedTime, startNanos;
    private BorderPane container, gameContainer;
    private GridPane grid;
    private AnimationTimer timer;
    private Text timerText, faceText, minesText;
    private int numMines, boardHeight, boardWidth;
    private int gameState;
    // TODO: 2d-array of image views so finding a flag ain't so damn hard
    // TODO: break into board and tile classes so you don't have 23058943702543 instance vars

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Minesweeper");
        window.setOnCloseRequest(e -> closeProgram());

        initialize();
    }

    private void initialize() {
        StatsHelper.xmlSetup();

        Map<String, Integer> settings = SettingsHelper.getSettings();
        numMines = settings.get("mines");
        boardHeight = settings.get("height");
        boardWidth = settings.get("width");
        container = new BorderPane();
        gameContainer = new BorderPane();
        grid = new GridPane();
        mined = new boolean[boardHeight][boardWidth];
        uncovered = new boolean[boardHeight][boardWidth];
        flagged = new boolean[boardHeight][boardWidth];
        questioned = new boolean[boardHeight][boardWidth];
        numRemainingTiles = boardHeight * boardWidth - numMines;
        numRemainingMines = numMines;
        gameState = 0;
        elapsedTime = 0; startNanos = 0;
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (startNanos == 0) {
                    startNanos = now;
                } else {
                    elapsedTime = (now - startNanos) / 1e9;
                    timerText.setText(Integer.toString((int) elapsedTime));
                }
            }
        };

        initializeMenu();
        initializeGameHUD();
        fillGridWithBlanks();
        placeMines();

        window.setWidth(32 * boardWidth);
        window.setHeight(32 * boardHeight + 67); // 67 is for the title bar/whatever you call it
        window.setResizable(false);
        Scene scene = new Scene(container, 300, 200);
        window.setScene(scene);
        window.show();
    }

    private void initializeMenu() {
        MenuBar mb = new MenuBar();
        Menu gameMenu = new Menu("Game");
        Menu settingsMenu = new Menu("Settings");
        Menu statsMenu = new Menu("Stats");

        MenuItem newGame = new MenuItem("New");
        newGame.setOnAction(e -> {
            e.consume();
            restart();
        });
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> {
            e.consume();
            closeProgram();
        });
        gameMenu.getItems().addAll(newGame, exit);

        MenuItem sBeginner = new MenuItem("Beginner");
        sBeginner.setOnAction(e -> {
            SettingsHelper.setDifficulty("beginner");
            restart();
        });
        MenuItem sIntermediate = new MenuItem("Intermediate");
        sIntermediate.setOnAction(e -> {
            SettingsHelper.setDifficulty("intermediate");
            restart();
        });
        MenuItem sExpert = new MenuItem("Expert");
        sExpert.setOnAction(e -> {
            SettingsHelper.setDifficulty("expert");
            restart();
        });
        MenuItem sLottery = new MenuItem("Lottery");
        sLottery.setOnAction(e -> {
            SettingsHelper.setDifficulty("lottery");
            restart();
        });
        settingsMenu.getItems().addAll(sBeginner, sIntermediate, sExpert, sLottery);

        MenuItem beginner = new MenuItem("Beginner");
        beginner.setOnAction(e -> StatsHelper.showStats("beginner"));
        MenuItem intermediate = new MenuItem("Intermediate");
        intermediate.setOnAction(e -> StatsHelper.showStats("intermediate"));
        MenuItem expert = new MenuItem("Expert");
        expert.setOnAction(e -> StatsHelper.showStats("expert"));
        MenuItem lottery = new MenuItem("Lottery");
        lottery.setOnAction(e -> StatsHelper.showStats("lottery"));
        MenuItem resetAll = new MenuItem("Reset All");
        resetAll.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.CLOSE);
            alert.setTitle("woah there");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                StatsHelper.resetAll();
                restart();
            }
        });
        statsMenu.getItems().addAll(beginner, intermediate, expert, lottery, resetAll);

        mb.getMenus().addAll(gameMenu, settingsMenu, statsMenu);
        container.setTop(mb);
    }

    private void initializeGameHUD() {

        timerText = new Text("0");
        faceText = new Text(":-)");
        minesText = new Text(Integer.toString(numRemainingMines));
        Region region1 = new Region();
        HBox.setHgrow(region1, Priority.ALWAYS);
        Region region2 = new Region();
        HBox.setHgrow(region2, Priority.ALWAYS);
        HBox hud = new HBox(timerText, region1, faceText, region2, minesText);
        hud.setPadding(new Insets(0, 15, 0, 15));

        gameContainer.setTop(hud);
    }

    private void fillGridWithBlanks() {
        for (int r = 0; r < boardHeight; r++) {
            for (int c = 0; c < boardWidth; c++) {
                Image image = new Image("File:assets/32px-Minesweeper_unopened_square.svg.png");
                ImageView iv = new ImageView(image);
                iv.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    ImageView tile = (ImageView) e.getSource();
                    MouseButton button = e.getButton();
                    e.consume();
                    if (startNanos == 0) {
                        timer.start();
                    }
                    if (button == MouseButton.PRIMARY) {
                        primaryClick(tile);
                    } else if (button == MouseButton.SECONDARY) {
                        secondaryClick(tile);
                    }
                });
                iv.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> faceText.setText(":-O"));
                iv.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> faceText.setText(":-)"));
                GridPane.setRowIndex(iv, r);
                GridPane.setColumnIndex(iv, c);
                grid.getChildren().add(iv);
            }
        }
        gameContainer.setCenter(grid);
        container.setCenter(gameContainer);
    }

    private void placeMines() {
        int i = 0;
        Random r = new Random();
        while (i < numMines) {
            int row = r.nextInt(boardHeight);
            int col = r.nextInt(boardWidth);
            if (!mined[row][col]) {
                mined[row][col] = true;
                i += 1;
            }
        }
    }

    private void primaryClick(ImageView tile) {
        int row = GridPane.getRowIndex(tile);
        int col = GridPane.getColumnIndex(tile);
        if (flagged[row][col] || questioned[row][col]) {
            toggleFlags(tile, row, col);
        } else if (uncovered[row][col]) {
            if (getNeighboringFlagged(row, col) == getNeighboringMines(row, col)) {
                uncoverUnflaggedNeighbors(row, col);
            }
        } else {
            uncoverTile(tile, row, col);
        }
        if (gameState == 1) {
            win();
        } else if (gameState == -1) {
            lose();
        }
    }

    private void secondaryClick(ImageView tile) {
        int row = GridPane.getRowIndex(tile);
        int col = GridPane.getColumnIndex(tile);
        if (!uncovered[row][col]) {
            flagTile(tile, row, col);
        }
    }

    private void uncoverTile(ImageView tile, int row, int col) {
        uncovered[row][col] = true;
        numRemainingTiles -= 1;
        if (mined[row][col]) {
            gameState = -1;
        } else {
            int neighboringMines = getNeighboringMines(row, col);
            tile.setImage(new Image("File:assets/32px-Minesweeper_" + neighboringMines + ".svg.png"));
            if (neighboringMines == 0) {
                uncoverUnflaggedNeighbors(row, col);
            }
            if (numRemainingTiles == 0) {
                gameState = 1;
            }
        }
    }

    private void flagTile(ImageView tile, int row, int col) {
        if (!(flagged[row][col] || questioned[row][col])) {
            flagged[row][col] = true;
            tile.setImage(new Image("File:assets/32px-Minesweeper_flag.svg.png"));
            numRemainingMines -= 1;
        } else {
            flagged[row][col] = false;
            questioned[row][col] = false;
            tile.setImage(new Image("File:assets/32px-Minesweeper_unopened_square.svg.png"));
            numRemainingMines += 1;
        }
        minesText.setText(Integer.toString(numRemainingMines));
    }

    private int getNeighboringMines(int row, int col) {
        int mines = 0;
        for (int r = Math.max(0, row - 1); r <= Math.min(boardHeight - 1, row + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(boardWidth - 1, col + 1); c++) {
                if (!(row == r && col == c) && mined[r][c]) {
                    mines += 1;
                }
            }
        }
        return mines;
    }

    private int getNeighboringFlagged(int row, int col) {
        int flags = 0;
        for (int r = Math.max(0, row - 1); r <= Math.min(boardHeight - 1, row + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(boardWidth - 1, col + 1); c++) {
                if (!(row == r && col == c) && flagged[r][c]) {
                    flags += 1;
                }
            }
        }
        return flags;
    }

    private void uncoverUnflaggedNeighbors(int row, int col) {
        for (int r = Math.max(0, row - 1); r <= Math.min(boardHeight - 1, row + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(boardWidth - 1, col + 1); c++) {
                if (!(row == r && col == c) && !flagged[r][c] && !uncovered[r][c]) {
                    ImageView neighbor = getIVFromGridPane(r, c);
                    uncoverTile(neighbor, r, c);
                }
            }
        }
    }

    private void toggleFlags(ImageView tile, int row, int col) {
        if (flagged[row][col]) {
            flagged[row][col] = false;
            questioned[row][col] = true;
            tile.setImage(new Image("File:assets/32px-Minesweeper_questionmark.svg.png"));
            numRemainingMines += 1;
        } else {
            questioned[row][col] = false;
            flagged[row][col] = true;
            tile.setImage(new Image("File:assets/32px-Minesweeper_flag.svg.png"));
            numRemainingMines -= 1;
        }
        minesText.setText(Integer.toString(numRemainingMines));
    }

    private ImageView getIVFromGridPane(int row, int col) {
        for (Node iv : grid.getChildren()) {
            if (GridPane.getColumnIndex(iv) == col && GridPane.getRowIndex(iv) == row) {
                return (ImageView) iv;
            }
        }
        return null;
    }

    private void restart() {
        timer.stop();
        initialize();
    }

    private void win() {
        timer.stop();
        faceText.setText("B-)");
        for (int r = 0; r < boardHeight; r++) {
            for (int c = 0; c < boardWidth; c++) {
                if (mined[r][c]) {
                    ImageView flag = getIVFromGridPane(r, c);
                    assert flag != null;
                    flag.setImage(new Image("File:assets/32px-Minesweeper_flag.svg.png"));
                }
            }
        }
        minesText.setText("0");
        StatsHelper.winGame(SettingsHelper.getDifficulty(), elapsedTime);

        popup("congrats", "You win! Play again?");
    }

    private void lose() {
        timer.stop();
        faceText.setText("x_x");
        for (int r = 0; r < boardHeight; r++) {
            for (int c = 0; c < boardWidth; c++) {
                if (mined[r][c] && !flagged[r][c]) {
                    ImageView mine = getIVFromGridPane(r, c);
                    assert mine != null;
                    mine.setImage(new Image("File:assets/32px-Minesweeper_mine.jpg"));
                } else if (!mined[r][c] && flagged[r][c]) {
                    ImageView fake = getIVFromGridPane(r, c);
                    assert fake != null;
                    fake.setImage(new Image("File:assets/32px-Minesweeper_fake.png"));
                }
            }
        }
        StatsHelper.loseGame(SettingsHelper.getDifficulty());
        popup("u lose", "R I P Play again?");
    }

    private void popup(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.CLOSE);
        alert.setTitle(title);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            restart();
        } else {
            closeProgram();
        }
    }

    private void closeProgram() {
        System.exit(0);
    }
}
