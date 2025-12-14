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
        HuiNameConfigUI.initialize(settingsPanel);

        // 注册 Mod Badge
        // 使用游戏自带的图标作为 Badge 图标
        Texture badgeTexture = new Texture("images/ui/tick.png"); 
        BaseMod.registerModBadge(badgeTexture, "以辉之名", "Author", "将特定的字替换为辉", settingsPanel);
    }
}
