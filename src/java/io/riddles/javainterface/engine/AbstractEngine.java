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

package io.riddles.javainterface.engine;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.riddles.javainterface.game.processor.AbstractProcessor;
import io.riddles.javainterface.game.player.AbstractPlayer;
import io.riddles.javainterface.game.state.AbstractState;
import io.riddles.javainterface.io.IOHandler;

/**
 * io.riddles.javainterface.engine.AbstractEngine - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public abstract class AbstractEngine<Pr extends AbstractProcessor,
        Pl extends AbstractPlayer, S extends AbstractState> {

    protected final static Logger LOGGER = Logger.getLogger(AbstractEngine.class.getName());

    private String[] botInputFiles;

    protected IOHandler ioHandler;
    protected ArrayList<Pl> players;

    // Can be overridden in subclass constructor
    protected GameLoop gameLoop;
    protected Pr processor;

    protected AbstractEngine() {
        this.players = new ArrayList<>();
        this.gameLoop = new SimpleGameLoop();
        this.ioHandler = new IOHandler();
    }

    /**
     * Initializes the engine in debug mode
     * @param wrapperInputFile Input file from the wrapper
     * @param botInputFiles Input files for the bots
     */
    protected AbstractEngine(String wrapperInputFile, String[] botInputFiles) {
        this.players = new ArrayList<>();
        this.gameLoop = new SimpleGameLoop();
        this.ioHandler = new IOHandler(wrapperInputFile);
        this.botInputFiles = botInputFiles;
    }

    public void run() {
        LOGGER.info("Starting...");

        setup();

        if (this.processor == null) {
            throw new NullPointerException("Processor has not been set");
        }

        LOGGER.info("Running pre-game phase...");

        this.processor.preGamePhase();

        LOGGER.info("Starting game loop...");

        S initialState = getInitialState();
        this.gameLoop.run(initialState, this.processor);

        finish(initialState);
    }

    protected void setup() {
        LOGGER.info("Setting up engine. Waiting for initialize...");

        this.ioHandler.waitForMessage("initialize");
        this.ioHandler.sendMessage("ok");

        LOGGER.info("Got initialize. Parsing settings...");

        try {
            String line = "";
            while (!line.equals("start")) { // from "start", setup is done
                line = this.ioHandler.getNextMessage();
                parseSetupInput(line);
            }
        } catch(IOException ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
        }

        this.processor = createProcessor();

        LOGGER.info("Got start. Sending game settings to bots...");

        this.players.forEach(this::sendGameSettings);

        LOGGER.info("Settings sent. Setting up engine done...");
    }

    protected void finish(S initialState) {

        // let the wrapper know the game has ended
        this.ioHandler.sendMessage("end");

        // send game details
        this.ioHandler.waitForMessage("details");

        AbstractPlayer winner = this.processor.getWinner();
        String winnerId = "null";
        if (winner != null) {
            winnerId = winner.getId() + "";
        }

        JSONObject details = new JSONObject();
        details.put("winner", winnerId);
        details.put("score", this.processor.getScore());

        this.ioHandler.sendMessage(details.toString());

        // send the game file
        this.ioHandler.waitForMessage("game");
        this.ioHandler.sendMessage(getPlayedGame(initialState));

        System.exit(0);
    }

    /**
     * Parses everything the engine wrapper API sends
     * we need to start the engine, like IDs of the bots
     * @param input Input from engine wrapper
     */
    protected void parseSetupInput(String input) {
        String[] split = input.split(" ");

        if (split[0].equals("bot_ids")) {
            String[] ids = split[1].split(",");
            for (int i = 0; i < ids.length; i++) {
                Pl player = createPlayer(Integer.parseInt(ids[i]));

                if (this.botInputFiles != null)
                    player.setInputFile(this.botInputFiles[i]);

                this.players.add(player);
            }
        }
    }

    protected abstract S getInitialState();

    protected abstract Pl createPlayer(int id);

    protected abstract Pr createProcessor();

    protected abstract void sendGameSettings(Pl player);

    protected abstract String getPlayedGame(S initialState);

    public ArrayList<Pl> getPlayers() {
        return this.players;
    }

    public Pr getProcessor() {
        return this.processor;
    }
}
