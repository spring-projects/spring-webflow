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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Action that encapsulates logic for the number guess sample flow.
 * 
 * @author Keri Donald
 * @author Keith Donald
 */
public class MastermindGame implements Serializable {

	private GameData data = new GameData();

	public GameData getData() {
		return data;
	}

	public Collection getGuessHistory() {
		return data.getGuessHistory();
	}

	public GuessResult getResult() {
		return data.getLastGuessResult();
	}

	public void setResult(GuessResult result) {
		data.setLastGuessResult(result);
	}

	public GuessResult makeGuess(String guess) {
		if (isGuessValid(guess)) {
			setResult(calculateResult(guess));
		}
		else {
			setResult(GuessResult.INVALID);
		}
		return getResult();
	}

	private boolean isGuessValid(String guess) {
		if (guess == null || guess.length() != 4) {
			return false;
		}
		for (int i = 0; i < 4; i++) {
			if (!Character.isDigit(guess.charAt(i))) {
				return false;
			}
			int digit = Character.getNumericValue(guess.charAt(i));
			for (int j = 0; j < i; j++) {
				if (digit == Character.getNumericValue(guess.charAt(j))) {
					return false;
				}
			}
		}
		return true;
	}

	private GuessResult calculateResult(String guess) {
		int rightPosition = 0;
		int correctButWrongPosition = 0;
		for (int i = 0; i < guess.length(); i++) {
			char digit = guess.charAt(i);
			for (int j = 0; j < data.answer.length(); j++) {
				char answerDigit = data.answer.charAt(j);
				if (digit == answerDigit) {
					if (i == j) {
						rightPosition++;
					}
					else {
						correctButWrongPosition++;
					}
					break;
				}
			}
		}
		data.recordGuessData(guess, rightPosition, correctButWrongPosition);
		if (rightPosition == 4) {
			return GuessResult.CORRECT;
		}
		else {
			return GuessResult.WRONG;
		}
	}

	/**
	 * Simple data holder for number guess info.
	 */
	public static class GameData implements Serializable {

		private static Random random = new Random();

		private Calendar start = Calendar.getInstance();

		private String answer;

		private List<GuessData> guessHistory = new ArrayList<GuessData>();

		private GuessResult lastGuessResult;

		// property accessors for JSTL EL

		public GameData() {
			this.answer = createAnswer();
		}

		public int getGuesses() {
			return guessHistory.size();
		}

		public GuessResult getLastGuessResult() {
			return lastGuessResult;
		}

		public void setLastGuessResult(GuessResult lastGuessResult) {
			this.lastGuessResult = lastGuessResult;
		}

		public String getAnswer() {
			return answer;
		}

		public long getDuration() {
			Calendar now = Calendar.getInstance();
			long durationMilliseconds = now.getTime().getTime() - start.getTime().getTime();
			return durationMilliseconds / 1000;
		}

		public Collection<GuessData> getGuessHistory() {
			return guessHistory;
		}

		public GuessData getLastGuessData() {
			if (guessHistory.isEmpty()) {
				return null;
			}
			return guessHistory.get(guessHistory.size() - 1);
		}

		public void recordGuessData(String guess, int rightPosition, int correctButWrongPosition) {
			guessHistory.add(new GuessData(guess, rightPosition, correctButWrongPosition));
		}

		public String createAnswer() {
			StringBuffer buffer = new StringBuffer(4);
			for (int i = 0; i < 4; i++) {
				int digit = random.nextInt(10);
				for (int j = 0; j < i; j++) {
					if (digit == Character.getNumericValue(buffer.charAt(j))) {
						j = -1;
						digit = random.nextInt(10);
					}
				}
				buffer.append(digit);
			}
			return buffer.toString();
		}

		public class GuessData implements Serializable {
			private String guess;

			private int rightPosition;

			private int correctButWrongPosition;

			public GuessData(String guess, int rightPosition, int correctButWrongPosition) {
				this.guess = guess;
				this.rightPosition = rightPosition;
				this.correctButWrongPosition = correctButWrongPosition;
			}

			public int getCorrectButWrongPosition() {
				return correctButWrongPosition;
			}

			public String getGuess() {
				return guess;
			}

			public int getRightPosition() {
				return rightPosition;
			}
		}
	}

	enum GuessResult {
		WRONG, CORRECT, INVALID
	}
}