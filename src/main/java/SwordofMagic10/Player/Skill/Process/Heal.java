package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.HashMap;

import static SwordofMagic10.SomCore.Log;

public class Heal extends SomSkill {

    public Heal(PlayerData playerData) {
        super(playerData);
    }

    HashMap<SomEntity, DurationSkill.Duration> recoveryList = new HashMap<>();

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.VILLAGER_HAPPY, playerData);
        double heal = getHeal();

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("瞬間回復の願瓶");
        if(playerData.hasBottle(bottle1)){
            heal *= bottle1.getParameter(SkillParameterType.Heal);
        }

        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 2, new ArrayList<>(playerData.getMemberNoDeathNoMe()), true);

        SomEntity target;
        if (ray.isHitEntity()) {
            target = ray.getHitEntity();
            for (SomEntity entity : ray.getHitEntities()){
                if (entity.getHealthPercent() < target.getHealthPercent()){
                    target = entity;
                }
            }
        } else {
            target = playerData;
        }

        if(playerData.hasEffect("LoyaltyForenos") && target.hasEffect("LoyaltyPhiema")){
            playerData.death("§cグリフィアの記憶を刺激しました");
            ((PlayerData) target).death("§cグリフィアの記憶を刺激しました");
        }

        Damage.makeHeal(playerData, target, heal);

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("持続回復の願瓶");
        if(playerData.hasBottle(bottle2)){
            final SomEffect effect = new SomEffect("Recovery", "持続回復", true, bottle2.getParameter(SkillParameterType.Duration)).setOwner(playerData);
            double duration =  bottle2.getParameter(SkillParameterType.Duration);
            double count =  bottle2.getParameter(SkillParameterType.Count);

            int durationTick = (int) (duration * 20);
            int perTick = (int) (durationTick / count);
            double perHeal = heal / count;;
            SomEntity finalTarget = target;

            DurationSkill.Duration durationSkill = new DurationSkill.Duration(playerData, perTick, durationTick);
            if (recoveryList.containsKey(finalTarget)) recoveryList.remove(finalTarget).end();
            recoveryList.put(finalTarget, durationSkill);
            durationSkill.setRunnable(()->{
                boolean isHit = false;
                if (finalTarget.hasEffect(effect) && finalTarget.getEffect(effect.getId()).getOwner() == playerData) {
                    Damage.makeHeal(playerData, finalTarget, perHeal);
                    isHit = true;
                }
                if (!isHit) durationSkill.end();
            });
            durationSkill.setRunnableEnd(()-> recoveryList.remove(finalTarget));
            durationSkill.run();

            playerData.addEffect(effect, playerData);
        }

        if (target != playerData) particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getHitPosition());
        particle.circleHeightTwin(playerData.getViewers(), target.getLivingEntity(), 1, 2, 3);
        SomSound.Heal.play(playerData.getViewers(), target.getSoundLocation());
        return null;
    }
}
