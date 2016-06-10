/*
 * Copyright 2016 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

package io.riddles.javainterface.game.move;

import io.riddles.javainterface.game.player.AbstractPlayer;

/**
 * io.riddles.javainterface.engine.move.AbstractMove - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public abstract class AbstractMove<P extends AbstractPlayer> {

    private P player;
    private Exception invalidException;

    public AbstractMove(P player) {
        this.player = player;
        this.invalidException = null;
    }

    public AbstractMove(P player, Exception exception) {
        this.player = player;
        this.invalidException = exception;
    }

    /**
     * @return True if this move is invalid
     */
    public boolean isInvalid() {
        return this.invalidException != null;
    }

    /**
     * Sets the name of the Player that this Move belongs to
     * @param player player
     */
    public void setPlayer(P player) {
        this.player = player;
    }

    /**
     * Sets the Exception of this move. Only set this if the Move is invalid.
     * @param exception exception
     */
    public void setException(Exception exception) {
        this.invalidException = exception;
    }

    /**
     * @return The player that this Move belongs to
     */
    public P getPlayer() {
        return this.player;
    }

    /**
     * @return The exception of this move
     */
    public Exception getException() {
        return this.invalidException;
    }
}
