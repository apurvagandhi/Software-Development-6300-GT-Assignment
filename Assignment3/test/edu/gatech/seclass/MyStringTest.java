package edu.gatech.seclass;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Timeout.ThreadMode;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Junit test class created for use in Georgia Tech CS6300.
 * <p>
 * This class is provided to interpret your grades via junit tests
 * and as a reminder, should NOT be posted in any public repositories,
 * even after the class has ended.
 */

@Timeout(value = 1, unit = TimeUnit.SECONDS, threadMode = ThreadMode.SEPARATE_THREAD)
public class MyStringTest {

    private MyStringInterface myString;

    @BeforeEach
    public void setUp() {
        myString = new MyString();
    }

    @AfterEach
    public void tearDown() {
        myString = null;
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: First count number example in the interface documentation
    public void testCountAlphabeticWords1() {
        myString.setString("My numbers are 11, 96, and thirteen");
        assertEquals(5, myString.countAlphabeticWords());
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: This test verifies that the countAlphabeticWords method correctly counts the number of alphabetic words in a string.
    public void testCountAlphabeticWords2() {
        assertThrows(NullPointerException.class, () -> myString.countAlphabeticWords());
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: Test the countAlphabeticWords method with a string containing multiple words and spaces.
    public void testCountAlphabeticWords3() {
        myString.setString("Hello,   world! This is a test... string.");
        assertEquals(7, myString.countAlphabeticWords());
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: Test the countAlphabeticWords method with a string containing a
    // single word.
    public void testCountAlphabeticWords4() {
        myString.setString("Hello");
        assertEquals(1, myString.countAlphabeticWords());
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: Test the setString method with an empty string as input
    public void testSetString1() {
        assertThrows(IllegalArgumentException.class, () -> myString.setString(""));
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: Sample encryption 1
    public void testEncrypt1() {
        myString.setString("Cat & 5 DogS");
        assertEquals("aY0 & J fBXs", myString.encrypt(5, 3));
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: Test the encrypt method with a string containing a single word.
    public void testEncrypt2() {
        myString.setString("World");
        String encrypted = myString.encrypt(3, 3);
        assertEquals("pYCPW", encrypted);
    }

    @Test
    // Description: Test the encrypt method with a string containing numbers, uppercase letters, and lowercase letters.
    public void testEncrypt3() {
        myString.setString("123ABCabc");
        String encrypted = myString.encrypt(3, 4);
        assertEquals("7AbMPSnqt", encrypted);
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: Test the encrypt method with a string containing a single word and encrypting with a shift of 1.
    public void testEncrypt4() {
        myString.setString("Hello");
        String encrypted = myString.encrypt(1, 1);
        assertEquals("hFMMP", encrypted);
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: Test the encrypt method with null input and expect a NullPointerException to be thrown.
    public void testEncrypt5() {
        assertThrows(NullPointerException.class, () -> myString.encrypt(1, 1));
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: Test the encrypt method with a string containing punctuation marks and expect them to be preserved.
    public void testEncrypt6() {
        myString.setString("Hello, World!");
        String encrypted = myString.encrypt(1, 1);
        assertEquals("hFMMP, wPSME!", encrypted);
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: First convert digits example in the interface documentation
    public void testConvertDigitsToNamesInSubstring1() {
        myString.setString("I'd b3tt3r put s0me d161ts in this 5tr1n6, right?");
        myString.convertDigitsToNamesInSubstring(17, 23);
        assertEquals("I'd b3tt3r put sZerome dOneSix1ts in this 5tr1n6, right?", myString.getString());
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: Test the convertDigitsToNamesInSubstring method with null input and expect a NullPointerException to be thrown.
    public void testConvertDigitsToNamesInSubstring2() {
        assertThrows(NullPointerException.class, () -> myString.convertDigitsToNamesInSubstring(1, 1));
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: Test the convertDigitsToNamesInSubstring method with an invalid range where the start index is greater than the end index.
    public void testConvertDigitsToNamesInSubstring3() {
        myString.setString("Hello");
        assertThrows(IllegalArgumentException.class, () -> myString.convertDigitsToNamesInSubstring(2, 1));
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: Test the convertDigitsToNamesInSubstring method with an index that is greater than the length of the string.
    public void testConvertDigitsToNamesInSubstring4() {
        myString.setString("Hello");
        assertThrows(MyIndexOutOfBoundsException.class, () -> myString.convertDigitsToNamesInSubstring(1, 6));
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    // Description: Test the convertDigitsToNamesInSubstring method with a string containing digits and convert them to their corresponding names within a specified substring.
    public void testConvertDigitsToNamesInSubstring5() {
        myString.setString("1Hello2World3");
        myString.convertDigitsToNamesInSubstring(1, 13);
        assertEquals("OneHelloTwoWorldThree", myString.getString());
    }
}
