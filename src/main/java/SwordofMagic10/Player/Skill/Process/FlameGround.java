package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.*;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class FlameGround extends SomSkill {

    public FlameGround(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.FLAME, playerData).setRandomVector().setRandomSpeed(0.25f);
        SomParticle particle2 = new SomParticle(Particle.LAVA, playerData).setRandomVector().setRandomSpeed(1f);

        double radius = getRadius();
        int waitTick = getDurationTick()/getCount();
        double damage = getDamage() / getCount();
        particle.setTime(waitTick*50);
        particle2.setTime(waitTick*50);

        SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), getReach(), true);
        CustomLocation center = ray.getHitPosition();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("燃焼の願瓶");
        if(playerData.hasBottle(bottle)){
            double duration = bottle.getParameter(SkillParameterType.Duration);
            double mat = bottle.getStatus(StatusType.MAT);
            playerData.addEffect(new SomEffect("Burning", "燃焼", true, duration).setMultiply(StatusType.MAT, mat), playerData);
        }

        SomSound.Fire.play(playerData.getViewers(), playerData.getSoundLocation());
        DurationSkill.MagicCircle magicCircle = DurationSkill.magicCircle(playerData, center, radius, waitTick, getDurationTick());
        magicCircle.setRunnable(() -> {
            SomTask.run(() -> particle.circleFill(playerData.getViewers(), center, radius, 3));
            SomTask.run(() -> particle2.circleFill(playerData.getViewers(), center, radius, 3));
            SomTask.run(() -> particle2.circle(playerData.getViewers(), center, radius, 3));
            SomSound.Flame.play(playerData.getViewers(), center);
            for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), center, radius)) {
                Damage.makeDamage(playerData, entity, DamageEffect.Fire, DamageOrigin.MAT, damage);
                SomTask.wait(50);
            }
        });
        magicCircle.run();
        return null;
    }
}
