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

package io.riddles.catchfrauds.game.processor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import io.riddles.catchfrauds.engine.CatchFraudsEngine;
import io.riddles.catchfrauds.game.move.ActionType;
import io.riddles.catchfrauds.game.move.CatchFraudsMove;
import io.riddles.catchfrauds.game.move.CatchFraudsMoveDeserializer;
import io.riddles.catchfrauds.game.data.Record;
import io.riddles.catchfrauds.game.player.CatchFraudsPlayer;
import io.riddles.catchfrauds.game.state.CatchFraudsPlayerState;
import io.riddles.catchfrauds.game.state.CatchFraudsState;
import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.javainterface.game.processor.SimpleProcessor;

/**
 * io.riddles.catchfrauds.interface.CatchFraudsProcessor - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public class CatchFraudsProcessor extends SimpleProcessor<CatchFraudsState, CatchFraudsPlayer> {

    private CatchFraudsMoveDeserializer moveDeserializer;
    private ArrayList<Record> poppingRecords;
    private double totalFraudulentRecords;

    public CatchFraudsProcessor(PlayerProvider<CatchFraudsPlayer> playerProvider) {
        super(playerProvider);
        this.moveDeserializer = new CatchFraudsMoveDeserializer();
        this.poppingRecords = new ArrayList<>(CatchFraudsEngine.RECORDS);
        this.totalFraudulentRecords = 0.0;
    }

    @Override
    public boolean hasGameEnded(CatchFraudsState state) {
        int recordCount = CatchFraudsEngine.configuration.getInt("recordCount");

        if (state.isBotDisqualified()) {
            return true;
        }

        if (recordCount < 0) {
            return state.getRoundNumber() >= CatchFraudsEngine.RECORDS.size();
        }

        return state.getRoundNumber() >= recordCount;
    }

    @Override
    public Integer getWinnerId(CatchFraudsState state) {
        return null;
    }

    @Override
    public double getScore(CatchFraudsState state) {
        if (state.isBotDisqualified()) {
            return -1.0;
        }

        CatchFraudsPlayerState playerState = state.getPlayerStates().get(0);

        int recordCount = CatchFraudsEngine.configuration.getInt("recordCount") < 0
                ? CatchFraudsEngine.RECORDS.size()
                : CatchFraudsEngine.configuration.getInt("recordCount");

        double c1 = CatchFraudsEngine.configuration.getDouble("c1Value");
        double penalty = (100 * c1 * Math.pow(playerState.getFalsePositives(), 2)) / recordCount;

        double score = this.totalFraudulentRecords > 0
                ? ((playerState.getDetectedFrauds() / this.totalFraudulentRecords) * 100) - penalty
                : 100 - penalty;

        score = Math.max(0.0, score);

        return new BigDecimal(score).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public CatchFraudsState createNextState(CatchFraudsState inputState, int roundNumber) {
        CatchFraudsState nextState = inputState.createNextState(roundNumber);

        Record record = getNextRecord(roundNumber);
        if (record.isFraudulent()) {
            this.totalFraudulentRecords++;
        }

        nextState.setIsFraudulent(record.isFraudulent());

        for (CatchFraudsPlayerState playerState : nextState.getPlayerStates()) {
            CatchFraudsPlayer player = getPlayer(playerState.getPlayerId());
            sendUpdatesToPlayer(player, record);

            CatchFraudsMove move = getPlayerMove(player, playerState.getCheckPoints().size());
            playerState.setMove(move);

            if (move.isInvalid()) {
                nextState.setBotDisqualified();
            } else if (move.isRefused()) {
                playerState.detectFraud(record, move.getCheckPointIds());
            }
        }

        return nextState;
    }

    private void sendUpdatesToPlayer(CatchFraudsPlayer player, Record record) {
        player.sendUpdate("next_record", player, record.toBotString());
    }

    private Record getNextRecord(int roundNumber) {
        // Just get all the records one by one
        if (CatchFraudsEngine.configuration.getInt("recordCount") < 0) {
            return CatchFraudsEngine.RECORDS.get(roundNumber - 1);
        }

        // Select random record
        int randomIndex = CatchFraudsEngine.RANDOM.nextInt(this.poppingRecords.size());
        return this.poppingRecords.remove(randomIndex);
    }

    private CatchFraudsMove getPlayerMove(CatchFraudsPlayer player, int checkPointCount) {
        String response = player.requestMove(ActionType.RECORD);
        return this.moveDeserializer.traverse(response, checkPointCount);
    }

    private CatchFraudsPlayer getPlayer(int id) {
        return this.playerProvider.getPlayerById(id);
    }
}
