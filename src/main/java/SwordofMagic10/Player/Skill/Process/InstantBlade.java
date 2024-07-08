package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.List;

public class InstantBlade extends BladeSkill {
    public InstantBlade(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.FIREWORKS_SPARK, playerData).setRandomVector().setRandomSpeed(0.25f);
        SomParticle particle2 = new SomParticle(Particle.SWEEP_ATTACK, playerData).setRandomSpeed(0.1f);

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("覚悟の願瓶");
        if(playerData.hasBottle(bottle1)){
            double duration = bottle1.getParameter(SkillParameterType.Duration);
            double atk = bottle1.getStatus(StatusType.ATK);
            playerData.addEffect(new SomEffect("Preparedness", "覚悟", true, duration).setMultiply(StatusType.ATK, atk), playerData);
        }

        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 1, playerData.getTargets(), true);
        CustomLocation from = playerData.getHipsLocation().clone();
        Location to = ray.getOriginPosition().subtract(playerData.getDirection()).setDirection(playerData.getDirection());

        List<SomEntity> list =  ray.getHitEntities();
        for (SomEntity entity : list) {
            Damage.makeDamage(playerData, entity, DamageEffect.None, DamageOrigin.ATK, getDamage());
        }


        int stackAmount = list.size();
        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("二刀の願瓶");
        if(playerData.hasBottle(bottle)) stackAmount *= 2;
        playerData.addEffectStack("DrawBlade", stackAmount);


        playerData.teleport(to, playerData.getDirection().setY(0));

        particle.line(playerData.getViewers(), from, to);
        particle2.line(playerData.getViewers(), from, to);
        SomSound.Blade.play(playerData.getViewers(), to);
        return null;
    }
}
