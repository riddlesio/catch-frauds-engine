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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.riddles.catchfrauds.game.move.CatchFraudsMove;
import io.riddles.javainterface.game.state.AbstractStateSerializer;

/**
 * io.riddles.catchfrauds.game.state.CatchFraudsStateSerializer - Created on 3-6-16
 *
 * [description]
 *
 * @author jim
 */
public class CatchFraudsStateSerializer extends AbstractStateSerializer<CatchFraudsState> {

    @Override
    public String traverseToString(CatchFraudsState state) {
        return visitState(state).toString();
    }

    @Override
    public JSONObject traverseToJson(CatchFraudsState state) throws NullPointerException {
        return visitState(state);
    }

    private JSONObject visitState(CatchFraudsState state) throws NullPointerException {
        JSONObject stateJson = new JSONObject();
        CatchFraudsPlayerState playerState = state.getPlayerStates().get(0);
        CatchFraudsMove move = playerState.getMove();

        stateJson.put("round", state.getRoundNumber());
        stateJson.put("isFraudulent", state.isFraudulent());
        if (move.getException() == null) {
            stateJson.put("isRefused", move.isRefused());
            stateJson.put("blockingCheckPointIds", visitCheckPointIds(move.getCheckPointIds()));
            stateJson.put("exception", JSONObject.NULL);
        } else {
            stateJson.put("isRefused", JSONObject.NULL);
            stateJson.put("isCheckpointApproved", JSONObject.NULL);
            stateJson.put("exception", move.getException().getMessage());
        }

        return stateJson;
    }

    private JSONArray visitCheckPointIds(ArrayList<Integer> checkPointIds) {
        JSONArray checkPointList = new JSONArray();

        for (Integer checkPointId : checkPointIds) {
            checkPointList.put(checkPointId);
        }

        return checkPointList;
    }
}
