import java.util.Map;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

public class StatsBox {
    public static void display(String difficulty, Map<String, String> stats) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1) + " Stats");

        String text = "Average time: " + stats.get("average") + "\n"
                    + "Best time: " + stats.get("best") + "\n"
                    + "Wins: " + stats.get("wins") + "\n"
                    + "Games played: " + stats.get("played") + "\n"
                    + "Win percentage: " + stats.get("percentage") + "\n";
        Text t = new Text(text);
        Button reset = new Button("Reset");
        reset.setOnAction(event -> {
            StatsHelper.resetMode(difficulty);
            window.close();
        });

        VBox v = new VBox();
        v.getChildren().addAll(t, reset);
        Scene scene = new Scene(v, 150, 150);
        window.setScene(scene);
        window.show();
    }
}
