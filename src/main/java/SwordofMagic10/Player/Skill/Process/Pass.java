package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
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

public class Pass extends SomSkill {
    public Pass(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effectOne = new SomEffect("YouCanNotTimeTravelOne", "タイムトラベル後遺症", false, getCoolTime()/20.0 - 1).setRank(SomEffect.Rank.Impossible);
        SomEffect effectAll = new SomEffect("YouCanNotTimeTravelAll", "タイムトラベル後遺症", false, getCoolTime()/20.0 - 1).setRank(SomEffect.Rank.Impossible);
        SomParticle particle = new SomParticle(Particle.WAX_ON, playerData).setVectorUp().setRandomSpeed(0.1f);

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("時空間の願瓶");
        if(playerData.hasBottle(bottle)){
            for (PlayerData member : playerData.getMember() ){
                particle.circle(member.getViewers(), member.getLocation(), 0.5);
                if (!member.hasEffect(effectAll)){
                    if (member.hasEffect(effectOne) && member.getEffect("YouCanNotTimeTravelOne").getUUID() == playerData.getUUID()) continue;
                    member.getSkillManager().getCoolTime().forEach((skill, coolTime)->{
                        member.getSkillManager().setCoolTime(skill, (int) (coolTime * (1 - bottle.getParameter(SkillParameterType.Pass))));
                    });
                    member.addEffect(effectAll, playerData);
                }
            }
        }else{
            particle.circle(playerData.getViewers(), playerData.getLocation(), 0.5);
            if (!playerData.hasEffect(effectOne)){
                if (!(playerData.hasEffect(effectAll) && playerData.getEffect("YouCanNotTimeTravelAll").getUUID() == playerData.getUUID())){
                    playerData.getSkillManager().getCoolTime().forEach((skill, coolTime)->{
                        playerData.getSkillManager().setCoolTime(skill, (int) (coolTime * (1 - getParameter(SkillParameterType.Pass))));
                    });
                    playerData.addEffect(effectOne, playerData);
                }
            }
        }

        SomSound.TickTack.play(playerData, playerData.getSoundLocation());
        return null;
    }
}
