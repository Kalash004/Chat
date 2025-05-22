package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view;


import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.*;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.commands.*;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.fxml_controllers.*;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.tictactoe_service.TictactoeGameHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeGameStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.ui_link_utils.Page;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.IListen;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.sort_utils.MessageHistorySorter;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.sort_utils.PeerSorter;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.tictactoe_service.TictactoeStatusReports;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.ui_link_utils.CSSLinker;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.ui_link_utils.FXMLLinker;

import java.io.IOException;
import java.util.*;

/**
 * UI & mini game controller,
 * possible use for minigames such as tic tac toe in chat
 *
 */
public class UiController extends Application implements IListen {
    private static final Logger logger = LoggerFactory.getLogger(UiController.class);
    private final HashMap<EventType, ICommand> commandsRegistry;
    private final FXMLLoader fxmlLoader;
    private static UiController instance;
    private static final EventHandler eventHandler = EventHandler.getInstance();
    private MainMenuController mainMenuController;
    private TictactoeController tictactoeController;
    private PublicChatController publicChatController;
    private PrivateChatController privateChatController;
    private OptionsController optionsController;
    private FatalErrorController fatalErrorController;
    private Page currentPage;
    private Page previousPage;
    private boolean darkMode = true;

    private Peer[] peerList;
    private HashMap<String, ArrayList<Message>> messageHistory;
    private int selectedPeerIndex;
    private String peerSortOption;
    private TictactoeGameHandler gameHandler;

    public void setPeerSortOption(String peerSortOption) {
        this.peerSortOption = peerSortOption;
    }

