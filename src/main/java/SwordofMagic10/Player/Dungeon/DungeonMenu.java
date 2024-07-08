package SwordofMagic10.Player.Dungeon;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Component.SomJson;
import SwordofMagic10.Component.SomSQL;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.DungeonDataLoader;
import SwordofMagic10.Player.Dungeon.Instance.DefensiveBattle;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Player.Dungeon.Instance.DungeonReward;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.SomParty;
import com.github.jasync.sql.db.RowData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.SomCore.Log;

public class DungeonMenu extends GUIManager {

    private DungeonInstance dungeon;
    private DungeonInstance selectDungeon;
    private DungeonDifficulty selectDifficulty;
    private boolean laterJoin = true;
    private boolean autoRetry = false;
    private MatchType matchType = null;
    private boolean legendRaid = false;
    public DungeonMenu(PlayerData playerData) {
        super(playerData, "ダンジョンボス選択", 1);
    }


    @Override
    public void topClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (matchType != null) {
                if (selectDungeon == null) {
                    if (CustomItemStack.hasCustomData(clickedItem, "DungeonID")) {
                        selectDungeon = DungeonDataLoader.getDungeon(CustomItemStack.getCustomData(clickedItem, "DungeonID"));
                        SomSound.Tick.play(playerData);
                    }
                } else {
                    if (!isInDungeon()) {
                        if (CustomItemStack.hasCustomData(clickedItem, "Difficulty")) {
                            selectDifficulty = DungeonDifficulty.valueOf(CustomItemStack.getCustomData(clickedItem, "Difficulty"));
                            if (selectDungeon.getBaseLevel(selectDifficulty) <= playerData.getLevel()) {
                                joinDungeon(selectDungeon, selectDifficulty);
                            } else {
                                playerData.sendMessage("§eレベル§aが足りません", SomSound.Nope);
                                selectDifficulty = null;
                            }
                        }
                    }
                }
            }

