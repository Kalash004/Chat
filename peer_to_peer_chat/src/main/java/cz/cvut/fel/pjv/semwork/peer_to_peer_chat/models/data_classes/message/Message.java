package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.connection.MessageType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.message_status.MessageStatus;

import java.util.Date;
import java.util.Objects;

public class Message {
    private String messageId;
    private MessageStatus status;
    private MessageType type;
    private Date timestamp;
    private String peerId;
    private String senderName;
    private String content;

    public Message() {}

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", peerId='" + peerId + '\'' +
                ", senderName='" + senderName + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(messageId, message.messageId) && status == message.status && type == message.type && Objects.equals(timestamp, message.timestamp) && Objects.equals(peerId, message.peerId) && Objects.equals(senderName, message.senderName) && Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, status, type, timestamp, peerId, senderName, content);
    }

    public void copyTo(Message target) {
        target.messageId = this.messageId;
        target.status = this.status;
        target.type = this.type;
        target.timestamp = this.timestamp != null ? (Date) this.timestamp.clone() : null;
        target.peerId = this.peerId;
        target.senderName = this.senderName;
        target.content = this.content;
    }
}
