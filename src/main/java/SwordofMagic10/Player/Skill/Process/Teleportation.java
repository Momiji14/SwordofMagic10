package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class Teleportation extends SomSkill {

    public Teleportation(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        playerData.addEffect(SomEffect.List.Invincible.getEffect());
        SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), getReach(), true);
        SomDisplayParticle displayParticle = new SomItemParticle(new ItemStack(Material.ENDER_PEARL));
        displayParticle.addAnimation(new AnimationDelay(5));
        displayParticle.addAnimation(new CustomTransformation().setScale(0, 0, 0).setTime(5));
        CustomLocation from = playerData.getHipsLocation().clone();
        displayParticle.spawn(playerData.getViewers(), from);
        SomParticle particle = new SomParticle(Particle.END_ROD).setRandomVector().setRandomSpeed(0.25f);
        SomParticle particle2 = new SomParticle(Particle.SPELL_WITCH);
        particle.spawn(playerData.getViewers(), from);
        particle2.spawn(playerData.getViewers(), from);
        Location to = ray.getOriginPosition().subtract(playerData.getDirection()).setDirection(playerData.getDirection());
        displayParticle.spawn(playerData.getViewers(), to);
        playerData.teleport(to);
        particle.line(playerData.getViewers(), from, to);
        particle2.line(playerData.getViewers(), from, to);
        particle.spawn(playerData.getViewers(), to);
        particle2.spawn(playerData.getViewers(), to);
        SomSound.Warp.play(playerData.getViewers(), to);
        return null;
    }
}
