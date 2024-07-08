package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class Brink extends SomSkill {

    public Brink(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public boolean cast() {
        SomParticle particle = new SomParticle(Particle.FIREWORKS_SPARK, playerData).setVectorUp().setRandomSpeed(0.25f);
        double radius = getRadius();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("空間把握の願瓶");
        if (playerData.hasBottle(bottle)) radius = 1;
        particle.circle(playerData.getViewers(), playerData.getLocation(), radius, 2);

        return super.cast() && (!playerData.hasBottle(bottle) || getCurrentStack() == getStack());
    }

    @Override
    public String active() {

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("空間把握の願瓶");
        if(playerData.hasBottle(bottle2)){
            double duration = bottle2.getParameter(SkillParameterType.Duration);
            double mat = bottle2.getStatus(StatusType.MAT);
            playerData.addEffect(new SomEffect("SpaceNotation", "空間把握", true, duration).setMultiply(StatusType.MAT, mat), playerData);

            playerData.getSkillManager().getSkill("Brink").addCurrentStack(-getStack());

            return null;
        }



        SomParticle particle = new SomParticle(Particle.END_ROD, playerData).setRandomVector().setRandomSpeed(0.25f);
        SomParticle particle2 = new SomParticle(Particle.SPELL_WITCH, playerData);
        SomDisplayParticle displayParticle = new SomItemParticle(new ItemStack(Material.ENDER_PEARL));
        displayParticle.addAnimation(new AnimationDelay(5));
        displayParticle.addAnimation(new CustomTransformation().setScale(0, 0, 0).setTime(5));
        double reach = getReach();

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("空間移動の願瓶");
        if(playerData.hasBottle(bottle1)){
            reach *= bottle1.getParameter(SkillParameterType.Reach);
        }

        SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), reach, true);

        CustomLocation from = playerData.getHipsLocation().clone();
        Location to = ray.getOriginPosition().subtract(playerData.getDirection()).setDirection(playerData.getDirection());

        displayParticle.spawn(playerData.getViewers(), from);

        particle.spawn(playerData.getViewers(), from);
        particle2.spawn(playerData.getViewers(), from);

        displayParticle.spawn(playerData.getViewers(), to);
        for (PlayerData playerData : playerData.getAllies(getRadius())) {
            playerData.teleport(to);
            if (this.playerData != playerData) {
                displayParticle.spawn(playerData.getViewers(), playerData.getHipsLocation());
                particle.line(playerData.getViewers(), playerData.getHipsLocation(), from);
                particle2.line(playerData.getViewers(), playerData.getHipsLocation(), from);
            }
        }

        particle.line(playerData.getViewers(), from, to);
        particle2.line(playerData.getViewers(), from, to);
        particle.spawn(playerData.getViewers(), to);
        particle2.spawn(playerData.getViewers(), to);
        SomSound.Warp.play(playerData.getViewers(), to);
        return null;
    }
}
