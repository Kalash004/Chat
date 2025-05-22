package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.ui_link_utils;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.ui_link_utils.Page;

import java.util.Map;
public class CSSLinker {
    public static final Map<Page, String> PAGE_LINKS = Map.of(
            Page.OPTIONS, "/pjv/semestral_work/kalasnikov_kolomiiets/peer_to_peer_chat/styles/options.css",
            Page.MAIN_MENU, "/pjv/semestral_work/kalasnikov_kolomiiets/peer_to_peer_chat/styles/main-menu.css",
            Page.PRIVATE_CHAT, "/pjv/semestral_work/kalasnikov_kolomiiets/peer_to_peer_chat/styles/chat.css",
            Page.PUBLIC_CHAT, "/pjv/semestral_work/kalasnikov_kolomiiets/peer_to_peer_chat/styles/chat.css",
            Page.TICTACTOE, "/pjv/semestral_work/kalasnikov_kolomiiets/peer_to_peer_chat/styles/tictactoe.css",
            Page.FATAL_ERROR, "/pjv/semestral_work/kalasnikov_kolomiiets/peer_to_peer_chat/styles/fatal-error.css"
    );
}