package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.Dungeon.Instance.DefensiveBattle;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;
import org.bukkit.Particle;

import static SwordofMagic10.Player.Skill.Process.BulletBit.headShot;

public class ShadowFatter extends SomSkill {

    public ShadowFatter(PlayerData playerData) {
        super(playerData);
    }

    final SomParticle shadowParticle = new SomParticle(Color.BLACK, playerData);

    @Override
    public void castFirstTick(){
        SomSound.ShadowFatter.play(playerData.getViewers(), playerData.getSoundLocation());
    }

    @Override
    public boolean cast() {
        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("影踏みの願瓶");
        if(playerData.hasBottle(bottle)){
            CustomLocation center = playerData.getLocation();
            shadowParticle.circle(playerData.getViewers(), center, getReach());
            for (SomEntity victim : SomEntity.nearSomEntity(playerData.getTargets(), center, getReach())){
                CustomLocation location = victim.getLocation();
                location.setPitch(-90);
                shadowParticle.line(playerData.getViewers(),  location, 3);
                location.addY(3);
                shadowParticle.sphere(playerData.getViewers(), location, 0.5);
            }
        }

        return super.cast();
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, false).setStun(true).setRank(SomEffect.Rank.High);
        SomEffect effectForDefensive = new SomEffect(this, false).setStun(true).setRank(SomEffect.Rank.High).setTime(getDuration()/2.0);

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("影踏みの願瓶");
        if(playerData.hasBottle(bottle)){
            CustomLocation center = playerData.getLocation();
            for (SomEntity victim : SomEntity.nearSomEntity(playerData.getTargets(), center, getReach())){
                victim.addEffect(effect, playerData);
//                if (!victim.hasEffect(DefensiveBattle.DefensiveEnemy)) victim.addEffect(effect, playerData);
//                else victim.addEffect(effectForDefensive, playerData);
            }
            shadowParticle.circle(playerData.getViewers(), center, getReach());
            SomSound.ShadowFatter.play(playerData.getViewers(), playerData.getSoundLocation());
        }else{
            SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.1, playerData.getTargets(), false);

            if (ray.isHitEntity()) {
                ray.getHitEntity().addEffect(effect, playerData);
            }

            shadowParticle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
            SomSound.ShadowFatter.play(playerData.getViewers(), playerData.getSoundLocation());
        }
        return null;
    }
}
