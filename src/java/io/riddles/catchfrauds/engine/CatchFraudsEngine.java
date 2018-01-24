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

package io.riddles.catchfrauds.engine;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import io.riddles.catchfrauds.CatchFrauds;
import io.riddles.catchfrauds.game.CatchFraudsGameSerializer;
import io.riddles.catchfrauds.game.data.Record;
import io.riddles.catchfrauds.game.move.ActionType;
import io.riddles.catchfrauds.game.checkpoint.CheckPoint;
import io.riddles.catchfrauds.game.player.CatchFraudsPlayer;
import io.riddles.catchfrauds.game.state.CatchFraudsPlayerState;
import io.riddles.catchfrauds.game.state.CatchFraudsState;
import io.riddles.catchfrauds.game.processor.CatchFraudsProcessor;
import io.riddles.javainterface.configuration.Configuration;
import io.riddles.javainterface.engine.AbstractEngine;
import io.riddles.javainterface.engine.GameLoopInterface;
import io.riddles.javainterface.engine.SimpleGameLoop;
import io.riddles.javainterface.exception.TerminalException;
import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.javainterface.io.IOInterface;

/**
 * io.riddles.catchfrauds.engine.CatchFraudsEngine - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public class CatchFraudsEngine extends AbstractEngine<CatchFraudsProcessor, CatchFraudsPlayer, CatchFraudsState> {

    public static SecureRandom RANDOM;
    public static ArrayList<Record> RECORDS;
    public static HashMap<Integer, String> FRAUD_TYPES;

    public CatchFraudsEngine(PlayerProvider<CatchFraudsPlayer> playerProvider, IOInterface ioHandler) throws TerminalException {
        super(playerProvider, ioHandler);
    }

    @Override
    protected Configuration getDefaultConfiguration() {
        Configuration configuration = new Configuration();

        configuration.put("dataFile", "/data.csv");
        configuration.put("recordCount", -1);
        configuration.put("seed", UUID.randomUUID().toString());
        configuration.put("c1Value", 0.1);
        configuration.put("maxCheckpoints", 20);

        return configuration;
    }

    @Override
    protected CatchFraudsProcessor createProcessor() {
        readRecordsFile();

        return new CatchFraudsProcessor(this.playerProvider);
    }

    @Override
    protected GameLoopInterface createGameLoop() {
        return new SimpleGameLoop();
    }

    @Override
    protected CatchFraudsPlayer createPlayer(int id) {
        return new CatchFraudsPlayer(id);
    }

    @Override
    protected void sendSettingsToPlayer(CatchFraudsPlayer player) {
        player.sendSetting("max_checkpoints", configuration.getInt("maxCheckpoints"));
        player.sendSetting("record_format", RECORDS.get(0).toBotFormat());
    }

    @Override
    protected CatchFraudsState getInitialState() {
        setRandomSeed();

        ArrayList<CatchFraudsPlayerState> playerStates = new ArrayList<>();

        for (CatchFraudsPlayer player : this.playerProvider.getPlayers()) {
            CatchFraudsPlayerState playerState = new CatchFraudsPlayerState(player.getId());
            playerStates.add(playerState);
        }

        CatchFraudsState initialState = new CatchFraudsState(playerStates);

        requestCheckpointValues(initialState);

        return initialState;
    }

    @Override
    protected String getPlayedGame(CatchFraudsState initialState) {
        CatchFraudsGameSerializer serializer = new CatchFraudsGameSerializer();
        return serializer.traverseToString(this.processor, initialState);
    }

    private void readRecordsFile() {
        RECORDS = new ArrayList<>();
        FRAUD_TYPES = new HashMap<>();

        try {
            InputStream fileInputStream;
            String filePath = configuration.getString("dataFile");

            try {
                fileInputStream = new FileInputStream(filePath);
            } catch (FileNotFoundException ex) {
                fileInputStream = CatchFrauds.class.getResourceAsStream(filePath);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            String[] recordFormat = null;

            while ((line = br.readLine()) != null) {
                if (recordFormat == null) { // It's the first line, i.e. the record format
                    recordFormat = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                } else { // normal record
                    Record record = new Record(recordFormat, line);
                    addFraudType(record);

                    RECORDS.add(record);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
            System.exit(1);
        }
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

    private void requestCheckpointValues(CatchFraudsState initialState) {
        for (CatchFraudsPlayer player : this.playerProvider.getPlayers()) {
            String response = player.requestMove(ActionType.CHECKPOINTS);

            if (response == null || response.length() <= 0 || response.equals("null")) {
                player.sendWarning("No check points received. Bot is shut down.");
                initialState.setBotDisqualified();
                return;
            }

            String[] values = response.split(";");
            int maxCheckpoints = configuration.getInt("maxCheckpoints");

            if (values.length > maxCheckpoints) {
                player.sendWarning(String.format("Your bot cannot have more " +
                        "than %d check points. Bot is shut down.", maxCheckpoints));
                initialState.setBotDisqualified();
                return;
            }

            CatchFraudsPlayerState playerState = initialState.getPlayerStateById(player.getId());

            for (int id = 0; id < values.length; id++) {
                String description = values[id].trim();
                int fraudTypesCount = CatchFraudsEngine.FRAUD_TYPES.size();

                playerState.addCheckPoint(new CheckPoint(id, description, fraudTypesCount));
            }
        }
    }

    private void addFraudType(Record record) {
        int fraudType = record.getFraudType();
        String fraudDescription = record.getFraudDescription();

        if (!FRAUD_TYPES.containsKey(fraudType)) {
            FRAUD_TYPES.put(fraudType, fraudDescription);
        }
    }
}
