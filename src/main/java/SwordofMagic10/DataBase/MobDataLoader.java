package SwordofMagic10.DataBase;

import SwordofMagic10.Component.Config;
import SwordofMagic10.Component.CustomItemStack;
import SwordofMagic10.Entity.Enemy.DropData;
import SwordofMagic10.Entity.Enemy.MobData;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomItem;
import me.libraryaddict.disguise.disguisetypes.*;
import me.libraryaddict.disguise.disguisetypes.watchers.*;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Villager;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.SomCore.Log;

public class MobDataLoader {

    private static final HashMap<String, MobData> mobDataList = new HashMap<>();
    private static final List<MobData> list = new ArrayList<>();
    public static MobData getMobData(String id) {
        if (!mobDataList.containsKey(id)) Log("§c存在しないMobが参照されました -> " + id);
        return mobDataList.get(id);
    }
    public static List<MobData> getMobDataList() {
        return list;
    }
    public static List<String> getComplete() {
        List<String> complete = new ArrayList<>();
        for (MobData mobData : getMobDataList()) {
            complete.add(mobData.getId());
        }
        return complete;
    }
    public static void load() {
        mobDataList.clear();
        list.clear();
        FileConfiguration commonDrop = YamlConfiguration.loadConfiguration(new File(Config.DataBase, "CommonDrop.yml"));
        for (File file : SomLoader.dumpFile(new File(Config.DataBase, "MobData"))) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String id = SomLoader.fileId(file);
                MobData mobData = new MobData();
                mobData.setId(id);
                mobData.setDisplay(data.getString("Display"));
                mobData.setEntityType(EntityType.valueOf(data.getString("Type")));
                mobData.setSize(data.getInt("Size", 1));
                mobData.setTier(MobData.Tier.valueOf(data.getString("Tier", "Normal")));
                if (data.isSet("BossClass")) mobData.setCustomClass(data.getString("BossClass"));
                if (data.isSet("Disguise.Type")) {
                    DisguiseType type = DisguiseType.valueOf(data.getString("Disguise.Type"));
                    if (type.isMob()) {
                        MobDisguise disguise = new MobDisguise(type);
                        switch (disguise.getType()) {
                            case OCELOT -> {
                                OcelotWatcher customWatcher = new OcelotWatcher(disguise);
                                //customWatcher.setType(Ocelot.Type.valueOf(data.getString("Disguise.OcelotType", "BLACK_CAT")));
                                disguise.setWatcher(customWatcher);
                            }
                            case FOX -> {
                                FoxWatcher customWatcher = new FoxWatcher(disguise);
                                customWatcher.setType(Fox.Type.valueOf(data.getString("Disguise.FoxType", "RED")));
                                disguise.setWatcher(customWatcher);
                            }
                            case SLIME -> {
                                SlimeWatcher customWatcher = new SlimeWatcher(disguise);
                                customWatcher.setSize(data.getInt("Disguise.Size", 1));
                                disguise.setWatcher(customWatcher);
                            }
                            case PHANTOM -> {
                                PhantomWatcher customWatcher = new PhantomWatcher(disguise);
                                customWatcher.setSize(data.getInt("Disguise.Size", 1));
                                disguise.setWatcher(customWatcher);
                            }
                            case SHEEP -> {
                                SheepWatcher customWatcher = new SheepWatcher(disguise);
                                customWatcher.setColor(DyeColor.valueOf(data.getString("Disguise.Color", "WHITE")));
                                customWatcher.setSheared(data.getBoolean("Sheared", false));
                                customWatcher.setRainbowWool(data.getBoolean("Disguise.Rainbow", false));
                                disguise.setWatcher(customWatcher);
                            }
                            case HORSE -> {
                                HorseWatcher customWatcher = new HorseWatcher(disguise);
                                customWatcher.setColor(Horse.Color.valueOf(data.getString("BROWN", "BROWN")));
                                customWatcher.setStyle(Horse.Style.valueOf(data.getString("Style", "NONE")));
                                disguise.setWatcher(customWatcher);
                            }
                            case RABBIT -> {
                                RabbitWatcher customWatcher = new RabbitWatcher(disguise);
                                customWatcher.setType(RabbitType.valueOf(data.getString("RabbitType", "WHITE")));
                                disguise.setWatcher(customWatcher);
                            }
                            case FALLING_BLOCK -> {
                                FallingBlockWatcher customWatcher = new FallingBlockWatcher(disguise);
                                customWatcher.setBlock(new CustomItemStack(Material.valueOf(data.getString("Block", "STONE"))));
                                disguise.setWatcher(customWatcher);
                            }
                            case ZOMBIE_VILLAGER -> {
                                ZombieVillagerWatcher customWatcher = new ZombieVillagerWatcher(disguise);
                                customWatcher.setBiome(Villager.Type.valueOf(data.getString("VillagerType", "PLAINS")));
                                customWatcher.setProfession(Villager.Profession.valueOf(data.getString("Profession", "ARMORER")));
                                disguise.setWatcher(customWatcher);
                            }
                        }
                        FlagWatcher watcher = disguise.getWatcher();
                        if (data.isSet("Disguise.OffsetY"))
                            watcher.setYModifier((float) data.getDouble("Disguise.OffsetY"));
                        if (data.isSet("Disguise.MainHand")) {
                            String[] split = data.getString("Disguise.MainHand").split(":");
                            Material material = Material.valueOf(split[0]);
                            CustomItemStack item = new CustomItemStack(material);
                            if (split.length == 2) item.setCustomModelData(Integer.parseInt(split[1]));
                            watcher.setItemInMainHand(item);
                        }
                        if (data.isSet("Disguise.OffHand")) {
                            String[] split = data.getString("Disguise.OffHand").split(":");
                            Material material = Material.valueOf(split[0]);
                            CustomItemStack item = new CustomItemStack(material);
                            if (split.length == 2) item.setCustomModelData(Integer.parseInt(split[1]));
                            watcher.setItemInOffHand(item);
                        }
                        if (data.isSet("Disguise.Head")) {
                            String[] split = data.getString("Disguise.Head").split(":");
                            Material material = Material.valueOf(split[0]);
                            CustomItemStack item = new CustomItemStack(material);
                            if (split.length == 2) {
                                String[] color = split[1].split(",");
                                item.setLeatherArmorColor(Color.fromRGB(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                            }
                            watcher.setHelmet(item);
                        }
                        if (data.isSet("Disguise.Chest")) {
                            String[] split = data.getString("Disguise.Chest").split(":");
                            Material material = Material.valueOf(split[0]);
                            CustomItemStack item = new CustomItemStack(material);
                            if (split.length == 2) {
                                String[] color = split[1].split(",");
                                item.setLeatherArmorColor(Color.fromRGB(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                            }
                            watcher.setChestplate(item);
                        }
                        if (data.isSet("Disguise.Legs")) {
                            String[] split = data.getString("Disguise.Legs").split(":");
                            Material material = Material.valueOf(split[0]);
                            CustomItemStack item = new CustomItemStack(material);
                            if (split.length == 2) {
                                String[] color = split[1].split(",");
                                item.setLeatherArmorColor(Color.fromRGB(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                            }
                            watcher.setLeggings(item);
                        }
                        if (data.isSet("Disguise.Boots")) {
                            String[] split = data.getString("Disguise.Boots").split(":");
                            Material material = Material.valueOf(split[0]);
                            CustomItemStack item = new CustomItemStack(material);
                            if (split.length == 2) {
                                String[] color = split[1].split(",");
                                item.setLeatherArmorColor(Color.fromRGB(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                            }
                            watcher.setBoots(item);
                        }
                        disguise.setWatcher(watcher);
                        mobData.setDisguise(disguise);
                    } else if (type.isMisc()) {
                        mobData.setDisguise(new MiscDisguise(type));
                    }
                }
                if (data.isSet("Collision.Size")) mobData.setCollisionSize(data.getDouble("Collision.Size"));
                if (data.isSet("Collision.SizeY")) mobData.setCollisionSize(data.getDouble("Collision.SizeY"));
                for (StatusType type : StatusType.values()) {
                    if (data.isSet(type.toString())) {
                        mobData.setStatus(type, data.getDouble(type.toString()));
                    }
                }
                List<String> dropData = data.getStringList("Drop");
                for (String str : dropData) {
                    String[] split = str.split(",");
                    String itemId = split[0];
                    DropData drop = new DropData();
                    SomItem item = ItemDataLoader.getItemData(itemId);
                    for (int i = 1; i < split.length; i++) {
                        String[] split2 = split[i].split(":");
                        switch (split2[0]) {
                            case "Amount" -> {
                                String[] split3 = split2[1].split("-");
                                if (split3.length == 2) {
                                    drop.setMinAmount(Integer.parseInt(split3[0]));
                                    drop.setMaxAmount(Integer.parseInt(split3[1]));
                                } else {
                                    drop.setMinAmount(Integer.parseInt(split3[0]));
                                    drop.setMaxAmount(Integer.parseInt(split3[0]));
                                }
                            }
                            case "Percent" -> drop.setPercent(Double.parseDouble(split2[1]));
                        }
                    }
                    drop.setItem(item);
                    item.getHowToGet().add(mobData);
                    mobData.getDropDataList().add(drop);
                }
                mobDataList.put(id, mobData);
                list.add(mobData);
            } catch (Exception e) {
                SomLoader.error(file, e);
            }
        }
        list.sort(Comparator.comparing(MobData::getId));
        Log("§a[MobDataLoader]§b" + mobDataList.size() + "種をロードしました");
    }
}
