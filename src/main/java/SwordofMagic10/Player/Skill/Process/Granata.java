package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class Granata extends SomSkill {

    public Granata(PlayerData playerData) {
        super(playerData);

    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.CRIT, playerData);
        SomParticle particle2 = new SomParticle(Particle.LAVA, playerData);
        double radius = getRadius();
        double damage = getDamage();

        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.5, playerData.getTargets(), false);
        CustomLocation center = ray.getOriginPosition();

        particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
        particle2.sphere(playerData.getViewers(), ray.getOriginPosition(), radius);
        SomSound.Handgun.play(playerData.getViewers(), playerData.getSoundLocation());
        for (SomEntity victim : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
            Damage.makeDamage(playerData, victim, DamageEffect.Fire, DamageOrigin.ATK, damage);
            SomTask.wait(50);
        }

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("高揚の願瓶");
        if(playerData.hasBottle(bottle)){
            double duration = bottle.getParameter(SkillParameterType.Duration);
            double atk = bottle.getStatus(StatusType.ATK);
            playerData.addEffect(new SomEffect("Excited", "高揚", true, duration).setMultiply(StatusType.ATK, atk), playerData);
        }

        return null;
    }
}
