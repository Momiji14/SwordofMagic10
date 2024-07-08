package SwordofMagic10.Player.QuickGUI;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Player.Dungeon.Instance.DefensiveBattle;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DefensiveBattleMenu extends QuickGUI {
    private static DefensiveBattleMenu defensiveBattleMenu;
    public static void open(PlayerData playerData) {
        defensiveBattleMenu.openIns(playerData);
    }

    public DefensiveBattleMenu() {
        super(Type.DefensiveBattle);
        int slot = 1;
        for (DefensiveBattle.MatchType matchType : DefensiveBattle.MatchType.values()) {
            setItem(slot, matchType.viewItem());
            slot += 2;
        }
        defensiveBattleMenu = this;
    }

    @Override
    public void topClick(PlayerData playerData, InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (playerData.isInCity()) {
                if (CustomItemStack.hasCustomData(clickedItem, "MatchType")) {
                    List<PlayerData> players = new ArrayList<>();
                    if (playerData.hasParty()) {
                        players.addAll(playerData.getParty().getMember());
                    } else {
                        players.add(playerData);
                    }
                    DefensiveBattle.MatchType matchType = DefensiveBattle.MatchType.valueOf(CustomItemStack.getCustomData(clickedItem, "MatchType"));
                    if (players.size() > matchType.getLimit()) {
                        playerData.sendMessage("§c人数制限§aです", SomSound.Nope);
                        return;
                    }
                    if (matchType != DefensiveBattle.MatchType.Solo) {
                        for (DefensiveBattle instance : DefensiveBattle.getInstanceList()) {
                            if (!instance.isEnd() && instance.getMatchType() == matchType && matchType.getLimit() >= instance.getMember().size() + players.size()) {
                                for (PlayerData member : players) {
                                    instance.joinPlayer(member);
                                }
                                return;
                            }
                        }
                    }
                    DefensiveBattle newInstance = new DefensiveBattle(matchType);
                    for (PlayerData member : players) {
                        newInstance.joinPlayer(member);
                    }
                }
            }
        }
    }
}
