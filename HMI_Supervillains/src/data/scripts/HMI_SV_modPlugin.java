package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.thoughtworks.xstream.XStream;
import data.campaign.fleets.DracoFleetManager;
import data.campaign.fleets.FangFleetManager;
import data.campaign.listeners.HMI_SV_Infinity_check;
import data.scripts.world.HMI_SV_gen;
import data.scripts.world.HMI_lootdracoget;
import data.scripts.world.HMI_lootfangget;
import exerelin.campaign.SectorManager;
import org.apache.log4j.Level;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.util.vector.Vector2f;

import java.io.IOException;

public class HMI_SV_modPlugin extends BaseModPlugin {
    public static boolean haveNexerelin = false;

    public void configureXStream(XStream x) {
        x.alias("HMI_lootfangget", HMI_lootfangget.class);
        x.alias("HMI_lootdracoget", HMI_lootdracoget.class);
    }

    public static void syncHMI_SVScripts() {
        if (!Global.getSector().hasScript(HMI_lootfangget.class)) {
            Global.getSector().addScript(new HMI_lootfangget());
        }
        if (!Global.getSector().hasScript(HMI_lootdracoget.class)) {
            Global.getSector().addScript(new HMI_lootdracoget());
        }
        if (!Global.getSector().hasScript(FangFleetManager.class)) {
            Global.getSector().addScript(new FangFleetManager());
        }
        if (!Global.getSector().hasScript(DracoFleetManager.class)) {
            Global.getSector().addScript(new DracoFleetManager());
        }
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!haveNexerelin || SectorManager.getManager().isCorvusMode()) {
            if (!Global.getSector().getListenerManager().hasListenerOfClass(HMI_SV_Infinity_check.class)) {
                Global.getSector().getListenerManager().addListener(new HMI_SV_Infinity_check(), true);
            }
        }
    }

    private void addFangFleet() {
        final SectorAPI sector = Global.getSector();
        final StarSystemAPI system = sector.getStarSystem("Phylum");
        final SectorEntityToken taxima = system.getEntityById("fang_taxima");
        final FleetParamsV3 params = new FleetParamsV3(taxima.getMarket(), (Vector2f)null, "fang", Float.valueOf(2.0f), "taskForce", 800.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        params.officerNumberMult = 4.0f;
        params.officerLevelBonus = 2;
        params.officerNumberBonus = 10;
        params.officerLevelLimit = Global.getSettings().getInt("officerMaxLevel");
        params.modeOverride = FactionAPI.ShipPickMode.ALL;
        params.averageSMods = 2;
        params.flagshipVariantId = "hmi_fang_onslaught_std";
        final CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);
        if (fleet == null || fleet.isEmpty()) {
            return;
        }
        fleet.setFaction("fang", true);
        fleet.getFlagship().setShipName("FANG Apex Predator");
        fleet.getFlagship().setId("hmi_fang_onslaught_std");
        fleet.getFleetData().addFleetMember("hmi_fang_retribution_elite");
        fleet.getFleetData().addFleetMember("hmi_fang_retribution_elite");
        fleet.getFleetData().addFleetMember("hmi_fang_atlas_cs");
        fleet.getFleetData().addFleetMember("hmi_fang_atlas_cs");
        fleet.getFleetData().addFleetMember("hmi_fang_dominator_assault");
        fleet.getFleetData().addFleetMember("hmi_fang_dominator_assault");
        fleet.getFleetData().addFleetMember("hmi_fang_yowie_brawl");
        fleet.getFleetData().addFleetMember("hmi_fang_mora_Assault");
        fleet.getFleetData().addFleetMember("hmi_fang_mora_Assault");
        fleet.getFleetData().addFleetMember("hmi_fang_eradicator_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_eradicator_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_condor_bomber");
        fleet.getFleetData().addFleetMember("hmi_fang_condor_bomber");
        fleet.getFleetData().addFleetMember("hmi_fang_condor_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_condor_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_caballoloco_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_caballoloco_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_caballoloco_std");
        fleet.getFleetData().addFleetMember("hmi_fang_caballoloco_std");
        fleet.getFleetData().addFleetMember("hmi_fang_burro_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_burro_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_wobbie_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_wobbie_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_vanguard_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_vanguard_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_vanguard_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_vanguard_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_vanguard_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_vanguard_attack");
        fleet.getFleetData().addFleetMember("hmi_fang_charlie_std");
        fleet.getFleetData().addFleetMember("hmi_fang_charlie_std");

        fleet.setNoFactionInName(true);
        fleet.setName("The Confederate Social Fleet");
        taxima.getContainingLocation().addEntity((SectorEntityToken)fleet);
        fleet.setAI(Global.getFactory().createFleetAI(fleet));
        fleet.setLocation(taxima.getLocation().x, taxima.getLocation().y);
        fleet.setFacing((float)Math.random() * 360.0f);
        fleet.getAI().addAssignment(FleetAssignment.DEFEND_LOCATION, taxima, (float)Math.random() * 90000.0f, (Script)null);
    }

    private void addDracoFleet() {
        final SectorAPI sector = Global.getSector();
        final StarSystemAPI system = sector.getStarSystem("Prester John");
        final SectorEntityToken haven = system.getEntityById("draco_havengate");
        final FleetParamsV3 params = new FleetParamsV3(haven.getMarket(), (Vector2f)null, "draco", Float.valueOf(1.0f), "taskForce", 600.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        params.officerNumberMult = 1.0f;
        params.officerLevelBonus = 8;
        params.officerNumberBonus = 4;
        params.officerLevelLimit = Global.getSettings().getInt("officerMaxLevel") + 2;
        params.modeOverride = FactionAPI.ShipPickMode.ALL;
        params.averageSMods = 1;
        params.flagshipVariantId = "hmi_draco_legion_cs";
        final CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);
        if (fleet == null || fleet.isEmpty()) {
            return;
        }
        fleet.setFaction("draco", true);
        fleet.getFlagship().setShipName("DRACO Truth and Purity");
        fleet.getFlagship().setId("hmi_draco_legion_cs");
        fleet.getFleetData().addFleetMember("hmi_draco_forgiveness_attack");
        fleet.getFleetData().addFleetMember("hmi_draco_forgiveness_attack");
        fleet.getFleetData().addFleetMember("hmi_draco_ionos_attack");
        fleet.getFleetData().addFleetMember("hmi_draco_ionos_missile");
        fleet.getFleetData().addFleetMember("hmi_draco_ionos_attack");
        fleet.getFleetData().addFleetMember("hmi_draco_champion_std");
        fleet.getFleetData().addFleetMember("hmi_draco_champion_std");
        fleet.getFleetData().addFleetMember("heron_draco_attack");
        fleet.getFleetData().addFleetMember("heron_draco_std");
        fleet.getFleetData().addFleetMember("heron_draco_std");
        fleet.getFleetData().addFleetMember("hmi_draco_eagle_std");
        fleet.getFleetData().addFleetMember("hmi_draco_eagle_std");
        fleet.getFleetData().addFleetMember("hmi_draco_eagle_std");
        fleet.getFleetData().addFleetMember("hmi_draco_falcon_assault");
        fleet.getFleetData().addFleetMember("hmi_draco_falcon_assault");
        fleet.getFleetData().addFleetMember("hmi_draco_hammerhead_cs");
        fleet.getFleetData().addFleetMember("hmi_draco_hammerhead_cs");
        fleet.getFleetData().addFleetMember("hmi_draco_drover_cs");
        fleet.getFleetData().addFleetMember("hmi_draco_drover_cs");
        fleet.getFleetData().addFleetMember("hmi_draco_pardon_attack");
        fleet.getFleetData().addFleetMember("hmi_draco_pardon_attack");
        fleet.getFleetData().addFleetMember("hmi_draco_appeasement_attack");
        fleet.getFleetData().addFleetMember("hmi_draco_appeasement_attack");
        fleet.getFleetData().addFleetMember("hmi_draco_centurion_cs");
        fleet.getFleetData().addFleetMember("hmi_draco_centurion_cs");

        fleet.setNoFactionInName(true);
        fleet.setName("The Aegis of the Dragon");
        haven.getContainingLocation().addEntity((SectorEntityToken)fleet);
        fleet.setAI(Global.getFactory().createFleetAI(fleet));
        fleet.setLocation(haven.getLocation().x, haven.getLocation().y);
        fleet.setFacing((float)Math.random() * 360.0f);
        fleet.getAI().addAssignment(FleetAssignment.DEFEND_LOCATION, haven, (float)Math.random() * 90000.0f, (Script)null);
    }

    public void onNewGameAfterEconomyLoad() {
        final MarketAPI taxima = Global.getSector().getEconomy().getMarket("fang_taxima");
        if (taxima != null) {
            this.addFangFleet();
        }
        final MarketAPI haven = Global.getSector().getEconomy().getMarket("draco_havengate");
        if (haven != null) {
            this.addDracoFleet();
        }
    }

    @Override
    public void onGameLoad(boolean newGame) {
        syncHMI_SVScripts();
    }

    @Override
    public void onNewGame() {
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!haveNexerelin || SectorManager.getManager().isCorvusMode()) {
            new HMI_SV_gen().generate(Global.getSector());
        }
    }
}


	

