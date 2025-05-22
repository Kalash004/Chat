package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs;

public class ApiConfig {
    private static final int messagingPort = 8899;
    private static String peerId = "0";
    private static String userName = "TestAnton";
    private static final int defualtPacketSize = 1024;
    private static final int discoveryTimeoutMillis = 500;
    private static final int messagingTimeoutMillis = 4800;
    private static final int discoverySocketSleep = 1000;
    private static final String discoveryBroadcastIpAddress = "255.255.255.255";
    private static final int discoveryClientSourcePort = 5300;
    private static final int discoveryServerSourcePort = 5100;
    private static final int discoveryServerTargetPort = 5100;
    private static final int discoveryKillTimeMS = 500;



    public static int getDiscoveryServerTargetPort() {
        return discoveryServerTargetPort;
    }

    public static String getDiscoveryBroadcastIpAddress() {
        return discoveryBroadcastIpAddress;
    }

    public static int getMessagingPort() {
        return messagingPort;
    }

    public static String getPeerId() {
        return peerId;
    }

    public static void setPeerId(String peerId) {
        ApiConfig.peerId = peerId;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        ApiConfig.userName = userName;
    }

    public static int getDefualtPacketSize() {
        return defualtPacketSize;
    }

    public static int getDiscoveryTimeoutMillis() {
        return discoveryTimeoutMillis;
    }

    public static int getDiscoverySocketSleep() {
        return discoverySocketSleep;
    }

    public static int getDiscoveryClientSourcePort() {
        return discoveryClientSourcePort;
    }

    public static int getDiscoveryServerSourcePort() {
        return discoveryServerSourcePort;
    }

    public static int getMessagingTimeoutMillis() {
        return messagingTimeoutMillis;
    }

    public static int getDiscoveryKillTimeMs() {
        return discoveryKillTimeMS;
    }
}