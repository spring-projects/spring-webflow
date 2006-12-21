/*
 * Copyright 2002-2006 the original author or authors.
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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;

/**
 * Action that encapsulates logic for the number guess sample flow. Note that
 * this is a stateful action: it holds modifiable state in instance members!
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 */
public class HigherLowerGame implements Serializable {

	private static final Random random = new Random();

	private Calendar start = Calendar.getInstance();

	private int answer = random.nextInt(101);

	private int guesses = 0;

	private GuessResult result;

	public int getAnswer() {
		return answer;
	}

	public int getGuesses() {
		return guesses;
	}

	public GuessResult getResult() {
		return result;
	}

	public void setResult(GuessResult result) {
		this.result = result;
	}

	public long getDuration() {
		Calendar now = Calendar.getInstance();
		long durationMilliseconds = now.getTime().getTime() - start.getTime().getTime();
		return durationMilliseconds / 1000;
	}

	public GuessResult makeGuess(int guess) {
		if (guess < 0 || guess > 100) {
			setResult(GuessResult.INVALID);
		}
		else {
			guesses++;
			if (answer < guess) {
				setResult(GuessResult.TOO_HIGH);
			}
			else if (answer > guess) {
				setResult(GuessResult.TOO_LOW);
			}
			else {
				setResult(GuessResult.CORRECT);
			}
		}
		return getResult();
	}

	enum GuessResult {
		TOO_HIGH, TOO_LOW, CORRECT, INVALID
	}
}