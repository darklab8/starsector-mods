package shipmastery.mastery.impl.stats;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.Misc;
import shipmastery.stats.ShipStat;
import shipmastery.stats.StatTags;
import shipmastery.util.Strings;
import shipmastery.util.Utils;

import java.util.List;
import java.util.Map;

public class ModifyStatsFlat extends ModifyStatsEffect {

    @Override
    float getModifiedAmount(ShipStat stat, float amount) {
        if (stat.tags.contains(StatTags.TAG_REQUIRE_INTEGER)) {
            if (amount < 1f && amount > 0f) return 1f;
            if (amount > -1f && amount < 0f) return -1f;
            return (int) amount;
        }
        if (stat.tags.contains(StatTags.TAG_IS_PERCENT)) {
            amount *= 100f;
        }
        return amount;
    }

    @Override
    String getAmountString(ShipStat stat, float modifiedAmount) {
        if (stat.tags.contains(StatTags.TAG_DISPLAY_AS_PERCENT)) {
            return Utils.absValueAsPercent(modifiedAmount);
        }
        if (stat.tags.contains(StatTags.TAG_IS_PERCENT)) {
            return Utils.absValueAsPercent(modifiedAmount / 100f);
        }
        if (stat.tags.contains(StatTags.TAG_REQUIRE_INTEGER)) {
            return Utils.asInt(Math.abs(modifiedAmount));
        }
        return Misc.getFormat().format(Math.abs(modifiedAmount));
    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats) {
        for (Map.Entry<ShipStat, Float> entry : amounts.entrySet()) {
            ShipStat stat = entry.getKey();
            float amount = getModifiedAmount(stat, getStrength(stats) * entry.getValue());
            modify(stat.get(stats), id + "_" + stat.getClass().getSimpleName(), amount);
        }
    }

    void modify(Object stat, String id, float amount) {
        if (stat instanceof StatBonus) {
            ((StatBonus) stat).modifyFlat(id, amount, Strings.Misc.shipMasteryEffect);
        }
        else if (stat instanceof MutableStat) {
            ((MutableStat) stat).modifyFlat(id, amount, Strings.Misc.shipMasteryEffect);
        }
        else {
            for (Object o : (Object[]) stat) {
                modify(o, id, amount);
            }
        }
    }

    @Override
    public List<String> generateRandomArgs(ShipHullSpecAPI spec, int maxTier, long seed) {
        return super.generateRandomArgs(spec, maxTier, seed, true);
    }
}
