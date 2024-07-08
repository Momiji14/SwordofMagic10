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

public class Hallucination extends SomSkill {
    public Hallucination(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true);

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("影絵の願瓶");
        if(playerData.hasBottle(bottle)){
            double duration = getDuration() * bottle.getParameter(SkillParameterType.Duration);
            effect.setTime(duration);
        }

        playerData.addEffect(effect, playerData);

        SomParticle particle = new SomParticle(Color.BLACK, playerData);
        particle.randomLocation(playerData.getViewers(), playerData.getHipsLocation(), 1, 10);
        SomSound.Heal.play(playerData.getViewers(), playerData.getSoundLocation());

        return null;
    }
}
