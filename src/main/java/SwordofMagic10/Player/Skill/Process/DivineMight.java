package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class DivineMight extends SomSkill {

    public DivineMight(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Color.PURPLE, playerData);

        for (SomEntity member : playerData.getMember()){
            //パーティクル
            particle.circleHeightTwin(playerData.getViewers(), member.getLivingEntity(), 1, 2, 3);
            //エフェクト
            for (SomEffect effect : member.getEffect().values()) {
                if (effect.isBuff() && !effect.isExtend() && effect.getRank() != SomEffect.Rank.Impossible) {
                    effect.addTime((int) (Math.max(getParameter(SkillParameterType.DivineMightMin), effect.getTime() * getParameter(SkillParameterType.DivineMight))));
                    effect.setExtend(true);
                }
            }
            //サウンド
            SomSound.Heal.play(member);
        }

        return null;
    }
}
