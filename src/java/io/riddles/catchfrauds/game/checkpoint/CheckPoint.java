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

package io.riddles.catchfrauds.game.checkpoint;

import io.riddles.catchfrauds.game.data.Record;

/**
 * io.riddles.catchfrauds.game.checkpoint.CheckPoint - Created on 3-6-16
 *
 * [description]
 *
 * @author jim
 */
public class CheckPoint {

    private int id;
    private String description;
    private int[] detectedFrauds;
    private int falsePositives;

    public CheckPoint(int id, String description, int fraudTypeCount) {
        this.id = id;
        this.description = description;
        this.detectedFrauds = new int[fraudTypeCount];
        this.falsePositives = 0;
    }

    public CheckPoint(CheckPoint checkPoint) {
        this.id = checkPoint.id;
        this.description = checkPoint.description;
        this.detectedFrauds = checkPoint.detectedFrauds.clone();
        this.falsePositives = checkPoint.falsePositives;
    }

    public int getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public void detectFraud(Record record) {
        int type = record.getFraudType();

        if (record.isFraudulent()) {
            this.detectedFrauds[type]++;
        } else {
            this.falsePositives++;
        }
    }

    public int[] getDetectedFrauds() {
        return this.detectedFrauds;
    }

    public int getFalsePositives() {
        return this.falsePositives;
    }
}
