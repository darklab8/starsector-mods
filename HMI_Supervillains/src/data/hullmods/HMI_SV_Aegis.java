package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HMI_SV_Aegis extends BaseHullMod {

    private static Map mag = new HashMap();
    static {
        mag.put(HullSize.FRIGATE, 24f);
        mag.put(HullSize.DESTROYER, 20f);
        mag.put(HullSize.CRUISER, 20f);
        mag.put(HullSize.CAPITAL_SHIP, 18f);
    }

    public static final Map<ShipAPI.HullSize, Float> LIGHTNING_DAMAGE = new HashMap<ShipAPI.HullSize, Float>();
    static {
        LIGHTNING_DAMAGE.put(ShipAPI.HullSize.FRIGATE, 45f);
        LIGHTNING_DAMAGE.put(ShipAPI.HullSize.DESTROYER, 90f);
        LIGHTNING_DAMAGE.put(ShipAPI.HullSize.CRUISER, 180f);
        LIGHTNING_DAMAGE.put(ShipAPI.HullSize.CAPITAL_SHIP, 400f);
    }

    public static final Map<ShipAPI.HullSize, Float> LIGHTNING_EMP = new HashMap<ShipAPI.HullSize, Float>();
    static {
        LIGHTNING_EMP.put(ShipAPI.HullSize.FRIGATE, 40f);
        LIGHTNING_EMP.put(ShipAPI.HullSize.DESTROYER, 80f);
        LIGHTNING_EMP.put(ShipAPI.HullSize.CRUISER, 160f);
        LIGHTNING_EMP.put(ShipAPI.HullSize.CAPITAL_SHIP, 300f);
    }
	
    public static final Map<ShipAPI.HullSize, Float> LIGHTNING_RANGE = new HashMap<ShipAPI.HullSize, Float>();
    static {
        LIGHTNING_RANGE.put(ShipAPI.HullSize.FRIGATE, 500f);
        LIGHTNING_RANGE.put(ShipAPI.HullSize.DESTROYER, 600f);
        LIGHTNING_RANGE.put(ShipAPI.HullSize.CRUISER, 650f);
        LIGHTNING_RANGE.put(ShipAPI.HullSize.CAPITAL_SHIP, 700f);
    }
	

    public static Color LIGHTNING_CORE_COLOR = new Color(219, 255, 223, 202);
    public static Color LIGHTNING_FRINGE_COLOR = new Color(27, 255, 19, 176);


	public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (LIGHTNING_RANGE.get(HullSize.FRIGATE)).intValue();
        if (index == 1) return "" + (LIGHTNING_RANGE.get(HullSize.DESTROYER)).intValue();
        if (index == 2) return "" + (LIGHTNING_RANGE.get(HullSize.CRUISER)).intValue();
        if (index == 3) return "" + (LIGHTNING_RANGE.get(HullSize.CAPITAL_SHIP)).intValue();
        if (index == 4) return "" + "hard flux level";
        if (index == 5) return "" + "Larger vessels produce EMP arcs emitted much faster and with greater power";
		return null;
	}

    private Color color = new Color(6, 255, 2,255);
    private IntervalUtil zapInterval = new IntervalUtil(1.0f, 1.5f);

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {

        float EMP_arc_speed_level = (1.01f - ship.getFluxTracker().getFluxLevel());
//        float EMP_arc_dmg_level = (1f + ship.getFluxTracker().getFluxLevel());

//        IntervalUtil zapInterval = new IntervalUtil((EMP_arc_speed_level * 0.6f), (EMP_arc_speed_level * 0.8f));


        float actualLightningRange = LIGHTNING_RANGE.get(ship.getHullSize());
        float actualLightingDamage = LIGHTNING_DAMAGE.get(ship.getHullSize());
        float actualLightningEMP = LIGHTNING_EMP.get(ship.getHullSize());

        //Can't EMP while overloaded or Dead
        if (!ship.getFluxTracker().isOverloaded() || !ship.isHulk());
        {

            zapInterval.advance(amount);
            //Checks if we should send lightning this frame
            if (zapInterval.intervalElapsed()) {
                zapInterval.setInterval(EMP_arc_speed_level * 1.5f, EMP_arc_speed_level * 2.0f);

                //Choose a random vent port to send lightning from
                List<WeaponSlotAPI> vents = new ArrayList<WeaponSlotAPI>();
                for (WeaponSlotAPI weaponSlotAPI : ship.getHullSpec().getAllWeaponSlotsCopy()) {
                    if (weaponSlotAPI.isSystemSlot()) {
                        vents.add(weaponSlotAPI);
                    }
                }

                //If we have no vents, we can't do a dangerous overload this frame; ignore the rest of the code
                if (!vents.isEmpty()) {
                    Vector2f sourcePoint = vents.get(MathUtils.getRandomNumberInRange(0, vents.size()-1)).computePosition(ship);

                    //Then, find all valid targets: we can only shoot missiles, ships and asteroids [including ourselves]
                    List<CombatEntityAPI> validTargets = new ArrayList<CombatEntityAPI>();
                    for (CombatEntityAPI entityToTest : CombatUtils.getEntitiesWithinRange(sourcePoint, actualLightningRange)) {
                        if (entityToTest instanceof ShipAPI || entityToTest instanceof AsteroidAPI || entityToTest instanceof MissileAPI){
                            //Phased targets, and targets with no collision, are ignored
                            if (entityToTest instanceof ShipAPI) {
                                if (((ShipAPI) entityToTest).isPhased() || entityToTest == ship || (entityToTest.getOwner() == ship.getOwner())) {
                                    continue;
                                }
                            }
                            //This Should mean it targets enemy missiles and not friendly ones...?
                            if (entityToTest instanceof MissileAPI) {
                                if((entityToTest.getOwner() == ship.getOwner()) || (((MissileAPI) entityToTest).isFizzling())) {
                                    continue;
                                }
                            }
                            if (entityToTest.getCollisionClass().equals(CollisionClass.NONE)) {
                                continue;
                            }
                            validTargets.add(entityToTest);
                        }
                    }

                    //If we have no valid targets, zap a random point near us
                    if (validTargets.isEmpty()) {
                        validTargets.add(new SimpleEntity(MathUtils.getRandomPointInCircle(sourcePoint, actualLightningRange)));
                    }

                    //And finally, fire at a random valid target
                    CombatEntityAPI target = validTargets.get(MathUtils.getRandomNumberInRange(0, validTargets.size() - 1));
                    Global.getCombatEngine().spawnEmpArc(ship, sourcePoint, ship, target,
                            DamageType.ENERGY, //Damage type
                            MathUtils.getRandomNumberInRange(0.8f, 1.2f) * actualLightingDamage, //Damage
                            MathUtils.getRandomNumberInRange(0.8f, 1.2f) * actualLightningEMP, //Emp
                            100000f, //Max range
                            "tachyon_lance_emp_impact", //Impact sound
                            15f, // thickness of the lightning bolt
                            LIGHTNING_CORE_COLOR, //Central color
                            LIGHTNING_FRINGE_COLOR //Fringe Color
                    );
                    validTargets.clear();
                }
                vents.clear();
            }
        }
    }
}