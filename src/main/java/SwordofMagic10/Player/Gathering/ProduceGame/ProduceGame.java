package SwordofMagic10.Player.Gathering.ProduceGame;

import SwordofMagic10.Component.*;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.Gathering.GatheringMenu;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Statistics;
import SwordofMagic10.SomCore;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.SomCore.Log;

public class ProduceGame extends GUIManager {

    private Game game = null;
    private final AimLab aimLab;
    private final LightsOut lightsOut;
    private final Pachislot pachislot;
    private final Typing typing;
    private final PianoTile pianoTile;
    public ProduceGame(PlayerData playerData) {
        super(playerData, "制作方法", 1);
        aimLab = new AimLab(playerData);
        lightsOut = new LightsOut(playerData);
        pachislot = new Pachislot(playerData);
        typing = new Typing(playerData);
        pianoTile = new PianoTile(playerData);
    }

    public Typing getTyping() {
        return typing;
    }

    public PianoTile getPianoTile() {
        return pianoTile;
    }

    public boolean isInGame() {
        return game != null;
    }

    public Game getGame() {
        return game;
    }

    public void leaveGame() {
        if (game != null) {
            playerData.sendMessage(game.getDisplay() + "§aを§c終了§aしました");
            game = null;
        }
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (CustomItemStack.hasCustomData(clickedItem, "Game")) {
                game = Game.valueOf(CustomItemStack.getCustomData(clickedItem, "Game"));
                switch (game) {
                    case AimLab -> aimLab.open();
                    case LightsOut -> lightsOut.open();
                    case Pachislot -> pachislot.open();
                    case Typing -> typing.start();
                    case PianoTile -> pianoTile.start();
                }
                SomSound.Tick.play(playerData);
            }
        }
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void open() {
        if (isInGame()) leaveGame();
        if (playerData.getProduce().getQueues().isEmpty()) {
            playerData.sendMessage("§b制作予約§aがありません", SomSound.Nope);
        }
        super.open();
    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    @Override
    public void update() {
        int slot = 0;
        for (Game game : Game.values()) {
            setItem(slot, game.viewItem());
            slot++;
        }
    }

    public boolean check(Game game) {
        return playerData.isOnline() && playerData.getGatheringMenu().isJoin() && getGame() == game;
    }

    public enum Game {
        AimLab("AimLab", "エイムトレーニング", Material.TARGET),
        LightsOut("ライツアウト", "すべてをそろえろ", Material.EMERALD),
        Pachislot("スロット", "ぴったり止めろ", Material.ENDER_CHEST),
        Typing("タイピング", "文字を入力しろ\n§e※終了する場合は「end」と入力します", Material.ITEM_FRAME),
        PianoTile("ピアノタイル", "4キーMania\n1,2,3,4キーまたは1,2,8,9キーでプレイ", Material.NOTE_BLOCK)
        ;

        private final String display;
        private final List<String> lore;
        private final Material icon;

        Game(String display, String lore, Material icon) {
            this.display = display;
            this.lore = loreText(List.of(lore.split("\n")));
            this.icon = icon;
        }

        public String getDisplay() {
            return display;
        }

        public Material getIcon() {
            return icon;
        }

        public CustomItemStack viewItem() {
            CustomItemStack item = new CustomItemStack(icon);
            item.setDisplay(display);
            item.addLore(lore);
            item.setCustomData("Game", this.toString());
            return item;
        }
    }
}
