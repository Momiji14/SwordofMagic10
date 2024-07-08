package SwordofMagic10.Pet;

import SwordofMagic10.Component.*;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.EnemyData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomPlus;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.Entity.Enemy.EnemyData.summonEntity;
import static SwordofMagic10.Entity.Enemy.MobData.Tier.*;
import static SwordofMagic10.Pet.SomPet.Person2.SafeDanger;

public class SomPet extends SomItem implements SomEntity, Cloneable {

    private PlayerData owner;
    private String petName;
    private MobData mobData;
    private int level;
    private double exp;
    private Person1 person1;
    private Person2 person2;
    private Person3 person3;
    private Person4 person4;


    public PlayerData getOwner() {
        return owner;
    }

    public void setOwner(PlayerData owner) {
        this.owner = owner;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public MobData getMobData() {
        return mobData;
    }

    public void setMobData(MobData mobData) {
        if (petName == null) petName = mobData.getDisplay();
        this.mobData = mobData;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void addLevel(int addLevel) {
        int currentLevel = getLevel();
        setLevel(Function.MinMax(currentLevel + addLevel, 1, Classes.MaxLevel));
        owner.sendMessage(petName + "§aが§eLv" + getLevel() + "§aに上がりました", SomSound.Level);
    }

    public double getExp() {
        return exp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    public void addExp(double addExp) {
        if (getLevel() >= Classes.MaxLevel) {
            setExp(0);
            return;
        }
        double reqExp = Classes.getReqExp(getLevel());
        int addLevel = 0;
        double currentExp = getExp();
        currentExp += addExp;
        while (currentExp >= reqExp) {
            currentExp -= reqExp;
            addLevel++;
            int nextLevel = getLevel() + addLevel;
            if (Classes.MaxLevel > nextLevel) {
                reqExp = Classes.getReqExp(nextLevel);
            } else {
                reqExp = Double.MAX_VALUE;
            }
        }
        if (addLevel > 0) {
            addLevel(addLevel);
        }
        setExp(currentExp);

        if (owner.getSetting().isExpLog()) {
            owner.sendMessage("§a[ExpLog]" + petName + " §e+" + scale(addExp, 2));
        }
    }

    public float getExpPercent() {
        if (getLevel() >= Classes.MaxLevel) return 0f;
        return (float) (getExp() / Classes.getReqExp(getLevel()));
    }

    public Person1 getPerson1() {
        return person1;
    }

    public void setPerson1(Person1 person1) {
        this.person1 = person1;
    }

    public Person2 getPerson2() {
        return person2;
    }

    public void setPerson2(Person2 person2) {
        this.person2 = person2;
    }

    public Person3 getPerson3() {
        return person3;
    }

    public void setPerson3(Person3 person3) {
        this.person3 = person3;
    }

    public Person4 getPerson4() {
        return person4;
    }

    public void setPerson4(Person4 person4) {
        this.person4 = person4;
    }

    private LivingEntity entity;
    private SomEntity target;
    private boolean isDeath = false;
    private HashMap<StatusType, Double> petStatus = new HashMap<>();
    private HashMap<StatusType, Double> status = new HashMap<>();
    private HashMap<StatusType, Double> basicStatus = new HashMap<>();
    private ConcurrentHashMap<DamageEffect, Double> damageEffect = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, SomEffect> effect = new ConcurrentHashMap<>();
    public void summon(Location location) {
        SomTask.sync(() -> {
            if (entity != null) entity.remove();
            if (mobData != null) {
                if (petName != null) {
                    isDeath = false;
                    entity = summonEntity(mobData, location, owner.getViewers());
                    entity.setMetadata(Config.PetMetaAddress, new FixedMetadataValue(SomCore.plugin(), this));
                    entity.addScoreboardTag(Config.SomEntityTag);
                    entity.setCustomName("§e《" + petName + " Lv" + level + "》");
                    if (person4 == Person4.Dignified) entity.setAI(false);
                    updateStatus();
                    setHealth(getMaxHealth());
                    owner.getPetMenu().summon(this);
                    owner.sendMessage(petName + "§aを§b召喚§aしました", SomSound.Tick);
                } else {
                    owner.sendMessage("§e召喚§aするには§e名前§aを付けてあげてください §e/petName", SomSound.Nope);
                }
            } else {
                owner.sendMessage("§cMobDataが不明です", SomSound.Nope);
            }
        });
    }

    public boolean isSummon() {
        return entity != null && entity.isValid();
    }

    public void delete() {
        if (entity != null) {
            owner.sendMessage(petName + "§aが§eケージ§aに戻りました", SomSound.Tick);
            SomTask.sync(() -> {
                entity.remove();
                entity = null;
            });
        }
    }

    public boolean hasTarget() {
        return target != null;
    }

    public SomEntity getTarget() {
        return target;
    }

    public void setTarget(SomEntity target) {
        if (person2 == SafeDanger) return;
        this.target = target;
    }

    public void resetTarget() {
        target = null;
    }

    @Override
    public void death() {
        isDeath = true;
        delete();
    }

    public void initialize() {
        for (StatusType statusType : StatusType.PetStatus()) {
            petStatus.put(statusType, randomDouble(1, 5));
        }
        setPerson1(Person1.random());
        setPerson2(Person2.random());
        setPerson3(Person3.random());
        setPerson4(Person4.random());
    }

    public void load() {
        updateStatus();
        setHealth(getMaxHealth());
        setMana(getMaxMana());
    }

    public HashMap<StatusType, Double> getPetStatus() {
        return petStatus;
    }

    public double getPetStatus(StatusType statusType) {
        return petStatus.getOrDefault(statusType, 1.0);
    }


    public void setPetStatus(StatusType status, double value) {
        petStatus.put(status, value);
    }
    @Override
    public HashMap<StatusType, Double> getStatus() {
        return status;
    }

    @Override
    public HashMap<StatusType, Double> getBasicStatus() {
        return basicStatus;
    }

    @Override
    public String getDisplayName() {
        return petName;
    }

    @Override
    public LivingEntity getLivingEntity() {
        return entity;
    }

    @Override
    public HashMap<StatusType, Double> getBaseStatus() {
        HashMap<StatusType, Double> baseStatus = new HashMap<>();
        baseStatus.put(StatusType.MaxHealth, 50.0);
        baseStatus.put(StatusType.MaxMana, 50.0);
        baseStatus.put(StatusType.HealthRegen, 0.1);
        baseStatus.put(StatusType.ManaRegen, 0.1);
        baseStatus.put(StatusType.ATK, 10.0);
        baseStatus.put(StatusType.MAT, 10.0);
        baseStatus.put(StatusType.DEF, 10.0);
        baseStatus.put(StatusType.MDF, 10.0);
        baseStatus.put(StatusType.SPT, 10.0);
        baseStatus.put(StatusType.Critical, 3.0);
        baseStatus.put(StatusType.CriticalDamage, 3.0);
        baseStatus.put(StatusType.CriticalResist, 3.0);
        baseStatus.put(StatusType.DamageMultiply, 1.0);
        baseStatus.put(StatusType.DamageResist, 1.0);
        baseStatus.put(StatusType.Movement, 200.0);
        return baseStatus;
    }

    @Override
    public ConcurrentHashMap<DamageEffect, Double> getDamageEffect() {
        return damageEffect;
    }

    @Override
    public ConcurrentHashMap<String, SomEffect> getEffect() {
        return effect;
    }

    @Override
    public Collection<PlayerData> interactAblePlayers() {
        return owner.getMember();
    }

    @Override
    public void hurt(Collection<PlayerData> viewers) {

    }

    @Override
    public boolean isDeath() {
        return isDeath;
    }

    @Override
    public boolean isInvalid() {
        return isDeath || entity == null || !entity.isValid();
    }


    @Override
    public SomPet clone() {
        SomPet clone = (SomPet) super.clone();
        clone.isDeath = false;
        clone.entity = null;
        clone.petStatus = new HashMap<>(getPetStatus());
        clone.status = new HashMap<>(getStatus());
        clone.basicStatus = new HashMap<>(getBasicStatus());
        clone.damageEffect = new ConcurrentHashMap<>(getDamageEffect());
        clone.effect = new ConcurrentHashMap<>(getEffect());
        return clone;
    }

    public enum Person1 {
        LovePeace("平和を愛する"), //友好プレイヤーとそのペット
        Java("JE好きの"), //JEプレイヤーとそのペットのみ
        BedRock("BE好きの"), //BEプレイヤーとそのペットのみ
        Relatives("身内びいきな"), //召喚者とそのペット
        Owner("ご主人命"), //召喚者
        Selfish("自分本位な"), //自分のみ
        PutOnAnAct("外ヅラがいい"), //召喚者以外のプレイヤーとそのペット
        Kindred("同族大好き"), //友好プレイヤーのペットのみ
        ;

        private final String display;
        Person1(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }

        public Collection<SomEntity> getMember(SomPet pet) {
            Set<SomEntity> list = new HashSet<>();
            List<PlayerData> member = new ArrayList<>(pet.getOwner().getMember());
            member.removeIf(player -> player.getLocation().distance(pet.getLocation()) > pet.getPerson3().getDistance());
            switch (this) {
                case LovePeace -> {
                    for (PlayerData playerData : member) {
                        list.add(playerData);
                        list.addAll(playerData.getPetMenu().getSummon());
                    }
                }
                case Java -> {
                    for (PlayerData playerData : pet.getOwner().getMember()) {
                        if (!playerData.isBE()) {
                            list.add(playerData);
                            list.addAll(playerData.getPetMenu().getSummon());
                        }
                    }
                }
                case BedRock -> {
                    for (PlayerData playerData : pet.getOwner().getMember()) {
                        if (playerData.isBE()) {
                            list.add(playerData);
                            list.addAll(playerData.getPetMenu().getSummon());
                        }
                    }
                }
                case Relatives -> {
                    list.add(pet.getOwner());
                    list.addAll(pet.getOwner().getPetMenu().getSummon());
                }
                case Owner -> list.add(pet.getOwner());
                case Selfish -> list.add(pet);
                case PutOnAnAct -> {
                    for (PlayerData playerData : pet.getOwner().getMember()) {
                        if (playerData != pet.getOwner()) {
                            list.add(playerData);
                            list.addAll(playerData.getPetMenu().getSummon());
                        }
                    }
                }
                case Kindred -> {
                    for (PlayerData playerData : pet.getOwner().getMember()) {
                        list.addAll(playerData.getPetMenu().getSummon());
                    }
                }
            }
            return list;
        }

        public static Person1 random() {
            return Person1.values()[randomInt(0, Person1.values().length)];
        }

        public static List<String> getComplete() {
            List<String> complete = new ArrayList<>();
            for (Person1 value : Person1.values()) {
                complete.add(value.toString());
            }
            return complete;
        }
    }

    public enum Person2 {
        Indiscriminate("見境のない"), //召喚者地点・雑魚優先
        Mind("あれこれ気になる"), //自身地点・雑魚優先
        Excited("強敵に心躍る"), //召喚者地点・ボス優先・レベル降順
        BigLover("大物大好き"), //召喚者地点・ボス優先・レベル降順
        Knight("ナイト的に"), //召喚者地点・ボス優先・レベル降順
        LoveSweet("お菓子好き"), //自身地点・ボス優先・レベル昇順
        SeekingPower("力を求めて"), //自身地点・ボス優先・レベル昇順
        LongingHero("勇者に憧れる"), //自身地点・ボス優先・レベル降順
        LongingDemonKing("魔王に憧れる"), //自身地点・ボス優先・レベル降順
        Chew("何でも噛み砕く"), //自身地点・雑魚優先・レベル降順
        MassJustice("正義の塊"), //自身地点・雑魚優先・レベル降順
        LittleHarder("少しだけ頑張る"), //召喚者地点・雑魚のみ
        BigShot("大物狙い"), //自身地点・ボスのみ
        PierceShield("盾を貫く"), //召喚者地点・レベル降順
        FullEnergy("元気いっぱい"), //召喚者地点・レベル降順
        Merciless("容赦のない"), //自身地点・レベル降順
        LoveMoney("お金に目がない"), //自身地点・レベル降順
        MoveLittle("ちょこまか動く"), //召喚者地点・レベル昇順
        Rumbling("ゴロゴロと"), //召喚者地点・レベル昇順
        SafetyFirst("安全第一"), //自身地点・自身のレベル未満のみ
        WatchOwner("主人を見守る"), //召喚者地点・召喚者のレベル未満のみ
        SafeDanger("危うきに近寄らない"), //攻撃対象なし
        ;

        private final String display;

        Person2(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }

        public Optional<SomEntity> sortTarget(SomPet pet) {
            PlayerData owner = pet.getOwner();
            List<SomEntity> targets = new ArrayList<>(owner.getTargets(pet.getPerson3().getDistance()));
            switch (this) {
                case Indiscriminate -> {
                    targets.sort(Comparator.comparingDouble(target -> target.getLocation().distance(owner.getLocation())));
                    targets.sort(Comparator.comparing(SomEntity::isBoss).reversed());
                }
                case Mind -> {
                    targets.sort(Comparator.comparingDouble(target -> target.getLocation().distance(pet.getLocation())));
                    targets.sort(Comparator.comparing(SomEntity::isBoss).reversed());
                }
                case Excited, BigLover, Knight -> {
                    targets.sort(Comparator.comparingDouble(target -> target.getLocation().distance(owner.getLocation())));
                    targets.sort(Comparator.comparing(SomEntity::isBoss).reversed());
                    targets.sort(Comparator.comparingInt(SomEntity::getLevel).reversed());
                }
                case LoveSweet, SeekingPower -> {
                    targets.sort(Comparator.comparingDouble(target -> target.getLocation().distance(pet.getLocation())));
                    targets.sort(Comparator.comparing(SomEntity::isBoss).reversed());
                    targets.sort(Comparator.comparingInt(SomEntity::getLevel));
                }
                case LongingHero, LongingDemonKing -> {
                    targets.sort(Comparator.comparingDouble(target -> target.getLocation().distance(pet.getLocation())));
                    targets.sort(Comparator.comparing(SomEntity::isBoss).reversed());
                    targets.sort(Comparator.comparingInt(SomEntity::getLevel).reversed());
                }
                case Chew, MassJustice -> {
                    targets.sort(Comparator.comparingDouble(target -> target.getLocation().distance(pet.getLocation())));
                    targets.sort(Comparator.comparing(SomEntity::isBoss));
                    targets.sort(Comparator.comparingInt(SomEntity::getLevel).reversed());
                }
                case LittleHarder -> {
                    targets.removeIf(SomEntity::isBoss);
                    targets.sort(Comparator.comparingDouble(target -> target.getLocation().distance(owner.getLocation())));
                }
                case BigShot -> {
                    targets.removeIf(target -> !target.isBoss());
                    targets.sort(Comparator.comparingDouble(target -> target.getLocation().distance(pet.getLocation())));
                }
                case PierceShield, FullEnergy -> {
                    targets.sort(Comparator.comparingDouble(target -> target.getLocation().distance(owner.getLocation())));
                    targets.sort(Comparator.comparingInt(SomEntity::getLevel).reversed());
                }
                case Merciless, LoveMoney -> {
                    targets.sort(Comparator.comparingDouble(target -> target.getLocation().distance(pet.getLocation())));
                    targets.sort(Comparator.comparingInt(SomEntity::getLevel).reversed());
                }
                case MoveLittle, Rumbling -> {
                    targets.sort(Comparator.comparingDouble(target -> target.getLocation().distance(owner.getLocation())));
                    targets.sort(Comparator.comparingInt(SomEntity::getLevel));
                }
                case SafetyFirst -> {
                    targets.removeIf(target -> target.getLevel() >= pet.getLevel());
                    targets.sort(Comparator.comparingDouble(target -> target.getLocation().distance(pet.getLocation())));
                }
                case WatchOwner -> {
                    targets.removeIf(target -> target.getLevel() >= owner.getLevel());
                    targets.sort(Comparator.comparingDouble(target -> target.getLocation().distance(owner.getLocation())));
                }
                case SafeDanger -> targets.clear();
            }
            if (targets.isEmpty()) return Optional.empty();
            return Optional.of(targets.get(0));
        }

        public static Person2 random() {
            return Person2.values()[randomInt(0, Person2.values().length)];
        }

        public static List<String> getComplete() {
            List<String> complete = new ArrayList<>();
            for (Person2 value : Person2.values()) {
                complete.add(value.toString());
            }
            return complete;
        }
    }

    public enum Person3 {
        Carefree("きままな", 40),
        Motivation("やる気マンマン", 35),
        Godlike("神の如き", 32),
        RunRunMood("ルンルン気分で", 29),
        WithSaint("光とともに", 28),
        ProtectGreen("深緑を護る", 28),
        Devilish("悪魔の如き", 26),
        Holy("光の如き", 26),
        Passion("情熱を秘めた", 25),
        Fluffy("ふわふわと", 22),
        Gutsy("ガッツのある", 21),
        Unwind("ほっこり", 20),
        Lightning("稲妻の如き", 18),
        Spoiled("甘えんぼうで", 16),
        Angel("天使の如き", 15),
        Lover("恋人みたいで", 9),
        Care("気にはしている", 8),
        Tsundere("ツンデレの", 6),
        ;

        private final String display;
        private final double distance;

        Person3(String display, double distance) {
            this.display = display;
            this.distance = distance;
        }

        public String getDisplay() {
            return display;
        }

        public double getDistance() {
            return distance;
        }

        public static Person3 random() {
            return Person3.values()[randomInt(0, Person3.values().length)];
        }

        public static List<String> getComplete() {
            List<String> complete = new ArrayList<>();
            for (Person3 value : Person3.values()) {
                complete.add(value.toString());
            }
            return complete;
        }
    }

    public enum Person4 {
        Stubborn("頑固な"), //召喚者のターゲットを優先・対象変更なし
        Clingy("べったりな"), //対象変更なし
        Random("手当たり次第の"), //攻撃を受けると対象変更する
        Obedient("従順な"), //召喚者に攻撃した相手を優先・攻撃を受けると対象変更する
        Chattering("ビビリな"), //召喚者と距離が離れると中断・対象変更なし
        Dignified("貫禄のある"), //行動不可
        ;
        private final String display;

        Person4(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }

        public static Person4 random() {
            return Person4.values()[randomInt(0, Person4.values().length)];
        }

        public static List<String> getComplete() {
            List<String> complete = new ArrayList<>();
            for (Person4 value : Person4.values()) {
                complete.add(value.toString());
            }
            return complete;
        }
    }
}
