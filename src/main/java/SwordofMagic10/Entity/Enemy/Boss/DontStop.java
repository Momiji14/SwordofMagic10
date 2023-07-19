package SwordofMagic10.Entity.Enemy.Boss;

import SwordofMagic10.Dungeon.DungeonDifficulty;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;

import java.util.Collection;

public class DontStop extends EnemyData {

    public DontStop(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers) {
        super(mobData, level, difficulty, location, viewers);
    }

    private static final SomEffect dontStop = new SomEffect("DontStop", "止まるな", false, 30);

    @Override
    public void tick() {
        for (PlayerData playerData : getViewers()) {
            if (playerData.hasEffect(dontStop.getId())) {
                if (playerData.getMovementSpeed() == 0) {
                    playerData.death(dontStop.getDisplay() + "§aによって§4死亡§aしました");
                }
            }
        }
    }
}
