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

package io.riddles.catchfrauds.game;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.riddles.catchfrauds.engine.CatchFraudsEngine;
import io.riddles.catchfrauds.game.checkpoint.CheckPoint;
import io.riddles.catchfrauds.game.processor.CatchFraudsProcessor;
import io.riddles.catchfrauds.game.state.CatchFraudsPlayerState;
import io.riddles.catchfrauds.game.state.CatchFraudsState;
import io.riddles.catchfrauds.game.state.CatchFraudsStateSerializer;
import io.riddles.javainterface.game.AbstractGameSerializer;

/**
 * io.riddles.catchfrauds.game.GameSerializer - Created on 8-6-16
 *
 * [description]
 *
 * @author jim
 */
public class CatchFraudsGameSerializer extends AbstractGameSerializer<CatchFraudsProcessor, CatchFraudsState> {

    @Override
    public String traverseToString(CatchFraudsProcessor processor, CatchFraudsState initialState) {
        CatchFraudsStateSerializer serializer = new CatchFraudsStateSerializer();

        JSONObject game = new JSONObject();
        game = addDefaultJSON(initialState, game, processor);

        // add all states
        JSONArray states = new JSONArray();
        CatchFraudsState state = initialState;
        while (state.hasNextState()) {
            state = (CatchFraudsState) state.getNextState();
            states.put(serializer.traverseToJson(state));
        }

        CatchFraudsPlayerState playerState = state.getPlayerStates().get(0);

        game.put("states", states);
        game.put("checkpoints", visitCheckPoints(playerState.getCheckPoints()));
        game.put("fraudTypes", visitFraudTypes(CatchFraudsEngine.FRAUD_TYPES));

        return game.toString();
    }

    private JSONArray visitCheckPoints(ArrayList<CheckPoint> checkPoints) {
        JSONArray checkPointsArray = new JSONArray();

        for (CheckPoint checkPoint : checkPoints) {
            JSONObject checkPointJson = new JSONObject();

            checkPointJson.put("id", checkPoint.getId());
            checkPointJson.put("description", checkPoint.getDescription());
            checkPointJson.put("falsePositives", checkPoint.getFalsePositives());
            checkPointJson.put("frauds", visitCheckPointFrauds(checkPoint));

            checkPointsArray.put(checkPointJson);
        }

        return checkPointsArray;
    }

    private JSONArray visitCheckPointFrauds(CheckPoint checkPoint) {
        JSONArray frauds = new JSONArray();

        int[] detectedFrauds = checkPoint.getDetectedFrauds();

        for (int type = 1; type < detectedFrauds.length; type++) {
            JSONObject fraud = new JSONObject();

            fraud.put("type", type);
            fraud.put("detectedFrauds", detectedFrauds[type]);

            frauds.put(fraud);
        }

        return frauds;
    }

    private JSONArray visitFraudTypes(HashMap<Integer, String> fraudTypes) {
        JSONArray fraudTypesArray = new JSONArray();

        for (Map.Entry<Integer, String> entry : fraudTypes.entrySet()) {
            JSONObject fraudType = new JSONObject();

            fraudType.put("type", entry.getKey());
            fraudType.put("description", entry.getValue());

            fraudTypesArray.put(fraudType);
        }

        return fraudTypesArray;
    }
}
