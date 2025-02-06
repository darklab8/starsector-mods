package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.*;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.DerelictThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.terrain.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;

public class HMI_SV_presterjohn implements SectorGeneratorPlugin {

    public static SectorEntityToken getSectorAccess() {
        return Global.getSector().getStarSystem("Prester John").getEntityById("presterjohn_star");
    }

	@Override
	public void generate(SectorAPI sector) {


		final Constellation hmi_prester_Constellation = new Constellation(
				Constellation.ConstellationType.NORMAL,
				StarAge.OLD
		);
		final NameGenData data = new NameGenData("null", "null");
		final ProcgenUsedNames.NamePick HMI_Constellation = new ProcgenUsedNames.NamePick(data, "Salem's Lost", "null");
		hmi_prester_Constellation.setNamePick(HMI_Constellation);


		StarSystemAPI system = sector.createStarSystem("Prester John");
		LocationAPI hyper = Global.getSector().getHyperspace();
		system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg");

		SectorEntityToken presterjohn_nebula = Misc.addNebulaFromPNG("data/campaign/terrain/eos_nebula.png",
				0, 0, // center of nebula
				system, // location to add to
				"terrain", "nebula_blue", // "nebula_blue", // texture to use, uses xxx_map for map
				4, 4, StarAge.OLD); // number of cells in texture

		PlanetAPI star = system.initStar("presterjohn_star",
										 "black_hole", // id in planets.json
										 200f, 		// radius (in pixels at default zoom)
										 450); // CR loss multiplier, good values are in the range of 1-5); // corona radius, from star edge

		system.setLightColor(new Color(153, 228, 255)); // light color in entire system, affects all entities
		system.getLocation().set(-10000, -27000);

		SectorEntityToken PresterEventHorizonOuter = system.addTerrain(Terrain.EVENT_HORIZON,
				new EventHorizonPlugin.CoronaParams(1500,
						750,
						star,
						-8f,
						0f,
						10f));


		float orbitRadius = star.getRadius() * 8f;
		float bandWidth = 256f;
		int numBands = 12;

		for (float i = 0; i < numBands; i++) {
			float radius = orbitRadius - i * bandWidth * 0.25f - i * bandWidth * 0.1f;
			float orbitDays = radius / (30f + 10f * Misc.random.nextFloat());
			WeightedRandomPicker<String> rings = new WeightedRandomPicker<>();
			rings.add("rings_dust0");
			rings.add("rings_ice0");
			String ring = rings.pick();
			RingBandAPI visual = system.addRingBand(star, "misc", ring, 256f, 0, Color.white, bandWidth,
					radius + bandWidth / 2f, -orbitDays);
			float spiralFactor = 2f + Misc.random.nextFloat() * 5f;
			visual.setSpiral(true);
			visual.setMinSpiralRadius(star.getRadius());
			visual.setSpiralFactor(spiralFactor);
		}
		SectorEntityToken ring = system.addTerrain(Terrain.RING, new BaseRingTerrain.RingParams(orbitRadius, orbitRadius / 2f, star, "Accretion Disk"));
		ring.addTag(Tags.ACCRETION_DISK);
		ring.setCircularOrbit(star, 0, 0, -100);



		PlanetAPI presterjohn1 = system.addPlanet("presterjohn1", star, "Saint Januarius", "ice_giant", 200, 400, 10000, 360);
		Misc.initConditionMarket(presterjohn1);
		presterjohn1.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
		presterjohn1.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
		presterjohn1.getMarket().addCondition(Conditions.HIGH_GRAVITY);
		presterjohn1.getMarket().addCondition(Conditions.EXTREME_WEATHER);
		presterjohn1.getMarket().addCondition(Conditions.DARK);
		presterjohn1.getMarket().addCondition(Conditions.VERY_COLD);

		SectorEntityToken presterjohn1_field = system.addTerrain(Terrain.MAGNETIC_FIELD,
				new MagneticFieldTerrainPlugin.MagneticFieldParams(200f, // terrain effect band width
						465f, // terrain effect middle radius
						presterjohn1, // entity that it's around
						410f, // visual band start
						490f, // visual band end
						new Color(50, 20, 100, 50), // base color
						1f, // probability to spawn aurora sequence, checked once/day when no aurora in progress
						new Color(50, 20, 110, 130),
						new Color(150, 30, 120, 150),
						new Color(200, 50, 130, 190),
						new Color(250, 70, 150, 240),
						new Color(200, 80, 130, 255),
						new Color(75, 0, 160),
						new Color(127, 0, 255)
				));
		presterjohn1_field.setCircularOrbit(presterjohn1, 0, 0, 100);

		PlanetAPI presterjohn1a = system.addPlanet("draco_havengate", presterjohn1, "Havengate", "cryovolcanic", 200, 225, 1500, 85);
		presterjohn1a.setCustomDescriptionId("planet_havengate");
		presterjohn1a.setInteractionImage("illustrations", "pirate_station");

		PlanetAPI presterjohn1b = system.addPlanet("draco_blessvale", presterjohn1, "Blessvale", "barren", 100, 125, 2200, 105);
		presterjohn1b.setCustomDescriptionId("planet_blessvale");
		presterjohn1b.setInteractionImage("illustrations", "vacuum_colony");

		PlanetAPI presterjohn1c = system.addPlanet("presterjohn1c", presterjohn1, "Illuminary Observatory", "barren", 60, 45, 3500, 220);
		Misc.initConditionMarket(presterjohn1c);
		presterjohn1c.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
		presterjohn1c.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
		presterjohn1c.getMarket().addCondition(Conditions.LOW_GRAVITY);
		presterjohn1c.getMarket().addCondition(Conditions.DARK);
		presterjohn1c.getMarket().addCondition(Conditions.VERY_COLD);

		PlanetAPI presterjohn2 = system.addPlanet("presterjohn2", star, "Saint Elizabeth", "ice_giant", 20, 520, 15500, 500);
		Misc.initConditionMarket(presterjohn2);
		presterjohn2.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
		presterjohn2.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
		presterjohn2.getMarket().addCondition(Conditions.HIGH_GRAVITY);
		presterjohn2.getMarket().addCondition(Conditions.VOLATILES_TRACE);
		presterjohn2.getMarket().addCondition(Conditions.EXTREME_WEATHER);
		presterjohn2.getMarket().addCondition(Conditions.DARK);
		presterjohn2.getMarket().addCondition(Conditions.VERY_COLD);

		SectorEntityToken presterjohn2_field = system.addTerrain(Terrain.MAGNETIC_FIELD,
				new MagneticFieldTerrainPlugin.MagneticFieldParams(200f, // terrain effect band width
						575f, // terrain effect middle radius
						presterjohn2, // entity that it's around
						550f, // visual band start
						600f, // visual band end
						new Color(50, 20, 100, 50), // base color
						1f, // probability to spawn aurora sequence, checked once/day when no aurora in progress
						new Color(50, 20, 110, 130),
						new Color(150, 30, 120, 150),
						new Color(200, 50, 130, 190),
						new Color(250, 70, 150, 240),
						new Color(200, 80, 130, 255),
						new Color(75, 0, 160),
						new Color(127, 0, 255)
				));
		presterjohn2_field.setCircularOrbit(presterjohn2, 0, 0, 100);

		PlanetAPI presterjohn3 = system.addPlanet("presterjohn3", star, "Saint Christopher", "ice_giant", 100, 650, 19000, 820);
		Misc.initConditionMarket(presterjohn3);
		presterjohn3.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
		presterjohn3.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
		presterjohn3.getMarket().addCondition(Conditions.HIGH_GRAVITY);
		presterjohn3.getMarket().addCondition(Conditions.IRRADIATED);
		presterjohn3.getMarket().addCondition(Conditions.DARK);
		presterjohn3.getMarket().addCondition(Conditions.VERY_COLD);

		SectorEntityToken presterjohn3_field = system.addTerrain(Terrain.MAGNETIC_FIELD,
				new MagneticFieldTerrainPlugin.MagneticFieldParams(200f, // terrain effect band width
						680f, // terrain effect middle radius
						presterjohn3, // entity that it's around
						660f, // visual band start
						720f, // visual band end
						new Color(50, 20, 100, 50), // base color
						1f, // probability to spawn aurora sequence, checked once/day when no aurora in progress
						new Color(50, 20, 110, 130),
						new Color(150, 30, 120, 150),
						new Color(200, 50, 130, 190),
						new Color(250, 70, 150, 240),
						new Color(200, 80, 130, 255),
						new Color(75, 0, 160),
						new Color(127, 0, 255)
				));
		presterjohn3_field.setCircularOrbit(presterjohn3, 0, 0, 100);

		JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("presterjohn_jump_point1", "Inner Jump-point");
		OrbitAPI orbit = Global.getFactory().createCircularOrbit(star, 60, 6000, 360);
		jumpPoint.setOrbit(orbit);
		jumpPoint.setStandardWormholeToHyperspaceVisual();
		system.addEntity(jumpPoint);

		// Relays
		SectorEntityToken presterjohn_relay = system.addCustomEntity("presterjohn_relay", // unique id
				"Prester John Relay", // name - if null, defaultName from custom_entities.json will be used
				"comm_relay_makeshift", // type of object, defined in custom_entities.json
				"draco"); // faction
		presterjohn_relay.setCircularOrbitPointingDown(star, 300, 3000, 360);

		SectorEntityToken presterjohn_buoy = system.addCustomEntity("presterjohn_buoy", // unique id
				"Prester John Buoy", // name - if null, defaultName from custom_entities.json will be used
				"nav_buoy_makeshift", // type of object, defined in custom_entities.json
				"draco"); // faction
		presterjohn_buoy.setCircularOrbitPointingDown(star, 120, 3200, 360);

		SectorEntityToken presterjohn_sensor = system.addCustomEntity("presterjohn_sensor", // unique id
				"Prester John Array", // name - if null, defaultName from custom_entities.json will be used
				"sensor_array_makeshift", // type of object, defined in custom_entities.json
				"draco"); // faction
		presterjohn_sensor.setCircularOrbitPointingDown(star, 240, 3800, 360);

		system.autogenerateHyperspaceJumpPoints(true, true);
		cleanup(system);

		hmi_prester_Constellation.getSystems().add(sector.getStarSystem("Prester John"));
		sector.getStarSystem("Prester John").setConstellation(hmi_prester_Constellation);

	}

	public static void addAccretionDisk(PlanetAPI star, String name) {
		StarSystemAPI system = star.getStarSystem();
		float orbitRadius = star.getRadius() * 8f;
		float bandWidth = 256f;
		int numBands = 12;

		for (float i = 0; i < numBands; i++) {
			float radius = orbitRadius - i * bandWidth * 0.25f - i * bandWidth * 0.1f;
			float orbitDays = radius / (30f + 10f * Misc.random.nextFloat());
			WeightedRandomPicker<String> rings = new WeightedRandomPicker<>();
			rings.add("rings_dust0");
			rings.add("rings_ice0");
			String ring = rings.pick();
			RingBandAPI visual = system.addRingBand(star, "misc", ring, 256f, 0, Color.white, bandWidth,
					radius + bandWidth / 2f, -orbitDays);
			float spiralFactor = 2f + Misc.random.nextFloat() * 5f;
			visual.setSpiral(true);
			visual.setMinSpiralRadius(star.getRadius());
			visual.setSpiralFactor(spiralFactor);
		}
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