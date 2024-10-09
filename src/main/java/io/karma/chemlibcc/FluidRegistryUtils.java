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

package io.karma.chemlibcc;

import com.smashingmods.chemlib.common.blocks.ChemicalLiquidBlock;
import com.smashingmods.chemlib.registry.FluidRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 09/10/2024
 */
public final class FluidRegistryUtils {
    // @formatter:off
    private FluidRegistryUtils() {}
    // @formatter:on

    public static void registerFluid(String pName,
                                     FluidType.Properties pFluidProperties,
                                     int pColor,
                                     int slopeFindDistance,
                                     int pDecreasePerBlock) {
        final var ref = new Object() {
            ForgeFlowingFluid.Properties properties = null;
        };

        RegistryObject<FluidType> fluidType = FluidRegistry.FLUID_TYPES.register(pName,
            () -> new FluidType(pFluidProperties) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        @Override
                        public ResourceLocation getStillTexture() {
                            return new ResourceLocation("block/water_still");
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return new ResourceLocation("block/water_flow");
                        }

                        @Override
                        public ResourceLocation getOverlayTexture() {
                            return new ResourceLocation("block/water_overlay");
                        }

                        @Override
                        public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                            return new ResourceLocation("minecraft", "textures/misc/underwater.png");
                        }

                        @Override
                        public int getTintColor() {
                            return pColor;
                        }

                        @Override
                        public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                            return pColor;
                        }
                    });
                }
            });

        final var fluidSource = FluidRegistry.FLUIDS.register(String.format("%s_fluid", pName),
            () -> new ForgeFlowingFluid.Source(ref.properties));
        final var fluidFlowing = FluidRegistry.FLUIDS.register(String.format("%s_flowing", pName),
            () -> new ForgeFlowingFluid.Flowing(ref.properties));
        final var liquidBlock = FluidRegistry.LIQUID_BLOCKS.register(pName,
            () -> new ChemicalLiquidBlock(fluidSource, pName));
        final var bucket = FluidRegistry.BUCKETS.register(String.format("%s_bucket", pName),
            () -> new BucketItem(fluidSource, new Item.Properties().stacksTo(1)));

        ref.properties = new ForgeFlowingFluid.Properties(fluidType, fluidSource, fluidFlowing).slopeFindDistance(
            slopeFindDistance).levelDecreasePerBlock(pDecreasePerBlock).block(liquidBlock).bucket(bucket);
    }

}
