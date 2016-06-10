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

package io.riddles.catchfrauds.game.state;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.riddles.catchfrauds.game.move.CatchFraudsMove;
import io.riddles.catchfrauds.game.move.CheckPoint;
import io.riddles.catchfrauds.game.player.CatchFraudsPlayer;
import io.riddles.javainterface.game.state.AbstractStateDeserializer;

/**
 * io.riddles.catchfrauds.game.state.CatchFraudsStateDeserializer - Created on 3-6-16
 *
 * [description]
 *
 * @author jim
 */
public class CatchFraudsStateDeserializer extends
        AbstractStateDeserializer<CatchFraudsPlayer, CatchFraudsState> {

    public CatchFraudsStateDeserializer (ArrayList<CatchFraudsPlayer> players) {
        super(players);
    }

    @Override
    public CatchFraudsState traverse(String statesString) throws JSONException {
        CatchFraudsState state = null;
        Object states = new JSONObject(statesString);

        if (states instanceof JSONArray) {
            JSONArray statesJson = (JSONArray) states;
            for (int i = 0; i < statesJson.length(); i++) {
                JSONObject stateJson = statesJson.getJSONObject(i);
                state = visitState(stateJson, state);
            }
        } else if (states instanceof JSONObject) {
            JSONObject stateJson = (JSONObject) states;
            state = visitState(stateJson, null);
        } else {
            throw new JSONException("Input string is not array and not object");
        }

        return state;
    }

    private CatchFraudsState visitState(JSONObject stateJson,
                                        CatchFraudsState previousState) throws JSONException {
        int roundNumber = stateJson.getInt("round");
        boolean isFraudulent = stateJson.getBoolean("isFraudulent");
        boolean isBusted = stateJson.getBoolean("isBusted");
        CheckPoint[] checkPoints = visitCheckPoints(stateJson.getJSONArray("isApproved"));

        CatchFraudsMove move = new CatchFraudsMove(this.getPlayers().get(1), isBusted, checkPoints);

        return new CatchFraudsState(previousState, move, roundNumber, isFraudulent);
    }

    private CheckPoint[] visitCheckPoints(JSONArray checkPointsJson) throws JSONException {
        CheckPoint[] checkPoints = new CheckPoint[checkPointsJson.length()];

        for (int i = 0; i < checkPointsJson.length(); i++) {
            checkPoints[i] = new CheckPoint(checkPointsJson.getBoolean(i));
        }

        return checkPoints;
    }
}
