package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.DurationSkill;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class WoodenGoddess extends SomSkill {
    public WoodenGoddess(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void castFirstTick(){
        int perTick = 7;
        DurationSkill.Duration durationSkill = new DurationSkill.Duration(playerData, perTick, getCastTime());
        durationSkill.setRunnable(()-> SomSound.Wood.play(playerData.getViewers(), playerData.getLocation()));
        durationSkill.run();
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true);
        SomParticle particle = new SomParticle(Color.WHITE, playerData).setRandomVector().setRandomSpeed(0.01f);
        double radius = getRadius();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("精巧の願瓶");
        if(playerData.hasBottle(bottle)) radius *= bottle.getParameter(SkillParameterType.Radius);

        double finalRadius = radius;
        int perTick = 10;

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("負目の願瓶");
        if(playerData.hasBottle(bottle2)){
            for (DurationSkill.MagicCircle magicCircle : ((Symbol) playerData.getSkillManager().getSkill("Symbol")).getSymbols()){
                CustomLocation locations = magicCircle.getLocation();

                DurationSkill.Duration durationSkill = new DurationSkill.Duration(playerData, perTick, getDurationTick());
                durationSkill.setRunnable(()->{
                    particle.circle(playerData.getViewers(), locations, finalRadius);
                    for(SomEntity entity : SomEntity.nearPlayer(playerData.getAllies(), locations, finalRadius)){
                        entity.addEffect(effect, playerData);
                    }
                });
                durationSkill.run();
            }
        }

        CustomLocation location = playerData.getLocation();

        DurationSkill.Duration durationSkill = new DurationSkill.Duration(playerData, perTick, getDurationTick());
        durationSkill.setRunnable(()->{
            particle.circle(playerData.getViewers(), location, finalRadius);
            for(SomEntity entity : SomEntity.nearPlayer(playerData.getAllies(), location, finalRadius)){
                entity.addEffect(effect, playerData);
            }
        });
        durationSkill.run();


        return null;
    }
}
