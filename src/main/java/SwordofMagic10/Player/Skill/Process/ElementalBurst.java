package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

public class ElementalBurst extends SomSkill {
    public ElementalBurst(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true);
        SomParticle particleRed = new SomParticle(Color.RED, playerData);
        SomParticle particleBlue = new SomParticle(Color.BLUE, playerData);
        SomParticle particleYellow = new SomParticle(Color.YELLOW, playerData);

        playerData.addEffect(effect, playerData);
        SomSound.Burst.play(playerData.getViewers(), playerData.getSoundLocation());

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("暴発の願瓶");
        if(playerData.hasBottle(bottle)){
            double width = 3;
            SomRay ray = SomRay.rayLocationEntity(playerData, bottle.getParameter(SkillParameterType.Reach), width, playerData.getTargets(), true);

            if (ray.isHitEntity()) {
                for (SomEntity entity : ray.getHitEntities()){
                    Damage.makeDamage(playerData, entity, DamageEffect.Fire, DamageOrigin.MAT, bottle.getParameter(SkillParameterType.Damage));
                }
            }
            particleRed.line(playerData.getViewers(), playerData.getEyeLocation(), ray.getOriginPosition(), width*2);
            particleBlue.line(playerData.getViewers(), playerData.getEyeLocation(), ray.getOriginPosition(), width*2);
            particleYellow.line(playerData.getViewers(), playerData.getEyeLocation(), ray.getOriginPosition(), width*2);
        }


        CustomLocation location  = playerData.getLocation();
        particleRed.circle(playerData.getViewers(), location.clone().addXZ(1, 0), 2);
        particleBlue.circle(playerData.getViewers(), location.clone().addXZ(-1, 1), 2);
        particleYellow.circle(playerData.getViewers(), location.clone().addXZ(-1, -1), 2);

        return null;
    }
}
