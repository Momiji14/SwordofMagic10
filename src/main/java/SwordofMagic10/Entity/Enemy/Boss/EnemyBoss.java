package SwordofMagic10.Entity.Enemy.Boss;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Dungeon.DungeonDifficulty;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;

import java.util.Collection;

import static SwordofMagic10.Component.Function.randomInt;
import static SwordofMagic10.Dungeon.Instance.DungeonInstance.Radius;

public abstract class EnemyBoss extends EnemyData {
    protected EnemyBoss(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers) {
        super(mobData, level, difficulty, location, viewers);
    }

    public void sendBossSkillMessage(String[] messageData) {
        for (PlayerData playerData : getViewers(Radius)) {
            String message = messageData[randomInt(0, messageData.length)];
            playerData.sendMessage(message, SomSound.BossSkill);
            playerData.sendTitle(message, "", 10, 40, 10);
        }
    }
}
