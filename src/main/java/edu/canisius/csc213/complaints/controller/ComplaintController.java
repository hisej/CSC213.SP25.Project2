package edu.canisius.csc213.complaints.controller;

import edu.canisius.csc213.complaints.model.Complaint;
import edu.canisius.csc213.complaints.service.ComplaintSimilarityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ComplaintController {

    private final List<Complaint> complaints;
    private final ComplaintSimilarityService similarityService;

    public ComplaintController(List<Complaint> complaints, ComplaintSimilarityService similarityService) {
        this.complaints = complaints;
        this.similarityService = similarityService;
    }

    @GetMapping("/complaint")
    public String showComplaint(@RequestParam(defaultValue = "0") String index, Model model) {
        int parsedIndex;

        try {
            parsedIndex = Integer.parseInt(index);
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Invalid complaint index. Showing default complaint.");
            parsedIndex = 0;
        }

        int max = complaints.size();
        if (parsedIndex < 0 || parsedIndex >= max) {
            model.addAttribute("error", "Complaint index out of range. Showing default complaint.");
            parsedIndex = 0;
        }

        Complaint current = complaints.get(parsedIndex);
        List<Complaint> similar = similarityService.findTop3Similar(current);

        model.addAttribute("complaint", current);
        model.addAttribute("similarComplaints", similar);
        model.addAttribute("prevIndex", parsedIndex > 0 ? parsedIndex - 1 : 0);
        model.addAttribute("nextIndex", parsedIndex < max - 1 ? parsedIndex + 1 : max - 1);

        return "complaint";
    }

    // adding a search feature to the ui
    @GetMapping("/search")
    public String searchComplaints(@RequestParam(required = false) String company, Model model) {
        if (company == null || company.trim().isEmpty()) {
            model.addAttribute("error", "Please enter a company name.");
            return "search";
        }

        String searchTerm = company.toLowerCase();
        List<Complaint> searchResults = complaints.stream()
                .filter(complaint -> complaint.getCompany() != null &&
                        complaint.getCompany().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        if (searchResults.isEmpty()) {
            model.addAttribute("error", "No complaints found for that company.");
        } else {
            model.addAttribute("searchResults", searchResults);
        }

        model.addAttribute("companySearch", company);
        return "search";
    }
}