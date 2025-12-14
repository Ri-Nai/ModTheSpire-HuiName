package huiname;

import basemod.ModButton;
import basemod.ModLabel;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import huiname.patches.TextReplacePatches;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class HuiNameConfigUI {
    private static ModPanel panel;
    private static final int RULES_PER_PAGE = 8;
    
    // Input fields
    private static ModTextField targetInput;
    private static ModTextField sourceInput;

    public static void setInputText(String target, String source) {
        if (targetInput != null) targetInput.setText(target);
        if (sourceInput != null) sourceInput.setText(source);
    }

    public static void initialize(ModPanel settingsPanel) {
        panel = settingsPanel;
        refreshUI();
    }

    private static void refreshUI() {
        // Clear existing elements if possible (BaseMod doesn't support this easily, so we build once)
        // Assuming initialize is called once.
        
        float startY = 750.0f;
        float x = 350.0f;
        float lineSpacing = 55.0f; // Consistent vertical spacing
        float labelOffsetY = 22.0f; // Align text vertically with input boxes/buttons
        float inputXOffset = 280.0f; // Space for labels
        
        // Title
        panel.addUIElement(new ModLabel("以辉之名 - 配置", x, startY, panel, (me) -> {}));
        startY -= lineSpacing;

        // Buttons
        panel.addUIElement(new ModLabel("打开配置文件", x, startY + 8.0f, panel, (me) -> {}));
        panel.addUIElement(new ModButton(x + 250.0f, startY - 45.0f, panel, (me) -> {
            try {
                File file = new File("preferences" + File.separator + "HuiNameConfig.properties");
                if (!file.exists()) {
                    HuiNameConfig.save();
                }
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        
        panel.addUIElement(new ModLabel("重置默认", x + 350.0f, startY + 8.0f, panel, (me) -> {}));
        panel.addUIElement(new ModButton(x + 500.0f, startY - 45.0f, panel, (me) -> {
            HuiNameConfig.resetToDefault();
        }));
        
        startY -= lineSpacing * 1.5f;

        // Add Rule Section
        panel.addUIElement(new ModLabel("添加新规则:", x, startY, panel, (me) -> {}));
        startY -= lineSpacing;
        
        panel.addUIElement(new ModLabel("目标词 (如: 辉):", x, startY + labelOffsetY, panel, (me) -> {}));
        targetInput = new ModTextField("输入目标词...", x + inputXOffset, startY, 200.0f, 40.0f);
        panel.addUIElement(targetInput);
        
        startY -= lineSpacing;
        panel.addUIElement(new ModLabel("源词 (逗号分隔):", x, startY + labelOffsetY, panel, (me) -> {}));
        sourceInput = new ModTextField("输入源词...", x + inputXOffset, startY, 400.0f, 40.0f);
        panel.addUIElement(sourceInput);
        
        startY -= lineSpacing;
        panel.addUIElement(new ModLabel("添加 (需重启生效)", x, startY + 8.0f, panel, (me) -> {}));
        panel.addUIElement(new ModButton(x + 250.0f, startY - 45.0f, panel, (me) -> {
            String target = targetInput.getText();
            String sourceStr = sourceInput.getText();
            
            if (target != null && !target.trim().isEmpty() && sourceStr != null && !sourceStr.trim().isEmpty()) {
                String[] sources = sourceStr.split("[,，]");
                java.util.List<String> sourceList = new ArrayList<>();
                for (String s : sources) {
                    if (!s.trim().isEmpty()) {
                        sourceList.add(s.trim());
                    }
                }
                
                if (!sourceList.isEmpty()) {
                    HuiNameConfig.data.rules.add(new HuiNameConfig.ReplacementRule(target, sourceList));
                    HuiNameConfig.save();
                    
                    // Clear inputs
                    targetInput.setText("");
                    sourceInput.setText("");
                }
            }
        }));

        startY -= lineSpacing;
        panel.addUIElement(new ModLabel("当前规则列表:", x, startY, panel, (me) -> {}));
        startY -= 30.0f;

        panel.addUIElement(new RuleListUI(x + 20.0f, startY));
    }
}
