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

package io.riddles.catchfrauds.engine

import io.riddles.javainterface.io.IOHandler
import spock.lang.Specification

/**
 * io.riddles.catchfrauds.engine.CatchFraudsEngineSpec - Created on 8-6-16
 *
 * [description]
 *
 * @author jim
 */
class CatchFraudsEngineSpec extends Specification {

    class TestEngine extends CatchFraudsEngine {

        TestEngine(String recordsFile, IOHandler ioHandler) {
            super(recordsFile);
            this.ioHandler = ioHandler;
        }

        TestEngine(String recordsFile, String wrapperFile, String[] botFiles) {
            super(recordsFile, wrapperFile, botFiles)
        }

        IOHandler getIOHandler() {
            return this.ioHandler;
        }

        void setup() {
            super.setup();
        }

        void finish() {
            super.finish();
        }
    }

    def recordsPath = "./test/adyentest.csv"
    def engine = new TestEngine(recordsPath, Mock(IOHandler));

    def "test engine initialization"() {

        setup:
        def file = new File(recordsPath).text
        def lines = file.split("\n")
        def format = lines[0].replace("state,", "")

        expect:
        engine.records.get(0).toBotFormat().equals(format)
        engine.records.size() == lines.length - 1
    }

    def "test engine setup"() {

        setup:
        engine.getIOHandler().getNextMessage() >>> ["initialize", "bot_ids 1", "start"]

        when:
        engine.setup()

        then:
        1 * engine.getIOHandler().sendMessage("ok")

        expect:
        engine.getPlayers().size() == 1
        engine.getPlayers().get(0).getId() == 1
    }


    def "Test running of full game with inputs from files"() {

        setup:
        String[] botInputs = new String[1]

        def recordsPath = "./test/adyentest.csv"
        def wrapperInput = "./test/wrapper_input.txt"
        botInputs[0] = "./test/bot_input.txt"

        def engine = new TestEngine(recordsPath, wrapperInput, botInputs)

        when:
        engine.run()

        then:
        1 * engine.finish()

        expect:
        def checkPointValues = engine.processor.checkPointValues
        checkPointValues.size() == 3
        checkPointValues.get(0) == "checkpoint 1 test lalala"
        checkPointValues.get(1) == "checkpoint 2 some other checkpoint"
        checkPointValues.get(2) == "checkpoint 3 jajaja"
    }
}
