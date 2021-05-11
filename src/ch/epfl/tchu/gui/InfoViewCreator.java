package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

final class InfoViewCreator {

    private static final String INFO_STYLES = "infos.css";
    private static final String COLORS_STYLES = "colors.css";

    private static final String PLAYER_STATS_ID = "player-stats";
    private static final String GAME_INFO_ID = "game-info";

    private static final String FILLED_CLASS = "filled";

    private static final int CIRCLE_STAT_RADIUS = 5;

    public VBox createInfoView(PlayerId playerId, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> infoTexts) {
        VBox infoView = new VBox();
        infoView.getStylesheets().addAll(INFO_STYLES, COLORS_STYLES);

        // Statistiques des joueurs
        VBox playerStats = new VBox();
        playerStats.setId(PLAYER_STATS_ID);

        for (int i = 0; i < PlayerId.COUNT; ++i) {
            final PlayerId id = (i == 0) ? playerId : playerId.next();

            Circle circle = new Circle(CIRCLE_STAT_RADIUS);
            circle.getStyleClass().add(FILLED_CLASS);

            Text text = new Text();

            text.textProperty().bind(
                    Bindings.format(StringsFr.PLAYER_STATS,
                        playerNames.get(id),
                        gameState.ticketCount(id),
                        gameState.cardCount(id),
                        gameState.carCount(id),
                        gameState.claimPoints(id))
            );

            TextFlow statsTF = new TextFlow(circle, text);
            statsTF.getStyleClass().add(id.name());
            statsTF.getChildren().addAll(circle, text);

            playerStats.getChildren().add(statsTF);
        }

        Separator separator = new Separator(Orientation.HORIZONTAL);

        // Messages
        TextFlow messagesTF = new TextFlow();
        messagesTF.setId(GAME_INFO_ID);
        Bindings.bindContent(messagesTF.getChildren(), infoTexts);

        // Ajout des enfants
        infoView.getChildren().addAll(playerStats, separator, messagesTF);

        return infoView;
    }

}
