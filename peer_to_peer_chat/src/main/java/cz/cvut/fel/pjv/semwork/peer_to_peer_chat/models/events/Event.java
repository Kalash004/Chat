package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventSender;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;

import java.util.ArrayList;

/**
 * Abstract class representing an event in the system.
 * An event always contains its event type and receivers, and might contain its sender, the input and output data.
 *
 * @param <I> the type of input data for the event
 * @param <O> the type of output data for the event
 */
public abstract class Event<I, O> {
    protected EventType type;
    protected ArrayList<EventReceiver> receivers;
    protected EventSender sender;
    private O output;
    private I input;

    public O getOutput() {
        return output;
    }

    public void setOutput(O output) {
        this.output = output;
    }

    public I getInput() {
        return input;
    }

    public Event<I, O> setInput(I input) {
        this.input = input;
        return this;
    }

    public EventReceiver[] getReceivers() {
        return receivers.toArray(new EventReceiver[0]);
    }

    public EventSender getSender() {
        return sender;
    }

    public EventType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                ", receivers=" + receivers +
                ", sender=" + sender +
                ", output=" + output +
                ", input=" + input +
                '}';
    }
}
