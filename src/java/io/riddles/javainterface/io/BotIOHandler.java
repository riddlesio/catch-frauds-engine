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

package io.riddles.javainterface.io;

import java.io.IOException;
import java.util.logging.Level;

/**
 * io.riddles.javainterface.io.BotIOHandler - Created on 9-6-16
 *
 * [description]
 *
 * @author jim
 */
public class BotIOHandler extends IOHandler {

    private int botId;

    public BotIOHandler(int botId) {
        super();
        this.botId = botId;
    }

    public BotIOHandler(int botId, String inputFile) {
        super(inputFile);
        this.botId = botId;
    }

    /**
     * Send a message to bot, expecting no response
     * @param message Message to send
     */
    public void sendMessage(String message) {
        super.sendMessage(String.format("bot %d send %s", this.botId, message));
    }

    /**
     * Send a message that will be received by all bots,
     * no response from bots is expected
     * @param message Message to send
     */
    public void broadcastMessage(String message) {
        super.sendMessage(String.format("bot all send %s", message));
    }

    /**
     * Ask the bot something and wait for it to return with
     * an answer
     * @param request Request to send
     * @return Answer from the bot
     */
    public String sendRequest(String request) {
        super.sendMessage(String.format("bot %d ask %s", this.botId, request));
        return getResponse();
    }

    /**
     * Send a warning to bot, this will not be received
     * by the bot, but will be logged in its dump
     * @param warning Warning to send
     */
    public void sendWarning(String warning) {
        super.sendMessage(String.format("bot %d warning %s", this.botId, warning));
    }

    /**
     * Waits until a response is returned by bot, only call
     * this after sending a request.
     * All messages received while waiting for response
     * from given bot are ignored.
     */
    private String getResponse() {
        String message = null;
        String identifier = String.format("bot %d ", this.botId);

        if (this.reader != null) {
            identifier = "";
        }

        while (message == null || !message.startsWith(identifier)) {
            try {
                message = getNextMessage();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, ex.toString(), ex);
                break;
            }
        }

        if (message == null)
            return null;

        return message.replace(identifier, "");
    }
}
