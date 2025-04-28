package edu.canisius.csc213.complaints.storage;

import com.opencsv.bean.CsvToBeanBuilder;
import edu.canisius.csc213.complaints.model.Complaint;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ComplaintEmbeddingMergeTest {

    @Test
    public void testEmbeddingMergeByComplaintId() throws Exception {
        // Load complaints
        InputStream csvStream = getClass().getResourceAsStream("/complaints_sample_1_30.csv");
        assertNotNull(csvStream, "CSV file not found");
        List<Complaint> complaints = new CsvToBeanBuilder<Complaint>(
                new InputStreamReader(csvStream, StandardCharsets.UTF_8)
        ).withType(Complaint.class).build().parse();

        // Load embeddings

        //InputStream jsonlStream = getClass().getResourceAsStream("/embeddings_sample_1_30_bk.jsonl");  had to edit this line 4/28 to get test to run
        InputStream jsonlStream = getClass().getResourceAsStream("/embeddings_sample_1_30.jsonl");

        assertNotNull(jsonlStream, "JSONL file not found");
        Map<Long, double[]> embeddings = EmbeddingLoader.loadEmbeddings(jsonlStream);

        // Merge them
        ComplaintMerger.mergeEmbeddings(complaints, embeddings);

        // Assert that all 30 have their embeddings
        for (Complaint complaint : complaints) {
            assertNotNull(complaint.getEmbedding(),
                    "Embedding missing for complaint ID " + complaint.getComplaintId());
            assertEquals(1024, complaint.getEmbedding().length,
                    "Embedding should have 1024 dimensions for complaint ID " + complaint.getComplaintId());
        }

        // Spot check one known ID (update this to match one from your sample)
        Complaint known = complaints.stream()
                .filter(c -> c.getComplaintId() == 11454889L)
                .findFirst()
                .orElse(null);

        assertNotNull(known, "Known complaint not found");
        assertNotNull(known.getEmbedding(), "Embedding not set on known complaint");
    }
}
