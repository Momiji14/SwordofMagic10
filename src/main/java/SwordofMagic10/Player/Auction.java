package SwordofMagic10.Player;

import SwordofMagic10.Component.*;
import SwordofMagic10.Item.SomItem;
import SwordofMagic10.Item.SomItemStack;
import SwordofMagic10.SomCore;
import com.github.jasync.sql.db.RowData;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static SwordofMagic10.Component.Config.DateFormat;
import static SwordofMagic10.Component.Function.*;
import static SwordofMagic10.SomCore.Log;

public class Auction {
    public static SomItemStack Stack;
    public static boolean Auctioning = false;
    public static boolean IsOwnerServer = false;
    public static String OwnerName;
    public static PlayerData Owner;

    public static boolean IsBetterServer = false;
    public static PlayerData Better;
    public static String BetterName;
    public static int Mel;
    private static int Time = 0;
    public static List<String> TextLine = new ArrayList<>();

    private static final String Table = "Auction";
    private static final String TableFlag = "AuctionFlag";
    private static String State = "None";

    public static void setState(String state) {
        State = state;
        SomSQL.setSql(TableFlag, "Server", SomCore.ID, "State", State);
    }

    public static void startCheck() {
        if (SomSQL.existSql(Table, "ID", "0")) {
            Time = SomSQL.getInt(Table, "Time");
            if (Time <= 0) return;
            Auctioning = true;
            IsOwnerServer = SomCore.ID.equals(SomSQL.getString(Table, "OwnerServer"));
            OwnerName = SomSQL.getString(Table, "OwnerName");
            Stack = SomItemStack.fromJson(SomSQL.getString(Table, "ItemStack"));
            Mel = SomSQL.getInt(Table, "Mel");
            broadcast(SomText.create(OwnerName + "§aが").add(Stack.toSomText()).add("§aを§eオークション§aに§e" + SomSQL.getInt(Table, "Mel") + "メル§aから§b出品§aしました"), SomSound.Tick);
            setState("Auctioning");
        }
    }

    public static void run() {
        setState("None");
        startCheck();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!Auctioning) {
                    startCheck();
                }
                if (Auctioning && State.equals("Auctioning")) {
                    if (Time > 0) {
                        IsBetterServer = SomCore.ID.equals(SomSQL.getString(Table, "BetterServer"));
                        String newBetter = SomSQL.getString(Table, "BetterName");
                        if (BetterName != null && newBetter == null) {
                            broadcast("§e入札者§aが§c入札違反§aしたため§eオークション§aの§b入札§aを取り消します", SomSound.Tick);
                        }
                        BetterName = newBetter;

                        int newMel = SomSQL.getInt(Table, "Mel");
                        if (newMel != Mel) {
                            broadcast(BetterName + "§aが§e" + newMel + "メル§aで§b入札§aしました", SomSound.Tick);
                            Mel = newMel;
                        }

                        List<String> textLine = new ArrayList<>();
                        textLine.add(decoText("オークション"));
                        textLine.add(decoLore("出品者") + OwnerName);
                        textLine.add(decoLore("出品物") + Stack.getItem().getColorDisplay() + "T" + Stack.getItem().getTier() + "§ex" + Stack.getAmount());
                        if (BetterName != null) {
                            textLine.add(decoLore("入札額") + Mel + "メル");
                            textLine.add(decoLore("入札者") + BetterName);
                        } else {
                            textLine.add(decoLore("入札額") + "§7未入札");
                            textLine.add(decoLore("入札者") + "§7未入札");
                        }
                        textLine.add(decoLore("残り時間") + Time + "秒");
                        TextLine = textLine;

                        if (IsOwnerServer) {
                            if (!Owner.getItemInventory().has(Stack)) {
                                SomSQL.setSql(Table, "Error", "§e出品者§aが§e出品物§aを§c紛失§aしたため§eオークション§aを終了します");
                            } else if (!Owner.isOnline()) {
                                SomSQL.setSql(Table, "Error","§e出品者§aが§c失踪§aしたため§eオークション§aを終了します");
                            }
                        }
                        if (IsBetterServer) {
                            if (Better != null && Better.getMel() < Mel) {
                                resetBetter();
                                Mel = SomSQL.getInt(Table, "StartMel");
                            } else if (Better != null && !Better.isOnline()) {
                                resetBetter();
                                Mel = SomSQL.getInt(Table, "StartMel");
                            }
                        }
                    } else {
                        String error = SomSQL.getString(Table, "Error");
                        if (error != null) {
                            broadcast(error, SomSound.Tick);
                        } else if (SomSQL.getString(Table, "BetterServer") != null) {
                            int reqMel2 = (int) Math.ceil(Mel * 0.05);
                            broadcast(SomText.create(BetterName + "§aが").add(Stack.toSomText()).add("§aを§e" + Mel + "メル§aで§c落札§aしました"));
                            if (IsOwnerServer) {
                                Owner.getItemInventory().remove(Stack);
                                Owner.addMel(Mel - reqMel2);
                                Owner.sendMessage("§c[取引手数料]§e" + reqMel2 + "メル");
                                Owner.getInventoryViewer().updateBar();
                            }
                            if (IsBetterServer) {
                                Better.getItemInventory().add(Stack);
                                Better.removeMel(Mel);
                                Better.getInventoryViewer().updateBar();
                            }
                        } else {
                            broadcast("§e入札者§aが現れなかったため§eオークション§aが§c終了§aしました", SomSound.Tick);
                        }

                        Owner = null;
                        OwnerName = null;

                        IsBetterServer = false;
                        Better = null;
                        BetterName = null;

                        Auctioning = false;
                        Stack = null;
                        Time = 0;
                        Mel = 0;
                        setState("Ending");
                    }
                    if (IsOwnerServer) {
                        Time--;
                        SomSQL.setSql(Table, "Time", Time);
                    } else {
                        Time = SomSQL.getInt(Table, "Time");
                    }
                }

