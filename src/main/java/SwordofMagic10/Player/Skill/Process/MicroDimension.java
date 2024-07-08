package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class MicroDimension extends SomSkill {

    public MicroDimension(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.SPELL_WITCH, playerData);

        double radius = getRadius();
        double damage = getDamage();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("空間掌握の願瓶");
        if(playerData.hasBottle(bottle)){
            damage = bottle.getParameter(SkillParameterType.Damage);
            radius = bottle.getParameter(SkillParameterType.Radius);

            CustomLocation center = playerData.getLocation();

            particle.sphere(playerData.getViewers(), center, radius, 720);
            SomSound.Warp.play(playerData.getViewers(), playerData.getSoundLocation());
            for (SomEntity victim : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
                Damage.makeDamage(playerData, victim, DamageEffect.None, DamageOrigin.MAT, damage);
            }

        }else{
            SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.5, playerData.getTargets(), false);
            CustomLocation center = ray.getOriginPosition();

            particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
            particle.sphere(playerData.getViewers(), ray.getOriginPosition(), radius);
            SomSound.Warp.play(playerData.getViewers(), playerData.getSoundLocation());
            for (SomEntity victim : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
                Damage.makeDamage(playerData, victim, DamageEffect.None, DamageOrigin.MAT, damage);
            }
        }
        return null;
    }
}
