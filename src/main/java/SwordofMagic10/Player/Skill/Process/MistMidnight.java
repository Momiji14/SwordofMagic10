package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class MistMidnight extends SomSkill {

    public MistMidnight(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        playerData.addEffect(new SomEffect(this, true));
        playerData.setInvisibility(getDurationTick());
        CustomLocation location = playerData.getEyeLocation();
        location.setPitch(0);
        if (playerData.getPlayer().isSneaking()) location.setYaw(location.getYaw()+180);
        SomRay ray = SomRay.rayLocationBlock(location, getReach(), true);
        CustomLocation from = playerData.getHipsLocation().clone();
        SomParticle particle = new SomParticle(Particle.SPELL_WITCH);
        particle.spawn(playerData.getViewers(), from);
        Location to = ray.getOriginPosition().subtract(playerData.getDirection()).setDirection(playerData.getDirection());
        playerData.teleport(to);
        particle.line(playerData.getViewers(), from, to);
        particle.spawn(playerData.getViewers(), to);
        SomSound.Warp.play(playerData.getViewers(), to);
        return null;
    }
}