    public UiController() {
        this.gameHandler = TictactoeGameHandler.getInstance();
        this.commandsRegistry = new HashMap<>(Map.ofEntries(
                Map.entry(EventType.ADD_PEER, new CommandAddPeer(this)),
                Map.entry(EventType.CONNECTION_REQUESTED, new CommandAddPeer(this)),
                Map.entry(EventType.ADD_MESSAGE_TO_HISTORY, new CommandAddMessage(this)),
                Map.entry(EventType.HANDLE_PUBLIC_MESSAGE, new CommandAddMessage(this)),
                Map.entry(EventType.ADD_PRIVATE_MESSAGE_TO_HISTORY, new CommandAddPrivateMessage(this)),
                Map.entry(EventType.REMOVE_PEER, new CommandRemovePeer(this)),
                Map.entry(EventType.CONFIRM_RECEIVE_TTT_INVITE, new CommandConfirmReceiveTTTInvite(gameHandler)),
                Map.entry(EventType.REFRESH_MESSAGE_HISTORY, new CommandRefreshMessageHistory(this)),
                Map.entry(EventType.RECEIVE_TTT_STATUS, new CommandReceiveTTTStatus(gameHandler)),
                Map.entry(EventType.SHOW_FATAL_ERROR, new CommandShowFatalError(this)),
                Map.entry(EventType.RECEIVE_TTT_INVITE, new CommandReceiveTTTInvite(gameHandler))
        ));
        this.peerList = new Peer[]{};
        this.fxmlLoader = new FXMLLoader(UiController.class.getResource(FXMLLinker.PAGE_LINKS.get(Page.MAIN_MENU)));
        this.selectedPeerIndex = -1;
        this.peerSortOption = PeerSorter.SORT_OPTIONS.get(0);
        this.previousPage = Page.MAIN_MENU;
        this.currentPage = Page.MAIN_MENU;
    }

    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Starting UI controller.");
        openPage(Page.MAIN_MENU, stage);
        stage.setOnCloseRequest(event -> {
            StopProgramEvent stopProgramEvent = new StopProgramEvent();
            EventHandler.getInstance().handleEvent(stopProgramEvent);
        });
    }

    public void launchUi() {
        Application.launch();
    }

    public static UiController getInstance() {
        // Block non main calls
//        String allowedCaller = "cz.cvut.fel.pjv.semwork.peer_to_peer_chat._main.Application"; // fully-qualified class name
//
//        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
//        if (stackTrace.length > 2) {
//            String callerClassName = stackTrace[2].getClassName();
//            if (!callerClassName.equals(allowedCaller)) {
//                throw new SecurityException("Unauthorized caller: " + callerClassName + " PLEASE USE EVENTS OR START FROM " + allowedCaller);
//            }
//        }
        // end

        if (instance == null) {
            instance = new UiController();

        }
        return instance;
    }

    /**
     * If the stage is not given, try to get it from the last visited page
     */
    private Stage getLastStage() {
        Stage stage = null;
        switch (currentPage) {
            case PUBLIC_CHAT: {
                stage = publicChatController.getStage();
                break;
            }
            case PRIVATE_CHAT: {
                stage = privateChatController.getStage();
                break;
            }
            case OPTIONS: {
                stage = optionsController.getStage();
                break;
            }
            default: {
                stage = new Stage();
                break;
            }
        }
        return stage;
    }

    private void setController(Page page, FXMLLoader fxmlLoader) {
        switch (page) {
            case MAIN_MENU: {
                mainMenuController = fxmlLoader.getController();
                break;
            }
            case TICTACTOE: {
                tictactoeController = fxmlLoader.getController();
                break;
            }
            case PUBLIC_CHAT: {
                publicChatController = fxmlLoader.getController();
                break;
            }
            case PRIVATE_CHAT: {
                privateChatController = fxmlLoader.getController();
                break;
            }
            case OPTIONS: {
                optionsController = fxmlLoader.getController();
                break;
            }
            case FATAL_ERROR: {
                fatalErrorController = fxmlLoader.getController();
                break;
            }
        }
    }

    public Peer[] getPeerList() {
        return peerList;
    }

    public Peer[] getPeerListToUI() {
        GetConnectedPeerListEvent getPeerListEvent = new GetConnectedPeerListEvent();
        EventHandler.getInstance().handleEvent(getPeerListEvent);
        Peer[] peerList = getPeerListEvent.getOutput();
        logger.info(peerList.length + " peers found when loading peer list to UI");
        return peerList;
    }

    private Peer[] getSortedPeerList(Peer[] peerList) {
        ArrayList<String> sortOptions = PeerSorter.SORT_OPTIONS;
        // decide based on peerSortOption in which way to sort the list
        // switch-case not used because I want to reference sortOptions values instead of constants
        // map of sort functions not used because sort functions accept different parameters
        if(peerList.length == 0) {
            return peerList;
        }
        if(peerSortOption.equals(sortOptions.get(0))) { // sort A-Z by username
            logger.info("Sorted in alphabetical order");
            return PeerSorter.sortAlphabetically(peerList);
        } else if(peerSortOption.equals(sortOptions.get(1))) { // sort Z-A by username
            return PeerSorter.sortAlphabeticallyDescending(peerList);
        } else if(peerSortOption.equals(sortOptions.get(2))) { // sort by the most recent message in private chats
            ArrayList<Pair<Peer, Date>> peerLatestMessageList = new ArrayList<>();
            for(Peer thisPeer : peerList) {
                ArrayList<Pair<Peer, Message>> privateMessageHistory = getPrivateMessageHistory(thisPeer);
                if(privateMessageHistory != null) {
                    peerLatestMessageList.add(new Pair<>(thisPeer, privateMessageHistory.get(privateMessageHistory.size() - 1).getValue().getTimestamp()));
                } else {
                    peerLatestMessageList.add(new Pair<>(thisPeer, null));
                }
            }
            return PeerSorter.sortRecentlyChattedWith(peerLatestMessageList);
        }
        return peerList;
    }

    private HashMap<String, ArrayList<Message>> getMessageHistory() {
        GetMessageHistoryEvent getMessageHistoryEvent = new GetMessageHistoryEvent();
        EventHandler.getInstance().handleEvent(getMessageHistoryEvent);
        HashMap<String, ArrayList<Message>> messageHistory = getMessageHistoryEvent.getOutput();
        logger.info(messageHistory.size() + " messages found when loading message history to UI");
        return messageHistory;
    }

    private ArrayList<Pair<Peer, Message>> getPrivateMessageHistory(Peer thisPeer) {
        GetPeerPrivateMessageHistoryEvent getMessageHistoryEvent = new GetPeerPrivateMessageHistoryEvent();
        getMessageHistoryEvent.setInput(thisPeer);
        EventHandler.getInstance().handleEvent(getMessageHistoryEvent);
        ArrayList<Pair<Peer, Message>> messageHistory = getMessageHistoryEvent.getOutput();
        if(messageHistory != null){
            logger.info(messageHistory.size() + " messages found when loading private message history to UI");
        }
        return messageHistory;
    }

    /**
     * @param page - page to be opened
     * @param sortNeeded - whether the peer list update needs any sorting refreshment
     * (typically when new mode is set in Options or when entering for the first time)
     */
    private void setControllerSettings(Page page, boolean sortNeeded) {
        switch (page) {
            case MAIN_MENU: {
                mainMenuController.setDarkMode(darkMode);
                break;
            }
            case TICTACTOE: {
                tictactoeController.setDarkMode(darkMode);
                tictactoeController.setMiddleWare();
                break;
            }
            case PUBLIC_CHAT: {
                publicChatController.setDarkMode(darkMode);
                if(sortNeeded) {
                    this.peerList = getSortedPeerList(getPeerListToUI());
                    publicChatController.setNewUserList(peerList);
                } else {
                    publicChatController.setNewUserList(peerList);
                }
                publicChatController.setLabel(peerList.length);
                this.messageHistory = getMessageHistory();
                publicChatController.setNewMessageHistory(MessageHistorySorter.getSortedMessageHistory(messageHistory));
                break;
            }
            case PRIVATE_CHAT: {
                privateChatController.setDarkMode(darkMode);
                if(sortNeeded) {
                    this.peerList = getSortedPeerList(getPeerListToUI());
                    findSelectedUser(peerList[this.selectedPeerIndex].getPeerId());
                    privateChatController.setNewUserList(peerList);
                } else {
                    privateChatController.setNewUserList(peerList);
                }

                if(this.selectedPeerIndex != -1) {
                    setCurrentPeer(this.selectedPeerIndex);
                    privateChatController.setCurrentPeer(peerList[this.selectedPeerIndex]);
                }
                break;
            }
            case OPTIONS: {
                optionsController.setDarkMode(darkMode);
                optionsController.setSortOption(peerSortOption);
                break;
            }
        }
    }

    public void setReceiveBoard(String buttonName, boolean hasFirstTurn) {
        tictactoeController.onReceiveBoard(buttonName, hasFirstTurn ? "O" : "X");
    }

    /**
     * Changes the current and previous pages when opening a new one.
     * @param page
     */
    private Page changePages(Page page) {
        if (page == null && previousPage == Page.PRIVATE_CHAT && selectedPeerIndex != -1) {
            page = Page.PRIVATE_CHAT;
        } else if(page == null && previousPage == Page.MAIN_MENU) {
            page = Page.MAIN_MENU;
        } else if(page == null) {
            page = Page.PUBLIC_CHAT;
        }

        previousPage = currentPage;
        currentPage = page;
        return page;
    }


    /**
     * Opens a new UI page in a new stage.
     *
     * @param page  the page to open
     * @param stage the JavaFX stage
     */
    public void openPage(Page page, Stage stage) {
        logger.info("Opening page. Peer list contains " + peerList.length + " peers.");
        if(stage == null) {
            stage = getLastStage();
        }
        //stage.close();
        page = changePages(page);
        boolean sortNeeded = (previousPage == Page.MAIN_MENU || previousPage == Page.OPTIONS); // sort needed whenever some changes in peer list indexation might be necessary

        FXMLLoader fxmlLoader = new FXMLLoader(MainMenuController.class.getResource(FXMLLinker.PAGE_LINKS.get(page)));
        Scene scene = null;

        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Exception at open page : {}",e.getMessage());
            throw new RuntimeException(e);
        }
        stage.setTitle(UiConfigurator.getInstance().getUiWindowName());
        setController(page, fxmlLoader);
        scene.getStylesheets().add(getClass().getResource(CSSLinker.PAGE_LINKS.get(page)).toExternalForm());
        setControllerSettings(page, sortNeeded);
        stage.setScene(scene);
        stage.show();

    }

    public void setDarkMode() {
        darkMode = !darkMode;
        System.out.println(darkMode);
        optionsController.setDarkMode(darkMode);
    }


    @Override
    public void handleEvent(Event<?, ?> event) {
        EventType type = event.getType();
        if (this.commandsRegistry.containsKey(type)) {
            ICommand command = this.commandsRegistry.get(type);
            command.execute(event);
        }
    }


    private void findSelectedUser(String selectedUserID) {
        for(int i = 0; i < peerList.length; i++) {
            if(peerList[i].getPeerId().equals(selectedUserID)) {
                selectedPeerIndex = i; // if the previously selected user found -> update index, break
                break;
            }
        }
        if(selectedPeerIndex == -1 && currentPage == Page.PRIVATE_CHAT) { // if the previously selected user disconnected -> return to public chat
            privateChatController.openPublicChat();
        } else if(selectedPeerIndex == -1 && currentPage == Page.TICTACTOE) {
            tictactoeController.openPublicChat();
        }
    }

    /**
     * Updates and refreshes the peer list in the chat UIs.
     * ALso handles removing disconnected peer from TicTacToe pending invites.
     */
    public void updatePeer() {
        logger.info("Peer list updated");
        String selectedUserID = null;
        if(this.selectedPeerIndex != -1) {
            selectedUserID = peerList[selectedPeerIndex].getPeerId();
            selectedPeerIndex = -1; // set to temporary value
        }
        this.peerList = getSortedPeerList(getPeerListToUI());
        if(selectedUserID != null) {
            findSelectedUser(selectedUserID); // find the user
        }
        if (currentPage == Page.PUBLIC_CHAT) {
            publicChatController.setNewUserList(peerList);
            publicChatController.setLabel(peerList.length);
        }
        if (currentPage == Page.PRIVATE_CHAT) {
            privateChatController.setNewUserList(peerList);
            if(selectedPeerIndex != -1) {
                privateChatController.setCurrentPeer(peerList[selectedPeerIndex]);
            }
        }
        if(selectedPeerIndex == -1) {
            gameHandler.setTTTPeer(null);
        }
    }

    /**
     * Sets the current peer for private chat.
     *
     * @param selectedIndex the index of the selected peer
     */
    public void setCurrentPeer(int selectedIndex) {
        this.selectedPeerIndex = selectedIndex;
        if(currentPage == Page.PRIVATE_CHAT) {
            privateChatController.setCurrentPeer(peerList[this.selectedPeerIndex]);
            ArrayList<Pair<Peer, Message>> privateMessageHistory = getPrivateMessageHistory(peerList[this.selectedPeerIndex]);
            privateChatController.setNewUserMessageHistory(privateMessageHistory);
        }
    }

    /**
     * Adds a new public message to the UI.
     *
     */
    public void addMessage(Message message) {
        logger.info("New message added to public chat UI");
        if (currentPage == Page.PUBLIC_CHAT) {
            this.messageHistory = getMessageHistory();
            logger.info("Message history has " + MessageHistorySorter.getSortedMessageHistory(messageHistory).size() + " elements.");
            publicChatController.setNewMessageHistory(MessageHistorySorter.getSortedMessageHistory(messageHistory));
        }
    }

    /**
     * Adds a new private message to the UI.
     *
     * @param thisPeer         the peer who sent the message
     * @param peerMessagePair  the peer and message pair
     */
    public void addPrivateMessage(Peer thisPeer, Pair<Peer, Message> peerMessagePair) {
        logger.info("New message added to private chat UI");
        if (currentPage == Page.PRIVATE_CHAT && thisPeer.getPeerId().equals(peerList[selectedPeerIndex].getPeerId())) {
            privateChatController.addMessage(peerMessagePair.getValue(), peerMessagePair.getKey());
        }
        if(peerSortOption.equals(PeerSorter.SORT_OPTIONS.get(2)) && (currentPage == Page.PRIVATE_CHAT || currentPage == Page.PUBLIC_CHAT)) { // if peers are sorted by most recent messages -> move this peer to the bottom
            peerList = PeerSorter.movePeer(peerList, thisPeer);
            findSelectedUser(thisPeer.getPeerId());
            if(currentPage == Page.PRIVATE_CHAT && peerList != null) {
                privateChatController.setNewUserList(peerList);
                privateChatController.setCurrentPeer(peerList[selectedPeerIndex]);
            } else if(currentPage == Page.PUBLIC_CHAT && peerList != null) {
                publicChatController.setNewUserList(peerList);
            }

        }
    }

    public void setGameStatus(TictactoeGameStatus gameStatus) {
        tictactoeController.setBoardStatusLabel(TictactoeStatusReports.STATUS_REPORTS.get(gameStatus));
    }

    /**
     * Displays a fatal error screen with the given exception message.
     *
     * @param errorException the exception to display
     */
    public void showFatalError(Exception errorException) {
        String errorMessage = errorException.getMessage();
        openPage(Page.FATAL_ERROR, new Stage());
        fatalErrorController.setErrorLabel("Error caught: " + errorMessage);
    }

    /**
     * Processes a request to refresh public message history. (when synchronizing history with the other users)
     */
    public void refreshPublicMessageHistory() {
        messageHistory = getMessageHistory();
        logger.info("Refreshed public message history in UI");
        if(currentPage == Page.PUBLIC_CHAT) {
            publicChatController.setNewMessageHistory(MessageHistorySorter.getSortedMessageHistory(messageHistory));
        }
    }

    public void setGamePeer(String userName) {
        tictactoeController.setGameWithLabel(userName);
    }
}