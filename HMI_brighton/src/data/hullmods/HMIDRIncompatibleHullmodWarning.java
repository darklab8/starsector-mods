package data.hullmods;  
  
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.BaseHullMod;  
  
public class HMIDRIncompatibleHullmodWarning extends BaseHullMod {
      
    @Override  
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {  
        if (index == 0) return "WARNING";  
          
        return null;  
    }  
} 