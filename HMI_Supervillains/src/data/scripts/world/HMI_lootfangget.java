package data.scripts.world;

		import com.fs.starfarer.api.EveryFrameScript;
		import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
		import com.fs.starfarer.api.campaign.CampaignFleetAPI;
		import com.fs.starfarer.api.campaign.CargoAPI;
		import com.fs.starfarer.api.campaign.FleetEncounterContextPlugin;
		import com.fs.starfarer.api.campaign.FleetEncounterContextPlugin.FleetMemberData;
		import com.fs.starfarer.api.fleet.FleetMemberAPI;
		import com.fs.starfarer.api.impl.campaign.ids.Commodities;
		import org.lazywizard.lazylib.MathUtils;

		import java.util.ArrayList;
		import java.util.List;

public class HMI_lootfangget extends BaseCampaignEventListener implements EveryFrameScript {

	public static final float MEAT_PER_HULL_POINT = 0.001f;

	public HMI_lootfangget() {
		super(true);
	}

	// Drops mess mass, courtesy of Histidine
	@Override
	public void reportEncounterLootGenerated(FleetEncounterContextPlugin plugin, CargoAPI loot) {
		CampaignFleetAPI loser = plugin.getLoser();
		if (loser == null) return;

		int totalHull = 0;
		int fangHull = 0;
		List<FleetMemberAPI> deadShips = new ArrayList<>();
		List<FleetEncounterContextPlugin.FleetMemberData> casualties = plugin.getLoserData().getOwnCasualties();
		for (FleetMemberData memberData : casualties) {
			FleetEncounterContextPlugin.Status status = memberData.getStatus();
			if (status == FleetEncounterContextPlugin.Status.DESTROYED || status == FleetEncounterContextPlugin.Status.NORMAL) continue;
			FleetMemberAPI member = memberData.getMember();
			deadShips.add(member);
			int fp = member.getFleetPointCost();
			float hull = member.getHullSpec().getHitpoints() + member.getHullSpec().getArmorRating() * 4;
			if (member.isFighterWing())
				hull *= member.getNumFightersInWing();

			totalHull += hull;
			if (member.getHullId().startsWith("hmi_fang"))
			{
				fangHull += hull;
			}
		}

		float contrib = plugin.computePlayerContribFraction();
		fangHull *= contrib;

		if (fangHull != 0)
		{
			int nummess = (int)(fangHull * MEAT_PER_HULL_POINT * MathUtils.getRandomNumberInRange(0.75f, 1.25f));
			loot.addCommodity(Commodities.ORGANS, nummess);
			loot.addCommodity(Commodities.METALS, -nummess);
		}
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public boolean runWhilePaused() {
		return false;
	}

	@Override
	public void advance(float amount) {

	}

}
