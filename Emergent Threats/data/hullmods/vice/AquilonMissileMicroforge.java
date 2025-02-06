package data.hullmods.vice;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.util.IntervalUtil;

public class AquilonMissileMicroforge extends BaseHullMod {
	
	public static String MR_DATA_KEY = "aquilon_reload_data_key";
	private static float RELOAD_TIME = 40f;
	private static float RELOAD_PERCENT = 50f;
	private static String CONFLICT_MOD_1 = "missleracks";
	private static String CONFLICT_MOD_2 = "missile_autoloader";
	
	private static String AQUILON_FL = "vice_aquilon_fl";
	private static String AQUILON_FLH = "vice_aquilon_fl_handler";
	private static String AQUILON_FL_SYSTEM = "vice_flare_launcher";
	private static String AQUILON_MM = "vice_aquilon_mm";
	private static String AQUILON_MMH = "vice_aquilon_mm_handler";
	private static String AQUILON_MM_SYSTEM = "vice_micromissile";
	private static String AQUILON_SC = "vice_aquilon_sc";
	private static String AQUILON_SCH = "vice_aquilon_sc_handler";
	private static String AQUILON_SC_SYSTEM = "vice_spatialcharges";
	
	public static class AquilonMissileReloadData {
		IntervalUtil interval = new IntervalUtil(RELOAD_TIME, RELOAD_TIME);
	}
	
	//also handles ship system swapping
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		if (!stats.getVariant().hasHullMod(AQUILON_FL) 
				&& !stats.getVariant().hasHullMod(AQUILON_MM)
				&& !stats.getVariant().hasHullMod(AQUILON_SC)) {
			stats.getVariant().addMod(AQUILON_FLH); //micro-missiles by default
		}
		if (stats.getVariant().hasHullMod(AQUILON_FL)) {
			stats.getVariant().getHullSpec().setShipSystemId(AQUILON_FL_SYSTEM);
		}
		else if (stats.getVariant().hasHullMod(AQUILON_MM)) {
			stats.getVariant().getHullSpec().setShipSystemId(AQUILON_MM_SYSTEM);
		}
		else if (stats.getVariant().hasHullMod(AQUILON_SC)) {
			stats.getVariant().getHullSpec().setShipSystemId(AQUILON_SC_SYSTEM);
		}
		
		stats.getVariant().removeMod(CONFLICT_MOD_1);
		stats.getVariant().removeMod(CONFLICT_MOD_2);
	}
	
	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		super.advanceInCombat(ship, amount);

		if (!ship.isAlive()) return;
		
		CombatEngineAPI engine = Global.getCombatEngine();
		
		String key = MR_DATA_KEY + "_" + ship.getId();
		AquilonMissileReloadData data = (AquilonMissileReloadData) engine.getCustomData().get(key);
		if (data == null) {
			data = new AquilonMissileReloadData();
			engine.getCustomData().put(key, data);
		}
		
		data.interval.advance(amount);
		if (data.interval.intervalElapsed()) {
			for (WeaponAPI w : ship.getAllWeapons()) {
				if (w.getSize() != WeaponSize.MEDIUM || w.getType() != WeaponType.MISSILE) continue;
				if (w.usesAmmo() && w.getAmmo() < w.getMaxAmmo()) {
					int extraAmmo = 1;
					if (w.getMaxAmmo() * RELOAD_PERCENT * 0.01f > 1f) {
						extraAmmo = (int) (w.getMaxAmmo() * RELOAD_PERCENT * 0.01f);
					}
					int ammo = w.getAmmo() + extraAmmo;
					if (w.getMaxAmmo() == 1) w.setAmmo(1);
					else if (ammo <= w.getMaxAmmo()) w.setAmmo(ammo);
					else w.setAmmo(w.getMaxAmmo());
				}
			}
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) RELOAD_PERCENT + "%";
		if (index == 1) return "" + (int) RELOAD_TIME;
		if (index == 2) return "" + 1;
		return null;
	}
}