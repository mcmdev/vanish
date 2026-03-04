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

package de.mcmdev.vanish.listeners;

import de.mcmdev.vanish.config.Config;
import de.mcmdev.vanish.storage.Storage;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetSocketAddress;
import java.util.regex.Pattern;

public final class VanishHostListener implements Listener {

    private final Storage storage;
    private final Pattern pattern;

    @Inject
    public VanishHostListener(Storage storage, Config config) {
        this.storage = storage;

        this.pattern = Pattern.compile(config.vanishHostRegex());
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        InetSocketAddress virtualHost = player.getVirtualHost();
        if (virtualHost == null) return;

        if (pattern.matcher(virtualHost.getHostString()).matches()) {
            storage.setVanished(player.getUniqueId(), true);
        }
    }

}
