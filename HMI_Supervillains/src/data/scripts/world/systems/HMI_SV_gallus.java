package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class HMI_SV_gallus implements SectorGeneratorPlugin {

	    public static SectorEntityToken getSectorAccess() {
        return Global.getSector().getStarSystem("Gallus").getEntityById("gallus_star");
    }

	public static String HMI_DO_NOT_COLONISE_KEY = "$nex_do_not_colonize";
//	public static String HMI_SV_VILLIANCHECK = "$hmi_sv_villiancheckinfinity";

	@Override
	public void generate(SectorAPI sector) {


		StarSystemAPI system = sector.createStarSystem("Gallus");
		LocationAPI hyper = Global.getSector().getHyperspace();
		system.setBackgroundTextureFilename("graphics/backgrounds/background6.jpg");

		// create the star and generate the hyperspace anchor for this system
		PlanetAPI star = system.initStar("gallus_star", // unique id for this star
				"star_yellow",  // id in planets.json
				360f,          // radius (in pixels at default zoom)
				240); // corona radius, from star edge
		system.setLightColor(new Color(255, 252, 162)); // light color in entire system, affects all entities
		system.getLocation().set(-24000, -22000);

		PlanetAPI gallus1 = system.addPlanet("hegemony_gulch", star, "Gulch", "barren-desert", 270, 55, 750, 240);
		gallus1.setCustomDescriptionId("planet_gulch");
		gallus1.setInteractionImage("illustrations", "mine");

		PlanetAPI gallus2 = system.addPlanet("fang_alphidae", star, "Alphidae", "barren-bombarded", 35, 120, 2600, 410);
		gallus2.setCustomDescriptionId("planet_alphidae");
		gallus2.setInteractionImage("illustrations", "pirate_station");


		PlanetAPI gallus3 = system.addPlanet("hmisv_infinity", star, "Infinity", "terran-eccentric", 25, 200, 5000, 450);
		gallus3.setCustomDescriptionId("planet_infinity");
		Misc.initConditionMarket(gallus3);
		gallus3.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
		gallus3.getMarket().addCondition(Conditions.MILD_CLIMATE);
		gallus3.getMarket().addCondition(Conditions.HABITABLE);
		gallus3.getMarket().addCondition(Conditions.INIMICAL_BIOSPHERE);
		gallus3.getMarket().addCondition(Conditions.RUINS_VAST);
		gallus3.getMarket().addCondition(Conditions.FARMLAND_RICH);
		gallus3.getMemoryWithoutUpdate().set(HMI_DO_NOT_COLONISE_KEY, true);
		gallus3.addTag("hmi_sv_blocktag");
//		gallus3.getMemoryWithoutUpdate().set(HMI_SV_VILLIANCHECK, false);
		//To Figure out how the Implement, to act as a break on colonizing Infinity while Draco and Fang are still present
//		gallus3.getMarket().addCondition("hmi_fang_infinity");
//		gallus3.getMarket().addCondition("hmi_draco_infinity");


		PlanetAPI gallus3a = system.addPlanet("hmisv_infinity_moon", gallus3, "Luminar", "barren-bombarded", 40, 35, 1050, 40);
		Misc.initConditionMarket(gallus3a);
		gallus3a.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
		gallus3a.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
		gallus3a.getMarket().addCondition(Conditions.METEOR_IMPACTS);
		gallus3a.getMarket().addCondition(Conditions.IRRADIATED);
		gallus3a.getMarket().addCondition(Conditions.LOW_GRAVITY);

		//JUMP POINT
		JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint("gallus_jumpPointA", "Infinity Jump-Point");
		OrbitAPI orbit = Global.getFactory().createCircularOrbit(gallus3, 220, 550, 40);
		jumpPoint1.setOrbit(orbit);
		jumpPoint1.setRelatedPlanet(gallus3);
		jumpPoint1.setStandardWormholeToHyperspaceVisual();
		system.addEntity(jumpPoint1);

		PlanetAPI gallus5 = system.addPlanet("draco_salem", star, "First Salem", "barren-bombarded", 15, 185, 7400, 480);
		gallus5.setCustomDescriptionId("planet_salem");
		gallus5.setInteractionImage("illustrations", "industrial_megafacility");


		PlanetAPI gallus6 = system.addPlanet("pirate_freelancer", star, "Freelancer", "desert", 270, 85, 9200, 520);
		gallus6.setCustomDescriptionId("planet_freelancer");
		gallus6.setInteractionImage("illustrations", "mairaath");

		system.addAsteroidBelt(star, 250, 2680, 256, 150, 250, Terrain.ASTEROID_BELT, null);
		system.addRingBand(star, "misc", "rings_dust0", 256f, 0, Color.blue, 256f, 8620, 80f);
		system.addRingBand(star, "misc", "rings_dust0", 256f, 0, Color.cyan, 128f, 8650, 80f);
		system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.darkGray, 512f, 8670, 80f);
		system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.white, 512f, 8690, 80f);



		//Gas Giant for Flavour
		PlanetAPI gallus7 = system.addPlanet("hmisv_reach", star, "Gendo's Reach", "ice_giant", 200, 300, 7500, 350);
		Misc.initConditionMarket(gallus7);
		gallus7.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
		gallus7.getMarket().addCondition(Conditions.VERY_COLD);
		gallus7.getMarket().addCondition(Conditions.HIGH_GRAVITY);
		gallus7.getMarket().addCondition(Conditions.EXTREME_WEATHER);
		gallus7.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);


		// Relay
		SectorEntityToken gallus_relay = system.addCustomEntity("gallus_relay", // unique id
				"Gallus Relay", // name - if null, defaultName from custom_entities.json will be used
				"comm_relay_makeshift", // type of object, defined in custom_entities.json
				"fang"); // faction
		gallus_relay.setCircularOrbitPointingDown(star, 300, 3000, 300);

		SectorEntityToken gallus_buoy = system.addCustomEntity("gallus_buoy", // unique id
				"Gallus Nav Buoy", // name - if null, defaultName from custom_entities.json will be used
				"nav_buoy_makeshift", // type of object, defined in custom_entities.json
				"draco"); // faction
		gallus_buoy.setCircularOrbitPointingDown(star, 85, 7000, 490);

		SectorEntityToken gallus_sensor = system.addCustomEntity("gallus_sensor", // unique id
				"Gallus Sensor Array", // name - if null, defaultName from custom_entities.json will be used
				"sensor_array_makeshift", // type of object, defined in custom_entities.json
				"pirates"); // faction
		gallus_sensor.setCircularOrbitPointingDown(star, 205, 900, 450);

		// generates hyperspace destinations for in-system jump points
		system.autogenerateHyperspaceJumpPoints(true, true);
		cleanup(system);
	}

	void cleanup(StarSystemAPI system){
		HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
		NebulaEditor editor = new NebulaEditor(plugin);
		float minRadius = plugin.getTileSize() * 2f;

		float radius = system.getMaxRadiusInHyperspace();
		editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius * 0.5f, 0, 360f);
		editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f, 0.25f);
	}
}
