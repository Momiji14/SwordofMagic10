package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.Dungeon.Instance.DungeonInstance;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static SwordofMagic10.SomCore.Log;

public class Resurrection extends SomSkill {

    public Resurrection(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        Collection<SomEntity> deathAllies = playerData.getDeathAllies();
        deathAllies.removeIf(entity -> {
            if (entity instanceof PlayerData targetData) {
                if (targetData.getDungeonMenu().isInDungeon()) {
                    DungeonInstance dungeon = targetData.getDungeonMenu().getDungeon();
                    if (dungeon.isLegendRaid() && dungeon.getDeathCount(targetData) > 2) {
                        playerData.sendMessage(targetData.getDisplayName() + "§aの§e復活可能回数§aは§c0§aです");
                        return true;
                    }
                }
            }
            return false;
        });
        SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 3, deathAllies, false);
        if (ray.isHitEntity()) {
            SomParticle particle = new SomParticle(Particle.END_ROD, playerData);
            particle.line(playerData.getViewers(), playerData.getHandLocation(), ray.getHitPosition());
            if (ray.getHitEntity() instanceof PlayerData target) target.setRespawn(true);

            SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("祝福の願瓶");
            if(playerData.hasBottle(bottle)){
                SomEffect effect = new SomEffect("Blessing", "祝福", true, bottle.getParameter(SkillParameterType.Duration)).setInvincible(true);
                playerData.addEffect(effect, playerData);
                ray.getHitEntity().addEffect(effect, playerData);
            }

            SomSound.Heal.play(playerData.getViewers(), ray.getHitPosition());
        } else {
            playerData.getSkillManager().setCoolTime(this, calcCoolTime(3));
        }
        return null;
    }
}
