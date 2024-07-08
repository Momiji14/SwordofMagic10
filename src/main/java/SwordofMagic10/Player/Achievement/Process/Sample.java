package SwordofMagic10.Player.Achievement.Process;

import SwordofMagic10.Player.PlayerData;

public class Sample extends AchievementProcess {
    @Override
    public boolean isProcess(PlayerData playerData) {
        return playerData.getUsername().equals("MomiNeko");
    }
}
