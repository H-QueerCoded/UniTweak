package Hogan.uniTweak.integration.crafttweaker.crossmod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.resource.Resource;
import teamroots.embers.compat.crafttweaker.CTUtil;
import teamroots.embers.recipe.ItemStampingRecipe;
import teamroots.embers.recipe.RecipeRegistry;

@ModOnly("embers")
@ZenClass("mods.unitweak.embers")
@ZenRegister
public class Embers {
	
	private static final List<stamperAdd> NEW_STAMPER_RECIPES = new ArrayList<>();
	private static final List<stamperRemove> REMOVE_STAMPER_RECIPES = new ArrayList<>();
	
	@ZenMethod
	public static void stamperAdd(String output,@NotNull IIngredient stamp, int liquidAmount, @Optional(valueLong = 1) int outputSize, @Optional IIngredient input) {
		CraftTweakerAPI.apply(new stamperAdd(output,stamp,liquidAmount,outputSize,input));
	}
	
	@ZenMethod
	public static void stamperRemove(String output) {
		CraftTweakerAPI.apply(new stamperRemove(output));
	}
	
	public static class stamperAdd implements IAction{
		
		String output;
		IIngredient stamp,input;
		int liquidAmount,outputSize;
		
		public stamperAdd(String output, IIngredient stamp, int liquidAmount, int outputSize, IIngredient input) {
			this.output=output;
			this.stamp=stamp;
			this.liquidAmount=liquidAmount;
			this.outputSize=outputSize;
			this.input=input;
		}
		
		@Override
		public void apply() {
			NEW_STAMPER_RECIPES.add(this);
		}

		@Override
		public String describe() {
			return "UniTweak: Trying to create Embers Stamper recipe for "+liquidAmount+"mb to "+output;
		}
		
	}
	
	public static class stamperRemove implements IAction{
		
		String output;
		
		public stamperRemove(String output) {
			this.output=output;
		}
		
		@Override
		public void apply() {
			REMOVE_STAMPER_RECIPES.add(this);
		}

		@Override
		public String describe() {
			return "UniTweak: Removing Embers Stamper recipes which output "+output;
		}
		
	}
	
	public static void postInit(@Nonnull final UniDictAPI uniDictAPI) {
		if(REMOVE_STAMPER_RECIPES.size()>0) {
			removeStamperRecipes(uniDictAPI);
		}
		if(NEW_STAMPER_RECIPES.size()>0) {
			registerStamperRecipes(uniDictAPI);
		}
	}
	
	private static void registerStamperRecipes(@Nonnull final UniDictAPI uniDictAPI) {
		for(stamperAdd template : NEW_STAMPER_RECIPES) {
			int kind = Resource.getKindFromName(template.output);
			List<Resource> list = uniDictAPI.getResources(kind);
			
			for (Resource resource: list) {
				ItemStack outStack = resource.getChild(kind).getMainEntry(template.outputSize);
				if(!FluidRegistry.isFluidRegistered(resource.getName().toLowerCase())){
					CraftTweakerAPI.logInfo("UniTweak: No molten version of "+resource.getName()+", skipping stamper recipe");
					continue;
				}
				Fluid fluid = FluidRegistry.getFluid(resource.getName().toLowerCase());
				CraftTweakerAPI.logInfo("UniTweak: Adding stamper recipe for "+template.liquidAmount+"mb of "+resource.getName()+" to "+template.outputSize+" "+outStack.getDisplayName());
				RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(CTUtil.toIngredient(template.input),new FluidStack(fluid, template.liquidAmount),CTUtil.toIngredient(template.stamp),outStack));
			}
		}
	}
	
	private static List<ItemStampingRecipe> getRecipesByOutput(ItemStack stack)
    {
        return RecipeRegistry.stampingRecipes.stream().filter(recipe -> ItemStack.areItemStacksEqual(stack,recipe.result)).collect(Collectors.toCollection(ArrayList::new));
    }
	
	private static void removeStamperRecipes(@Nonnull final UniDictAPI uniDictAPI) {
		for (stamperRemove removal : REMOVE_STAMPER_RECIPES) {
			int kind = Resource.getKindFromName(removal.output);
			List<Resource> list = uniDictAPI.getResources(kind);
			
			for(Resource resource : list) {
				List<ItemStack> outputList = resource.getChild(kind).getEntries();
				for (ItemStack output : outputList) {
					CraftTweakerAPI.logInfo("UniTweak: Removing Metal Press recipes with output "+output.getDisplayName());
					RecipeRegistry.stampingRecipes.removeAll(getRecipesByOutput(output));
				}
			}
		}
	}
}
