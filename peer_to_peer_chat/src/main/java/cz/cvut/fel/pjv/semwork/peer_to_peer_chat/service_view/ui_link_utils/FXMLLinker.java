package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.ui_link_utils;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.ui_link_utils.Page;

import java.util.Map;
public class FXMLLinker {
    public static final Map<Page, String> PAGE_LINKS = Map.of(
            Page.OPTIONS, "/pjv/semestral_work/kalasnikov_kolomiiets/peer_to_peer_chat/options.fxml",
            Page.MAIN_MENU, "/pjv/semestral_work/kalasnikov_kolomiiets/peer_to_peer_chat/main-menu.fxml",
            Page.PRIVATE_CHAT, "/pjv/semestral_work/kalasnikov_kolomiiets/peer_to_peer_chat/private-chat.fxml",
            Page.PUBLIC_CHAT, "/pjv/semestral_work/kalasnikov_kolomiiets/peer_to_peer_chat/public-chat.fxml",
            Page.TICTACTOE, "/pjv/semestral_work/kalasnikov_kolomiiets/peer_to_peer_chat/tictactoe.fxml",
            Page.FATAL_ERROR, "/pjv/semestral_work/kalasnikov_kolomiiets/peer_to_peer_chat/fatal-error.fxml"
    );
}
