package edu.canisius.csc213.complaints.storage;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

public class EmbeddingLoader {

    /**
     * Loads complaint embeddings from a JSONL (newline-delimited JSON) file.
     * Each line must be a JSON object with:
     * {
     *   "complaintId": <long>,
     *   "embedding": [<double>, <double>, ...]
     * }
     *
     * @param jsonlStream InputStream to the JSONL file
     * @return A map from complaint ID to its embedding vector
     * @throws IOException if the file cannot be read or parsed
     */
    public static Map<Long, double[]> loadEmbeddings(InputStream jsonlStream) throws IOException {
        // TO-DO: Implement parsing of JSONL to extract complaintId and embedding
        //return new HashMap<>();
        Map<Long, double[]> embeddings = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        // Ensure stream is not null before proceeding
        if (jsonlStream == null) {
            throw new FileNotFoundException("JSONL file not found in resources.");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(jsonlStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    ParsedEntry entry = mapper.readValue(line, ParsedEntry.class);

                    // Validate parsed data
                    if (entry.id == null || entry.embedding == null || entry.embedding.length != 1024) {
                        System.err.println("Skipping invalid entry: " + line);
                        continue; // Skip bad entries
                    }

                    embeddings.put(entry.id, entry.embedding);
                } catch (Exception e) {
                    System.err.println("Error parsing line, skipping: " + line);
                }
            }
        }

        return embeddings;
    }
    // Helper class for parsing JSON data
    static class ParsedEntry {
        public Long id;
        public double[] embedding;
    }

    public static void main(String[] args) {
        // Corrected file path for testing
        //InputStream jsonlStream = EmbeddingLoader.class.getResourceAsStream("/embeddings_test_1_30.jsonl");

        // Debugging: Verify file existence before attempting to load it
        File file = new File("target/classes/embeddings_test_1_30.jsonl");
        System.out.println("Checking file existence: " + file.getAbsolutePath());
        System.out.println("File exists: " + file.exists());

        // Use ClassLoader for reliable resource loading
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream jsonlStream = classLoader.getResourceAsStream("embeddings_test_1_30.jsonl");

        if (jsonlStream == null) {
            System.err.println("Error: JSONL file not found in classpath.");
            // Debugging
            try {
                // Alternative approach: Load using FileInputStream
                jsonlStream = new FileInputStream(file);
                System.out.println("Successfully loaded file via direct access.");
            } catch (FileNotFoundException e) {
                System.err.println("Still unable to locate JSONL file. Ensure it's in the correct directory.");
                return;
            }
        }

        try {
            Map<Long, double[]> embeddings = EmbeddingLoader.loadEmbeddings(jsonlStream);
            System.out.println("Loaded embeddings successfully! Total entries: " + embeddings.size());
        } catch (IOException e) {
            System.err.println("Error loading embeddings: " + e.getMessage());
        }
    }
}
