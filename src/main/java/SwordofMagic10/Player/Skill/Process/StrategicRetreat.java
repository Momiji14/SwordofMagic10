package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;

import java.util.ArrayList;

import static SwordofMagic10.SomCore.Log;

public class StrategicRetreat extends SomSkill {
    public StrategicRetreat(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {

        if (playerData.getDungeonMenu().isInDungeon()){
            SomParticle particle = new SomParticle(Color.ORANGE, playerData);
            ArrayList<SomEntity> list = new ArrayList<>(playerData.getDungeonMenu().getDungeon().getMember());
            list.remove(playerData);

            SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 1, list, false);
            if (ray.isHitEntity()) {
                particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getHitPosition());
                PlayerData target = (PlayerData) ray.getHitEntity();

                target.teleport(playerData.getLocation());

                target.addEffect(SomEffect.List.Invincible.getEffect(), playerData);
                playerData.addEffect(SomEffect.List.Invincible.getEffect(), playerData);

                SomSound.Taken.play(playerData.getViewers(), playerData.getSoundLocation());
            } else {
                playerData.getSkillManager().setCoolTime(this, 20);
            }
        } else {
            playerData.getSkillManager().setCoolTime(this, 20);
        }
        return null;
    }
}
