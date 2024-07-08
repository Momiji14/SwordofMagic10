package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class Zornhau extends SomSkill {

    public Zornhau(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public boolean cast(){
        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("速攻の願瓶");
        return super.cast() && (!playerData.hasBottle(bottle) || getCurrentStack() == getStack());
    }

    @Override
    public String active() {
        double damage = getDamage();

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("型の願瓶");
        if(playerData.hasBottle(bottle2)){
            double duration = bottle2.getParameter(SkillParameterType.Duration);
            double atk = bottle2.getStatus(StatusType.ATK);
            playerData.addEffect(new SomEffect("Model", "型", true, duration).setMultiply(StatusType.ATK, atk), playerData);
        }

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("速攻の願瓶");
        if (playerData.hasBottle(bottle1)) damage *= 2;

        for (SomEntity entity : SomEntity.fanShapedSomEntity(playerData.getTargets(), playerData.getLocation(), getReach(), getAngle())) {
            Damage.makeDamage(playerData, entity, DamageEffect.None, DamageOrigin.ATK, damage);
            SomTask.wait(50);
        }
        SomParticle particle = new SomParticle(Particle.SWEEP_ATTACK, playerData);
        particle.widthLine(playerData.getViewers(), playerData.getEyeLocation().frontHorizon(getReach()/2), 0, 1);
        particle.fanShaped(playerData.getViewers(), playerData.getLocation(), getReach(), getAngle());
        SomSound.Slash.play(playerData.getViewers(), playerData.getSoundLocation());



        if(playerData.hasBottle(bottle1)){
            playerData.getSkillManager().getSkill("Zornhau").addCurrentStack(-getStack());

            SomTask.wait(50);
            playerData.getSkillManager().getSkill("Zucken").active();
            SomTask.wait(50);
            playerData.getSkillManager().getSkill("Redel").active();
        }else{
            ((Zucken) playerData.getSkillManager().getSkill("Zucken")).setReady();
        }
        return null;
    }
}
