package SwordofMagic10.Entity;

import java.util.HashMap;

import static SwordofMagic10.Component.Function.MinMax;

public interface SomStatus {

    HashMap<StatusType, Double> getStatus();

    default double getStatus(StatusType status) {
        return this.getStatus().getOrDefault(status, 0d);
    }

    default void setStatus(StatusType status, double value) {
        this.getStatus().put(status, value);
    }

    default void addStatus(StatusType status, double value) {
        this.getStatus().merge(status, value, Double::sum);
    }

    default double getMaxHealth() {
        return getStatus().getOrDefault(StatusType.MaxHealth, 0d);
    }

    default SomStatus setMaxHealth(double value) {
        getStatus().put(StatusType.MaxHealth, value);
        return this;
    }

    default double getHealth() {
        return getStatus().getOrDefault(StatusType.Health, 0d);
    }

    default void setHealth(double value) {
        getStatus().put(StatusType.Health, MinMax(value, 0, getMaxHealth()));
    }

    default void addHealth(double value) {
        setHealth(getHealth()+value);
    }

    default double getHealthPercent() {
        return getHealth() / getMaxHealth();
    }

    default double getHealthRegen() {
        return getStatus().getOrDefault(StatusType.HealthRegen, 0d);
    }

    default SomStatus setHealthRegen(double value) {
        getStatus().put(StatusType.HealthRegen, value);
        return this;
    }

    default double getMaxMana() {
        return getStatus().getOrDefault(StatusType.MaxMana, 0d);
    }

    default SomStatus setMaxMana(double value) {
        getStatus().put(StatusType.MaxMana, value);
        return this;
    }

    default double getMana() {
        return getStatus().getOrDefault(StatusType.Mana, 0d);
    }

    default void setMana(double value) {
        getStatus().put(StatusType.Mana, MinMax(value, 0, getMaxMana()));
    }

    default void addMana(double value) {
        setMana(getMana()+value);
    }

    default double getManaRegen() {
        return getStatus().getOrDefault(StatusType.ManaRegen, 0d);
    }

    default SomStatus setManaRegen(double value) {
        getStatus().put(StatusType.ManaRegen, value);
        return this;
    }

    default double getATK() {
        return getStatus().getOrDefault(StatusType.ATK, 0d);
    }

    default SomStatus setATK(double value) {
        getStatus().put(StatusType.ATK, value);
        return this;
    }

    default double getMAT() {
        return getStatus().getOrDefault(StatusType.MAT, 0d);
    }

    default SomStatus setMAT(double value) {
        getStatus().put(StatusType.MAT, value);
        return this;
    }

    default double getDEF() {
        return getStatus().getOrDefault(StatusType.DEF, 0d);
    }

    default SomStatus setDEF(double value) {
        getStatus().put(StatusType.DEF, value);
        return this;
    }

    default double getMDF() {
        return getStatus().getOrDefault(StatusType.MDF, 0d);
    }

    default SomStatus setMDF(double value) {
        getStatus().put(StatusType.MDF, value);
        return this;
    }

    default double getSPT() {
        return getStatus().getOrDefault(StatusType.SPT, 0d);
    }

    default SomStatus setSPT(double value) {
        getStatus().put(StatusType.SPT, value);
        return this;
    }

}
