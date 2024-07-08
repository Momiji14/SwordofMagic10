package SwordofMagic10.Player.Achievement;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.SomSQL;
import SwordofMagic10.DataBase.AchievementDataLoader;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import com.github.jasync.sql.db.RowData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AchievementMenu extends GUIManager {
    private static final String Table = "PlayerAchievement";
    private static final String[] priKey = new String[]{"UUID", "Achievement"};
    private final HashMap<AchievementData, LocalDateTime> achievement = new HashMap<>();
    private AchievementData viewAchievement;

    public AchievementMenu(PlayerData playerData) {
        super(playerData, "アチーブメント", 6);
        if (SomSQL.existSql(Table, "UUID", playerData.getUUIDAsString())) {
            for (RowData objects : SomSQL.getSqlList(Table, "UUID", playerData.getUUIDAsString(), "*")) {
                try {
                    achievement.put(AchievementDataLoader.getAchievementData(objects.getString("Achievement")), LocalDateTime.parse(objects.getString("Date"), Config.DateFormat));
                } catch (Exception ignore) {}
            }
        }
        if (SomSQL.existSql(PlayerData.Table, "UUID", playerData.getUUIDAsString(), "ViewAchievement")) {
            String viewAchievement = SomSQL.getString(PlayerData.Table, "UUID", playerData.getUUIDAsString(), "ViewAchievement");
            if (!"".equals(viewAchievement)) {
                this.viewAchievement = AchievementDataLoader.getAchievementData(viewAchievement);
            }
        }
    }

    public void add(AchievementData achievement) {
        this.achievement.put(achievement, LocalDateTime.now());
        String[] priValue = new String[]{playerData.getUUIDAsString(), achievement.getId()};
        SomSQL.setSql(Table, priKey, priValue, "Username", playerData.getUsername());
        SomSQL.setSql(Table, priKey, priValue, "Date", LocalDateTime.now().format(Config.DateFormat));
    }

    public AchievementData getViewAchievement() {
        return viewAchievement;
    }

    public void setViewAchievement(AchievementData viewAchievement) {
        this.viewAchievement = viewAchievement;
        SomSQL.setSql(PlayerData.Table, "UUID", playerData.getUUIDAsString(), "ViewAchievement", viewAchievement.getId());
    }

    @Override
    public void topClick(InventoryClickEvent event) {

    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    @Override
    public void update() {
        int slot = 0;
        for (AchievementData achievementData : achievement.keySet()) {
            setItem(slot, achievementData.viewItem());
            slot++;
        }
    }
}
