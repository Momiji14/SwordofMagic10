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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class MistMidnight extends SomSkill {

    public MistMidnight(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.SPELL_WITCH, playerData);
        SomEffect effect = new SomEffect(this, true);

        double reach = getReach();
        double duration = getDuration();

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("霧の願瓶");
        if(playerData.hasBottle(bottle2)){
            duration *= bottle2.getParameter(SkillParameterType.Duration);
            reach = 0;
        }

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("機敏の願瓶");
        if(playerData.hasBottle(bottle1)){
            reach *= bottle1.getParameter(SkillParameterType.Reach);
        }else{
            playerData.setInvisibility((int) (duration * 20));
            playerData.addEffect(effect.setTime(duration), playerData);
        }

        CustomLocation location = playerData.getEyeLocation();
        location.setPitch(0);

        if (playerData.getPlayer().isSneaking()) location.setYaw(location.getYaw()+180);
        SomRay ray = SomRay.rayLocationBlock(location, reach, true);

        CustomLocation from = playerData.getHipsLocation().clone();
        Location to = ray.getOriginPosition().subtract(playerData.getDirection()).setDirection(playerData.getDirection());


        if(!playerData.hasBottle(bottle2)) playerData.teleport(to);

        particle.spawn(playerData.getViewers(), from);
        particle.line(playerData.getViewers(), from, to);
        particle.spawn(playerData.getViewers(), to);

        SomSound.Warp.play(playerData.getViewers(), to);
        return null;
    }
}
