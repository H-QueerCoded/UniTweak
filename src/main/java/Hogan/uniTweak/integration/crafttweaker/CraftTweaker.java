package Hogan.uniTweak.integration.crafttweaker;

import Hogan.uniTweak.integration.crafttweaker.crossmod.*;
import wanion.unidict.UniDict;
import wanion.unidict.api.UniDictAPI;

public class CraftTweaker {
	public static void postInit() {
		final UniDictAPI uniDictAPI = UniDict.getAPI();
		ImmersiveEngineering.postInit(uniDictAPI);
		Embers.postInit(uniDictAPI);
	}
}
