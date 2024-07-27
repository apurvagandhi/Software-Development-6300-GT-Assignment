package edu.gatech.seclass.moditext;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Check if the program is called with at least one argument
        if (args.length < 1) {
            usage();
            return;
        }
        // Read the lines from the file specified in the last argument
        List<String> lines = new ArrayList<>();
        String fileName = args[args.length - 1];
        boolean endsWithNewline = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            // Check if the file is empty
            if (!lines.isEmpty()) {
                try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
                    if (raf.length() > 0) {
                        raf.seek(raf.length() - 1);
                        int lastLineDecimal = raf.read();
                        if (lastLineDecimal == 10) {
                            endsWithNewline = true;
                        } else {
                            endsWithNewline = false;
                        }
                    }
                }
                if (!endsWithNewline) {
                    usage();
                    return;
                }
            }
        } catch (IOException e) {
            usage();
            return;
        }

        // Initialize variables for different transformation options
        boolean keepLines = false;
        boolean padBeginning = false;
        boolean trimLines = false;
        boolean globalReplace = false;
        boolean formatText = false;
        boolean reverseLines = false;
        String keepSubstring = null;
        char padSymbol = '0';
        String formatStyle = null;
        String formatSubstring = null;
        int maxPadding = 0;
        int trimLength = 0;
        // Parse the command line arguments
        for (int i = 0; i < args.length - 1; i++) {
            switch (args[i]) {
                case "-k":
                    // Keep lines that contain a specific substring
                    keepLines = true;
                    if (i + 1 < args.length - 1) {
                        keepSubstring = args[++i];
                    } else {
                        usage();
                        return;
                    }
                    break;
                case "-p":
                    // Pad the beginning of lines with a symbol and a specified maximum padding
                    padBeginning = true;
                    if (i + 2 < args.length - 1) {
                        String symbol = args[++i];
                        try {
                            maxPadding = Integer.parseInt(args[++i]);
                            if (maxPadding < 1 || maxPadding > 100) {
                                usage();
                                return;
                            } else if (symbol.length() != 1) {
                                usage();
                                return;
                            } else {
                                padSymbol = symbol.charAt(0);
                            }
                        } catch (NumberFormatException e) {
                            usage();
                            return;
                        }
                    } else {
                        usage();
                        return;
                    }
                    break;
                case "-t":
                    // Trim lines to a specified length
                    trimLines = true;
                    if (i + 1 < args.length - 1) {
                        try {
                            trimLength = Integer.parseInt(args[++i]);
                            if (trimLength < 0 || trimLength > 100) {
                                usage();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            usage();
                            return;
                        }
                    } else {
                        usage();
                        return;
                    }
                    break;
                case "-g":
                    // Enable global replace mode
                    globalReplace = true;
                    break;
                case "-f":
                    // Format text by replacing a substring with a specified style
                    formatText = true;
                    if (i + 2 < args.length - 1) {
                        formatStyle = args[++i];
                        formatSubstring = args[++i];
                        if (!formatStyle.equals("bold") && !formatStyle.equals("italic")
                                && !formatStyle.equals("code")) {
                            usage();
                            return;
                        }
                        if (formatSubstring.isEmpty()) {
                            usage();
                            return;
                        }
                    } else {
                        usage();
                        return;
                    }
                    break;
                case "-r":
                    // Reverse the order of lines
                    reverseLines = true;
                    break;
                default:
                    // Print an error message for unrecognized options
                    usage();
                    return;
            }
        }
        // Check for conflicting options
        if (padBeginning && trimLines) {
            usage();
            return;
        }
        if (globalReplace && !formatText) {
            usage();
            return;
        }
        // Apply transformations in the specified order
        // Keep lines that contain a specific substring
        if (keepLines) {
            if (keepSubstring == null) {
                keepSubstring = "";
            }
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).contains(keepSubstring)) {
                    lines.remove(i);
                    i = i - 1;
                }
            }
        }
        // Pad the beginning of lines with a symbol and a specified maximum padding
        if (padBeginning) {
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.length() < maxPadding) {
                    // Calculate the number of spaces needed for padding
                    int numSpaces = maxPadding - line.length();
                    // Add the specified character for padding
                    StringBuilder paddedLine = new StringBuilder();
                    for (int j = 0; j < numSpaces; j++) {
                        paddedLine.append(padSymbol);
                    }
                    paddedLine.append(line);
                    lines.set(i, paddedLine.toString());
                }
            }
        }
        // Trim lines to a specified length
        else if (trimLines) {
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.length() > trimLength) {
                    lines.set(i, line.substring(0, trimLength));
                }
            }
        }
        // Format text by replacing a substring with a specified style
        if (formatText) {
            String replacement;
            if (formatStyle.equals("bold")) {
                replacement = "**%s**";
            } else if (formatStyle.equals("italic")) {
                replacement = "*%s*";
            } else if (formatStyle.equals("code")) {
                replacement = "`%s`";
            } else {
                replacement = "%s";
            }

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (globalReplace) {
                    lines.set(i, line.replaceAll("\\Q" + formatSubstring + "\\E",
                            String.format(replacement, formatSubstring)));
                } else {
                    lines.set(i, line.replaceFirst("\\Q" + formatSubstring + "\\E",
                            String.format(replacement, formatSubstring)));
                }
            }
        }
        // Reverse the order of lines
        if (reverseLines) {
            Collections.reverse(lines);
        }

        for (String line : lines) {
            System.out.println(line);
        }
    }

    // Print the usage message
    private static void usage() {
        System.err.println("Usage: moditext [ -k substring | -p ch num | -t num | -g | -f style substring | -r ] FILE");
    }
}
