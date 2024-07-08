package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.DurationSkill;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

import static SwordofMagic10.SomCore.Log;

public class TreNapalm extends SomSkill {

    public TreNapalm(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public boolean cast(){
        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("投擲の願瓶");
        return super.cast() && (!playerData.hasBottle(bottle) || getCurrentStack() == getStack());
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.FLAME, playerData).setRandomVector().setRandomSpeed(0.25f);
        SomParticle particle2 = new SomParticle(Particle.LAVA, playerData).setRandomVector().setRandomSpeed(1f);
        SomParticle particle3 = new SomParticle(Particle.CRIT, playerData);

        double reach = getReach();
        double radius = getRadius();
        double damage = getDamage() / getCount();
        int waitTick = getDurationTick()/getCount();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("投擲の願瓶");
        if(playerData.hasBottle(bottle)){
            reach *= bottle.getParameter(SkillParameterType.Reach);
            radius *= bottle.getParameter(SkillParameterType.Radius);
            damage *= getStack();

            playerData.getSkillManager().getSkill("TreNapalm").addCurrentStack(-getStack());
        }

        SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), reach, true);
        CustomLocation center = ray.getHitPosition().lower();

        particle.line(playerData.getViewers(), playerData.getHandLocation(), center, 0, 2);
        particle3.line(playerData.getViewers(), playerData.getHandLocation(), center, 0);
        particle.setTime(waitTick*50);
        particle2.setTime(waitTick*50);
        SomSound.Handgun.play(playerData.getViewers(), playerData.getSoundLocation());
        DurationSkill.MagicCircle magicCircle = DurationSkill.magicCircle(playerData, center, radius, waitTick, getDurationTick());
        final double finalRadius = radius;
        double finalDamage = damage;

        magicCircle.setRunnable(() -> {
            SomTask.run(() -> particle.circleFill(playerData.getViewers(), center, finalRadius, 3));
            SomTask.run(() -> particle2.circleFill(playerData.getViewers(), center, finalRadius, 3));
            SomSound.Flame.play(playerData.getViewers(), center);
            for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), center, finalRadius)) {
                Damage.makeDamage(playerData, entity, DamageEffect.Fire, DamageOrigin.ATK, finalDamage);
                SomTask.wait(50);
            }
        });
        magicCircle.run();

        return null;
    }
}
