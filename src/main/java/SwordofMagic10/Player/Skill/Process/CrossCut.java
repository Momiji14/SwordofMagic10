package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

import static SwordofMagic10.SomCore.Log;

public class CrossCut extends SomSkill {

    public CrossCut(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double reach = getReach();
        double angle = getAngle();
        double count = getCount();
        double damage = getDamage() / count;

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("斬撃の願瓶");
        if(playerData.hasBottle(bottle)){
            damage = bottle.getParameter(SkillParameterType.Damage) / count;
        }

        for (int i = 0; i < count; i++) {
            for (SomEntity entity : SomEntity.fanShapedSomEntity(playerData.getTargets(), playerData.getLocation(), reach, angle)) {
                Damage.makeDamage(playerData, entity, DamageEffect.None, DamageOrigin.ATK, damage);
            }
        }
        SomParticle particle = new SomParticle(Particle.SWEEP_ATTACK, playerData);
        SomParticle particle2 = new SomParticle(Particle.CRIT, playerData);
        particle.widthLine(playerData.getViewers(), playerData.getHipsLocation(), reach/2, 1);
        particle2.fanShaped(playerData.getViewers(), playerData.getLocation(), reach, angle);
        SomSound.Slash.play(playerData.getViewers(), playerData.getSoundLocation());

        return null;
    }
}
