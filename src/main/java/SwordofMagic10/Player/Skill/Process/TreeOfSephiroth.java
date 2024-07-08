package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DurationSkill;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class TreeOfSephiroth extends SomSkill {
    public TreeOfSephiroth(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true);
        SomParticle particle = new SomParticle(Color.LIME, playerData);

        SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), getReach(), true);
        CustomLocation center = ray.getHitPosition().lower();
        double radius = getRadius();
        double heal = getHeal() / getCount();

        DurationSkill.MagicCircle magicCircle = DurationSkill.magicCircle(playerData, center, radius, getDurationTick()/getCount(), getDurationTick());
        magicCircle.setRunnable(() -> {
            SomTask.run(() -> {
                particle.circle(playerData.getViewers(), center, radius);
                MobiusParticle(particle, center, radius);
            });
            for (SomEntity entity : SomEntity.nearPlayer(playerData.getAllies(), center, radius)) {
                Damage.makeHeal(playerData, entity, heal);
                SomTask.wait(50);
            }
        });
        magicCircle.run();

        return null;
    }

    public void MobiusParticle(SomParticle particle, CustomLocation location, double radius){
        double pos = getRadius() / 2;
        particle.circle(playerData.getViewers(), location.clone().addXZ(pos, 0), pos);
        particle.circle(playerData.getViewers(), location.clone().addXZ(-pos, 0), pos);
        particle.circle(playerData.getViewers(), location.clone().addXZ(0, pos), pos);
        particle.circle(playerData.getViewers(), location.clone().addXZ(0, -pos), pos);
    }
}
