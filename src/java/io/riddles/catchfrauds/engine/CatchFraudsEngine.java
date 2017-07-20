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

package io.riddles.catchfrauds.engine;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

import io.riddles.catchfrauds.CatchFrauds;
import io.riddles.catchfrauds.game.CatchFraudsGameSerializer;
import io.riddles.catchfrauds.game.data.Record;
import io.riddles.catchfrauds.game.player.CatchFraudsPlayer;
import io.riddles.catchfrauds.game.state.CatchFraudsState;
import io.riddles.catchfrauds.game.processor.CatchFraudsProcessor;
import io.riddles.javainterface.engine.AbstractEngine;
import io.riddles.javainterface.exception.ConfigurationException;

/**
 * io.riddles.catchfrauds.engine.CatchFraudsEngine - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public class CatchFraudsEngine extends AbstractEngine<CatchFraudsProcessor,
        CatchFraudsPlayer, CatchFraudsState> {

    public static SecureRandom RANDOM;

    private final String DEFAULT_DATA_FILE = "/data.csv";
    private final int MAX_CHECKPOINTS = 20;
    private ArrayList<Record> records;

    public CatchFraudsEngine() {
        configuration.put("recordCount", -1);
        configuration.put("seed", UUID.randomUUID().toString());
    }

    @Override
    protected void setup() {
        super.setup();
        setRandomSeed();

        this.records = readRecordsFile();
    }

    @Override
    protected CatchFraudsState getInitialState() {
        return new CatchFraudsState();
    }

    @Override
    protected CatchFraudsPlayer createPlayer(int id) {
        return new CatchFraudsPlayer(id);
    }

    @Override
    protected CatchFraudsProcessor createProcessor() {
        return new CatchFraudsProcessor(this.players, this.records, MAX_CHECKPOINTS);
    }

    @Override
    protected void sendGameSettings(CatchFraudsPlayer player) {
        player.sendSetting("max_checkpoints", MAX_CHECKPOINTS);
        player.sendSetting("record_format", this.records.get(0).toBotFormat());
    }

    @Override
    protected String getPlayedGame(CatchFraudsState initialState) {
        CatchFraudsGameSerializer serializer = new CatchFraudsGameSerializer();
        return serializer.traverseToString(this.processor, initialState);
    }

    private ArrayList<Record> readRecordsFile() {
        ArrayList<Record> records = new ArrayList<>();

        try {
            InputStream fileInputStream;

            try {
                String filePath = configuration.getString("dataFile");
                fileInputStream = new FileInputStream(filePath);
            } catch (ConfigurationException ex) {
                fileInputStream = CatchFrauds.class.getResourceAsStream(DEFAULT_DATA_FILE);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            String[] recordFormat = null;

            while ((line = br.readLine()) != null) {
                if (recordFormat == null) { // It's the first line, i.e. the record format
                    recordFormat = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                } else { // normal record
                    records.add(new Record(recordFormat, line));
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
            System.exit(1);
        }

        return records;
    }

    private void setRandomSeed() {
        try {
            RANDOM = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.severe("Not able to use SHA1PRNG, using default algorithm");
            RANDOM = new SecureRandom();
        }
        String seed = configuration.getString("seed");
        LOGGER.info("RANDOM SEED IS: " + seed);
        RANDOM.setSeed(seed.getBytes());
    }
}
