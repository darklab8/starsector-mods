package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import org.magiclib.util.MagicIncompatibleHullmods;

public class ImprovisedShieldImprovements extends BaseHullMod {

	public static final float PIERCE_MULT = 0.5f;
	public static final float SHIELD_BONUS = 15f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getShieldDamageTakenMult().modifyMult(id, 1f - SHIELD_BONUS * 0.01f);
		stats.getDynamic().getStat(Stats.SHIELD_PIERCED_MULT).modifyMult(id, PIERCE_MULT);		
		
			if(stats.getVariant().getHullMods().contains("advancedshieldemitter")){
			MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), "advancedshieldemitter", "improvisedshieldimprovements");
			}

			if(stats.getVariant().getHullMods().contains("extendedshieldemitter")){
			MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), "extendedshieldemitter", "improvisedshieldimprovements");
			}
	
			if(stats.getVariant().getHullMods().contains("frontemitter")){
			MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), "frontemitter", "improvisedshieldimprovements");
			}
			
			if(stats.getVariant().getHullMods().contains("hardenedshieldemitter")){
			MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), "hardenedshieldemitter", "improvisedshieldimprovements");
			}
			
			if(stats.getVariant().getHullMods().contains("adaptiveshields")){
			MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), "adaptiveshields", "improvisedshieldimprovements");
			}
			
			if(stats.getVariant().getHullMods().contains("stabilizedshieldemitter")){
			MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), "stabilizedshieldemitter", "improvisedshieldimprovements");
			}
		
		
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) SHIELD_BONUS + "%";
		return null;
	}

	public boolean isApplicableToShip(ShipAPI ship) {
		return ship != null && ship.getShield() != null;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		return "Ship has no shields";
	}
}
