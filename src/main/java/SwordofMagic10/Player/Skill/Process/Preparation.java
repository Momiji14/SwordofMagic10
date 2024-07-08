package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import SwordofMagic10.SomCore;
import org.bukkit.scheduler.BukkitRunnable;

public class Preparation extends SomSkill {
    public Preparation(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, true).setDoubleData(0, -1);

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("攻勢の願瓶");
        if(playerData.hasBottle(bottle)){
            double duration = bottle.getParameter(SkillParameterType.Duration);
            double atk = bottle.getStatus(StatusType.ATK);
            playerData.addEffect(new SomEffect("Offensive", "攻勢", true, duration).setMultiply(StatusType.ATK, atk), playerData);
            effect.setDoubleData(0, 1);
        }

        playerData.addEffect(effect, playerData);

        new BukkitRunnable() {
            int soundCount = 6;
            @Override
            public void run() {
                if (soundCount == 0 || !playerData.hasEffect(effect)) this.cancel();
                SomSound.Swish.play(playerData.getViewers(), playerData.getSoundLocation());
                soundCount--;
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), 0, 5);


        return null;
    }
}
