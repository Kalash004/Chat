import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.peer.SynchronizationStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HashTest {

    @Test
    public void test() {
        HashMap<String, Peer> peers = new HashMap<>(
            Map.ofEntries(
                    Map.entry("A", new Peer())
            )
        );
        Thread t = new Thread(() -> {
            Peer p = peers.get("A");
            System.out.println(p);
            p.setStatus(SynchronizationStatus.SYNCHRONIZED);
            System.out.println(p);
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Peer pp = peers.get("A");
        System.out.println(pp);
    }
}
