package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
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

public class ArcaneEnergy extends SomSkill {

    public ArcaneEnergy(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public boolean cast() {
        SomParticle particle = new SomParticle(Color.AQUA, playerData);
        double radius = getParameter(SkillParameterType.Radius);
        particle.circlePointLine(playerData.getViewers(), playerData.getLocation(), radius, 6, 0, 0);
        particle.circlePointLine(playerData.getViewers(), playerData.getLocation(), radius/2, 6, 0, 0.5);
        return super.cast();
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true);
        SomParticle particle = new SomParticle(Color.YELLOW, playerData);

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("蓄積の願瓶");
        double percent = 0.3;

        for (SomEntity member : playerData.getMember()){
            //パーティクル
            particle.circleHeightTwin(playerData.getViewers(), member.getLivingEntity(), 1, 2, 3);
            //エフェクト
            if(playerData.hasBottle(bottle)){
                member.addMana(playerData.getMaxMana() * percent);
            }else{
                member.addEffect(effect.clone().setDoubleData(0, member.getMana()), playerData);;
            }
            //サウンド
            SomSound.Heal.play(member);
        }

        return null;
    }
}
