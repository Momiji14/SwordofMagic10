package SwordofMagic10.Player.Guild;

import SwordofMagic10.Component.SomSQL;
import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Player.PlayerData;

public class SomGuild {

    private static final int mel = 500000;
    private static final String Prefix = "§3[Guild]";
    private static final String Guild = "Guild";
    private static final String Member = "GuildMember";
    public static void createGuild(PlayerData playerData, String id, String guildName) {
        if (id.length() > 16 || guildName.length() > 16) {
            playerData.sendMessage(Prefix + "§eID§aおよび§e表示名§aは§e16文字以内§aである必要があります", SomSound.Nope);
            return;
        }
        if (playerData.getMel() >= mel) {
            if (!SomSQL.existSql(Guild, "ID", id)) {
                SomSQL.setSql(Guild, "ID", id, "Display", guildName);
                SomSQL.setSql(Guild, "ID", id, "Level", 1);
                SomSQL.setSql(Guild, "ID", id, "Exp", 0);
                SomSQL.setSql(Member, "UUID", playerData.getUUIDAsString(), "Guild", id);
                SomSQL.setSql(Member, "UUID", playerData.getUUIDAsString(), "Permission", Permission.Master.toString());
                playerData.sendMessage(Prefix + "§b" + id + "(" + guildName + ")§aを§e設立§aしました", SomSound.Level);
                playerData.removeMel(mel);
            } else {
                playerData.sendMessage(Prefix + "§aその§eID§aはすでに使用されています", SomSound.Nope);
            }
        } else {
            playerData.sendMessageNonMel();
        }
    }

    public enum Permission {
        Master,
        Member,
    }
}
