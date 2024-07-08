package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Player.Skill.Process.BulletBit.headShot;

public class SeptEtoiles extends SomSkill {
    public SeptEtoiles(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomTask.run(()->{
            SomParticle particle = new SomParticle(Particle.CRIT, playerData);
            double reach = getReach();
            double damage = getDamage();
            CustomLocation position;

            SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("神出の願瓶");
            if (playerData.hasEffect("Prompt")) damage = bottle.getParameter(SkillParameterType.Damage);

            //レイキャスト
            SomRay ray = SomRay.rayLocationEntity(playerData, reach, 1, playerData.getTargets(), false);
            //ヒット判定
            if (ray.isHitEntity()) {
                Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.None, DamageOrigin.ATK, damage);
                position = ray.getHitEntity().getEyeLocation();
            }else position = ray.getHitPosition();

            for (int i = 0; i < 6; i++){
                CustomLocation clonePosition =  position.clone().lookLocation(playerData.getEyeLocation());

                clonePosition.addYaw((float) randomDouble(-15, 15));
                clonePosition.addPitch((float) randomDouble(-15, 15));
                //パーティクル
                particle.line(playerData.getViewers(), clonePosition, reach);
                SomSound.Swish.play(playerData.getViewers(), position);
                SomTask.wait(25);
            }
        });

        return null;
    }
}
