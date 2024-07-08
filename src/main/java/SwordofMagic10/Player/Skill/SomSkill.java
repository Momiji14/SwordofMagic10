package SwordofMagic10.Player.Skill;

import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.PlayerData;

import static SwordofMagic10.Component.Function.decoLore;
import static SwordofMagic10.Component.Function.scale;

public abstract class SomSkill extends SkillData {

    protected final PlayerData playerData;
    private int currentStack = 0;

    public SomSkill(PlayerData playerData) {
        this.playerData = playerData;
    }

    public SomEntity getPlayerData() {
        return playerData;
    }

    public int getCurrentStack() {
        return currentStack;
    }

    public void setCurrentStack(int currentStack) {
        this.currentStack = currentStack;
    }

    public void addCurrentStack(int currentStack) {
        this.currentStack += currentStack;
    }

    @Override
    public void setStack(int stack) {
        super.setStack(stack);
        currentStack = stack;
    }

    public double getParameter(SkillParameterType parameterType) {
        double value = super.getParameter(parameterType);
        switch (parameterType) {
            case Damage, HeadDamage -> value *= 1 + (Math.min(playerData.getLevelSync(), playerData.getClasses().getLevel(getOwnerClass())-1)) * 0.01;
        }
        return value;
    }

    @Override
    public int getCastTime() {
        return (int) Math.ceil(super.getCastTime() * (1.0/playerData.getStatus(StatusType.CastTime)));
    }

    @Override
    public int getRigidTime() {
        return (int) Math.ceil(super.getRigidTime() * (1.0/playerData.getStatus(StatusType.RigidTime)));
    }

    @Override
    public int getCoolTime() {
        return (int) Math.ceil(super.getCoolTime() * (1.0/playerData.getStatus(StatusType.CoolTime)));
    }

    public int calcCoolTime(int coolTime) {
        return (int) Math.ceil(coolTime * 20 * (1.0/playerData.getStatus(StatusType.CoolTime)));
    }


    public boolean cast() {
        return true;
    }
    public void castFirstTick() {}
    public abstract String active();

    public CustomItemStack viewItem(int level) {
        CustomItemStack item = new CustomItemStack(getIcon());
        item.setDisplay(getDisplay());
        item.addLore(getLore());
        item.addSeparator("スキル情報");
        for (SkillParameterType type : SkillParameterType.values()) {
            double value = getParameter(type);
            if (value != 0) {
                item.addLore(decoLore(type.getDisplay()) + type.getPrefix() + scale(value * type.getDisplayMultiply(), type.getDigit()) + type.getSuffix());
            }
        }
        for (StatusType statusType : StatusType.values()) {
            double value = getStatus(statusType);
            if (value != 0) {
                item.addLore(decoLore(statusType.getDisplay()) + scale(value*100, true) + "%");
            }
        }
        item.addLore(decoLore("消費マナ") + scale(getManaCost(level)));
        item.addLore(decoLore("詠唱時間") + getCastTime()/20f + "秒");
        item.addLore(decoLore("再使用時間") + getCoolTime()/20f + "秒");
        item.addLore(decoLore("硬直時間") + getRigidTime()/20f + "秒");
        return item;
    }

}
