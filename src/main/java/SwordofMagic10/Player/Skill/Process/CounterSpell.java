package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
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
        SomParticle particle = new SomParticle(Color.YELLOW);
        particle.circlePointLine(playerData.getViewers(), playerData.getLocation(), getRadius(), 6, 0, 0);
        return super.cast();
    }

    @Override
    public String active() {
        playerData.addEffect(Effect);
        SomParticle particle = new SomParticle(Particle.SPELL_WITCH);
        particle.circlePointLine(playerData.getViewers(), playerData.getLocation(), getRadius(), 6, 0, 0);
        SomSound.Porn.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
