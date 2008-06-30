package org.springframework.binding.convert.converters;

public class StringToCharacter extends StringToObject {

	public StringToCharacter() {
		super(Character.class);
	}

	protected Object toObject(String string, Class targetClass) throws Exception {
		return new Character(string.charAt(0));
	}

	protected String toString(Object object) throws Exception {
		Character character = (Character) object;
		return character.toString();
	}

}
