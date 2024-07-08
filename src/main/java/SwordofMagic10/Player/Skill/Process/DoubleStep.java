package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import static SwordofMagic10.SomCore.Log;

public class DoubleStep extends SomSkill {
    public DoubleStep(PlayerData playerData) {
        super(playerData);
    }

    boolean backStep = false;

    @Override
    public String active() {
        CustomLocation direction = playerData.getEyeLocation();
        direction.setPitch(0);
        boolean flg = true;

        if (backStep){
            direction.addYaw(180);
            backStep = false;
            flg = false;
        }else{
            SomTask.delay(()->{
                backStep = true;
                SomTask.delay(()-> backStep = false,35);
            },5);
        }

        SomRay rayEntity = SomRay.rayLocationEntity(playerData.getTargets(), playerData.getEyeLocation(), getReach(), 1, false);
        SomRay ray = SomRay.rayLocationBlock(direction, getReach(), true);
        Location to;
        if (rayEntity.isHitEntity() && flg){
            to = rayEntity.getHitPosition().subtract(playerData.getDirection().setY(0).multiply(3)).setDirection(playerData.getDirection());
        }else if (flg) {
            to = ray.getOriginPosition().subtract(playerData.getDirection().setY(0).multiply(0.2)).setDirection(playerData.getDirection());
        }else {
            to = ray.getOriginPosition().add(playerData.getDirection().setY(0).multiply(0.2)).setDirection(playerData.getDirection());
        }

        playerData.teleport(to);

        SomSound.Swish.play(playerData, playerData.getLocation());

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("神出の願瓶");
        if(playerData.hasBottle(bottle)){
            double duration = bottle.getParameter(SkillParameterType.Duration);
            playerData.addEffect(new SomEffect("Prompt", "神出", true, duration), playerData);
        }

        return null;
    }
}
