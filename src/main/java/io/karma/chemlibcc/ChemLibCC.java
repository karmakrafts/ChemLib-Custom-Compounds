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

import com.smashingmods.chemlib.ChemLib;
import io.karma.chemlibcc.item.GeneratedCompoundDustItem;
import io.karma.chemlibcc.item.GeneratedCompoundItem;
import io.karma.chemlibcc.item.GeneratedElementItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Alexander Hinze
 * @since 09/10/2024
 */
@Mod(ChemLibCC.MODID)
public class ChemLibCC {
    public static final String MODID = "chemlibcc";
    public static final Logger LOGGER = LogManager.getLogger("ChemLib CC");
    private static final ResourceLocation COMPOUND_MODEL = new ResourceLocation(ChemLib.MODID,
        "item/compound_solid_model");
    private static final ResourceLocation COMPOUND_DUST_MODEL = new ResourceLocation(ChemLib.MODID,
        "item/compound_dust_model");
    private static final ResourceLocation COMPOUND_GAS_MODEL = new ResourceLocation(ChemLib.MODID,
        "item/compound_gas_model");
    private static final ResourceLocation COMPOUND_LIQUID_MODEL = new ResourceLocation(ChemLib.MODID,
        "item/compound_liquid_model");
    private static final ResourceLocation BUILTIN_MODEL = new ResourceLocation(MODID, "item/builtin");

    public ChemLibCC() {
        LOGGER.info("Hello, World!");
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            final var bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addListener(this::onRegisterAdditional);
            bus.addListener(this::onModifyBakingResult);
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void onRegisterAdditional(final ModelEvent.RegisterAdditional event) {
        LOGGER.info("Registering additional item models");
        event.register(COMPOUND_MODEL);
        event.register(COMPOUND_DUST_MODEL);
        event.register(COMPOUND_GAS_MODEL);
        event.register(COMPOUND_LIQUID_MODEL);
        event.register(BUILTIN_MODEL);
    }

    @OnlyIn(Dist.CLIENT)
    private void onModifyBakingResult(final ModelEvent.ModifyBakingResult event) {
        final var models = event.getModels();

        final var builtinModel = models.get(BUILTIN_MODEL);
        final var compoundModel = models.get(COMPOUND_MODEL);
        final var compoundDustModel = models.get(COMPOUND_DUST_MODEL);
        final var compoundGasModel = models.get(COMPOUND_GAS_MODEL);
        final var compoundLiquidModel = models.get(COMPOUND_LIQUID_MODEL);

        for (final var entry : ForgeRegistries.ITEMS.getEntries()) {
            final var item = entry.getValue();
            final var itemName = entry.getKey().location();
            if (item instanceof GeneratedElementItem) {
                models.put(new ModelResourceLocation(itemName, "inventory"), builtinModel);
                LOGGER.info("Patched item model for {}", itemName);
                continue;
            }
            if (item instanceof GeneratedCompoundItem compoundItem) {
                final var model = switch (compoundItem.getMatterState()) {
                    case SOLID -> compoundModel;
                    case GAS -> compoundGasModel;
                    case LIQUID -> compoundLiquidModel;
                };
                models.put(new ModelResourceLocation(itemName, "inventory"), model);
                LOGGER.info("Patched item model for {}", itemName);
                continue;
            }
            if (item instanceof GeneratedCompoundDustItem) {
                models.put(new ModelResourceLocation(itemName, "inventory"), compoundDustModel);
                LOGGER.info("Patched item model for {}", itemName);
            }
        }
    }
}
