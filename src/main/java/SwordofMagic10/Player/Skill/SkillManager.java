package SwordofMagic10.Player.Skill;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.DataBase.SkillDataLoader;
import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static SwordofMagic10.Component.Function.MinMax;
import static SwordofMagic10.SomCore.Log;

public class SkillManager {

    private static final int tick = 5;

    private final PlayerData playerData;
    private boolean castable = true;
    private boolean rigid = false;
    public SkillManager(PlayerData playerData) {
        this.playerData = playerData;
        SomTask.timerPlayer(playerData, () -> {
            boolean update = !coolTime.isEmpty();
            taskTick();
            if (update) playerData.getInventoryViewer().updateBar();
        }, 20, tick);
    }

    public void taskTick() {
        for (SomSkill skill : coolTime.keySet()) {
            coolTime.merge(skill, -tick, Integer::sum);
        }
        coolTime.entrySet().removeIf(entry -> {
            SomSkill skill = entry.getKey();
            int coolTime = entry.getValue();
            if (coolTime <= 0) {
                skill.setCurrentStack(skill.getStack());
                return true;
            } else return false;
        });
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    private int castTick;
    private final HashMap<String, SomSkill> instance = new HashMap<>();
    private final ConcurrentHashMap<SomSkill, Integer> coolTime = new ConcurrentHashMap<>();

    public int getCoolTime(SomSkill skill) {
        return coolTime.getOrDefault(skill, 0);
    }

    public ConcurrentHashMap<SomSkill, Integer> getCoolTime() {
        return coolTime;
    }

    public boolean isCoolTime(SomSkill skill) {
        return getCoolTime(skill) > 0;
    }

    public void setCoolTime(SomSkill skill) {
        setCoolTime(skill, skill.getCoolTime());
    }

    public void setCoolTime(SomSkill skill, int coolTime) {
        synchronized (this.coolTime) {
            this.coolTime.put(skill, coolTime);
        }
    }

    public SomSkill getSkill(String skillId) {
        try {
            if (!instance.containsKey(skillId)) {
                Class<?> skillClass = Class.forName("SwordofMagic10.Player.Skill.Process." + skillId);
                Constructor<?> constructor = skillClass.getConstructor(PlayerData.class);
                SomSkill skill = (SomSkill) constructor.newInstance(playerData);
                SkillData skillData = SkillDataLoader.getSkillData(skillId);
                skill.setId(skillData.getId());
                skill.setDisplay(skillData.getDisplay());
                skill.setIcon(skillData.getIcon());
                skill.setLore(skillData.getLore());
                skill.setGroup(skillData.getGroup());
                skill.setStack(skillData.getStack());
                skill.setManaCost(skillData.getManaCost(0));
                skill.setCastTime(skillData.getCastTime());
                skill.setRigidTime(skillData.getRigidTime());
                skill.setCoolTime(skillData.getCoolTime());
                skill.setParameter(skillData.getParameter());
                skillData.getStatus().forEach((skill::setStatus));
                instance.put(skillId, skill);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return instance.get(skillId);
    }

    private SomSkill castSkill;
    private double skillCastProgress = -1;

    public SomSkill getCastSkill() {
        return castSkill;
    }

    public double getSkillCastProgress() {
        return skillCastProgress;
    }

    public boolean isCastable() {
        return castable;
    }

    public void cast(String skill) {
        cast(getSkill(skill));
    }

    public void cast(int slot) {
        cast(playerData.getPalletMenu().getPallet()[slot]);
    }

    public void clearCoolTime() {
       getCoolTime().keySet().forEach(key -> playerData.getSkillManager().setCoolTime(key, 1));
    }

    public synchronized void cast(SomSkill skill) {
        if (playerData.sendMessageIsSomReload()) return;
        if (playerData.sendMessageIsAFK()) return;
        boolean log = playerData.getSetting().isSkillLog();
        if (playerData.getEquipment(EquipSlot.MainHand) == null) {
            if (log) playerData.sendMessage("§e武器§aを§e装備§aしていないため§c攻撃§aできません", SomSound.Nope);
            return;
        }
        if (playerData.isSilence(skill.getId())) {
            if (log) playerData.sendMessage("§c沈黙状態§aです", SomSound.Nope);
            return;
        }
        if (playerData.isDeath()) {
            if (log) playerData.sendMessage("§c死亡状態§aです", SomSound.Nope);
            return;
        }
        if (!rigid) {
            if (castable) {
                double mana = skill.getManaCost(playerData.getLevel());
                if (playerData.getMana() >= mana) {
                    if (skill.getCurrentStack() > 0) {
                        skillCastProgress = skill.getCastTime() > 0 ? 0 : 1;
                        castSkill = skill;
                        castable = false;
                        SomTask.run(() -> {
                            castTick = 0;
                            BossBar bossBar = Bukkit.createBossBar("§e" + skill.getDisplay(), BarColor.YELLOW, BarStyle.SOLID);
                            bossBar.addPlayer(playerData.getPlayer());
                            bossBar.setVisible(true);
                            if (skill.getCastTime() > 0) {
                                SomSound.Cast.play(playerData.getViewers(), playerData.getSoundLocation());
                                bossBar.setProgress(1f);
                            }
                            skill.castFirstTick();
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (skill.cast()) {
                                            castTick++;
                                            if (skill.getCastTime() > 0) {
                                                skillCastProgress = (double) castTick / skill.getCastTime();
                                                bossBar.setProgress(MinMax(skillCastProgress, 0, 1));
                                            }
                                        } else {
                                            this.cancel();
                                            playerData.sendMessage("§b" + skill.getDisplay() + "§aの§e構築§aが§c無効化§aされました", SomSound.Nope);
                                            castable = true;
                                            bossBar.removeAll();
                                            bossBar.setVisible(false);
                                            return;
                                        }
                                    } catch (Exception e) {
                                        this.cancel();
                                        playerData.sendMessage("§b" + skill.getDisplay() + "§aの§e詠唱中§aに§cエラー§aが§b発生§aしました", SomSound.Nope);
                                        error(bossBar, e);
                                    }
                                    try {
                                        if (skill.getCastTime() <= castTick) {
                                            this.cancel();
                                            playerData.removeMana(mana);
                                            bossBar.setColor(BarColor.PURPLE);
                                            bossBar.setTitle("§d" + skill.getDisplay());
                                            String activeResult = skill.active();
                                            if (activeResult != null) {
                                                playerData.sendMessage(activeResult, SomSound.Nope);
                                            }
                                            if (skill.getCoolTime() > 0) skill.addCurrentStack(-1);
                                            rigid = true;
                                            new BukkitRunnable() {
                                                int i = 0;
                                                double progress = 1;
                                                final double subtract = 1.0/skill.getRigidTime();
                                                @Override
                                                public void run() {
                                                    try {
                                                        if (skill.getRigidTime() > i) {
                                                            bossBar.setProgress(progress);
                                                            progress -= subtract;
                                                            i++;
                                                        } else {
                                                            this.cancel();
                                                            if (!isCoolTime(skill)) setCoolTime(skill);
                                                            rigid = false;
                                                            castable = true;
                                                            bossBar.setProgress(0f);
                                                            SomTask.delay(() -> {
                                                                bossBar.removeAll();
                                                                bossBar.setVisible(false);
                                                            }, 1);
                                                        }
                                                    } catch (Exception e) {
                                                        this.cancel();
                                                        playerData.sendMessage("§b" + skill.getDisplay() + "§aの§e硬直中§aに§cエラー§aが§b発生§aしました", SomSound.Nope);
                                                        error(bossBar, e);
                                                    }
                                                }
                                            }.runTaskTimerAsynchronously(SomCore.plugin(), 1, 1);
                                        }
                                    } catch (Exception e) {
                                        this.cancel();
                                        playerData.sendMessage("§b" + skill.getDisplay() + "§aの§e実行中§aに§cエラー§aが§b発生§aしました", SomSound.Nope);
                                        error(bossBar, e);
                                    }
                                }
                            }.runTaskTimerAsynchronously(SomCore.plugin(), 0, 1);
                        });
                    } else if (log) {
                        playerData.sendMessage("§b" + skill.getDisplay() + "§aは§cクールタイム中§aです", SomSound.Nope);
                    }
                } else if (log) {
                    playerData.sendMessage("§bマナ§aが足りません", SomSound.Nope);
                }
            } else if (log) {
                playerData.sendMessage("§a別の§bスキル§aが§e発動中§aです", SomSound.Nope);
            }
        } else if (log) {
            playerData.sendMessage("§c硬直中§aです", SomSound.Nope);
        }
    }

    public void error(BossBar bossBar, Exception e) {
        e.printStackTrace();
        castable = true;
        rigid = false;
        bossBar.removeAll();
        bossBar.setVisible(false);
    }

    public void normalAttack() {
        ClassType mainClass = playerData.getClasses().getMainClass();
        if (mainClass != null) {
            SkillManager skillManager = playerData.getSkillManager();
            skillManager.cast(skillManager.getSkill(mainClass.getNormalSkill()));
        }
    }
}
