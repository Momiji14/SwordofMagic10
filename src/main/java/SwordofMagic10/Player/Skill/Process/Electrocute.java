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
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Electrocute extends SomSkill {
    public Electrocute(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Color.YELLOW, playerData);
        List<SomEntity> hitList = new ArrayList<>();
        double damage = getDamage();
        double count = getCount();
        double radius = getRadius();


        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("過放電の願瓶");
        if(playerData.hasBottle(bottle1)){
            count *= bottle1.getParameter(SkillParameterType.Count);
            radius *= bottle1.getParameter(SkillParameterType.Radius);
        }

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("過充電の願瓶");
        if(playerData.hasBottle(bottle2)){
            damage = bottle2.getParameter(SkillParameterType.Damage);
        }

        double finalCount = count;
        double finalDamage = damage;
        double finalRadius = radius;
        SomTask.run(()->{
            SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), getReach(), true);
            CustomLocation location = ray.getHitPosition();

            particle.line(playerData.getViewers(), playerData.getHandLocation(), location);
            SomTask.wait(50);
            for (int i = 0; i < finalCount; i++){
                for (SomEntity entity : SomEntity.nearestSomEntity(playerData.getTargets(), location, finalRadius)){
                    if (!hitList.contains(entity)){
                        SomSound.Hit.play(playerData.getViewers(), location);
                        Damage.makeDamage(playerData, entity, DamageEffect.Elect, DamageOrigin.MAT, finalDamage);
                        particle.line(playerData.getViewers(), location, entity.getEyeLocation());
                        hitList.add(entity);
                        location = entity.getEyeLocation();
                        break;
                    }
                    SomTask.wait(50);
                }
            }
        });

        return null;
    }
}
