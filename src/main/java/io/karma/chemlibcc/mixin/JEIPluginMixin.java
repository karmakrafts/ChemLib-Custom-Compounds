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

import com.smashingmods.chemlib.client.jei.JEIPlugin;
import com.smashingmods.chemlib.registry.ItemRegistry;
import io.karma.chemlibcc.item.GeneratedCompoundItem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Alexander Hinze
 * @since 09/10/2024
 */
@Mixin(value = JEIPlugin.class, remap = false)
public final class JEIPluginMixin {
    @Inject(method = "registerRecipes", at = @At("HEAD"), cancellable = true)
    private void onRegisterRecipes(final IRecipeRegistration registration, final CallbackInfo cbi) {
        for (final var element : ItemRegistry.getElements()) {
            registration.addIngredientInfo(new ItemStack(element),
                VanillaTypes.ITEM_STACK,
                MutableComponent.create(new TranslatableContents("chemlib.jei.element.description",
                    null,
                    TranslatableContents.NO_ARGS)));
        }

        for (final var compound : ItemRegistry.getCompounds()) {
            if (compound instanceof GeneratedCompoundItem generatedCompound) {
                registration.addIngredientInfo(new ItemStack(compound),
                    VanillaTypes.ITEM_STACK,
                    generatedCompound.getDescription());
                continue;
            }
            registration.addIngredientInfo(new ItemStack(compound),
                VanillaTypes.ITEM_STACK,
                MutableComponent.create(new TranslatableContents(String.format("chemlib.jei.compound.%s.description",
                    compound.getChemicalName()), null, TranslatableContents.NO_ARGS)));
        }

        cbi.cancel();
    }
}
