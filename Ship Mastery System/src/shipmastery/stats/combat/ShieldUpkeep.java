
package shipmastery.stats.combat;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import shipmastery.stats.ShipStat;
import shipmastery.util.Utils;

public class ShieldUpkeep extends ShipStat {
    @Override
    public Object get(MutableShipStatsAPI stats) {
        return stats.getShieldUpkeepMult();
    }

    @Override
    public Float getSelectionWeight(ShipHullSpecAPI spec) {
        // No civilian ships
        if (spec.isCivilianNonCarrier()) return null;
        if (!Utils.hasShield(spec)) return null;
        // Prefer ships with higher shield upkeep
        return Utils.getSelectionWeightScaledByValue(spec.getShieldSpec().getUpkeepCost(), 250f, false);
    }
}
