package com.fariha.deshistore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EmailVerificationActivity extends AppCompatActivity {

    private EditText etCode1, etCode2, etCode3, etCode4, etCode5, etCode6;
    private Button btnVerify, btnResendCode;
    private TextView tvEmail, tvTimer, tvResendInfo;
    private ProgressBar progressBar;

    private String userEmail;
    private String correctCode;
    private long codeExpiryTime;
    private CountDownTimer countDownTimer;

    private static final String PREFS_NAME = "VerificationPrefs";
    private static final long CODE_VALIDITY_DURATION = 10 * 60 * 1000; // 10 minutes
    private static final long RESEND_COOLDOWN = 60 * 1000; // 1 minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        initializeViews();
        loadVerificationData();
        setupCodeInputs();
        setupClickListeners();
        startTimer();
    }

    private void initializeViews() {
        etCode1 = findViewById(R.id.etCode1);
        etCode2 = findViewById(R.id.etCode2);
        etCode3 = findViewById(R.id.etCode3);
        etCode4 = findViewById(R.id.etCode4);
        etCode5 = findViewById(R.id.etCode5);
        etCode6 = findViewById(R.id.etCode6);
        btnVerify = findViewById(R.id.btnVerify);
        btnResendCode = findViewById(R.id.btnResendCode);
        tvEmail = findViewById(R.id.tvEmail);
        tvTimer = findViewById(R.id.tvTimer);
        tvResendInfo = findViewById(R.id.tvResendInfo);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);
    }

    private void loadVerificationData() {
        // Get data from intent
        userEmail = getIntent().getStringExtra("email");
        correctCode = getIntent().getStringExtra("code");
        codeExpiryTime = getIntent().getLongExtra("expiry", System.currentTimeMillis() + CODE_VALIDITY_DURATION);

        if (userEmail != null) {
            tvEmail.setText("We sent a code to " + maskEmail(userEmail));
        }
    }

    private void setupCodeInputs() {
        EditText[] codeInputs = {etCode1, etCode2, etCode3, etCode4, etCode5, etCode6};

        for (int i = 0; i < codeInputs.length; i++) {
            final int index = i;
            codeInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < codeInputs.length - 1) {
                        codeInputs[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Auto-verify when all 6 digits entered
                    if (index == 5 && s.length() == 1) {
                        verifyCode();
                    }
                }
            });

            // Handle backspace
            codeInputs[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && 
                    codeInputs[index].getText().length() == 0 && index > 0) {
                    codeInputs[index - 1].requestFocus();
                    return true;
                }
                return false;
            });
        }
    }

    private void setupClickListeners() {
        btnVerify.setOnClickListener(v -> verifyCode());

        btnResendCode.setOnClickListener(v -> resendCode());
    }

    private void verifyCode() {
        String enteredCode = etCode1.getText().toString() +
                etCode2.getText().toString() +
                etCode3.getText().toString() +
                etCode4.getText().toString() +
                etCode5.getText().toString() +
                etCode6.getText().toString();

        if (enteredCode.length() != 6) {
            Toast.makeText(this, "Please enter all 6 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if code expired
        if (System.currentTimeMillis() > codeExpiryTime) {
            Toast.makeText(this, "Verification code has expired. Please request a new one.", Toast.LENGTH_LONG).show();
            return;
        }

        // Verify code
        if (enteredCode.equals(correctCode)) {
            Toast.makeText(this, "Email verified successfully!", Toast.LENGTH_SHORT).show();
            
            // Save verification status
            saveVerificationStatus(userEmail, true);
            
            // Return to login or proceed to home
            Intent resultIntent = new Intent();
            resultIntent.putExtra("verified", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Invalid verification code. Please try again.", Toast.LENGTH_SHORT).show();
            clearCodeInputs();
        }
    }

    private void resendCode() {
        btnResendCode.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // Generate new code
        String newCode = EmailSender.generateVerificationCode();

        // Send email
        EmailSender.sendVerificationEmail(userEmail, newCode, new EmailSender.EmailCallback() {
            @Override
            public void onSending() {
                runOnUiThread(() -> Toast.makeText(EmailVerificationActivity.this, 
                    "Sending verification code...", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    correctCode = newCode;
                    codeExpiryTime = System.currentTimeMillis() + CODE_VALIDITY_DURATION;
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EmailVerificationActivity.this, 
                        "New verification code sent!", Toast.LENGTH_SHORT).show();
                    
                    // Restart timer
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    startTimer();
                    
                    // Enable resend button after cooldown
                    new CountDownTimer(RESEND_COOLDOWN, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            tvResendInfo.setText("Resend available in " + (millisUntilFinished / 1000) + "s");
                        }

                        @Override
                        public void onFinish() {
                            btnResendCode.setEnabled(true);
                            tvResendInfo.setText("");
                        }
                    }.start();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnResendCode.setEnabled(true);
                    Toast.makeText(EmailVerificationActivity.this, 
                        "Failed to send code: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void startTimer() {
        long remainingTime = codeExpiryTime - System.currentTimeMillis();
        
        if (remainingTime <= 0) {
            tvTimer.setText("Code expired");
            return;
        }

        countDownTimer = new CountDownTimer(remainingTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format("Code expires in %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("Code expired");
                Toast.makeText(EmailVerificationActivity.this, 
                    "Verification code has expired", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void clearCodeInputs() {
        etCode1.setText("");
        etCode2.setText("");
        etCode3.setText("");
        etCode4.setText("");
        etCode5.setText("");
        etCode6.setText("");
        etCode1.requestFocus();
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) return email;
        
        String masked = username.charAt(0) + "***" + username.charAt(username.length() - 1) + "@" + domain;
        return masked;
    }

    private void saveVerificationStatus(String email, boolean verified) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean("verified_" + email, verified).apply();
    }

    public static boolean isEmailVerified(android.content.Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        return prefs.getBoolean("verified_" + email, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
