package SwordofMagic10.Player.Menu;

import SwordofMagic10.Component.CustomLocation;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Entity.Damage;
import SwordofMagic10.Entity.DamageEffect;
import SwordofMagic10.Entity.Enemy.DamageOrigin;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Pet.SomPet;
import SwordofMagic10.Player.GUIManager;
import SwordofMagic10.Player.PlayerData;
import org.bukkit.entity.Mob;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PetMenu extends GUIManager {

    private final Set<SomPet> summon = new HashSet<>();
    private SomEntity lastVictim;
    private SomEntity lastAttacker;

    public PetMenu(PlayerData playerData) {
        super(playerData, "ペット管理", 1);
        SomTask.syncTimerPlayer(playerData, () -> {
            if (hasLastVictim() && lastVictim.isInvalid()) lastVictim = null;
            if (hasLastAttacker() && lastAttacker.isInvalid()) lastAttacker = null;
            summon.removeIf(pet -> {
                if (pet.isInvalid()) {
                    pet.death();
                    return true;
                } else return false;
            });
            CustomLocation ownerLocation = playerData.getEyeLocation();
            for (SomPet pet : summon) {
                if (pet.getPerson4() != SomPet.Person4.Dignified) {
                    if (pet.getLivingEntity() instanceof Mob mob) {
                        CustomLocation location = pet.getEyeLocation();
                        double distance = ownerLocation.distance(location);
                        if (distance > 48) {
                            mob.teleport(playerData.getLocation());
                        } else {
                            switch (pet.getPerson4()) {
                                case Stubborn -> {
                                    if (hasLastVictim()) {
                                        pet.setTarget(getLastVictim());
                                    }
                                }
                                case Obedient -> {
                                    if (hasLastAttacker()) {
                                        pet.setTarget(getLastAttacker());
                                    }
                                }
                                case Chattering -> {
                                    if (pet.getOwner().getLocation().distance(pet.getLocation()) > pet.getPerson3().getDistance()/2) {
                                        pet.resetTarget();
                                    }
                                }
                            }
                            if (pet.hasTarget() && pet.getTarget().isInvalid()) {
                                pet.resetTarget();
                            }
                            if (!pet.hasTarget()) {
                                pet.getPerson2().sortTarget(pet).ifPresent(pet::setTarget);
                            }
                            if (pet.hasTarget()) {
                                CustomLocation targetLocation = pet.getTarget().getEyeLocation();
                                mob.getPathfinder().moveTo(targetLocation, 2);
                                if (targetLocation.distance(location) < 2) {
                                    Damage.makeDamage(pet, pet.getTarget(), DamageEffect.None, DamageOrigin.ATK, 1);
                                }
                            } else if (distance > 2 && pet.getPerson3() != SomPet.Person3.Carefree) {
                                mob.getPathfinder().moveTo(ownerLocation.clone().add(pet.getDirection().multiply(-1)), 2);
                            }
                        }
                    }
                }
            }
        }, 10, 10);
    }

    public void summon(SomPet pet) {
        summon.add(pet);
    }

    public Set<SomPet> getSummon() {
        return summon;
    }

    public boolean hasLastVictim() {
        return lastVictim != null;
    }

    public SomEntity getLastVictim() {
        return lastVictim;
    }

    public void setLastVictim(SomEntity lastVictim) {
        this.lastVictim = lastVictim;
    }

    public boolean hasLastAttacker() {
        return lastAttacker != null;
    }

    public SomEntity getLastAttacker() {
        return lastAttacker;
    }

    public void setLastAttacker(SomEntity lastAttacker) {
        this.lastAttacker = lastAttacker;
    }

    @Override
    public void topClick(InventoryClickEvent event) {

    }

    @Override
    public void bottomClick(InventoryClickEvent event) {

    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    @Override
    public void update() {

    }
}
