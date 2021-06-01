package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.File;

import static ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import static ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import static ch.epfl.tchu.gui.ConstantsGui.*;

/**
 * Classe finale et non instanciable permettant de créer
 * la vue de la main du joueur ainsi que la vue des cartes.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
final class DecksViewCreator {

    private DecksViewCreator() {}
    
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
        
        private <T> void bindPropertiesAndEvent(ObjectProperty<T> handlerP, ReadOnlyIntegerProperty percentageP, EventHandler<? super MouseEvent> handler) {
            bindDisable(handlerP);
            bindPercentage(percentageP);
            button.setOnMouseClicked(handler);
        }
        
        void bindPercentage(ReadOnlyIntegerProperty percentageProperty) {
            gaugeRect.widthProperty().bind(
                    percentageProperty.multiply(GAUGE_WIDTH).divide(100));
        }
        
        <T> void bindDisable(ObjectProperty<T> handlerProperty) {
            button.disableProperty().bind(handlerProperty.isNull());
        }
    }
    
    private static StackPane createCardViewPane() {
        Rectangle outsideRect = new Rectangle(OUTSIDE_CARD_WIDTH, OUTSIDE_CARD_HEIGHT);
        outsideRect.getStyleClass().add(OUTSIDE_CARD_CLASS);
    
        Rectangle insideRect = new Rectangle(INSIDE_CARD_WIDTH, INSIDE_CARD_HEIGHT);
        insideRect.getStyleClass().addAll(FILLED_CLASS, INSIDE_CARD_CLASS);
    
        Rectangle trainRect = new Rectangle(INSIDE_CARD_WIDTH, INSIDE_CARD_HEIGHT);
        trainRect.getStyleClass().add(TRAIN_IMAGE_CLASS);
    
        StackPane stackPane = new StackPane(outsideRect, insideRect, trainRect);
        stackPane.getStyleClass().add(CARD_CLASS);
        
        return stackPane;
    }
    
    private static String getCardStyleClass(Card card) {
        return card.color() == null
                ? NEUTRAL_CLASS
                : card.color().name();
    }
    
    /**
     * Méthode permettant de créer la vue de la main du joueur.
     * 
     * @param gameState
     *          l'état du jeu observable
     * @throws NullPointerException
     *          si <code>gameState</code> vaut <code>null</code>
     * @return
     *          la vue de la main du joueur
     */
    public static Node createHandView(ObservableGameState gameState) {
        Preconditions.requireNonNull(gameState);
        
        HBox handView = new HBox();
        handView.getStylesheets().addAll(DECKS_STYLES, COLORS_STYLES);

        ListView<Ticket> ticketsView = new ListView<>(gameState.tickets());
        ticketsView.setId(TICKETS_ID);

        HBox handPane = new HBox();
        handPane.setId(ConstantsGui.CARD_PANE_ID);

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
    
    /**
     * Méthode permettant de créer la vue des pioches de cartes et de billets,
     * ainsi que des cartes face visible.
     * 
     * @param gameState
     *          l'état du jeu observable
     * @param drawTicketsHP
     *          une propriété contenant un gestionnaire de tirage de billets
     * @param drawCardHP
     *          une propriété contenant un gestionnaire de tirage de cartes
     * @throws NullPointerException
     *          si un des arguments vaut <code>null</code>
     * @return
     *          la vue des pioches de cartes et de billets,
     *          ainsi que des cartes face visible
     */
    public static Node createCardsView(ObservableGameState gameState, ObjectProperty<DrawTicketsHandler> drawTicketsHP, ObjectProperty<DrawCardHandler> drawCardHP) {
        Preconditions.requireNonNull(gameState, drawTicketsHP, drawCardHP);
        
        VBox cardPane = new VBox();
        cardPane.setId(CARD_PANE_ID);
        cardPane.getStylesheets().addAll(DECKS_STYLES, COLORS_STYLES);

        GaugedButton ticketsBtn = new GaugedButton(StringsFr.TICKETS);
        ticketsBtn.bindPropertiesAndEvent(
                drawTicketsHP,
                gameState.ticketsPercentage(),
                e -> drawTicketsHP.get().onDrawTickets()
        );
        
        GaugedButton cardsBtn = new GaugedButton(StringsFr.CARDS);
        cardsBtn.bindPropertiesAndEvent(
                drawCardHP,
                gameState.cardsPercentage(),
                e -> drawCardHP.get().onDrawCard(Constants.DECK_SLOT)
        );
    
        cardPane.getChildren().add(ticketsBtn.get());
    
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            StackPane faceUpPane = createCardViewPane();
            faceUpPane.disableProperty().bind(drawCardHP.isNull());
            
            gameState.faceUpCard(slot).addListener((o, oV, card) -> {
                faceUpPane.getStyleClass().clear();
                faceUpPane.getStyleClass().addAll(CARD_CLASS, getCardStyleClass(card));
            });
    
    
    
            faceUpPane.setOnMouseClicked(e -> {
                drawCardHP.get().onDrawCard(slot);
                new MediaPlayer(new Media(
                        new File("resources/card.wav").toURI().toString())
                ).play();
            });
    
            cardPane.getChildren().add(faceUpPane);
        }

        cardPane.getChildren().add(cardsBtn.get());

        return cardPane;
    }

}
