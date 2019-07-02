package io.github.paradoxicalblock.questing_api;

import com.google.gson.*;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;

//Based from ee3's code
public class ItemStackSerializer2 implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

	@Override
	public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return ShapedRecipe.getItemStack(json.getAsJsonObject());
	}

	@Override
	public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject ret = new JsonObject();
		ret.addProperty("item", Registry.ITEM.getId(src.getItem()).toString());
		if(src.getCount() > 1) {
			ret.addProperty("count", src.getCount());
		}
		return ret;
	}

}