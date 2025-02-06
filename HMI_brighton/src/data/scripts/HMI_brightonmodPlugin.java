package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.GenericPluginManagerAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.thoughtworks.xstream.XStream;
import data.campaign.procgen.*;
import data.scripts.world.Brighton_gen;
import data.scripts.world.HMIDR_procgen;
import exerelin.campaign.SectorManager;
import org.apache.log4j.Level;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class HMI_brightonmodPlugin extends BaseModPlugin {

   public static boolean isExerelin = false;
   public static void syncHMIDRScripts() {
        SectorAPI sector = Global.getSector();
        GenericPluginManagerAPI plugins = sector.getGenericPlugins();

        if (!plugins.hasPlugin(DomResHabitatPluginImpl.class)) {
            plugins.addPlugin(new DomResHabitatPluginImpl(), true);
        }

        if (!plugins.hasPlugin(DomResMiningPluginImpl.class)) {
            plugins.addPlugin(new DomResMiningPluginImpl(), true);
        }

        if (!plugins.hasPlugin(DomResSciencePluginImpl.class)) {
            plugins.addPlugin(new DomResSciencePluginImpl(), true);
        }

        if (!plugins.hasPlugin(DomResSurprise1PluginImpl.class)) {
            plugins.addPlugin(new DomResSurprise1PluginImpl(), true);
        }

        if (!plugins.hasPlugin(DomResSurprise2PluginImpl.class)) {
            plugins.addPlugin(new DomResSurprise2PluginImpl(), true);
        }

        if (!plugins.hasPlugin(DomResSurprise3PluginImpl.class)) {
            plugins.addPlugin(new DomResSurprise3PluginImpl(), true);
        }

        if (!plugins.hasPlugin(DomResSurprise4PluginImpl.class)) {
            plugins.addPlugin(new DomResSurprise4PluginImpl(), true);
        }
//        if (!Global.getSector().hasScript(HMIExecFleetManager.class)) {
//            Global.getSector().addScript(new HMIExecFleetManager());
//      }
    }
	
    @Override
    public void onApplicationLoad() throws Exception {
        isExerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
    }
    @Override
    public void onGameLoad(boolean newGame) {
        {
            syncHMIDRScripts();
        }
    }
	
    @Override
    public void onNewGame() {
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!haveNexerelin || SectorManager.getManager().isCorvusMode()) {
        new Brighton_gen().generate(Global.getSector());
        }
		new HMIDR_procgen().generate(Global.getSector());
    }
}
	

