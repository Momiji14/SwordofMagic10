package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class Slithering extends SomSkill {

    public Slithering(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect.Toggle(this, true).setSilence(getId()).setStun(true).setRank(SomEffect.Rank.High);

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("盾の願瓶");
        if(playerData.hasBottle(bottle)){
            effect = new SomEffect.Toggle(this, true).setRank(SomEffect.Rank.High).setMultiply(StatusType.DamageResist, 1-bottle.getStatus(StatusType.DamageResist));
        }

        if (playerData.hasEffect(effect)) {
            playerData.removeEffect(effect);
        } else {
            playerData.addEffect(effect, playerData);
        }
        SomSound.Bash.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
