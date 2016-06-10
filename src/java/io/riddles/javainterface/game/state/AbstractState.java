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

package io.riddles.javainterface.game.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.riddles.javainterface.game.move.AbstractMove;

/**
 * io.riddles.javainterface.engine.state.AbstractState - Created on 3-6-16
 *
 * [description]
 *
 * @author jim
 */
public abstract class AbstractState {

    private AbstractState previousState;
    private AbstractState nextState;
    private int roundNumber;
    private List moves;

    public AbstractState() {
        this.previousState = null;
        this.nextState = null;
        this.roundNumber = -1;
        this.moves = new ArrayList<AbstractMove>();
    }

    public AbstractState(AbstractState previousState, List moves, int roundNumber) {
        this.previousState = previousState;
        this.previousState.nextState = this;
        this.roundNumber = roundNumber;
        this.moves = moves;
    }

    public AbstractState(AbstractState previousState, AbstractMove move, int roundNumber) {
        this.previousState = previousState;
        this.previousState.nextState = this;
        this.roundNumber = roundNumber;
        this.moves = Collections.singletonList(move);
    }

    public AbstractState getPreviousState() {
        return this.previousState;
    }

    public AbstractState getNextState() {
        return this.nextState;
    }

    public int getRoundNumber() {
        return this.roundNumber;
    }

    public List getMoves() {
        return this.moves;
    }

    public boolean hasNextState() {
        return this.nextState != null;
    }

    public boolean hasPreviousState() {
        return this.previousState != null;
    }
}
