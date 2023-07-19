package SwordofMagic10.Player.Skill.Process;

import SwordofMagic10.Component.SomRay;
import SwordofMagic10.Component.SomTask;
import SwordofMagic10.Player.PlayerData;
import SwordofMagic10.Player.Skill.SkillParameterType;

public class ContinueBlade extends BladeSkill {
    public ContinueBlade(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String active() {
        double damage = getDamage() * (1 + (1 - 1.0 / Math.min(50, playerData.getEffect("DrawBlade").getStack()))) / getCount();
        for (int i = 0; i < getParameter(SkillParameterType.Count); i++) {
            SomRay ray = SomRay.rayLocationEntity(playerData, getReach(), 1, playerData.getTargets(), false);
            DrawBlade.Process(playerData, ray.getHitEntity(), ray, damage);
            SomTask.wait(100);
        }
        return null;
    }
}
