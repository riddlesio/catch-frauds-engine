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

package io.riddles.catchfrauds.game;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import io.riddles.catchfrauds.engine.CatchFraudsEngine;
import io.riddles.catchfrauds.game.processor.CatchFraudsProcessor;
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
public class CatchFraudsGameSerializer extends
        AbstractGameSerializer<CatchFraudsProcessor, CatchFraudsState> {

    @Override
    public String traverseToString(CatchFraudsProcessor processor, CatchFraudsState initialState) {
        JSONObject game = new JSONObject();
        game = addDefaultJSON(game, processor);

        // add checkpoints
        JSONArray checkPoints = new JSONArray();
        for (String checkPointValue : processor.getCheckPointValues()) {
            JSONObject checkPoint = new JSONObject();
            checkPoint.put("description", checkPointValue);
            checkPoints.put(checkPoint);
        }
        ((JSONObject) game.get("settings")).put("checkpoints", checkPoints);

        // add all states
        JSONArray states = new JSONArray();
        CatchFraudsState state = initialState;
        CatchFraudsStateSerializer serializer = new CatchFraudsStateSerializer();
        while (state.hasNextState()) {
            state = (CatchFraudsState) state.getNextState();
            states.put(serializer.traverseToJson(state));
        }
        game.put("states", states);

        return game.toString();
    }
}
