package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class Revive extends SomSkill {
    public Revive(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true).setRank(SomEffect.Rank.High);
        SomParticle particle = new SomParticle(Color.AQUA, playerData);

        for (SomEntity member : playerData.getMember()){
            //パーティクル
            particle.circleHeightTwin(playerData.getViewers(), member.getLivingEntity(), 1, 2, 3);
            //エフェクト
            member.addEffect(effect, playerData);
            //サウンド
            SomSound.Heal.play(member);
        }

        //レジェンドレイド中なら
        if (playerData.getDungeonMenu().isInDungeon() && playerData.getDungeonMenu().getDungeon().isLegendRaid()){
            playerData.getSkillManager().setCoolTime(this, 12000);
        }
        return null;
    }
}
