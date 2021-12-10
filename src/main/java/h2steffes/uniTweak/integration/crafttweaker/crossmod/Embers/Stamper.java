package h2steffes.uniTweak.integration.crafttweaker.crossmod.Embers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import wanion.unidict.UniDict;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.resource.Resource;
import teamroots.embers.compat.crafttweaker.CTUtil;
import teamroots.embers.recipe.ItemStampingRecipe;
import teamroots.embers.recipe.RecipeRegistry;

@ModOnly("embers")
@ZenClass("mods.unitweak.embers.stamper")
@ZenRegister
public class Stamper {
	
	@ZenMethod
	public static void add(String outputKind,@NotNull IIngredient stamp, int liquidAmount, @Optional(valueLong = 1) int outputSize, @Optional IIngredient input) {
		CraftTweaker.LATE_ACTIONS.add(new Add(outputKind,stamp,liquidAmount,outputSize,input));
	}
	
	@ZenMethod
	public static void remove(String outputKind) {
		CraftTweaker.LATE_ACTIONS.add(new Remove(outputKind));
	}
	
	public static class Add implements IAction{
		
		String outputKindString;
		IIngredient stamp,input;
		int liquidAmount,outputSize;
		
		public Add(String output, IIngredient stamp, int liquidAmount, int outputSize, IIngredient input) {
			this.outputKindString=output;
			this.stamp=stamp;
			this.liquidAmount=liquidAmount;
			this.outputSize=outputSize;
			this.input=input;
		}
		
		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int outKindInt = Resource.getKindFromName(outputKindString);
			List<Resource> matchingResources = uniDictAPI.getResources(outKindInt);
			
			for (Resource resource: matchingResources) {
				ItemStack outputStack = resource.getChild(outKindInt).getMainEntry(outputSize);
				if(!FluidRegistry.isFluidRegistered(resource.getName().toLowerCase())){
					CraftTweakerAPI.logInfo("UniTweak: No molten version of "+resource.getName()+", skipping stamper recipe");
					continue;
				}
				Fluid fluid = FluidRegistry.getFluid(resource.getName().toLowerCase());
				CraftTweakerAPI.logInfo("UniTweak: Adding stamper recipe for "+liquidAmount+"mb of "+resource.getName()+" to "+outputSize+" "+outputStack.getDisplayName());
				RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(CTUtil.toIngredient(input),new FluidStack(fluid, liquidAmount),CTUtil.toIngredient(stamp),outputStack));
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Trying to create Embers Stamper recipe for "+liquidAmount+"mb to "+outputKindString;
		}
		
	}
	
	public static class Remove implements IAction{
		
		String outputKindString;
		
		public Remove(String output) {
			this.outputKindString=output;
		}
		
		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int outputKindInt = Resource.getKindFromName(outputKindString);
			List<Resource> matchingResources = uniDictAPI.getResources(outputKindInt);
			
			for(Resource resource : matchingResources) {
				List<ItemStack> outputList = resource.getChild(outputKindInt).getEntries();
				for (ItemStack output : outputList) {
					CraftTweakerAPI.logInfo("UniTweak: Removing Stamper recipes with output "+output.getDisplayName());
					RecipeRegistry.stampingRecipes.removeAll(getRecipesByOutput(output));
				}
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Removing Embers Stamper recipes which output "+outputKindString;
		}
		
	}
	
	private static List<ItemStampingRecipe> getRecipesByOutput(ItemStack stack)
    {
        return RecipeRegistry.stampingRecipes.stream().filter(recipe -> ItemStack.areItemStacksEqual(stack,recipe.result)).collect(Collectors.toCollection(ArrayList::new));
    }
}
