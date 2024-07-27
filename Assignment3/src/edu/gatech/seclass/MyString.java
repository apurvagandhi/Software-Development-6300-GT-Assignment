package edu.gatech.seclass;

public class MyString implements MyStringInterface {

    private String myString = null;

    @Override
    public String getString() {
        return myString;
    }

    @Override
    public void setString(String string) {
        if (string.equals(easterEgg) || string.isEmpty() || !containsLetterOrDigit(string)) {
            throw new IllegalArgumentException("Invalid Argument");
        }
        myString = string;
    }

    // Helper Function
    private static boolean containsLetterOrDigit(String string) {
        for (char c : string.toCharArray()) {
            if (Character.isLetter(c) || Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int countAlphabeticWords() {
        if (myString == null) {
            throw new NullPointerException("The input string is null");
        }

        int count = 0;
        boolean inWord = false;

        for (int i = 0; i < myString.length(); i++) {
            char c = myString.charAt(i);
            if (Character.isLetter(c)) {
                if (!inWord) {
                    inWord = true; // Starting a new word
                    count++;
                }
            } else {
                inWord = false; // Ending a current word
            }
        }

        return count;
    }

    @Override
    public String encrypt(int a, int b) {
        if (a <= 0 || a >= 62 || gcd(a, 62) != 1) {
            throw new IllegalArgumentException("arg1 should be an integer co-prime to 62 between 0 and 62");
        }
        if (b < 1 || b >= 62) {
            throw new IllegalArgumentException("arg2 should be an integer >= 1 and < 62");
        }

        StringBuilder encrypted = new StringBuilder();
        String alphabet = "0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";

        for (char c : myString.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                int x = alphabet.indexOf(c);
                int encodedValue = (x * a + b) % 62;
                encrypted.append(alphabet.charAt(encodedValue));
            } else {
                encrypted.append(c);
            }
        }

        return encrypted.toString();
    }

    private int gcd(int a, int b) {
        if (b == 0) {
            return a;
        } else {
            return gcd(b, a % b);
        }
    }

    @Override
    public void convertDigitsToNamesInSubstring(int firstPosition, int finalPosition) {
        if (myString == null) {
            throw new NullPointerException("Input string cannot be null");
        }

        if (firstPosition < 1 || firstPosition > finalPosition) {
            throw new IllegalArgumentException("Invalid positions");
        }

        if (finalPosition > myString.length()) {
            throw new MyIndexOutOfBoundsException("Final position is out of bounds");
        }

        StringBuilder result = new StringBuilder(myString);

        for (int i = firstPosition - 1; i < finalPosition; i++) {
            char c = result.charAt(i);
            if (Character.isDigit(c)) {
                String digitName = getDigitName(c);
                result.replace(i, i + 1, digitName);
                int adjustment = digitName.length() - 1;
                finalPosition += adjustment;
                i += adjustment;
            }
        }

        myString = result.toString();
    }

    private String getDigitName(char digit) {
        switch (digit) {
            case '0':
                return "Zero";
            case '1':
                return "One";
            case '2':
                return "Two";
            case '3':
                return "Three";
            case '4':
                return "Four";
            case '5':
                return "Five";
            case '6':
                return "Six";
            case '7':
                return "Seven";
            case '8':
                return "Eight";
            case '9':
                return "Nine";
            default:
                return "";
        }
    }
}