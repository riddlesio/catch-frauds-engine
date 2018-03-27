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

import java.util.ArrayList;

import io.riddles.javainterface.exception.InvalidInputException;
import io.riddles.javainterface.serialize.Deserializer;

/**
 * io.riddles.catchfrauds.game.move.CatchFraudsMoveDeserializer - Created on 8-6-16
 *
 * [description]
 *
 * @author jim
 */
public class CatchFraudsMoveDeserializer implements Deserializer<CatchFraudsMove> {

    @Override
    public CatchFraudsMove traverse(String string) {
        return null;
    }

    public CatchFraudsMove traverse(String string, int checkPointCount) {
        try {
            return visitMove(string, checkPointCount);
        } catch (InvalidInputException ex) {
            return new CatchFraudsMove(ex);
        } catch (Exception ex) {
            return new CatchFraudsMove(new InvalidInputException("Failed to parse move"));
        }
    }

    private CatchFraudsMove visitMove(String input, int checkPointCount) throws InvalidInputException {
        String[] split = input.split(" ");

        boolean isRefused = visitAssessment(split[0]);

        if (!isRefused) {
            return new CatchFraudsMove();
        }

        if (split.length != 2) {
            throw new InvalidInputException(
                    "The checkpoints that the assessment failed on are not given"
            );
        }

        return new CatchFraudsMove(visitCheckPoints(split[1], checkPointCount));
    }

    private boolean visitAssessment(String input) throws InvalidInputException {
        switch (input) {
            case "rejected":
                return true;
            case "authorized":
                return false;
            default:
                throw new InvalidInputException("Move does not contain authorized or rejected");
        }
    }

    private ArrayList<Integer> visitCheckPoints(String input, int checkPointCount) throws InvalidInputException {
        ArrayList<Integer> checkPointIds = new ArrayList<>();
        String[] split = input.split(",");

        for (String id : split) {
            int checkPointId;

            try {
                checkPointId = Integer.parseInt(id);
            } catch (Exception ex) {
                throw new InvalidInputException(String.format("Can't parse checkpoint '%s'", id));
            }

            if (checkPointId >= checkPointCount) {
                throw new InvalidInputException(String.format("Checkpoint id '%s' not found", id));
            }

            checkPointIds.add(checkPointId);
        }

        return checkPointIds;
    }
}
