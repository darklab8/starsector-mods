package data.campaign.submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickMode;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.impl.campaign.submarkets.OpenMarketPlugin;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.util.Highlights;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class HMI_MaxwellLowMarketPlugin extends BaseSubmarketPlugin {


    private boolean playerPaidToUnlock = false;
    private float sinceLastUnlock = 0f;
    private static final float  getUnlockCost = 0.05f;

    public String getIllegalTransferText(CargoStackAPI stack, TransferAction action) {
        if (action == TransferAction.PLAYER_SELL) {
            return "No refunds.";
        }

        return super.getIllegalTransferText(stack, action);
    }

    @Override
    public String getIllegalTransferText(FleetMemberAPI member, TransferAction action) {
        if (action == TransferAction.PLAYER_SELL) {
            return "No refunds.";
        }

        return super.getIllegalTransferText(member, action);
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        float days = Global.getSector().getClock().convertToDays(amount);
        sinceLastUnlock += days;
        if (sinceLastUnlock > 7f) {
            playerPaidToUnlock = false;
        }
    }

    @Override
    public void init(SubmarketAPI submarket) {
        super.init(submarket);
    }

    @Override
    public float getTariff() {
            return -0.50f;
    }


    @Override
    public boolean isIllegalOnSubmarket(String commodityId, TransferAction action) {
        if (action == TransferAction.PLAYER_SELL) {
            return true;
        }
        if (market.hasCondition(Conditions.FREE_PORT)) {
            return false;
        }
        return submarket.getFaction().isIllegal(commodityId);
    }

    @Override
    public boolean isIllegalOnSubmarket(CargoStackAPI stack, TransferAction action) {
        if (action == TransferAction.PLAYER_SELL) {
            return true;
        }
        if (!stack.isCommodityStack()) {
            return false;
        }
        return isIllegalOnSubmarket((String) stack.getData(), action);
    }

    @Override
    public boolean isIllegalOnSubmarket(FleetMemberAPI member, TransferAction action) {
        return action == TransferAction.PLAYER_SELL;
    }



    @Override
    public DialogOption[] getDialogOptions(CoreUIAPI ui) {
        if (canPlayerAffordUnlock()) {
            return new DialogOption[]{
                    new DialogOption("Pay", new Script() {
                        @Override
                        public void run() {
                            Global.getSector().adjustPlayerReputation((-getUnlockCost * 100f), "hmi_maxwell");
                            Global.getSector().getFaction("hmi_maxwell").adjustRelationship(Factions.PLAYER, -getUnlockCost);
                            playerPaidToUnlock = true;
                            sinceLastUnlock = 0f;
                        }
                    }),
                    new DialogOption("Never mind", null)
            };
        } else {
            return new DialogOption[]{
                    new DialogOption("Never mind", null)
            };
        }
    }

    @Override
    public String getDialogText(CoreUIAPI ui) {
        int relationshipUI = (int) (getUnlockCost * 100);
        if (canPlayerAffordUnlock()) {

            return "\"Access to the Maxwell Explorer Market costs "
                    + (relationshipUI) + " Relationshares.\"";
        } else {
            return "\"Access to the Maxwell Explorer Market costs "
                    + (relationshipUI) + " Relationshares. Please "
                    + "redeem Relationshares from depositing AI cores "
                    + "at your local Maxwell Affiliate.\"";
        }
    }

    @Override
    public Highlights getDialogTextHighlights(CoreUIAPI ui) {
        Highlights h = new Highlights();
        h.setText("" + (getUnlockCost * 100f));
        if (canPlayerAffordUnlock()) {
            h.setColors(Misc.getHighlightColor());
        } else {
            h.setColors(Misc.getNegativeHighlightColor());
        }
        return h;
    }

    @Override
    public OnClickAction getOnClickAction(CoreUIAPI ui) {
        if (playerPaidToUnlock) {
            return OnClickAction.OPEN_SUBMARKET;
        }
        return OnClickAction.SHOW_TEXT_DIALOG;
    }

    @Override
    public String getTooltipAppendix(CoreUIAPI ui) {
//       float relationship = submarket.getFaction().getRelToPlayer().getRel();
//       if (relationship <= getUnlockCost && !playerPaidToUnlock) {
        if (playerPaidToUnlock == false) {
            int relationshipUI = (int) (getUnlockCost * 100);
            int relationshipint = (int) (submarket.getFaction().getRelToPlayer().getRel()*100);
            return "Requires: " + relationshipUI + " Relationshares. You currently have " + relationshipint + " Relationshares.";
        }
        return super.getTooltipAppendix(ui);
    }

    @Override
    public boolean isEnabled(CoreUIAPI ui) {
        float relationship = submarket.getFaction().getRelToPlayer().getRel();
        return relationship >= getUnlockCost;
    }



    public void updateCargoPrePlayerInteraction() {
        float seconds = Global.getSector().getClock().convertToSeconds(sinceLastCargoUpdate);
        addAndRemoveStockpiledResources(seconds, false, true, true);
        sinceLastCargoUpdate = 0f;

        if (okToUpdateShipsAndWeapons()) {
            sinceSWUpdate = 0f;

            pruneWeapons(0f);

            int weapons = 10 + Math.max(0, market.getSize() - 1) + (Misc.isMilitary(market) ? 5 : 0);
            int fighters = 3 + Math.max(0, (market.getSize() - 1) / 2) + (Misc.isMilitary(market) ? 2 : 0);


            addWeapons(weapons, weapons + 8, 1, "hmi_maxwell");
            addFighters(fighters, fighters + 4, 1, "hmi_maxwell");


            getCargo().getMothballedShips().clear();

            FleetMemberAPI memberapogee = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "apogee_hmimax_Hull");
            memberapogee.getRepairTracker().setMothballed(true);
            memberapogee.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(memberapogee);


            FleetMemberAPI memberhippo = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "hippocampus_hmimax_Hull");
            memberhippo.getRepairTracker().setMothballed(true);
            memberhippo.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(memberhippo);


            FleetMemberAPI membermule = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "mule_hmimax_Hull");
            membermule.getRepairTracker().setMothballed(true);
            membermule.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(membermule);

            FleetMemberAPI membermule2 = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "mule_hmimax_Hull");
            membermule2.getRepairTracker().setMothballed(true);
            membermule2.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(membermule2);

            FleetMemberAPI membermule3 = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "mule_hmimax_Hull");
            membermule3.getRepairTracker().setMothballed(true);
            membermule3.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(membermule3);


            FleetMemberAPI membercondor = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "condor_hmimax_Hull");
            membercondor.getRepairTracker().setMothballed(true);
            membercondor.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(membercondor);

            FleetMemberAPI membercondor2 = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "condor_hmimax_Hull");
            membercondor2.getRepairTracker().setMothballed(true);
            membercondor2.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(membercondor2);


            FleetMemberAPI membercerberus = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "cerberus_hmimax_Hull");
            membercerberus.getRepairTracker().setMothballed(true);
            membercerberus.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(membercerberus);

            FleetMemberAPI membercerberus2 = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "cerberus_hmimax_Hull");
            membercerberus2.getRepairTracker().setMothballed(true);
            membercerberus2.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(membercerberus2);

            FleetMemberAPI membercerberus3 = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "cerberus_hmimax_Hull");
            membercerberus3.getRepairTracker().setMothballed(true);
            membercerberus3.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(membercerberus3);


            FleetMemberAPI memberwayfarer = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "wayfarer_hmimax_Hull");
            memberwayfarer.getRepairTracker().setMothballed(true);
            memberwayfarer.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(memberwayfarer);

            FleetMemberAPI memberwayfarer2 = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "wayfarer_hmimax_Hull");
            memberwayfarer2.getRepairTracker().setMothballed(true);
            memberwayfarer2.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(memberwayfarer2);

            FleetMemberAPI memberwayfarer3 = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "wayfarer_hmimax_Hull");
            memberwayfarer3.getRepairTracker().setMothballed(true);
            memberwayfarer3.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(memberwayfarer3);


            FleetMemberAPI member_remora = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "remora_hmimax_Hull");
            member_remora.getRepairTracker().setMothballed(true);
            member_remora.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(member_remora);

            FleetMemberAPI member_remora2 = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "remora_hmimax_Hull");
            member_remora2.getRepairTracker().setMothballed(true);
            member_remora2.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(member_remora2);

            FleetMemberAPI member_remora3 = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "remora_hmimax_Hull");
            member_remora3.getRepairTracker().setMothballed(true);
            member_remora3.getRepairTracker().setCR(0.5f);
            getCargo().getMothballedShips().addFleetMember(member_remora3);


            float freighters = 10f;
            CommodityOnMarketAPI com = market.getCommodityData(Commodities.SHIPS);
            freighters += com.getMaxSupply() * 2f;
            if (freighters > 20) freighters = 20;

            addShips(market.getFactionId(),
                    10f, // combat
                    freighters, // freighter
                    0f, // tanker
                    10f, // transport
                    10f, // liner
                    5f, // utilityPts
                    null, // qualityOverride
                    0f, // qualityMod
                    ShipPickMode.PRIORITY_THEN_ALL,
                    null);

            float tankers = 10f;
            com = market.getCommodityData(Commodities.FUEL);
            tankers += com.getMaxSupply() * 2f;
            if (tankers > 20) tankers = 20;
            //tankers = 40;
            addShips(market.getFactionId(),
                    0f, // combat
                    0f, // freighter
                    tankers, // tanker
                    0, // transport
                    0f, // liner
                    0f, // utilityPts
                    null, // qualityOverride
                    0f, // qualityMod
                    ShipPickMode.PRIORITY_THEN_ALL,
                    null);


            addHullMods(1, 1 + itemGenRandom.nextInt(3));
        }

        getCargo().sort();
    }

    protected Object writeReplace() {
        if (okToUpdateShipsAndWeapons()) {
            pruneWeapons(0f);
            getCargo().getMothballedShips().clear();
        }
        return this;
    }


    public boolean shouldHaveCommodity(CommodityOnMarketAPI com) {
        return !market.isIllegal(com);
    }

    @Override
    public int getStockpileLimit(CommodityOnMarketAPI com) {

        //Just eliminate everything, we only want weapons and fighters and hullmods
        float limit = OpenMarketPlugin.getBaseStockpileLimit(com);
        limit *= 0;
        if (limit < 0) limit = 0;

        return (int) limit;
    }

    @Override
    public PlayerEconomyImpactMode getPlayerEconomyImpactMode() {
        return PlayerEconomyImpactMode.PLAYER_SELL_ONLY;
    }


    @Override
    public boolean isOpenMarket() {
        return true;
    }


    @Override
    public Highlights getTooltipAppendixHighlights(CoreUIAPI ui) {
        return super.getTooltipAppendixHighlights(ui);
    }



    private boolean canPlayerAffordUnlock() {

        float relationship = submarket.getFaction().getRelToPlayer().getRel();
        return relationship >= getUnlockCost;

    }


}