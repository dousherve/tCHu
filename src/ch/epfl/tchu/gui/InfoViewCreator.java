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

import static ch.epfl.tchu.gui.ConstantsGui.*;

/**
 * Classe finale et non instanciable permettant de créer
 * la vue des informations, située dans la partie gauche
 * de l'interface graphique d'un joueur de tCHu.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
final class InfoViewCreator {
    
    private InfoViewCreator() {}
    
    /**
     * Méthode permettant de créer la vue des informations.
     * 
     * @param playerId
     *          l'identité du joueur auquel l'interface correspond
     * @param playerNames
     *          la table associative des noms des joueurs
     * @param gameState
     *          l'état de jeu observable
     * @param infoTexts
     *          une liste observable contenant les informations
     *          sur le déroulement de la partie, sous la forme
     *          d'instances de {@link Text}
     * @return
     *          la vue des informations
     */
    public static VBox createInfoView(PlayerId playerId, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> infoTexts) {
        VBox infoView = new VBox();
        infoView.getStylesheets().addAll(INFO_STYLES, COLORS_STYLES);

        // Statistiques des joueurs
        VBox playerStats = new VBox();
        playerStats.setId(PLAYER_STATS_ID);

        for (int i = 0; i < PlayerId.COUNT; ++i) {
            final PlayerId id = (i == 0) ? playerId : playerId.next();

            Circle circle = new Circle(STATS_CIRCLE_RADIUS);
            circle.getStyleClass().add(FILLED_CLASS);

            Text statsText = new Text();

            statsText.textProperty().bind(
                    Bindings.format(StringsFr.PLAYER_STATS,
                        playerNames.get(id),
                        gameState.ticketCount(id),
                        gameState.cardCount(id),
                        gameState.carCount(id),
                        gameState.claimPoints(id))
            );

            TextFlow statsTF = new TextFlow();
            statsTF.getStyleClass().add(id.name());
            statsTF.getChildren().addAll(circle, statsText);

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
