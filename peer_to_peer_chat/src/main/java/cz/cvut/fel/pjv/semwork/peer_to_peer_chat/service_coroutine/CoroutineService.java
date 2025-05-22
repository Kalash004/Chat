package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_coroutine;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.coroutine.CoroutineType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICorutine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.IListen;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_coroutine.commands.CommandStartWatchdog;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * CoroutineService manages the execution of coroutines in a separate thread.
 * <p>
 * It provides functionality to add, resume, and stop coroutines. It also
 * listens to specific system events (e.g., PROGRAM_START, PROGRAM_END) and
 * executes associated commands.
 * <p>
 * This service uses a thread-safe queue to manage coroutine execution.
 */
public class CoroutineService implements Runnable, IListen {
    private static final Logger logger = LoggerFactory.getLogger(CoroutineService.class);

    private boolean runFlag = true;
    private static CoroutineService instance;
    private static final EventHandler eventHandler = EventHandler.getInstance();
    private Thread thread;
    private final ConcurrentLinkedQueue<ICorutine> coroutineQueue;

    /**
     * Map of event types to commands that should be executed when those events are triggered.
     */
    private final HashMap<EventType, ICommand> noInputCommands = new HashMap<EventType, ICommand>(Map.of(
            EventType.PROGRAM_START, new CommandStartWatchdog(this),
            EventType.PROGRAM_END, new ICommand() {
                @Override
                public void execute(Event<?, ?> input) {
                    logger.info("Program Stopping");
                    stop();
                }
            }
    ));

    /**
     * Returns the singleton instance of CoroutineService.
     *
     * @return the instance of CoroutineService
     */
    public static CoroutineService getInstance() {
        if (instance == null) {
            instance = new CoroutineService();
            instance.thread = new Thread(instance, "CoroutineService");
        }
        return instance;
    }

    /**
     * Private constructor to enforce singleton pattern.
     */
    private CoroutineService() {
        coroutineQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * Adds a coroutine to the queue to be executed.
     *
     * @param coroutine the coroutine to be added
     * @throws IllegalStateException if the service is not running
     */
    public synchronized void addCoroutine(ICorutine coroutine) {
        if (!runFlag) {
            throw new IllegalStateException("Coroutine has not been started");
        }
        logger.info("Adding coroutine: {}", coroutine);
        coroutineQueue.add(coroutine);
        logger.info("Added coroutine: {}", this.coroutineQueue.size());
    }

    /**
     * Continuously polls and resumes coroutines in the queue.
     * Stops when {@code runFlag} is set to false.
     */
    @Override
    public void run() {
        logger.info("Coroutine service started");
        while (runFlag) {
            try {
                Thread.sleep(50); // A small delay to reduce CPU usage
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            while (!(coroutineQueue.isEmpty())) {
                ICorutine coroutine = this.coroutineQueue.poll();
                if (coroutine == null) {
                    continue;
                }
                boolean stillRunning = coroutine.resume();
                if (stillRunning && !runFlag && coroutine.getCoroutineType().equals(CoroutineType.INFINITE))
                    stillRunning = false; // Stop if service is shutting down

                if (stillRunning) {
                    this.coroutineQueue.add(coroutine);
                }
            }
        }
        logger.info("Coroutine service stopped {}", this.coroutineQueue.size());
    }

    /**
     * Handles system events by executing the corresponding no-input commands.
     *
     * @param event the event to handle
     */
    @Override
    public void handleEvent(Event<?, ?> event) {
        if (!this.noInputCommands.containsKey(event.getType())) {
            // TODO: Log error
            return;
        }
        ICommand command = this.noInputCommands.get(event.getType());
        command.execute(null);
    }

    /**
     * Starts the coroutine service by launching the thread.
     */
    public void start() {
        runFlag = true;
        if (thread == null) {
            thread = new Thread(instance, "CoroutineService");
        }
        thread.start();
    }

    /**
     * Stops the coroutine service by halting the thread and setting the run flag to false.
     */
    public void stop() {
        if (thread == null) return;
        logger.info("Stopping CoroutineService");
        runFlag = false;
        try {
            thread.join();
            logger.info("CoroutineService stopped");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        thread = null;
    }
}
