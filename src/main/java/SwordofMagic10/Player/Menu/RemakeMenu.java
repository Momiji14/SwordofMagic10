package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomText;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.Gathering.GatheringMenu;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.decoText;

public class RemakeMenu extends GUIManager {
    private SomTool tool;
    private SomTool result;
    public RemakeMenu(PlayerData playerData) {
        super(playerData, "リメイク", 1);
    }

    @Override
    public void topClick(InventoryClickEvent event) {
        switch (event.getSlot()) {
            case 1 -> tool = null;
            case 7 -> {
                if (result != null) {
                    if (playerData.getMel() >= mel()) {
                        boolean fall = false;
                        List<SomText> message = new ArrayList<>();
                        message.add(SomText.create(decoText("必要リスト")));
                        for (SomItemStack stack : tool.getRemake().getRecipeSlot()) {
                            SomItem itemData = stack.getItem();
                            SomText itemText = SomText.create("§7・").add(itemData.toSomText(stack.getAmount()));
                            if (playerData.getItemInventory().req(stack)) {
                                message.add(itemText.add("§a✔"));
                            } else {
                                message.add(itemText.add("§c✖"));
                                fall = true;
                            }
                        }
                        if (fall) {
                            playerData.sendSomText(message, SomSound.Nope);
                        } else {
                            playerData.removeMel(mel());
                            for (SomItemStack stack : tool.getRemake().getRecipeSlot()) {
                                playerData.getItemInventory().remove(stack);
                            }
                            playerData.getItemInventory().remove(tool, 1);
                            playerData.getItemInventory().add(result, 1);
                            playerData.sendSomText(result.toSomText().add("§aを§eリメイク§aしました"), SomSound.Level);
                            tool = null;
                            result = null;
                        }
                    } else {
                        playerData.sendMessage("§eメル§aが足りません", SomSound.Nope);
                    }
                }
            }
        }
        update();
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (playerData.getInventoryViewer().isSomItemStack(clickedItem)) {
            SomItemStack stack = playerData.getInventoryViewer().getSomItemStack(clickedItem);
            SomItem item = stack.getItem();
            if (this.tool == null) {
                if (item instanceof SomTool tool && tool.hasRemake()) {
                    if (tool.getLevel() != tool.getRawLevel()) {
                        playerData.sendMessage("§aこの§eツール§aは§eレベル§aが初期値でないため§eリメイク§a出来ません", SomSound.Nope);
                        return;
                    }
                    if (tool.getLevel() >= playerData.getGatheringMenu().getLevel(GatheringMenu.Type.Produce)) {
                        playerData.sendMessage("§aあなたより§c低い技量§aで制作された§eツール§aのみ§eリメイク§a出来ます", SomSound.Nope);
                        return;
                    }
                    this.tool = tool;
                    SomSound.Tick.play(playerData);
                } else {
                    playerData.sendMessage("§eリメイク可能ツール§aを§b選択§aしてください", SomSound.Nope);
                }
            }
            update();
        }
    }

    public int mel() {
        return 1000+(result.getLevel())^2;
    }

    @Override
    public void close(InventoryCloseEvent event) {
        tool = null;
        result = null;
    }

    @Override
    public void update() {
        clear();
        for (int i = 0; i < 9; i++) {
            setItem(i, Config.FlameItem);
        }
        if (tool != null) {
            result = tool.clone();
            result.setLevel(playerData.getGatheringMenu().getLevel(GatheringMenu.Type.Produce));
            result.setRawLevel(result.getLevel());
            setItem(1, tool.viewItem());
            CustomItemStack resultView = result.viewItem();
            resultView.addSeparator("強化費用");
            resultView.addLore("§7・§e" + mel() + "メル");
            for (SomItemStack stack : tool.getRemake().getRecipeSlot()) {
                resultView.addLore("§7・" + stack.getItem().getColorTierDisplay() + "§ex" + stack.getAmount());
            }
            setItem(7, resultView);
        } else {
            setItem(1, Config.AirItem);
            setItem(7, Config.AirItem);
        }
    }
}
