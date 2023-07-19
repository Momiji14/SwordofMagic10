package SwordofMagic10.Component;

import net.md_5.bungee.api.chat.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SomText implements Cloneable, Serializable {

    private ComponentBuilder builder = new ComponentBuilder();

    public static SomText create(String text) {
        return new SomText().addText(text);
    }

    public static SomText create() {
        return new SomText();
    }

    private SomText() {

    }

    public SomText reset() {
        builder.reset();
        return this;
    }

    public SomText addText(String text) {
        builder.append(text);
        return this;
    }

    public SomText addText(SomText text) {
        builder.append(text.toComponent());
        return this;
    }

    public SomText addLore(String prefix, String value) {
        builder.append("§7・§e" + prefix + "§7: §a" + value);
        return this;
    }

    public SomText addLore(String prefix, int value) {
        return addLore(prefix, String.valueOf(value));
    }

    public SomText addDeco(String text) {
        builder.append("§6====== " + text + " §6======");
        return this;
    }

    public SomText addHover(String text, String hoverText) {
        return addHover(text, SomText.create(hoverText));
    }

    public SomText addHover(String text, SomText hoverText) {
        TextComponent hover = new TextComponent(text);
        hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText.toComponent()));
        builder.append(hover);
        return this;
    }

    public SomText addClickEvent(String text, SomText hoverText, String command, ClickEvent.Action action) {
        TextComponent runCommand = new TextComponent(text);
        runCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText.toComponent()));
        runCommand.setClickEvent(new ClickEvent(action, command));
        builder.append(runCommand);
        return this;
    }

    public SomText addRunCommand(String text, String hoverText, String command) {
        return addRunCommand(text, SomText.create(hoverText), command);
    }

    public SomText addRunCommand(String text, SomText hoverText, String command) {
        return addClickEvent(text, hoverText, command, ClickEvent.Action.RUN_COMMAND);
    }

    public SomText addSuggestCommand(String text, String hoverText, String command) {
        return addSuggestCommand(text, SomText.create(hoverText), command);
    }

    public SomText addSuggestCommand(String text, SomText hoverText, String command) {
        return addClickEvent(text, hoverText, command, ClickEvent.Action.SUGGEST_COMMAND);
    }

    public SomText addClipboard(String text, String hoverText, String command) {
        return addClipboard(text, SomText.create(hoverText), command);
    }

    public SomText addClipboard(String text, SomText hoverText, String command) {
        return addClickEvent(text, hoverText, command, ClickEvent.Action.COPY_TO_CLIPBOARD);
    }

    public SomText addOpenURL(String text, String hoverText, String url) {
        return addOpenURL(text, SomText.create(hoverText), url);
    }

    public SomText addOpenURL(String text, SomText hoverText, String url) {
        return addClickEvent(text, hoverText, url, ClickEvent.Action.OPEN_URL);
    }

    public SomText newLine() {
        builder.append("\n");
        return this;
    }

    public BaseComponent[] toComponent() {
        return builder.create();
    }

    public static SomText getNowTime() {
        String formatNowDate = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH時mm分ss秒 E曜日").format(LocalDateTime.now());
        return SomText.create().addRunCommand("§7[" + LocalTime.now().format(DateTimeFormatter.ofPattern("H:m:s")) + "]§r", "§e" + formatNowDate, "/chatTimeStamp");
    }

    @Override
    public SomText clone() {
        try {
            SomText clone = (SomText) super.clone();
            clone.builder = new ComponentBuilder(builder);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
