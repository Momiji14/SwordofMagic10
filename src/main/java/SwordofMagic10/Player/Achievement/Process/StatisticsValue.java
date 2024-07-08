package SwordofMagic10.Player.Achievement.Process;

import SwordofMagic10.Player.Classes.ClassType;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Statistics;

import static SwordofMagic10.SomCore.Log;

public class StatisticsValue extends AchievementProcess {

    private final Statistics.Type type;
    private final double value;
    private final String than;

    public StatisticsValue(Statistics.Type type, double value, String than) {
        this.type = type;
        this.value = value;
        this.than = than;
    }
    @Override
    public boolean isProcess(PlayerData playerData) {
        switch (than) {
            case "More" -> {
                return playerData.getStatistics().get(type) >= value;
            }
            case "Less" -> {
                return playerData.getStatistics().get(type) <= value;
            }
            default -> Log("§a[AchievementProcess]§cStatisticsValueのThanが未知のTypeです -> " + than);
        }
        return false;
    }
}
