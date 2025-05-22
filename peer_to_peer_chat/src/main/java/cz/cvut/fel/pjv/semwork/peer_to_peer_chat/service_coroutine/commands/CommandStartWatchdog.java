package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_coroutine.commands;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_coroutine.CoroutineService;

public class CommandStartWatchdog implements ICommand {
    private final CoroutineService watchdog;
    public CommandStartWatchdog(CoroutineService watchdog) {
        this.watchdog = watchdog;
    }

    @Override
    public void execute(Event<?, ?> event) {
        watchdog.start();
    }
}
