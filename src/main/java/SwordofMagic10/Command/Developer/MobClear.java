package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class MobClear implements SomCommand {
    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        return false;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        sender.sendMessage("MobClear: " + clean());
        return true;
    }

    public static int clean() {
        int i = 0;
        for (World world : Bukkit.getWorlds()) {
            i += clean(world);
        }
        return i;
    }

    public static int clean(World world) {
        int i = 0;
        for (Entity entity : world.getEntities()) {
            if (clean(entity)) i++;;
        }
        return i;
    }

    public static int clean(Chunk chunk) {
        int i = 0;
        for (Entity entity : chunk.getEntities()) {
            if (clean(entity)) i++;;
        }
        return i;
    }

    public static boolean clean(Entity entity) {
        if (entity.hasMetadata(Config.EnemyMetaAddress)) {
            for (MetadataValue metadata : entity.getMetadata(Config.EnemyMetaAddress)) {
                if (metadata.getOwningPlugin() == (SomCore.plugin())) {
                    if (metadata.value() instanceof EnemyData enemyData) {
                        enemyData.delete();
                        return true;
                    }
                }
            }
        }
        return cleanOther(entity);
    }

    public static boolean cleanOther(Entity entity) {
        switch (entity.getType()) {
            case ITEM_FRAME, GLOW_ITEM_FRAME, PLAYER -> {}
            default -> {
                if (!entity.getScoreboardTags().contains("NoClear")) {
                    if (entity.getName().contains("§c《") || entity.hasMetadata(Config.SomParticleMetaAddress)) {
                        SomTask.sync(entity::remove);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

