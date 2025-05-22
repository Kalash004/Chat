import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes.PeerAndMessageTextHolder;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;

public class JsonUtilsTest {

    @Test
    public void jsonToObject() throws JsonProcessingException {
        //Assemble
        String json = "{\"peerId\":\"hello\", \"text\":\"world\"}";
        // Act
        PeerAndMessageTextHolder p = JsonUtils.fromJson(json, PeerAndMessageTextHolder.class);
        // Assert
        Assertions.assertEquals("world", p.messageTextHolder.text);
        Assertions.assertEquals("hello", p.peerIdHolder.peerId);
        System.out.println("Passed " + this.getClass().getName());
    }

    @Test
    public void objectToJson() {
        //Assemble
        PeerAndMessageTextHolder peerAndMessageTextHolder = new PeerAndMessageTextHolder("hello", "world");
        // Act
        String json = peerAndMessageTextHolder.toJson();
        // Assert
        Assertions.assertEquals("{\"peerId\":\"hello\",\"text\":\"world\"}", json);
        System.out.println("Passed " + this.getClass().getName());
    }

}
