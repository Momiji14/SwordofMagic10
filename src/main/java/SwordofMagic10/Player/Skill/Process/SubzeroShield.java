package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class SubzeroShield extends SomSkill {
    public SubzeroShield(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true).setDoubleData(0, getPercent()).setDoubleData(1, 5.0);
        SomParticle particle = new SomParticle(Color.AQUA, playerData);

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("氷棘の願瓶");
        if(playerData.hasBottle(bottle1)){
            effect.setMultiply(StatusType.MAT, bottle1.getStatus(StatusType.MAT));
        }

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("氷結の願瓶");
        if(playerData.hasBottle(bottle2)){
            effect.setDoubleData(0, bottle2.getParameter(SkillParameterType.Percent));
        }

        playerData.addEffect(effect, playerData);

        particle.randomLocation(playerData.getViewers(), playerData.getHipsLocation(), 1, 10);
        SomSound.Heal.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
