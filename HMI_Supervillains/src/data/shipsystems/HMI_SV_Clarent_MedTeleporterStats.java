package data.shipsystems;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;


public class HMI_SV_Clarent_MedTeleporterStats extends BaseShipSystemScript {

	public boolean isActive = false;

	public static Color LIGHTNING_CORE_COLOR = new Color(219, 255, 223, 202);
	public static Color LIGHTNING_FRINGE_COLOR = new Color(27, 255, 19, 176);

	private Color color = new Color(100,255,100,255);
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

		float actualLightningRange = 1000f;
		float actualLightingDamage = 200f;
		float actualLightningEMP = 400f;
		int arcnumber = 24;
		

		if (state == ShipSystemStatsScript.State.OUT) {
			stats.getMaxTurnRate().unmodify(id);
		} else {
			stats.getMaxTurnRate().modifyFlat(id, 12f);
			stats.getMaxTurnRate().modifyPercent(id, 100f);
		}

		if (stats.getEntity() instanceof ShipAPI) {
			ShipAPI ship = (ShipAPI) stats.getEntity();

                                if (state == State.OUT) {
									if (!isActive) {

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
													//This SHould mean it targets enemy missiles and not friendly ones...?
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

											for (int x = 0; x < arcnumber; x++) {
											//And finally, fire at a random valid target
											CombatEntityAPI target = validTargets.get(MathUtils.getRandomNumberInRange(0, validTargets.size() - 1));

												Global.getCombatEngine().spawnEmpArc(ship, sourcePoint, ship, target,
														DamageType.ENERGY, //Damage type
														MathUtils.getRandomNumberInRange(0.8f, 1.2f) * actualLightingDamage, //Damage
														MathUtils.getRandomNumberInRange(0.8f, 1.2f) * actualLightningEMP, //Emp
														100000f, //Max range
														"tachyon_lance_emp_impact", //Impact sound
														10f, // thickness of the lightning bolt
														LIGHTNING_CORE_COLOR, //Central color
														LIGHTNING_FRINGE_COLOR //Fringe Color
												);
											}
											validTargets.clear();
										}
										vents.clear();
									}
                                  isActive = true;
                                }
                            }

		}

	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getTurnAcceleration().unmodify(id);
                isActive = false;
	}
	
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData("improved maneuverability", false);
		}
		return null;
	}
}
