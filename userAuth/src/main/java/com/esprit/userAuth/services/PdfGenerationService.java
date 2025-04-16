package com.esprit.userAuth.services;

import com.esprit.userAuth.entities.User;
import com.esprit.userAuth.repositories.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class PdfGenerationService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${user-auth-service.url:http://localhost:8081}")
    private String userServiceUrl;
    
    @Value("${competence-service.url:http://localhost:8082/api/competence}")
    private String competenceServiceUrl;
    
    /**
     * Generate a standard PDF report for a user
     * @param userId ID of the user to generate report for
     * @return PDF as byte array
     * @throws Exception if PDF generation fails
     */
    public byte[] generateUserReport(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get user competences from competence service
        List<Map<String, Object>> competences = fetchUserCompetences(userId);
        
        return generatePdf(user, competences, null);
    }
    
    /**
     * Generate a PDF report with AI-generated summary
     * @param userId ID of the user to generate report for
     * @param aiSummary AI-generated summary text
     * @return PDF as byte array
     * @throws Exception if PDF generation fails
     */
    public byte[] generateUserReportWithAiSummary(Long userId, String aiSummary) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get user competences from competence service
        List<Map<String, Object>> competences = fetchUserCompetences(userId);
        
        return generatePdf(user, competences, aiSummary);
    }
    
    /**
     * Fetch user competences from the competence service
     * @param userId User ID
     * @return List of competence objects
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchUserCompetences(Long userId) {
        try {
            return restTemplate.getForObject(
                    competenceServiceUrl + "/competences/user/" + userId,
                    List.class);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Return empty list if service is unavailable
        }
    }
    
    /**
     * Generate the actual PDF document
     * @param user User object
     * @param competences List of competence objects
     * @param aiSummary Optional AI-generated summary (can be null)
     * @return PDF as byte array
     * @throws Exception if PDF generation fails
     */
    private byte[] generatePdf(User user, List<Map<String, Object>> competences, String aiSummary) throws Exception {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Add logo or header
            addHeader(document);
            
            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("User Competence Profile", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Add generated date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            Paragraph date = new Paragraph("Generated on: " + dateFormat.format(new Date()), 
                                          new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);
            
            // Add user information
            addUserInfo(document, user);
            
            // Add AI summary if provided
            if (aiSummary != null && !aiSummary.isEmpty()) {
                addAiSummary(document, aiSummary);
            }
            
            // Add competences
            addCompetences(document, competences);
            
            // Add footer
            addFooter(document);
            
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            document.close();
            throw e;
        }
    }
    
    private void addHeader(Document document) throws DocumentException {
        Paragraph header = new Paragraph("Acti'V Competence Management", 
                                        new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLUE));
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(10);
        document.add(header);
        
        // Add a line separator
        LineSeparator line = new LineSeparator();
        line.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
    }
    
    private void addUserInfo(Document document, User user) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
        Font contentFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        
        Paragraph section = new Paragraph("User Information", sectionFont);
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(20);
        
        // Add cells with user information
        addRow(table, "Username:", user.getUserName(), contentFont);
        addRow(table, "Full Name:", user.getFirstName() + " " + user.getLastName(), contentFont);
        addRow(table, "Email:", user.getEmail(), contentFont);
        addRow(table, "Role:", user.getRole().getRoleName().toString(), contentFont);
        addRow(table, "Account Created:", user.getCreatedDate().toString(), contentFont);
        
        document.add(table);
    }
    
    private void addAiSummary(Document document, String aiSummary) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
        Font contentFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC);
        
        Paragraph section = new Paragraph("AI-Generated Professional Profile Summary", sectionFont);
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);
        
        Paragraph summary = new Paragraph(aiSummary, contentFont);
        summary.setAlignment(Element.ALIGN_JUSTIFIED);
        summary.setSpacingAfter(20);
        document.add(summary);
        
        // Add a line separator
        LineSeparator line = new LineSeparator();
        line.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(new Chunk(line));
    }
    
    private void addCompetences(Document document, List<Map<String, Object>> competences) throws DocumentException {
        if (competences.isEmpty()) {
            document.add(new Paragraph("No competences found for this user."));
            return;
        }
        
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font contentFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        
        Paragraph section = new Paragraph("Competences", sectionFont);
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);
        
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 1, 6});
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        
        // Add table header
        PdfPCell nameHeader = new PdfPCell(new Phrase("Competence", headerFont));
        PdfPCell levelHeader = new PdfPCell(new Phrase("Level", headerFont));
        PdfPCell descHeader = new PdfPCell(new Phrase("Description", headerFont));
        
        nameHeader.setBackgroundColor(BaseColor.DARK_GRAY);
        levelHeader.setBackgroundColor(BaseColor.DARK_GRAY);
        descHeader.setBackgroundColor(BaseColor.DARK_GRAY);
        
        nameHeader.setPadding(5);
        levelHeader.setPadding(5);
        descHeader.setPadding(5);
        
        table.addCell(nameHeader);
        table.addCell(levelHeader);
        table.addCell(descHeader);
        
        // Add competence rows
        for (Map<String, Object> comp : competences) {
            PdfPCell nameCell = new PdfPCell(new Phrase(getStringValue(comp.get("name")), contentFont));
            PdfPCell levelCell = new PdfPCell(new Phrase(getStringValue(comp.get("level")), contentFont));
            PdfPCell descCell = new PdfPCell(new Phrase(getStringValue(comp.get("description")), contentFont));
            
            nameCell.setPadding(5);
            levelCell.setPadding(5);
            descCell.setPadding(5);
            
            table.addCell(nameCell);
            table.addCell(levelCell);
            table.addCell(descCell);
        }
        
        document.add(table);
    }
    
    private void addFooter(Document document) throws DocumentException {
        LineSeparator line = new LineSeparator();
        line.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(new Chunk(line));
        
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);
        Paragraph footer = new Paragraph("This document is confidential and generated by Acti'V Competence Management System.", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10);
        document.add(footer);
    }
    
    private void addRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        
        labelCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setBorder(Rectangle.NO_BORDER);
        
        labelCell.setPadding(5);
        valueCell.setPadding(5);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    private String getStringValue(Object obj) {
        return obj != null ? obj.toString() : "N/A";
    }
} 