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

package io.riddles.javainterface.game.processor;

import java.util.ArrayList;
import java.util.logging.Logger;

import io.riddles.javainterface.game.player.AbstractPlayer;
import io.riddles.javainterface.game.state.AbstractState;

/**
 * io.riddles.javainterface.engine.Processor - Created on 6-6-16
 *
 * [description]
 *
 * @author jim
 */
public abstract class AbstractProcessor<P extends AbstractPlayer, S extends AbstractState> {

    protected final static Logger LOGGER = Logger.getLogger(AbstractProcessor.class.getName());

    protected ArrayList<P> players;

    public AbstractProcessor(ArrayList<P> players) {
        this.players = players;
    }

    public ArrayList<P> getPlayers() {
        return this.players;
    }

    public abstract void preGamePhase();

    public abstract S playRound(int roundNumber, S state);

    public abstract boolean hasGameEnded(S state);

    public abstract P getWinner();

    public abstract int getScore();
}
