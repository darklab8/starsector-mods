package data.campaign.fleets;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickMode;
import com.fs.starfarer.api.campaign.ai.CampaignFleetAIAPI.EncounterOption;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.fleets.DisposableFleetManager;
import com.fs.starfarer.api.campaign.SectorEntityToken.VisibilityLevel;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.intel.bases.PirateBaseManager;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;

import java.util.List;

public class FangFleetManager extends DisposableFleetManager {

    public static int MAX_FANG_FLEETS = 1;
    private static final float MAX_LY_FROM_FANG = 10f;

    @Override
    protected Object readResolve() {
        super.readResolve();
        return this;
    }

    @Override
    protected String getSpawnId() {
        return "fang_spawn"; // not a faction id, just an identifier for this spawner
    }

    @Override
    protected int getDesiredNumFleetsForSpawnLocation() {
        MarketAPI market = getLargestMarket();
        MarketAPI closestMarket = getClosestFANG();

        float desiredNumFleets;
        if (closestMarket == null) {
            desiredNumFleets = 0f;
        } else {
            float distScale = 1f - Math.min(1f, Misc.getDistanceToPlayerLY(closestMarket.getLocationInHyperspace()) / MAX_LY_FROM_FANG);
            desiredNumFleets = 1f * distScale;
        }

        if (market != null) {
            desiredNumFleets += Math.max(0f, (market.getSize() - 2f) * 0.5f);
        }

        int level = getGFANGLevel();
        desiredNumFleets += level * 0.5f;

        return (int) Math.round(desiredNumFleets * MAX_FANG_FLEETS);
    }

    protected int getGFANGLevel() {
        if (currSpawnLoc == null) {
            return 0;
        }
        int total = 0;

        for (MarketAPI market : Global.getSector().getEconomy().getMarkets(currSpawnLoc)) {
            if (market.isHidden()) {
                continue;
            }
            if (market.hasCondition(Conditions.DECIVILIZED)) {
                continue;
            }

            if (market.hasCondition("hmi_fang_spawn")) {
                total++;
            }
            if (market.getFactionId() == "fang") {
                total++;
            }

            }
        return total;
    }

