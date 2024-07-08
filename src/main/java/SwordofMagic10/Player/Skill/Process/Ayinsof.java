package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

import java.util.ArrayList;

public class Ayinsof extends SomSkill {
    public Ayinsof(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true).setFixed(StatusType.HealthRegen, playerData.getHealthRegen()*2);
        SomParticle particle = new SomParticle(Color.LIME, playerData);

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("無限の願瓶");
        if(playerData.hasBottle(bottle)){
            double duration = getDuration() * bottle.getParameter(SkillParameterType.Duration);
            double mhp = getMaxHealth() * bottle.getMaxHealth();
            double hpr = playerData.getHealthRegen() * 20;
            effect.setTime(duration).setMultiply(StatusType.MaxHealth, mhp).setFixed(StatusType.HealthRegen, hpr);

            SomRay ray = SomRay.rayLocationEntity(playerData, 50, 2, new ArrayList<>(playerData.getMemberNoDeathNoMe()), true);

            SomEntity target;
            if (ray.isHitEntity()) target = ray.getHitEntity();
            else target = playerData;

            target.addEffect(effect, playerData);
            MobiusParticle(particle, target.getLocation(), 2);
            SomSound.Heal.play(playerData.getViewers(), target.getSoundLocation());
        }else{
            for (SomEntity member : playerData.getMember()){
                //パーティクル
                MobiusParticle(particle, member.getLocation(), 2);
                //エフェクト
                member.addEffect(effect, playerData);
                //サウンド
                SomSound.Heal.play(member);
            }
        }

        return null;
    }

    public void MobiusParticle(SomParticle particle, CustomLocation location, double radius){
        double pos = radius / 2;
        particle.circle(playerData.getViewers(), location.clone().addXZ(pos, 0), pos);
        particle.circle(playerData.getViewers(), location.clone().addXZ(-pos, 0), pos);
        particle.circle(playerData.getViewers(), location.clone().addXZ(0, pos), pos);
        particle.circle(playerData.getViewers(), location.clone().addXZ(0, -pos), pos);
    }
}
