package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DurationSkill;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import SwordofMagic10.SomCore;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import static SwordofMagic10.SomCore.Log;

public class Indulgentia extends SomSkill {

    public Indulgentia(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public boolean cast() {
        SomParticle particle = new SomParticle(Color.GREEN, playerData);
        particle.circle(playerData.getViewers(), playerData.getLocation(), getRadius());
        return super.cast();
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true);
        int durationTick = getDurationTick();
        int count = getCount();
        double heal = getHeal() / count;

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("集中回復の願瓶");
        if(playerData.hasBottle(bottle)){
            effect.setTime(getDuration() * bottle.getParameter(SkillParameterType.Duration));
            durationTick *= bottle.getParameter(SkillParameterType.Duration);
            count *= bottle.getParameter(SkillParameterType.Duration);
        }

        SomParticle particle = new SomParticle(Particle.VILLAGER_HAPPY, playerData);
        particle.circleFill(playerData.getViewers(), playerData.getLocation(), getRadius());
        for (SomEntity ally : playerData.getAllies(getRadius())) {
            ally.addEffect(effect, playerData);
            SomSound.Heal.play(ally);
        }

        int perTick = durationTick / count;
        DurationSkill.Duration durationSkill = new DurationSkill.Duration(playerData, perTick, durationTick);
        durationSkill.setRunnable(()->{
            boolean isHit = false;
            for (SomEntity ally : playerData.getAllies()) {
                if (ally.hasEffect(effect) && ally.getEffect(effect.getId()).getOwner() == playerData) {
                    Damage.makeHeal(playerData, ally, heal);
                    isHit = true;
                }
            }
            if (!isHit) durationSkill.end();
        });
        durationSkill.run();

        return null;
    }
}
