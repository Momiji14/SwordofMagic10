package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomText;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Shop.RecipeData;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.decoText;

public class QuestPassItem extends QuestPhase {
    private final List<SomItemStack> reqItem = new ArrayList<>();
    private String handler;

    public QuestPassItem(QuestData questData) {
        super(questData);
    }

    public void addReqItem(SomItem item, int amount) {
        reqItem.add(new SomItemStack(item, amount));
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public boolean check(PlayerData playerData) {
        for (SomItemStack reqItem : reqItem) {
            if (!playerData.getItemInventory().req(reqItem)) {
                return false;
            }
        }
        return true;
    }

    public List<SomItemStack> getReqItem() {
        return reqItem;
    }

    public boolean passItem(PlayerData playerData) {
        if (!flag) {
            if (check(playerData)) {
                for (SomItemStack reqItem : reqItem) {
                    playerData.getItemInventory().removeReq(reqItem);
                }
                flag = true;
                playerData.sendMessage("§c要求§aされた§eアイテム§aを渡しました", SomSound.Tick);
                return true;
            } else {
                List<SomText> message = new ArrayList<>();
                message.add(SomText.create(decoText("必要リスト")));
                for (SomItemStack stack : reqItem) {
                    SomItem itemData = stack.getItem();
                    SomText itemText = SomText.create("§7・").add(itemData.toSomText(stack.getAmount()));
                    if (playerData.getItemInventory().has(stack)) {
                        message.add(itemText.add("§a✔"));
                    } else {
                        message.add(itemText.add("§c✖"));
                    }
                }
                playerData.sendSomText(message, SomSound.Nope);
                return false;
            }
        } else {
            playerData.sendMessage("§aすでに§eアイテム§aを渡しました", SomSound.Nope);
            return false;
        }
    }

    @Override
    public boolean isProcess(PlayerData playerData) {
        return flag;
    }

    @Override
    public List<String> sidebarLine(PlayerData playerData) {
        List<String> list = new ArrayList<>();
        for (SomItemStack stack : reqItem) {
            SomItem item = stack.getItem();
            SomItemStack hasStack = playerData.getItemInventory().get(item).orElse(new SomItemStack(item, 0));
            list.add("§7・§r" + item.getColorTierDisplay() + " §a" + hasStack.getAmount() + "/" + stack.getAmount());
        }
        return list;
    }
}
