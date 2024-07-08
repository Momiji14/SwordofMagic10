package SwordofMagic10.Player.Skill;

import SwordofMagic10.Entity.SomStatus;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.Classes.ClassType;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;

public class SkillData implements SomStatus {
    private String id;
    private String display;
    private Material icon;
    private List<String> lore;
    private final HashMap<SkillParameterType, Double> parameter = new HashMap<>();
    private final HashMap<StatusType, Double> status = new HashMap<>();
    private SkillGroup group;
    private int stack = 1;
    private double manaCost = 0;
    private int castTime = 0;
    private int rigidTime = 0;
    private int coolTime = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public double getParameter(SkillParameterType parameterType) {
        return parameter.getOrDefault(parameterType, 0.0);
    }

    public void setParameter(SkillParameterType parameterType, double value) {
        parameter.put(parameterType, value);
    }

    protected HashMap<SkillParameterType, Double> getParameter() {
        return parameter;
    }

    protected void setParameter(HashMap<SkillParameterType, Double> parameter) {
        this.parameter.putAll(parameter);
    }

    public int getStack() {
        return stack;
    }

    public void setStack(int stack) {
        this.stack = stack;
    }

    protected double getManaCost() {
        return manaCost;
    }

    public double getManaCost(int level) {
        return manaCost * (1 + (level * 0.07));
    }

    public void setManaCost(double manaCost) {
        this.manaCost = manaCost;
    }

    public SkillGroup getGroup() {
        return group;
    }

    public void setGroup(SkillGroup group) {
        this.group = group;
    }

    public ClassType getOwnerClass() {
        return group.getOwnerClass();
    }

    public int getCastTime() {
        return castTime;
    }

    public void setCastTime(int castTime) {
        this.castTime = castTime;
    }

    public int getRigidTime() {
        return rigidTime;
    }

    public void setRigidTime(int rigidTime) {
        this.rigidTime = rigidTime;
    }

    public int getCoolTime() {
        return coolTime;
    }

    public void setCoolTime(int coolTime) {
        this.coolTime = coolTime;
    }

    public double getDamage() {
        return getParameter(SkillParameterType.Damage);
    }

    public double getHeadDamage() {
        return getParameter(SkillParameterType.HeadDamage);
    }

    public double getHeal() {
        return getParameter(SkillParameterType.Heal);
    }

    public double getPercent() {
        return getParameter(SkillParameterType.Percent);
    }

    public double getReach() {
        return getParameter(SkillParameterType.Reach);
    }

    public double getRadius() {
        return getParameter(SkillParameterType.Radius);
    }

    public double getAngle() {
        return getParameter(SkillParameterType.Angle);
    }

    public int getDuration() {
        return (int) getParameter(SkillParameterType.Duration);
    }

    public int getDurationTick() {
        return (int) (getParameter(SkillParameterType.Duration)*20);
    }

    public int getDurationMillie() {
        return (int) (getParameter(SkillParameterType.Duration)*1000);
    }

    public int getCount() {
        return (int) getParameter(SkillParameterType.Count);
    }

    @Override
    public HashMap<StatusType, Double> getStatus() {
        return status;
    }
}
