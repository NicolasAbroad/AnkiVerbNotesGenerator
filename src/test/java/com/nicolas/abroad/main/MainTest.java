package com.nicolas.abroad.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Element;
import org.junit.Test;

/** Test class */
public class MainTest {

    // ----------------------------------
    // promptInputMode
    // ----------------------------------

    /**
     * Happy path. Test all patterns with correct input.
     * @throws Exception
     */
    @Test
    public void testPromptInputMode1() throws Exception {
        String text = "1\r\n2\r\n3";
        InputStream inputFileStream = new ByteArrayInputStream(text.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream))) {
            String output = Main.promptInputMode(br);
            assertEquals("INDICATIF", output);

            output = Main.promptInputMode(br);
            assertEquals("CONDITIONNEL", output);

            output = Main.promptInputMode(br);
            assertEquals("SUBJONCTIF", output);
        }
        inputFileStream.close();
    }

    /**
     * Happy path. Test using incorrect input several times before using correct
     * input.
     * @throws Exception
     */
    @Test
    public void testPromptInputMode2() throws Exception {
        String text = "4\r\n\r\n3";
        InputStream inputFileStream = new ByteArrayInputStream(text.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream))) {
            String output = Main.promptInputMode(br);
            assertEquals("SUBJONCTIF", output);
        }
        inputFileStream.close();
    }

    // ----------------------------------
    // promptInputTime
    // ----------------------------------

    /**
     * Happy path. Test all patterns with correct input. (Indicatif)
     * @throws Exception
     */
    @Test
    public void testPromptInputTime1() throws Exception {
        String mode = "INDICATIF";
        String text = "1\r\n2\r\n3\r\n4\r\n5\r\n6\r\n7\r\n8";
        InputStream inputFileStream = new ByteArrayInputStream(text.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream))) {
            String output = Main.promptInputTime(br, mode);
            assertEquals("Présent", output);

            output = Main.promptInputTime(br, mode);
            assertEquals("Imparfait", output);

            output = Main.promptInputTime(br, mode);
            assertEquals("Passé simple", output);

            output = Main.promptInputTime(br, mode);
            assertEquals("Futur simple", output);

            output = Main.promptInputTime(br, mode);
            assertEquals("Passé composé", output);

            output = Main.promptInputTime(br, mode);
            assertEquals("Plus-que-parfait", output);

            output = Main.promptInputTime(br, mode);
            assertEquals("Passé antérieur", output);

            output = Main.promptInputTime(br, mode);
            assertEquals("Futur antérieur", output);
        }
        inputFileStream.close();
    }

    /**
     * Happy path. Test all patterns with correct input. (Conditionnel)
     * @throws Exception
     */
    @Test
    public void testPromptInputTime2() throws Exception {
        String mode = "CONDITIONNEL";
        String text = "1\r\n2";
        InputStream inputFileStream = new ByteArrayInputStream(text.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream))) {
            String output = Main.promptInputTime(br, mode);
            assertEquals("Présent", output);

            output = Main.promptInputTime(br, mode);
            assertEquals("Passé", output);
        }
        inputFileStream.close();
    }

    /**
     * Happy path. Test all patterns with correct input. (Subjonctif)
     * @throws Exception
     */
    @Test
    public void testPromptInputTime3() throws Exception {
        String mode = "SUBJONCTIF";
        String text = "1\r\n2\r\n3\r\n4";
        InputStream inputFileStream = new ByteArrayInputStream(text.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream))) {
            String output = Main.promptInputTime(br, mode);
            assertEquals("Présent", output);

            output = Main.promptInputTime(br, mode);
            assertEquals("Imparfait", output);

            output = Main.promptInputTime(br, mode);
            assertEquals("Passé", output);

            output = Main.promptInputTime(br, mode);
            assertEquals("Plus-que-parfait", output);
        }
        inputFileStream.close();
    }

    /**
     * Happy path. Test using incorrect input several times before using correct
     * input.
     * @throws Exception
     */
    @Test
    public void testPromptInputTime4() throws Exception {
        String mode = "SUBJONCTIF";
        String text = "5\r\n\r\n4";
        InputStream inputFileStream = new ByteArrayInputStream(text.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream))) {
            String output = Main.promptInputTime(br, mode);
            assertEquals("Plus-que-parfait", output);
        }
        inputFileStream.close();
    }

    // ----------------------------------
    // promptLoadFile
    // ----------------------------------

    /**
     * Happy path. Test all patterns with correct input.
     * @throws Exception
     */
    @Test
    public void testPromptLoadFile() throws Exception {
        String text = "y\r\nn\r\na\r\ny\r\n5\r\n6\r\n7\r\n8";
        InputStream inputFileStream = new ByteArrayInputStream(text.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream))) {
            boolean output = Main.promptLoadFile(br);
            assertEquals(true, output);

            output = Main.promptLoadFile(br);
            assertEquals(false, output);

            output = Main.promptLoadFile(br);
            assertEquals(true, output);

        }
        inputFileStream.close();
    }

    // ----------------------------------
    // promptInputVerbs
    // ----------------------------------

    /**
     * Happy path. Input several verbs then line break.
     * @throws Exception
     */
    @Test
    public void testPromptInputVerbs1() throws Exception {
        String text = "donner\r\nenvoyer\r\navoir";
        InputStream inputFileStream = new ByteArrayInputStream(text.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream))) {
            List<String> output = Main.promptInputVerbs(br);
            assertEquals("[donner, envoyer, avoir]", output.toString());
        }
        inputFileStream.close();
    }

    /**
     * Happy path. Input one verb then line break.
     * @throws Exception
     */
    @Test
    public void testPromptInputVerbs2() throws Exception {
        String text = "donner\r\n\r\nenvoyer\r\navoir";
        InputStream inputFileStream = new ByteArrayInputStream(text.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream))) {
            List<String> output = Main.promptInputVerbs(br);
            assertEquals("[donner]", output.toString());
        }
        inputFileStream.close();
    }

    // ----------------------------------
    // outputSingleNote
    // ----------------------------------

    /**
     * Happy path. Generate one note.
     * @throws Exception
     */
    @Test
    public void testOutputSingleNote1() throws Exception {
        String verb = "donner";
        String mode = "INDICATIF";
        String time = "Imparfait";
        String note = Main.outputSingleNote(verb, mode, time);
        String expected = "\"donner-indicatif-imparfait\"; \"donner\"; \"INDICATIF Imparfait\"; \"je donnais\"; \"tu donnais\"; \"il (elle) donnait\"; \"nous donnions\"; \"vous donniez\"; \"ils (elles) donnaient\"";
        assertEquals(expected, note);
    }

    /**
     * Unhappy path. Incorrect verb.
     * @throws Exception
     */
    @Test
    public void testOutputSingleNote2() throws Exception {
        String verb = "kebab";
        String mode = "INDICATIF";
        String time = "Imparfait";
        try {
            Main.outputSingleNote(verb, mode, time);
            fail();
        } catch (HttpStatusException e) {
        }
    }

    /**
     * Unhappy path. Incorrect mode.
     * @throws Exception
     */
    @Test
    public void testOutputSingleNote3() throws Exception {
        String verb = "donner";
        String mode = "kebab";
        String time = "Imparfait";
        try {
            Main.outputSingleNote(verb, mode, time);
            fail();
        } catch (RuntimeException e) {
        }
    }

    /**
     * Unhappy path. Incorrect time.
     * @throws Exception
     */
    @Test
    public void testOutputSingleNote4() throws Exception {
        String verb = "donner";
        String mode = "INDICATIF";
        String time = "kebab";
        try {
            Main.outputSingleNote(verb, mode, time);
            fail();
        } catch (RuntimeException e) {
        }
    }

    // ----------------------------------
    // formatConjugation
    // ----------------------------------

    /**
     * Happy path.
     * @throws Exception
     */
    @Test
    public void testFormatConjugation1() throws Exception {
        String html = "<div><p><personal-pronoun>j’</personal-pronoun><auxiliary>ai</auxiliary><verb>traité</verb></p></div>";
        Element element = new Element("<a>");
        element.html(html);
        String conjugation = Main.formatConjugation(element);
        String actual = "j’ai traité";
        assertEquals(actual, conjugation);
    }

    /**
     * Happy path.
     * @throws Exception
     */
    @Test
    public void testFormatConjugation2() throws Exception {
        String html = "<div><p><personal-pronoun>que j’</personal-pronoun><auxiliary>aie</auxiliary><verb>traité</verb></p></div>";
        Element element = new Element("<a>");
        element.html(html);
        String conjugation = Main.formatConjugation(element);
        String actual = "que j’aie traité";
        assertEquals(actual, conjugation);
    }

    /**
     * Happy path.
     * @throws Exception
     */
    @Test
    public void testFormatConjugation3() throws Exception {
        String html = "<div><p><personal-pronoun>je </personal-pronoun><verb>traite</verb></p></div>";
        Element element = new Element("<a>");
        element.html(html);
        String conjugation = Main.formatConjugation(element);
        String actual = "je traite";
        assertEquals(actual, conjugation);
    }

}
