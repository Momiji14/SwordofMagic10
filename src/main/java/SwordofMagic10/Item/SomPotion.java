package SwordofMagic10.Item;

import SwordofMagic10.Component.SomSound;
import SwordofMagic10.Component.SomText;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.StatusType;
import SwordofMagic10.Player.PlayerData;

import java.util.HashMap;

public class SomPotion extends SomItem {
    private final HashMap<StatusType, Double> fixed = new HashMap<>();
    private final HashMap<StatusType, Double> multiply = new HashMap<>();
    private int duration = 1800;
    private final String effectId;

    public SomPotion(String id) {
        effectId = id;
    }

    public void setFixed(StatusType statusType, double value) {
        fixed.put(statusType, value);
    }

    public double getFixed(StatusType statusType) {
        return fixed.getOrDefault(statusType, 0.0);
    }

    public HashMap<StatusType, Double> getFixed() {
        return fixed;
    }

    public void setMultiply(StatusType statusType, double value) {
        multiply.put(statusType, value);
    }

    public double getMultiply(StatusType statusType) {
        return multiply.getOrDefault(statusType, 0.0);
    }

    public HashMap<StatusType, Double> getMultiply() {
        return multiply;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void use(PlayerData playerData) {
        if (playerData.getItemInventory().has(this, 1)) {
            if (!playerData.hasEffect(effectId) || playerData.getEffect(effectId).getFormItem() != this) {
                SomEffect effect = new SomEffect(effectId, getDisplay(), true, getDuration());
                effect.setRank(SomEffect.Rank.Impossible);
                effect.setFormItem(this);
                getMultiply().forEach(effect::setMultiply);
                getFixed().forEach(effect::setFixed);
                playerData.addEffect(effect, playerData);
                playerData.getItemInventory().remove(this, 1);
                playerData.sendSomText(toSomText().add("§aを§e使用§aしました"), SomSound.Porn);
            } else {
                playerData.sendSomText(SomText.create("§aすでに").add(toSomText()).add("§aを使用しています"), SomSound.Nope);
            }
        } else {
            playerData.sendSomText(toSomText().add("§aを§e所持§aしていません"), SomSound.Nope);
        }
    }
}
