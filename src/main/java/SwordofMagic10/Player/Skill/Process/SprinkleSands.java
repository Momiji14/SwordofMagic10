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

public class SprinkleSands extends SomSkill {
    public SprinkleSands(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.CRIT, playerData);
        SomEffect effect = new SomEffect(this, false).setSilence("All").setMultiply(StatusType.Movement, -0.9);
        double reach = getReach();
        double angle = getAngle();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("当たり所の願瓶");

        particle.fanShaped(playerData.getViewers(), playerData.getHandLocation(), reach, angle);
        SomSound.Web.play(playerData.getViewers(), playerData.getSoundLocation());
        for (SomEntity entity : SomEntity.fanShapedSomEntity(playerData.getTargets(), playerData.getLocation(), reach, angle)) {
            entity.addEffect(effect, playerData);
            if(playerData.hasBottle(bottle)){
                double damage = bottle.getParameter(SkillParameterType.Damage);
                Damage.makeDamage(playerData, entity, DamageEffect.None, DamageOrigin.ATK, damage);
            }
        }

        return null;
    }
}
