/*
 * Copyright 2024 Karma Krafts & associates
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.karma.chemlibcc.util;

import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.registry.ItemRegistry;
import io.karma.chemlibcc.item.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Alexander Hinze
 * @since 09/10/2024
 */
public final class ItemRegistryUtils {
    // @formatter:off
    private ItemRegistryUtils() {}
    // @formatter:on

    public static <B extends Block> void fromChemicalBlock(RegistryObject<B> registryObject, Properties properties) {
        ItemRegistry.REGISTRY_BLOCK_ITEMS.register(registryObject.getId().getPath(),
            () -> new GeneratedChemicalBlockItem((ChemicalBlock) registryObject.get(), properties));
    }

    public static <B extends Block> void lampFromChemicalBlock(RegistryObject<B> registryObject,
                                                               Properties properties) {
        ItemRegistry.REGISTRY_BLOCK_ITEMS.register(registryObject.getId().getPath(),
            () -> new GeneratedLampBlockItem((ChemicalBlock) registryObject.get(), properties));
    }

    public static void registerItemByType(RegistryObject<Item> registryObject, ChemicalItemType chemicalItemType) {
        final var registryName = String.format("%s_%s",
            registryObject.getId().getPath(),
            chemicalItemType.getSerializedName());
        switch (chemicalItemType) {
            case COMPOUND -> ItemRegistry.REGISTRY_COMPOUND_DUSTS.register(registryName,
                () -> new GeneratedCompoundDustItem(registryObject.getId(), chemicalItemType, new Properties()));
            case DUST -> ItemRegistry.REGISTRY_METAL_DUSTS.register(registryName,
                () -> new GeneratedDustItem(registryObject.getId(), chemicalItemType, new Properties()));
            case NUGGET -> ItemRegistry.REGISTRY_NUGGETS.register(registryName,
                () -> new GeneratedNuggetItem(registryObject.getId(), chemicalItemType, new Properties()));
            case INGOT -> ItemRegistry.REGISTRY_INGOTS.register(registryName,
                () -> new GeneratedIngotItem(registryObject.getId(), chemicalItemType, new Properties()));
            case PLATE -> ItemRegistry.REGISTRY_PLATES.register(registryName,
                () -> new GeneratedPlateItem(registryObject.getId(), chemicalItemType, new Properties()));
        }

    }
}
