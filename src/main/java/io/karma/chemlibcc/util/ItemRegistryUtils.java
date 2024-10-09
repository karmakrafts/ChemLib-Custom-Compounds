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
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import io.karma.chemlibcc.item.GeneratedChemicalItem;
import io.karma.chemlibcc.item.GeneratedCompoundDustItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 09/10/2024
 */
public final class ItemRegistryUtils {
    // @formatter:off
    private ItemRegistryUtils() {}
    // @formatter:on

    public static void registerItemByType(RegistryObject<Item> pRegistryObject, ChemicalItemType pChemicalItemType) {
        final var registryName = String.format("%s_%s",
            pRegistryObject.getId().getPath(),
            pChemicalItemType.getSerializedName());
        final Supplier<ChemicalItem> supplier = () -> new GeneratedChemicalItem(pRegistryObject.getId(),
            pChemicalItemType,
            new Item.Properties());
        switch (pChemicalItemType) {
            case COMPOUND -> ItemRegistry.REGISTRY_COMPOUND_DUSTS.register(registryName,
                () -> new GeneratedCompoundDustItem(pRegistryObject.getId(), pChemicalItemType, new Item.Properties()));
            case DUST -> ItemRegistry.REGISTRY_METAL_DUSTS.register(registryName, supplier);
            case NUGGET -> ItemRegistry.REGISTRY_NUGGETS.register(registryName, supplier);
            case INGOT -> ItemRegistry.REGISTRY_INGOTS.register(registryName, supplier);
            case PLATE -> ItemRegistry.REGISTRY_PLATES.register(registryName, supplier);
        }

    }
}
