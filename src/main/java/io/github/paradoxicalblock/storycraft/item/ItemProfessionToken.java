/*
package io.github.paradoxicalblock.storycraft.item;

import io.github.paradoxicalblock.storycraft.ProfessionType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.world.World;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillagerTek;

import java.util.function.BiFunction;


public class ItemProfessionToken extends Item {
    private final String name;
    private final int cost;
    private final ProfessionType professionType;
    private BiFunction<World, EntityVillagerTek, EntityVillagerTek> villagerFunc;

    public ItemProfessionToken(String name, ProfessionType pt, BiFunction<World, EntityVillagerTek, EntityVillagerTek> biFunction, int emeraldCost) {
        super(new Settings().group(ItemGroup.MISC).maxCount(1));
        this.name = name;
        this.professionType = pt;
        this.villagerFunc = biFunction;
        this.cost = emeraldCost;
    }

    public int getCost(Village v) {
        float mult = Math.min((v.getTownData().getProfessionSales() / 5) * 0.2F, 10.0F);
        return (int) (this.cost * (1.0F + mult));
    }


    public ProfessionType getProfessionType() {
        return this.professionType;
    }


    public EntityVillagerTek createVillager(World world, EntityVillagerTek clickedEntity) {
        return this.villagerFunc.apply(world, clickedEntity);
    }


    public void registerItemModel() {
        TekVillager.proxy.registerItemRenderer(this, 0, this.name);
    }
}
*/
