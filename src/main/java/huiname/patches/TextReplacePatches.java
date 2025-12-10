package huiname.patches;

import huiname.HuiNameConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

import java.lang.reflect.Field;
import java.util.Map;

public class TextReplacePatches {

    // 文本替换方法
    static String replaceText(String text) {
        if (text == null) {
            return null;
        }
        
        if (HuiNameConfig.data != null && HuiNameConfig.data.rules != null) {
            for (HuiNameConfig.ReplacementRule rule : HuiNameConfig.data.rules) {
                if (rule.target == null || rule.sources == null) continue;
                for (String source : rule.sources) {
                    text = text.replace(source, rule.target);
                }
            }
        }
        
        return text;
    }

    private static void replaceFields(Object obj) {
        if (obj == null) return;
        // 简单过滤，只处理 Strings 结尾的类，避免处理 String 本身或其他对象
        if (!obj.getClass().getSimpleName().endsWith("Strings") && !obj.getClass().getSimpleName().equals("Keyword")) {
             return;
        }

        Class<?> clazz = obj.getClass();
        for (Field f : clazz.getFields()) { // 只处理 public 字段
            if (f.getType() == String.class) {
                try {
                    String val = (String) f.get(obj);
                    if (val != null) {
                        f.set(obj, replaceText(val));
                    }
                } catch (Exception e) {
                    // 忽略错误
                }
            } else if (f.getType() == String[].class) {
                try {
                    String[] val = (String[]) f.get(obj);
                    if (val != null) {
                        for (int i = 0; i < val.length; i++) {
                            val[i] = replaceText(val[i]);
                        }
                    }
                } catch (Exception e) {
                    // 忽略错误
                }
            }
        }
    }

    // Patch LocalizedStrings 构造方法，在所有文本加载完成后统一替换
    @SpirePatch(clz = LocalizedStrings.class, method = SpirePatch.CONSTRUCTOR)
    public static class LocalizedStringsPatch {
        @SpirePostfixPatch
        public static void Postfix(LocalizedStrings __instance) {
            System.out.println("HuiName: Starting text replacement...");
            Field[] fields = LocalizedStrings.class.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                try {
                    Object fieldValue = f.get(__instance);
                    if (fieldValue instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) fieldValue;
                        for (Object value : map.values()) {
                            replaceFields(value);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("HuiName: Text replacement complete.");
        }
    }
}
