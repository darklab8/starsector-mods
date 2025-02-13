package data.campaign.procgen;

import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.CampaignEventListener.FleetDespawnReason;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.fleets.*;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.Misc;

public class MaxwellStationFleetManager extends SourceBasedFleetManager {

	protected int minPts;
	protected int maxPts;
	protected int totalLost;

	public MaxwellStationFleetManager(SectorEntityToken source, float thresholdLY, int minFleets, int maxFleets, float respawnDelay,
									  int minPts, int maxPts) {
		super(source, thresholdLY, minFleets, maxFleets, respawnDelay);
		this.minPts = minPts;
		this.maxPts = maxPts;
	}

	@Override
	protected CampaignFleetAPI spawnFleet() {
		if (source == null) return null;

		Random random = new Random();

		int combatPoints = minPts + random.nextInt(maxPts - minPts + 1);

		int bonus = totalLost * 4;
		if (bonus > maxPts) bonus = maxPts;

		combatPoints += bonus;

		String type = FleetTypes.PATROL_SMALL;
		if (combatPoints > 8) type = FleetTypes.PATROL_MEDIUM;
		if (combatPoints > 16) type = FleetTypes.PATROL_LARGE;

		combatPoints *= 8f;

		FleetParamsV3 params = new FleetParamsV3(
				source.getMarket(),
				source.getLocationInHyperspace(),
				"hmi_maxwell",
				1f,
				type,
				combatPoints, // combatPts
				0f, // freighterPts
				0f, // tankerPts
				0f, // transportPts
				0f, // linerPts
				0f, // utilityPts
				0f // qualityMod
		);
		//params.officerNumberBonus = 10;
		params.random = random;

		CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);
		if (fleet == null) return null;;


		LocationAPI location = source.getContainingLocation();
		location.addEntity(fleet);

		MaxwellSeededFleetManager.initMaxwellFleetProperties(random, fleet, false);

		fleet.setLocation(source.getLocation().x, source.getLocation().y);
		fleet.setFacing(random.nextFloat() * 360f);

		fleet.addScript(new MaxwellAssignmentAI(fleet, (StarSystemAPI) source.getContainingLocation(), source));
		fleet.getMemoryWithoutUpdate().set("$sourceId", source.getId());

		return fleet;
	}

	@Override
	public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, FleetDespawnReason reason, Object param) {
		super.reportFleetDespawnedToListener(fleet, reason, param);
		if (reason == FleetDespawnReason.DESTROYED_BY_BATTLE) {
			String sid = fleet.getMemoryWithoutUpdate().getString("$sourceId");
			if (sid != null && source != null && sid.equals(source.getId())) {
				//if (sid != null && sid.equals(source.getId())) {
				totalLost++;
			}
		}
	}

	public int getTotalLost() {
		return totalLost;
	}


}
