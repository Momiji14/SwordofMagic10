package SwordofMagic10.Player.Gathering;

import SwordofMagic10.DataBase.ItemDataLoader;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.bukkit.Material.*;
import static org.bukkit.Material.JUNGLE_WOOD;

public class Fishing {
    public static final List<GatheringTable> Table = new ArrayList<>() {{
        add(new GatheringTable(ItemDataLoader.getItemData("フグ"), 0.25));
        add(new GatheringTable(ItemDataLoader.getItemData("熱帯魚"), 0.25));
        add(new GatheringTable(ItemDataLoader.getItemData("生鮭"), 0.25));
        add(new GatheringTable(ItemDataLoader.getItemData("生鱈"), 0.25));
        add(new GatheringTable(ItemDataLoader.getItemData("昆布"), 0.25));
        add(new GatheringTable(ItemDataLoader.getItemData("精錬石"), 0.05));
    }};
}
