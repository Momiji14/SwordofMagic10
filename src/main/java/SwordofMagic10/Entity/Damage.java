package SwordofMagic10.Entity;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTextParticle;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Pet.SomPet;
import SwordofMagic10.Player.Dungeon.Instance.DefensiveBattle;
import SwordofMagic10.Player.Menu.PetMenu;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.Entity.StatusType.*;
import static SwordofMagic10.Player.Dungeon.Instance.DefensiveBattle.DefensiveEnemy;
import static SwordofMagic10.SomCore.Log;

public class Damage {
    private static final Random random = new Random();

    public static void makeHeal(SomEntity healer, SomEntity target, double multiply) {
        makeHeal(healer, target, healer.getSPT(), multiply);
    }

    public static void makeHeal(SomEntity healer, SomEntity target, double baseHeal, double multiply) {
        double heal = baseHeal * multiply;

        if (target instanceof PlayerData playerData && playerData.getSetting().isPvPMode()) heal *= 0.1;
        target.addHealth(heal);

        new SomTextParticle("§a" + scale(heal) + "♥").pop(healer.getViewers(), target.getEyeLocation());

        String logText = scale(heal) + " §f[" + scale(multiply*100) + "%]" + " §c[" + scale(target.getStatus(Health)) + "/" + scale(target.getStatus(MaxHealth)) + "]";
        if (healer instanceof PlayerData playerData) {
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

    public static void makeDamage(SomEntity attacker, SomEntity victim, DamageEffect damageEffect, DamageOrigin damageOrigin, double multiply, double hate, double knock) {
        makeDamage(attacker, victim, damageEffect, damageOrigin, multiply, hate, knock, attacker.getLocation());
    }

    public static void makeDamage(SomEntity attacker, SomEntity victim, DamageEffect damageEffect, DamageOrigin damageOrigin, double multiply, double knock, Location origin) {
        makeDamage(attacker, victim, damageEffect, damageOrigin, multiply, 1, knock, origin);
    }
    public static void makeDamage(SomEntity attacker, SomEntity victim, DamageEffect damageEffect, DamageOrigin damageOrigin, double multiply, double hate, double knock, Location origin) {
        if (victim.isInvalid() || attacker.isInvalid()) return;

        double atk = Math.max(0, damageOrigin == DamageOrigin.ATK ? attacker.getATK() : attacker.getMAT());
        double def = Math.max(0, damageOrigin == DamageOrigin.ATK ? victim.getDEF() : victim.getMDF());
        double criRate = attacker.getStatus(Critical);
        double criResist = victim.getStatus(CriticalResist);
        double criPercent = criRate / (1+criResist*5);
        double criATK = attacker.getStatus(CriticalDamage);
        if (victim.hasEffect("Fatal") && victim.getEffect("Fatal").getOwner() == attacker) criATK += criATK * victim.getEffect("Fatal").getDoubleData()[0];

        boolean isCritical = random.nextDouble() < criPercent;
        double baseDamage = Math.max(0, (atk * atk) / (atk + def * 3.5));
        double criDamage = MinMax(baseDamage*0.1 + (criATK * criATK) / (criATK + criResist), 0, baseDamage);
        double damage = baseDamage;
        if (isCritical) damage += criDamage;

        damage *= multiply;

        if (victim.isInvincible()) {
            //とりあえず無敵って書いときます
            if (attacker instanceof PlayerData playerData) {
                if (playerData.getSetting().isDamageLog()) {
                    playerData.sendMessage("§a≫§b無敵");
                }
            }
            if (victim instanceof PlayerData playerData) {
                if (playerData.getSetting().isDamageLog()) {
                    playerData.sendMessage("§c≪§b無敵");
                }
            }
            return;
        }

        if (victim.hasEffect("CounterBlade")) {
            SomEffect effect = victim.getEffect("CounterBlade");
            makeDamage(victim, attacker, damageEffect, DamageOrigin.ATK, effect.getDoubleData()[0]);
            victim.removeEffectStack(effect, 1);

            int stackAmount = 1;
            SomAmulet.Bottle bottle = (SomAmulet.Bottle) ItemDataLoader.getItemData("二刀の願瓶");
            if(victim instanceof PlayerData player && player.hasBottle(bottle)) stackAmount *= 2;

            victim.addEffectStack("DrawBlade", stackAmount);
            SomSound.Blade.play(victim);
            damage = Math.max(0, damage - def);
        }

        if (victim.hasEffect("CrossGuardCounter")) {
            SomEffect effect = victim.getEffect("CrossGuardCounter");
            victim.removeEffect(effect);
            SomEffect newEffect = new SomEffect("CrossGuard", "クロスガード", true, (int) effect.getDoubleData()[0]);
            newEffect.setMultiply(ATK, effect.getDoubleData()[1]);
            SomSound.Blade.play(victim);
            victim.addEffect(newEffect, victim);
        }

        if (victim.hasEffect("SubzeroShield")) {
            SomEffect effect = victim.getEffect("SubzeroShield");
            double percent = effect.getDoubleData()[0];
            if (randomDouble(0, 1) < percent) {
                attacker.addEffect(SomEffect.List.Freeze.getEffect().setTime((int) effect.getDoubleData()[1]), victim);
            }
        }

        if (victim.hasEffect("MagicShield")) {
            victim.addMana(victim.getMaxMana() * victim.getEffect("MagicShield").getDoubleData()[0]);
            SomSound.Porn.play(victim);
        }

        if (attacker.hasEffect("Nachash")) {
            attacker.addHealth(attacker.getEffect("Nachash").getOwner().getSPT()/5);
        }

        damage *= attacker.getStatus(DamageMultiply) / victim.getStatus(DamageResist);

        if (attacker.hasEffect("Preparation")){
            attacker.removeEffect("Preparation");
            attacker.updateStatus();
        }
        if (victim.hasEffect("Preparation") && victim.getEffect("Preparation").getDoubleData()[0] < 0){
            victim.removeEffect("Preparation");
            victim.updateStatus();
        }

        if (attacker instanceof PlayerData && victim instanceof PlayerData) {
            damage *= 0.1;
        }

        victim.removeHealth(damage);
        victim.addDamageEffect(damageEffect, damage);
        victim.hurt(victim.interactAblePlayers());
        if (knock != 0 && !victim.hasEffect("PainBarrier") && !victim.hasEffect(DefensiveEnemy)) {
            origin.setPitch(0);
            Vector vector = origin.getDirection().normalize().setY(0.1).multiply(knock);
            victim.getLivingEntity().setVelocity(vector);
        }

        if (victim instanceof EnemyData enemyData) {
            enemyData.addHate(attacker, damage * attacker.getStatus(Hate)*0.01 * hate);
            enemyData.addDamage(attacker, damage);
        }
        if (victim instanceof SomPet pet) {
            if (pet.getPerson4() == SomPet.Person4.Random) {
                pet.setTarget(attacker);
            }
        }

        if (!victim.hasEffect(DefensiveEnemy)) {
            new SomTextParticle("§c" + scale(damage) + "♥").pop(attacker.getViewers(), victim.getEyeLocation());
        }

        String logText = (isCritical ? "§b" : "§c") + scale(damage) + " §f[" + scale(multiply*100) + "%]" + " §a[AD:" + scale(atk) + "/" + scale(def) + "]" + " §e[CRD:" + scale(criPercent*100) + "%+" + scale(criDamage) + "]" + "§b[" + damageEffect.getDisplay() + ":" + damageOrigin + "]" + " §c[" + scale(victim.getHealth()) + "/" + scale(victim.getMaxHealth()) + "]";
        if (attacker instanceof PlayerData playerData) {
            playerData.getPetMenu().setLastVictim(victim);
            if (playerData.getSetting().isDamageLog()) {
                playerData.sendMessage("§a≫" + logText);
            }
        }

        if (victim instanceof PlayerData playerData) {
            playerData.getPetMenu().setLastAttacker(attacker);
            if (playerData.getSetting().isDamageLog()) {
                playerData.sendMessage("§c≫" + logText);
            }
        }
    }
}
