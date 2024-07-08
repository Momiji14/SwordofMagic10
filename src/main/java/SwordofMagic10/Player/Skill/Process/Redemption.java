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

public class Redemption extends SomSkill {
    public Redemption(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true).setInvincible(true).setStun(true);
        SomParticle particle = new SomParticle(Color.AQUA, playerData);

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("背水の願瓶");
        if(playerData.hasBottle(bottle)){
            effect.setMultiply(StatusType.ATK, bottle.getStatus(StatusType.ATK));
            effect.setMultiply(StatusType.CriticalDamage, bottle.getStatus(StatusType.CriticalDamage));
        }

        playerData.addEffect(effect, playerData);

        particle.randomLocation(playerData.getViewers(), playerData.getHipsLocation(), 1, 10);

        SomSound.Heal.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
