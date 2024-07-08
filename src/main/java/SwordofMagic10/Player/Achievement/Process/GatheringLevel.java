package SwordofMagic10.Player.Achievement.Process;

import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.Gathering.GatheringMenu;
import SwordofMagic10.Player.PlayerData;

public class GatheringLevel extends AchievementProcess {

    private final GatheringMenu.Type type;
    private final int level;

    public GatheringLevel(GatheringMenu.Type type, int level) {
        this.type = type;
        this.level = level;
    }
    @Override
    public boolean isProcess(PlayerData playerData) {
        return playerData.getGatheringMenu().getLevel(type) >= level;
    }
}
