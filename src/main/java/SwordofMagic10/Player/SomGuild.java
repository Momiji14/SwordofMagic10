package SwordofMagic10.Player;

import SwordofMagic10.Component.SomSQL;
import SwordofMagic10.Component.SomSound;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SomGuild {

    private final String id;

    public SomGuild(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<String> getMemberData() {
        return SomSQL.getStringList("Guild", "ID", id, "Member");
    }

    public List<Member> getMember() {
        List<Member> member = new ArrayList<>();
        for (String uuid : getMemberData()) {
            member.add(Member.create(uuid));
        }
        return member;
    }

    public Member getMaster() {
        return Member.create(SomSQL.getString("Guild", "ID", id, "Master"));
    }

    public void setMaster(UUID uuid) {
        SomSQL.setSql("Guild", "ID", id, "Master", uuid.toString());
        sendMessage(PlayerData.getUsername(uuid) + "§aが§eギルドマスター§aになりました", SomSound.Tick);
    }

    public void joinPlayer(PlayerData playerData) {
        List<String> member = getMemberData();
        member.add(playerData.getUUIDAsString());
        sendMessage(playerData.getDisplayName() + "§aが§eギルド§aに§b入団§aしました", SomSound.Tick);
    }

    public void leavePlayer(PlayerData playerData) {
        List<String> member = getMemberData();
        if (getMaster().getUUID() == playerData.getUUID()) {
            setMaster(UUID.fromString(member.get(0)));
        }
        sendMessage(playerData.getDisplayName() + "§aが§eギルド§aを§c脱退§aしました", SomSound.Tick);
    }

    public void requestPlayer(PlayerData playerData) {
        sendMessage(playerData.getDisplayName() + "§aから§e入団申請§aが届きました", SomSound.Tick);
    }


    public static void requestAccept(UUID uuid) {

    }

    public void sendMessage(String message) {
        sendMessage(message, null);
    }

    public void sendMessage(String message, SomSound sound) {
        List<String> member = getMemberData();
        for (PlayerData playerData : PlayerData.getPlayerList()) {
            if (member.contains(playerData.getUUIDAsString())) {
                playerData.sendMessage(message, sound);
            }
        }
    }

    public interface Member {

        static Member create(String uuid) {
            return new Data(uuid);
        }

        UUID getUUID();
        default String getUUIDAsString() {
            return getUUID().toString();
        }

        class Data implements Member {
            private final UUID uuid;
            public Data(UUID uuid) {
                this.uuid = uuid;
            }

            public Data(String uuid) {
                this.uuid = UUID.fromString(uuid);
            }

            @Override
            public UUID getUUID() {
                return uuid;
            }
        }
    }
}
