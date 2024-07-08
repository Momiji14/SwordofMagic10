package SwordofMagic10.Item;

import SwordofMagic10.Component.Function;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.SomStatus;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.Classes.Classes;

import java.util.HashMap;

import static SwordofMagic10.Component.Function.randomDouble;
import static SwordofMagic10.Component.Function.randomInt;
import static SwordofMagic10.Entity.StatusType.*;

public class SomRune extends SomQuality implements SomStatus, Cloneable {

    private final static int[] ReqExp = new int[Classes.MaxLevel];

    static {
        ReqExp[0] = 30;
        for (int i = 1; i < ReqExp.length; i++) {
            ReqExp[i] = (int) Math.ceil(ReqExp[i-1] * 1.1) + 400;
            if (i >= 99) ReqExp[i] *= (int) Math.pow(i, 1.2);
        }
    }

    public static int getReqExp(int level) {
        return ReqExp[Function.MinMax(level, 1, Classes.MaxLevel)-1];
    }
    public static final StatusType[] Table = {MaxHealth, MaxMana, HealthRegen, ManaRegen, ATK, MAT, DEF, MDF, SPT, Critical, CriticalDamage, CriticalResist};
    public static SomRune getRandomRune() {
        return (SomRune) ItemDataLoader.getItemData("RuneOf" + Table[randomInt(0, Table.length)]);
    }
    private HashMap<StatusType, Double> status = new HashMap<>();
    private double power;

    @Override
    public HashMap<StatusType, Double> getStatus() {
        return status;
    }

    public double getStatusLevelSync(StatusType statusType, int levelSync, int tierSync) {
        double qualityMultiply = DefenceStatus().contains(statusType) ? 1.5 : 0.5;
        return getStatus(statusType) * (1 + (Math.min(levelSync, getLevel())-1) * 0.08) * (0.82 + getPower() * 0.2) * (0.75 + getQuality()*qualityMultiply) * (1+(Math.pow(1.35, Math.min(tierSync, getTier())-1)-1));
    }

    public HashMap<StatusType, Double> getStatusLevelSync(int levelSync, int tierSync) {
        HashMap<StatusType, Double> status = new HashMap<>();
        for (StatusType statusType : StatusType.BaseStatus()) {
            status.put(statusType, getStatusLevelSync(statusType, levelSync, tierSync));
        }
        return status;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double randomPower() {
        setPower(randomDouble(0.5, 1));
        return getPower();
    }

    @Override
    public SomRune clone() {
        SomRune clone = (SomRune) super.clone();
        clone.status = new HashMap<>(status);
        clone.setLevel(getLevel());
        clone.setExp(getExp());
        clone.setQuality(getQuality());
        return clone;
    }
}
