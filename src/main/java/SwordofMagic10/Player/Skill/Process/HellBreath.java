package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
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

public class HellBreath extends SomSkill {
    public HellBreath(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.FLAME, playerData).setRandomVector().setRandomSpeed(0.1f);

        double reach = getReach();
        double count = getCount();
        double damage = getDamage() / count;
        double wait = getDurationMillie()/count;
        double width = 1;

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("着火爆発の願瓶");
        if(playerData.hasBottle(bottle)){
            count = 1;
            reach *= bottle.getParameter(SkillParameterType.Reach);
            width *= bottle.getParameter(SkillParameterType.Reach);
            damage = bottle.getParameter(SkillParameterType.Damage);
        }

        playerData.setLocationLock(playerData.getLocation());
        for (int i = 0; i < count; i++) {
            SomRay ray = SomRay.rayLocationEntity(playerData, reach, width, playerData.getTargets(), true);
            for (SomEntity entity : ray.getHitEntities()) {
                Damage.makeDamage(playerData, entity, DamageEffect.Fire, DamageOrigin.MAT, damage);
            }
            particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition(), width*2);
            SomSound.Fire.play(playerData.getViewers(), playerData.getSoundLocation());
            SomTask.wait(wait);
        }
        playerData.resetLocationLock();
        return null;
    }
}
