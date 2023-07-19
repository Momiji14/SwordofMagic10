package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class Resurrection extends SomSkill {

    public SomEffect Effect = new SomEffect(this, true).setTime(2);
    public Resurrection(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 1, playerData.getDeathAllies(), false);
        if (ray.isHitEntity()) {
            SomParticle particle = new SomParticle(Particle.END_ROD);
            particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getHitPosition());
            ray.getHitEntity().addEffect(Effect);
            SomSound.Heal.play(playerData.getViewers(), ray.getHitPosition());
        } else {
            playerData.getSkillManager().setCoolTime(this, 5);
        }
        return null;
    }
}
