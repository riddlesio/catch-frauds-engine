/*
 *  Copyright 2018 riddles.io (developers@riddles.io)
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 *      For the full copyright and license information, please view the LICENSE
 *      file that was distributed with this source code.
 */

package io.riddles.catchfrauds.game.state;

import java.util.ArrayList;
import java.util.stream.Collectors;

import io.riddles.catchfrauds.game.data.Record;
import io.riddles.catchfrauds.game.move.CatchFraudsMove;
import io.riddles.catchfrauds.game.checkpoint.CheckPoint;
import io.riddles.javainterface.game.state.AbstractPlayerState;

/**
 * io.riddles.catchfrauds.game.state.CatchFraudsPlayerState - Created on 18-1-18
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class CatchFraudsPlayerState extends AbstractPlayerState<CatchFraudsMove> {

    private double detectedFrauds;
    private double falsePositives;
    private ArrayList<CheckPoint> checkPoints;

    public CatchFraudsPlayerState(int playerId) {
        super(playerId);
        this.detectedFrauds = 0.0;
        this.falsePositives = 0.0;
        this.checkPoints = new ArrayList<>();
    }

    public CatchFraudsPlayerState(CatchFraudsPlayerState playerState) {
        super(playerState.getPlayerId());
        this.detectedFrauds = playerState.getDetectedFrauds();

        this.checkPoints = playerState.checkPoints.stream()
                .map(CheckPoint::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void detectFraud(Record record, int checkPointId) {
        if (record.isFraudulent()) {
            this.detectedFrauds++;
        } else {
            this.falsePositives++;
        }

        this.checkPoints.get(checkPointId).detectFraud(record);
    }

    public double getDetectedFrauds() {
        return this.detectedFrauds;
    }

    public double getFalsePositives() {
        return this.falsePositives;
    }

    public void addCheckPoint(CheckPoint checkPoint) {
        this.checkPoints.add(checkPoint);
    }

    public ArrayList<CheckPoint> getCheckPoints() {
        return this.checkPoints;
    }
}
