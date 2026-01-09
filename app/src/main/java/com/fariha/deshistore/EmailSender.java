package com.fariha.deshistore;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Email sender utility class for sending verification codes
 */
public class EmailSender {

    private static final String SENDER_EMAIL = "deshistore.main@gmail.com";
    private static final String SENDER_PASSWORD = "metagmqgcjkbgzld"; // App password (spaces removed)
    private static final String TAG = "EmailSender";

    /**
     * Generate a random 6-digit verification code
     */
    public static String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generates 6-digit number
        return String.valueOf(code);
    }

    /**
     * Send verification email asynchronously
     */
    public static void sendVerificationEmail(String recipientEmail, String verificationCode, EmailCallback callback) {
        new SendEmailTask(recipientEmail, verificationCode, callback).execute();
    }

    /**
     * Build HTML email content
     */
    private static String buildEmailContent(String verificationCode) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<body style='font-family: Arial, sans-serif; padding: 20px; background-color: #f5f5f5;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);'>" +
                "<h2 style='color: #2E7D32; margin-bottom: 20px;'>Email Verification</h2>" +
                "<p style='font-size: 16px; color: #333; margin-bottom: 20px;'>Your verification code is:</p>" +
                "<div style='background-color: #E8F5E9; padding: 20px; border-radius: 5px; text-align: center; margin-bottom: 20px;'>" +
                "<h1 style='color: #2E7D32; margin: 0; font-size: 36px; letter-spacing: 5px;'>" + verificationCode + "</h1>" +
                "</div>" +
                "<p style='font-size: 14px; color: #666; margin-bottom: 10px;'>This code will expire in 10 minutes.</p>" +
                "<p style='font-size: 14px; color: #666;'>If you didn't request this code, please ignore this email.</p>" +
                "<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 20px 0;'>" +
                "<p style='font-size: 12px; color: #999; text-align: center;'>Finding BD Products - Deshi Store</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * AsyncTask for sending email in background
     */
    private static class SendEmailTask extends AsyncTask<Void, Void, Boolean> {
        private String recipientEmail;
        private String verificationCode;
        private EmailCallback callback;
        private String errorMessage;

        public SendEmailTask(String recipientEmail, String verificationCode, EmailCallback callback) {
            this.recipientEmail = recipientEmail;
            this.verificationCode = verificationCode;
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (callback != null) {
                callback.onSending();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Setup mail server properties
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.starttls.required", "true");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");

                // Create session with authentication
                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                    }
                });

                // Create email message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL, "Deshi Store"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Deshi Store - Email Verification Code");
                message.setContent(buildEmailContent(verificationCode), "text/html; charset=utf-8");

                // Send email
                Transport.send(message);
                Log.d(TAG, "Verification email sent successfully to: " + recipientEmail);
                return true;

            } catch (Exception e) {
                Log.e(TAG, "Failed to send email", e);
                errorMessage = e.getMessage();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (callback != null) {
                if (success) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(errorMessage != null ? errorMessage : "Failed to send email");
                }
            }
        }
    }

    /**
     * Callback interface for email sending
     */
    public interface EmailCallback {
        void onSending();
        void onSuccess();
        void onFailure(String error);
    }
}
