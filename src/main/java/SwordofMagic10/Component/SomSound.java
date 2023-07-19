package SwordofMagic10.Component;

import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Collection;

public enum SomSound {
    Tick(Sound.BLOCK_LEVER_CLICK, 1f),
    Nope(Sound.BLOCK_NOTE_BLOCK_HARP, 1f),
    Hurt(Sound.ENTITY_PLAYER_HURT, 1f),
    Cast(Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, 1),
    Level(Sound.ENTITY_PLAYER_LEVELUP, 1),
    FireWork(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1),
    Explode(Sound.ENTITY_GENERIC_EXPLODE, 1f),
    Blade(Sound.BLOCK_ANVIL_PLACE, 2f, 0.7f),

    Break(Sound.BLOCK_GRASS_BREAK, 0.5f),

    PeaceMaker(Sound.AMBIENT_CAVE, 1.5f),

    EnemyDeath(Sound.ENTITY_ARROW_HIT_PLAYER, 1.5f),

    Handgun(Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f),
    Cannon(Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f),

    Slash(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f),
    Bash(Sound.ENTITY_PLAYER_ATTACK_STRONG, 1f),

    Ball(Sound.ENTITY_SNOW_GOLEM_SHOOT, 1f),
    Fire(Sound.ENTITY_BLAZE_SHOOT, 1f),
    Flame(Sound.BLOCK_FIRE_AMBIENT, 1f),
    Ice(Sound.BLOCK_GLASS_BREAK, 1f),
    Bell(Sound.BLOCK_BELL_USE, 1f),
    Shine(Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 100f, 10),

    Shot(Sound.ENTITY_ARROW_SHOOT, 1f),
    ArrowRain(Sound.ENTITY_ARROW_HIT, 1f),

    Blow(Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f),
    Heal(Sound.ENTITY_ENDER_EYE_DEATH, 1f),
    Porn(Sound.ENTITY_ENDER_EYE_DEATH, 0.5f),

    JetDash(Sound.ENTITY_WITHER_SHOOT, 1f),
    NearAura(Sound.ENTITY_WOLF_HOWL, 1f),

    Curse(Sound.BLOCK_BEACON_DEACTIVATE, 0.5f),
    BossDefeat(Sound.ITEM_GOAT_HORN_SOUND_1, 1.5f),
    TimeOver(Sound.ITEM_GOAT_HORN_SOUND_7, 1.4f),
    Warp(Sound.ENTITY_ENDERMAN_TELEPORT, 1f),

    HeadShot(Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f),
    TransUp(Sound.ITEM_TOTEM_USE, 1f),
    Success(Sound.BLOCK_ANVIL_USE, 1f),
    Failed(Sound.BLOCK_ANVIL_DESTROY, 1f),
    RareDrop(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f),
    Booster(Sound.ENTITY_WITHER_SPAWN, 1.25f),
    BossSpawn(Sound.ENTITY_WITHER_SPAWN, 0.75f),
    BossSkill(Sound.ENTITY_ENDER_DRAGON_GROWL, 1f),
    ;

    private final Sound sound;
    private final float pitch;
    private final float volume;
    private final int amount;

    SomSound(Sound sound, float pitch) {
        this.sound = sound;
        this.pitch = pitch;
        volume = 1f;
        amount = 1;
    }

    SomSound(Sound sound, float pitch, float volume) {
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
        amount = 1;
    }

    SomSound(Sound sound, float pitch, float volume, int amount) {
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
        this.amount = amount;
    }

    public void playRadius(Location location, Collection<PlayerData> viewers) {
        for (PlayerData playerData : viewers) {
            play(playerData.getPlayer(), location);
        }
    }

    public void play(SomEntity entity) {
        if (entity instanceof PlayerData playerData) {
            play(playerData);
        }
    }

    public void play(Collection<PlayerData> playerList) {
        for (PlayerData playerData : playerList) {
            play(playerList, playerData.getEyeLocation());
        }
    }

    public void play(PlayerData playerData) {
        play(playerData.getPlayer(), playerData.getSoundLocation());
    }

    public void play(PlayerData playerData, Location location) {
        play(playerData.getPlayer(), location);
    }


    public void play(Collection<PlayerData> playerList, Location location) {
        for (PlayerData playerData : playerList) {
            play(playerData.getPlayer(), location);
        }
    }

    public void play(Player player, Location location) {
        for (int i = 0; i < amount; i++) {
            player.playSound(location, sound, SoundCategory.PLAYERS, volume, pitch);
        }
    }
}
