package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

import static SwordofMagic10.Entity.DurationSkill.SkillTick;

public class IcePike extends SomSkill {

    public IcePike(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double radius = getRadius();
        double count = getCount();
        double damage = getDamage()/count;
        double wait = getDurationMillie()/count;

        SomParticle particle1 = new SomParticle(Color.AQUA, playerData).setVectorUp().setRandomSpeed(1f);
        particle1.setTime(SkillTick);
        SomEffect effect = SomEffect.List.Slow.getEffect().setTime(1);

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("凍結の願瓶");
        if(playerData.hasBottle(bottle1)){
            effect.setRank(SomEffect.Rank.High);
        }

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("過冷却の願瓶");

        SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), getReach(), true);
        CustomLocation center = ray.getHitPosition().lower();

        SomTask.run(() -> {
            for (int i = 0; i < count; i++) {
                SomTask.run(() -> particle1.circleFill(playerData.getViewers(), center, radius));
                SomSound.Ice.play(playerData.getViewers(), center);
                SomTask.run(() -> {
                    for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
                        entity.addEffect(effect, playerData);
                        if (entity.hasEffect("Freeze") && playerData.hasBottle(bottle2)){
                            Damage.makeDamage(playerData, entity, DamageEffect.Ice, DamageOrigin.MAT, bottle2.getParameter(SkillParameterType.Damage)/count);
                        }else{
                            Damage.makeDamage(playerData, entity, DamageEffect.Ice, DamageOrigin.MAT, damage);
                        }
                        SomTask.wait(50);
                    }
                });
                SomTask.wait(wait);
            }
        });
        return null;
    }
}
