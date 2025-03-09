package com.rising.ranked.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.rising.ranked.models.ItemDefinition;
import com.rising.ranked.models.PlayerRanking;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ItemStackHelper {
    public static ItemStack fromItemDefinition(ItemDefinition def) {
        String id = def.getItemID();
        int meta = def.getMeta();
        if (id.startsWith("pixelmon:")) {
            String pixelmonName = id.substring("pixelmon:".length());
            Item pixelmonItem = PixelmonItems.getItemFromName(pixelmonName);
            if (pixelmonItem != null) {
                return new ItemStack(pixelmonItem, 1, meta);
            }
        }
        // Caso contrário, busca o item normalmente via ForgeRegistries
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        return new ItemStack(item, 1, meta);
    }

    public static ItemStack createPlayerHead(GameProfile profile) {
        ItemStack skull = new ItemStack(Items.SKULL, 1, 3);
        if (profile == null) {
            return skull;
        }
        NBTTagCompound skullTag = skull.getTagCompound();
        if (skullTag == null) {
            skullTag = new NBTTagCompound();
            skull.setTagCompound(skullTag);
        }

        NBTTagCompound ownerTag = new NBTTagCompound();

        ownerTag.setString("Name", profile.getName() != null ? profile.getName() : "");

        NBTTagList texturesList = new NBTTagList();
        // Verifica se o PropertyMap está presente; se não, cria um vazio
        PropertyMap properties = profile.getProperties();
        if (properties == null) {
            properties = new PropertyMap();
        }
        if (properties.containsKey("textures")) {
            for (Property property : properties.get("textures")) {
                NBTTagCompound textureTag = new NBTTagCompound();
                textureTag.setString("Value", property.getValue());
                if (property.getSignature() != null) {
                    textureTag.setString("Signature", property.getSignature());
                }
                texturesList.appendTag(textureTag);
            }
        }
        NBTTagCompound propertiesTag = new NBTTagCompound();
        propertiesTag.setTag("textures", texturesList);
        ownerTag.setTag("Properties", propertiesTag);

        skullTag.setTag("SkullOwner", ownerTag);
        return skull;
    }

}
