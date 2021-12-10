package h2steffes.uniTweak.integration.crafttweaker.crossmod.IE;

import java.util.List;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import wanion.unidict.UniDict;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.resource.Resource;

@ModOnly("immersiveengineering")
@ZenClass("mods.unitweak.IE.arcFurnace")
@ZenRegister
public class ArcFurnace {

	@ZenMethod
	public static void add(String outputKind, String inputKind, IItemStack slag, int time, int energyPerTick, @Optional IIngredient[] additives, @Optional String specialRecipeType) {
		CraftTweaker.LATE_ACTIONS.add(new Add());
	}
	
	@ZenMethod
	public static void remove(String outputKind) {
		CraftTweaker.LATE_ACTIONS.add(new Remove(outputKind));
	}

	public static class Add implements IAction {

		public Add() {
		}

		@Override
		public void apply() {
		}

		@Override
		public String describe() {
			return "UniTweak: ";
		}
	}

	public static class Remove implements IAction {
		
		String outputKindString;

		public Remove(String outputKind) {
			outputKindString=outputKind;
		}

		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int outputKindInt = Resource.getKindFromName(outputKindString);
			List<Resource> matchingResources = uniDictAPI.getResources(outputKindInt);

			for (Resource resource : matchingResources) {
				List<ItemStack> outputList = resource.getChild(outputKindInt).getEntries();
				for (ItemStack output : outputList) {
					CrusherRecipe.removeRecipesForOutput(output);
				}
			}
		}

		@Override
		public String describe() {
			return "UniTweak: removing Arc Furnace recipes that output "+outputKindString;
		}
	}
}
