package data.campaign.econ;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class hmi_infinity_draco extends BaseMarketConditionPlugin {

    public static final float ACCESS_PENALTY = -35f;
    public static final float STABILITY_PENALTY = -3f;


    @Override
    public void apply(String id) {

//        if (!NoDraco(market)) {
//            unapply(id);
//            return;
//        }
                market.getStability().modifyFlat(id, STABILITY_PENALTY, "Draco Presence");
                market.getAccessibilityMod().modifyFlat(id, ACCESS_PENALTY / 100f, "Draco Presence");
    }

    @Override
    public void unapply(String id) {
        market.getStability().unmodify(id);
        market.getAccessibilityMod().unmodifyFlat(id);
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);

        tooltip.addPara("%s accessibility.",
                10f, Misc.getHighlightColor(),
                "+" + (int) ACCESS_PENALTY + "%");
    }


    public static boolean NoDraco(MarketAPI market) {
        StarSystemAPI thissystem = market.getStarSystem();
        boolean hasDraco = false;
        for (MarketAPI othermarket : Global.getSector().getEconomy().getMarkets(thissystem)) {
            if (othermarket.getFactionId().equals("draco")) hasDraco = true;
        }
        return hasDraco;
    }
}
