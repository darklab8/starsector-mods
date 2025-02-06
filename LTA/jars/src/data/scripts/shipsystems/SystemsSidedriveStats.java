package data.scripts.shipsystems;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.AIUtils;

public class SystemsSidedriveStats extends BaseShipSystemScript {
    
        public boolean isActive = false;
	public static final float ROF_BONUS = 2f;
	public static final float FLUX_REDUCTION = 50f;
	public static float SPEED_BONUS = 200f;
	public static float TURN_BONUS = -10f;
        
	private Color color = new Color(220, 50, 0,255);

	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		float mult = 1f + ROF_BONUS * effectLevel;
		stats.getBallisticRoFMult().modifyMult(id, mult);
                stats.getMissileRoFMult().modifyMult(id, mult);
		stats.getBallisticWeaponFluxCostMod().modifyPercent(id, -FLUX_REDUCTION);
                
		if (state == ShipSystemStatsScript.State.OUT) {
			stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
		} else {
			stats.getMaxSpeed().modifyFlat(id, 200f * effectLevel);
			stats.getAcceleration().modifyFlat(id, 200f * effectLevel);
			//stats.getAcceleration().modifyPercent(id, 200f * effectLevel);
		}
                
		if (stats.getEntity() instanceof ShipAPI) {
			ShipAPI ship = (ShipAPI) stats.getEntity();
			
			ship.getEngineController().fadeToOtherColor(this, color, new Color(0,0,0,0), effectLevel, 0.67f);
			//ship.getEngineController().fadeToOtherColor(this, Color.white, new Color(0,0,0,0), effectLevel, 0.67f);
			ship.getEngineController().extendFlame(this, 2f * effectLevel, 0f * effectLevel, 0f * effectLevel);
			
                                if (state == State.OUT) {
                                //once
                                if (!isActive) {
                            ShipAPI empTarget = ship;
                            for (int x = 0; x < 30; x++) {
                                Global.getCombatEngine().spawnEmpArc(ship, ship.getLocation(),
                                                   empTarget,
                                                   empTarget, DamageType.ENERGY, 0, 200,
                                                   2000, null, 30f, new Color(230,40,40,0),
                                                   new Color(255,255,255,0));
                            }	
                               
                            }
                            
                                  isActive = true;
                                }//end once
                                }
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getBallisticRoFMult().unmodify(id);
		stats.getBallisticWeaponFluxCostMod().unmodify(id);
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
                isActive = false;
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float mult = 1f + ROF_BONUS * effectLevel;
		float bonusPercent = (int) ((mult - 1f) * 100f);
		if (index == 0) {
			return new StatusData("increased engine power", false);
		}
		if (index == 1) {
			return new StatusData("ballistic/missile rate of fire +" + (int) bonusPercent + "%", false);
		}
		if (index == 2) {
			return new StatusData("ballistic flux use -" + (int) FLUX_REDUCTION + "%", false);
		}
		return null;
	}
}
