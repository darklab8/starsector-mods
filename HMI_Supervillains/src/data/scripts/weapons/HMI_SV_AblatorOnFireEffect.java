package data.scripts.weapons;

import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

import static com.fs.starfarer.api.combat.DamageType.FRAGMENTATION;
import static com.fs.starfarer.api.combat.DamageType.HIGH_EXPLOSIVE;

public class HMI_SV_AblatorOnFireEffect implements OnFireEffectPlugin {

    public HMI_SV_AblatorOnFireEffect() {
    }

    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        projectile.getDamage().setSoftFlux(true);

    }
}


