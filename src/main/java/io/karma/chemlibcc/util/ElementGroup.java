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

import io.karma.chemlibcc.ChemLibCC;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * @author Alexander Hinze
 * @since 12/10/2024
 */
public enum ElementGroup implements StringRepresentable {
    // @formatter:off
    REACTIVE_NON_METALS,
    NOBLE_GASSES,
    ALKALI_METALS,
    ALKALINE_EARTH_METALS,
    METALLOIDS,
    HALOGENS,
    POST_TRANSITION_METALS,
    TRANSITION_METALS,
    LANTHANIDES,
    ACTINIDES,
    UNKNOWN_PROPERTIES;
    // @formatter:on

    public static ElementGroup byName(final String name) {
        for (final var group : values()) {
            if (!group.getSerializedName().equals(name)) {
                continue;
            }
            return group;
        }
        return UNKNOWN_PROPERTIES;
    }

    public static ElementGroup byAtomicNumber(final int atomicNumber) {
        return switch (atomicNumber) {
            case 1, 6, 7, 8, 15, 16, 34 -> REACTIVE_NON_METALS;
            case 2, 10, 18, 36, 54, 86 -> NOBLE_GASSES;
            case 3, 11, 19, 37, 55, 87 -> ALKALI_METALS;
            case 4, 12, 20, 38, 56, 88 -> ALKALINE_EARTH_METALS;
            case 5, 14, 32, 33, 51, 52 -> METALLOIDS;
            case 9, 17, 35, 53, 85 -> HALOGENS;
            case 13, 31, 49, 50, 81, 82, 83, 84 -> POST_TRANSITION_METALS;
            case 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 72, 73, 74, 75, 76, 77,
                 78, 79, 80, 104, 105, 106, 107, 108 -> TRANSITION_METALS;
            case 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71 -> LANTHANIDES;
            case 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103 -> ACTINIDES;
            default -> UNKNOWN_PROPERTIES;
        };
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String getLocalizedName() {
        return I18n.get(String.format("group.%s.%s", ChemLibCC.MODID, getSerializedName()));
    }

    public Component getLocalizedNameComponent() {
        return Component.literal(getLocalizedName());
    }
}
