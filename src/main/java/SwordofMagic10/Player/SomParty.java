package SwordofMagic10.Player;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SomParty {
    private static final HashMap<String, SomParty> partyList = new HashMap<>();
    private static final HashMap<PlayerData, SomParty> inviteList = new HashMap<>();

    public static HashMap<String, SomParty> getPartyList() {
        return partyList;
    }

    private final String id;
    private final List<PlayerData> member = new ArrayList<>();
    private PlayerData leader;

    public static SomParty create(String id, PlayerData playerData) {
        playerData.sendMessage("§e" + id + "§aを§b結成§aしました", SomSound.Level);
        return new SomParty(id, playerData);
    }

    private SomParty(String id, PlayerData leader) {
        this.id = id;
        setLeader(leader);
        member.add(leader);
        leader.setParty(this);
        partyList.put(id, this);
    }

    public String getId() {
        return id;
    }

    public List<PlayerData> getMember() {
        return member;
    }

    public PlayerData getLeader() {
        return leader;
    }

    public void setLeader(PlayerData leader) {
        this.leader = leader;
    }

    public void joinPlayer(PlayerData playerData) {
        member.add(playerData);
        playerData.setParty(this);
        sendMessage(playerData.getDisplayName() + "§aが§eパーティ§aに§b参加§aしました", SomSound.Tick);
    }

    public void leavePlayer(PlayerData playerData) {
        member.remove(playerData);
        playerData.setParty(null);
        if (member.size() == 0) {
            partyList.remove(this.getId());
        } else if (leader == playerData) {
            setLeader(member.get(0));
            sendMessage(leader.getDisplayName() + "§aが§eリーダー§aになりました", SomSound.Tick);
        }
        sendMessage(playerData.getDisplayName() + "§aが§eパーティ§aを§c脱退§aしました", SomSound.Tick);
        playerData.sendMessage("§eパーティ§aを§c脱退§aしました", SomSound.Tick);
    }

    public void invitePlayer(PlayerData sender, PlayerData playerData) {
        if (member.size() < 5) {
            if (!playerData.hasParty()) {
                inviteList.put(playerData, this);
                sendMessage(sender.getDisplayName() + "§aが§r" + playerData.getDisplayName() + "§aを§eパーティ§aに§e招待§aしました", SomSound.Tick);
                playerData.sendMessage(sender.getDisplayName() + "§aから§eパーティ§aに§e招待§aされました §e/party accept", SomSound.Tick);
                SomTask.delay(() -> {
                    if (inviteList.containsKey(playerData)) {
                        inviteList.remove(playerData);
                        sendMessage(playerData.getDisplayName() + "§aへの§eパーティ§aの§e招待§aが§cタイムアウト§aしました", SomSound.Tick);
                        playerData.sendMessage(sender.getDisplayName() + "§aからの§eパーティ§aの§e招待§aが§cタイムアウト§aしました", SomSound.Tick);
                    }
                }, 20 * 60);
            } else {
                sender.sendMessage(playerData.getDisplayName() + "§aはすでに§eパーティ§aに§b参加§aしています", SomSound.Nope);
            }
        } else {
            sender.sendMessage("§eパーティ§aが§c満員§aです", SomSound.Nope);
        }
    }


    public static boolean hasInvite(PlayerData playerData) {
        return inviteList.containsKey(playerData);
    }

    public static void inviteAccept(PlayerData playerData) {
        if (inviteList.containsKey(playerData)) {
            SomParty party = inviteList.get(playerData);
            if (party.getMember().size() < 5) {
                party.joinPlayer(playerData);
            } else {
                playerData.sendMessage("§eパーティ§aが§c満員§aです", SomSound.Nope);
            }
            inviteList.remove(playerData);
        } else {
            playerData.sendMessage("§e招待§aされている§eパーティ§aがありません", SomSound.Nope);
        }
    }

    public void sendMessage(String message) {
        sendMessage(message, null);
    }

    public void sendMessage(String message, SomSound sound) {
        for (PlayerData playerData : member) {
            playerData.sendMessage(message, sound);
        }
    }
}
