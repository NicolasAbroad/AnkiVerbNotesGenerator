package com.nicolas.abroad.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

/** Anki verb notes generator */
public class Main {

    /**
     * Generate Anki verb notes.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // Prompt user input
        String mode = null;
        String time = null;
        List<String> verbList = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            mode = promptInputMode(br);
            time = promptInputTime(br, mode);
            boolean loadFile = promptLoadFile(br);
            if (loadFile) {
                String inputFile = "input.txt";
                verbList = loadVerbs(inputFile);
            } else {
                verbList = promptInputVerbs(br);
            }
        }

        // Console log
        System.out.println("Les verbes suivants ont été traités.");

        // Output all notes
        List<String> allNoteList = new ArrayList<>();
        for (int i = 0; i < verbList.size(); i++) {
            String verb = verbList.get(i);

            // Delay loop to avoid spamming server with requests
            if (i != 0) {
                Thread.sleep(500);
            }

            // Output single note
            String singleNote = outputSingleNote(verb, mode, time);
            allNoteList.add(singleNote);
            System.out.println(verb);
        }

        // Generate output file content
        StringBuilder sb = new StringBuilder();
        for (String note : allNoteList) {
            sb.append(note);
            sb.append(System.lineSeparator());
        }
        String output = sb.toString();

        // Output file
        String outputFile = "output.txt";
        writeToFile(outputFile, output);
    }

    /**
     * Prompt user for mode.
     * @param br buffered reader
     * @return mode
     * @throws Exception
     */
    public static String promptInputMode(BufferedReader br) throws Exception {
        // Prepare map
        Map<String, String> modeMap = new HashMap<>();
        modeMap.put("1", "INDICATIF");
        modeMap.put("2", "CONDITIONNEL");
        modeMap.put("3", "SUBJONCTIF");

        // Output prompt
        String prompt = "Choisissez un mode.\r\n1 - Indicatif\r\n2 - Conditionnel\r\n3 - Subjonctif";
        System.out.println(prompt);

        // Prompt input
        String mode = null;
        while (mode == null || mode.isEmpty()) {

            String input = br.readLine();

            String temp = modeMap.get(input);
            if (temp != null && !temp.isEmpty()) {
                mode = temp;
            }
        }
        return mode;
    }

    /**
     * Prompt user for time.
     * @param br buffered reader
     * @param mode
     * @return time
     * @throws Exception
     */
    public static String promptInputTime(BufferedReader br, String mode) throws Exception {
        // Prepare maps
        Map<String, String> indicatifMap = new TreeMap<>();
        indicatifMap.put("1", "Présent");
        indicatifMap.put("2", "Imparfait");
        indicatifMap.put("3", "Passé simple");
        indicatifMap.put("4", "Futur simple");
        indicatifMap.put("5", "Passé composé");
        indicatifMap.put("6", "Plus-que-parfait");
        indicatifMap.put("7", "Passé antérieur");
        indicatifMap.put("8", "Futur antérieur");

        Map<String, String> conditionnelMap = new TreeMap<>();
        conditionnelMap.put("1", "Présent");
        conditionnelMap.put("2", "Passé");

        Map<String, String> subjonctifMap = new TreeMap<>();
        subjonctifMap.put("1", "Présent");
        subjonctifMap.put("2", "Imparfait");
        subjonctifMap.put("3", "Passé");
        subjonctifMap.put("4", "Plus-que-parfait");

        Map<String, Map<String, String>> timeMap = new TreeMap<>();
        timeMap.put("INDICATIF", indicatifMap);
        timeMap.put("CONDITIONNEL", conditionnelMap);
        timeMap.put("SUBJONCTIF", subjonctifMap);

        // Output prompt
        Map<String, String> modeMap = timeMap.get(mode);
        List<String> keys = new ArrayList<String>(modeMap.keySet());
        System.out.println("Choisissez un temps.");
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String time = modeMap.get(key);
            System.out.println(key + " - " + time);
        }

