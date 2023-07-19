package SwordofMagic10.Player;

import SwordofMagic10.Component.SomJson;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static SwordofMagic10.Component.Config.DateFormat;
import static SwordofMagic10.Component.Config.ServiceStart;

public class Statistics {
    private LocalDateTime playStart = LocalDateTime.now();
    private LocalDateTime lastLogout = LocalDateTime.now();
    private final HashMap<Enum, Double> enums = new HashMap<>();

    public LocalDateTime getPlayStart() {
        return playStart;
    }

    public void setPlayStart(LocalDateTime playStart) {
        this.playStart = playStart;
    }

    public LocalDateTime getLastLogout() {
        return lastLogout;
    }

    public void setLastLogout(LocalDateTime lastLogout) {
        this.lastLogout = lastLogout;
    }

    public enum Enum {
        AmountHeal,
        EnemyKill,
    }

    public void add(Enum key, double score) {
        enums.merge(key, score, Double::sum);
    }

    public SomJson save() {
        SomJson json = new SomJson();
        json.set("PlayStart", playStart.format(DateFormat));
        json.set("LastLogout", LocalDateTime.now().format(DateFormat));

        for (Map.Entry<Enum, Double> entry : enums.entrySet()) {
            json.set(entry.getKey().toString(), entry.getValue());
        }
        return json;
    }

    public void load(SomJson json) {
        try {
            setPlayStart(LocalDateTime.parse(json.getString("PlayStart"), DateFormat));
        } catch (Exception e) {
            setPlayStart(LocalDateTime.parse(ServiceStart, DateFormat));
        }
        try {
            setLastLogout(LocalDateTime.parse(json.getString("LastLogout"), DateFormat));
        } catch (Exception e) {
            setLastLogout(LocalDateTime.now());
        }

        enums.replaceAll((k, v) -> json.getDouble(k.toString(), 0));
    }
}
