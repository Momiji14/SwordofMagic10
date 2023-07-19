package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;

public class Zucken extends SomSkill {

    public Zucken(PlayerData playerData) {
        super(playerData);
    }

    private boolean ready = false;

    public void setReady() {
        this.ready = true;
        SomTask.delay(() -> ready = false, 60);
    }

    @Override
    public boolean cast() {
        return super.cast() && ready;
    }

    @Override
    public String active() {
        double damage = getDamage();
        for (SomEntity hitEntity : SomEntity.nearSomEntity(playerData.getTargets(), playerData.getLocation(), getRadius())) {
            Damage.makeDamage(playerData, hitEntity, DamageEffect.None, DamageOrigin.ATK, damage);
        }
        SomParticle particle = new SomParticle(Particle.SWEEP_ATTACK);
        particle.circle(playerData.getViewers(), playerData.getHipsLocation(), getRadius());
        SomSound.Slash.play(playerData.getViewers(), playerData.getSoundLocation());
        ((Redel) playerData.getSkillManager().getSkill("Redel")).setReady();
        return null;
    }
}
