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

package de.mcmdev.vanish.config;

import space.arim.dazzleconf.engine.Comments;
import space.arim.dazzleconf.engine.liaison.SubSection;

@Comments("Configuration file for mcmdev's vanish plugin")
public interface Config {

    default StorageType storageType() {
        return StorageType.PERSISTENT_DATA_CONTAINER;
    }

    default int maximumHidingLevel() {
        return 100;
    }

    default CommandHook vanishHooks() {
        return CommandHook.empty();
    }

    default CommandHook unvanishHooks() {
        return CommandHook.empty();
    }

    default CommandHook playerClickHook() {
        return CommandHook.of("inv %player_name%");
    }

    default String vanishHostRegex() {
        return "vanish\\.localhost";
    }

    @SubSection
    default Messages messages() {
        return new Messages() {
        };
    }

    enum StorageType {
        PERSISTENT_DATA_CONTAINER,
    }

    interface Messages {

        default Message actionbar() {
            return Message.of("<aqua>You are invisible.");
        }

        default Message toggleOn() {
            return Message.of("<aqua>You are now invisible.");
        }

        default Message toggleOff() {
            return Message.of("<aqua>You are no longer invisible.");
        }

        default Message levelOverrideSet() {
            return Message.of("<aqua>Your level override has been set to <yellow><level></yellow>.");
        }

        default Message levelOverrideCleared() {
            return Message.of("<aqua>Your level override has been cleared.");
        }

        default Message levelOverrideNotSet() {
            return Message.of("<red>You currently have no level override set.");
        }

        default Message fakeJoin() {
            return Message.of("<yellow>%player_name% joined the game.</yellow>");
        }

        default Message fakeQuit() {
            return Message.of("<yellow>%player_name% left the game.</yellow>");
        }

        default Message notifyJoin() {
            return Message.of("<aqua>%player_name% joined the game while invisible.</aqua>");
        }

        default Message notifyQuit() {
            return Message.of("<aqua>%player_name% left the game while invisible.</aqua>");
        }

    }

}
