package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.DurationSkill;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;
import org.bukkit.block.data.type.Fire;

import static SwordofMagic10.Player.Skill.Process.BulletBit.headShot;

public class FireBlindly  extends SomSkill {
    public FireBlindly(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void castFirstTick() {
        SomTask.run(()->{
            SomSound.TickLow.play(playerData.getViewers(), playerData.getSoundLocation());
            SomTask.wait(500);
            SomSound.TickMiddle.play(playerData.getViewers(), playerData.getSoundLocation());
        });
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.CRIT, playerData);
        double reach = getReach();
        double damage = getDamage() / getCount();
        double head = getHeadDamage() / getCount();
        double additional = getParameter(SkillParameterType.FireBlindly) / getCount();

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("笑顔の願瓶");

        for (int i = 0; i < getCount(); i++) {
            //サウンド
            SomSound.MachineGun.play(playerData.getViewers(), playerData.getSoundLocation());
            //レイキャスト
            SomRay ray = SomRay.rayLocationEntity(playerData, reach, 0.1, playerData.getTargets(), false);
            //パーティクル
            particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
            //ヒット判定
            if (ray.isHitEntity()) {
                if (ray.getHitEntity().isSilence("FireBlindly") || playerData.hasBottle(bottle)){
                    Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.None, DamageOrigin.ATK, headShot(playerData, ray, damage + additional, head));
                }else{
                    Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.None, DamageOrigin.ATK, headShot(playerData, ray, damage, head));
                }
            }

            SomTask.wait(getDurationMillie()/getCount());
        }

//        int perTick = getDurationTick() / getCount();
//        DurationSkill.Duration durationSkill = new DurationSkill.Duration(playerData, perTick, getDurationTick());
//        durationSkill.setRunnable(()->{
//            //サウンド
//            SomSound.MachineGun.play(playerData.getViewers(), playerData.getSoundLocation());
//            //レイキャスト
//            SomRay ray = SomRay.rayLocationEntity(playerData, reach, 0.1, playerData.getTargets(), false);
//            //パーティクル
//            particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
//            //ヒット判定
//            if (ray.isHitEntity()) {
//                if (!ray.getHitEntity().isSilence("FireBlindly")) Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.None, DamageOrigin.ATK, headShot(playerData, ray, damage, head));
//                else Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.None, DamageOrigin.ATK, headShot(playerData, ray, damage + additional, head));
//            }
//        });
//        durationSkill.run();

        return null;
    }
}
