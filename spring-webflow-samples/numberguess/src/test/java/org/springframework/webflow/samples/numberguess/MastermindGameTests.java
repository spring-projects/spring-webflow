/*
 * Copyright 2004-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.samples.numberguess;

import junit.framework.TestCase;

import org.springframework.webflow.samples.numberguess.MastermindGame.GameData;
import org.springframework.webflow.samples.numberguess.MastermindGame.GuessResult;

public class MastermindGameTests extends TestCase {

	private MastermindGame action = new MastermindGame();
	
	protected void setUp() {
		action = new MastermindGame();
	}
	
	public void testGuessNoInputProvided() throws Exception {
		GuessResult result = action.makeGuess(null);
		assertEquals(GuessResult.INVALID, result);
	}

	public void testGuessInputInvalidLength() throws Exception {
		GuessResult result = action.makeGuess("123");
		assertEquals(GuessResult.INVALID, result);
	}

	public void testGuessInputNotAllDigits() throws Exception {
		GuessResult result = action.makeGuess("12AB");
		assertEquals(GuessResult.INVALID, result);
	}

	public void testGuessInputNotUniqueDigits() throws Exception {
		GuessResult result = action.makeGuess("1111");
		assertEquals(GuessResult.INVALID, result);
	}

	public void testGuessRetry() throws Exception {
		GuessResult result = action.makeGuess("1234");
		assertEquals(GuessResult.WRONG, result);
	}

	public void testGuessCorrect() throws Exception {
		GuessResult result = action.makeGuess(null);
		assertEquals(GuessResult.INVALID, result);
		GameData data = action.getData();
		result = action.makeGuess(data.getAnswer());
		assertEquals(GuessResult.CORRECT, result);
	}
}