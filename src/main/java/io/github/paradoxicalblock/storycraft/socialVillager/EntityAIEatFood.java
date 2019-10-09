/*
package io.github.paradoxicalblock.storycraft.socialVillager;

import io.github.paradoxicalblock.storycraft.ItemTagType;
import io.github.paradoxicalblock.storycraft.entity.FamiliarsEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.entities.EntityVillagerTek;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class EntityAIEatFood extends Goal {
    public static final Map<Item, VillagerFood> villagerFood = new HashMap<>();

    static {
      BiConsumer<FamiliarsEntity, ItemStack> returnBowl = ((v, i) -> {
            ItemStack bowl = new ItemStack(Items.BOWL);
            if (ModItems.isTaggedItem(i, ItemTagType.VILLAGER)) {
                ModItems.makeTaggedItem(bowl, ItemTagType.VILLAGER);
            }
            v.getInventory().add(bowl);
        });

        registerFood(Items.APPLE, 12, -1);
        registerFood(Items.BAKED_POTATO, 35, 1);
        registerFood(Items.BEETROOT, 7, -1);
        registerFood(Items.BEETROOT_SOUP, 50, 6, returnBowl);
        registerFood(Items.BREAD, 55, 4);
        registerFood(Items.CAKE, 7, 25);
        registerFood(Items.CARROT, 12, -1);
        registerFood(Items.COOKED_BEEF, 70, 14);
        registerFood(Items.COOKED_CHICKEN, 60, 6);
        registerFood(Items.COOKED_MUTTON, 66, 4);
        registerFood(Items.COOKED_PORKCHOP, 70, 14);
        registerFood(Items.COOKIE, 5, 16);
        registerFood(Items.GOLDEN_CARROT, 70, 20);
        registerFood(Items.MELON, 6, 3);
        registerFood(Items.MUSHROOM_STEW, 50, 4, returnBowl);
        registerFood(Items.POTATO, 7, -1);
        registerFood(Items.PUMPKIN_PIE, 35, 18);
    }

    private FamiliarsEntity villager;
    private ItemStack foodItem;
    private int eatTime;

    public EntityAIEatFood(FamiliarsEntity v) {
        this.eatTime = 0;
        this.villager = v;
        setControls(EnumSet.of(Control.LOOK));
    }

    private static void registerFood(Item item, int hunger, int happy) {
        registerFood(item, hunger, happy, null);
    }


    private static void registerFood(Item item, int hunger, int happy, BiConsumer<FamiliarsEntity, ItemStack> postEat) {
        VillagerFood food = new VillagerFood(item, hunger, happy, postEat);
        villagerFood.put(food.item, food);
    }

    public static int getFoodScore(Item item, FamiliarsEntity v) {
        VillagerFood food = villagerFood.get(item);
        if (food != null) {
            return getFoodScore(food, v);
        }
        return -1;
    }

    public static int getFoodScore(VillagerFood food, FamiliarsEntity v) {
        if (v != null) {
            int happy = food.getHappy(v);
            int hunger = food.getHunger(v);
            if (v.getHunger() + hunger > v.getMaxHunger()) {
                hunger = 1;
            }

            if (v.getHappy() + happy > v.getMaxHappy()) {
                happy = 0;
            }

            int score = hunger;

            int happyPotential = happy * 5;
            float happyFactor = 1.0F;
            if (happyPotential > 0) {
                happyFactor = (v.getMaxHappy() - v.getHappy()) / v.getMaxHappy();
            }
            score += (int) (happyPotential * happyFactor);
            return Math.max(score, 1);
        }

        return food.happy + food.hunger;
    }

    public boolean canStop() {
        return false;
    }

    public boolean canStart() {
        if (this.villager.isAITick() && this.villager.isHungry() && !this.villager.isSleeping()) {
            this.foodItem = this.villager.getInventory().getItem(p -> Integer.valueOf(getFoodScore(p.getItem(), this.villager)));
            if (!this.foodItem.isEmpty()) {
                return true;
            }
            this.villager.setThought(FamiliarsEntity.VillagerThought.HUNGRY);
        }


        return false;
    }

    public void start() {
        startEat();
        super.start();
    }

    public boolean shouldContinue() {
      return this.eatTime >= 0;
    }

    public void tick() {
        this.eatTime--;
        if (this.eatTime == 0 &&
                !this.villager.getInventory().removeItems(p -> ItemStack.areEqualIgnoreDamage(p, this.foodItem), 1).isEmpty()) {

            VillagerFood food = villagerFood.get(this.foodItem.getItem());
            if (food != null) {
                food.eat(this.villager, this.foodItem);
            }
        }


        super.tick();
    }

    private void startEat() {
        this.eatTime = 80;
        this.villager.getNavigation().stop();
        this.villager.equipActionItem(this.foodItem);
    }

    private void stopEat() {
        this.villager.unequipActionItem(this.foodItem);
    }

    public void stop() {
        super.stop();
        stopEat();
        this.foodItem = null;
        this.eatTime = 0;
    }

    public static class VillagerFood {
        private final int happy;
        private final int hunger;
        private final Item item;
        private final BiConsumer<FamiliarsEntity, ItemStack> postEat;

        public VillagerFood(Item item, int hunger, int happy) {
            this(item, hunger, happy, null);
        }


        public VillagerFood(Item item, int hunger, int happy, BiConsumer<FamiliarsEntity, ItemStack> post) {
            this.item = item;
            this.hunger = hunger;
            this.happy = happy;
            this.postEat = post;
        }

        public void eat(FamiliarsEntity v, ItemStack foodItem) {
            boolean isVFood = ModItems.isTaggedItem(foodItem, ItemTagType.VILLAGER);

            int hunger = getHunger(v);
            if (!isVFood) {
                hunger /= 2;
            }
            v.modifyHunger(hunger);

            if (isVFood) {
                int happy = getHappy(v);
                v.modifyHappy(happy);
            }

            if (this.postEat != null) {
                this.postEat.accept(v, foodItem);
            }
            v.debugOut("Eating Food " + foodItem.getItem().getTranslationKey());
            v.addRecentEat(this.item);
        }

        public int getHappy(FamiliarsEntity villager) {
            int recentEatModifier = villager.getRecentEatModifier(this.item);


            return Math.max(this.happy + recentEatModifier, -3);
        }


        public int getHunger(FamiliarsEntity villager) {
            return this.hunger;
        }


        public Item getItem() {
            return this.item;
        }
    }
}
*/
