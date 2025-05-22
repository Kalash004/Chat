package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Scanner;

public class ConfigLoader {
    public static ApiConfig loadAPIConfig() throws IOException {
        String fileName = "api_config.json";
        Scanner scanner = new Scanner(Paths.get(fileName), StandardCharsets.UTF_8.name());
        String content = scanner.useDelimiter("\\A").next();
        scanner.close();
        ApiConfig apiConfig = JsonUtils.fromJson(content, ApiConfig.class);
        return apiConfig;
    }

    public static void saveAPIConfig(ApiConfig apiConfig) throws IOException {
        String fileName = "api_config.json";
        String str = JsonUtils.toJson(apiConfig);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);
        writer.close();
        return;
    }
}
