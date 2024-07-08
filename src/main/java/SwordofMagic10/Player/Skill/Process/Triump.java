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

public class Triump extends SomSkill {
    public Triump(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void castFirstTick() {
        SomSound.Triump.play(playerData.getViewers(), playerData.getSoundLocation());
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true);
        SomParticle particle = new SomParticle(Color.YELLOW, playerData);
        double radius = getRadius();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("爆音の願瓶");
        if(playerData.hasBottle(bottle)){
            radius *= bottle.getParameter(SkillParameterType.Radius);
            double duration = getDuration() * bottle.getParameter(SkillParameterType.Duration);
            effect.setTime(duration);
        }

        particle.circle(playerData.getViewers(), playerData.getLocation(), radius);
        for (SomEntity victim : SomEntity.nearPlayer(playerData.getAllies(), playerData.getLocation(), radius)) {
            victim.addEffect(effect, playerData);
        }
        return null;
    }
}