                if (IsOwnerServer) {
                    for (RowData objects : SomSQL.getSqlList(TableFlag, "State")) {
                        if (!"Ending".equals(objects.getString("State"))) {
                            return;
                        }
                    }
                    SomSQL.delete(Table);
                    IsOwnerServer = false;
                }
            }
        }.runTaskTimerAsynchronously(SomCore.plugin(), 20, 20);
    }

    public static void resetBetter() {
        SomSQL.setSql(Table, "BetterServer", null);
        SomSQL.setSql(Table, "Better", null);
        SomSQL.setSql(Table, "BetterName", null);
    }

    public static void auctionCommand(PlayerData playerData, String[] args) {
        if (args.length >= 2) {
            SomTask.run(() -> {
                try {
                    String type = args[0];
                    int index = Integer.parseInt(args[1]);
                    if (index > -1 && type.equalsIgnoreCase("sell") && playerData.getItemInventory().getInventory().size() > index) {
                        if (!SomSQL.existSql(Table, "ID", "0")) {
                            Stack = playerData.getItemInventory().getInventory().get(index).clone();
                            if (args.length >= 4) {
                                Stack.setAmount(MinMax(Integer.parseInt(args[3]), 1, Stack.getAmount()));
                            }
                            if (!playerData.getItemInventory().has(Stack) || Stack.getAmount() < 1) {
                                playerData.sendMessage("§e所持数§a以上は§b出品§aできません", SomSound.Nope);
                                return;
                            }
                            if (args.length >= 3) {
                                Mel = Integer.parseInt(args[2]);
                            } else Mel = 1;
                            if (Mel < 1) Mel = 1;
                            int reqMel = (int) Math.ceil(Mel * playerData.getRank().getCommission());
                            if (playerData.getMel() < reqMel) {
                                playerData.sendMessage("§e出品手数料§aが足りません §e[" + reqMel + "]", SomSound.Nope);
                                return;
                            }
                            Owner = playerData;
                            SomSQL.setSql(Table, "Time", 30);
                            SomSQL.setSql(Table, "OwnerServer", SomCore.ID);
                            SomSQL.setSql(Table, "Owner", Owner.getUUIDAsString());
                            SomSQL.setSql(Table, "OwnerName", Owner.getDisplayName());
                            SomSQL.setSql(Table, "ItemStack", Stack.toJson().toString());
                            SomSQL.setSql(Table, "Mel", Mel);
                            SomSQL.setSql(Table, "StartMel", Mel);
                        } else {
                            if (Auctioning) {
                                playerData.sendMessage("§aすでに§eオークション§aが開催されています", SomSound.Nope);
                            } else {
                                playerData.sendMessage("§c同期中§aです", SomSound.Nope);
                            }
                        }
                    } else if (type.equalsIgnoreCase("bet")) {
                        if (Auctioning) {
                            if (!IsOwnerServer || playerData != Owner) {
                                if (!IsBetterServer || playerData != Better) {
                                    int reqMel = (int) Math.ceil(Mel * 1.05);
                                    if (reqMel <= index) {
                                        if (playerData.getMel() >= index) {
                                            Better = playerData;
                                            SomSQL.setSql(Table, "BetterServer", SomCore.ID);
                                            SomSQL.setSql(Table, "Better", Better.getUUIDAsString());
                                            SomSQL.setSql(Table, "BetterName", Better.getDisplayName());
                                            SomSQL.setSql(Table, "Mel", index);
                                            if (Time < 10) SomSQL.setSql(Table, "Time", ZonedDateTime.now(ZoneId.systemDefault()).plusSeconds(10).format(DateFormat));
                                        } else {
                                            playerData.sendMessage("§eメル§aが足りません", SomSound.Nope);
                                        }
                                    } else {
                                        playerData.sendMessage("§e" + reqMel + "メル§a以上でないと§b入札§a出来ません", SomSound.Nope);
                                    }
                                } else {
                                    playerData.sendMessage("§aすでに§b筆頭入札者§aです", SomSound.Nope);
                                }
                            } else {
                                playerData.sendMessage("§a自身の§eオークション§aには§e入札§a出来ません", SomSound.Nope);
                            }
                        } else {
                            playerData.sendMessage("§eオークション§aが開催されていません", SomSound.Nope);
                        }
                    }
                } catch (Exception ignore) {}
            });
        } else {
            playerData.sendMessage("§e/auction sell <SlotID> [<開始金額>] [<個数>]");
            playerData.sendMessage("§e/auction bet <メル>");
        }
    }
}