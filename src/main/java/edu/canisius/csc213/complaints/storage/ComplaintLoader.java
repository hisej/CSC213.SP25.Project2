package edu.canisius.csc213.complaints.storage;

import com.opencsv.bean.CsvToBeanBuilder;
import edu.canisius.csc213.complaints.model.Complaint;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Handles loading of complaints and embedding data,
 * and returns a fully hydrated list of Complaint objects.
 */
public class ComplaintLoader {

    /**
     * Loads complaints from a CSV file and merges with embedding vectors from a JSONL file.
     *
     * @param csvPath   Resource path to the CSV file
     * @param jsonlPath Resource path to the JSONL embedding file
     * @return A list of Complaint objects with attached embedding vectors
     * @throws Exception if file reading or parsing fails
     */
    public static List<Complaint> loadComplaintsWithEmbeddings(String csvPath, String jsonlPath) throws Exception {
        // TO-DO: Load CSV and JSONL resources, parse, and return hydrated Complaint list
        //InputStream csvStream = ComplaintLoader.class.getResourceAsStream(csvPath);
        //InputStream jsonlStream = ComplaintLoader.class.getResourceAsStream(jsonlPath);
        InputStream csvStream = ComplaintLoader.class.getResourceAsStream("/complaints_test_1_30.csv");
        InputStream jsonlStream = ComplaintLoader.class.getResourceAsStream("/embeddings_test_1_30.jsonl");


        if (csvStream == null || jsonlStream == null) {
            throw new IllegalArgumentException("CSV or JSONL file not found in resources.");
        }

        List<Complaint> complaints = new CsvToBeanBuilder<Complaint>(
                new InputStreamReader(csvStream, StandardCharsets.UTF_8))
                .withType(Complaint.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse();

        Map<Long, double[]> embeddings = EmbeddingLoader.loadEmbeddings(jsonlStream);
        ComplaintMerger.mergeEmbeddings(complaints, embeddings);

        return complaints;
    }
}