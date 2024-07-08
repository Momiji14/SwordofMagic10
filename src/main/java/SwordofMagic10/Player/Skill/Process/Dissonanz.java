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

public class Dissonanz extends SomSkill {
    public Dissonanz(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void castFirstTick() {
        SomSound.Dissonanz.play(playerData.getViewers(), playerData.getSoundLocation());
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, false);
        SomParticle particle = new SomParticle(Color.BLACK, playerData);
        double radius = getRadius();

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("妨害の願瓶");
        if(playerData.hasBottle(bottle1)){
            effect.setMultiply(StatusType.MAT, bottle1.getStatus(StatusType.MAT));
            effect.setMultiply(StatusType.Movement, bottle1.getStatus(StatusType.Movement));
        }

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("爆音の願瓶");
        if(playerData.hasBottle(bottle2)){
            radius *= bottle2.getParameter(SkillParameterType.Radius);
            double duration = getDuration() * bottle2.getParameter(SkillParameterType.Duration);
            effect.setTime(duration);
        }

        particle.circle(playerData.getViewers(), playerData.getLocation(), radius);
        for (SomEntity victim : SomEntity.nearSomEntity(playerData.getTargets(), playerData.getLocation(), radius)) {
            victim.addEffect(effect, playerData);
        }
        return null;
    }
}
