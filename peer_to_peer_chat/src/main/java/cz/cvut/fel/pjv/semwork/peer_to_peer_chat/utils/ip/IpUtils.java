package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.ip;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IpUtils {
    @Deprecated
    /**
     * Deprecated dont use
     * @Deprecated
     * @return
     */
    public static String getLocalIp() {
        String localIp = null;
        Enumeration<NetworkInterface> networkInterfaceEnumerator = null;
        try {
            networkInterfaceEnumerator = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaceEnumerator.hasMoreElements() && localIp == null) {
                NetworkInterface networkInterface = networkInterfaceEnumerator.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addressesInInterfaceEnumerator = networkInterface.getInetAddresses();
                while (addressesInInterfaceEnumerator.hasMoreElements()) {
                    InetAddress address = addressesInInterfaceEnumerator.nextElement();
                    String ip = address.getHostAddress();
                    if (! (address instanceof Inet4Address)) continue;
                    String part = ip.substring(0,ip.indexOf("."));
                    if ((address.isSiteLocalAddress() || part.equals("147"))) { // to be accessible in eduroam network
                        localIp = ip;
                        break;
                    }
                }
            }
        } catch (SocketException ex) {
            throw new RuntimeException(ex);
        }

        if (localIp == null) {
            throw new RuntimeException("Could not get local IP");
        }
        return localIp;
    }
}
