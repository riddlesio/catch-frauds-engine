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

package io.riddles.catchfrauds.game.move;

import io.riddles.javainterface.exception.InvalidInputException;
import io.riddles.javainterface.game.move.AbstractMove;

/**
 * io.riddles.catchfrauds.game.move.CatchFraudsMove - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public class CatchFraudsMove extends AbstractMove {

    private boolean isRefused;
    private Integer checkPointId;

    public CatchFraudsMove() {
        this.isRefused = false;
        this.checkPointId = null;
    }

    public CatchFraudsMove(int checkPointId) {
        this.isRefused = true;
        this.checkPointId = checkPointId;
    }

    public CatchFraudsMove(InvalidInputException exception) {
        super(exception);
    }

    public boolean isRefused() {
        return this.isRefused;
    }

    public Integer getCheckPointId() {
        return this.checkPointId;
    }
}
