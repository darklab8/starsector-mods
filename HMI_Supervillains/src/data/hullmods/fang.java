package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class fang extends BaseHullMod {

    private static final float HULL_BUFF = 0.10f;
	private static final float WEAPON_BUFF = 2.00f;
    private static final float AGILITY_BUFF = 0.10f;
    private static final float FIGHTER_MALUS = 0.75f;
    private static final float FIGHTER_SPEED_MALUS = 0.25f;

	private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FRIGATE, 200f);
		mag.put(HullSize.DESTROYER, 260f);
		mag.put(HullSize.CRUISER, 300f);
		mag.put(HullSize.CAPITAL_SHIP, 340f);
	}



    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
    static
    {
        // These hullmods will automatically be removed
        BLOCKED_HULLMODS.add("advancedshieldemitter");
        BLOCKED_HULLMODS.add("extendedshieldemitter");
        BLOCKED_HULLMODS.add("frontshield");
		BLOCKED_HULLMODS.add("hardenedshieldemitter");
		BLOCKED_HULLMODS.add("adaptiveshields");
        BLOCKED_HULLMODS.add("stabilizedshieldemitter");
		BLOCKED_HULLMODS.add("shield_shunt");
        BLOCKED_HULLMODS.add("swp_shieldbypass");
    }
    private float check=0;
    private String id, ERROR="IncompatibleHullmodWarning";

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id){
        
        if (check>0) {     
            check-=1;
            if (check<1){
            ship.getVariant().removeMod(ERROR);   
            }
        }
        
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {                
                ship.getVariant().removeMod(tmp);      
                ship.getVariant().addMod(ERROR);
                check=3;
            }
        }
        ship.setShield(ShieldAPI.ShieldType.NONE, 0f, 0f, 90f);
    }


	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getArmorBonus().modifyFlat(id, (Float) mag.get(hullSize));

        stats.getHullBonus().modifyMult(id, (1 + HULL_BUFF));

        stats.getWeaponHealthBonus().modifyMult(id, (1 + WEAPON_BUFF));
        stats.getEngineHealthBonus().modifyMult(id, (1 + WEAPON_BUFF));

        stats.getAcceleration().modifyMult(id, (1 + AGILITY_BUFF));
        stats.getDeceleration().modifyMult(id, (1 + AGILITY_BUFF));
        stats.getTurnAcceleration().modifyMult(id, (1 + AGILITY_BUFF));
        stats.getMaxTurnRate().modifyMult(id, (1 + AGILITY_BUFF));

        stats.getShieldDamageTakenMult().modifyMult(id, 0f);
        stats.getShieldUpkeepMult().modifyMult(id, 0f);
	}

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {

        MutableShipStatsAPI stats = fighter.getMutableStats();

        stats.getMaxSpeed().modifyMult(id, 1f - FIGHTER_SPEED_MALUS);

        stats.getMissileWeaponDamageMult().modifyPercent(id, FIGHTER_MALUS * 100f);
        stats.getBallisticWeaponDamageMult().modifyPercent(id, FIGHTER_MALUS * 100f);
        stats.getEnergyWeaponDamageMult().modifyPercent(id, FIGHTER_MALUS * 100f);

        fighter.setHeavyDHullOverlay();
    }

	public String getDescriptionParam(int index, HullSize hullSize) {

        if (index == 0) return "" + "forgo shields entirely and utilise a Damper Field System";
        if (index == 1) return "" + "allow the ship to fire on enemies while it is active";
		if (index == 2) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue();
		if (index == 3) return "" + ((Float) mag.get(HullSize.DESTROYER)).intValue();
		if (index == 4) return "" + ((Float) mag.get(HullSize.CRUISER)).intValue();
		if (index == 5) return "" + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue();
        if (index == 6) return "" + (int)Math.round((HULL_BUFF) * 100f) + "%";
        if (index == 7) return "" + (int)Math.round((WEAPON_BUFF) * 100f) + "%";
        if (index == 8) return "" + (int)Math.round((AGILITY_BUFF) * 100f) + "%";
        if (index == 9) return "" + (int)Math.round((1 - FIGHTER_MALUS) * 100f) + "%";

		return null;
	}

}