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
    Hit(Sound.ENTITY_ARROW_HIT_PLAYER, 1f),
    Hurt(Sound.ENTITY_PLAYER_HURT, 1f),
    Cast(Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, 1),
    Level(Sound.ENTITY_PLAYER_LEVELUP, 1),
    FireWork(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1),
    Explode(Sound.ENTITY_GENERIC_EXPLODE, 1f),
    Blade(Sound.BLOCK_ANVIL_PLACE, 2f),

    Break(Sound.BLOCK_GRASS_BREAK, 0.5f),

    PeaceMaker(Sound.AMBIENT_CAVE, 1.5f),
    Dissonanz(Sound.ITEM_GOAT_HORN_SOUND_5, 1f),
    Triump(Sound.ITEM_GOAT_HORN_SOUND_0, 1f),
    Wiegenlied(Sound.ITEM_GOAT_HORN_SOUND_7, 1f),
    Burst(Sound.ITEM_TOTEM_USE, 1f),
    TickTack(Sound.UI_LOOM_SELECT_PATTERN, 0.3f),
    Taken(Sound.ENTITY_ALLAY_ITEM_TAKEN, 1f),

    EnemyDeath(Sound.ENTITY_ARROW_HIT_PLAYER, 1.5f),

    TickLow(Sound.BLOCK_LEVER_CLICK, 0.1f),
    TickMiddle(Sound.BLOCK_LEVER_CLICK, 0.8f),
    Handgun(Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f),
    MachineGun(Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.5f),
    Cannon(Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f),

    Slash(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f),
    Swish(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 2f),
    Bash(Sound.ENTITY_PLAYER_ATTACK_STRONG, 1f),
    Push(Sound.ITEM_SHIELD_BLOCK, 1f),
    Web(Sound.BLOCK_STEM_HIT, 1f),
    Bow(Sound.ENTITY_ARROW_HIT, 0.01f),
    Wood(Sound.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, 0.1f),

    Ball(Sound.ENTITY_SNOW_GOLEM_SHOOT, 1f),
    Fire(Sound.ENTITY_BLAZE_SHOOT, 1f),
    ShadowFatter(Sound.UI_STONECUTTER_TAKE_RESULT, 0.1f),
    Flame(Sound.BLOCK_FIRE_AMBIENT, 1f),
    Lava(Sound.ITEM_BUCKET_FILL_LAVA, 1f),
    Drown(Sound.ENTITY_PLAYER_HURT_DROWN, 1f),
    Ice(Sound.BLOCK_GLASS_BREAK, 1f),
    Bell(Sound.BLOCK_BELL_USE, 1f),
    Shine(Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 25),
    Angry(Sound.ENTITY_WARDEN_ANGRY, 1f),
    HeartBeat(Sound.BLOCK_CHISELED_BOOKSHELF_INSERT, 0.5f),
    PageTurn(Sound.ITEM_BOOK_PAGE_TURN, 0.8f),

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
    private final int amount;

    SomSound(Sound sound, float pitch) {
        this.sound = sound;
        this.pitch = pitch;
        amount = 1;
    }

    SomSound(Sound sound, float pitch, int amount) {
        this.sound = sound;
        this.pitch = pitch;
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
        play(playerList, location, 1f);
    }

    public void play(Collection<PlayerData> playerList, Location location, float volume) {
        for (PlayerData playerData : playerList) {
            play(playerData.getPlayer(), location, volume);
        }
    }

    public void play(Player player, Location location) {
        play(player, location, 1f);
    }

    public void play(Player player, Location location, float volume) {
        for (int i = 0; i < amount; i++) {
            player.playSound(location, sound, SoundCategory.PLAYERS, volume, pitch);
        }
    }
}
