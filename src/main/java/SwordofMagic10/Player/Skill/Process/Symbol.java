package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.*;
import SwordofMagic10.DataBase.ItemDataLoader;
import SwordofMagic10.Entity.DurationSkill;
import SwordofMagic10.Entity.SomEffect;
import SwordofMagic10.Entity.SomEntity;
import SwordofMagic10.Item.SomAmulet;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;
import SwordofMagic10.Player.Skill.SomSkill;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

import static SwordofMagic10.SomCore.Log;

public class Symbol extends SomSkill {
    public Symbol(PlayerData playerData) {
        super(playerData);
    }

    private final List<DurationSkill.MagicCircle> symbolList = new ArrayList<>();

    @Override
    public String active() {
        SomEffect effect = new SomEffect(this, false).setTime(1);
        SomParticle particle = new SomParticle(Color.MAROON, playerData);
        SomBlockParticle displayParticle = new SomBlockParticle(Material.LIGHTNING_ROD);
        double radius = getRadius();
        double reach = getReach();
        int duration = getDuration();

        SomAmulet.Bottle bottle1 = (SomAmulet.Bottle) ItemDataLoader.getItemData("精巧の願瓶");
        if(playerData.hasBottle(bottle1)) radius *= bottle1.getParameter(SkillParameterType.Radius);

        SomAmulet.Bottle bottle2 = (SomAmulet.Bottle) ItemDataLoader.getItemData("負目の願瓶");
        if(playerData.hasBottle(bottle2)){
            duration *= bottle2.getParameter(SkillParameterType.Duration);
            reach *= bottle2.getParameter(SkillParameterType.Reach);
        }

        SomRay ray = SomRay.rayLocationBlock(playerData.getEyeLocation(), reach, true);
        CustomLocation location = ray.getHitPosition().lower();

        displayParticle.stick(playerData.getViewers(), location.clone(), duration*20);

        double finalRadius = radius;
        int perTick = 10;
        DurationSkill.MagicCircle magicCircle = DurationSkill.magicCircle(playerData, location, radius, perTick, duration*20, displayParticle);
        magicCircle.setRunnable(()->{
            particle.circle(playerData.getViewers(), location, finalRadius);
            for(SomEntity entity : SomEntity.nearSomEntity(playerData.getTargets(), location, finalRadius)){
                entity.addEffect(effect, playerData);
            }
            if (300 < magicCircle.getLocation().distance(playerData.getLocation())){
                magicCircle.end();
            }
        });

        if(playerData.hasBottle(bottle2)){
            magicCircle.setRunnableEnd(()-> symbolList.remove(magicCircle));
            symbolList.add(magicCircle);

            if (getStack() < symbolList.size()) symbolList.get(0).end();

            playerData.getSkillManager().setCoolTime(this, calcCoolTime(5));
        }

        magicCircle.run();
        SomTask.delay(() -> SomSound.Bow.play(playerData.getViewers(), location),3);

        return null;
    }

    public List<DurationSkill.MagicCircle> getSymbols(){
        return symbolList;
    }
}
