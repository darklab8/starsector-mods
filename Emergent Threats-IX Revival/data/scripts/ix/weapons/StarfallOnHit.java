package data.scripts.ix.weapons;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

import org.magiclib.util.MagicLensFlare;

public class StarfallOnHit implements OnHitEffectPlugin {
	
	public void onHit(DamagingProjectileAPI proj, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		MagicLensFlare.createSharpFlare(
        engine,
        proj.getSource(),
        point,
		8,		//thickness
        400,	//length
        0,		//angle
        new Color(50,50,255),	//fringe
        new Color(100,100,255)	//core
        );
		Global.getSoundPlayer().playSound("starfall_mlrs_ix_hit", 1f, 1f, point, new Vector2f());
	}
}