            if (CustomItemStack.hasCustomData(clickedItem, "MatchType")) {
                MatchType matchType = MatchType.valueOf(CustomItemStack.getCustomData(clickedItem, "MatchType"));
                this.matchType = this.matchType == null ? matchType : matchType.next();
                SomSound.Tick.play(playerData);
            } else if (CustomItemStack.hasCustomData(clickedItem, "PartyMatch")) {
                joinPartyDungeon();
            } else if (CustomItemStack.hasCustomData(clickedItem, "LaterJoin")) {
                laterJoin = !laterJoin;
                SomSound.Tick.play(playerData);
            } else if (CustomItemStack.hasCustomData(clickedItem, "AutoRetry")) {
                autoRetry = !autoRetry;
                SomSound.Tick.play(playerData);
            }
            update();
        }
    }

    public void joinPartyDungeon() {
        if (playerData.hasParty()) {
            for (PlayerData member : playerData.getParty().getMember()) {
                if (member.getDungeonMenu().isInDungeon()) {
                    DungeonInstance instance = member.getDungeonMenu().getDungeon();
                    if (playerData.getRawLevel() >= instance.getBaseLevel() && instance.isPartyJoinAble()) {
                        if (instance.getMember().size() < instance.getMatchType().getLimit()) {
                            instance.joinPlayer(playerData);
                            return;
                        }
                    }
                }
            }
            playerData.sendMessage("§b参加可能§aな§ePTメンバー§aの§cダンジョン§aがありません", SomSound.Nope);
        } else {
           playerData.sendMessage("§eパーティ§aに§b参加§aしていません", SomSound.Nope);
        }
    }

    public void joinDungeon(DungeonInstance dungeon, DungeonDifficulty difficulty) {
        if (matchType != MatchType.Solo) {
            if (laterJoin) {
                for (DungeonInstance instance : DungeonInstance.getDungeonInstanceList()) {
                    boolean isLaterJoin = instance.isLaterJoin() || (playerData.hasParty() && playerData.getParty().getMember().contains(instance.getMember().get(0)));
                    if (instance.getId().equalsIgnoreCase(dungeon.getId()) && instance.getMatchType() == matchType && instance.getDifficulty() == difficulty && instance.isJoinAble() && isLaterJoin) {
                        if (instance.getMember().size() < instance.getMatchType().getLimit()) {
                            instance.joinPlayer(playerData);
                            return;
                        }
                    }
                }
            } else if (playerData.hasParty()) {
                for (PlayerData memberData : playerData.getParty().getMember()) {
                    if (playerData == memberData) continue;
                    if (memberData.getDungeonMenu().isInDungeon()) {
                        DungeonInstance instance = memberData.getDungeonMenu().getDungeon();
                        if (instance.getId().equalsIgnoreCase(dungeon.getId()) && instance.getMatchType() == matchType && instance.getDifficulty() == difficulty && instance.isJoinAble()) {
                            if (instance.getMember().size() < instance.getMatchType().getLimit()) {
                                instance.joinPlayer(playerData);
                                return;
                            }
                        }
                    }
                }
            }
        }
        DungeonInstance newInstance = dungeon.clone();
        newInstance.setDifficulty(difficulty);
        newInstance.setLaterJoin(laterJoin);
        newInstance.setMatchType(matchType);
        DungeonInstance.getDungeonInstanceList().add(newInstance);
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

    public void open(boolean legendRaid) {
        if (!playerData.isInInstance()) {
            this.legendRaid = legendRaid;
            open();
        }
    }

    private final CustomItemStack PartyIcon = new CustomItemStack(Material.ENDER_PEARL).setDisplay("パーティに合流").addLore("§aPTメンバーが参加しているダンジョンに合流します").setCustomData("PartyMatch", true);
    @Override
    public void update() {
        clear();
        if (legendRaid) matchType = MatchType.Party;
        String mapId = playerData.getMapData().getId();
        if (DungeonInstance.complete().contains(mapId)) {
            selectDungeon = DungeonInstance.get(mapId);
        }
        if (matchType == null) {
            setItem(1, MatchType.Solo.viewItem());
            setItem(3, MatchType.Duo.viewItem());
            setItem(5, MatchType.Party.viewItem());
            setItem(7, MatchType.FreeSearch.viewItem());
        } else if (selectDungeon == null) {
            int slot = 0;
            for (DungeonInstance dungeonData : DungeonDataLoader.getDungeonList()) {
                if (dungeonData.isLegendRaid() == legendRaid) {
                    CustomItemStack item = new CustomItemStack(dungeonData.getIcon()).setCustomData("DungeonID", dungeonData.getId());
                    item.setDisplay(dungeonData.getDisplay());
                    item.addLore(dungeonData.getLore());
                    item.addSeparator("ダンジョン参加状況");
                    for (DungeonDifficulty difficulty : dungeonData.activeDifficulty()) {
                        int count = 0;
                        for (RowData objects : SomSQL.getSqlList("DungeonJoinStatus", new String[]{"Dungeon", "Difficulty"}, new String[]{dungeonData.getId(), difficulty.toString()}, "Count")) {
                            count += objects.getInt("Count");
                        }
                        item.addLore(decoLore(difficulty.toString()) + count + "人");
                    }
                    item.addSeparator("クリアタイム");
                    for (DungeonDifficulty difficulty : dungeonData.activeDifficulty()) {
                        item.addLore(decoLore(difficulty.toString()) + (hasClearTime(dungeonData.getId(), difficulty) ? getClearTime(dungeonData.getId(), difficulty) : "§c未クリア"));
                    }
                    setItem(slot, item);
                    slot++;
                }
            }
            if (playerData.hasParty()) setItem(8, PartyIcon);
        } else {
            if (!legendRaid) setItem(6, matchType.viewItem());
            setItem(7, new CustomItemStack(autoRetry ? Material.REDSTONE_TORCH : Material.LEVER).setDisplay("§e自動リトライ" + (autoRetry ? "§a✓" : "§c×")).setCustomData("AutoRetry", true));
            setItem(8, new CustomItemStack(laterJoin ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK).setDisplay("§e自動マッチング" + (laterJoin ? "§a✓" : "§c×")).setCustomData("LaterJoin", true));
            int slot = 0;
            for (DungeonDifficulty difficulty : selectDungeon.activeDifficulty()) {
                CustomItemStack item = new CustomItemStack(difficulty.getIcon()).setCustomData("Difficulty", difficulty.toString());
                item.setDisplay(difficulty.toString());
                item.addLore(decoLore("入場可能レベル") + selectDungeon.getBaseLevel(difficulty));
                item.addLore(decoLore("ボスレベル") + selectDungeon.getBossLevel(difficulty));
                item.addLore(decoLore("レベルシンク") + selectDungeon.getLevelSync(difficulty));
                item.addSeparator("クリア報酬");
                item.addLore(decoLore("アイテムレベル") + selectDungeon.getItemLevel(difficulty));
                DungeonReward reward = selectDungeon.getReward(difficulty);
                item.addLore(decoLore("クラス経験値") + scale(reward.getExp()));
                item.addLore(decoLore("報酬メル") + reward.getMel());
                reward.getUpgradeStone().forEach((tier, percent) -> item.addLore(decoLore("精錬石T" + tier) + (int)Math.floor(percent) + "~" + (int)Math.ceil(percent)));
                reward.getTierStone().forEach((tier, percent) -> item.addLore(decoLore("昇級石T" + tier) + (int)Math.floor(percent) + "~" + (int)Math.ceil(percent)));
                reward.getQualityStone().forEach((tier, percent) -> item.addLore(decoLore("品質変更石T" + tier) + (int)Math.floor(percent) + "~" + (int)Math.ceil(percent)));
                String[] priValue = new String[]{selectDungeon.getId(), difficulty.toString()};
                if (SomSQL.existSql(DungeonInstance.DungeonWorldRecord, DungeonInstance.priKey, priValue, "Party")) {
                    item.addSeparator("ワールドレコード");
                    item.addLore(decoLore("クリアタイム") + scale(SomSQL.getDouble(DungeonInstance.DungeonWorldRecord, DungeonInstance.priKey, priValue, "Time"), 3) + "秒");
                    SomJson json = new SomJson(SomSQL.getString(DungeonInstance.DungeonWorldRecord, DungeonInstance.priKey, priValue, "Party"));
                    for (String username : json.getStringList("Username")) {
                        item.addLore("§7・§f" + username);
                    }
                }
                setItem(slot, item);
                slot++;
            }
        }
    }

    public boolean isLaterJoin() {
        return laterJoin;
    }

    public void setLaterJoin(boolean laterJoin) {
        this.laterJoin = laterJoin;
    }

    public boolean isAutoRetry() {
        return autoRetry;
    }

    public void setAutoRetry(boolean autoRetry) {
        this.autoRetry = autoRetry;
    }

    private final String DungeonClearTime = "DungeonClearTime";
    private final String[] priKey = new String[]{"UUID", "Dungeon", "Difficulty"};
    private String[] priValue(String dungeonID, DungeonDifficulty difficulty) {
        return new String[]{playerData.getUUIDAsString(), dungeonID, difficulty.toString()};
    }

    public void updateClearTime(DungeonInstance dungeon, double newTime) {
        boolean isNew;
        if (hasClearTime(dungeon.getId(), dungeon.getDifficulty())) {
            double time = getClearTime(dungeon.getId(), dungeon.getDifficulty());
            isNew = time > newTime;
        } else isNew = true;
        if (isNew) {
            setClearTime(dungeon.getId(), dungeon.getDifficulty(), newTime);
            playerData.sendMessage(dungeon.getDisplay() + "[" + dungeon.getDifficulty() + "]§aの§eクリアタイム§aを§b更新§aしました §e[" + scale(newTime, 2) + "秒]", SomSound.Level);
        }
    }

    public boolean hasClearTime(String dungeonID, DungeonDifficulty difficulty) {
        return SomSQL.existSql(DungeonClearTime, priKey, priValue(dungeonID, difficulty));
    }

    public double getClearTime(String dungeonID, DungeonDifficulty difficulty) {
        return SomSQL.getDouble(DungeonClearTime, priKey, priValue(dungeonID, difficulty), "Time");
    }

    public void setClearTime(String dungeonID, DungeonDifficulty difficulty, double time) {
        SomSQL.setSql(DungeonClearTime, priKey, priValue(dungeonID, difficulty), "Time", time);
    }

    public enum MatchType {
        Solo("ソロ", "ダンジョンに1人で挑みます", Material.TOTEM_OF_UNDYING, 1),
        Duo("デュオ", "ダンジョンに最大2人で挑みます", Material.EMERALD, 2),
        Party("パーティ", "ダンジョンに最大5人で挑みます", Material.AXOLOTL_BUCKET, 5),
        FreeSearch("自由探索", "ダンジョンに自由に探索します", Material.CRAFTING_TABLE, 8),
        ;

        private final String display;
        private final List<String> lore;
        private final Material icon;
        private final int limit;

        MatchType(String display, String lore, Material icon, int limit) {
            this.display = display;
            this.lore = loreText(Collections.singletonList(lore));
            this.icon = icon;
            this.limit = limit;
        }

        public String getDisplay() {
            return display;
        }

        public List<String> getLore() {
            return lore;
        }

        public Material getIcon() {
            return icon;
        }

        public int getLimit() {
            return limit;
        }

        public CustomItemStack viewItem() {
            return new CustomItemStack(icon).setDisplay("§e" + getDisplay() + "モード").addLore(getLore()).setCustomData("MatchType", toString());
        }

        public MatchType next() {
            if (this.ordinal()+1 < MatchType.values().length) {
                return MatchType.values()[this.ordinal()+1];
            } else return MatchType.values()[0];
        }
    }
}
