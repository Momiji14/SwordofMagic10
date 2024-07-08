package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.inventory.ItemStack;

import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Component.Function.randomDoubleSign;

public class BulletBit extends SomSkill {

    public static double headShot(SomEntity shooter, SomRay ray, double damage, double headDamage) {
        return headShot(shooter, ray, ray.getHitEntity(), damage, headDamage);
    }

    public static double headShot(SomEntity shooter, SomRay ray, SomEntity entity, double damage, double headDamage) {
        if (ray.isHeadShot(entity) || shooter.hasEffect("WeakPoint")) {
            damage += headDamage;
            SomSound.HeadShot.play(shooter);
        }
        return damage;
    }

    public static SomDisplayParticle Process(PlayerData playerData, SomEntity target, SomRay ray, double damage, double headDamage, SomParticle... particle) {
        CustomLocation location = playerData.getEyeLocation().frontHorizon(5).right(randomDoubleSign(1.5, 4)).addY(randomDouble(1, 3));
        location.lookLocation(ray.getHitPosition());
        SomDisplayParticle display = new SomItemParticle(new ItemStack(playerData.getEquipment().get(EquipSlot.MainHand).getIcon()));
        display.setLeftRotation(location);
        display.setRightRotation(-90, 0, 0);
        display.setBillboard(Display.Billboard.FIXED);
        display.setScale(0, 0, 0);
        display.addAnimation(display.clone().setScale(5, 2, 2).setTime(3));
        display.addAnimation(new AnimationDelay(10));
        display.addAnimation(display.clone().setScale(0, 0, 0).setTime(3));
        display.spawn(playerData.getViewers(), location);
        SomTask.delay(() -> {
            for (SomParticle somParticle : particle) {
                somParticle.line(playerData.getViewers(), location, ray.getHitPosition(), 0);
            }
            if (target != null) Damage.makeDamage(playerData, target, DamageEffect.None, DamageOrigin.ATK, headShot(playerData, ray, damage, headDamage));
            SomSound.Handgun.play(playerData.getViewers(), location);
        }, 5);
        return display;
    }

    public BulletBit(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.1, playerData.getTargets(), false);
        Process(playerData, ray.getHitEntity(), ray, getDamage(), getHeadDamage(), new SomParticle(Particle.CRIT, playerData));
        return null;
    }
}
