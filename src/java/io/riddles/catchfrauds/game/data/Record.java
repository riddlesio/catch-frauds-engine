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

package io.riddles.catchfrauds.game.data;

import java.util.LinkedHashMap;

/**
 * io.riddles.catchfrauds.game.data.Record - Created on 8-6-16
 *
 * [description]
 *
 * @author jim
 */
public class Record {

    private final String FRAUD_COLUMN = "fraud";
    private final String FRAUD_TYPE_COLUMN = "fraud_type";
    private final String FRAUD_DESCRIPTION_COLUMN = "fraud_description";
    private final int NON_FRAUDULENT_VALUE = 0;

    private LinkedHashMap<String, String> columns;
    private int fraudType;
    private String fraudDescription;

    public Record(String[] format, String input) {
        this.columns = new LinkedHashMap<>();
        String[] values = input.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

        if (format.length != values.length) {
            throw new InstantiationError("Record input does not fit record format");
        }

        for (int i = 0; i < format.length; i++) {
            String key = format[i];
            String value = values[i];

            this.columns.put(key, value);

            switch (key) {
                case FRAUD_TYPE_COLUMN:
                    this.fraudType = Integer.parseInt(value);
                    break;
                case FRAUD_DESCRIPTION_COLUMN:
                    this.fraudDescription = value;
                    break;
            }
        }
    }

    public String toString() {
        return toString(this.columns);
    }

    public String toBotString() {
        return toString(getColumnsWithoutState());
    }

    public String toBotFormat() {
        return String.join(",", getColumnsWithoutState().keySet());
    }

    public boolean isFraudulent() {
        return this.fraudType != NON_FRAUDULENT_VALUE;
    }

    public int getFraudType() {
        return this.fraudType;
    }

    public String getFraudDescription() {
        return this.fraudDescription;
    }

    private String toString(LinkedHashMap<String, String> columns) {
        return String.join(",", columns.values());
    }

    private LinkedHashMap<String, String> getColumnsWithoutState() {
        LinkedHashMap<String, String> columnsClone = new LinkedHashMap<>(this.columns);

        columnsClone.remove(FRAUD_COLUMN);
        columnsClone.remove(FRAUD_TYPE_COLUMN);
        columnsClone.remove(FRAUD_DESCRIPTION_COLUMN);

        return columnsClone;
    }
}
