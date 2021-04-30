package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

import static ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import static ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;

final class DecksViewCreator {

    private DecksViewCreator() {}

    private static final int OUTSIDE_RECT_WIDTH = 60;
    private static final int OUTSIDE_RECT_HEIGHT = 90;
    private static final int INSIDE_RECT_WIDTH = 40;
    private static final int INSIDE_RECT_HEIGHT = 70;
    private static final int GAUGE_WIDTH = 50;
    private static final int GAUGE_HEIGHT = 5;

    private static final String DECKS_STYLES = "decks.css";
    private static final String COLORS_STYLES = "colors.css";
    private static final String TICKETS_ID = "tickets";
    private static final String HAND_PANE_ID = "hand-pane";
    private static final String CARD_PANE_ID = "hand-pane";
    private static final String CARD_CLASS = "card";
    private static final String OUTSIDE_RECT_CLASS = "outside";
    private static final String INSIDE_RECT_CLASS = "inside";
    private static final String FILLED_CLASS = "filled";
    private static final String TRAIN_IMAGE_CLASS = "train-image";
    private static final String COUNT_CLASS = "count";
    private static final String GAUGED_CLASS = "gauged";
    private static final String BACKGROUND_CLASS = "background";
    private static final String FOREROUND_CLASS = "foreground";
    private static final String NEUTRAL_CLASS = "NEUTRAL";

    static Node createHandView(ObservableGameState ogs) {
        HBox handView = new HBox();
        handView.getStylesheets().addAll(DECKS_STYLES, COLORS_STYLES);

        ListView<String> ticketsView = new ListView<>();
        ticketsView.setId(TICKETS_ID);
        handView.getChildren().add(ticketsView);

        HBox handPane = new HBox();
        handPane.setId(HAND_PANE_ID);

        for (Card card : Card.ALL) {
            StackPane stackPane = new StackPane();
            stackPane.getStyleClass().addAll(
                    card.color() == null
                            ? NEUTRAL_CLASS
                            : card.color().name(),
                    CARD_CLASS
            );
            // TODO: check la visibilité
            stackPane.setVisible(true);

            // Carte
            Rectangle outsideRect = new Rectangle(OUTSIDE_RECT_WIDTH, OUTSIDE_RECT_HEIGHT);
            outsideRect.getStyleClass().add(OUTSIDE_RECT_CLASS);

            Rectangle insideRect = new Rectangle(INSIDE_RECT_WIDTH, INSIDE_RECT_HEIGHT);
            insideRect.getStyleClass().addAll(FILLED_CLASS, INSIDE_RECT_CLASS);

            Rectangle trainRect = new Rectangle(INSIDE_RECT_WIDTH, INSIDE_RECT_HEIGHT);
            trainRect.getStyleClass().add(TRAIN_IMAGE_CLASS);

            // Compteur
            Text countText = new Text();
            countText.getStyleClass().add(COUNT_CLASS);

            // Ajout des enfants
            stackPane.getChildren().addAll(outsideRect, insideRect, trainRect, countText);
            handPane.getChildren().add(stackPane);
        }

        handView.getChildren().add(handPane);

        return handView;
    }

    static Node createCardsView(ObservableGameState ogs, ObjectProperty<DrawTicketsHandler> drawTickets, ObjectProperty<DrawCardHandler> drawCard) {
        VBox cardPane = new VBox();
        cardPane.setId(CARD_PANE_ID);
        cardPane.getStylesheets().addAll(DECKS_STYLES, COLORS_STYLES);

        Button ticketsBtn = createGaugedButton(StringsFr.TICKETS);
        Button deckBtn = createGaugedButton(StringsFr.CARDS);

        cardPane.getChildren().add(ticketsBtn);

        // TODO: boucler sur les faceUp
        for (Card card : List.of(Card.LOCOMOTIVE, Card.BLUE, Card.RED, Card.ORANGE, Card.RED)) {
            StackPane stackPane = new StackPane();
            stackPane.getStyleClass().addAll(
                    card.color() == null
                            ? NEUTRAL_CLASS
                            : card.color().name(),
                    CARD_CLASS
            );
            // TODO: check la visibilité
            cardPane.setVisible(true);

            // Carte
            Rectangle outsideRect = new Rectangle(OUTSIDE_RECT_WIDTH, OUTSIDE_RECT_HEIGHT);
            outsideRect.getStyleClass().add(OUTSIDE_RECT_CLASS);

            Rectangle insideRect = new Rectangle(INSIDE_RECT_WIDTH, INSIDE_RECT_HEIGHT);
            insideRect.getStyleClass().addAll(FILLED_CLASS, INSIDE_RECT_CLASS);

            Rectangle trainRect = new Rectangle(INSIDE_RECT_WIDTH, INSIDE_RECT_HEIGHT);
            trainRect.getStyleClass().add(TRAIN_IMAGE_CLASS);

            // Ajout des enfants
            stackPane.getChildren().addAll(outsideRect, insideRect, trainRect);
            cardPane.getChildren().add(stackPane);
        }

        cardPane.getChildren().add(deckBtn);

        return cardPane;
    }

    static Button createGaugedButton(String label) {
        Group gauge = new Group();

        Rectangle bgRect = new Rectangle(GAUGE_WIDTH, GAUGE_HEIGHT);
        bgRect.getStyleClass().add(BACKGROUND_CLASS);
        Rectangle fgRect = new Rectangle(GAUGE_WIDTH, GAUGE_HEIGHT);
        bgRect.getStyleClass().add(FOREROUND_CLASS);

        gauge.getChildren().addAll(bgRect, fgRect);

        Button button = new Button(label);
        button.getStyleClass().add(GAUGED_CLASS);
        button.setGraphic(gauge);

        return button;
    }

}
