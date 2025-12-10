package huiname;

import basemod.BaseMod;
import basemod.ModButton;
import basemod.ModLabel;
import basemod.ModPanel;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;

@SpireInitializer
public class HuiName implements PostInitializeSubscriber {
    public HuiName() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        HuiNameConfig.load();
        new HuiName();
    }

    @Override
    public void receivePostInitialize() {
        // 创建 Mod 配置菜单
        ModPanel settingsPanel = new ModPanel();
        
        ModLabel label = new ModLabel("配置已加载。请修改游戏根目录下的 HuiNameConfig.json 文件来自定义替换规则。", 
                350.0f, 700.0f, settingsPanel, (me) -> {});
        settingsPanel.addUIElement(label);
        
        ModLabel label2 = new ModLabel("修改后点击下方按钮重载配置，或重启游戏。", 
                350.0f, 650.0f, settingsPanel, (me) -> {});
        settingsPanel.addUIElement(label2);

        ModButton reloadBtn = new ModButton(350.0f, 600.0f, settingsPanel, (me) -> {
            HuiNameConfig.load();
            System.out.println("HuiName: Config reloaded.");
        });
        settingsPanel.addUIElement(reloadBtn);
        
        ModLabel reloadLabel = new ModLabel("重载配置", 450.0f, 615.0f, settingsPanel, (me) -> {});
        settingsPanel.addUIElement(reloadLabel);

        // 注册 Mod Badge
        // 使用游戏自带的图标作为 Badge 图标
        Texture badgeTexture = new Texture("images/ui/tick.png"); 
        BaseMod.registerModBadge(badgeTexture, "以辉之名", "Author", "将特定的字替换为辉", settingsPanel);
    }
}
