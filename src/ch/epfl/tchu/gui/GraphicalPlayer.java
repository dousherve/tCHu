package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ch.epfl.tchu.gui.ActionHandlers.*;
import static javafx.application.Platform.isFxApplicationThread;

// TODO: regarder la visibilité 
public final class GraphicalPlayer {
    
    private static final int MAX_INFOS_COUNT = 5;
    private static final String WINDOW_TITLE = "tCHu \u2014 %s";
    private static final String CHOOSER_CLASS = "chooser.css";
    private static final CardBagStringConverter CARD_BAG_STRING_CONVERTER = new CardBagStringConverter();
    
    private final ObservableGameState gameState;
    private final ObservableList<Text> infosText;

    private final ObjectProperty<DrawTicketsHandler> drawTicketsHP;
    private final ObjectProperty<DrawCardHandler> drawCardHP;
    private final ObjectProperty<ClaimRouteHandler> claimRouteHP;
    
    private final Stage mainWindow;

    private static <T> SimpleObjectProperty<T> createObjectProperty() {
        return new SimpleObjectProperty<>(null);
    }

    private static ObservableList<Text> createInfosTexts() {
        ObservableList<Text> list = FXCollections.observableArrayList();

        for (int i = 0; i < MAX_INFOS_COUNT; ++i)
            list.add(new Text());

        return list;
    }
    
    private static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {
    
        @Override
        public String toString(SortedBag<Card> cards) {
            return Info.cardsDescription(cards);
        }
    
        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    
    }
    
    private <T> void showModalWindow(String title, String intro, List<T> options, int minSelected, Consumer<MultipleSelectionModel<T>> btnHandler, SelectionMode selectionMode, StringConverter<T> converter) {
        // Stage de la fenêtre modale
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle(title);
        stage.initOwner(mainWindow);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(Event::consume);
        
        // Conteneur du texte d'introduction
        TextFlow tf = new TextFlow(new Text(intro));
    
        // ListView des choix possibles
        // TODO: amélioration : ne pas avoir besoin de presser Cmd (ou Ctrl) pour les choix multiples ?
        ListView<T> listView = new ListView<>(FXCollections.observableArrayList(options));
        var selectionModel = listView.getSelectionModel();
        selectionModel.setSelectionMode(selectionMode);
        if (minSelected > 0) selectionModel.selectFirst();
        if (converter != null)
            listView.setCellFactory(v -> new TextFieldListCell<>(converter));
    
        // Bouton "Choisir"
        Button chooseBtn = new Button(StringsFr.CHOOSE);
        chooseBtn.disableProperty().bind(
                Bindings.size(selectionModel.getSelectedItems()).lessThan(minSelected));
        chooseBtn.setOnAction(e -> {
            stage.hide();
            btnHandler.accept(selectionModel);
        });
        
        // Conteneur vertical principal de la fenêtre modale
        VBox vbox = new VBox(tf, listView, chooseBtn);
        
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(CHOOSER_CLASS);
        
        stage.setScene(scene);
        stage.show();
    }
    
    private void chooseCards(String introText, List<SortedBag<Card>> options, int minSelected, ChooseCardsHandler chooseCardsH) {
        showModalWindow(
                StringsFr.CARDS_CHOICE,
                introText,
                options,
                minSelected,
                model -> {
                    SortedBag<Card> selected = model.getSelectedItem();
                    chooseCardsH.onChooseCards(selected != null ? selected : SortedBag.of());
                },
                SelectionMode.SINGLE,
                CARD_BAG_STRING_CONVERTER
        );
    }
    
    private void resetHandlers() {
        drawTicketsHP.set(null);
        drawCardHP.set(null);
        claimRouteHP.set(null);
    }
    