        // Prompt input
        String time = null;
        while (time == null || time.isEmpty()) {
            String input = br.readLine();
            String temp = modeMap.get(input);
            if (temp != null && !temp.isEmpty()) {
                time = temp;
            }
        }
        return time;
    }

    /**
     * Prompt user whether to load verbs from file.
     * @param br
     * @return true: load, false: don't load
     * @throws Exception
     */
    public static boolean promptLoadFile(BufferedReader br) throws Exception {
        String prompt = "Voulez-vous charger les verbes à partir d’un fichier ? (y/n)";
        System.out.println(prompt);

        while (true) {
            String input = br.readLine();
            if ("y".equals(input)) {
                return true;
            } else if ("n".equals(input)) {
                return false;
            }
        }
    }

    /**
     * Load verbs from file.
     * @param inputFile
     * @return verbs
     * @throws Exception
     */
    public static List<String> loadVerbs(String inputFile) throws Exception {
        List<String> verbs = new ArrayList<>();

        Path path = Paths.get(inputFile);
        try (FileInputStream fileInputStream = new FileInputStream(path.toFile());
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);) {
            String currentLine = null;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine == null || currentLine.isEmpty()) {
                    continue;
                }
                verbs.add(currentLine);
            }
        }

        return verbs;
    }

    /**
     * Prompt user for verbs.
     * @param br buffered reader
     * @return verbs
     * @throws Exception
     */
    public static List<String> promptInputVerbs(BufferedReader br) throws Exception {
        String prompt = "Saisissez un ou plusieurs verbes. (Appuyez sur entrée pour continuer)";
        System.out.println(prompt);

        List<String> verbs = new ArrayList<>();
        while (true) {
            String verb = br.readLine();
            if (verb == null || verb.isEmpty()) {
                break;
            }
            verbs.add(verb);
        }
        return verbs;
    }

    /**
     * Output a single anki note.
     * @param verb
     * @param mode
     * @param time
     * @return note
     * @throws Exception
     */
    public static String outputSingleNote(String verb, String mode, String time) throws Exception {
        // Fetch html document
        String url = "https://conjugaison.bescherelle.com/verbes/" + verb;
        Document document = fetchHTMLDocument(url);

        // Parse conjugations
        List<String> conjugationList = parseConjugation(document, mode, time);

        // Generate GUID (Global Unique Identifier)
        String guid = (verb + "-" + mode + "-" + time).toLowerCase();

        // Generate conjugation list
        List<String> singleNoteList = new ArrayList<>();
        singleNoteList.add(guid);
        singleNoteList.add(verb);
        singleNoteList.add(mode + " " + time);
        singleNoteList.addAll(conjugationList);

        // Output single note
        String singleNote = singleNoteList.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining("; "));
        return singleNote;
    }

    /**
     * Fetch html document.
     * @param url
     * @return html document
     * @throws IOException
     */
    public static Document fetchHTMLDocument(String url) throws IOException {
        // Get html source
        Connection connection = Jsoup.connect(url).method(Method.GET);
        Response response = connection.execute();
        Document document = response.parse().normalise();

        // Output document
        OutputSettings settings = new OutputSettings();
        settings.escapeMode(EscapeMode.xhtml);
        settings.prettyPrint(false);
        settings.indentAmount(0);
        settings.charset("UTF-8");
        document.outputSettings(settings);
        return document;
    }

    /**
     * Parse conjugations from the provided html document.
     * @param document
     * @param mode
     * @param time
     * @return list of conjugations
     */
    public static List<String> parseConjugation(Document document, String mode, String time) {
        // Selectors
        String containerSelector = "#nav-tabContent-active-passive>div.active"; // select active container
        String modeSelector = "h4.card-title:contains(" + mode + ")";
        String timeSelector = "h5.card-title:contains(" + time + ")";
        String selector = containerSelector + " " + modeSelector + "~div " + timeSelector + "+div";

        // Element selection
        Elements conjugationElements = document.select(selector);
        Element firstElement = conjugationElements.first();
        if (firstElement == null) {
            throw new RuntimeException("Erreur mode/temps");
        }
        Elements children = firstElement.children();

        // Add to conjugation list
        List<String> conjugationList = new ArrayList<>();
        for (Element child : children) {
            String conjugation = formatConjugation(child);
            conjugationList.add(conjugation);
        }
        return conjugationList;
    }

    /**
     * Format conjugation text contained in child element.
     * @param child
     * @return conjugation
     */
    public static String formatConjugation(Element child) {
        String pronoun = child.select("personal-pronoun").text();
        String auxiliary = child.select("auxiliary").text();
        String verb = child.select("verb").text();

        StringBuilder conjugation = new StringBuilder();
        conjugation.append(pronoun);
        if (auxiliary != null && !auxiliary.isEmpty()) {
            if (pronoun.charAt(pronoun.length() - 1) != '’') {
                conjugation.append(" ");
            }
            conjugation.append(auxiliary);
        }
        conjugation.append(" ");
        conjugation.append(verb);

        return conjugation.toString();
    }

    /**
     * Write string to file.
     * @param fileName
     * @param text
     * @throws IOException
     */
    public static void writeToFile(String fileName, String text) throws IOException {
        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"))) {
            out.write(text);
        }
    }

}
