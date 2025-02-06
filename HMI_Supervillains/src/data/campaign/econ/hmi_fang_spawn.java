package data.campaign.econ;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.ConditionData;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.Arrays;

public class hmi_fang_spawn extends BaseMarketConditionPlugin {

	private static String [] fangFactions = new String [] {
		"fang",
	};
	public static float DEFENSE_BONUS = 1000;

	public void apply(String id) {
		if (Arrays.asList(fangFactions).contains(market.getFactionId())) {
			market.getStability().modifyFlat(id, ConditionData.STABILITY_LUDDIC_MAJORITY_BONUS, "Fang Support");
		} else {
			market.getStability().modifyFlat(id, ConditionData.STABILITY_LUDDIC_MAJORITY_PENALTY, "Fang Resistance");
		}
		Industry industry = market.getIndustry(Industries.POPULATION);
		if(industry!=null){
			industry.getDemand(Commodities.DRUGS).getQuantity().modifyFlat(id + "_0", 2);
		}
		float mult = market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).getBonusMult();
		if (Arrays.asList(fangFactions).contains(market.getFactionId())) {
			market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyFlat(id, DEFENSE_BONUS/mult, "Resilient Draco Group Defenses");
		}
	}

	public void unapply(String id) { market.getStability().unmodify(id);
	}

	@Override
	protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
		super.createTooltipAfterDescription(tooltip, expanded);

		tooltip.addPara(
				"%s defense rating.",
				10f,
				Misc.getHighlightColor(),
				"+" + (int)DEFENSE_BONUS
		);
	}
}
