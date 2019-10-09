/*
package io.github.paradoxicalblock.storycraft.economy;

import io.github.paradoxicalblock.storycraft.ItemTagType;
import io.github.paradoxicalblock.storycraft.ProfessionType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.tangotek.tektopia.ModItems;


public class ItemValue {
    private final ItemStack itemStack;
    private final int baseValue;
    private final int appearanceWeight;
    private final ProfessionType requiredProfession;
    private final float PURCHASE_MIN_REDUCTION;
    private final float PURCHASE_MAX_REDUCTION;
    private int purchaseCount;
    private float markDown;
    private float markUp;

    public ItemValue(ItemStack itemStack, int baseValue, int appearanceWeight, ProfessionType reqProf) {
        this.PURCHASE_MIN_REDUCTION = 0.1F;
        this.PURCHASE_MAX_REDUCTION = 0.3F;

        this.itemStack = ModItems.makeTaggedItem(itemStack, ItemTagType.VILLAGER);
        this.baseValue = baseValue;
        this.appearanceWeight = appearanceWeight;
        this.requiredProfession = reqProf;
        reset();
    }

    public static String getName(ItemStack stack) {
        return stack.getItem().getTranslationKey() + "_" + stack.getMetadata();
    }

    public String getName() {
        return getName(this.itemStack);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }


    public ProfessionType getRequiredProfession() {
        return this.requiredProfession;
    }


    public int getBaseValue() {
        return this.baseValue;
    }


    public int getPurchaseCount() {
        return this.purchaseCount;
    }


    public float getAppearanceWeight() {
        return (this.appearanceWeight * getCurrentValue() / getBaseValue());
    }


    public int getCurrentValue() {
        return Math.max(1, (int) (this.baseValue - this.markDown + this.markUp));
    }


    public void reset() {
        this.purchaseCount = 0;
        this.markDown = 0.0F;
        this.markUp = 0.0F;
    }

    public float markDown(float ageLerp) {
        float reduction = (float) MathHelper.clampedLerp(0.10000000149011612D, 0.30000001192092896D, ageLerp) * getCurrentValue();
        this.markDown += reduction;
        this.purchaseCount++;
        return reduction;
    }


    public boolean isForSale() {
        return (this.purchaseCount < 8);
    }


    public void markUp(float value) {
        this.markUp += value;
    }


    public String toString() {
        return "[" + getName() + "]  pc: " + this.purchaseCount + "   markDown: " + this.markDown + "    markUp: " + this.markUp + "    price: " + getCurrentValue() + "(" + getBaseValue() + ")";
    }
}
*/
