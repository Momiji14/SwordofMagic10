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
import org.bukkit.Color;
import org.bukkit.Particle;

public class Retaliation extends SomSkill {

    public Retaliation(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Color.YELLOW, playerData);
        SomParticle particle2 = new SomParticle(Particle.FIREWORKS_SPARK, playerData);

        double reach = getReach();
        double radius = getRadius();
        double damage = getDamage();

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("裁きの願瓶");
        if(playerData.hasBottle(bottle1)){
            radius *= bottle1.getParameter(SkillParameterType.Radius);
        }

        SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), reach, true);
        CustomLocation center = ray.getOriginPosition();
        particle.line(playerData.getViewers(), center, center.clone().addY(14), radius);
        particle2.line(playerData.getViewers(), center, center.clone().addY(14), radius);
        SomSound.Shine.play(playerData.getViewers(), playerData.getSoundLocation());
        for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
            Damage.makeDamage(playerData, entity, DamageEffect.Holy, DamageOrigin.MAT, damage);
            SomTask.wait(50);
        }
        return null;
    }
}
