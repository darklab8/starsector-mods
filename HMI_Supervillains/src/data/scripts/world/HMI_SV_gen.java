package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import data.scripts.world.systems.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("unchecked")
    public class HMI_SV_gen implements SectorGeneratorPlugin {
    @Override
    public void generate(SectorAPI sector) {

        new HMI_SV_phylum().generate(sector);
        new HMI_SV_presterjohn().generate(sector);
        new HMI_SV_gallus().generate(sector);

        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("fang");
		SharedData.getData().getPersonBountyEventData().addParticipatingFaction("draco");
//    }
//    
//        public static void initFactionRelationships(SectorAPI sector) {
        FactionAPI fang = sector.getFaction("fang");
        FactionAPI draco = sector.getFaction("draco");
        FactionAPI player = sector.getFaction(Factions.PLAYER);
        FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
        FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI independent = sector.getFaction(Factions.INDEPENDENT);
        FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
        FactionAPI kol = sector.getFaction(Factions.KOL);
        FactionAPI diktat = sector.getFaction(Factions.DIKTAT);
        FactionAPI persean = sector.getFaction(Factions.PERSEAN);

        fang.setRelationship("player_npc", RepLevel.HOSTILE);
        fang.setRelationship(Factions.PLAYER, RepLevel.HOSTILE);
        fang.setRelationship(hegemony.getId(), RepLevel.SUSPICIOUS);
        fang.setRelationship(tritachyon.getId(), RepLevel.INHOSPITABLE);
        fang.setRelationship(pirates.getId(), RepLevel.HOSTILE);
        fang.setRelationship(independent.getId(), RepLevel.HOSTILE);
        fang.setRelationship(persean.getId(), RepLevel.INHOSPITABLE);
        fang.setRelationship(church.getId(), RepLevel.INHOSPITABLE);
        fang.setRelationship(path.getId(), RepLevel.HOSTILE);
        fang.setRelationship(kol.getId(), RepLevel.SUSPICIOUS);
        fang.setRelationship(diktat.getId(), RepLevel.SUSPICIOUS);
        fang.setRelationship("exigency", RepLevel.INHOSPITABLE);
        fang.setRelationship("shadow_industry", RepLevel.INHOSPITABLE);
        fang.setRelationship("mayorate", RepLevel.INHOSPITABLE);
        fang.setRelationship("blackrock", RepLevel.INHOSPITABLE);
        fang.setRelationship("tiandong", RepLevel.INHOSPITABLE);
        fang.setRelationship("SCY", RepLevel.INHOSPITABLE);
        fang.setRelationship("neutrinocorp", RepLevel.INHOSPITABLE);
        fang.setRelationship("interstellarimperium", RepLevel.INHOSPITABLE);
        fang.setRelationship("diableavionics", RepLevel.INHOSPITABLE);
        fang.setRelationship("ora", RepLevel.INHOSPITABLE);
        fang.setRelationship("draco", RepLevel.VENGEFUL);

        draco.setRelationship("player_npc", RepLevel.HOSTILE);
        draco.setRelationship(Factions.PLAYER, RepLevel.HOSTILE);
        draco.setRelationship(hegemony.getId(), RepLevel.INHOSPITABLE);
        draco.setRelationship(tritachyon.getId(), RepLevel.NEUTRAL);
        draco.setRelationship(pirates.getId(), RepLevel.HOSTILE);
        draco.setRelationship(independent.getId(), RepLevel.HOSTILE);
        draco.setRelationship(persean.getId(), RepLevel.SUSPICIOUS);
        draco.setRelationship(church.getId(), RepLevel.INHOSPITABLE);
        draco.setRelationship(path.getId(), RepLevel.HOSTILE);
        draco.setRelationship(kol.getId(), RepLevel.INHOSPITABLE);
        draco.setRelationship(diktat.getId(), RepLevel.INHOSPITABLE);
        draco.setRelationship("exigency", RepLevel.INHOSPITABLE);
        draco.setRelationship("shadow_industry", RepLevel.INHOSPITABLE);
        draco.setRelationship("mayorate", RepLevel.INHOSPITABLE);
        draco.setRelationship("blackrock", RepLevel.INHOSPITABLE);
        draco.setRelationship("tiandong", RepLevel.INHOSPITABLE);
        draco.setRelationship("SCY", RepLevel.INHOSPITABLE);
        draco.setRelationship("neutrinocorp", RepLevel.INHOSPITABLE);
        draco.setRelationship("interstellarimperium", RepLevel.INHOSPITABLE);
        draco.setRelationship("diableavionics", RepLevel.INHOSPITABLE);
        draco.setRelationship("ora", RepLevel.INHOSPITABLE);
        draco.setRelationship("fang", RepLevel.VENGEFUL);
    }
}
