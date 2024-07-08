package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomBlockParticle;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class MagicShield extends SomSkill {
    public MagicShield(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        playerData.addEffect(new SomEffect(this, true), playerData);
        SomBlockParticle blockParticle = new SomBlockParticle(Material.LIGHT_BLUE_STAINED_GLASS);
        blockParticle.rotationCircleAtEntity(playerData.getViewers(), playerData, new Vector(0.5, 1.5, 0.5), -1.2, 1.5, 4, 16, getDurationTick(), 4);
        SomParticle particle = new SomParticle(Color.AQUA, playerData);
        particle.randomLocation(playerData.getViewers(), playerData.getHipsLocation(), 1, 10);
        SomSound.Heal.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
