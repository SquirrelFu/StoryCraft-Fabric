/*
package io.github.paradoxicalblock.storycraft.crafting;

import io.github.paradoxicalblock.storycraft.ProfessionType;
import io.github.paradoxicalblock.storycraft.entity.EntityVillagerTek;
import net.minecraft.item.ItemStack;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.storage.VillagerInventory;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;



public class Recipe
{
  private final ProfessionType profession;
  private final int skillChance;
  private final ItemStack product;
  private final List<ItemStack> needs;
  private final int storageGoal;
  public final int idealCount;
  public final int limitCount;
  public final String aiFilter;
  public final Predicate<EntityVillagerTek> shouldCraft;
  private final Function<EntityVillagerTek, Integer> iterations;
  
  public Recipe(ProfessionType pt, String aiFilter, int skillChance, ItemStack itemProduct, List<ItemStack> needs, int idealCount, int limitCount, Function<EntityVillagerTek, Integer> animationIterations, int storageGoal) { this(pt, aiFilter, skillChance, itemProduct, needs, idealCount, limitCount, animationIterations, storageGoal, null); }

  
  public Recipe(ProfessionType pt, String aiFilter, int skillChance, ItemStack itemProduct, List<ItemStack> needs, int idealCount, int limitCount, Function<EntityVillagerTek, Integer> animationIterations, int storageGoal, Predicate<EntityVillagerTek> pred) {
    this.profession = pt;
    this.skillChance = skillChance;
    this.aiFilter = aiFilter;
    this.product = itemProduct;
    this.needs = needs;
    this.storageGoal = storageGoal;
    
    this.shouldCraft = pred;
    this.iterations = animationIterations;
    this.idealCount = idealCount;
    this.limitCount = limitCount;
  }
  
  public boolean hasItems(EntityVillagerTek villager) {
    for (ItemStack itemReq : this.needs) {
      int reqCount = villager.getInventory().getItemCount(p -> (p.getItem() == itemReq.getItem() && !p.isItemEnchanted()));
      if (reqCount < itemReq.getCount()) {
        return false;
      }
    } 
    return true;
  }

  
  public String getAiFilter() { return this.aiFilter; }

  
  public ItemStack craft(EntityVillagerTek villager) {
    boolean nonVillagerItems = false;
    for (ItemStack itemReq : this.needs) {
      List<ItemStack> items = villager.getInventory().removeItems(p -> (p.getItem() == itemReq.getItem()), itemReq.getCount());
      int total = VillagerInventory.countItems(items);
      if (total != itemReq.getCount()) {
        return null;
      }
      nonVillagerItems |= items.stream().anyMatch(itemStack -> !ModItems.isTaggedItem(itemStack, ItemTagType.VILLAGER));
    } 
    
    villager.tryAddSkill(this.profession, this.skillChance);
    
    villager.debugOut("has crafted: " + this.product.getItem().getTranslationKey());
    
    ItemStack result = this.product.copy();
    if (!nonVillagerItems) {
      ModItems.makeTaggedItem(result, ItemTagType.VILLAGER);
    }
    return result;
  }

  
  public ItemStack getProduct() { return this.product; }

  
  public Predicate<ItemStack> isNeed() {
    return p -> {
        for (ItemStack need : this.needs) {
          if (need.getItem() == p.getItem())
            return true; 
        } 
        return false;
      };
  }
  
  public List<ItemStack> getNeeds() { return this.needs; }





  
  public static boolean hasPersonalGoal(EntityVillagerTek villager, ItemStack goalStack) {
    Predicate<ItemStack> pred = p -> (p.getItem() == goalStack.getItem());
    int count = villager.getInventory().getItemCount(pred);
    return (count >= goalStack.getCount());
  }
  
  public boolean shouldCraft(EntityVillagerTek villager) {
    if (!villager.hasVillage() || !villager.isAIFilterEnabled(this.aiFilter)) {
      return false;
    }
    int storageCount = villager.getVillage().getStorageCount(p -> (p.getItem() == getProduct().getItem() && p.isItemEnchanted() == getProduct().isItemEnchanted()));
    if (storageCount >= this.storageGoal) {
      return false;
    }
    return this.shouldCraft == null || this.shouldCraft.test(villager);
  }
  
  public int getAnimationIterations(EntityVillagerTek v) { return this.iterations.apply(v); }
}
*/
