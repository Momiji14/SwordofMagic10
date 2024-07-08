package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.SkillGroupLoader;
import SwordofMagic10.Entity.EquipSlot;
import SwordofMagic10.Item.*;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.Gathering.GatheringMenu;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillGroup;
import SwordofMagic10.Player.Statistics;
import com.github.jasync.sql.db.RowData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static SwordofMagic10.SomCore.Log;

public class Test implements SomCommand {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        playerData.sendMessage("viewDistance: " + playerData.getPlayer().getSendViewDistance());
        return true;
    }

    public static void test3() {
        String[] priKey = new String[]{"UUID", "Type"};
        for (RowData objects : SomSQL.getSqlList(PlayerData.Table, "UUID")) {String uuid = objects.getString("UUID");
            SomJson json = new SomJson(SomSQL.getString(PlayerData.Table, "UUID", uuid, "Json")).getSomJson("Statistics");
            String username = SomSQL.getString(PlayerData.Table, "UUID", uuid, "Username");
            try {
                for (Statistics.Type type : Statistics.Type.values()) {
                    if (json.has(type.toString())) {
                        String[] priValue = new String[]{uuid, type.toString()};
                        SomSQL.setSql(Statistics.Table, priKey, priValue, "Username", username);
                        SomSQL.setSql(Statistics.Table, priKey, priValue, "Value", json.getDouble(type.toString(), 0));
                    }
                }
                Log(username);
            } catch (Exception e) {
                e.printStackTrace();
                Log("§cError " + username);
            }
        }
    }

    public static void check13(PlayerData playerData) {
        for (RowData objects : SomSQL.getSqlList(PlayerData.Table, "UUID")) {
            String uuid = objects.getString("UUID");
            SomJson json = new SomJson(SomSQL.getString(PlayerData.Table, "UUID", uuid, "Json"));
            String username = SomSQL.getString(PlayerData.Table, "UUID", uuid, "Username");
            HashMap<EquipSlot, SomItem> equip = new HashMap<>();
            List<SomItemStack> inventory = new ArrayList<>();
            List<SomItemStack> storage = new ArrayList<>();
            for (EquipSlot equipSlot : EquipSlot.values()) {
                if (json.has("Item.Equipment." + equipSlot)) {
                    SomItem item = SomItem.fromJson(json.getSomJson("Item.Equipment." + equipSlot));
                    equip.put(equipSlot, item);
                    if (item instanceof SomEquipment equipment) {
                        if (equipment.getPlus() >= 13 || equipment.getPlus() >= 12 && equipment.getExp() == 0) {
                            playerData.sendSomText(SomText.create(username + " -> " + equipSlot + " -> ").add(equipment.toSomText()));
                            equipment.setPlus(10);
                        }
                    }
                }
            }
            for (String data : json.getList("Item.Inventory")) {
                SomItemStack stack = SomItemStack.fromJson(data);
                SomItem item = stack.getItem();
                inventory.add(stack);
                if (item instanceof SomEquipment equipment) {
                    if (equipment.getPlus() >= 13 || equipment.getPlus() >= 12 && equipment.getExp() == 0) {
                        playerData.sendSomText(SomText.create(username + " -> Inventory -> ").add(equipment.toSomText()));
                        equipment.setPlus(10);
                    }
                }
            }

            for (String data : json.getList("Item.Storage")) {
                SomItemStack stack = SomItemStack.fromJson(data);
                SomItem item = stack.getItem();
                storage.add(stack);
                if (item instanceof SomEquipment equipment) {
                    if (equipment.getPlus() >= 13 || equipment.getPlus() >= 12 && equipment.getExp() == 0) {
                        playerData.sendSomText(SomText.create(username + " -> Storage -> ").add(equipment.toSomText()));
                        equipment.setPlus(10);
                    }
                }
            }

            for (EquipSlot equipSlot : EquipSlot.values()) {
                if (equip.containsKey(equipSlot)) {
                    json.set("Item.Equipment." + equipSlot, equip.get(equipSlot).toJson());
                }
            }
            json.deleteKey("Item.Inventory");
            for (SomItemStack stack : inventory) {
                json.addArray("Item.Inventory", stack.toJson());
            }
            json.deleteKey("Item.Storage");
            for (SomItemStack stack : storage) {
                json.addArray("Item.Storage", stack.toJson());
            }
            SomSQL.setSql(PlayerData.Table, "UUID", uuid, "Json", json.toString());
        }
    }

    public static void Gathering() {
        String[] priKey = new String[]{"UUID", "Type"};
        for (RowData objects : SomSQL.getSqlList(PlayerData.Table, "UUID")) {
            String uuid = objects.getString("UUID");
            String username = SomSQL.getString(PlayerData.Table, "UUID", uuid, "Username");
            SomJson json = new SomJson(SomSQL.getString(PlayerData.Table, "UUID", uuid, "Json"));
            SomJson jsonGathering = json.getSomJson("Gathering");
            SomSQL.setSql(PlayerData.Table, "UUID", uuid, "Username", username);
            for (GatheringMenu.Type type : GatheringMenu.Type.values()) {
                String[] priValue = new String[]{uuid, type.toString()};
                SomSQL.setSql(GatheringMenu.Table, priKey, priValue, "Username", username);
                SomSQL.setSql(GatheringMenu.Table, priKey, priValue, "Level", jsonGathering.getInt(type + ".Level", 1));
                SomSQL.setSql(GatheringMenu.Table, priKey, priValue, "Exp", jsonGathering.getDouble(type + ".Exp", 0));
            }
            Log(username + ": " + uuid);
        }
    }

    public static void Classes() {
        String[] priKey = new String[]{"UUID", "Class"};
        for (RowData objects : SomSQL.getSqlList(PlayerData.Table, "UUID")) {
            String uuid = objects.getString("UUID");
            String username = SomSQL.getString(PlayerData.Table, "UUID", uuid, "Username");
            SomJson json = new SomJson(SomSQL.getString(PlayerData.Table, "UUID", uuid, "Json"));
            for (ClassType classType : ClassType.values()) {
                String[] priValue = new String[]{uuid, classType.toString()};
                String path = "Classes." + classType;
                SomSQL.setSql(Classes.Table, priKey, priValue, "Username", username);
                SomSQL.setSql(Classes.Table, priKey, priValue, "Level", json.getInt(path + ".Level", 1));
                SomSQL.setSql(Classes.Table, priKey, priValue, "Exp", json.getDouble(path + ".Exp", 0));

                SomJson skillGroup = new SomJson();
                for (int i = 1; i < Classes.UnlockSkillGroupSlot.length; i++) {
                    try {
                        if (json.has(path + ".SkillGroup.Slot-" + i)) {
                            skillGroup.set("SkillGroup.Slot-" + i, json.getString(path + ".SkillGroup.Slot-" + i));
                        }
                    } catch (Exception ignore) {}
                }
                SomSQL.setSql(Classes.Table, priKey, priValue, "SkillGroup", skillGroup.toString());

                SomJson pallet = new SomJson();
                for (int i = 0; i < 12; i++) {
                    try {
                        String key = path + ".Skill-" + i;
                        if (json.has(key)) {
                            pallet.set("Skill-" + i, json.getString(key));
                        }
                    } catch (Exception ignore) {}
                }
                for (int i = 0; i < 2; i++) {
                    try {
                        String key = path + ".Item-" + i;
                        if (json.has(key)) {
                            pallet.set("Item-" + i, SomItem.fromJson(json.getSomJson(key)).toJson().toString());
                        }
                    } catch (Exception ignore) {}
                }
                SomSQL.setSql(Classes.Table, priKey, priValue, "Pallet", pallet.toString());
            }
            Log(username + ": " + uuid);
        }
    }

    public static void GatheringLevelReduce() {
        for (RowData objects : SomSQL.getSqlList(PlayerData.Table, "UUID")) {
            String uuid = objects.getString("UUID");
            SomJson json = new SomJson(SomSQL.getString(PlayerData.Table, "UUID", uuid, "Json"));
            SomSQL.setSql(PlayerData.Table, "UUID", uuid, "Mel", json.getInt("Mel", 0));
            int level = json.getSomJson("Gathering").getInt("Produce.Level", 0);
            if (level > 20) {
                SomJson gathering = json.getSomJson("Gathering");
                gathering.set("Produce.Level", 20+(level-20)/2);
                json.set("Gathering", gathering);
            }

            HashMap<SomTool.Type, SomTool> tools = new HashMap<>();
            List<SomItemStack> inventory = new ArrayList<>();
            List<SomItemStack> storage = new ArrayList<>();
            for (SomTool.Type toolType : SomTool.Type.values()) {
                if (json.has("Item.Tool." + toolType)) {
                    SomTool tool = (SomTool) SomItem.fromJson(json.getSomJson("Item.Tool." + toolType));
                    if (tool.getLevel() > 20)  tool.setLevel(20+(tool.getLevel()-20)/2);
                    tools.put(toolType, tool);
                }
            }
            for (String data : json.getList("Item.Inventory")) {
                SomItemStack stack = SomItemStack.fromJson(data);
                if (stack.getItem() instanceof SomTool tool) {
                    if (tool.getLevel() > 20)  tool.setLevel(20+(tool.getLevel()-20)/2);
                }
                if (stack.getItem() instanceof SomAlchemyStone stone) {
                    if (stone.getLevel() > 20)  stone.setLevel(20+(stone.getLevel()-20)/2);
                }
                inventory.add(stack);
            }
            for (String data : json.getList("Item.Storage")) {
                SomItemStack stack = SomItemStack.fromJson(data);
                if (stack.getItem() instanceof SomTool tool) {
                    if (tool.getLevel() > 20)  tool.setLevel(20+(tool.getLevel()-20)/2);
                }
                if (stack.getItem() instanceof SomAlchemyStone stone) {
                    if (stone.getLevel() > 20)  stone.setLevel(20+(stone.getLevel()-20)/2);
                }
                storage.add(stack);
            }
            json.deleteKey("Item.Inventory");
            json.deleteKey("Item.Storage");
            for (SomTool.Type toolType : SomTool.Type.values()) {
                if (tools.containsKey(toolType)) {
                    json.set("Item.Tool." + toolType, tools.get(toolType).toJson());
                }
            }
            for (SomItemStack stack : inventory) {
                json.addArray("Item.Inventory", stack.toJson());
            }
            for (SomItemStack stack : storage) {
                json.addArray("Item.Storage", stack.toJson());
            }
            if (json.has("Item.AlchemyStone")) {
                SomAlchemyStone stone = (SomAlchemyStone) SomItem.fromJson(json.getSomJson("Item.AlchemyStone"));
                if (stone.getLevel() > 20)  stone.setLevel(20+(stone.getLevel()-20)/2);
                json.set("Item.AlchemyStone", stone.toJson());
            }
            SomSQL.setSql(PlayerData.Table, "UUID", uuid, "Json", json.toString());
            Log(SomSQL.getString(PlayerData.Table, "UUID", uuid, "Username"));
        }
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }
}