    private Stage createMainWindow(PlayerId playerId, Map<PlayerId, String> playerNames) {
        Stage stage = new Stage();
        stage.setTitle(String.format(WINDOW_TITLE, playerNames.get(playerId)));
    
        Node mapView = MapViewCreator
                .createMapView(gameState, claimRouteHP, this::chooseClaimCards);
        Node cardsView = DecksViewCreator
                .createCardsView(gameState, drawTicketsHP, drawCardHP);
        Node handView = DecksViewCreator
                .createHandView(gameState);
        Node infoView = InfoViewCreator
                .createInfoView(playerId, playerNames, gameState, infosText);
    
        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, infoView);
        stage.setScene(new Scene(mainPane));
        stage.show();
        
        return stage;
    }

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        assert isFxApplicationThread();
        
        this.gameState = new ObservableGameState(playerId);
        this.infosText = createInfosTexts();
        
        this.drawTicketsHP = createObjectProperty();
        this.drawCardHP = createObjectProperty();
        this.claimRouteHP = createObjectProperty();
        
        this.mainWindow = createMainWindow(playerId, playerNames);
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        assert isFxApplicationThread();
        gameState.setState(newGameState, newPlayerState);
    }

    public void receiveInfo(String info) {
        assert isFxApplicationThread();
        
        List<String> messages = infosText.stream()
                .map(Text::getText)
                .filter(t -> ! t.isBlank())
                .collect(Collectors.toList());
        
        messages.add(info);
        
        if (messages.size() > MAX_INFOS_COUNT) {
            messages = messages.subList(
                    messages.size() - MAX_INFOS_COUNT, messages.size());
        }

        for (int i = 0; i < messages.size(); ++i)
            infosText.get(i).setText(messages.get(i));
    }

    public void startTurn(DrawTicketsHandler drawTicketsH, DrawCardHandler drawCardH, ClaimRouteHandler claimRouteH) {
        assert isFxApplicationThread();
        
        drawTicketsHP.set(
                gameState.canDrawTickets()
                ? () -> {
                    drawTicketsH.onDrawTickets();
                    resetHandlers();
                }
                : null
        );
        drawCardHP.set(
                gameState.canDrawCards()
                ? slot -> {
                    drawCardH.onDrawCard(slot);
                    resetHandlers();
                }
                : null
        );
        claimRouteHP.set((route, initialCards) -> {
            claimRouteH.onClaimRoute(route, initialCards);
            resetHandlers();
        });
    }

    public void chooseTickets(SortedBag<Ticket> drawnTickets, ChooseTicketsHandler chooseTicketsH) {
        assert isFxApplicationThread();
        
        int ticketsCount = drawnTickets.size();
        Preconditions.checkArgument(ticketsCount == 3 || ticketsCount == 5);
        
        int minSelected = ticketsCount - Constants.DISCARDABLE_TICKETS_COUNT;
        
        showModalWindow(
                StringsFr.TICKETS_CHOICE,
                String.format(StringsFr.CHOOSE_TICKETS, minSelected, StringsFr.plural(minSelected)),
                drawnTickets.toList(),
                minSelected,
                model -> chooseTicketsH.onChooseTickets(SortedBag.of(model.getSelectedItems())),
                SelectionMode.MULTIPLE,
                null
        );
    }

    public void drawCard(DrawCardHandler drawCardH) {
        assert isFxApplicationThread();
        drawCardHP.set(slot -> {
            drawCardH.onDrawCard(slot);
            resetHandlers();
        });
    }

    public void chooseClaimCards(List<SortedBag<Card>> initialCards, ChooseCardsHandler chooseCardsH) {
        assert isFxApplicationThread();
        chooseCards(StringsFr.CHOOSE_CARDS, initialCards, 1, chooseCardsH);
    }

    public void chooseAdditionalCards(List<SortedBag<Card>> options, ChooseCardsHandler chooseCardsH) {
        assert isFxApplicationThread();
        chooseCards(StringsFr.CHOOSE_ADDITIONAL_CARDS, options, 0, chooseCardsH);
    }

}
