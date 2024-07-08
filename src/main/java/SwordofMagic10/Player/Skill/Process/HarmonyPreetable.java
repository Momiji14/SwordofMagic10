package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.DurationSkill;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

import static SwordofMagic10.Player.Skill.Process.BulletBit.headShot;

public class HarmonyPreetable extends SomSkill {

    public HarmonyPreetable(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void castFirstTick() {
        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("結末の願瓶");
        if(playerData.hasBottle(bottle)){
            SomSound.PageTurn.play(playerData.getViewers(), playerData.getSoundLocation());
        }else{
            SomSound.Tick.play(playerData.getViewers(), playerData.getSoundLocation());
        }
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.CRIT, playerData);
        double damage = getDamage();


        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("結末の願瓶");
        if(playerData.hasBottle(bottle)){
            particle = new SomParticle(Particle.SWEEP_ATTACK, playerData);
            damage = bottle.getParameter(SkillParameterType.HarmonyPreetable);
            double reach = bottle.getParameter(SkillParameterType.Reach);

            boolean flg = false;
            SomRay ray = SomRay.rayLocationEntity(playerData, reach, 0.1, playerData.getTargets(), false);
            if (ray.isHitEntity()) {
                if (Math.abs(Math.abs(ray.getHitEntity().getLocation().getYaw()) - Math.abs(playerData.getLocation().getYaw())) < 40 && playerData.hasEffect("MistMidnight") && ray.getHitEntity().hasEffect("DayDream")) {
                    Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.None, DamageOrigin.ATK, damage);
                    flg = true;
                }else Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.None, DamageOrigin.ATK, 0);
            }
            particle.spawn(playerData.getViewers(), ray.getOriginPosition());
            SomSound.Slash.play(playerData, playerData.getSoundLocation());
            playerData.getSkillManager().setCoolTime(this, calcCoolTime(43));

            if (flg){
                SomTask.wait(500);
                DurationSkill.Duration durationSkill = new DurationSkill.Duration(playerData, 20, 20*4);
                durationSkill.setRunnable(() -> SomSound.HeartBeat.play(playerData.getViewers(), playerData.getSoundLocation()));
                durationSkill.run();
            }

        }else{
            SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 0.1, playerData.getTargets(), false);
            if (ray.isHitEntity()) {
                if (Math.abs(Math.abs(ray.getHitEntity().getLocation().getYaw()) - Math.abs(playerData.getLocation().getYaw())) < 40) {
                    damage += getParameter(SkillParameterType.HarmonyPreetable);
                }
                Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.None, DamageOrigin.ATK, headShot(playerData, ray, damage, getHeadDamage()));
            }
            particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getOriginPosition());
            SomSound.Handgun.play(playerData.getViewers(), playerData.getSoundLocation());
        }

        return null;
    }
}
