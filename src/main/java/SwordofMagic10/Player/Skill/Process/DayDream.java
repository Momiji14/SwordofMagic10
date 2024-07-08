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
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

import static SwordofMagic10.Player.Skill.Process.BulletBit.headShot;

public class DayDream extends SomSkill {

    public DayDream(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void castFirstTick() {
        SomSound.Tick.play(playerData.getViewers(), playerData.getSoundLocation());
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.SMOKE_LARGE, playerData);
        SomEffect effect = new SomEffect(this, false);

        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.1, playerData.getTargets(), false);

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("白昼夢の願瓶");
        if(playerData.hasBottle(bottle)){
            CustomLocation center = ray.getOriginPosition();
            for (SomEntity victim : SomEntity.nearSomEntity(playerData.getTargets(), center, getReach())){
                victim.addEffect(effect, playerData);
            }
            particle.circle(playerData.getViewers(), center, getReach());
        }else{
            if (ray.isHitEntity()) {
                ray.getHitEntity().addEffect(effect, playerData);
            }
        }

        particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
        SomSound.Handgun.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
