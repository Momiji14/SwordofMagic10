package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

import static SwordofMagic10.Player.Skill.Process.BulletBit.headShot;

public class FreezeBullet extends SomSkill {

    public FreezeBullet(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void castFirstTick() {
        SomSound.Tick.play(playerData.getViewers(), playerData.getSoundLocation());
    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.2, playerData.getTargets(), false);
        if (ray.isHitEntity()) {
            Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.Ice, DamageOrigin.ATK, headShot(playerData, ray, getDamage(), getHeadDamage()));
            ray.getHitEntity().addEffect(SomEffect.List.Freeze.getEffect().setTime(getDuration()), playerData);
        }
        SomParticle particle = new SomParticle(Particle.SNOWFLAKE, playerData);
        particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
        SomSound.Handgun.play(playerData.getViewers(), playerData.getSoundLocation());

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("冷静の願瓶");
        if(playerData.hasBottle(bottle)){
            double duration = bottle.getParameter(SkillParameterType.Duration);
            double atk = bottle.getStatus(StatusType.ATK);
            playerData.addEffect(new SomEffect("Cool", "冷静", true, duration).setMultiply(StatusType.ATK, atk), playerData);
        }

        return null;
    }
}
