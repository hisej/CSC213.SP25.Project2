package edu.canisius.csc213.complaints.storage;

import edu.canisius.csc213.complaints.model.Complaint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ComplaintLoaderTest {

    @Test
    public void testLoadComplaintsWithEmbeddings() throws Exception {
        List<Complaint> complaints = ComplaintLoader.loadComplaintsWithEmbeddings(
                "/complaints_sample_1_30.csv",
                "/embeddings_sample_1_30_bk.jsonl"
        );

        assertNotNull(complaints, "Complaint list should not be null");
        assertEquals(30, complaints.size(), "Expected 30 complaints loaded");

        for (Complaint c : complaints) {
            assertNotNull(c.getEmbedding(), "Complaint ID " + c.getComplaintId() + " is missing embedding");
            assertTrue(c.getEmbedding().length > 100, "Embedding should have non-trivial length");
        }

        // Spot check one known ID
        Complaint known = complaints.stream()
                .filter(c -> c.getComplaintId() == 11454889L)
                .findFirst()
                .orElse(null);

        assertNotNull(known, "Expected known complaint ID 11454889");
        assertNotNull(known.getEmbedding(), "Known complaint should have embedding");
    }
}
