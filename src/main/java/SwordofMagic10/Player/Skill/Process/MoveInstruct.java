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

public class MoveInstruct extends SomSkill {
    public MoveInstruct(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true);
        SomParticle particle = new SomParticle(Color.BLUE, playerData);

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("撤収の願瓶");
        if(playerData.hasBottle(bottle1)){
            effect.setMultiply(StatusType.Movement, bottle1.getStatus(StatusType.Movement));
        }

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("退却の願瓶");
        if(playerData.hasBottle(bottle2)){
            double duration = getDuration() * bottle2.getParameter(SkillParameterType.Duration);
            effect.setTime(duration);
        }

        for (SomEntity member : playerData.getMember()){
            //パーティクル
            particle.circle(playerData.getViewers(), member.getLocation(), 0.5);
            //エフェクト
            member.addEffect(effect, playerData);
            //サウンド
            SomSound.Heal.play(member);
        }

        return null;
    }
}
