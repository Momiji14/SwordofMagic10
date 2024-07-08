package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.DurationSkill;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class Nachash extends SomSkill {
    public Nachash(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect("Nachash", "ナハシ", true, 1).setOwner(playerData);
        SomParticle particle = new SomParticle(Color.GREEN, playerData);
        double radius = getRadius();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("循環の願瓶");
        if(playerData.hasBottle(bottle)){
            effect.setTime(getDuration());
            for (SomEntity member : playerData.getMember()){
                member.addEffect(effect, playerData);
                particle.circle(playerData.getViewers(), member.getLocation(), 1);
            }
        }else{
            SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), getReach(), true);
            CustomLocation center = ray.getHitPosition().lower();

            DurationSkill.MagicCircle magicCircle = DurationSkill.magicCircle(playerData, center, getRadius(), 5, getDurationTick());
            magicCircle.setRunnable(() -> {
                SomTask.run(() -> {
                    particle.circle(playerData.getViewers(), center, radius);
                    MobiusParticle(particle, center, radius);
                });
                for (SomEntity entity : SomEntity.nearPlayer(playerData.getAllies(), center, radius)) {
                    entity.addEffect(effect, playerData);
                    SomTask.wait(50);
                }
            });
            magicCircle.run();
        }


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
