package Hogan.uniTweak.integration.crafttweaker.crossmod;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import java.util.List;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.common.util.compat.crafttweaker.MetalPress;
import wanion.unidict.UniDict;
import wanion.unidict.api.*;
import wanion.unidict.resource.Resource;

@ModOnly("immersiveengineering")
@ZenClass("unitweak.ie")
@ZenRegister
public class ImmersiveEngineering {
	
	UniDictAPI uniDictAPI = UniDict.getAPI();
	
	@ZenMethod
	public static void newPressRecipe(String outputKind, int outCount, String inputKind, int inCount, IItemStack mold, int energy, @Optional int inputSize) {
		int input = Resource.getKindOfName(inputKind);
		int output = Resource.getKindOfName(outputKind);
		
		List<Resource> inAndOut = UniDictAPI.getResources(input, output);
		
		for(Resource resource : inAndOut) {
			IItemStack outStack = CraftTweakerMC.getIItemStack(resource.getChild(output).getMainEntry(outCount));
			IItemStack inStack = CraftTweakerMC.getIItemStack(resource.getChild(input).getMainEntry(inCount));
			MetalPress.addRecipe(outStack, inStack, mold, energy, inputSize);
		}
	}
}
