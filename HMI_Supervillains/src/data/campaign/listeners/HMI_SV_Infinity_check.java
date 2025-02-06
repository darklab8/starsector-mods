package data.campaign.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;

public class HMI_SV_Infinity_check implements EconomyTickListener {

    public static String HMI_SV_VILLIANCHECK = "$hmi_sv_villiancheckinfinity";


    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {
        VillianCheck();
    }

        public static void VillianCheck() {
            if (!areDracoOrFangPresentInGallus()) {
                Global.getSector().getEntityById("hmisv_infinity").getMemoryWithoutUpdate().set(HMI_SV_VILLIANCHECK, true);
            }
        }

        public static boolean areDracoOrFangPresentInGallus() {
            StarSystemAPI sys = Global.getSector().getStarSystem("Gallus");
            if (sys == null) return false;
            for (MarketAPI market : Global.getSector().getEconomy().getMarkets(sys)) {
                if (market.getFactionId().equals("draco") || market.getFactionId().equals("fang"))
                    return true;
            }
            return false;
        }
}
