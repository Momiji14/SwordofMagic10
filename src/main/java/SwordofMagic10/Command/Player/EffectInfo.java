package SwordofMagic10.Command.Player;

import SwordofMagic10.Command.SomCommand;
import SwordofMagic10.Component.SomText;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.Component.Function.*;

public class EffectInfo implements SomCommand {

    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        List<SomText> message = new ArrayList<>();
        message.add(SomText.create(decoText("Effect Info")));
        for (SomEffect effect : playerData.getEffect().values()) {
            StringBuffer text = new StringBuffer(decoText(effect.getDisplay())).append("\n");
            effect.getMultiply().forEach(((statusType, value) -> text.append(decoLore(statusType.getDisplay())).append(scale(value*100, 2, true)).append("%\n")));
            effect.getFixed().forEach(((statusType, value) -> text.append(decoLore(statusType.getDisplay())).append(scale(value, 2, true)).append("\n")));
            text.append(decoLore("残り時間")).append(scale(effect.getTime(), 2)).append("秒");
            message.add(SomText.create().addHover(effect.getDisplay(), text.toString()));
        }
        playerData.sendSomText(message, null);
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }
}
