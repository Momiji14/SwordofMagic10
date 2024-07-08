package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.DataBase.SkillDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;

public class ContinueBlade extends BladeSkill {
    public ContinueBlade(PlayerData playerData) {
        super(playerData);
        effect = new SomEffect.Toggle("納刀", "納刀", true);
        effect.setStun(true);
        bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("納刀の願瓶");
    }

    private final SomEffect effect;
    private final SomAmulet.Bottle bottle;

    @Override
    public String active() {
        double damage = getDamage() * (1 + (1 - 1.0 / Math.min(50, playerData.getEffect("DrawBlade").getStack())));
        if (playerData.hasBottle(bottle)) {
            playerData.addEffect(effect, playerData);
            CustomLocation lastLocation = playerData.getLocation();
            SomParticle particle = new SomParticle(Particle.SWEEP_ATTACK, playerData);
            SomParticle particle2 = new SomParticle(Particle.FIREWORKS_SPARK, playerData);
            List<SomEntity> entityList = new ArrayList<>(SomEntity.nearSomEntity(playerData.getTargets(), lastLocation, bottle.getParameter(SkillParameterType.Radius)));
            for (SomEntity target : entityList) {
                target.addEffect(effect, playerData);
            }
            for (SomEntity target : entityList) {
                particle.line(playerData.getViewers(), lastLocation, target.getHipsLocation(), 2, 5);
                lastLocation = target.getHipsLocation();
                SomSound.Slash.playRadius(lastLocation, playerData.getViewers());
                SomTask.wait(50);
            }
            SomTask.wait(500);

            for (SomEntity target : entityList) {
                particle2.line(playerData.getViewers(), lastLocation, target.getHipsLocation(), 0.3, 3);
                lastLocation = target.getHipsLocation();
                Damage.makeDamage(playerData, target, DamageEffect.None, DamageOrigin.ATK, damage);
            }
            SomSound.Blade.playRadius(lastLocation, playerData.getViewers());
            playerData.removeEffect(effect);
            //playerData.addEffect(new SomEffect(getId(), getDisplay() + "再開", true, 3), playerData);
            playerData.getSkillManager().setCoolTime(playerData.getSkillManager().getSkill("DrawBlade"), 1);
            SomTask.delay(() -> {
                for (SomEntity target : entityList) {
                    target.removeEffect(effect);
                }
            }, 50);
        } else {
            damage /= getCount();
            for (int i = 0; i < getParameter(SkillParameterType.Count); i++) {
                SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 1, playerData.getTargets(), false);
                DrawBlade.Process(playerData, ray, damage);
                SomTask.wait(100);
            }
        }
        return null;
    }
}
