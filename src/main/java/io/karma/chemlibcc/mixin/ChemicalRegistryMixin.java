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

package io.karma.chemlibcc.mixin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalBlockType;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.registry.BlockRegistry;
import com.smashingmods.chemlib.registry.ChemicalRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import io.karma.chemlibcc.ChemLibCC;
import io.karma.chemlibcc.item.GeneratedChemicalBlock;
import io.karma.chemlibcc.item.GeneratedCompoundItem;
import io.karma.chemlibcc.item.GeneratedElementItem;
import io.karma.chemlibcc.item.GeneratedLampBlock;
import io.karma.chemlibcc.util.FluidRegistryUtils;
import io.karma.chemlibcc.util.ItemRegistryUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 09/10/2024
 */
@Mixin(value = ChemicalRegistry.class, remap = false)
public final class ChemicalRegistryMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void onRegister(final CallbackInfo cbi) {
        final var directory = FMLLoader.getGamePath().resolve("config").resolve("chemlibcc");
        chemlibcc$tryLoad(directory.resolve("elements.json"), ChemicalRegistryMixin::chemlibcc$registerElements);
        chemlibcc$tryLoad(directory.resolve("compounds.json"), ChemicalRegistryMixin::chemlibcc$registerCompounds);
    }

    @Unique
    private static void chemlibcc$tryLoad(final Path path, final Consumer<JsonObject> consumer) {
        if (!Files.exists(path)) {
            return;
        }
        ChemLibCC.LOGGER.info("Loading external content configuration from {}", path);
        try (final var reader = Files.newBufferedReader(path)) {
            final var element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                throw new IllegalStateException("Config root must be a JSON object");
            }
            consumer.accept(element.getAsJsonObject());
        }
        catch (Throwable error) {
            ChemLibCC.LOGGER.error("Could not load configuration from {}: {}", path, error);
        }
    }

    @Unique
    private static void chemlibcc$registerElements(final JsonObject rootObject) {
        for (final var jsonElement : rootObject.getAsJsonArray("elements")) {
            final var object = jsonElement.getAsJsonObject();
            final var elementName = object.get("name").getAsString();
            final var displayName = object.get("display_name").getAsString();
            final var atomicNumber = object.get("atomic_number").getAsInt();
            final var abbreviation = object.get("abbreviation").getAsString();
            final var groupName = object.get("group_name").getAsString();
            final var group = object.get("group").getAsInt();
            final var period = object.get("period").getAsInt();
            final var matterState = MatterState.valueOf(object.get("matter_state").getAsString().toUpperCase(Locale.ROOT));
            final var metalType = MetalType.valueOf(object.get("metal_type").getAsString().toUpperCase(Locale.ROOT));
            final var artificial = object.has("artificial") && object.get("artificial").getAsBoolean();
            final var color = object.get("color").getAsString();

            ItemRegistry.REGISTRY_ELEMENTS.register(elementName,
                () -> new GeneratedElementItem(elementName,
                    displayName,
                    atomicNumber,
                    abbreviation,
                    groupName,
                    group,
                    period,
                    matterState,
                    metalType,
                    artificial,
                    color,
                    ChemicalRegistry.mobEffectsFactory(object)));
            final var registryObject = ItemRegistry.getRegistryObject(ItemRegistry.REGISTRY_ELEMENTS, elementName);

            if (!artificial) {
                switch (matterState) {
                    case SOLID -> {
                        final var hasItem = object.has("has_item") && object.get("has_item").getAsBoolean();

                        if (metalType == MetalType.METAL) {
                            ItemRegistryUtils.registerItemByType(registryObject, ChemicalItemType.PLATE);
                            if (!hasItem) {
                                ItemRegistryUtils.registerItemByType(registryObject, ChemicalItemType.NUGGET);
                                ItemRegistryUtils.registerItemByType(registryObject, ChemicalItemType.INGOT);
                                BlockRegistry.BLOCKS.register(String.format("%s_metal_block", elementName),
                                    () -> new GeneratedChemicalBlock(new ResourceLocation(ChemLib.MODID, elementName),
                                        ChemicalBlockType.METAL,
                                        BlockRegistry.METAL_BLOCKS,
                                        BlockRegistry.METAL_PROPERTIES));
                                BlockRegistry.getRegistryObjectByName(String.format("%s_metal_block",
                                    elementName)).ifPresent(block -> ItemRegistryUtils.fromChemicalBlock(block,
                                    new Item.Properties()));
                            }
                        }
                        ItemRegistryUtils.registerItemByType(registryObject, ChemicalItemType.DUST);
                    }
                    case LIQUID, GAS -> {
                        final var hasFluid = object.has("has_fluid") && object.get("has_fluid").getAsBoolean();
                        if (!hasFluid) {
                            final var properties = object.get("fluid_properties").getAsJsonObject();
                            final var slopeFindDistance = properties.has("slope_find_distance") ? properties.get(
                                "slope_find_distance").getAsInt() : 4;
                            final var decreasePerBlock = properties.has("decrease_per_block") ? properties.get(
                                "decrease_per_block").getAsInt() : 1;

                            if (group == 18) {
                                BlockRegistry.BLOCKS.register(String.format("%s_lamp_block", elementName),
                                    () -> new GeneratedLampBlock(new ResourceLocation(ChemLib.MODID, elementName),
                                        ChemicalBlockType.LAMP,
                                        BlockRegistry.LAMP_BLOCKS,
                                        BlockRegistry.LAMP_PROPERTIES));
                                BlockRegistry.getRegistryObjectByName(String.format("%s_lamp_block",
                                    elementName)).ifPresent(block -> ItemRegistryUtils.lampFromChemicalBlock(block,
                                    new Item.Properties()));
                            }
                            FluidRegistryUtils.registerFluid(elementName,
                                ChemicalRegistry.fluidTypePropertiesFactory(properties, ChemLib.MODID, elementName),
                                Integer.parseInt(color, 16) | 0xFF000000,
                                slopeFindDistance,
                                decreasePerBlock);
                        }
                    }
                }
            }
        }
    }

    @Unique
    private static void chemlibcc$registerCompounds(final JsonObject rootObject) {
        for (final var jsonElement : rootObject.getAsJsonArray("compounds")) {
            final var object = jsonElement.getAsJsonObject();
            final var compoundName = object.get("name").getAsString();
            final var displayName = object.get("display_name").getAsString();
            final var matterState = MatterState.valueOf(object.get("matter_state").getAsString().toUpperCase(Locale.ROOT));
            final var description = object.has("description") ? object.get("description").getAsString() : "";
            final var color = object.get("color").getAsString();

            final var components = object.getAsJsonArray("components");
            final var componentMap = new LinkedHashMap<String, Integer>();
            for (final var component : components) {
                final var componentObject = component.getAsJsonObject();
                final var componentName = componentObject.get("name").getAsString();
                final var count = componentObject.has("count") ? componentObject.get("count").getAsInt() : 1;
                componentMap.put(componentName, count);
            }

            ItemRegistry.REGISTRY_COMPOUNDS.register(compoundName,
                () -> new GeneratedCompoundItem(compoundName,
                    displayName,
                    matterState,
                    componentMap,
                    description,
                    color,
                    ChemicalRegistry.mobEffectsFactory(object)));

            switch (matterState) {
                case SOLID -> {
                    final var hasItem = object.get("has_item").getAsBoolean();
                    if (!hasItem) {
                        ItemRegistryUtils.registerItemByType(ItemRegistry.getRegistryObject(ItemRegistry.REGISTRY_COMPOUNDS,
                            compoundName), ChemicalItemType.COMPOUND);
                        if (compoundName.equals("polyvinyl_chloride")) {
                            ItemRegistryUtils.registerItemByType(ItemRegistry.getRegistryObject(ItemRegistry.REGISTRY_COMPOUNDS,
                                compoundName), ChemicalItemType.PLATE);
                        }
                    }
                }
                case LIQUID, GAS -> {
                    final var hasFluid = object.has("has_fluid") && object.get("has_fluid").getAsBoolean();
                    if (!hasFluid) {
                        final var properties = object.get("fluid_properties").getAsJsonObject();
                        final var slopeFindDistance = properties.has("slope_find_distance") ? properties.get(
                            "slope_find_distance").getAsInt() : 4;
                        final var decreasePerBlock = properties.has("decrease_per_block") ? properties.get(
                            "decrease_per_block").getAsInt() : 1;

                        switch (matterState) {
                            case LIQUID, GAS -> FluidRegistryUtils.registerFluid(compoundName,
                                ChemicalRegistry.fluidTypePropertiesFactory(properties, ChemLib.MODID, compoundName),
                                Integer.parseInt(color, 16) | 0xFF000000,
                                slopeFindDistance,
                                decreasePerBlock);
                        }
                    }
                }
            }
        }
    }
}
