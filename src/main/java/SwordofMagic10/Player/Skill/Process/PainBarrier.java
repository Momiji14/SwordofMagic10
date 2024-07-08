package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import SwordofMagic10.SomCore;
import org.bukkit.Color;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Predicate;

public class PainBarrier extends SomSkill {

    public PainBarrier(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        playerData.addEffect(SomEffect.List.Invincible.getEffect(), playerData);
        SomEffect effect = new SomEffect(this, true);
        playerData.addEffect(effect, playerData);
        SomParticle particle = new SomParticle(Color.MAROON, playerData).setRandomVector().setSpeed(0.15f).setAmount(10);
        SomTask.timerPlayer(playerData, () -> particle.spawn(playerData.getViewers(), playerData.getHipsLocation()), playerData -> playerData.hasEffect(effect), 5);
        SomSound.Heal.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
