package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.DataBase.SkillDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class ShieldPush extends SomSkill {

    public ShieldPush(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, false).setStun(true);
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 2, playerData.getTargets(), false);

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("衝撃の願瓶");
        if(playerData.hasBottle(bottle1)){
            effect.setRank(SomEffect.Rank.High);
        }


        if (ray.isHitEntity()) {
            SomSound.Push.play(playerData);
            Damage.makeDamage(playerData, ray.getHitEntity(), DamageEffect.None, DamageOrigin.ATK, getDamage(), 10, 0);
            ray.getHitEntity().addEffect(effect, playerData);
        }
        SomParticle particle = new SomParticle(Particle.CRIT, playerData);
        particle.randomLocation(playerData.getViewers(), ray.getHitPosition(), 1, 10);
        SomSound.Bash.play(playerData.getViewers(), playerData.getSoundLocation());


        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("不屈の願瓶");
        if(playerData.hasBottle(bottle2)){
            SomSkill painBarrier = playerData.getSkillManager().getSkill("PainBarrier");
            if (playerData.getSkillManager().isCoolTime(painBarrier)){
                playerData.getSkillManager().setCoolTime(painBarrier, 0);
            }
            playerData.getSkillManager().setCoolTime(this,getCoolTime()*2);
        }

        return null;
    }
}
