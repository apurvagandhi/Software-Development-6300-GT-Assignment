package edu.gatech.seclass.moditext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Timeout(value = 1, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class MyMainTest {
        // Place all of your tests in this class, optionally using MainTest.java as an
        // example
        private final String usageStr = "Usage: moditext [ -k substring | -p ch num | -t num | -g | -f style substring | -r ] FILE"
                        + System.lineSeparator();

        @TempDir
        Path tempDirectory;

        @RegisterExtension
        OutputCapture capture = new OutputCapture();

        /* ----------------------------- Test Utilities ----------------------------- */

        /**
         * Returns path of a new "input.txt" file with specified contents written
         * into it. The file will be created using {@link TempDir TempDir}, so it
         * is automatically deleted after test execution.
         * 
         * @param contents the text to include in the file
         * @return a Path to the newly written file, or null if there was an
         *         issue creating the file
         */
        private Path createFile(String contents) {
                return createFile(contents, "input.txt");
        }

        /**
         * Returns path to newly created file with specified contents written into
         * it. The file will be created using {@link TempDir TempDir}, so it is
         * automatically deleted after test execution.
         * 
         * @param contents the text to include in the file
         * @param fileName the desired name for the file to be created
         * @return a Path to the newly written file, or null if there was an
         *         issue creating the file
         */
        private Path createFile(String contents, String fileName) {
                Path file = tempDirectory.resolve(fileName);
                try {
                        Files.writeString(file, contents);
                } catch (IOException e) {
                        return null;
                }

                return file;
        }

        /**
         * Takes the path to some file and returns the contents within.
         * 
         * @param file the path to some file
         * @return the contents of the file as a String, or null if there was an
         *         issue reading the file
         */
        private String getFileContent(Path file) {
                try {
                        return Files.readString(file);
                } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                }
        }

        /* ------------------------------- Test Cases ------------------------------- */
        // Frame #: 1 Occurrence of FILE in option : Many
        @Test
        public void moditextTest1() {
                String input = "Sample input";
                Path inputFile1 = createFile(input);
                Path inputFile2 = createFile(input);
                String[] args = { "-k", "the", inputFile1.toString(), inputFile2.toString() };
                Main.main(args);
                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
        }

        // Frame #: 2 Occurrence of FILE in option : Not Present
        @Test
        public void moditextTest2() {
                String[] args = { "-k", "the" };
                Main.main(args);
                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
        }

        // Frame #: 3 The FILE idenfified by filename exists : File does not exists
        @Test
        public void moditextTest3() {
                String input = "Sample input";
                String[] args = { "-k", "the", "filedoesnotexist.txt" };
                Main.main(args);
                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
        }

        // Frame #: 4 Size : Empty
        @Test
        public void moditextTest4() {
                String input = "";
                Path inputFile = createFile(input);
                String[] args = { inputFile.toString() };
                Main.main(args);
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 5 file content : not terminated by newline
        @Test
        public void moditextTest5() {
                String input = "Sample input without newline at the end";
                Path inputFile = createFile(input);
                String[] args = { inputFile.toString() };
                Main.main(args);
                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 6 Occurrence of k in option : Multiple
        @Test
        public void moditextTest6() {
                String input = "- has everyone packed? Check." + System.lineSeparator()
                                + "- Does the car contain enough gas? Check." + System.lineSeparator()
                                + "- Fun will be had? Check." + System.lineSeparator();
                String expected = "- has everyone packed? *Check*." + System.lineSeparator()
                                + "- Fun will be had? *Check*." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-f", "italic", "Check", "-k", "contain", "-k", "ha", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expected, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 7 k specification : Not Present
        @Test
        public void moditextTest7() {
                String input = "- has everyone packed? Check." + System.lineSeparator()
                                + "- Does the car contain enough gas? Check." + System.lineSeparator()
                                + "- Fun will be had? Check." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-f", "italic", "Check", "-k", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // frame #: 8 Occurrence of p in option : One time with t option
        @Test
        public void moditextTest8() {
                String input = System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-p", "a", "2", "-t", "2", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // frame #: 9 Occurrence of p in option : One time without t option
        @Test
        public void moditextTest9() {
                String input = System.lineSeparator();
                String expected = "aa" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-p", "a", "2", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expected, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // frame #: 10 p specification : ch Not present
        @Test
        public void moditextTest10() {
                String input = "Once upon a time, here was a hen." + System.lineSeparator()
                                + "When this hen left the den, it roamed all of the land." + System.lineSeparator()
                                + "All of it, until the hen got to the end." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "the", "-p", "42", "-g", "-f", "bold", "hen", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // frame #: 11 p specification : num Not present
        @Test
        public void moditextTest11() {
                String input = "Once upon a time, here was a hen." + System.lineSeparator()
                                + "When this hen left the den, it roamed all of the land." + System.lineSeparator()
                                + "All of it, until the hen got to the end." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "the", "-p", "-", "-g", "-f", "bold", "hen", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // frame #: 12 p specification : String for ch Option
        @Test
        public void moditextTest12() {
                String input = "Once upon a time, here was a hen." + System.lineSeparator()
                                + "When this hen left the den, it roamed all of the land." + System.lineSeparator()
                                + "All of it, until the hen got to the end." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "the", "-p", "STRING", "42", "-g", "-f", "bold", "hen", "-r",
                                inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // frame #: 13 p specification : Out of Range for num
        @Test
        public void moditextTest13() {
                String input = "Once upon a time, here was a hen." + System.lineSeparator()
                                + "When this hen left the den, it roamed all of the land." + System.lineSeparator()
                                + "All of it, until the hen got to the end." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "the", "-p", "-", "1000000000", "-g", "-f", "bold", "hen", "-r",
                                inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // frame #: 14 p specification : Non-integer for num
        @Test
        public void moditextTest14() {
                String input = "Once upon a time, here was a hen." + System.lineSeparator()
                                + "When this hen left the den, it roamed all of the land." + System.lineSeparator()
                                + "All of it, until the hen got to the end." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "the", "-p", "-", "Non-Integer", "-g", "-f", "bold", "hen", "-r",
                                inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // frame #: 15 Occurrence of t in option : One time with p option
        @Test
        public void moditextTest15() {
                String input = System.lineSeparator();
                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-p", "a", "2", "-t", "2", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // frame #: 16 t specification : Out of Range
        @Test
        public void moditextTest16() {
                String input = System.lineSeparator();
                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-t", "20000000", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));

        }

        // frame #: 17 t specification : Non-integer
        @Test
        public void moditextTest17() {
                String input = "The vibrant red roses bloomed in the garden" + System.lineSeparator()
                                + "She wore a beautiful blue dress to the party" + System.lineSeparator()
                                + "The sky turned into a brilliant shade of blue" + System.lineSeparator()
                                + "His favorite color is red, her favorite is blue" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-t", "NON-INTEGER", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 18 t specification : Not Present
        @Test
        public void moditextTest18() {
                String input = "The vibrant red roses bloomed in the garden" + System.lineSeparator()
                                + "She wore a beautiful blue dress to the party" + System.lineSeparator()
                                + "The sky turned into a brilliant shade of blue" + System.lineSeparator()
                                + "His favorite color is red, her favorite is blue" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-f", "code", "int", "-t", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 19 Occurrence of f in option : Multiple
        @Test
        public void moditextTest19() {
                String input = "Integers in Java are written using the keyword int." + System.lineSeparator()
                                + "An int is 32-bits in most programming languages." + System.lineSeparator()
                                + "Java is no exception." + System.lineSeparator()
                                + "C++ however has uint, which is an int holding positive numbers."
                                + System.lineSeparator();
                String expected = "Integers in `Java` are written using the keyword int." + System.lineSeparator()
                                + "An int is 32-bits in most programming languages." + System.lineSeparator()
                                + "`Java` is no exception." + System.lineSeparator()
                                + "C++ however has uint, which is an int holding positive numbers."
                                + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-f", "code", "int", "-f", "code", "Java", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expected, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 20 f specification : Empty Substring
        @Test
        public void moditextTest20() {
                String input = "Integers in Java are written using the keyword int." + System.lineSeparator()
                                + "An int is 32-bits in most programming languages." + System.lineSeparator()
                                + "Java is no exception." + System.lineSeparator()
                                + "C++ however has uint, which is an int holding positive numbers."
                                + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-f", "code", "", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 21 f specification : Invalid string for style
        @Test
        public void moditextTest21() {
                String input = "Integers in Java are written using the keyword int." + System.lineSeparator()
                                + "An int is 32-bits in most programming languages." + System.lineSeparator()
                                + "Java is no exception." + System.lineSeparator()
                                + "C++ however has uint, which is an int holding positive numbers."
                                + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-f", "Joke", "int", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 22 f specification : Not Present
        @Test
        public void moditextTest22() {
                String input = "Integers in Java are written using the keyword int." + System.lineSeparator()
                                + "An int is 32-bits in most programming languages." + System.lineSeparator()
                                + "Java is no exception." + System.lineSeparator()
                                + "C++ however has uint, which is an int holding positive numbers."
                                + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-f", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 23 Occurrence of g in option : One with f option
        @Test
        public void moditextTest23() {
                String input = "Once upon a time, here was a hen." + System.lineSeparator()
                                + "When this hen left the den, it roamed all of the land." + System.lineSeparator()
                                + "All of it, until the hen got to the end." + System.lineSeparator();
                String expected = "--All of it, until the **hen** got to the end." + System.lineSeparator()
                                + "W**hen** this **hen** left the den, it roamed all of the land."
                                + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "the", "-p", "-", "42", "-g", "-f", "bold", "hen", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expected, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 24 Occurrence of g in option : One without f option
        @Test
        public void moditextTest24() {
                String input = "Once upon a time, here was a hen." + System.lineSeparator()
                                + "When this hen left the den, it roamed all of the land." + System.lineSeparator()
                                + "All of it, until the hen got to the end." + System.lineSeparator();
                Path inputFile = createFile(input);
                String[] args = { "-k", "the", "-p", "-", "42", "-g", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(usageStr, capture.stderr());
                Assertions.assertTrue(capture.stdout().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));

        }

        // Frame #: 25 Occurrence of r in option : Multiple
        @Test
        public void moditextTest25() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();

                String expected = "-roses are either red or purple." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-red paint goes well with purple paint." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-r", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expected, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));

        }

        // Frame #: 26
        @Test
        public void moditextTest26() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-roses are either red or purple." + System.lineSeparator()
                                + "-teal `is` a type of blue and green." + System.lineSeparator()
                                + "-red paint goes well with purple paint." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-t", "50", "-f", "code", "is", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 27
        @Test
        public void moditextTest27() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal `is` a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-t", "50", "-f", "code", "is", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));

        }

        // Frame #: 28
        @Test
        public void moditextTest28() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-roses are either red or purple." + System.lineSeparator()
                                + "-teal `is` a type of blue and green." + System.lineSeparator()
                                + "-red paint goes well with purple paint." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-f", "code", "is", "-r", "-t", "50", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));

        }

        // Frame #: 29
        @Test
        public void moditextTest29() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal `is` a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-f", "code", "is", "-t", "50", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));

        }

        // Frame #: 30
        @Test
        public void moditextTest30() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-roses are either red or purple." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-red paint goes well with purple paint." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-t", "50", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));

        }

        // Frame #: 31
        @Test
        public void moditextTest31() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-t", "50", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));

        }

        // Frame #: 32
        @Test
        public void moditextTest32() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-roses are either red or purple." + System.lineSeparator()
                                + "-teal `is` a type of blue and green." + System.lineSeparator()
                                + "-red paint goes well with purple paint." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-r", "-f", "code", "is", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 33
        @Test
        public void moditextTest33() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal `is` a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-f", "code", "is", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 34
        @Test
        public void moditextTest34() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-roses are either red or purple." + System.lineSeparator()
                                + "-teal `is` a type of blue and green." + System.lineSeparator()
                                + "-red paint goes well with purple paint." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-f", "code", "is", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 35
        @Test
        public void moditextTest35() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a `type` of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-f", "code", "type", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 36
        @Test
        public void moditextTest36() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-roses are either red or purple." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-red paint goes well with purple paint." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 37
        @Test
        public void moditextTest37() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 38
        @Test
        public void moditextTest38() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-teal is a `type` of blue and gr" + System.lineSeparator();
                Path inputFile = createFile(input);
                String[] args = { "-k", "blue", "-t", "30", "-f", "code", "type", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 39
        @Test
        public void moditextTest39() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-teal is a `type` of blue and gr" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "blue", "-t", "30", "-f", "code", "type", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 40
        @Test
        public void moditextTest40() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-teal is a type of `blue` and gr" + System.lineSeparator();
                Path inputFile = createFile(input);
                String[] args = { "-k", "blue", "-t", "30", "-f", "code", "blue", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 41
        @Test
        public void moditextTest41() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-teal is a type of `blue` and gr" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "is", "-t", "30", "-f", "code", "blue", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 42
        @Test
        public void moditextTest42() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-teal is a type of blue and gr" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "is", "-t", "30", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 43
        @Test
        public void moditextTest43() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-teal is a type of blue and gr" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "is", "-t", "30", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 44
        @Test
        public void moditextTest44() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-teal is a type of `blue` and green." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "is", "-f", "code", "blue", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 45
        @Test
        public void moditextTest45() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-teal is a type of `blue` and green." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "is", "-f", "code", "blue", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 46
        @Test
        public void moditextTest46() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-teal is a type of blue and `green`." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "is", "-f", "code", "green", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 47
        @Test
        public void moditextTest47() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-teal is a type of blue and `green`." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "is", "-f", "code", "green", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 48
        @Test
        public void moditextTest48() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-teal is a type of blue and green." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "is", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 49
        @Test
        public void moditextTest49() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-teal is a type of blue and green." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", "is", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 50
        @Test
        public void moditextTest50() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-roses are either red or purpl" + System.lineSeparator()
                                + "-teal is a type of `blue` and gr" + System.lineSeparator()
                                + "-red paint goes well with purp" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-t", "30", "-f", "code", "blue", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 51
        @Test
        public void moditextTest51() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-red paint goes well with purp" + System.lineSeparator()
                                + "-teal is a type of `blue` and gr" + System.lineSeparator()
                                + "-roses are either red or purpl" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-t", "30", "-f", "code", "blue", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 52
        @Test
        public void moditextTest52() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-roses are either red or purpl" + System.lineSeparator()
                                + "-teal is a type of blue and gr" + System.lineSeparator()
                                + "-red paint goes well with purp" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-t", "30", "-f", "bold", "purple", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 53
        @Test
        public void moditextTest53() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-red paint goes well with purp" + System.lineSeparator()
                                + "-teal is a type of blue and gr" + System.lineSeparator()
                                + "-roses are either red or purpl" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-t", "30", "-f", "bold", "purple", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 54
        @Test
        public void moditextTest54() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-roses are either red or purpl" + System.lineSeparator()
                                + "-teal is a type of blue and gr" + System.lineSeparator()
                                + "-red paint goes well with purp" + System.lineSeparator();
                Path inputFile = createFile(input);
                String[] args = { "-t", "30", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 55
        @Test
        public void moditextTest55() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-red paint goes well with purp" + System.lineSeparator()
                                + "-teal is a type of blue and gr" + System.lineSeparator()
                                + "-roses are either red or purpl" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-t", "30", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 56
        @Test
        public void moditextTest56() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-roses are either red or **purple**." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-red paint goes well with **purple** paint." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-f", "bold", "purple", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 57
        @Test
        public void moditextTest57() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-red paint goes well with **purple** paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or **purple**." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-f", "bold", "purple", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 58
        @Test
        public void moditextTest58() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-roses are either red or purple." + System.lineSeparator()
                                + "-teal is a type of blue and **green**." + System.lineSeparator()
                                + "-red paint goes well with purple paint." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-f", "bold", "green", "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 59
        @Test
        public void moditextTest59() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and **green**." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-f", "bold", "green", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 60
        @Test
        public void moditextTest60() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-roses are either red or purple." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-red paint goes well with purple paint." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-r", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // Frame #: 61
        @Test
        public void moditextTest61() {
                String input = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();
                String expectedOutput = "-red paint goes well with purple paint." + System.lineSeparator()
                                + "-teal is a type of blue and green." + System.lineSeparator()
                                + "-roses are either red or purple." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // #: 62
        @Test
        public void moditextTest62() {
                String input = "Okay, here is how this is going to work." + System.lineSeparator()
                                + "No shouting!" + System.lineSeparator()
                                + "Does that make sense?" + System.lineSeparator()
                                + "Alright, good meeting." + System.lineSeparator();
                String expectedOutput = "Okay, here is how this is going to work." + System.lineSeparator()
                                + "Alright, good meeting." + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-k", ".", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // #: 63
        @Test
        public void moditextTest63() {
                String input = "Hey, mind rotating this for me?" + System.lineSeparator()
                                + "*" + System.lineSeparator()
                                + "**" + System.lineSeparator()
                                + "***" + System.lineSeparator()
                                + "****" + System.lineSeparator()
                                + "*****" + System.lineSeparator()
                                + "Thanks!" + System.lineSeparator();

                String expectedOutput = "Hey, mind rotating this for me?" + System.lineSeparator()
                                + "----*" + System.lineSeparator()
                                + "---**" + System.lineSeparator()
                                + "--***" + System.lineSeparator()
                                + "-****" + System.lineSeparator()
                                + "*****" + System.lineSeparator()
                                + "Thanks!" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-p", "-", "5", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }

        // #: 64
        @Test
        public void moditextTest64() {
                String input = "The vibrant red roses bloomed in the garden" + System.lineSeparator()
                                + "She wore a beautiful blue dress to the party" + System.lineSeparator()
                                + "The sky turned into a brilliant shade of blue" + System.lineSeparator()
                                + "His favorite color is red, her favorite is blue" + System.lineSeparator();

                String expectedOutput = "The" + System.lineSeparator()
                                + "She" + System.lineSeparator()
                                + "The" + System.lineSeparator()
                                + "His" + System.lineSeparator();

                Path inputFile = createFile(input);
                String[] args = { "-t", "2", "-t", "6", "-t", "3", inputFile.toString() };
                Main.main(args);

                Assertions.assertEquals(expectedOutput, capture.stdout());
                Assertions.assertTrue(capture.stderr().isEmpty());
                Assertions.assertEquals(input, getFileContent(inputFile));
        }
}