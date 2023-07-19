package SwordofMagic10.Component;

import SwordofMagic10.Command.Developer.MobClear;
import SwordofMagic10.DataBase.QuestDataLoader;
import SwordofMagic10.DataBase.ShopDataLoader;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Quest.QuestPassItem;
import SwordofMagic10.Player.Quest.QuestTalk;
import SwordofMagic10.Player.Quest.SomQuest;
import SwordofMagic10.Player.QuickGUI;
import SwordofMagic10.Player.Shop.ShopData;
import SwordofMagic10.Player.Skill.SomSkill;
import SwordofMagic10.Player.Tutorial;
import SwordofMagic10.SomCore;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        PlayerData playerData = PlayerData.get((Player) event.getWhoClicked());
        if (playerData.isPlayMode()) {
            event.setCancelled(true);
            if (playerData.isInteraction()) {
                if (!playerData.isLoading() && playerData.isClickAbleInventory()) {
                    playerData.setClickAbleInventory(false);
                    SomTask.delay(() -> {
                        playerData.getInventoryViewer().clickInventory(event);
                        QuickGUI.onClickInventory(event);
                        playerData.setClickAbleInventory(true);
                    }, 1);
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
                guiManager.close(event);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.isInteraction()) {
            if (playerData.isPlayMode()) {
                if (event.getHand() == EquipmentSlot.HAND) {
                    Action action = event.getAction();
                    if (!playerData.getGatheringMenu().isJoin()) {
                        event.setCancelled(true);
                        if (action.isRightClick()) {
                            int slot = player.getInventory().getHeldItemSlot();
                            if (player.isSneaking()) slot += 9;
                            if (!playerData.getSetting().isQuickCast()) {
                                usePallet(playerData, slot);
                            }
                        } else if (action.isLeftClick()) {
                            if (playerData.getSkillManager().isCastable() && action.isLeftClick()) {
                                playerData.getSkillManager().normalAttack();
                            }
                        }
                    }
                }
            }
        } else event.setCancelled(true);
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.isInteraction()) {
            Entity entity = event.getRightClicked();
            if (playerData.isPlayMode()) {
                event.setCancelled(true);
                if (event.getHand() == EquipmentSlot.HAND) {
                    String entityName = uncolored(entity.getName());
                    SomTask.run(() -> {
                        if (playerData.getClasses().getMainClass() == null) {
                            playerData.getClasses().open();
                            return;
                        }
                        boolean trigger = true;
                        for (SomQuest somQuest : playerData.getQuestMenu().getQuests().values()) {
                            if (somQuest.getNowPhase() instanceof QuestPassItem questPassItem) {
                                if (questPassItem.check(playerData) && entityName.equals(questPassItem.getHandler())) {
                                    questPassItem.passItem(playerData);
                                    trigger = false;
                                }
                            } else if (somQuest.getNowPhase() instanceof QuestTalk questTalk) {
                                if (entityName.equals(questTalk.getHandler())) {
                                    questTalk.talk(playerData);
                                    trigger = false;
                                }
                            }
                        }
                        if (trigger) {
                            switch (entityName) {
                                case "転職神官" -> playerData.getClasses().open();
                                case "ダンジョン入場受付" -> playerData.getDungeonMenu().open();
                                case "武器支給" -> QuickGUI.WeaponSupply(player);
                                case "買取屋" -> playerData.getSellManager().open();
                                case "アイテム精錬" -> playerData.getUpgradeMenu().open();
                                case "アイテム強化" -> playerData.getEnhanceMenu().open();
                                case "アイテム昇級" -> playerData.getTierMenu().open();
                                case "品質変更" -> playerData.getQualityMenu().open();
                                case "ルーン装着" -> playerData.getRuneMenu().open();
                                case "労働者管理" -> playerData.getGatheringMenu().open();
                                case "制作加工" -> playerData.getProduce().open();

                                default -> {
                                    if (ShopDataLoader.getComplete().contains(entityName)) {
                                        ShopData shopData = ShopDataLoader.getShopData(entityName);
                                        playerData.getShopManager().open(shopData);
                                    }
                                    if (QuestDataLoader.getHandler().containsKey(entityName)) {
                                        playerData.getQuestMenu().openGUIQuest(QuestDataLoader.getHandler().get(entityName));
                                    }
                                }
                            }
                        }
                    });
                }
            }
        } else event.setCancelled(true);
    }

    @EventHandler
    public void onFlightToggle(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.isLocationLock()) event.setCancelled(true);
    }

    @EventHandler
    public void onToolChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.isPlayMode()) {
            if (!playerData.getGatheringMenu().isJoin()) {
                int slot = event.getNewSlot();
                if (playerData.getSetting().isQuickCast() && slot < playerData.getPalletMenu().getPallet().length / 2) {
                    event.setCancelled(true);
                    player.getInventory().setHeldItemSlot(8);
                    if (player.isSneaking()) slot += 9;
                    usePallet(playerData, slot);
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
            case 7, 16 -> playerData.evasion();
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        SomTask.run(() -> {
            Location to = event.getTo();
            Location from = event.getFrom();
            Player player = event.getPlayer();
            PlayerData playerData = PlayerData.get(player);
            if (to.distance(from) > 0.001) {
                //playerData.setVelocity(to.toVector().subtract(from.toVector()));
                if (playerData.isPlayMode() && !playerData.isLoading()) {
                    if (playerData.isLocationLock() || playerData.isDeath()) {
                        event.setCancelled(true);
                    }
                }
            }
        });
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
        playerData.evasion();
    }

    @EventHandler
    public void onSneakToggle(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.isPlayMode()) {
            SomTask.delay(() -> playerData.getInventoryViewer().updateBar(true), 1);
            if (!player.isSneaking()) {
                if (playerData.getDungeonMenu().isInDungeon()) {
                    playerData.getDungeonMenu().getDungeon().enterBossGate(playerData);
                } else {
                    Tutorial.Gate(playerData);
                }

                if (player.getGameMode() != GameMode.CREATIVE) {
                    new BukkitRunnable() {
                        boolean walkKick = false;

                        @Override
                        public void run() {
                            CustomLocation center = playerData.getLocation().addY(0.1);
                            center.setPitch(0);
                            if (player.isSneaking()) {
                                if (SomRay.rayLocationBlock(center, 1, true).isHitBlock()) {
                                    player.setVelocity(center.getDirection().multiply(0.1).setY(0.25));
                                    walkKick = true;
                                }
                            } else {
                                if (walkKick && SomRay.rayLocationBlock(center, 0.7, true).isHitBlock()) {
                                    player.setVelocity(center.getDirection().multiply(-0.7).setY(0.7));
                                    playerData.setEvasionWait(10);
                                }
                                this.cancel();
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
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && player.isOnline()) {
            PlayerData playerData = PlayerData.get(player);
            if (event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
                playerData.removeHealth(playerData.getStatus(StatusType.MaxHealth)*0.025);
                playerData.hurt(playerData.interactAblePlayers());
            }
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && SomEntity.isSomEntity(event.getEntity())) {
            PlayerData playerData = PlayerData.get(player);
            SomEntity victim = SomEntity.getSomEntity(event.getEntity());
            if (playerData.getTargets().contains(victim)) {
                playerData.getSkillManager().normalAttack();
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.isPlayMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.isPlayMode()) {
            playerData.getMining().mining(event.getBlock());
            playerData.getLumber().lumber(event.getBlock());
            playerData.getCollect().collect(event.getBlock());
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
    public void onSave(WorldSaveEvent event) {
        for (Entity entity : event.getWorld().getEntities()) {
            MobClear.cleanOther(entity);
        }
    }
}
