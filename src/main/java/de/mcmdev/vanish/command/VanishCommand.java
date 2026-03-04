/*
 * vanish
 * Copyright (C) 2026 MCMDEV
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.mcmdev.vanish.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.mcmdev.vanish.VanishPlugin;
import de.mcmdev.vanish.api.VanishApi;
import de.mcmdev.vanish.config.Config;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import jakarta.inject.Inject;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class VanishCommand {

    private final VanishPlugin plugin;
    private final VanishApi vanishApi;
    private final Config config;

    @Inject
    public VanishCommand(final VanishPlugin plugin, final VanishApi vanishApi, final Config config) {
        this.plugin = plugin;
        this.vanishApi = vanishApi;
        this.config = config;
    }

    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(createCommandNode()));
    }

    private LiteralCommandNode<CommandSourceStack> createCommandNode() {
        return Commands.literal("vanish")
                .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("vanish.command"))
                .executes(this::run)
                .then(
                        Commands.literal("setlevel")
                                .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("vanish.command.setlevel"))
                                .executes(this::runSetlevel)
                                .then(
                                        Commands.argument("level", IntegerArgumentType.integer(0, config.maximumHidingLevel()))
                                                .executes(this::runSetlevelLevel)
                                )
                )
                .build();
    }

    private int run(final CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof final Player player)) {
            return 1;
        }

        if (vanishApi.isVanished(player)) {
            vanishApi.unvanish(player);
            config.messages().toggleOff().send(player);
        } else {
            vanishApi.vanish(player);
            config.messages().toggleOn().send(player);
        }

        return 0;
    }

    private int runSetlevel(final CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof final Player player)) {
            return 1;
        }

        if (!vanishApi.supportsLevels()) {
            player.sendRichMessage("<red>Vanish levels are not available in the current configuration.");
            return 2;
        }

        final boolean hasOverride = vanishApi.getLevelOverride(player.getUniqueId()) != null;
        if (!hasOverride) {
            config.messages().levelOverrideNotSet().send(player);
            return 3;
        }

        vanishApi.setLevelOverride(player.getUniqueId(), null);
        config.messages().levelOverrideCleared().send(player);

        return 0;
    }

    private int runSetlevelLevel(final CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof final Player player)) {
            return 1;
        }

        final Integer level = context.getArgument("level", Integer.class);

        if (!vanishApi.supportsLevels()) {
            player.sendRichMessage("<red>Vanish levels are not available in the current configuration.");
            return 2;
        }

        vanishApi.setLevelOverride(player.getUniqueId(), level);
        config.messages().levelOverrideSet().send(player, Placeholder.parsed("level", String.valueOf(level)));

        return 0;
    }

}
