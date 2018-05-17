import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
/* import javafx.event.ActionEvent;    // unused imports
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.geometry.Insets; */

import java.util.Random;

public class Launcher extends Application {
    private Stage window;
    private boolean[][] mined, uncovered, flagged, questioned;
    private int numRemainingTiles;
    private GridPane grid;
    private static final int NUM_MINES = 150, BOARD_HEIGHT = 25, BOARD_WIDTH = 30;

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
        validateSettings();

        grid = new GridPane();
        mined = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        uncovered = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        flagged = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        questioned = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        numRemainingTiles = BOARD_HEIGHT * BOARD_WIDTH - NUM_MINES;

        // MenuBar mb = new MenuBar();


        fillWithBlanks(grid);
        placeMines();

        window.setWidth(32 * BOARD_WIDTH);
        window.setHeight(32 * BOARD_HEIGHT + 22); // 22 is for the title bar/whatever you call it
        window.setResizable(false);
        Scene scene = new Scene(grid, 300, 200);
        window.setScene(scene);
        window.show();
    }

    private void fillWithBlanks(GridPane grid) {
        for (int r = 0; r < BOARD_HEIGHT; r++) {
            for (int c = 0; c < BOARD_WIDTH; c++) {
                Image image = new Image("File:assets/32px-Minesweeper_unopened_square.svg.png");
                ImageView iv = new ImageView(image);
                iv.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    ImageView tile = (ImageView) e.getSource();
                    MouseButton button = e.getButton();
                    e.consume();
                    if (button == MouseButton.PRIMARY) {
                        primaryClick(tile);
                    } else if (button == MouseButton.SECONDARY) {
                        secondaryClick(tile);
                    }
                });
                GridPane.setRowIndex(iv, r);
                GridPane.setColumnIndex(iv, c);
                grid.getChildren().add(iv);
            }
        }
    }

    private void validateSettings() {
        if (BOARD_HEIGHT * BOARD_WIDTH <= NUM_MINES) {
            throw new IllegalStateException("invalid game settings");
        }
    }

    private void placeMines() {
        int numMines = 0;
        Random r = new Random();
        while (numMines < NUM_MINES) {
            int row = r.nextInt(BOARD_HEIGHT);
            int col = r.nextInt(BOARD_WIDTH);
            if (!mined[row][col]) {
                mined[row][col] = true;
                numMines += 1;
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
            lose();
        } else {
            int neighboringMines = getNeighboringMines(row, col);
            tile.setImage(new Image("File:assets/32px-Minesweeper_" + neighboringMines + ".svg.png"));
            if (neighboringMines == 0) {
                uncoverUnflaggedNeighbors(row, col);
            }
            if (numRemainingTiles == 0) {
                win();
            }
        }
    }

    private void flagTile(ImageView tile, int row, int col) {
        if (!(flagged[row][col] || questioned[row][col])) {
            flagged[row][col] = true;
            tile.setImage(new Image("File:assets/32px-Minesweeper_flag.svg.png"));
        } else {
            flagged[row][col] = false;
            questioned[row][col] = false;
            tile.setImage(new Image("File:assets/32px-Minesweeper_unopened_square.svg.png"));
        }
    }

    private int getNeighboringMines(int row, int col) {
        int mines = 0;
        for (int r = Math.max(0, row - 1); r <= Math.min(BOARD_HEIGHT - 1, row + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(BOARD_WIDTH - 1, col + 1); c++) {
                if (!(row == r && col == c) && mined[r][c]) {
                    mines += 1;
                }
            }
        }
        return mines;
    }

    private int getNeighboringFlagged(int row, int col) {
        int flags = 0;
        for (int r = Math.max(0, row - 1); r <= Math.min(BOARD_HEIGHT - 1, row + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(BOARD_WIDTH - 1, col + 1); c++) {
                if (!(row == r && col == c) && flagged[r][c]) {
                    flags += 1;
                }
            }
        }
        return flags;
    }

    private void uncoverUnflaggedNeighbors(int row, int col) {
        for (int r = Math.max(0, row - 1); r <= Math.min(BOARD_HEIGHT - 1, row + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(BOARD_WIDTH - 1, col + 1); c++) {
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
        } else {
            questioned[row][col] = false;
            flagged[row][col] = true;
            tile.setImage(new Image("File:assets/32px-Minesweeper_flag.svg.png"));
        }
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
        initialize();
    }

    private void win() {
        for (int r = 0; r < BOARD_HEIGHT; r++) {
            for (int c = 0; c < BOARD_WIDTH; c++) {
                if (mined[r][c]) {
                    ImageView flag = getIVFromGridPane(r, c);
                    flag.setImage(new Image("File:assets/32px-Minesweeper_flag.svg.png"));
                }
            }
        }

        popup("congrats", "You win! Play again?");
    }

    private void lose() {
        for (int r = 0; r < BOARD_HEIGHT; r++) {
            for (int c = 0; c < BOARD_WIDTH; c++) {
                if (mined[r][c] && !flagged[r][c]) {
                    ImageView mine = getIVFromGridPane(r, c);
                    mine.setFitHeight(32);
                    mine.setFitWidth(32);
                    mine.setImage(new Image("File:assets/32px-Minesweeper_mine.png"));
                }
            }
        }
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
