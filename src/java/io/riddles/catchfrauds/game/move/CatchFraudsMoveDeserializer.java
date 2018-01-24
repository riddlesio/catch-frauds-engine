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
            throw new InvalidInputException("The checkpoint that the " +
                    "assessment failed on is not given");
        }

        return new CatchFraudsMove(visitCheckPoint(split[1], checkPointCount));
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

    private int visitCheckPoint(String input, int checkPointCount) throws InvalidInputException {
        int checkPointId;

        try {
            checkPointId = Integer.parseInt(input);
        } catch (Exception ex) {
            throw new InvalidInputException("Can't parse the checkpoint that the " +
                    "assessment failed on");
        }

        if (checkPointId >= checkPointCount) {
            throw new InvalidInputException("Given checkpoint ID not found");
        }

        return checkPointId;
    }
}
