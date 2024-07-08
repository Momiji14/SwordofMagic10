package SwordofMagic10.Component;

import SwordofMagic10.Command.Developer.MobClear;
import SwordofMagic10.DataBase.MapDataLoader;
import SwordofMagic10.DataBase.QuestDataLoader;
import SwordofMagic10.DataBase.ShopDataLoader;
import SwordofMagic10.Entity.Enemy.Boss.Griphia;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Item.SomPotion;
import SwordofMagic10.Item.SomTool;
import SwordofMagic10.Player.*;
import SwordofMagic10.Player.Gathering.Fishing;
import SwordofMagic10.Player.Quest.QuestPassItem;
import SwordofMagic10.Player.Quest.QuestShowItem;
import SwordofMagic10.Player.Quest.QuestTalk;
import SwordofMagic10.Player.Quest.SomQuest;
import SwordofMagic10.Player.QuickGUI.*;
import SwordofMagic10.Player.Shop.ShopData;
import SwordofMagic10.Player.Skill.SomSkill;
import SwordofMagic10.SomCore;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static SwordofMagic10.Component.Function.MinMax;
import static SwordofMagic10.Component.Function.uncolored;
import static SwordofMagic10.SomCore.Log;

public class Events implements Listener {
    public Events(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        playerData.loadAsync();
        if (player.getWorld().getName().equals("world")) {
            playerData.teleport(player.getWorld().getSpawnLocation());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        playerData.saveAsync();
        if (playerData.hasParty()) {
            playerData.getParty().sendMessage(playerData.getDisplayName() + "§aが§cオフライン§aになりました", SomSound.Tick);
            playerData.getParty().leavePlayer(playerData);
        }
        SomTask.delay(playerData::delete, 5);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.getProduceGame().getTyping().isStart()) {
            SomTask.run(() -> playerData.getProduceGame().getTyping().typing(event.getMessage()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        PlayerData playerData = PlayerData.get((Player) event.getPlayer());
        if (playerData.sendMessageIsSomReload()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        PlayerData playerData = PlayerData.get((Player) event.getWhoClicked());
        if (playerData.isPlayMode()) {
            event.setCancelled(true);
            if (playerData.sendMessageIsSomReload()) return;
            if (playerData.isInteraction()) {
                if (!playerData.isLoading() && playerData.isClickAbleInventory()) {
                    playerData.setClickAbleInventory(false);
                    SomTask.run(() -> {
                        try {
                            playerData.getInventoryViewer().clickInventory(event);
                            if (!playerData.isInvalid()) {
                                for (QuickGUI quickGUI : QuickGUI.getList()) {
                                    if (event.getView().getTitle().equals(quickGUI.getTitle())) {
                                        InventoryView view = event.getView();
                                        if (view.getTopInventory() == event.getClickedInventory()) {
                                            quickGUI.topClick(playerData, event);
                                        } else if (view.getBottomInventory() == event.getClickedInventory()) {
                                            quickGUI.bottomClick(playerData, event);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            playerData.sendMessage("§cInventoryClick処理中にエラーが発生しました\n連打などしていた場合はやめてください", SomSound.Nope);
                        } finally {
                            playerData.setClickAbleInventory(true);
                        }
                    });
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        PlayerData playerData = PlayerData.get((Player) event.getPlayer());
        InventoryView view = event.getView();
        for (GUIManager guiManager : playerData.getGuiManagerList()) {
            if (view.getTitle().equalsIgnoreCase(guiManager.getDisplay())) {
                SomTask.run(() -> guiManager.close(event));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.isInteraction() || player.getInventory().getItemInMainHand().getType() == Material.CROSSBOW) {
            if (playerData.isPlayMode()) {
                Action action = event.getAction();
                if (player.getInventory().getHeldItemSlot() == 7) playerData.evasion();
                if (!playerData.getGatheringMenu().isJoin()) {
                    event.setCancelled(true);
                    if (event.getHand() == EquipmentSlot.HAND) {
                        if (action.isRightClick()) {
                            rightClickCast(playerData, player.getInventory().getHeldItemSlot());
                        } else if (action.isLeftClick()) {
                            if (playerData.getSkillManager().isCastable() && action.isLeftClick()) {
                                playerData.getSkillManager().normalAttack();
                            }
                        }
                    }
                } else {
                    if (event.hasBlock() && action.isRightClick()) {
                        event.setUseInteractedBlock(Event.Result.DENY);
                    }
                    if (playerData.getFishing().isFishing()) {
                        if (action.isLeftClick()) playerData.getFishing().comboFishing(Fishing.Combo.Centre);
                        if (action.isRightClick()) playerData.getFishing().comboFishing(Fishing.Combo.Rim);
                        event.setCancelled(true);
                        return;
                    }
                    if (event.getItem() != null && CustomItemStack.hasCustomData(event.getItem(), "ProduceGame")) {
                        playerData.getProduceGame().open();
                    }
                }
            }
        } else event.setCancelled(true);
    }

    public void rightClickCast(PlayerData playerData, int slot) {
        if (playerData.getPlayer().isSneaking()) slot += 9;
        if (!playerData.getSetting().isQuickCast()) {
            usePallet(playerData, slot);
        }
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.sendMessageIsSomReload()) {
            event.setCancelled(true);
            return;
        }
        if (event.getHand() == EquipmentSlot.HAND && playerData.isInteraction()) {
            if (playerData.isPlayMode()) {
                event.setCancelled(true);
                Entity entity = event.getRightClicked();
                String entityName = uncolored(entity.getName());
                SomTask.run(() -> {
                    try {
                        if (!playerData.getGatheringMenu().isJoin()) {
                            if (entity.getScoreboardTags().contains(Config.SomEntityTag) || (entity instanceof Player playerIns && playerIns.isOnline())) {
                                if (!playerData.getSetting().isQuickCast()) {
                                    rightClickCast(playerData, player.getInventory().getHeldItemSlot());
                                }
                            }
                        }

                        if (playerData.getClasses().isNullClass()) {
                            playerData.getClasses().open();
                            return;
                        }
                        for (SomQuest somQuest : playerData.getQuestMenu().getQuests().values()) {
                            if (somQuest.getNowPhase() instanceof QuestPassItem questPassItem) {
                                if (entityName.equals(questPassItem.getHandler())) {
                                    if (questPassItem.passItem(playerData)) return;
                                }
                            } else if (somQuest.getNowPhase() instanceof QuestShowItem questShowItem) {
                                if (entityName.equals(questShowItem.getHandler())) {
                                    if (questShowItem.showItem(playerData)) return;
                                }
                            } else if (somQuest.getNowPhase() instanceof QuestTalk questTalk) {
                                if (entityName.equals(questTalk.getHandler())) {
                                    questTalk.talk(playerData);
                                    return;
                                }
                            }
                        }

                        switch (entityName) {
                            case "転職神官" -> playerData.getClasses().open();
                            case "ダンジョン入場受付" -> {
                                if (playerData.getDungeonMenu().isInDungeon()) {
                                    if (player.isSneaking()) {
                                        playerData.getDungeonMenu().getDungeon().leavePlayer(playerData);
                                    } else {
                                        playerData.sendMessage("§eスニーク§aしながら話しかけると§cリタイア§aします", SomSound.Nope);
                                    }
                                } else {
                                    playerData.getDungeonMenu().open(false);
                                }
                            }
                            case "武器支給" -> WeaponSupply.open(playerData);
                            case "買取屋" -> playerData.getSellManager().open();
                            case "アイテム精錬" -> playerData.getUpgradeMenu().open();
                            case "アイテム強化" -> playerData.getEnhanceMenu().open();
                            case "アイテム昇級" -> playerData.getTierMenu().open();
                            case "品質変更" -> playerData.getQualityMenu().open();
                            case "ルーン装着" -> playerData.getRuneMenu().open();
                            case "ルーン合成" -> playerData.getRuneSynthesis().open();
                            case "ルーン粉砕" -> RuneCrusher.open(playerData);
                            case "ツール再制作" -> playerData.getRemakeMenu().open();
                            case "シリーズ交換" -> playerData.getSeriesMenu().open();
                            case "レベル降下" -> playerData.getLevelReduceMenu().open();
                            case "労働者管理" -> playerData.getGatheringMenu().open();
                            case "制作加工" -> playerData.getProduce().select().open();
                            case "願封願瓶" -> playerData.getAmuletMenu().open();
                            case "アミュレット交換" -> AmuletTrade.open(playerData);
                            case "マーケット" -> playerData.getMarket().open();
                            case "オーダー" -> playerData.getOrder().open();
                            case "アイテム受取" -> ItemReceipt.itemReceipt(playerData);
                            case "鍛冶屋" -> EquipmentSmith.open(playerData);

                            default -> {
                                if (ShopDataLoader.getComplete().contains(entityName)) {
                                    ShopData shopData = ShopDataLoader.getShopData(entityName);
                                    playerData.getShopManager().open(shopData);
                                } else if (QuestDataLoader.getHandler().containsKey(entityName)) {
                                    playerData.getQuestMenu().openGUIQuest(QuestDataLoader.getHandler().get(entityName));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        playerData.sendMessage("§cInteractEntity処理中にエラーが発生しました", SomSound.Nope);
                    }
                });
            }
        } else event.setCancelled(true);
    }

    @EventHandler
    public void onToolChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.sendMessageIsSomReload()) {
            event.setCancelled(true);
            return;
        }
        if (playerData.isPlayMode()) {
            if (!playerData.getGatheringMenu().isJoin()) {
                int slot = event.getNewSlot();
                if (playerData.getSetting().isQuickCast()) {
                    event.setCancelled(true);
                    player.getInventory().setHeldItemSlot(8);
                    if (player.isSneaking()) slot += 9;
                    usePallet(playerData, slot);
                    if (slot == 7) playerData.evasion();
                }
            } else {
                if (playerData.getFishing().isFishing()) {
                    SomTask.run(() -> {
                        switch (event.getNewSlot()) {
                            case 1, 7 -> playerData.getFishing().comboFishing(Fishing.Combo.Rim);
                            case 0, 8 -> playerData.getFishing().comboFishing(Fishing.Combo.Centre);
                            default -> playerData.getFishing().setFishing(false);
                        }
                    });
                    event.setCancelled(true);
                } else if (playerData.getProduceGame().getPianoTile().isStart()) {
                    SomTask.run(() -> {
                        switch (event.getNewSlot()) {
                            case 0 -> playerData.getProduceGame().getPianoTile().tap(0);
                            case 1 -> playerData.getProduceGame().getPianoTile().tap(1);
                            case 2,7 -> playerData.getProduceGame().getPianoTile().tap(2);
                            case 3,8 -> playerData.getProduceGame().getPianoTile().tap(3);
                        }
                    });
                }
            }
        }
    }

    public void usePallet(PlayerData playerData, int slot) {
        switch (slot) {
            case 0, 1, 2, 3, 4, 5, 9, 10, 11, 12, 13, 14 -> {
                if (slot > 8) slot -= 3;
                SomSkill[] pallet = playerData.getPalletMenu().getPallet();
                if (pallet != null) {
                    SomSkill skill = pallet[slot];
                    if (skill != null) {
                        if (playerData.getClasses().hasSkill(skill)) {
                            playerData.getSkillManager().cast(skill);
                        } else {
                            playerData.sendMessage("§b" + skill.getGroup().getDisplay() + "§aが§c必要§aです", SomSound.Nope);
                        }
                    }
                }
            }
            case 6, 15 -> {
                if (slot > 8) slot -= 8;
                slot -= 6;
                SomPotion[] itemPallet = playerData.getPalletMenu().getItemPallet();
                if (itemPallet != null) {
                    SomPotion item = itemPallet[slot];
                    if (item != null) {
                        item.use(playerData);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        Location from = event.getFrom();
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (to.distance(from) > 0.001) {
            if (playerData.isPlayMode() && !playerData.isLoading()) {
                if (playerData.isLocationLock()) {
                    event.setCancelled(true);
                } else {
                    SomTask.run(() -> {
                        if (!playerData.isInvalid()) {
                            if (161 < to.z() && to.z() < 170 && -20 > to.y() && (from.x() > 77 && to.x() < 77 || from.x() < 72 && to.x() > 72)) {
                                RaidEnter.open(playerData);
                            }
                            if (124 > to.x() && to.x() > 117 && -29 < to.y() && to.y() < -12 && (from.z() > 271.5 && 271.5 < to.z())) {
                                MapDataLoader.getMapData("Luoria").teleportStart(playerData);
                            }
                        }
                    });
                }
            }
        }
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.isPlayMode()) {
            if (playerData.getStatus(StatusType.Movement) <= 0) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onOffHandSwitch(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        playerData.evasion();
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (!playerData.getGatheringMenu().isJoin()) {
            SomTask.delay(() -> {
                SomSkill[] pallet = playerData.getPalletMenu().getPallet().clone();
                for (int i = 0; i < pallet.length/2; i++) {
                    playerData.getPalletMenu().setPallet(i, pallet[i+6]);
                }
                for (int i = pallet.length/2; i < pallet.length; i++) {
                    playerData.getPalletMenu().setPallet(i, pallet[i-6]);
                }
                SomPotion[] item = playerData.getPalletMenu().getItemPallet().clone();
                playerData.getPalletMenu().setItemPallet(0, item[1]);
                playerData.getPalletMenu().setItemPallet(1, item[0]);
                playerData.getInventoryViewer().updateBar(true);
            }, 1);
        }
    }

    @EventHandler
    public void onSneakToggle(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.isPlayMode()) {
            if (playerData.isDeath()) return;
            SomTask.delay(() -> playerData.getInventoryViewer().updateBar(true), 1);
            if (!player.isSneaking()) {
                if (playerData.getDungeonMenu().isInDungeon()) {
                    playerData.getDungeonMenu().getDungeon().enterBossGate(playerData);
                    Griphia.Gate(playerData);
                } else if (!playerData.isTutorialClear()) {
                    Tutorial.Gate(playerData);
                } else {
                    playerData.getMapData().useGate(playerData);
                }

                if (playerData.getSetting().isClimb() && player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                    new BukkitRunnable() {
                        boolean walkKick = false;

                        @Override
                        public void run() {
                            CustomLocation center = playerData.getLocation().addY(0.1);
                            center.setPitch(0);
                            if (player.isSneaking()) {
                                SomRay ray = SomRay.rayLocationBlock(center, 1, true);
                                if (ray.isHitBlock() && ray.getHitBlock().getType() != Material.BARRIER) {
                                    player.setVelocity(center.getDirection().multiply(0.1).setY(0.25));
                                    walkKick = true;
                                }
                            } else {
                                this.cancel();
                                if (playerData.getSetting().isWallKick()) {
                                    if (walkKick && SomRay.rayLocationBlock(center, 0.7, true).isHitBlock()) {
                                        player.setVelocity(center.getDirection().multiply(-0.7).setY(0.7));
                                        playerData.setEvasionWait(10);
                                    }
                                }
                            }
                        }
                    }.runTaskTimerAsynchronously(SomCore.plugin(), 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.getSetting().isDashOnEvasion()) {
            if (event.isSprinting()) {
                playerData.evasion();
            }
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            PlayerData playerData = PlayerData.get(player);
            playerData.getInventoryViewer().updateBar(true);
            if (!playerData.getHunting().isCoolTime()) {
                playerData.getHunting().setCoolTime(true);
                SomTask.delay(() -> playerData.getHunting().setCoolTime(false), playerData.getHunting().getCoolTime());
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        switch (event.getCause()) {
            case DROWNING -> {
                if (entity instanceof Player player && player.isOnline()) {
                    PlayerData playerData = PlayerData.get(player);
                    playerData.removeHealth(playerData.getStatus(StatusType.MaxHealth)*0.025);
                    playerData.hurt(playerData.interactAblePlayers());
                }
            }
            case CUSTOM -> event.setCancelled(true);
            case SUFFOCATION -> entity.remove();
            case WORLD_BORDER -> {
                if (entity instanceof Player player && player.isOnline()) {
                    PlayerData playerData = PlayerData.get(player);
                    playerData.death();
                } else {
                    entity.remove();
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity entity = event.getEntity();
        if (attacker instanceof Player player && SomEntity.isSomEntity(entity)) {
            PlayerData playerData = PlayerData.get(player);
            SomEntity victim = SomEntity.getSomEntity(entity);
            if (playerData.getTargets().contains(victim)) {
                playerData.getSkillManager().normalAttack();
            }
        }
        if (attacker instanceof Arrow arrow && arrow.getOwnerUniqueId() != null && entity.getScoreboardTags().contains("Hunting")) {
            Player player = Bukkit.getPlayer(arrow.getOwnerUniqueId());
            if (player != null) {
                PlayerData playerData = PlayerData.get(player);
                SomTask.run(() -> {
                    playerData.getHunting().hunting(entity);
                    SomSound.Hit.play(playerData);
                });
                arrow.remove();
                player.hideEntity(SomCore.plugin(), entity);
                if (!entity.getScoreboardTags().contains("Dead")) {
                    entity.addScoreboardTag("Dead");
                    SomTask.syncDelay(entity::remove, playerData.getHunting().getCoolTime());
                }
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.sendMessageIsSomReload()) {
            event.setCancelled(true);
            return;
        }
        if (playerData.sendMessageIsAFK()) {
            event.setCancelled(true);
            return;
        }
        FishHook hook = event.getHook();
        switch (event.getState()) {
            case FISHING -> {
                hook.setRainInfluenced(false);
                hook.setSkyInfluenced(false);
                int wait = 26;
                int plus = playerData.getGatheringMenu().getTool(SomTool.Type.Fishing).getPlus();
                if (plus > 10) {
                    wait -= (plus-10) * 5;
                }
                hook.setLureAngle(0, 360);
                hook.setWaitTime(1);
                hook.setLureTime(wait, wait);
                playerData.getFishing().waitFishing();
            }
            case BITE -> {
                hook.setWaitTime(Integer.MAX_VALUE, Integer.MAX_VALUE);
                playerData.getFishing().startFishing(hook);
                event.setCancelled(true);
            }
            case REEL_IN -> {
                playerData.getPlayer().resetTitle();
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.isPlayMode()) {
            event.setCancelled(true);
        }
    }

    private static final HashMap<Player, Integer> AFKBreakCount = new HashMap<>();
    private static final Set<Player> AFKBreakWait= new HashSet<>();
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.sendMessageIsSomReload()) {
            event.setCancelled(true);
            return;
        }
        if (playerData.isPlayMode()) {
            SomTask.run(() -> {
                if (!playerData.sendMessageIsAFK()) {
                    playerData.getMining().mining(event.getBlock());
                    playerData.getLumber().lumber(event.getBlock());
                    playerData.getCollect().collect(event.getBlock());
                    AFKBreakCount.remove(player);
                } else if (!AFKBreakWait.contains(player)) {
                    AFKBreakWait.add(player);
                    int count = AFKBreakCount.getOrDefault(player, 0) + 1;
                    AFKBreakCount.put(player, count);
                    if (count > 1) SomCore.WarnLog(playerData, "AFK Break " + event.getBlock().getType() + " " + count + "min");
                    SomTask.delay(() -> AFKBreakWait.remove(player), 20*60);
                }
            });
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.isPlayMode()) {
            if (!playerData.getGatheringMenu().isJoin()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onTame(EntityTameEvent event) {
        Player player = (Player) event.getOwner();
        event.setCancelled(true);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onSpectateTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        playerData.setLastLocation(event.getTo());
    }

    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.isPlayMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player player) {
            PlayerData playerData = PlayerData.get(player);
            if (playerData.isPlayMode()) {
                event.setCancelled(true);
            }
        } else event.setCancelled(true);
    }

    @EventHandler
    public void onTnt(TNTPrimeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        MobClear.clean(chunk);
    }
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        MobClear.clean(chunk);
    }

    @EventHandler
    public void onLoad(WorldLoadEvent event) {
        MobClear.clean(event.getWorld());
    }
}
