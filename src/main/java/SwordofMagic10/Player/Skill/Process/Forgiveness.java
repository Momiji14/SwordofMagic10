package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

import java.util.ArrayList;

public class Forgiveness extends SomSkill {

    public Forgiveness(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.SPELL_WITCH, playerData);
        SomEffect effect;

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("羊皮紙の願瓶");
        if(playerData.hasBottle(bottle)){
            effect = new SomEffect("Prophecy", "プロフェシー", true, getDuration()).setRank(SomEffect.Rank.High);

            for (SomEntity member : playerData.getMember()){
                member.addEffect(effect, playerData);
                particle.circleHeightTwin(playerData.getViewers(), member.getLivingEntity(), 1, 2, 3);
                SomSound.Heal.play(playerData.getViewers(), member.getSoundLocation());
            }

            playerData.getSkillManager().setCoolTime(this, calcCoolTime(40));
        }else{
            SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.5, new ArrayList<>(playerData.getMemberNoDeathNoMe()), false);

            SomEntity target;
            if (ray.isHitEntity()) {
                target = ray.getHitEntity();
            } else {
                target = playerData;
            }

            effect = new SomEffect(this, true).setStack(3).setRank(SomEffect.Rank.High);
            target.addEffect(effect, playerData);

            if (target != playerData) particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getHitPosition());
            particle.circleHeightTwin(playerData.getViewers(), target.getLivingEntity(), 1, 2, 3);
            SomSound.Heal.play(playerData.getViewers(), target.getSoundLocation());
        }


        return null;
    }
}
