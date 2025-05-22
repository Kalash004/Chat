package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes.PortHolderContent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;

public class MessageUtils {

    /**
     *
     * @param messageObj
     * @return
     * @deprecated use {@link #getPortFromJson(Message)}
     */
    public static int getPortFromContent(Message messageObj) {
        String content = messageObj.getContent();
        String[] splitted = content.split(",");
        for (String s : splitted) {
            String[] key_val = s.split(":");
            if (key_val[0].equals("message_port")) {
                return Integer.parseInt(key_val[1]);
            }
        }
        throw new RuntimeException("Could not find port in content");
    }

    public static int getPortFromJson(Message messageObj) throws JsonProcessingException {
        PortHolderContent portHolder = JsonUtils.fromJson(messageObj.getContent(), PortHolderContent.class);
        return portHolder.port;
    }
}
