/*
package io.github.paradoxicalblock.storycraft.economy;

import io.github.paradoxicalblock.storycraft.ProfessionType;
import io.github.paradoxicalblock.storycraft.Village;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.entities.EntityVillagerTek;

import java.security.InvalidParameterException;
import java.util.*;

public class ItemEconomy {
    static final int MAX_ITEM_SALES = 5;
    static final int ITEMS_FOR_SALE = 8;
    protected Map<String, ItemValue> items = new HashMap();
    protected MerchantRecipeList merchantList = new MerchantRecipeList();
    private Random rnd = new Random();
    private boolean itemsDirty = true;
    private Queue<ItemValue> salesHistory = new LinkedList();
    private float totalMarketValue = 0.0F;
    private float totalSalesAppearanceWeight;
    private int tradesAvailableForProfs;
    private Set<ProfessionType> profsInVillage = new HashSet();

    public void addItem(ItemValue itemValue) {
        ItemValue existingValue = this.items.put(itemValue.getName(), itemValue);
        if (existingValue != null) {
            throw new InvalidParameterException("Duplicate marketId in economy: " + itemValue.getName());
        }
        this.totalMarketValue += itemValue.getBaseValue();
        this.totalSalesAppearanceWeight += itemValue.getAppearanceWeight();
        this.itemsDirty = true;
    }


    public boolean hasItems() {
        return !this.items.isEmpty();
    }


    public void refreshValues(Village village) {
        this.totalSalesAppearanceWeight = 0.0F;
        this.tradesAvailableForProfs = 0;
        this.items.values().forEach(iv -> iv.reset());


        this.profsInVillage.clear();
        List<EntityVillagerTek> villagers = village.getWorld().getEntities(EntityVillagerTek.class, village.getAABB().grow(40.0D));
        for (EntityVillagerTek v : villagers) {
            this.profsInVillage.add(v.getProfessionType());
        }

        if (this.profsInVillage.isEmpty()) {
            boolean bool = true;
        }


        int historyIndex = 1;
        for (ItemValue iv : this.salesHistory) {
            float reduction = iv.markDown(historyIndex / this.salesHistory.size());
            for (ItemValue iv2 : this.items.values()) {
                if (iv2 != iv) {
                    iv2.markUp(iv2.getBaseValue() * reduction / (this.totalMarketValue - iv.getBaseValue()));
                }
            }
            historyIndex++;
        }

        for (ItemValue iv : this.items.values()) {
            this.totalSalesAppearanceWeight += iv.getAppearanceWeight();
            if (this.profsInVillage.contains(iv.getRequiredProfession())) {
                this.tradesAvailableForProfs++;
            }
        }
        this.itemsDirty = false;
        this.merchantList = null;
    }

    public MerchantRecipeList getMerchantList(Village v, int stallLevel) {
        if (this.itemsDirty) {
            refreshValues(v);
        }
        if (this.merchantList == null) {
            v.debugOut("Generating Merchant List");
            this.merchantList = generateMerchantList(stallLevel);
        }

        return this.merchantList;
    }


    private int getHistorySize(Village village) {
        return (int) MathHelper.clampedLerp(30.0D, 50.0D, village.getResidentCount() / 100.0D);
    }


    public void sellItem(MerchantRecipe recipe, Village village) {
        ItemValue itemValue = this.items.get(ItemValue.getName(recipe.getItemToBuy()));
        if (itemValue != null) {
            this.salesHistory.add(itemValue);
            if (this.salesHistory.size() > getHistorySize(village)) {
                this.salesHistory.poll();
            }
            this.itemsDirty = true;
        }
    }


    private MerchantRecipe createFoodTrade() {
        List<MerchantRecipe> tempList = new ArrayList<MerchantRecipe>();
        tempList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 2), ItemStack.EMPTY, ModItems.createTaggedItem(Items.BAKED_POTATO, 15, ItemTagType.VILLAGER), 0, 1));
        tempList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 2), ItemStack.EMPTY, ModItems.createTaggedItem(Items.COOKED_BEEF, 8, ItemTagType.VILLAGER), 0, 1));
        tempList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 2), ItemStack.EMPTY, ModItems.createTaggedItem(Items.PUMPKIN_PIE, 10, ItemTagType.VILLAGER), 0, 1));
        tempList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 2), ItemStack.EMPTY, ModItems.createTaggedItem(Items.COOKIE, 15, ItemTagType.VILLAGER), 0, 1));
        tempList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 2), ItemStack.EMPTY, ModItems.createTaggedItem(Items.GOLDEN_CARROT, 6, ItemTagType.VILLAGER), 0, 1));

        return tempList.get(this.rnd.nextInt(tempList.size()));
    }

    private MerchantRecipeList generateMerchantList(int stallLevel) {
        float totalWeight = this.totalSalesAppearanceWeight;
        MerchantRecipeList outList = new MerchantRecipeList();

        if (this.salesHistory.size() < this.items.size()) {
            outList.add(createFoodTrade());
        }

        List<ItemValue> workItems = new LinkedList<>(this.items.values());
        Collections.shuffle(workItems);
        int itemsForSale = 0;
        int MAX_TRADES = 8 + stallLevel * 2;
        while (itemsForSale < MAX_TRADES && !workItems.isEmpty()) {
            float appearanceRoll = this.rnd.nextFloat() * totalWeight;
            float count = 0.0F;
            ListIterator<ItemValue> itr = workItems.listIterator();
            while (itr.hasNext()) {
                ItemValue iv = itr.next();
                count += iv.getAppearanceWeight();
                if (!itr.hasNext()) {

                    System.out.println("Merchant economy count/weight mismatch");
                    count = appearanceRoll + 1.0F;
                }

                if (appearanceRoll < count) {


                    if (iv.isForSale()) {
                        if (this.profsInVillage.contains(iv.getRequiredProfession())) {
                            outList.add(new MerchantRecipe(ModItems.makeTaggedItem(iv.getItemStack(), ItemTagType.VILLAGER), ItemStack.EMPTY, new ItemStack(Items.EMERALD, iv.getCurrentValue()), 0, 5));
                            itemsForSale++;

                            totalWeight -= iv.getAppearanceWeight();
                            itr.remove();
                            continue;
                        }
                        break;
                    }
                    break;
                }
            }
        }
        return outList;
    }

    public void report(StringBuilder builder) {
        builder.append("========== Item Economy Report ========== [" + this.totalMarketValue + "]\n");
        builder.append("    --History--\n");
        this.salesHistory.forEach(iv -> builder.append("       " + iv.getItemStack() + "\n"));

        builder.append("    --Item Values--\n");
        int sumValues = 0;
        for (ItemValue iv : this.items.values()) {
            builder.append("       " + iv + "\n");
            sumValues += iv.getCurrentValue();
        }
        builder.append("     Current Value Sum: " + sumValues + "\n");
    }


    public int getSalesHistorySize() {
        return this.salesHistory.size();
    }


    public void writeNBT(CompoundTag compound) {
        ListTag historyList = new ListTag();
        for (ItemValue iv : this.salesHistory) {
            historyList.add(new StringTag(iv.getName()));
        }
        compound.put("SalesHistory", historyList);
    }

    public void readNBT(CompoundTag compound) {
        this.salesHistory.clear();
        ListTag nbttaglist = compound.getList("SalesHistory", 8);
        for (int i = 0; i < nbttaglist.size(); i++) {

            String name = nbttaglist.getString(i);
            ItemValue iv = this.items.get(name);
            if (iv != null)
                this.salesHistory.add(iv);
        }
    }
}
*/
