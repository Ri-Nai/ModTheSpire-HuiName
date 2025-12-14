package huiname;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HuiNameConfig {
    private static final String MOD_NAME = "HuiName";
    private static final String CONFIG_NAME = "HuiNameConfig";
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
        try {
            Properties defaults = new Properties();
            SpireConfig config = new SpireConfig(MOD_NAME, CONFIG_NAME, defaults);
            config.load();
            
            String json = config.getString("data");
            Gson gson = new Gson();
            
            if (json != null && !json.isEmpty()) {
                data = gson.fromJson(json, ConfigData.class);
            }
            
            if (data == null) {
                loadDefaultFromResource();
                save();
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadDefaultFromResource();
        }
    }

    public static void resetToDefault() {
        loadDefaultFromResource();
        save();
    }

    private static void loadDefaultFromResource() {
        Gson gson = new Gson();
        try (InputStream is = HuiNameConfig.class.getResourceAsStream("/default_config.json")) {
            if (is != null) {
                try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    data = gson.fromJson(reader, ConfigData.class);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (data == null) {
            data = new ConfigData();
        }
    }

    public static void save() {
        try {
            SpireConfig config = new SpireConfig(MOD_NAME, CONFIG_NAME);
            Gson gson = new Gson();
            String json = gson.toJson(data);
            config.setString("data", json);
            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
