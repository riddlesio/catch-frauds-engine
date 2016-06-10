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

package io.riddles.javainterface.game;

import org.json.JSONArray;
import org.json.JSONObject;

import io.riddles.javainterface.game.player.AbstractPlayer;
import io.riddles.javainterface.game.processor.AbstractProcessor;
import io.riddles.javainterface.game.state.AbstractState;

/**
 * io.riddles.javainterface.game.AbstractGameSerializer - Created on 8-6-16
 *
 * Used to transform the played game to a string that can be stored
 * and used in the game visualizer to show the played game
 *
 * @author jim
 */
public abstract class AbstractGameSerializer<P extends AbstractProcessor, S extends AbstractState> {

    /**
     * Should return the complete game in a json string
     * @param processor Processor that is used this game
     * @param initialState Initial state of the game
     * @return Complete game as a json string
     */
    public abstract String traverseToString(P processor, S initialState);

    /**
     * Method that can be used for (almost) every game type. Will add everything
     * to the output file that every visualizer needs
     * @param game JSONObject that stores the full game output
     * @param processor Processor that is used this game
     * @return Updated JSONObject with added stuff
     */
    protected JSONObject addDefaultJSON(JSONObject game, P processor) {

        // add default settings (player settings)
        JSONArray playerNames = new JSONArray();
        for (Object obj : processor.getPlayers()) {
            AbstractPlayer player = (AbstractPlayer) obj;
            playerNames.put(player.getName());
        }

        JSONObject players = new JSONObject();
        players.put("count", processor.getPlayers().size());
        players.put("names", playerNames);

        JSONObject settings = new JSONObject();
        settings.put("players", players);

        game.put("settings", settings);

        // add winner
        String winner = "null";
        if (processor.getWinner() != null) {
            winner = processor.getWinner().getId() + "";
        }
        game.put("winner", winner);

        // add score
        game.put("score", processor.getScore());

        return game;
    }
}
