package data.scripts.world.systems;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.*;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin.AsteroidFieldParams;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin.MagneticFieldParams;
import com.fs.starfarer.api.util.Misc;

public class HMI_SV_phylum implements SectorGeneratorPlugin {

	public static SectorEntityToken getSectorAccess() {
		return Global.getSector().getStarSystem("Phylum").getEntityById("phylum_star");
	}
	public void generate(SectorAPI sector) {

		final Constellation hmi_phylum_Constellation = new Constellation(
				Constellation.ConstellationType.NORMAL,
				StarAge.OLD
		);
		final NameGenData data = new NameGenData("null", "null");
		final ProcgenUsedNames.NamePick HMI_Constellation = new ProcgenUsedNames.NamePick(data, "The Bloody Stretch", "null");
		hmi_phylum_Constellation.setNamePick(HMI_Constellation);

		StarSystemAPI system = sector.createStarSystem("Phylum");
		LocationAPI hyper = Global.getSector().getHyperspace();
		
		system.setBackgroundTextureFilename("graphics/backgrounds/background1.jpg");
		
		// create the star and generate the hyperspace anchor for this system
		PlanetAPI star = system.initStar("phylum_star", // unique id for this star
										    "star_red_dwarf",  // id in planets.json
										    200f, 		  // radius (in pixels at default zoom)
										    120, // corona
										    5f, // solar wind burn level
											0.25f, // flare probability
											3.0f); // CR loss multiplier, good values are in the range of 1-5
		
		system.setLightColor(new Color(245, 169, 137)); // light color in entire system, affects all entities
		system.getLocation().set(-28000, -10000);


		PlanetAPI phylum1 = system.addPlanet("fang_taxima", star, "Taxima", "jungle", 200, 100, 1200, 85);
		phylum1.setCustomDescriptionId("planet_phylum");

		PlanetAPI phylum2 = system.addPlanet("fang_genus", star, "Genus", "toxic", 120, 85, 1850, 270);
		phylum2.setInteractionImage("illustrations", "desert_moons_ruins");

		system.addAsteroidBelt(star, 120, 1350, 512, 200, 225, Terrain.ASTEROID_BELT, "The Smoke");
		system.addRingBand(star, "misc", "rings_ice0", 256f, 2, Color.white, 256f, 2350, 212f);

		PlanetAPI phylum3 = system.addPlanet("fang_familiae", star, "Familiae", "barren", 20, 85, 2850, 270);
		phylum3.setInteractionImage("illustrations", "pirate_station");

		JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint("familiae_jump_point1", "Inner Jump-point");
		OrbitAPI orbit = Global.getFactory().createCircularOrbit(star, 60, 2850, 270);
		jumpPoint1.setOrbit(orbit);
		orbit.setEntity(jumpPoint1);
		jumpPoint1.setRelatedPlanet(phylum3);
		jumpPoint1.setStandardWormholeToHyperspaceVisual();
		system.addEntity(jumpPoint1);


		system.addAsteroidBelt(star, 200, 2000, 512, 80, 95, Terrain.ASTEROID_BELT, "The Mist");
		system.addRingBand(star, "misc", "rings_dust0", 256f, 0, Color.white, 256f, 3200, 80f);
		system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 3400, 100f);
		system.addRingBand(star, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 3600, 130f);

		PlanetAPI phylum4 = system.addPlanet("fang_ordos", star, "Ordos", "barren_castiron", 220, 65, 4800, 380);
		phylum4.setInteractionImage("illustrations", "pirate_station");

		JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint("ordos_jump_point1", "Outer Jump-point");
		OrbitAPI orbit2 = Global.getFactory().createCircularOrbit(star, 140, 4800, 380);
		jumpPoint2.setOrbit(orbit2);
		orbit2.setEntity(jumpPoint2);
		jumpPoint2.setRelatedPlanet(phylum4);
		jumpPoint2.setStandardWormholeToHyperspaceVisual();
		system.addEntity(jumpPoint2);

		PlanetAPI phylum5 = system.addPlanet("fang_classus", star, "Classus", "rocky_metallic", 100, 65, 5500, 420);
		phylum5.setInteractionImage("illustrations", "vacuum_colony");

		system.addAsteroidBelt(star, 400, 4200, 512, 550, 600, Terrain.ASTEROID_BELT, "The Fog");
		system.addRingBand(star, "misc", "rings_ice0", 256f, 0, Color.white, 256f, 7000, 550f);
		system.addRingBand(star, "misc", "rings_ice0", 256f, 1, Color.white, 256f, 7200, 550f);
		system.addRingBand(star, "misc", "rings_ice0", 256f, 2, Color.white, 256f, 7400, 550f);
		system.addRingBand(star, "misc", "rings_ice0", 256f, 0, Color.white, 256f, 7600, 550f);

		// Relays
		SectorEntityToken phylum_relay = system.addCustomEntity("phylum_relay", // unique id
				"Phylum Relay", // name - if null, defaultName from custom_entities.json will be used
				"comm_relay_makeshift", // type of object, defined in custom_entities.json
				"fang"); // faction
		phylum_relay.setCircularOrbitPointingDown(star, 300, 6800, 700);

		SectorEntityToken phylum_buoy = system.addCustomEntity("phylum_buoy", // unique id
				"Phylum Nav Buoy", // name - if null, defaultName from custom_entities.json will be used
				"nav_buoy_makeshift", // type of object, defined in custom_entities.json
				"fang"); // faction
		phylum_buoy.setCircularOrbitPointingDown(star, 120, 6800, 700);

		SectorEntityToken phylum_sensor = system.addCustomEntity("phylum_sensor", // unique id
				"Phylum Sensor Array", // name - if null, defaultName from custom_entities.json will be used
				"sensor_array_makeshift", // type of object, defined in custom_entities.json
				"fang"); // faction
		phylum_sensor.setCircularOrbitPointingDown(star, 240, 6800, 700);

		system.autogenerateHyperspaceJumpPoints(true, true);
		cleanup(system);


		hmi_phylum_Constellation.getSystems().add(sector.getStarSystem("Phylum"));
		sector.getStarSystem("Phylum").setConstellation(hmi_phylum_Constellation);
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
