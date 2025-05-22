package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.ShowFatalErrorEvent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.IListen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.ApiController;

import java.util.HashMap;

public class EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    private boolean runFlag = true;
    private static EventHandler instance;
    private HashMap<EventReceiver, IListen> listeners;

    private EventHandler() {
    }

    private Thread eventHandlerThread;

    public static synchronized EventHandler getInstance() {
        if (instance == null) {
            instance = new EventHandler();
            instance.listeners = new HashMap<EventReceiver, IListen>();
        }
        return instance;
    }

    public void handleEvent(Event<?, ?> event) {
        try {
            processEvent(event);
        } catch (Exception e) {
            logger.error("Caught exception {}", e.getMessage());
            ShowFatalErrorEvent exceptionEvent = new ShowFatalErrorEvent();
            exceptionEvent.setInput(e);
//            processEvent(exceptionEvent);
            throw e;
        }
    }

    private void processEvent(Event<?, ?> event) {
        if (event == null) {
            // TODO: handle exceptions
            // TODO: logger add log of exception
            return;
        }
        EventReceiver[] tempReceivers = event.getReceivers();
        logger.debug("Processing event: " + event);
        for (EventReceiver receiver : tempReceivers) {
            if (!listeners.containsKey(receiver)) {
                // TODO: exceptions
                // TODO: logger add log of exception
                continue;
            }
            // TODO: possible middle ware preprocessing add here
            IListen listener = listeners.get(receiver);
            listener.handleEvent(event);
        }
    }

    public void addListener(EventReceiver receiver, IListen listener) {
        this.listeners.put(receiver, listener);
    }

    public void setRunFlag(boolean runFlag) {
        this.runFlag = runFlag;
    }
}
