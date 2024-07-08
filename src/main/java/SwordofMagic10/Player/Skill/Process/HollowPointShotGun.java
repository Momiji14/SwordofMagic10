package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

import static SwordofMagic10.Player.Skill.Process.BulletBit.headShot;

public class HollowPointShotGun extends SomSkill {

    public HollowPointShotGun(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void castFirstTick() {
        SomSound.Tick.play(playerData.getViewers(), playerData.getSoundLocation());
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.CRIT, playerData);
        SomParticle particle2 = new SomParticle(Particle.FLAME, playerData);
        SomParticle particle3 = new SomParticle(Particle.LAVA, playerData);

        SomRay ray;
        double width;
        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("貫通の願瓶");
        if(playerData.hasBottle(bottle)){
            SomEffect effect = new SomEffect("Fatal","致命傷", false, bottle.getParameter(SkillParameterType.Duration)).setOwner(playerData).setDoubleData(0, bottle.getStatus(StatusType.CriticalDamage));

            width = bottle.getParameter(SkillParameterType.Radius);
            ray = SomRay.rayLocationEntity(playerData, getReach(), width, playerData.getTargets(), true);

            if (ray.isHitEntity()) {
                for (SomEntity entity : ray.getHitEntities()){
                    Damage.makeDamage(playerData, entity, DamageEffect.Fire, DamageOrigin.ATK, getDamage());
                    entity.addEffect(effect, playerData);
                }
            }
        }else{
            width = 0.2;
            ray = SomRay.rayLocationEntity(playerData, getReach(), width, playerData.getTargets(), false);

            if (ray.isHitEntity()) {
                for (SomEntity entity : ray.getHitEntities()){
                    Damage.makeDamage(playerData, entity, DamageEffect.Fire, DamageOrigin.ATK, getDamage());
                }
            }
        }

        particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition(), width*2);
        particle2.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition(), width*2);
        particle3.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition(), width*2);
        SomSound.Handgun.play(playerData.getViewers(), playerData.getSoundLocation());

        return null;
    }
}
