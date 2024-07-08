package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import SwordofMagic10.SomCore;
import org.bukkit.scheduler.BukkitRunnable;

public class Stop extends SomSkill {
    public Stop(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true).setInvincible(true).setStun(true).setSilence("All").setRank(SomEffect.Rank.High);

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("休憩時間の願瓶");
        if(playerData.hasBottle(bottle)){
            double duration = getDuration() * bottle.getParameter(SkillParameterType.Duration);
            effect.setTime(duration);
        }

        playerData.addEffect(effect, playerData);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (playerData.hasEffect(effect)) {
                    SomSound.TickTack.play(playerData, playerData.getSoundLocation());
                } else this.cancel();
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), 0, 20);

        return null;
    }
}
