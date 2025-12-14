package huiname;

import basemod.IUIElement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.List;

public class RuleListUI implements IUIElement {
    private float x;
    private float y;
    private int page = 0;
    private static final int ITEMS_PER_PAGE = 8;
    private float rowHeight;

    public RuleListUI(float x, float y) {
        this.x = x * Settings.scale;
        this.y = y * Settings.scale;
        this.rowHeight = 40.0f * Settings.scale;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (HuiNameConfig.data == null || HuiNameConfig.data.rules == null) return;
        
        List<HuiNameConfig.ReplacementRule> rules = HuiNameConfig.data.rules;
        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, rules.size());
        
        float currentY = y;
        
        for (int i = start; i < end; i++) {
            HuiNameConfig.ReplacementRule rule = rules.get(i);
            String sourceSummary = rule.sources.toString();
            if (sourceSummary.length() > 30) sourceSummary = sourceSummary.substring(0, 30) + "...";
            String text = rule.target + " <- " + sourceSummary;
            
            FontHelper.renderFontLeft(sb, FontHelper.tipBodyFont, text, x, currentY, Color.WHITE);
            
            // Draw Edit Button
            float editX = x + 600.0f * Settings.scale;
            FontHelper.renderFontLeft(sb, FontHelper.tipBodyFont, "[编辑]", editX, currentY, Color.CYAN);

            // Draw Delete Button
            float deleteX = x + 700.0f * Settings.scale;
            FontHelper.renderFontLeft(sb, FontHelper.tipBodyFont, "[删除]", deleteX, currentY, Color.RED);
            
            currentY -= rowHeight;
        }
        
        // Pagination
        float navY = y - (ITEMS_PER_PAGE * rowHeight) - 20.0f * Settings.scale;
        if (page > 0) {
            FontHelper.renderFontLeft(sb, FontHelper.tipBodyFont, "< 上一页", x, navY, Color.YELLOW);
        }
        if (end < rules.size()) {
            FontHelper.renderFontLeft(sb, FontHelper.tipBodyFont, "下一页 >", x + 200.0f * Settings.scale, navY, Color.YELLOW);
        }
        
        FontHelper.renderFontLeft(sb, FontHelper.tipBodyFont, "页码: " + (page + 1) + " / " + ((rules.size() - 1) / ITEMS_PER_PAGE + 1), x + 100.0f * Settings.scale, navY, Color.WHITE);
    }

    @Override
    public void update() {
        if (HuiNameConfig.data == null || HuiNameConfig.data.rules == null) return;
        
        List<HuiNameConfig.ReplacementRule> rules = HuiNameConfig.data.rules;
        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, rules.size());
        
        float currentY = y;
        
        // Check clicks
        if (InputHelper.justClickedLeft) {
            float mx = InputHelper.mX;
            float my = InputHelper.mY;
            
            // Check buttons
            for (int i = start; i < end; i++) {
                // Edit
                float editX = x + 400.0f * Settings.scale;
                if (mx >= editX && mx <= editX + 60.0f * Settings.scale && my >= currentY - 20.0f * Settings.scale && my <= currentY + 10.0f * Settings.scale) {
                    HuiNameConfig.ReplacementRule rule = rules.get(i);
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < rule.sources.size(); j++) {
                        sb.append(rule.sources.get(j));
                        if (j < rule.sources.size() - 1) sb.append(",");
                    }
                    HuiNameConfigUI.setInputText(rule.target, sb.toString());
                    
                    rules.remove(i);
                    HuiNameConfig.save();
                    InputHelper.justClickedLeft = false;
                    return;
                }

                // Delete
                float deleteX = x + 500.0f * Settings.scale;
                // Approximate hitbox for "[删除]"
                if (mx >= deleteX && mx <= deleteX + 60.0f * Settings.scale && my >= currentY - 20.0f * Settings.scale && my <= currentY + 10.0f * Settings.scale) {
                    rules.remove(i);
                    HuiNameConfig.save();
                    InputHelper.justClickedLeft = false; // Consume click
                    
                    // Adjust page if empty
                    if (page > 0 && (page * ITEMS_PER_PAGE) >= rules.size()) {
                        page--;
                    }
                    return;
                }
                currentY -= rowHeight;
            }
            
            // Check pagination
            float navY = y - (ITEMS_PER_PAGE * rowHeight) - 20.0f * Settings.scale;
            // Prev
            if (page > 0) {
                if (mx >= x && mx <= x + 80.0f * Settings.scale && my >= navY - 20.0f * Settings.scale && my <= navY + 10.0f * Settings.scale) {
                    page--;
                    InputHelper.justClickedLeft = false;
                    return;
                }
            }
            // Next
            if (end < rules.size()) {
                float nextX = x + 200.0f * Settings.scale;
                if (mx >= nextX && mx <= nextX + 80.0f * Settings.scale && my >= navY - 20.0f * Settings.scale && my <= navY + 10.0f * Settings.scale) {
                    page++;
                    InputHelper.justClickedLeft = false;
                    return;
                }
            }
        }
    }

    @Override
    public int renderLayer() {
        return 1;
    }

    @Override
    public int updateOrder() {
        return 1;
    }
}
