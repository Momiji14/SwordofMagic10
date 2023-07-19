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

    private boolean trigger = false;
    @Override
    public boolean cast() {
        if (!trigger) {
            trigger = true;
            SomSound.Tick.play(playerData.getViewers(), playerData.getSoundLocation());
        }
        return super.cast();
    }

    @Override
    public String active() {
        double count = getCount();
        double damage = getDamage() / count;
        double headDamage = getHeadDamage() / count;
        SomTask.run(() -> {
            for (int i = 0; i < count; i++) {
                SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.1, playerData.getTargets(), false);
                BulletBit.Process(playerData, ray.getHitEntity(), ray, damage, headDamage, new SomParticle(Particle.CRIT));
                SomTask.wait(100);
            }
        });
        trigger = false;
        return null;
    }
}
