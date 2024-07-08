package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.DurationSkill;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class LocateInstruct extends SomSkill {
    public LocateInstruct(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true).setTime(1);
        SomParticle particle = new SomParticle(Color.ORANGE, playerData);
        double radius = getRadius();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("待機の願瓶");
        if(playerData.hasBottle(bottle)){
            for (SomEntity entity : playerData.getMember()){
                CustomLocation center = entity.getLocation();

                DurationSkill.MagicCircle magicCircle = DurationSkill.magicCircle(playerData, center, radius, 10, getDurationTick());
                magicCircle.setRunnable(() -> {
                    SomTask.run(() -> particle.circle(playerData.getViewers(), center, radius));

                    for (SomEntity target : SomEntity.nearPlayer(playerData.getAllies(), center, radius)) {
                        target.addEffect(effect, playerData);
                        SomTask.wait(50);
                    }
                });
                magicCircle.setRunnableEnd(effect::delete);
                magicCircle.run();
            }
        }else{
            SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), getReach(), true);
            CustomLocation center = ray.getHitPosition().lower();

            DurationSkill.MagicCircle magicCircle = DurationSkill.magicCircle(playerData, center, radius, 10, getDurationTick());
            magicCircle.setRunnable(() -> {
                SomTask.run(() -> particle.circle(playerData.getViewers(), center, radius));

                for (SomEntity entity : SomEntity.nearPlayer(playerData.getAllies(), center, radius)) {
                    entity.addEffect(effect, playerData);
                    SomTask.wait(50);
                }
            });
            magicCircle.setRunnableEnd(effect::delete);
            magicCircle.run();
        }
        return null;
    }
}
