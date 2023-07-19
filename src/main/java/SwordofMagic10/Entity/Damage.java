package SwordofMagic10.Entity;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTextParticle;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.Process.CounterBlade;
import SwordofMagic10.Player.Statistics;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Random;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.Entity.StatusType.*;

public class Damage {
    private static final Random random = new Random();

    public static void makeHeal(SomEntity healer, SomEntity target, double baseHeal, double multiply) {
        double spt = healer.getStatus(SPT);
        double heal = baseHeal + spt * multiply;

        if (target instanceof PlayerData playerData && playerData.getSetting().isPvPMode()) heal *= 0.1;
        double score = Math.min(heal, target.getStatus(MaxHealth) - target.getStatus(Health));
        target.addHealth(heal);

        new SomTextParticle("§a" + scale(heal) + "♥").pop(healer.getViewers(), target.getEyeLocation());

        String logText = scale(heal) + " §f[" + scale(multiply*100) + "%]" + " §c[" + scale(target.getStatus(Health)) + "/" + scale(target.getStatus(MaxHealth)) + "]";
        if (healer instanceof PlayerData playerData) {
            if (healer == target) {
                playerData.getStatistics().add(Statistics.Enum.AmountHeal, score);
            }
            if (playerData.getSetting().isDamageLog()) {
                playerData.sendMessage("§a≫" + logText + " §e[" + target.getDisplayName() + "§e]");
            }
        }

        if (healer != target && target instanceof PlayerData playerData) {
            if (playerData.getSetting().isDamageLog()) {
                playerData.sendMessage("§a≪" + logText + " §e[" + healer.getDisplayName() + "§e]");
            }
        }
    }

    public static void makeDamage(SomEntity attacker, SomEntity victim, DamageEffect damageEffect, DamageOrigin damageOrigin, double multiply) {
        makeDamage(attacker, victim, damageEffect, damageOrigin, multiply, 0, attacker.getLocation());
    }

    public static void makeDamage(SomEntity attacker, SomEntity victim, DamageEffect damageEffect, DamageOrigin damageOrigin, double multiply, Location origin) {
        makeDamage(attacker, victim, damageEffect, damageOrigin, multiply, 0, origin);
    }

    public static void makeDamage(SomEntity attacker, SomEntity victim, DamageEffect damageEffect, DamageOrigin damageOrigin, double multiply, double knock) {
        makeDamage(attacker, victim, damageEffect, damageOrigin, multiply, knock, attacker.getLocation());
    }

    public static void makeDamage(SomEntity attacker, SomEntity victim, DamageEffect damageEffect, DamageOrigin damageOrigin, double multiply, double knock, Location origin) {
        if (victim.isInvalid() || attacker.isInvalid()) return;

        double atk = damageOrigin == DamageOrigin.ATK ? attacker.getATK() : attacker.getMAT();
        double def = damageOrigin == DamageOrigin.ATK ? victim.getDEF() : victim.getMDF();
        double criRate = attacker.getStatus(Critical);
        double criResist = victim.getStatus(CriticalResist);
        double criPercent = criRate / (1+criResist*7.5);
        double criATK = attacker.getStatus(CriticalDamage);

        boolean isCritical = random.nextDouble() < criPercent;
        double baseDamage = Math.max(0, (atk * atk) / (atk + def * 2));
        double criDamage = criATK;
        double damage = baseDamage;
        if (isCritical) damage += criDamage;

        damage *= multiply;

        if (victim.getLevel() > attacker.getLevel()) {
            damage *= MinMax(1 - (victim.getLevel() - (attacker.getLevel()+5))*0.2, 0, 1);
        }

        if (victim.hasEffect("Invincible")) {
            SomEffect effect = victim.getEffect("Invincible");
            if (attacker instanceof PlayerData playerData) {
                if (playerData.getSetting().isDamageLog()) {
                    playerData.sendMessage("§a≫§b" + effect.getDisplay());
                }
            }
            if (victim instanceof PlayerData playerData) {
                if (playerData.getSetting().isDamageLog()) {
                    playerData.sendMessage("§c≪§b" + effect.getDisplay());
                }
            }
            return;
        }

        if (victim.hasEffect(CounterBlade.Effect)) {
            makeDamage(victim, attacker, damageEffect, DamageOrigin.ATK, multiply);
            victim.removeEffectStack(CounterBlade.Effect, 1);
            victim.addEffectStack("DrawBlade", 1);
            SomSound.Blade.play(victim);
            if (attacker instanceof PlayerData playerData) {
                if (playerData.getSetting().isDamageLog()) {
                    playerData.sendMessage("§a≫§b" + CounterBlade.Effect.getDisplay());
                }
            }
            return;
        }

        if (victim.hasEffect("CrossGuardCounter")) {
            SomEffect effect = victim.getEffect("CrossGuardCounter");
            victim.removeEffect(effect);
            SomEffect newEffect = new SomEffect("CrossGuard", "クロスガード", true, (int) effect.getDoubleData()[0]);
            newEffect.setATK(effect.getDoubleData()[1]);
            victim.addEffect(newEffect);
        }

        if (victim.hasEffect("SubzeroShield")) {
            SomEffect effect = victim.getEffect("SubzeroShield");
            double percent = effect.getDoubleData()[0];
            if (randomDouble(0, 1) < percent) {
                attacker.addEffect(SomEffect.List.Freeze.getEffect().setTime((int) effect.getDoubleData()[1]));
            }
        }

        if (victim.hasEffect("MagicShield")) {
            victim.addMana(victim.getMaxMana() * victim.getEffect("MagicShield").getDoubleData()[0]);
        }

        damage /= victim.getStatus(DamageResist);

        victim.removeHealth(damage);
        victim.addDamageEffect(damageEffect, damage);
        victim.hurt(victim.interactAblePlayers());
        if (knock > 0) {
            Location victimLocation = victim.getLocation();
            victimLocation.setY(origin.getY());
            Vector vector = VectorFromTo(victimLocation, origin, 0.5, 0.35).multiply(knock);
            victim.getLivingEntity().setVelocity(vector);
        }

        if (victim instanceof EnemyData enemyData) {
            enemyData.addHate(attacker, damage);
            enemyData.addDamage(attacker, damage);
        }

        new SomTextParticle("§c" + scale(damage) + "♥").pop(attacker.getViewers(), victim.getEyeLocation());

        String logText = (isCritical ? "§b" : "§c") + scale(damage) + " §f[" + scale(multiply*100) + "%]" + " §a[AD:" + scale(atk) + "/" + scale(def) + "]" + " §e[CRD:" + scale(criPercent*100) + "%+" + scale(criDamage) + "]" + "§b[" + damageEffect.getDisplay() + ":" + damageOrigin + "]" + " §c[" + scale(victim.getHealth()) + "/" + scale(victim.getMaxHealth()) + "]";
        if (attacker instanceof PlayerData playerData) {
            if (playerData.getSetting().isDamageLog()) {
                playerData.sendMessage("§a≫" + logText);
            }
        }

        if (victim instanceof PlayerData playerData) {
            if (playerData.getSetting().isDamageLog()) {
                playerData.sendMessage("§c≫" + logText);
            }
        }
    }
}
