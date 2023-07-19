package SwordofMagic10.Player.Quest;

import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.PlayerData;

import java.util.ArrayList;
import java.util.List;

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
            if (!playerData.getItemInventory().has(reqItem)) {
                return false;
            }
        }
        return true;
    }

    public List<SomItemStack> getReqItem() {
        return reqItem;
    }

    public void passItem(PlayerData playerData) {
        if (!flag) {
            if (check(playerData)) {
                for (SomItemStack reqItem : reqItem) {
                    playerData.getItemInventory().remove(reqItem);
                }
                flag = true;
                playerData.sendMessage("§c要求§aされた§eアイテム§aを渡しました", SomSound.Nope);
            } else {
                playerData.sendMessage("§aすでに§c要求§aされた§eアイテム§aを§e所持§aしていません", SomSound.Nope);
            }
        } else {
            playerData.sendMessage("§aすでに§eアイテム§aを渡しました", SomSound.Nope);
        }
    }

    @Override
    public boolean isProcess(PlayerData playerData) {
        return flag;
    }

    @Override
    public SomJson toJson() {
        SomJson json = new SomJson();
        return json;
    }

    @Override
    public QuestPhase fromJson(SomJson json) {
        return clone();
    }
}
