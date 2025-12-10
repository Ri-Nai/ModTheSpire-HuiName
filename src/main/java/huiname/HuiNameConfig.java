package huiname;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HuiNameConfig {
    private static final String CONFIG_FILE_NAME = "HuiNameConfig.json";
    public static ConfigData data;

    public static class ReplacementRule {
        public String target;
        public List<String> sources;

        public ReplacementRule(String target, List<String> sources) {
            this.target = target;
            this.sources = sources;
        }
    }

    public static class ConfigData {
        public List<ReplacementRule> rules = new ArrayList<>();
    }

    public static void load() {
        File file = new File(CONFIG_FILE_NAME);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (!file.exists()) {
            createDefaultConfig(file, gson);
        } else {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                data = gson.fromJson(reader, ConfigData.class);
                if (data == null) {
                    createDefaultConfig(file, gson);
                }
            } catch (IOException e) {
                e.printStackTrace();
                createDefaultConfig(file, gson);
            }
        }
    }

    private static void createDefaultConfig(File file, Gson gson) {
        try (InputStream is = HuiNameConfig.class.getResourceAsStream("/default_config.json")) {
            if (is != null) {
                try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    data = gson.fromJson(reader, ConfigData.class);
                }
            } else {
                data = new ConfigData(); // Fallback if resource not found
            }
        } catch (IOException e) {
            e.printStackTrace();
            data = new ConfigData();
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
