package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomParticle;
import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class Redel extends SomSkill {

    public Redel(PlayerData playerData) {
        super(playerData);
    }

    private int ready = 0;
    private List<BukkitTask> tasks = new ArrayList<>();

    public void setReady() {
        this.ready++;
        BukkitTask task = SomTask.delay(() -> {
            if(0 < ready) ready--;
            tasks.remove(0);
        }, 80);
        tasks.add(task);
    }

    @Override
    public boolean cast() {
        return super.cast() && 0 < ready;
    }

    @Override
    public String active() {
        if(0 < ready){
            ready--;
            tasks.get(0).cancel();
            tasks.remove(0);
        }

        double damage = getDamage();

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("速攻の願瓶");
        if (playerData.hasBottle(bottle1)) damage *= 2;

        for (SomEntity hitEntity : SomEntity.nearSomEntity(playerData.getTargets(), playerData.getLocation(), getRadius())) {
            Damage.makeDamage(playerData, hitEntity, DamageEffect.None, DamageOrigin.ATK, damage);
        }
        SomParticle particle = new SomParticle(Particle.SWEEP_ATTACK, playerData);
        particle.circle(playerData.getViewers(), playerData.getHipsLocation(), getRadius());
        SomSound.Slash.play(playerData.getViewers(), playerData.getSoundLocation());
        return null;
    }
}
