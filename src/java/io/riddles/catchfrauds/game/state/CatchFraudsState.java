/*
 * Copyright 2018 riddles.io (developers@riddles.io)
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

package io.riddles.catchfrauds.game.state;

import java.util.ArrayList;

import io.riddles.javainterface.game.state.AbstractState;

/**
 * io.riddles.catchfrauds.game.state.CatchFraudsState - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public class CatchFraudsState extends AbstractState<CatchFraudsPlayerState> {

    private Boolean isFraudulent;
    private boolean isBotDisqualified;

    public CatchFraudsState(ArrayList<CatchFraudsPlayerState> playerStates) {
        super(null, playerStates, 0);
        this.isFraudulent = null;
        this.isBotDisqualified = false;
    }

    public CatchFraudsState(CatchFraudsState previousState,
                            ArrayList<CatchFraudsPlayerState> playerStates, int roundNumber) {
        super(previousState, playerStates, roundNumber);
        this.isFraudulent = null;
        this.isBotDisqualified = previousState.isBotDisqualified();
    }

    public CatchFraudsState createNextState(int roundNumber) {
        // Create new player states from current player states
        ArrayList<CatchFraudsPlayerState> playerStates = new ArrayList<>();
        for (CatchFraudsPlayerState playerState : getPlayerStates()) {
            playerStates.add(new CatchFraudsPlayerState(playerState));
        }

        // Create new state from current state
        return new CatchFraudsState(this, playerStates, roundNumber);
    }

    public void setBotDisqualified() {
        this.isBotDisqualified = true;
    }

    public boolean isBotDisqualified() {
        return this.isBotDisqualified;
    }

    public void setIsFraudulent(boolean isFraudulent) {
        this.isFraudulent = isFraudulent;
    }

    public Boolean isFraudulent() {
        return this.isFraudulent;
    }
}
