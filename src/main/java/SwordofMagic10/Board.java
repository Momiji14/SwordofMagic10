package SwordofMagic10;

import SwordofMagic10.Component.SomSQL;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.Classes.Classes;
import SwordofMagic10.Player.Gathering.GatheringMenu;
import SwordofMagic10.Player.Statistics;
import com.github.jasync.sql.db.RowData;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static SwordofMagic10.Component.Function.decoText;
import static SwordofMagic10.Component.Function.scale;
import static SwordofMagic10.Player.Gathering.GatheringMenu.Type.*;

public class Board {
    public static final String Tag = "Board";
    private static final HashMap<GatheringMenu.Type, TextDisplay> GatheringRanking = new HashMap<>();
    private static final HashMap<ClassType, TextDisplay> ClassRanking = new HashMap<>();
    private static final HashMap<Statistics.Type, TextDisplay> StatisticsRanking = new HashMap<>();
    public static void initialize() {
        SomTask.syncTimer(Board::update, 50, 20*60);
    }

    public static void update() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getScoreboardTags().contains(Tag)) {
                    entity.remove();
                }
            }
        }
        double y = -26.75;
        double z = 92.99;
        double x = 125.5;
        for (GatheringMenu.Type type : values()) {
            GatheringRanking.put(type, SomCore.World.spawn(new Location(SomCore.World, x, y, z), TextDisplay.class));
            x += 5.0;
        }
        x = 115.5;
        for (ClassType classType : ClassType.values()) {
            ClassRanking.put(classType, SomCore.World.spawn(new Location(SomCore.World, x, y, z), TextDisplay.class));
            x -= 5.0;
        }
        x = 134.5;
        z = 67.01;
        for (Statistics.Type type : Statistics.Type.Ranking()) {
            StatisticsRanking.put(type, SomCore.World.spawn(new Location(SomCore.World, x, y, z), TextDisplay.class));
            x -= 4.0;
        }
        List<TextDisplay> list = new ArrayList<>(GatheringRanking.values());
        list.addAll(ClassRanking.values());
        for (TextDisplay display : list) {
            display.setRotation(180, 0);
        }
        list.addAll(StatisticsRanking.values());
        for (TextDisplay display : list) {
            display.setAlignment(TextDisplay.TextAlignment.LEFT);
            display.setBackgroundColor(Color.fromARGB(128, 0, 0, 0));
            display.addScoreboardTag(Tag);
        }

        SomTask.run(() -> {
            GatheringRanking.forEach((type, display) -> {
                StringBuilder builder = new StringBuilder(decoText(type.getDisplay()) + "\n§r");
                int i = 1;
                for (RowData objects : SomSQL.getSqlList(GatheringMenu.Table, "Type", type.toString(), "*", "`Level` DESC, `Exp` DESC", 0, 20)) {
                    int level = objects.getInt("Level");
                    double exp = objects.getDouble("Exp");
                    builder.append("§e").append(i).append("位§7:§r ").append(objects.getString("Username")).append("§7: §eLv").append(level).append(" §a").append(scale(exp/GatheringMenu.getReqExp(level)*100, 3)).append("%").append("\n");
                    i++;
                }
                builder.delete(builder.length()-1, builder.length());
                display.setText(builder.toString());
            });

            ClassRanking.forEach((classType, display) -> {
                StringBuilder builder = new StringBuilder(decoText(classType.getColorDisplay()) + "\n§r");
                int i = 1;
                for (RowData objects : SomSQL.getSqlList(Classes.Table, "Class", classType.toString(), "*", "`Level` DESC, `Exp` DESC", 0, 20)) {
                    int level = objects.getInt("Level");
                    double exp = objects.getDouble("Exp");
                    builder.append("§e").append(i).append("位§7:§r ").append(objects.getString("Username")).append("§7: §eLv").append(level).append(" §a").append(scale(exp/Classes.getReqExp(level)*100, 3)).append("%").append("\n");
                    i++;
                }
                builder.delete(builder.length()-1, builder.length());
                display.setText(builder.toString());
            });

            StatisticsRanking.forEach((type, display) -> {
                StringBuilder builder = new StringBuilder(decoText(type.getDisplay()) + "\n§r");
                int i = 1;
                String sort = type.isASC() ? "ASC" : "DESC";
                for (RowData objects : SomSQL.getSqlList(Statistics.Table, "Type", type.toString(), "*", "`Value` " + sort, 0, 20)) {
                    builder.append("§e").append(i).append("位§7:§r ").append(objects.getString("Username")).append("§7: §e").append(scale(objects.getDouble("Value"), type.getScale())).append("\n");
                    i++;
                }
                builder.delete(builder.length()-1, builder.length());
                display.setText(builder.toString());
            });
        });
    }
}
