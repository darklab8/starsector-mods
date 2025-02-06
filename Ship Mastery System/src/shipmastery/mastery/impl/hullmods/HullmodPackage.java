package shipmastery.mastery.impl.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import shipmastery.mastery.BaseMasteryEffect;
import shipmastery.mastery.MasteryDescription;

public abstract class HullmodPackage extends BaseMasteryEffect {
    @Override
    public MasteryDescription getDescription(ShipAPI selectedModule, FleetMemberAPI selectedFleetMember) {
        return MasteryDescription.initDefaultHighlight(getDescriptionString()).params((Object[]) getDescriptionParams(selectedModule));
    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats) {
        ShipVariantAPI variant = stats.getVariant();
        if (variant == null) return;
        int count = 0;
        for (HullmodData data : getHullmodList()) {
            if (variant.hasHullMod(data.id) &&
                    (!data.requireBuiltIn ||
                            (variant.getSMods().contains(data.id) ||
                                    variant.getHullSpec().getBuiltInMods().contains(data.id)))) {
                count++;
            }
        }
        if (count >= getRequiredCount()) {
            apply(hullSize, stats);
        }
    }

    protected abstract String getDescriptionString();

    protected abstract String[] getDescriptionParams(ShipAPI selectedModule);

    protected abstract HullmodData[] getHullmodList();

    protected abstract int getRequiredCount();
    protected abstract void apply(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats);

    protected static class HullmodData {
        String id;
        boolean requireBuiltIn;
        protected HullmodData(String id, boolean requireBuiltIn) {
            this.id = id;
            this.requireBuiltIn = requireBuiltIn;
        }
    }
}
