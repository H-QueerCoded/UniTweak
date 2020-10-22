package Hogan.uniTweak.integration.crafttweaker.crossmod;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import wanion.unidict.UniDict;
import wanion.unidict.api.*;
import wanion.unidict.resource.Resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

@ModOnly("immersiveengineering")
@ZenClass("mods.unitweak.ie")
@ZenRegister
public class ImmersiveEngineering {
	
	private static final List<pressRecipe> NEW_PRESS_RECIPE_TEMPLATE_LIST = new ArrayList<>();
	
	@ZenMethod
	public static void pressRecipe(String outputKind, String inputKind, IItemStack mold, int energy, @Optional int inputSize, @Optional("1") int outCount) {
		CraftTweakerAPI.apply(new pressRecipe(outputKind, outCount, inputKind, mold, energy, inputSize));
	}
	
	public static class pressRecipe implements IAction{
		
		int inNum, outNum, energy;
		String inputKind, outputKind;
		ItemStack mold;

		public pressRecipe(String outputKind, int outCount, String inputKind, IItemStack mold, int energy, int inputSize) {
			this.inputKind=inputKind;
			this.outputKind=outputKind;
			this.mold=CraftTweakerMC.getItemStack(mold);
			inNum=inputSize;
			outNum=outCount;
			this.energy=energy;
		}
		
		@Override
		public void apply() {
			NEW_PRESS_RECIPE_TEMPLATE_LIST.add(this);
		}

		@Override
		public String describe() {
			return "Trying to create patterned Metal Press recipe template for "+inputKind+" to "+outputKind;
		}
		
	}
	
	public static void init(){
		CraftTweakerAPI.logInfo("mods.unitweak.ie init");
		final UniDictAPI uniDictAPI = NEW_PRESS_RECIPE_TEMPLATE_LIST.size()>0 ? UniDict.getAPI() : null;
		if(uniDictAPI == null) {
			CraftTweakerAPI.logInfo("null API, empty list");
			return;
		}
		registerPressRecipeTemplates(uniDictAPI);
		
	}
	
	private static void registerPressRecipeTemplates(@Nonnull final UniDictAPI uniDictAPI) {
		for (pressRecipe template : NEW_PRESS_RECIPE_TEMPLATE_LIST) {
			CraftTweakerAPI.logInfo("inputKind String: "+template.inputKind);
			CraftTweakerAPI.logInfo("does inputKind exist: "+Resource.kindExists(template.inputKind));
			int input = Resource.getKindFromName(template.inputKind);
			int output = Resource.getKindFromName(template.outputKind);
			List<Resource> inAndOut = uniDictAPI.getResources(input, output);
			CraftTweakerAPI.logInfo("List length: "+inAndOut.size());
			
			for(Resource resource : inAndOut) {
				ItemStack outStack = resource.getChild(output).getMainEntry(template.outNum);
				ItemStack inStack = resource.getChild(input).getMainEntry(template.inNum);
				//MetalPress.addRecipe(outStack, inStack, mold, energy, inNum);
				MetalPressRecipe.addRecipe(outStack, inStack, template.mold, template.energy);
			}
		}
	}
}