    protected MarketAPI getClosestFANG() {
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        if (player == null) {
            return null;
        }

        MarketAPI closest = null;
        float minDistance = 100000f;
        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (market.isHidden()) {
                continue;
            }
            if (market.hasCondition(Conditions.DECIVILIZED)) {
                continue;
            }
            if (!market.hasCondition("hmi_fang_spawn")) {
                continue;
            }
            float distance = Misc.getDistanceToPlayerLY(market.getLocationInHyperspace());
            if (distance < minDistance) {
                closest = market;
                minDistance = distance;
            }
        }
        return closest;
    }


    protected MarketAPI getLargestMarket() {
        if (currSpawnLoc == null) {
            return null;
        }
        MarketAPI largest = null;
        int maxSize = 0;
        for (MarketAPI market : Global.getSector().getEconomy().getMarkets(currSpawnLoc)) {
            if (market.isHidden()) {
                continue;
            }
            if (market.hasCondition(Conditions.DECIVILIZED)) {
                continue;
            }
            if (!market.hasCondition("hmi_fang_spawn")) {
                continue;
            }

            if (market.getSize() > maxSize) {
                maxSize = (market.getSize());
                largest = market;
            }
        }
        return largest;
    }

    @Override
    protected float getExpireDaysPerFleet() {
        /* Bigger fleets, slower wind-down */
        return 20f;
    }

    @Override
    protected CampaignFleetAPI spawnFleetImpl() {

        StarSystemAPI system = currSpawnLoc;
        if (system == null) {
            return null;
        }

        float combat = MathUtils.getRandomNumberInRange(10f, MathUtils.getRandomNumberInRange(40f, MathUtils.getRandomNumberInRange(80f, 120f)));

        float timeFactor = (PirateBaseManager.getInstance().getDaysSinceStart() - 180f) / (365f * 2f);
        if (timeFactor < 0f) {
            timeFactor = 0f;
        }
        if (timeFactor > 1f) {
            timeFactor = (float) Math.sqrt(timeFactor);
        }
        if (timeFactor > 2f) {
            timeFactor = 2f;
        }
        combat *= 1f + MathUtils.getRandomNumberInRange(0f, timeFactor);

        float levelFactor = 0f;
        if (Global.getSector().getPlayerPerson() != null) {
            levelFactor = Global.getSector().getPlayerPerson().getStats().getLevel() / 50f;
        }

        combat *= 1f + MathUtils.getRandomNumberInRange(0f, levelFactor);
        float freighter = Math.round(combat * MathUtils.getRandomNumberInRange(0.05f, 0.1f));
        float tanker = Math.round(combat * MathUtils.getRandomNumberInRange(0f, 0.1f));
        float utility = Math.round((freighter + tanker) * MathUtils.getRandomNumberInRange(0f, 0.5f));

        String fleetType;
        if (combat < 50) {
            fleetType = FleetTypes.PATROL_SMALL;
        } else if (combat < 100) {
            fleetType = FleetTypes.PATROL_MEDIUM;
        } else {
            fleetType = FleetTypes.PATROL_LARGE;
        }

        FleetParamsV3 params = new FleetParamsV3(
                null, // market
                system.getLocation(), // location
                "fang", // fleet's faction, if different from above, which is also used for source market picking
                1.0f,
                fleetType,
                combat, // combatPts
                freighter, // freighterPts
                tanker, // tankerPts
                0f, // transportPts
                0f, // linerPts
                utility, // utilityPts
                0 // qualityBonus
        );

        FactionDoctrineAPI doctrine = Global.getSector().getFaction("fang").getDoctrine().clone();

        FANGFleetType fangFleetType;
        if (combat < 80 && Math.random() < 0.3) {
            fangFleetType = FANGFleetType.FRIGATES;
            doctrine.setShipSize(1);
            doctrine.setWarships(4);
            doctrine.setCarriers(1);
            doctrine.setPhaseShips(2);
        } else if (combat >= 40 && combat < 120 && Math.random() < 0.3) {
            fangFleetType = FANGFleetType.DESTROYERS;
            doctrine.setShipSize(2);
            doctrine.setWarships(4);
            doctrine.setCarriers(2);
            doctrine.setPhaseShips(1);
        } else if (combat >= 80 && Math.random() < 0.3) {
            fangFleetType = FANGFleetType.CRUISERS;
            doctrine.setShipSize(4);
            doctrine.setWarships(5);
            doctrine.setCarriers(1);
            doctrine.setPhaseShips(1);
        } else if (combat >= 60 && Math.random() < 0.4) {
            fangFleetType = FANGFleetType.CARRIERS;
            doctrine.setShipSize(3);
            doctrine.setWarships(2);
            doctrine.setCarriers(5);
            doctrine.setPhaseShips(1);
        }  else {
            fangFleetType = FANGFleetType.BALANCED;
            doctrine.setShipSize(2);
            doctrine.setWarships(5);
            doctrine.setCarriers(2);
            doctrine.setPhaseShips(1);
        }

        params.doctrineOverride = doctrine;
        params.ignoreMarketFleetSizeMult = true;
        params.officerLevelBonus = 1;
        params.officerNumberBonus = 4;
        params.forceAllowPhaseShipsEtc = false;
        params.modeOverride = ShipPickMode.PRIORITY_THEN_ALL;

        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

        if ((fleet == null) || fleet.isEmpty()) {
            return null;
        }

        if (combat < 50) {
            fleet.getCommander().setRankId(Ranks.SPACE_COMMANDER);
        } else if (combat < 100) {
            fleet.getCommander().setRankId(Ranks.SPACE_CAPTAIN);
        } else {
            fleet.getCommander().setRankId(Ranks.SPACE_ADMIRAL);
        }

        switch (fangFleetType) {
            case FRIGATES:
                fleet.setName("Young Scouts");
                break;
            case DESTROYERS:
                fleet.setName("Hunter Fleet");
                break;
            case CRUISERS:
                fleet.setName("Predator Fleet");
                break;
            case CARRIERS:
                fleet.setName("Ravenous Swarm");
                break;
            default:
            case BALANCED:
                if (combat < 50) {
                    fleet.setName("Hunter Pack");
                } else if (combat < 100) {
                    fleet.setName("Great Hunt");
                } else {
                    fleet.setName("Cheiftain's Hunt");
                }
                break;
        }

        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_FLEET_TYPE, "gmdaFleet");
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PIRATE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_NO_MILITARY_RESPONSE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_FIGHT_TO_THE_LAST, true);

        setLocationAndOrders(fleet, 0f, 0f);

        return fleet;
    }




    private static enum FANGFleetType {
        FRIGATES, DESTROYERS, CRUISERS, CAPITALS, CARRIERS, PHASE, BALANCED
    }
}
