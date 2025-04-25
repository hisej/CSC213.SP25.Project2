package edu.canisius.csc213.complaints.service;

import edu.canisius.csc213.complaints.model.Complaint;

import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;


public class ComplaintSimilarityService {

    private final List<Complaint> complaints;

    public ComplaintSimilarityService(List<Complaint> complaints) {
        this.complaints = complaints;
    }

    public List<Complaint> findTop3Similar(Complaint target) {
        // TO-DO: Return top 3 most similar complaints (excluding itself)
        //return List.of();
        return complaints.stream()
                .filter(c -> c.getComplaintId() != target.getComplaintId() && c.getEmbedding() != null)
                .map(c -> new ComplaintWithScore(c, cosineSimilarity(target.getEmbedding(), c.getEmbedding())))
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(3)
                .map(cws -> cws.complaint)
                .toList();
    }

    private double cosineSimilarity(double[] a, double[] b) {
        // TO-DO: Implement cosine similarity
        //return 0.0;
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return (normA == 0 || normB == 0) ? 0.0 : dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private static class ComplaintWithScore {
        Complaint complaint;
        double score;

        ComplaintWithScore(Complaint c, double s) {
            this.complaint = c;
            this.score = s;
        }
    }
}
