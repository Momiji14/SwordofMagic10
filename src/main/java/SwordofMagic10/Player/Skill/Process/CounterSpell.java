package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;
import org.bukkit.Particle;

public class CounterSpell extends SomSkill {

    public SomEffect Effect = new SomEffect("CounterSpell", "カウンタースペル", true, 1);
    public CounterSpell(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public boolean cast() {
        SomParticle particle = new SomParticle(Color.YELLOW, playerData);

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("裾分の願瓶");
        if(playerData.hasBottle(bottle)){
            for (SomEntity member : playerData.getMember()){
                particle.circlePointLine(playerData.getViewers(), member.getLocation(), getRadius(), 6, 0, 0);
            }
        }else{
            particle.circlePointLine(playerData.getViewers(), playerData.getLocation(), getRadius(), 6, 0, 0);
        }

        return super.cast();
    }

    @Override
    public String active() {
        SomParticle particle = new SomParticle(Particle.SPELL_WITCH, playerData);
        Effect.setOwner(playerData);

        SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("裾分の願瓶");
        if(playerData.hasBottle(bottle)){
            for (SomEntity member : playerData.getMember()){
                member.addEffect(Effect, playerData);
                particle.circlePointLine(playerData.getViewers(), member.getLocation(), getRadius(), 6, 0, 0);
                SomSound.Porn.play(playerData.getViewers(), member.getSoundLocation());
            }
        }else{
            playerData.addEffect(Effect, playerData);
            particle.circlePointLine(playerData.getViewers(), playerData.getLocation(), getRadius(), 6, 0, 0);
            SomSound.Porn.play(playerData.getViewers(), playerData.getSoundLocation());
        }

        return null;
    }
}
