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
import com.smashingmods.chemlib.common.items.CompoundItem;
import io.karma.chemlibcc.util.GeneratedChemical;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Hinze
 * @since 09/10/2024
 */
public final class GeneratedCompoundItem extends CompoundItem implements GeneratedChemical {
    private final String displayName;

    public GeneratedCompoundItem(String compoundName,
                                 String displayName,
                                 MatterState matterState,
                                 Map<String, Integer> components,
                                 String description,
                                 String color,
                                 List<MobEffectInstance> effects) {
        super(compoundName, matterState, components, description, color, effects);
        this.displayName = displayName;
    }

    @Override
    public @NotNull Component getName(final @NotNull ItemStack stack) {
        return Component.translatableWithFallback(String.format("item.%s.compound_%s",
            ChemLib.MODID,
            getChemicalName()), displayName);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.translatableWithFallback(String.format("%s.jei.compound.%s.description",
            ChemLib.MODID,
            getChemicalName()), getChemicalDescription());
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
