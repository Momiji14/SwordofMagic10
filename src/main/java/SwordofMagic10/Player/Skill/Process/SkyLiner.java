package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
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

public class SkyLiner extends SomSkill {
    public SkyLiner(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.CRIT, playerData);
        SomParticle particle2 = new SomParticle(Particle.SWEEP_ATTACK, playerData);

        double reach = getReach();
        double angle = getAngle();
        double count = getCount();
        double damage = getDamage() / count;
        double wait = getDurationMillie()/count;

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("旋風の願瓶");
        if(playerData.hasBottle(bottle)){
            damage = bottle.getParameter(SkillParameterType.Damage) / count;
            double finalDamage = damage;
            SomTask.run(()->{
                for (int i = 0; i < count; i++) {
                    for (SomEntity entity : SomEntity.fanShapedSomEntity(playerData.getTargets(), playerData.getLocation(), reach, angle)) {
                        Damage.makeDamage(playerData, entity, DamageEffect.None, DamageOrigin.ATK, finalDamage);
                    }
                    particle.fanShaped(playerData.getViewers(), playerData.getHandLocation(), reach, angle);
                    particle2.widthLine(playerData.getViewers(), playerData.getHipsLocation(), reach/2, 3);
                    SomSound.Slash.play(playerData.getViewers(), playerData.getSoundLocation());
                    SomTask.wait(wait);
                }
            });
        }else{
            for (int i = 0; i < count; i++) {
                for (SomEntity entity : SomEntity.fanShapedSomEntity(playerData.getTargets(), playerData.getLocation(), reach, angle)) {
                    Damage.makeDamage(playerData, entity, DamageEffect.None, DamageOrigin.ATK, damage);
                }
                particle.fanShaped(playerData.getViewers(), playerData.getHandLocation(), reach, angle);
                particle2.widthLine(playerData.getViewers(), playerData.getHipsLocation(), reach/2, 3);
                SomSound.Slash.play(playerData.getViewers(), playerData.getSoundLocation());
                SomTask.wait(wait);
            }
        }
        return null;
    }
}
