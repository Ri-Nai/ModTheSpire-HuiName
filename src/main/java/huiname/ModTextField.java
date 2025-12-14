package huiname;

import basemod.IUIElement;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class ModTextField implements IUIElement, InputProcessor {
    private String text = "";
    private float x;
    private float y;
    private float width;
    private float height;
    private boolean isFocused = false;
    private Rectangle hitBox;
    private String placeholder;
    private InputProcessor oldInputProcessor;

    public ModTextField(String placeholder, float x, float y, float width, float height) {
        this.placeholder = placeholder;
        this.x = x * Settings.scale;
        this.y = y * Settings.scale;
        this.width = width * Settings.scale;
        this.height = height * Settings.scale;
        this.hitBox = new Rectangle(this.x, this.y, this.width, this.height);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void render(SpriteBatch sb) {
        // Draw background
        sb.setColor(Color.GRAY);
        if (isFocused) {
            sb.setColor(Color.GOLD);
        }
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x - 2, y - 2, width + 4, height + 4);
        
        // Draw inner background
        sb.setColor(new Color(0.2f, 0.2f, 0.2f, 1.0f));
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y, width, height);

        // Draw text
        BitmapFont font = FontHelper.tipBodyFont;
        String renderText = text;
        Color textColor = Color.WHITE;
        
        if (text.isEmpty() && !isFocused) {
            renderText = placeholder;
            textColor = Color.GRAY;
        }
        
        // Simple cursor
        if (isFocused && System.currentTimeMillis() % 1000 > 500) {
            renderText += "|";
        }

        FontHelper.renderFontLeft(sb, font, renderText, x + 10, y + height / 2 + 7, textColor);
    }

    @Override
    public void update() {
        if (InputHelper.justClickedLeft) {
            float mx = InputHelper.mX;
            float my = InputHelper.mY;
            if (mx >= x && mx <= x + width && my >= y && my <= y + height) {
                if (!isFocused) {
                    isFocused = true;
                    oldInputProcessor = Gdx.input.getInputProcessor();
                    Gdx.input.setInputProcessor(this);
                }
            } else {
                if (isFocused) {
                    isFocused = false;
                    if (oldInputProcessor != null) {
                        Gdx.input.setInputProcessor(oldInputProcessor);
                    }
                }
            }
        }
    }

    @Override
    public int renderLayer() {
        return 1; // Top layer
    }

    @Override
    public int updateOrder() {
        return 1;
    }

    // InputProcessor methods
    @Override
    public boolean keyDown(int keycode) {
        if (isFocused) {
            if (keycode == Input.Keys.BACKSPACE && text.length() > 0) {
                text = text.substring(0, text.length() - 1);
                return true;
            }
            // Paste from clipboard (Ctrl+V)
            if (keycode == Input.Keys.V && (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT))) {
                try {
                    String clipboard = Gdx.app.getClipboard().getContents();
                    if (clipboard != null) {
                        text += clipboard;
                    }
                } catch (Exception e) {}
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) { return false; }

    @Override
    public boolean keyTyped(char character) {
        if (isFocused) {
            // Filter non-printable characters if needed, but Chinese input needs them.
            // LibGDX keyTyped handles most characters including Chinese if IME is supported.
            // However, Slay the Spire's default font might not support all characters.
            // We rely on FontHelper.tipBodyFont which usually supports current language.
            if (Character.isISOControl(character) && character != '\b') return false;
            if (character == '\b') return false; // Handled in keyDown
            if (character == '\r' || character == '\n') return false; // Ignore enter
            
            text += character;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override
    public boolean scrolled(int amount) { return false; }
}
