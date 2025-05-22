package cz.cvut.fel.pjv.semwork.peer_to_peer_chat._main;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.ApiController;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.PeerManager;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_message_history.MessageServiceController;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.tictactoe_service.TictactoeGameHandler;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.UiController;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_coroutine.CoroutineService;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;

/**
 * Entry point of the peer-to-peer chat application.
 * <p>
 * Initializes core services and registers them with the event handler.
 * Finally, launches the user interface.
 */
public class Application {

    /**
     * The main method that starts the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        EventHandler eventHandler = EventHandler.getInstance();
        UiController uiController = UiController.getInstance();

        ApiController apiController = ApiController.getInstance();
        PeerManager peerManager = PeerManager.getInstance();
        MessageServiceController messageServiceController = MessageServiceController.getInstance();
        CoroutineService coroutine = CoroutineService.getInstance();

        eventHandler.addListener(EventReceiver.API, apiController);
        eventHandler.addListener(EventReceiver.PEER_MANAGER, peerManager);
        eventHandler.addListener(EventReceiver.MIDDLE_WARE, uiController);
        eventHandler.addListener(EventReceiver.COROUTINE, coroutine);
        eventHandler.addListener(EventReceiver.MESSAGE_SERVICE, messageServiceController);
        eventHandler.addListener(EventReceiver.UI, uiController);
        uiController.launchUi();
    }
}
