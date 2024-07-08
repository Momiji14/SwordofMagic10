package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class CrossGuard extends SomSkill {

    public CrossGuard(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double duration = getDuration();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("静の願瓶");
        if(playerData.hasBottle(bottle)){
            duration *= bottle.getParameter(SkillParameterType.Duration);
        }

        playerData.addEffect(new SomEffect(getId() + "Counter", "構え", true, 1).setDoubleData(0, duration).setDoubleData(1, getATK()).setMultiply(StatusType.DamageResist, getStatus(StatusType.DamageResist)), playerData);
        return null;
    }
}
