package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs.UiConfig;

/**
 * Class responsible for providing UI configuration details from UiConfig.
 */
public class UiConfigurator {
    private static final UiConfigurator instance = new UiConfigurator();

    private UiConfigurator() {}

    public static UiConfigurator getInstance() {
        return instance;
    }

    public String getUiWindowName() {
        return UiConfig.getTitle();
    }
}
