package SwordofMagic10.Entity.Enemy.Boss;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.Map.MapData;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;

import static SwordofMagic10.Component.Function.randomInt;
import static SwordofMagic10.Player.Dungeon.Instance.DungeonInstance.Radius;

public abstract class EnemyBoss extends EnemyData {
    protected EnemyBoss(MobData mobData, int level, DungeonDifficulty difficulty, Location location, Collection<PlayerData> viewers, MapData mapData, DungeonInstance dungeon) {
        super(mobData, level, difficulty, location, viewers, mapData, dungeon);
    }

    public HashMap<PlayerData, Integer> sendBossSkillMessage(String[] messageData) {
        HashMap<PlayerData, Integer> indexEachPlayer = new HashMap<>();
        for (PlayerData playerData : getViewers(Radius)) {
            int index = randomInt(0, messageData.length);
            String message = messageData[index];
            playerData.sendMessage(message, SomSound.BossSkill);
            playerData.sendTitle(message, "", 10, 40, 10);
            indexEachPlayer.put(playerData, index);
        }
        return indexEachPlayer;
    }
    public void sendBossSkillMessage(String message){
        for (PlayerData playerData : getViewers(Radius)) {
            playerData.sendMessage(message, SomSound.BossSkill);
            playerData.sendTitle(message, "", 10, 40, 10);
        }
    }

    public void sendBossMessage(String message){
        for (PlayerData playerData : getViewers(Radius)) {
            playerData.sendMessage(message, SomSound.BossSkill);
        }
    }

    public EnemyData spawnByBoss(MobData mobData, Location location){
        return spawnByBoss(mobData, location, getLevel());
    }

    public EnemyData spawnByBoss(MobData mobData, Location location, int level) {
        EnemyData enemyData;
        if (isInDungeon()){
            enemyData = EnemyData.spawn(mobData, level, getDifficulty(), location, getViewers(), getMapData(), getDungeon());
            getDungeon().addEnemies(enemyData);
        } else {
            enemyData = EnemyData.spawn(mobData, level, getDifficulty(), location, getViewers(), getMapData());
        }
        return enemyData;
    }

    public Collection<SomEntity> getTargetsOutHate(){
        Collection<SomEntity> targets = getTargets();
        targets.remove(getTarget());
        return targets;
    }
}
