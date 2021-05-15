package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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
    private static final String FOREGROUND_CLASS = "foreground";
    private static final String NEUTRAL_CLASS = "NEUTRAL";
    
    private static final class GaugedButton {
        
        private final Button button;
        private final Rectangle gaugeRect;
        
        private GaugedButton(String label) {
            Group gaugeGroup = new Group();
            
            Rectangle bgRect = new Rectangle(GAUGE_WIDTH, GAUGE_HEIGHT);
            bgRect.getStyleClass().add(BACKGROUND_CLASS);
            this.gaugeRect = new Rectangle(GAUGE_WIDTH, GAUGE_HEIGHT);
            this.gaugeRect.getStyleClass().add(FOREGROUND_CLASS);
            
            gaugeGroup.getChildren().addAll(bgRect, gaugeRect);
            
            this.button = new Button(label);
            this.button.getStyleClass().add(GAUGED_CLASS);
            this.button.setGraphic(gaugeGroup);
        }
        
        Button get() {
            return button;
        }
        
        void bindPercentage(ReadOnlyIntegerProperty percentageProperty) {
            gaugeRect.widthProperty().bind(
                    percentageProperty.multiply(GAUGE_WIDTH).divide(100));
        }
        
        <T> void bindDisable(ObjectProperty<T> handlerProperty) {
            button.disableProperty().bind(handlerProperty.isNull());
        }
        
        void setOnMouseClicked(EventHandler<? super MouseEvent> handler) {
            button.setOnMouseClicked(handler);
        }
        
    }
    
    private static StackPane createCardViewPane() {
        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().add(CARD_CLASS);
    
        Rectangle outsideRect = new Rectangle(OUTSIDE_RECT_WIDTH, OUTSIDE_RECT_HEIGHT);
        outsideRect.getStyleClass().add(OUTSIDE_RECT_CLASS);
    
        Rectangle insideRect = new Rectangle(INSIDE_RECT_WIDTH, INSIDE_RECT_HEIGHT);
        insideRect.getStyleClass().addAll(FILLED_CLASS, INSIDE_RECT_CLASS);
    
        Rectangle trainRect = new Rectangle(INSIDE_RECT_WIDTH, INSIDE_RECT_HEIGHT);
        trainRect.getStyleClass().add(TRAIN_IMAGE_CLASS);
    
        stackPane.getChildren().addAll(outsideRect, insideRect, trainRect);
        
        return stackPane;
    }
    
    private static String getCardStyleClass(Card card) {
        return card.color() == null
                ? NEUTRAL_CLASS
                : card.color().name();
    }

    static Node createHandView(ObservableGameState gameState) {
        HBox handView = new HBox();
        handView.getStylesheets().addAll(DECKS_STYLES, COLORS_STYLES);

        ListView<Ticket> ticketsView = new ListView<>(gameState.tickets());
        ticketsView.setId(TICKETS_ID);

        HBox handPane = new HBox();
        handPane.setId(HAND_PANE_ID);

        for (Card card : Card.ALL) {
            StackPane currentCardsPane = createCardViewPane();
            currentCardsPane.getStyleClass().add(getCardStyleClass(card));
    
            ReadOnlyIntegerProperty countP = gameState.cardCountOf(card);
            currentCardsPane.visibleProperty().bind(Bindings.greaterThan(countP, 0));

            // Compteur
            Text countText = new Text();
            countText.getStyleClass().add(COUNT_CLASS);
            countText.textProperty().bind(Bindings.convert(countP));
            countText.visibleProperty().bind(Bindings.greaterThan(countP, 1));

            // Ajout des enfants
            currentCardsPane.getChildren().add(countText);
            handPane.getChildren().add(currentCardsPane);
        }

        handView.getChildren().addAll(ticketsView, handPane);

        return handView;
    }

    static Node createCardsView(ObservableGameState gameState, ObjectProperty<DrawTicketsHandler> drawTicketsHP, ObjectProperty<DrawCardHandler> drawCardHP) {
        VBox cardPane = new VBox();
        cardPane.setId(CARD_PANE_ID);
        cardPane.getStylesheets().addAll(DECKS_STYLES, COLORS_STYLES);

        GaugedButton ticketsBtn = new GaugedButton(StringsFr.TICKETS);
        ticketsBtn.bindDisable(drawTicketsHP);
        ticketsBtn.bindPercentage(gameState.ticketsPercentage());
        ticketsBtn.setOnMouseClicked(e -> drawTicketsHP.get().onDrawTickets());
        
        GaugedButton cardsBtn = new GaugedButton(StringsFr.CARDS);
        cardsBtn.bindDisable(drawCardHP);
        cardsBtn.bindPercentage(gameState.cardsPercentage());
        cardsBtn.setOnMouseClicked(e -> drawCardHP.get().onDrawCard(Constants.DECK_SLOT));
    
        cardPane.getChildren().add(ticketsBtn.get());
    
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            StackPane faceUpPane = createCardViewPane();
            faceUpPane.disableProperty().bind(drawCardHP.isNull());
            
            gameState.faceUpCard(slot).addListener((o, oV, card) -> {
                faceUpPane.getStyleClass().clear();
                faceUpPane.getStyleClass().addAll(CARD_CLASS, getCardStyleClass(card));
            });
            
            faceUpPane.setOnMouseClicked(e -> drawCardHP.get().onDrawCard(slot));
    
            cardPane.getChildren().add(faceUpPane);
        }

        cardPane.getChildren().add(cardsBtn.get());

        return cardPane;
    }

}
