package net.dreamer.theyaremultiplying.util;

import net.dreamer.theyaremultiplying.TheyAreMultiplying;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FoodTagCreator {
    public static TagKey<Item> createMobFeedingTag(Identifier name,Entity entity) {
        boolean bl = entity instanceof SlimeEntity;
        String transformedName = name.getNamespace() + "/" + name.getPath() + (bl ? "_inedible" : "_edible");
        return TagKey.of(Registry.ITEM_KEY,new Identifier(TheyAreMultiplying.MOD_ID,transformedName));
    }
}
