package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.agriculture.MobSlaughterFactoryTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.List;

public class
MobSlaughterFactoryBlock extends CustomOrientedBlock<MobSlaughterFactoryTile> {

    private float meatValue;

    public MobSlaughterFactoryBlock() {
        super("mob_slaughter_factory", MobSlaughterFactoryTile.class, Material.ROCK, 1000, 40);
    }

    @Override
    public void getMachineConfig() {
        super.getMachineConfig();
        meatValue = CustomConfiguration.config.getFloat("meatValue", "machines" + Configuration.CATEGORY_SPLITTER + this.getRegistryName().getResourcePath().toString(), 5, 1, Integer.MAX_VALUE, "Mob health multiplier, mobHealth * meatValue = meat mb produced");
    }

    public float getMeatValue() {
        return meatValue;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pgp", "sms", "ara",
                'p', ItemRegistry.plastic,
                'g', "gearGold",
                's', Items.IRON_SWORD,
                'm', MachineCaseItem.INSTANCE,
                'a', Items.IRON_AXE,
                'r', Items.REDSTONE);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.MOB;
    }


    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("When provided with power it will " + PageText.bold("grind") + " any entity in front of it and produce " + PageText.bold("Liquid Meat") + " from it.\n\nIt will " + PageText.bold("not") + " drop items or experience in the process.\n\nIt will also produce " + PageText.bold("some pink") + " slime in the process."));
        return pages;
    }
}
