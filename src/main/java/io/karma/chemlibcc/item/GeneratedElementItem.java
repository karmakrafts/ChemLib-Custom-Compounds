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

package io.karma.chemlibcc.item;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Alexander Hinze
 * @since 09/10/2024
 */
public final class GeneratedElementItem extends ElementItem {
    private final String displayName;

    public GeneratedElementItem(final String chemicalName,
                                final String displayName,
                                final int atomicNumber,
                                final String abbreviation,
                                final int group,
                                final int period,
                                final MatterState matterState,
                                final MetalType metalType,
                                final boolean artificial,
                                final String color,
                                final List<MobEffectInstance> effects) {
        super(chemicalName,
            atomicNumber,
            abbreviation,
            group,
            period,
            matterState,
            metalType,
            artificial,
            color,
            effects);
        this.displayName = displayName;
    }

    @Override
    public @NotNull Component getName(final @NotNull ItemStack stack) {
        return Component.translatableWithFallback(String.format("item.%s.element_%s", ChemLib.MODID, getChemicalName()),
            displayName);
    }
}
