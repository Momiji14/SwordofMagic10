package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;
import org.bukkit.Sound;

import static SwordofMagic10.Player.Skill.Process.BulletBit.headShot;

public class PeaceMaker extends SomSkill {

    public PeaceMaker(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void castFirstTick() {
        SomSound.Tick.play(playerData.getViewers(), playerData.getSoundLocation());
        SomSound.PeaceMaker.play(playerData.getViewers(), playerData.getSoundLocation());
    }

    @Override
    public boolean cast() {
        SomParticle particle = new SomParticle(Particle.SPELL_WITCH, playerData).setRandomVector().setRandomSpeed(0.1f);
        particle.spawn(playerData.getViewers(), playerData.getHandLocation());
        return super.cast();
    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.1, playerData.getTargets(), false);

        double damage = getDamage();
        double headDamage = getHeadDamage();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("正義の願瓶");
        if(playerData.hasBottle(bottle)){
            damage = bottle.getParameter(SkillParameterType.Damage);
            headDamage = bottle.getParameter(SkillParameterType.HeadDamage);
        }

        if (ray.isHitEntity()) {
            SomEntity entity = ray.getHitEntity();
            Damage.makeDamage(playerData, entity, DamageEffect.None, DamageOrigin.ATK, headShot(playerData, ray, damage, headDamage));
            SomParticle particle = new SomParticle(Particle.SPELL_WITCH, playerData).setRandomVector().setRandomSpeed(0.5f);
            SomParticle particle2 = new SomParticle(Particle.FIREWORKS_SPARK, playerData).setRandomVector().setRandomSpeed(0.5f);
            SomParticle particle3 = new SomParticle(Particle.LAVA, playerData).setRandomVector().setRandomSpeed(0.5f);
            SomParticle particle4 = new SomParticle(Particle.EXPLOSION_NORMAL, playerData).setRandomVector().setRandomSpeed(0.5f);
            if (ray.isHeadShot(entity)) {
                particle.randomLocation(playerData.getViewers(), entity.getHipsLocation(), 1, 50);
                particle2.randomLocation(playerData.getViewers(), entity.getHipsLocation(), 1, 50);
                particle3.randomLocation(playerData.getViewers(), entity.getHipsLocation(), 1, 50);
                particle4.randomLocation(playerData.getViewers(), entity.getHipsLocation(), 1, 50);
                SomSound.Explode.play(playerData.getViewers(), playerData.getSoundLocation());
            }
            SomSound.Curse.play(playerData.getViewers(), playerData.getSoundLocation());
        }

        SomParticle particle = new SomParticle(Particle.CRIT, playerData);
        particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
        SomSound.Handgun.play(playerData.getViewers(), playerData.getSoundLocation());

        return null;
    }
}
