package SwordofMagic10.Command.Developer;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Command.SomTabComplete;
import SwordofMagic10.Component.SomSQL;
import SwordofMagic10.Component.SomText;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.DataBase.SkillDataLoader;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.Dungeon.DungeonDifficulty;
import SwordofMagic10.Player.ItemReceipt;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.SomCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerVote implements SomCommand, SomTabComplete {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        return false;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String name = args[0];
            if (SomSQL.existSql(PlayerData.Table, "Username", name)) {
                String uuid = SomSQL.getString(PlayerData.Table, "Username", name, "UUID");

                int maxLevel = SomSQL.getMax(Classes.Table, "UUID", uuid, "Level");
                DungeonDifficulty difficulty = DungeonDifficulty.fromLevel(maxLevel);
                int tier = Math.max(4, difficulty.index(DungeonDifficulty.Ultimate)+1);

                SomItem enhanceStone = ItemDataLoader.getItemData("精錬石").setTier(tier);
                SomItem tierStone = ItemDataLoader.getItemData("昇級石").setTier(tier);
                SomItem qualityStone = ItemDataLoader.getItemData("品質変更石").setTier(tier);

                ItemReceipt.addItem(uuid, enhanceStone, 30);
                ItemReceipt.addItem(uuid, tierStone, 1);
                ItemReceipt.addItem(uuid, qualityStone, 10);

                SomCore.globalMessageComponent(SomText.create(name + "§aが§d投票§aしました。§eアイテム受取NPC§aから§b報酬§aを受け取れます"));
            }
        }
        return false;
    }

    @Override
    public List<String> PlayerTabComplete(Player player, PlayerData playerData, Command command, String[] args) {
        return null;
    }

    @Override
    public List<String> TabComplete(CommandSender sender, Command command, String[] args) {
        return PlayerData.getComplete();
    }
}

