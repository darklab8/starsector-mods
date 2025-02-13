package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

public class adf_AttunedEngines extends BaseLogisticsHullMod {
	private static final int BURN_LEVEL_BONUS = 1;
	
//	private static final int STRENGTH_PENALTY = 50;
//	private static final int PROFILE_PENALTY = 50;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
//		stats.getSensorProfile().modifyPercent(id, PROFILE_PENALTY);
//		stats.getSensorStrength().modifyMult(id, 1f - STRENGTH_PENALTY * 0.01f);
	
		stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + BURN_LEVEL_BONUS;
//		if (index == 1) return "" + STRENGTH_PENALTY + "%";
//		if (index == 2) return "" + PROFILE_PENALTY + "%";
		return null;
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return (!ship.getVariant().getHullMods().contains("augmentedengines"));
	}

	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().getHullMods().contains("augmentedengines")) {
			return "Incompatible with Augmented Drive Field.";
		}
		
		return null;
	}
}


