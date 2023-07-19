package SwordofMagic10.Dungeon;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.DataBase.DungeonDataLoader;
import SwordofMagic10.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Dungeon.Instance.DungeonReward;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.decoLore;

public class DungeonMenu extends GUIManager {

    private DungeonInstance dungeon;
    private DungeonInstance selectDungeon;
    private DungeonDifficulty selectDifficulty;
    private boolean laterJoin = true;
    private boolean partyJoin = true;
    private boolean autoRetry = false;
    public DungeonMenu(PlayerData playerData) {
        super(playerData, "ダンジョン選択", 1);
    }

    private static final int PartyLimit = 5;
    @Override
    public void topClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (selectDungeon == null) {
            if (DungeonDataLoader.getDungeonList().size() > slot) {
                selectDungeon = DungeonDataLoader.getDungeonList().get(slot);
            }
            update();
        } else if (selectDifficulty == null) {
            switch (slot) {
                case 8 -> {
                    laterJoin = !laterJoin;
                    update();
                }
                case 7 -> {
                    partyJoin = !partyJoin;
                    update();
                }
                case 6 -> {
                    autoRetry = !autoRetry;
                    update();
                }
                default -> {
                    if (!isInDungeon()) {
                        if (selectDungeon.activeDifficulty().size() > slot) {
                            selectDifficulty = DungeonDifficulty.values()[slot];
                            if (selectDungeon.getBaseLevel(selectDifficulty) <= playerData.getLevel()) {
                                joinDungeon(selectDungeon, selectDifficulty);
                            } else {
                                playerData.sendMessage("§eレベル§aが足りません");
                                selectDifficulty = null;
                            }
                        }
                    }
                }
            }
        }
    }

    public void joinDungeon(DungeonInstance dungeon, DungeonDifficulty difficulty) {
        if (partyJoin && playerData.hasParty()) {
            for (PlayerData member : playerData.getParty().getMember()) {
                if (member.getDungeonMenu().isInDungeon()) {
                    DungeonInstance instance = member.getDungeonMenu().getDungeon();
                    if (instance.getMember().size() < PartyLimit) {
                        instance.joinPlayer(playerData);
                        return;
                    }
                }
            }
        }
        if (laterJoin) {
            for (DungeonInstance instance : DungeonInstance.getDungeonInstance()) {
                if (instance.getId().equalsIgnoreCase(dungeon.getId()) && instance.getDifficulty() == difficulty && instance.isJoinAble()) {
                    if (instance.getMember().size() < PartyLimit) {
                        instance.joinPlayer(playerData);
                        return;
                    }
                }
            }
        }
        DungeonInstance newInstance = dungeon.clone();
        newInstance.setDifficulty(difficulty);
        newInstance.setLaterJoin(laterJoin);
        DungeonInstance.getDungeonInstance().add(newInstance);
        newInstance.joinPlayer(playerData);
    }

    public void setDungeon(DungeonInstance dungeon) {
        this.dungeon = dungeon;
    }

    public DungeonInstance getDungeon() {
        return dungeon;
    }

    public boolean isInDungeon() {
        return dungeon != null;
    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void close(InventoryCloseEvent event) {
        selectDungeon = null;
        selectDifficulty = null;
    }

    @Override
    public void update() {
        clear();
        if (selectDungeon == null) {
            int slot = 0;
            for (DungeonInstance dungeonData : DungeonDataLoader.getDungeonList()) {
                CustomItemStack item = new CustomItemStack(dungeonData.getIcon());
                item.setDisplay(dungeonData.getDisplay());
                item.addLore("§a" + dungeonData.getDisplay() + "です");
                for (DungeonDifficulty difficulty : dungeonData.activeDifficulty()) {
                    item.addSeparator(difficulty.toString());
                    item.addLore(dungeonLore(dungeonData, difficulty));
                }
                setItem(slot, item);
                slot++;
            }
        } else {
            setItem(6, new CustomItemStack(autoRetry ? Material.REDSTONE_TORCH : Material.LEVER).setDisplay("§e自動リトライ" + (autoRetry ? "§a✓" : "§c×")));
            setItem(7, new CustomItemStack(partyJoin ? Material.GLOWSTONE_DUST : Material.REDSTONE).setDisplay("§eパーティ優先参加" + (partyJoin ? "§a✓" : "§c×")));
            setItem(8, new CustomItemStack(laterJoin ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplay("§e途中参加" + (laterJoin ? "§a✓" : "§c×")));
            int slot = 0;
            for (DungeonDifficulty difficulty : selectDungeon.activeDifficulty()) {
                CustomItemStack item = new CustomItemStack(difficulty.getIcon());
                item.setDisplay(difficulty.toString());
                item.addLore(dungeonLore(selectDungeon, difficulty));
                setItem(slot, item);
                slot++;
            }
        }
    }

    public List<String> dungeonLore(DungeonInstance dungeon, DungeonDifficulty difficulty) {
        List<String> lore = new ArrayList<>();
        lore.add(decoLore("入場可能レベル") + dungeon.getBaseLevel(difficulty));
        lore.add(decoLore("レベルシンク") + dungeon.getLevelSync(difficulty));
        DungeonReward reward = dungeon.getReward(difficulty);
        lore.add(decoLore("クラス経験値") + reward.getExp());
        reward.getUpgradeStone().forEach((tier, percent) -> lore.add(decoLore("精錬石T" + tier) + (int)Math.floor(percent) + "~" + (int)Math.ceil(percent)));
        reward.getTierStone().forEach((tier, percent) -> lore.add(decoLore("昇級石T" + tier) + (int)Math.floor(percent) + "~" + (int)Math.ceil(percent)));
        reward.getQualityStone().forEach((tier, percent) -> lore.add(decoLore("品質変更石T" + tier) + (int)Math.floor(percent) + "~" + (int)Math.ceil(percent)));
        return  lore;
    }

    public boolean isLaterJoin() {
        return laterJoin;
    }

    public void setLaterJoin(boolean laterJoin) {
        this.laterJoin = laterJoin;
    }

    public boolean isPartyJoin() {
        return partyJoin;
    }

    public void setPartyJoin(boolean partyJoin) {
        this.partyJoin = partyJoin;
    }

    public boolean isAutoRetry() {
        return autoRetry;
    }

    public void setAutoRetry(boolean autoRetry) {
        this.autoRetry = autoRetry;
    }
}
