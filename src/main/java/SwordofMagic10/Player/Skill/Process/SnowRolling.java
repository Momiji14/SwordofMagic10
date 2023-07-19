package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import SwordofMagic10.SomCore;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class SnowRolling extends SomSkill {

    public SnowRolling(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double radius = getRadius();
        double duration = getDuration();
        double damage = getDamage()/getCount();
        long tick = getDurationTick()/getCount();
        SomParticle particle = new SomParticle(Particle.END_ROD);
        new BukkitRunnable() {
            int timer = 0;
            @Override
            public void run() {
                if (timer < duration*20) {
                    for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), playerData.getEyeLocation(), radius)) {
                        entity.setVelocity(entity.getLocation().toLocationVector(playerData.getEyeLocation()));
                        entity.addEffect(SomEffect.List.Freeze.getEffect());
                        Damage.makeDamage(playerData, entity, DamageEffect.Ice, DamageOrigin.MAT, damage);
                    }
                    particle.randomLocation(playerData.getViewers(), playerData.getHipsLocation(), 2, 5);
                    timer += tick;
                } else this.cancel();
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), 0, tick);
        return null;
    }
}
