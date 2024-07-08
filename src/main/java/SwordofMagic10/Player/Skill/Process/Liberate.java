package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import SwordofMagic10.SomCore;
import org.bukkit.Color;
import org.bukkit.scheduler.BukkitRunnable;

public class Liberate extends SomSkill {
    public Liberate(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect.Toggle(this, true);

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("気迫の願瓶");
        if(playerData.hasBottle(bottle1)){
            effect.setMultiply(StatusType.Hate, bottle1.getStatus(StatusType.Hate));
        }

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("熟練の願瓶");
        if(playerData.hasBottle(bottle2)){
            effect.setMultiply(StatusType.DamageMultiply, bottle2.getStatus(StatusType.DamageMultiply));
            effect.setMultiply(StatusType.ATK, 0);
        }

        if (playerData.hasEffect(effect)) {
            playerData.removeEffect(effect);
        } else {
            playerData.addEffect(effect, playerData);
        }
        SomParticle particle = new SomParticle(Color.YELLOW, playerData).setRandomVector().setSpeed(0.15f).setAmount(10);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (playerData.hasEffect(effect) && playerData.isOnline()) {
                    particle.spawn(playerData.getViewers(), playerData.getHipsLocation());
                } else this.cancel();
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), 0, 5);
        SomSound.Heal.play(playerData, playerData.getSoundLocation());
        return null;
    }
}
