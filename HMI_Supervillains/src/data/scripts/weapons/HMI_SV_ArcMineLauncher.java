package data.scripts.weapons;

import com.fs.starfarer.api.combat.*;

public class HMI_SV_ArcMineLauncher implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin
{

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon)
    {
        // do nothin
    }

    @Override
    public void onFire(DamagingProjectileAPI proj, WeaponAPI weapon, CombatEngineAPI engine)
    {
        // can't really do it with an AI script because I don't want to copy Alex's mine AI
        engine.addPlugin(new HMI_SV_ArcMineScript(weapon.getShip(), proj));
    }
}
