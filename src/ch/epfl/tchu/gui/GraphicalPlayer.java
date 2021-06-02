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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import static ch.epfl.tchu.gui.GuiUtils.*;
import static javafx.application.Platform.isFxApplicationThread;

/**
 * Classe publique, finale et instanciable représentant
 * l'interface graphique d'un joueur de tCHu.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class GraphicalPlayer {
    
    private static final CardBagStringConverter CARD_BAG_STRING_CONVERTER = new CardBagStringConverter();
    
    private final ObservableGameState gameState;
    private final PlayerId playerId;
    private final ObservableList<Text> infosText;
    
    private final BooleanProperty darkModeP;

    private final ObjectProperty<DrawTicketsHandler> drawTicketsHP;
    private final ObjectProperty<DrawCardHandler> drawCardHP;
    private final ObjectProperty<ClaimRouteHandler> claimRouteHP;
    
    private final Stage mainWindow;
    
    private static final class CardBagStringConverter extends StringConverter<SortedBag<Card>> {
        
        @Override
        public String toString(SortedBag<Card> cards) {
            return Info.cardsDescription(cards);
        }
        
        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
        
    }
    
    private static <T> SimpleObjectProperty<T> createObjectProperty() {
        return new SimpleObjectProperty<>(null);
    }

    private static ObservableList<Text> createInfosTexts() {
        ObservableList<Text> list = FXCollections.observableArrayList();

        for (int i = 0; i < MAX_INFOS_COUNT; ++i)
            list.add(new Text());

        return list;
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
        scene.getStylesheets().add(CHOOSER_STYLES);
        
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
        Preconditions.checkArgument(playerNames.size() == PlayerId.COUNT);
        Stage stage = new Stage();
        stage.setTitle(String.format(WINDOW_TITLE, playerNames.get(playerId)));
    
        Node mapView = MapViewCreator
                .createMapView(gameState, claimRouteHP, this::chooseClaimCards, darkModeP);
        Node cardsView = DecksViewCreator
                .createCardsView(gameState, drawTicketsHP, drawCardHP);
        Node handView = DecksViewCreator
                .createHandView(gameState);
        Node infoView = InfoViewCreator
                .createInfoView(playerId, playerNames, gameState, infosText);
    
        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, infoView);
    
        Scene scene = new Scene(mainPane);
        
        MenuBar mb = new MenuBar();
        
        Menu viewMenu = new Menu(StringsFr.VIEW_MENU);
        CheckMenuItem darkModeItem = new CheckMenuItem(StringsFr.DARK_MODE);
        
        darkModeP.bind(darkModeItem.selectedProperty());
        
        darkModeItem.selectedProperty().addListener((o, oV, selected) -> {
            var stylesheets = scene.getStylesheets();
            stylesheets.clear();
            if (selected) stylesheets.add(DARK_STYLES);
        });
    
        RadioMenuItem normalLayoutItem = new RadioMenuItem(StringsFr.NORMAL_LAYOUT_ITEM);
        normalLayoutItem.selectedProperty().addListener((o, oV, selected) -> {
            mainPane.setLeft(null);
            mainPane.setRight(null);
            
            mainPane.setLeft(selected ? infoView : cardsView);
            mainPane.setRight(selected ? cardsView : infoView);
        });
        
        RadioMenuItem reversedLayoutItem = new RadioMenuItem(StringsFr.REVERSED_LAYOUT_ITEM);
        
        ToggleGroup layoutGroup = new ToggleGroup();
        layoutGroup.getToggles().addAll(normalLayoutItem, reversedLayoutItem);
        
        viewMenu.getItems().add(darkModeItem);
        viewMenu.getItems().add(new SeparatorMenuItem());
        viewMenu.getItems().addAll(normalLayoutItem, reversedLayoutItem);
    
        mb.getMenus().add(viewMenu);
    
        mainPane.setTop(mb);
        
        darkModeItem.setSelected(true);
        normalLayoutItem.setSelected(true);
        
        stage.setScene(scene);
        stage.show();
        
        return stage;
    }
    
    /**
     * Construit l'interface graphique d'un joueur de tCHu, constitué d'une fenêtre
     * découpée en 4 zones : au centre, la carte du jeu ; à gauche, les informations
     * concernant le déroulement de la partie ; à droite, les pioches de cartes
     * et de billets ; et en bas, la vue de la main du joueur, billets et cartes.
     * 
     * @param playerId
     *          l'identité du joueur auquel cette instance correspond
     * @param playerNames
     *          la table associative des noms des joueurs
     * @throws NullPointerException
     *          si l'identité du joueur ou bien la table associative vaut <code>null</code>
     * @throws IllegalArgumentException
     *          si la taille de la table associative n'est pas égale
     *          au nombre de joueurs d'une partie de tCHu
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        assert isFxApplicationThread();
        Preconditions.requireNonNull(playerId, playerNames);
        Preconditions.checkArgument(playerNames.size() == PlayerId.COUNT);
        
        this.gameState = new ObservableGameState(playerId);
        this.playerId = playerId;
        this.infosText = createInfosTexts();
        
        this.darkModeP = new SimpleBooleanProperty(true);
        
        this.drawTicketsHP = createObjectProperty();
        this.drawCardHP = createObjectProperty();
        this.claimRouteHP = createObjectProperty();
        
        this.mainWindow = createMainWindow(playerId, playerNames);
        
        gameState.carCount(playerId).addListener((o, oV, count) -> {
            if (count.intValue() <= Constants.LAST_TURN_CAR_COUNT_THRESHOLD)
                playSound(TRAIN_SOUND);
        });
    }
    
    /**
     * Appelle la méthode correspondante sur l'état de jeu courant.
     * 
     * @param newGameState
     *          le nouvel état public de jeu
     * @param newPlayerState
     *          le nouvel état public du joueur
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        assert isFxApplicationThread();
        gameState.setState(newGameState, newPlayerState);
    }
    
    /**
     * Ajoute le message donné au bas des informations sur le déroulement
     * de la partie. Cette zone contient au maximum 5 informations (<code>ConstantsGui.MAX_INFOS_COUNT</code>).
     *
     * @param info
     *          l'information à ajouter
     */
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

    /**
     * Permet au joueur d'effectuer une action.
     *
     * @param drawTicketsH
     *          gestionnaire d'action de tirage de billets
     * @param drawCardH
     *          gestionnaire d'action de tirage de cartes
     * @param claimRouteH
     *          gestionnaire d'action de prise de route
     */
    public void startTurn(DrawTicketsHandler drawTicketsH, DrawCardHandler drawCardH, ClaimRouteHandler claimRouteH) {
        assert isFxApplicationThread();
        
        drawTicketsHP.set(
                gameState.canDrawTickets()
                ? () -> {
                    drawTicketsH.onDrawTickets();
                    playSound(CARD_SOUND, 3);
                    resetHandlers();
                }
                : null
        );
        drawCardHP.set(
                gameState.canDrawCards()
                ? slot -> {
                    drawCardH.onDrawCard(slot);
                    playSound(CARD_SOUND);
                    resetHandlers();
                }
                : null
        );
        claimRouteHP.set((route, initialCards) -> {
            claimRouteH.onClaimRoute(route, initialCards);
            gameState.routeOwner(route).addListener((o, oV, owner) -> {
                if (owner == playerId) playSound(HAMMER_SOUND);
            });
            resetHandlers();
        });
    }

    /**
     * Permet au joueur de faire son choix de billets.
     * Une fois celui-ci confirmé, le gestionnaire de choix
     * est appelé avec ce choix en argument.
     *
     * @param drawnTickets
     *          multiensemble de billets que le joueur peut choisir
     * @param chooseTicketsH
     *          gestionnaire d'action de choix de billets
     * @throws IllegalArgumentException
     *          si le nombre de billets donnés n'est pas égal à
     *          <code>Constants.IN_GAME_TICKETS_COUNT</code> ou à
     *          <code>Constants.INITIAL_TICKETS_COUNT</code>
     */
    public void chooseTickets(SortedBag<Ticket> drawnTickets, ChooseTicketsHandler chooseTicketsH) {
        assert isFxApplicationThread();
        
        int ticketsCount = drawnTickets.size();
        Preconditions.checkArgument(
                ticketsCount == Constants.IN_GAME_TICKETS_COUNT ||
                        ticketsCount == Constants.INITIAL_TICKETS_COUNT
        );
        
        int minSelected = ticketsCount - Constants.DISCARDABLE_TICKETS_COUNT;
        
        showModalWindow(
                StringsFr.TICKETS_CHOICE,
                String.format(StringsFr.CHOOSE_TICKETS, minSelected, StringsFr.plural(minSelected)),
                drawnTickets.toList(),
                minSelected,
                model -> {
                    var selected = SortedBag.of(model.getSelectedItems());
                    chooseTicketsH.onChooseTickets(selected);
                    playSound(CARD_SOUND, selected.size());
                },
                SelectionMode.MULTIPLE,
                null
        );
    }

    /**
     * Autorise le joueur à choisir une carte wagon/locomotive :
     *          soit l'une des cinq dont la face est visible,
     *          soit celle du sommet de la pioche.
     *
     * Une fois que le joueur a cliqué sur l'une de ces cartes, le gestionnaire
     * est appelé avec le choix du joueur et cette méthode est destinée
     * à être appelée lorsque le joueur a déjà tiré une première carte et doit
     * maintenant tirer la seconde.
     *
     * @param drawCardH
     *          gestionnaire d'action de tirage de cartes
     */
    public void drawCard(DrawCardHandler drawCardH) {
        assert isFxApplicationThread();
        drawCardHP.set(slot -> {
            drawCardH.onDrawCard(slot);
            playSound(CARD_SOUND);
            resetHandlers();
        });
    }

    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix de cartes initiales
     * pour s'emparer d'une route. Une fois que celui-ci a été fait et confirmé,
     * le gestionnaire de choix est appelé avec le choix du joueur en argument.
     *
     * Cette méthode n'est destinée qu'à être passée en argument à createMapView
     * en tant que valeur de type CardChooser.
     *
     * @param initialCards
     *          liste de multiensembles de cartes, qui sont les cartes initiales
     *          que le joueur peut utiliser pour s'emparer d'une route
     * @param chooseCardsH
     *          gestionnaire de choix de cartes
     */
    public void chooseClaimCards(List<SortedBag<Card>> initialCards, ChooseCardsHandler chooseCardsH) {
        assert isFxApplicationThread();
        chooseCards(StringsFr.CHOOSE_CARDS, initialCards, 1, chooseCardsH);
    }

    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix de cartes additionnelles
     * pour s'emparer d'un tunnel. Une fois que celui-ci a été fait et confirmé,
     * le gestionnaire de choix est appelé avec le choix du joueur en argument.
     *
     * @param options
     *          liste de multiensembles de cartes, qui sont les cartes additionnelles
     *          que le joueur peut utiliser pour s'emparer d'un tunnel
     * @param chooseCardsH
     *          gestionnaire de choix de cartes
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> options, ChooseCardsHandler chooseCardsH) {
        assert isFxApplicationThread();
        chooseCards(StringsFr.CHOOSE_ADDITIONAL_CARDS, options, 0, chooseCardsH);
    }

}
