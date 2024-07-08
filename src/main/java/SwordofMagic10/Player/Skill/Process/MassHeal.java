package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;
import org.bukkit.Particle;

public class MassHeal extends SomSkill {

    public MassHeal(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.VILLAGER_HAPPY, playerData);
        double heal = getHeal();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("平等の願瓶");
        if(playerData.hasBottle(bottle)){
            heal /= 2;
            playerData.getSkillManager().setCoolTime(this, getCoolTime()/2);
        }

        for (SomEntity member : playerData.getMember()){
            //パーティクル
            particle.circleHeightTwin(playerData.getViewers(), member.getLivingEntity(), 1, 2, 3);
            //エフェクト
            Damage.makeHeal(playerData, member, heal);
            //サウンド
            SomSound.Heal.play(member);
        }

        return null;
    }
}
