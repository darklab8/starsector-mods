package shipmastery.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ColonyInteractionListener;
import com.fs.starfarer.api.campaign.listeners.CoreUITabListener;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.campaign.listeners.ShipRecoveryListener;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import shipmastery.util.VariantLookup;

import java.util.List;

public class PlayerFleetHandler implements ColonyInteractionListener, ShipRecoveryListener, CoreUITabListener,
                                           EconomyTickListener {

    @Override
    public void reportPlayerOpenedMarket(MarketAPI market) {
//        for (SubmarketAPI sub : market.getSubmarketsCopy()) {
//            CargoAPI cargo = sub.getCargo();
//            if (cargo != null) {
//                FleetDataAPI mothballed = cargo.getMothballedShips();
//                FleetDataAPI carrying = cargo.getFleetData();
//                List<FleetMemberAPI> fms = new ArrayList<>();
//                if (mothballed != null) {
//                    fms.addAll(mothballed.getMembersListCopy());
//                }
//                if (carrying != null) {
//                    fms.addAll(carrying.getMembersListCopy());
//                }
//                for (FleetMemberAPI fm : fms) {
//                    int level = ShipMastery.getPlayerMasteryLevel(fm.getHullSpec());
//                    if (level > 0) {
//                        fm.getVariant().addMod("sms_npcIndicator" + level);
//                    }
//                }
//            }
//        }
    }
    @Override
    public void reportPlayerClosedMarket(MarketAPI market) {}
    @Override
    public void reportPlayerOpenedMarketAndCargoUpdated(MarketAPI market) {}

    @Override
    public void reportEconomyMonthEnd() {}

    @Override
    public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {
        addMasteryHandlerToPlayerFleet();
    }

    @Override
    public void reportAboutToOpenCoreTab(CoreUITabId id, Object o) {
        if (id == CoreUITabId.FLEET || id == CoreUITabId.REFIT) {
            addMasteryHandlerToPlayerFleet();
        }
    }

    @Override
    public void reportShipsRecovered(List<FleetMemberAPI> fms, InteractionDialogAPI dialog) {
        addMasteryHandlerToPlayerFleet();
    }


    @Override
    public void reportEconomyTick(int tick) {
        // Also check periodically in case none of the above catch a ship being added to the player's fleet
        addMasteryHandlerToPlayerFleet();
    }

    public static void addMasteryHandlerToPlayerFleet() {
        for (FleetMemberAPI fm : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            ShipVariantAPI variant = fm.getVariant();
            VariantLookup.VariantInfo variantInfo = VariantLookup.getVariantInfo(variant);
            if (!variant.hasHullMod("sms_masteryHandler")
                    || variantInfo == null
                    || variantInfo.fleet != Global.getSector().getPlayerFleet()
                    || variant.isStockVariant() || variant.isGoalVariant()) {
                fm.setVariant(FleetHandler.addHandlerMod(variant, variant, fm), false, false);
            }
            // Remove NPC mastery indicators if they exist
            for (int i = 1; i <= 9; i++) {
                fm.getVariant().removeMod("sms_npcIndicator" + i);
            }
        }
    }
}
