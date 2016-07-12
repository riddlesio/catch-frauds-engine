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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import io.riddles.catchfrauds.CatchFrauds;
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

    private int MAX_CHECKPOINTS;

    private ArrayList<String> checkPointValues;
    private ArrayList<Record> records;
    private int roundNumber;
    private boolean gameOver;
    private double scoreDelta; // subtracted from player score for each mistake

    public CatchFraudsProcessor(ArrayList<CatchFraudsPlayer> players,
                                ArrayList<Record> records, int maxCheckPoints) {
        super(players);
        this.records = records;
        this.checkPointValues = new ArrayList<>();
        this.gameOver = false;
        this.scoreDelta = 100.0 / records.size();

        MAX_CHECKPOINTS = maxCheckPoints;
    }

    @Override
    public void preGamePhase() {
        for (CatchFraudsPlayer player : this.players) {
            storeCheckpointInput(player, player.requestMove(ActionType.CHECKPOINTS.toString()));
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

            this.updateScore(nextState);

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
    public double getScore() {
        double score = this.getPlayers().get(0).getScore();
        BigDecimal bdScore = new BigDecimal(score);

        return bdScore.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public ArrayList<String> getCheckPointValues() {
        return this.checkPointValues;
    }

    private void updateScore(CatchFraudsState state) {
        CatchFraudsMove move = state.getMoves().get(0);
        CatchFraudsPlayer player = move.getPlayer();

        player.updateScore(state.isFraudulent(), move.isRefused(), this.scoreDelta);
    }

    private void storeCheckpointInput(CatchFraudsPlayer player, String input) {
        if (input.length() <= 0) return;

        String[] values = input.split(";");

        if (values.length > MAX_CHECKPOINTS) {
            String warning = String.format("Your bot cannot have more " +
                    "than %d check points. Bot is shut down.", MAX_CHECKPOINTS);
            player.sendWarning(warning);
            this.gameOver = true;

            return;
        }

        for (String value : values) {
            value = value.trim();
            if (value.length() > 0) {
                this.checkPointValues.add(value);
            }
        }
    }
}
