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

package io.riddles.catchfrauds;

import io.riddles.catchfrauds.engine.CatchFraudsEngine;
import io.riddles.catchfrauds.game.state.CatchFraudsState;
import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.javainterface.io.IOHandler;

/**
 * io.riddles.catchfrauds.CatchFrauds - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public class CatchFrauds {

    public static void main(String[] args) throws Exception {
        CatchFraudsEngine engine = new CatchFraudsEngine(new PlayerProvider<>(), new IOHandler());

        CatchFraudsState firstState = engine.willRun();
        CatchFraudsState finalState = engine.run(firstState);

        engine.didRun(firstState, finalState);
    }
}