package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.inventory.ItemStack;

import static SwordofMagic10.Component.Function.randomDouble;

public class DrawBlade extends SomSkill {

    public SomEffect Effect = new SomEffect("DrawBlade", "抜刀状態", true, 30);
    public static final String Message = "§e抜刀状態§aではありません";

    public DrawBlade(PlayerData playerData) {
        super(playerData);
    }

    public static SomDisplayParticle Process(PlayerData playerData, SomEntity target, SomRay ray, double damage) {
        CustomLocation location = playerData.getEyeLocation().frontHorizon(5).right(randomDouble(-4, 4)).addY(randomDouble(1, 3));
        location.lookLocation(ray.getHitPosition());
        SomDisplayParticle display = new SomItemParticle(new ItemStack(playerData.getEquipment().get(EquipSlot.MainHand).getIcon()));
        display.setLeftRotation(location);
        display.setRightRotation(90, 0, 135);
        display.setBillboard(Display.Billboard.FIXED);
        display.setScale(0, 0, 0);
        CustomTransformation flame = display.clone().setScale(3, 1.2, 1.2).setTime(5);
        display.addAnimation(flame);
        display.addAnimation(flame.setOffset(location.toLocationVector(SomRay.rayLocationBlock(location, 16, true).getHitPosition())));
        display.addAnimation(flame.setScale(0, 0, 0));
        display.spawn(playerData.getViewers(), location);
        SomParticle particle = new SomParticle(Particle.SWEEP_ATTACK);
        SomTask.delay(() -> {
            if (target != null) Damage.makeDamage(playerData, target, DamageEffect.None, DamageOrigin.ATK, damage);
            particle.randomLocation(playerData.getViewers(), ray.getHitPosition(), 0.5, 5);
            SomSound.Blade.play(playerData.getViewers(), location);
        }, 10);
        return display;
    }

    @Override
    public String active() {
        Effect.setTime(getDuration());
        if (playerData.hasEffect(Effect.getId())) {
            playerData.addEffectStack(Effect, 1);
        } else {
            playerData.addEffect(Effect);
        }
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 1, playerData.getTargets(), false);
        Process(playerData, ray.getHitEntity(), ray, getDamage());

        return null;
    }
}
