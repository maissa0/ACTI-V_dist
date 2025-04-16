package com.esprit.userAuth.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String resetUrl){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("Click the link to reset your password: " + resetUrl);
        mailSender.send(message);
    }
    
    public void sendCompanyRegistrationApprovalEmail(String to, String companyName, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Company Registration Approved");
        
        String emailText = "Dear " + companyName + ",\n\n" +
                "Your company registration has been approved. You can now access the application with the following credentials:\n\n" +
                "Username: " + username + "\n" +
                "Password: " + password + "\n\n" +
                "For security reasons, please change your password as soon as possible after logging in.\n\n" +
                "Best regards,\nThe Admin Team";
        
        message.setText(emailText);
        mailSender.send(message);
    }
    
    public void sendCompanyRegistrationRejectionEmail(String to, String companyName, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Company Registration Rejected");
        
        String emailText = "Dear " + companyName + ",\n\n" +
                "We regret to inform you that your company registration request has been rejected.\n\n";
        
        if (reason != null && !reason.isEmpty()) {
            emailText += "Reason: " + reason + "\n\n";
        }
        
        emailText += "If you have any questions, please contact our support team.\n\n" +
                "Best regards,\nThe Admin Team";
        
        message.setText(emailText);
        mailSender.send(message);
    }
    
    public void sendEmployeeRegistrationEmail(String to, String employeeName, String companyName, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your Employee Account at " + companyName);
        
        String emailText = "Dear " + employeeName + ",\n\n" +
                "An account has been created for you as an employee of " + companyName + ".\n\n" +
                "Your login credentials are:\n" +
                "Username: " + username + "\n" +
                "Password: " + password + "\n\n" +
                "For security reasons, please change your password as soon as possible after logging in.\n\n" +
                "Best regards,\n" + companyName + " Management";
        
        message.setText(emailText);
        mailSender.send(message);
    }
}