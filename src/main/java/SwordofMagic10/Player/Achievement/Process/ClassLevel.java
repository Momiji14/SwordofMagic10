package SwordofMagic10.Player.Achievement.Process;

import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.PlayerData;

public class ClassLevel extends AchievementProcess {

    private final ClassType classType;
    private final int level;

    public ClassLevel(ClassType classType, int level) {
        this.classType = classType;
        this.level = level;
    }
    @Override
    public boolean isProcess(PlayerData playerData) {
        return playerData.getClasses().getLevel(classType) >= level;
    }
}
