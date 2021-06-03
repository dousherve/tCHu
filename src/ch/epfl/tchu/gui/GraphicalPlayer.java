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
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Scale;
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
    
    private static final CardBagStringConverter BAG_TO_STRING_CONV = new CardBagStringConverter();
    private static final double DEFAULT_SCALE = 1.0d;
    private static final double SPACING = 10.0d;
    private static final int ICON_SIZE = 15;
    
    private final ObservableGameState gameState;
    private final PlayerId playerId;
    private final ObservableList<Text> infosText;
    
    private final BooleanProperty darkModeP;
    private final BooleanProperty soundWhenDrawCardsP;
    private final BooleanProperty soundWhenDrawTicketsP;
    private final BooleanProperty soundWhenChooseTicketsP;
    private final BooleanProperty soundWhenClaimRouteP;
    private final BooleanProperty soundWhenLateGameP;

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
    
    private static BooleanProperty createBooleanProperty() {
        return new SimpleBooleanProperty(true);
    }
    
    private static CheckMenuItem createSoundItemProperty(String text, BooleanProperty property) {
        CheckMenuItem item = new CheckMenuItem(text);
        property.bind(item.selectedProperty());
        item.setSelected(true);
        return item;
    }

    private static ObservableList<Text> createInfosTexts() {
        ObservableList<Text> list = FXCollections.observableArrayList();

        for (int i = 0; i < MAX_INFOS_COUNT; ++i)
            list.add(new Text());

        return list;
    }
    
    private static void resetSidePanes(BorderPane mainPane) {
        mainPane.setLeft(null);
        mainPane.setRight(null);
    }
    
    private void resetHandlers() {
        drawTicketsHP.set(null);
        drawCardHP.set(null);
        claimRouteHP.set(null);
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
        if (darkModeP.get())
            scene.getStylesheets().add(DARK_STYLES);
        
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
                BAG_TO_STRING_CONV
        );
    }
    
    private Menu createTchuMenu(Scene scene) {
        ImageView iconView = new ImageView(GuiUtils.TRAIN_ICON);
        iconView.setFitWidth(ICON_SIZE);
        iconView.setFitHeight(ICON_SIZE);
        
        Menu tchuMenu = new Menu(StringsFr.TCHU, iconView);
        
        MenuItem rulesItem = new MenuItem(StringsFr.RULES);
        
        MenuItem whistleItem = new MenuItem(StringsFr.WHISTLE);
        whistleItem.setOnAction(e -> playSound(TRAIN_SOUND));
        
        MenuItem quitItem = new MenuItem(StringsFr.QUIT);
        quitItem.setOnAction(e -> scene.getWindow().hide());
        
        tchuMenu.getItems().addAll(
                rulesItem,
                new SeparatorMenuItem(),
                whistleItem,
                new SeparatorMenuItem(),
                quitItem
        );
        
        return tchuMenu;
    }
    
    private Menu createViewMenu(Scene scene, Node mapView, Node cardsView, Node infoView, BorderPane mainPane) {
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
            resetSidePanes(mainPane);
        
            mainPane.setLeft(selected ? infoView : cardsView);
            mainPane.setRight(selected ? cardsView : infoView);
        });
    
        RadioMenuItem reversedLayoutItem = new RadioMenuItem(StringsFr.REVERSED_LAYOUT_ITEM);
    
        ToggleGroup layoutGroup = new ToggleGroup();
        layoutGroup.getToggles().addAll(normalLayoutItem, reversedLayoutItem);
    
        double delta = 0.25d;
        Slider scaleSlider = new Slider(
                DEFAULT_SCALE - delta,
                DEFAULT_SCALE + delta,
                DEFAULT_SCALE
        );
    
        Scale scale = new Scale(DEFAULT_SCALE, DEFAULT_SCALE);
        scale.xProperty().bind(scaleSlider.valueProperty());
        scale.yProperty().bind(scaleSlider.valueProperty());
    
        CustomMenuItem scaleSliderItem = new CustomMenuItem(
                new HBox(SPACING,
                        new Text(StringsFr.SCALE_SLIDER_LABEL),
                        scaleSlider
                ), false);
    
        MenuItem resetZoomItem = new MenuItem(StringsFr.RESET_ZOOM);
        resetZoomItem.setOnAction(e -> scaleSlider.setValue(DEFAULT_SCALE));
    
        MenuItem resetLayoutItem = new MenuItem(StringsFr.RESET_LAYOUT);
        resetLayoutItem.setOnAction(e -> {
            resetSidePanes(mainPane);
        
            mainPane.setLeft(infoView);
            mainPane.setRight(cardsView);
        
            scaleSlider.setValue(DEFAULT_SCALE);
            darkModeItem.setSelected(true);
        });
    
        viewMenu.getItems().addAll(
                darkModeItem,
                new SeparatorMenuItem(),
                normalLayoutItem,
                reversedLayoutItem,
                new SeparatorMenuItem(),
                scaleSliderItem,
                resetZoomItem,
                new SeparatorMenuItem(),
                resetLayoutItem
        );
    
        mapView.getTransforms().add(scale);
        
        darkModeItem.setSelected(true);
        normalLayoutItem.setSelected(true);
    
        return viewMenu;
    }
    
    private Menu createSoundMenu(Scene scene) {
        Menu soundMenu = new Menu(StringsFr.SOUND_MENU);
        
        CheckMenuItem drawCardsItem = createSoundItemProperty(
                StringsFr.SOUND_DRAW_CARDS,
                soundWhenDrawCardsP);
        CheckMenuItem drawTicketsItem = createSoundItemProperty(
                StringsFr.SOUND_DRAW_TICKETS,
                soundWhenDrawTicketsP);
        CheckMenuItem chooseTicketsItem = createSoundItemProperty(
                StringsFr.SOUND_CHOOSE_TICKETS,
                soundWhenChooseTicketsP);
        CheckMenuItem claimRouteItem = createSoundItemProperty(
                StringsFr.SOUND_CLAIM_ROUTE,
                soundWhenClaimRouteP);
        CheckMenuItem lateGameItem = createSoundItemProperty(
                StringsFr.SOUND_LATE_GAME,
                soundWhenLateGameP);
    
        List<CheckMenuItem> soundItems = List.of(
                drawCardsItem,
                drawTicketsItem,
                chooseTicketsItem,
                claimRouteItem,
                lateGameItem
        );
        
        MenuItem allSoundsOnItem = new MenuItem(StringsFr.ALL_SOUND_ON_ITEM);
        allSoundsOnItem.setOnAction(e -> soundItems.forEach(item -> item.setSelected(true)));
    
        MenuItem allSoundsOffItem = new MenuItem(StringsFr.ALL_SOUND_OFF_ITEM);
        allSoundsOffItem.setOnAction(e -> soundItems.forEach(item -> item.setSelected(false)));
        
        soundMenu.getItems().addAll(
                allSoundsOnItem,
                allSoundsOffItem,
                new SeparatorMenuItem(),
                drawCardsItem,
                new SeparatorMenuItem(),
                drawTicketsItem,
                chooseTicketsItem,
                new SeparatorMenuItem(),
                claimRouteItem,
                new SeparatorMenuItem(),
                lateGameItem
        );
        
        return soundMenu;
    }
    
    private Stage createMainWindow(PlayerId playerId, Map<PlayerId, String> playerNames) {
        Preconditions.checkArgument(playerNames.size() == PlayerId.COUNT);
        
        Stage stage = new Stage();
        stage.setTitle(String.format(WINDOW_TITLE, playerNames.get(playerId)));
    
        Node mapView = MapViewCreator
                .createMapView(gameState, claimRouteHP, this::chooseClaimCards, darkModeP);
        Node cardsView = DecksViewCreator
                .createCardsView(gameState, drawTicketsHP, drawCardHP, darkModeP);
        Node handView = DecksViewCreator
                .createHandView(gameState, darkModeP);
        Node infoView = InfoViewCreator
                .createInfoView(playerId, playerNames, gameState, infosText);
        
        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, infoView);
    
        Scene scene = new Scene(mainPane);
        
        // Barre de menus
        mainPane.setTop(new MenuBar(
                createTchuMenu(scene),
                createViewMenu(scene, mapView, cardsView, infoView, mainPane),
                createSoundMenu(scene)
        ));
    
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
        
        this.darkModeP = createBooleanProperty();
        this.soundWhenDrawCardsP = createBooleanProperty();
        this.soundWhenDrawTicketsP = createBooleanProperty();
        this.soundWhenChooseTicketsP = createBooleanProperty();
        this.soundWhenLateGameP = createBooleanProperty();
        this.soundWhenClaimRouteP = createBooleanProperty();
        
        this.drawTicketsHP = createObjectProperty();
        this.drawCardHP = createObjectProperty();
        this.claimRouteHP = createObjectProperty();
        
        this.mainWindow = createMainWindow(playerId, playerNames);
        
        gameState.carCount(playerId).addListener((o, oV, count) -> {
            if (
                count.intValue() <= Constants.LAST_TURN_CAR_COUNT_THRESHOLD 
                && soundWhenLateGameP.get()
            ) playSound(TRAIN_SOUND);
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
                    if (soundWhenDrawTicketsP.get())
                        playSound(CARD_SOUND, 3);
                    resetHandlers();
                }
                : null
        );
        drawCardHP.set(
                gameState.canDrawCards()
                ? slot -> {
                    drawCardH.onDrawCard(slot);
                    if (soundWhenDrawCardsP.get())
                        playSound(CARD_SOUND);
                    resetHandlers();
                }
                : null
        );
        claimRouteHP.set((route, initialCards) -> {
            claimRouteH.onClaimRoute(route, initialCards);
            gameState.routeOwner(route).addListener((o, oV, owner) -> {
                if (owner == playerId && soundWhenClaimRouteP.get())
                    playSound(HAMMER_SOUND);
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
                    if (soundWhenChooseTicketsP.get())
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
            if (soundWhenDrawCardsP.get())
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
