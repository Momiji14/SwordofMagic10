package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class Lethargy extends SomSkill {

    public Lethargy(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, false);
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.5, playerData.getTargets(), false);
        CustomLocation center = ray.getOriginPosition();
        double radius = getRadius();

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("怠惰の願瓶");

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("怠慢の願瓶");

        for (SomEntity victim : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
            victim.addEffect(effect, playerData);
            if(playerData.hasBottle(bottle1)){
                victim.addEffect(new SomEffect("Laziness", "怠惰", false, getDuration()).setMultiply(StatusType.MDF, bottle1.getMDF()-this.getMDF()), playerData);
            }
            if(playerData.hasBottle(bottle2)){
                victim.addEffect(new SomEffect("Procrastination", "怠慢", false, getDuration()).setMultiply(StatusType.DEF, bottle2.getDEF()), playerData);
            }
        }

        SomParticle particle = new SomParticle(Color.GREEN, playerData);
        particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
        particle.sphere(playerData.getViewers(), ray.getOriginPosition(), radius);
        SomSound.Fire.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
