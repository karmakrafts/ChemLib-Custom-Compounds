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
import com.smashingmods.chemlib.api.ChemicalItemType;
import io.karma.chemlibcc.ChemLibCC;
import io.karma.chemlibcc.util.GeneratedChemical;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 09/10/2024
 */
public final class GeneratedCompoundDustItem extends GeneratedChemicalItem {
    public GeneratedCompoundDustItem(final ResourceLocation name,
                                     final ChemicalItemType chemicalItemType,
                                     final Properties properties) {
        super(name, chemicalItemType, properties);
    }

    @Override
    public @NotNull Component getName(final @NotNull ItemStack stack) {
        final var chemical = getChemical();
        if (!(chemical instanceof GeneratedChemical generatedChemical)) {
            return super.getName(stack);
        }
        final var compoundNameKey = String.format("item.%s.compound_%s", ChemLib.MODID, getChemicalName());
        // @formatter:off
        var compoundName = I18n.exists(compoundNameKey)
            ? I18n.get(compoundNameKey)
            : generatedChemical.getDisplayName();
        // @formatter:on
        return Component.literal(String.format("%s %s",
            compoundName,
            I18n.get(String.format("%s.dust", ChemLibCC.MODID))));
    }
}
