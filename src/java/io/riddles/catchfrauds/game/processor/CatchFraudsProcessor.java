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

package io.riddles.catchfrauds.game.processor;

import java.util.ArrayList;

import io.riddles.catchfrauds.game.move.ActionType;
import io.riddles.catchfrauds.game.move.CatchFraudsMove;
import io.riddles.catchfrauds.game.move.CatchFraudsMoveDeserializer;
import io.riddles.catchfrauds.game.data.Record;
import io.riddles.catchfrauds.game.player.CatchFraudsPlayer;
import io.riddles.catchfrauds.game.state.CatchFraudsState;
import io.riddles.javainterface.game.processor.AbstractProcessor;

/**
 * io.riddles.catchfrauds.interface.CatchFraudsProcessor - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public class CatchFraudsProcessor extends AbstractProcessor<CatchFraudsPlayer, CatchFraudsState> {

    private ArrayList<String> checkPointValues;
    private ArrayList<Record> records;
    private int roundNumber;
    private boolean gameOver;

    public CatchFraudsProcessor(ArrayList<CatchFraudsPlayer> players, ArrayList<Record> records) {
        super(players);
        this.records = records;
        this.checkPointValues = new ArrayList<>();
        this.gameOver = false;
    }

    @Override
    public void preGamePhase() {
        for (CatchFraudsPlayer player : this.players) {
            storeCheckpointInput(player.requestMove(ActionType.CHECKPOINTS.toString()));
        }
    }

    @Override
    public CatchFraudsState playRound(int roundNumber, CatchFraudsState state) {
        this.roundNumber = roundNumber;
        LOGGER.info(String.format("Playing round %d", roundNumber));

        int checkPointCount = checkPointValues.size();
        CatchFraudsState nextState = null;

        for (CatchFraudsPlayer player : this.players) {

            Record record = this.records.get(this.roundNumber - 1);

            // send next record and ask bot's assessment of the record
            player.sendUpdate("next_record", player, record.toBotString());
            String response = player.requestMove(ActionType.RECORD.toString());

            // parse the response
            CatchFraudsMoveDeserializer deserializer = new CatchFraudsMoveDeserializer(
                    player, checkPointCount);
            CatchFraudsMove move = deserializer.traverse(response);

            // create the next state
            nextState = new CatchFraudsState(state, move, roundNumber, record.isFraudulent());

            state = nextState;

            // stop game if bot returns nothing
            if (response == null) {
                this.gameOver = true;
            }
        }

        return nextState;
    }

    @Override
    public boolean hasGameEnded(CatchFraudsState state) {
        return this.gameOver || this.roundNumber >= this.records.size();
    }

    @Override
    public CatchFraudsPlayer getWinner() {
        return null;
    }

    @Override
    public int getScore() {
        return 0;
    }

    public ArrayList<String> getCheckPointValues() {
        return this.checkPointValues;
    }

    private void storeCheckpointInput(String input) {
        if (input.length() <= 0) return;

        String[] values = input.split(";");
        for (String value : values) {
            value = value.trim();
            if (value.length() > 0) {
                this.checkPointValues.add(value);
            }
        }
    }
}
