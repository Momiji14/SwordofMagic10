package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;
import org.bukkit.Particle;

public class Condemn extends SomSkill {

    public Condemn(PlayerData playerData) {
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
            reach *= 1.5;
        }

        CustomLocation center = playerData.getLocation().frontHorizon(reach).addY(0.1);

        particle.circleFill(playerData.getViewers(), center, radius);
        particle.widthLine(playerData.getViewers(), playerData.getEyeLocation(), reach, 2, 1);
        particle2.widthLine(playerData.getViewers(), playerData.getEyeLocation(), reach, 2, 1);
        SomSound.Slash.play(playerData.getViewers(), playerData.getSoundLocation());
        for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
            Damage.makeDamage(playerData, entity, DamageEffect.Holy, DamageOrigin.MAT, damage);
            SomTask.wait(50);
        }

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("刻印の願瓶");
        if(playerData.hasBottle(bottle2)){
            double duration = bottle2.getParameter(SkillParameterType.Duration);
            double mat = bottle2.getStatus(StatusType.MAT);
            playerData.addEffect(new SomEffect("Engraving", "刻印", true, duration).setMultiply(StatusType.MAT, mat), playerData);
        }
        return null;
    }
}
