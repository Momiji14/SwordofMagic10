package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.inventory.ItemStack;

import static SwordofMagic10.Component.Function.randomDouble;

public class DrawBlade extends SomSkill {

    public SomEffect Effect = new SomEffect("DrawBlade", "抜刀状態", true, 6);
    public static final String Message = "§e抜刀状態§aではありません";

    public DrawBlade(PlayerData playerData) {
        super(playerData);
    }

    public static SomDisplayParticle Process(PlayerData playerData, SomRay ray, double damage) {
        CustomLocation targetLocation = ray.isHitEntity() ? ray.getHitEntity().getEyeLocation() : ray.getOriginPosition();
        CustomLocation location = targetLocation.clone().addXZ(randomDouble(-4, 4), randomDouble(-4, 4)).addY(randomDouble(1, 3));
        location.lookLocation(targetLocation);
        SomRay ray2 = SomRay.rayLocationEntity(playerData.getTargets(), location, 16, 1, true);
        SomDisplayParticle display = new SomItemParticle(new ItemStack(playerData.getEquipment().get(EquipSlot.MainHand).getIcon()));
        display.setLeftRotation(location);
        display.setRightRotation(90, 0, 135);
        display.setBillboard(Display.Billboard.FIXED);
        display.setScale(0, 0, 0);
        CustomTransformation flame = display.clone().setScale(3, 1.2, 1.2).setTime(5);
        display.addAnimation(flame);
        display.addAnimation(flame.setOffset(location.toLocationVector(ray2.getOriginPosition())));
        display.addAnimation(flame.setScale(0, 0, 0));
        display.spawn(playerData.getViewers(), location);
        SomParticle particle = new SomParticle(Particle.SWEEP_ATTACK, playerData);
        SomTask.delay(() -> {
            for (SomEntity hitEntity : ray2.getHitEntities()) {
                Damage.makeDamage(playerData, hitEntity, DamageEffect.None, DamageOrigin.ATK, damage);
            }
            particle.randomLocation(playerData.getViewers(), targetLocation, 0.5, 5);
            SomSound.Blade.play(playerData.getViewers(), location);
        }, 10);
        return display;
    }

    @Override
    public String active() {
        Effect.setTime(getDuration());

        int stackAmount = 1;
        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("二刀の願瓶");
        if(playerData.hasBottle(bottle)) stackAmount *= 2;

        if (playerData.hasEffect(Effect.getId())) {
            playerData.getEffect(Effect.getId()).setTime(Effect.getTime());
            playerData.addEffectStack(Effect, stackAmount);
        } else {
            playerData.addEffect(Effect, playerData);
        }
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 1, playerData.getTargets(), false);
        Process(playerData, ray, getDamage());

        return null;
    }
}
