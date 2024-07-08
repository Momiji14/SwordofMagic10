package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class Meteor extends SomSkill {
    public Meteor(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomParticle particleFire = new SomParticle(Particle.FLAME, playerData).setRandomVector().setRandomSpeed(1f);
        //降ってくる球の半径
        int starRadius = 5;
        //高さのスタート位置
        int high = 20;

        SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), getReach(), true);
        CustomLocation center = ray.getHitPosition().lower();
        CustomLocation centerUp = center.clone().addY(high+starRadius-3);

        particleFire.circle(playerData.getViewers(), center, 0.5);

        SomTask.run(()->{
            //上から降ってくる
            for (int i = 0; i < high; i++){
                particleFire.sphere(playerData.getViewers(), centerUp, starRadius);
                SomSound.Fire.play(playerData.getViewers(), centerUp, 5f);
                centerUp.addY(-1);
                SomTask.wait(125);
            }
            //どか～ん
            SomSound.Explode.play(playerData.getViewers(), center, 5f);
            particleFire.circleFill(playerData.getViewers(), center, getRadius());
            for (SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), center, getRadius())) {
                Damage.makeDamage(playerData, entity, DamageEffect.Fire, DamageOrigin.MAT, getDamage());
                SomTask.wait(50);
            }
        });

        return null;
    }
}
