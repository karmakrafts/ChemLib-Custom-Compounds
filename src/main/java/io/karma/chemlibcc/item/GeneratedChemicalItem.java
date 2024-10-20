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
import com.smashingmods.chemlib.common.items.ChemicalItem;
import io.karma.chemlibcc.ChemLibCC;
import io.karma.chemlibcc.util.GeneratedChemical;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Alexander Hinze
 * @since 09/10/2024
 */
public class GeneratedChemicalItem extends ChemicalItem {
    public GeneratedChemicalItem(final ResourceLocation pResourceLocation,
                                 final ChemicalItemType pChemicalItemType,
                                 final Properties pProperties) {
        super(pResourceLocation, pChemicalItemType, pProperties);
    }

    @Override
    public @NotNull Component getName(final @NotNull ItemStack stack) {
        final var chemical = getChemical();
        if (!(chemical instanceof GeneratedChemical generatedChemical)) {
            return super.getName(stack);
        }
        final var compoundNameKey = String.format("item.%s.element_%s", ChemLib.MODID, getChemicalName());
        // @formatter:off
        var compoundName = I18n.exists(compoundNameKey)
            ? I18n.get(compoundNameKey)
            : generatedChemical.getDisplayName();
        // @formatter:on
        return Component.literal(String.format("%s %s",
            compoundName,
            I18n.get(String.format("%s.%s", ChemLibCC.MODID, getItemType().getSerializedName()))));
    }

    @Override
    public void appendHoverText(final @NotNull ItemStack stack,
                                final @Nullable Level world,
                                final @NotNull List<Component> tooltip,
                                final @NotNull TooltipFlag isAdvanced) {
        tooltip.add(Component.translatable(String.format("tooltip.%s", ChemLibCC.MODID)));
        super.appendHoverText(stack, world, tooltip, isAdvanced);
    }
}
