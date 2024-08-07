package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomBlockParticle;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Material;
import org.bukkit.Particle;

public class Prominence extends SomSkill {
    public Prominence(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double radius = getRadius();
        double count = getCount();
        double damage = getDamage() / count;
        double wait = getDurationMillie()/count;

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("炎風の願瓶");
        if(playerData.hasBottle(bottle)){
            radius *= bottle.getParameter(SkillParameterType.Radius);
        }

        SomParticle particle = new SomParticle(Particle.FLAME, playerData).setRandomVector().setRandomSpeed(0.2f).setAmount(5);
        SomBlockParticle blockParticle = new SomBlockParticle(Material.RED_GLAZED_TERRACOTTA);

        blockParticle.rotationCircleAtEntity(playerData.getViewers(), playerData, 0.5, -0.8, radius, 6, 14, getDurationTick(), 2);

        SomSound.Fire.play(playerData.getViewers(), playerData.getSoundLocation());
        double finalRadius = radius;
        SomTask.run(() -> {
            for (int i = 0; i < count; i++) {
                for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), playerData.getLocation(), finalRadius)) {
                    Damage.makeDamage(playerData, entity, DamageEffect.Fire, DamageOrigin.MAT, damage);
                }
                particle.spawn(playerData.getViewers(), playerData.getHipsLocation());
                SomTask.wait(wait);
            }
        });
        return null;
    }
}
