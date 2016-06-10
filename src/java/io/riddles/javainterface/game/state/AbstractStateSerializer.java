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

package io.riddles.javainterface.game.state;

import org.json.JSONObject;

import io.riddles.javainterface.serialize.Serializer;

/**
 * io.riddles.javainterface.engine.state.AbstractStateSerializer - Created on 3-6-16
 *
 * [description]
 *
 * @author jim
 */
public abstract class AbstractStateSerializer<S extends AbstractState> implements Serializer<S> {

    public abstract String traverseToString(S state);

    public abstract JSONObject traverseToJson(S state);
}
