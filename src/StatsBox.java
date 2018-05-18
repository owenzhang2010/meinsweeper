import java.util.Map;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.text.Text;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class StatsBox {
    public static void display(String difficulty, Map<String, String> stats) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1) + " Stats");

        String text = "Average time: " + stats.get("average") + "\n"
                    + "Best time: " + stats.get("best") + "\n"
                    + "Wins: " + stats.get("wins") + "\n"
                    + "Gamed played: " + stats.get("played") + "\n"
                    + "Win percentage: " + stats.get("percentage") + "\n";
        Text t = new Text(text);

        GridPane g = new GridPane();
        g.getChildren().add(t);
        Scene scene = new Scene(g, 150, 150);
        window.setScene(scene);
        window.show();
    }
}
