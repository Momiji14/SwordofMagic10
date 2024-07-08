package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class RestInPeace extends SomSkill {
    public RestInPeace(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void castFirstTick() {
        SomSound.Tick.play(playerData.getViewers(), playerData.getSoundLocation());
    }

    @Override
    public String active() {
        double count = getCount();
        double damage = getDamage() / count;
        double headDamage = getHeadDamage() / count;

        SomTask.run(() -> {
            SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.2, playerData.getTargets(), false);

            for (int i = 0; i < count; i++) {
                if (!ray.isHitEntity()){
                    ray = SomRay.rayLocationEntity(playerData, getReach(), 0.2, playerData.getTargets(), false);
                }
                BulletBit.Process(playerData, ray.getHitEntity(), ray, damage, headDamage, new SomParticle(Particle.CRIT, playerData));
                SomTask.wait(50);
            }
        });
        return null;
    }
}
