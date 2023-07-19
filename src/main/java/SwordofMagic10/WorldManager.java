package SwordofMagic10;

import org.bukkit.GameRule;
import org.bukkit.World;

public class WorldManager {

    public static void setupWorld(World world) {
        world.setKeepSpawnInMemory(false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.BLOCK_EXPLOSION_DROP_DECAY, false);
        world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
        world.setGameRule(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK, true);
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_LIMITED_CRAFTING, true);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.DO_WARDEN_SPAWNING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
        world.setGameRule(GameRule.DROWNING_DAMAGE, true);
        world.setGameRule(GameRule.FALL_DAMAGE, false);
        world.setGameRule(GameRule.FIRE_DAMAGE, false);
        world.setGameRule(GameRule.FORGIVE_DEAD_PLAYERS, true);
        world.setGameRule(GameRule.FREEZE_DAMAGE, false);
        world.setGameRule(GameRule.GLOBAL_SOUND_EVENTS, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.LAVA_SOURCE_CONVERSION, false);
        world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
        world.setGameRule(GameRule.MAX_COMMAND_CHAIN_LENGTH, 16);
        world.setGameRule(GameRule.MAX_ENTITY_CRAMMING, 32);
        world.setGameRule(GameRule.MOB_EXPLOSION_DROP_DECAY, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.NATURAL_REGENERATION, false);
        world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 0);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        world.setGameRule(GameRule.REDUCED_DEBUG_INFO, false);
        world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, true);
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        world.setGameRule(GameRule.SNOW_ACCUMULATION_HEIGHT, 0);
        world.setGameRule(GameRule.SPAWN_RADIUS, 0);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        world.setGameRule(GameRule.TNT_EXPLOSION_DROP_DECAY, false);
        world.setGameRule(GameRule.UNIVERSAL_ANGER, false);
        world.setGameRule(GameRule.WATER_SOURCE_CONVERSION, false);
    }
}
