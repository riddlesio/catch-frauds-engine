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

package io.riddles.catchfrauds.game.move;

import java.util.ArrayList;
import java.util.Arrays;

import io.riddles.catchfrauds.game.player.CatchFraudsPlayer;
import io.riddles.javainterface.exception.InvalidMoveException;
import io.riddles.javainterface.serialize.Deserializer;

/**
 * io.riddles.catchfrauds.game.move.CatchFraudsMoveDeserializer - Created on 8-6-16
 *
 * [description]
 *
 * @author jim
 */
public class CatchFraudsMoveDeserializer implements Deserializer<CatchFraudsMove> {

    private CatchFraudsPlayer player;
    private int checkPointCount;

    public CatchFraudsMoveDeserializer(CatchFraudsPlayer player, int checkPointCount) {
        this.player = player;
        this.checkPointCount = checkPointCount;
    }

    @Override
    public CatchFraudsMove traverse(String string) {
        try {
            return visitMove(string);
        } catch (InvalidMoveException ex) {
            return new CatchFraudsMove(this.player, ex);
        } catch (Exception ex) {
            return new CatchFraudsMove(
                    this.player, new InvalidMoveException("Failed to parse move"));
        }
    }

    private CatchFraudsMove visitMove(String input) throws InvalidMoveException {
        String[] split = input.split(" ");

        boolean isRefused = visitAssessment(split[0]);

        String checkPointInput = null;
        if (split.length > 1) {
            checkPointInput = split[1];
        }
        CheckPoint[] checkPoints = visitCheckPoints(checkPointInput);

        return new CatchFraudsMove(this.player, isRefused, checkPoints);
    }

    private boolean visitAssessment(String input) throws InvalidMoveException {
        switch (input) {
            case "refused":
                return true;
            case "authorized":
                return false;
            default:
                throw new InvalidMoveException("Move does not contain authorized or refused");
        }
    }

    private CheckPoint[] visitCheckPoints(String input) throws InvalidMoveException {
        CheckPoint[] checkPoints = new CheckPoint[this.checkPointCount];

        if (input == null) {
            for (int i = 0; i < checkPoints.length; i++) {
                checkPoints[i] = new CheckPoint(true);
            }
            return checkPoints;
        }

        String[] split = input.split(",");
        ArrayList<Integer> indexes = new ArrayList<>();
        for (String index : split) {
            try {
                indexes.add(Integer.parseInt(index));
            } catch (Exception ex) {
                throw new InvalidMoveException("Can't parse failed checkpoints");
            }
        }

        for (int i = 0; i < checkPoints.length; i++) {
            if (indexes.contains(i + 1)) {
                checkPoints[i] = new CheckPoint(false);
            } else {
                checkPoints[i] = new CheckPoint(true);
            }
        }

        return checkPoints;
    }
}
