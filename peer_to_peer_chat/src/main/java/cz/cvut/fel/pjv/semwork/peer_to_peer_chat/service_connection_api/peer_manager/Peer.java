package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.peer.SynchronizationStatus;

import java.net.InetAddress;
import java.util.Date;
import java.util.Objects;

public class Peer {

    private String peerId;
    private String userName;
    private InetAddress ipAddress;
    private int messagePort;
    private Date lastCheckTime;

    private SynchronizationStatus status;

    public Peer(String peerId, String peerName, InetAddress ipAddress, int messagePort) {
        this.peerId = peerId;
        this.userName = peerName;
        this.ipAddress = ipAddress;
        this.messagePort = messagePort;
        this.lastCheckTime = new Date(); // sets current datetime
        this.status = SynchronizationStatus.NONE;
    }

    public Peer() {}

    public String getPeerId() {
        return peerId;
    }

    public String getUserName() {
        return userName;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public int getMessagePort() {
        return messagePort;
    }

    public Date getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(Date lastCheck) {
        this.lastCheckTime = lastCheck;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "peerId='" + peerId + '\'' +
                ", userName='" + userName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", messagePort=" + messagePort + '\'' +
                ", synchronizationStatus=" + status +
                '}';
    }

    public SynchronizationStatus getStatus() {
        return status;
    }

    public void setStatus(SynchronizationStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return messagePort == peer.messagePort && Objects.equals(peerId, peer.peerId) && Objects.equals(userName, peer.userName) && Objects.equals(ipAddress, peer.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(peerId, userName, ipAddress, messagePort, lastCheckTime, status);
    }
}
