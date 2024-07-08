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
import org.bukkit.Color;

public class Wiegenlied extends SomSkill {
    public Wiegenlied(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void castFirstTick() {
        SomSound.Wiegenlied.play(playerData.getViewers(), playerData.getSoundLocation());
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, false);
        SomParticle particle = new SomParticle(Color.PURPLE, playerData);
        double radius = getRadius();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("爆音の願瓶");
        if(playerData.hasBottle(bottle)){
            radius *= bottle.getParameter(SkillParameterType.Radius);
            double duration = getDuration() * bottle.getParameter(SkillParameterType.Duration);
            effect.setTime(duration);
        }

        particle.circle(playerData.getViewers(), playerData.getSoundLocation(), radius);
        for (SomEntity victim : SomEntity.nearSomEntity(playerData.getTargets(), playerData.getLocation(), radius)) {
            particle.sphere(playerData.getViewers(), victim.getLocation().addY(3), 0.5);
            victim.addEffect(effect, playerData);
        }
        return null;
    }
}
