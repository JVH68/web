package com.robotemplates.webviewapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment;
import com.beautycoder.pflockscreen.security.PFFingerprintPinCodeHelper;
import com.beautycoder.pflockscreen.security.PFSecurityException;
import com.robotemplates.webviewapp.activity.MainActivity;

public class Dash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        showLockScreenFragment();
    }


    private PFLockScreenFragment.OnPFLockScreenCodeCreateListener mCodeCreateListener =
            new PFLockScreenFragment.OnPFLockScreenCodeCreateListener() {
        @Override
        public void onCodeCreated(String encodedCode) {
            Toast.makeText(Dash.this, "Code created", Toast.LENGTH_SHORT).show();
            showNext();
            PreferencesSettings.saveToPref(Dash.this, encodedCode);

        }
    };

    private PFLockScreenFragment.OnPFLockScreenLoginListener mLoginListener =
            new PFLockScreenFragment.OnPFLockScreenLoginListener() {

        @Override
        public void onCodeInputSuccessful() {
            Toast.makeText(Dash.this, "Code successfull", Toast.LENGTH_SHORT).show();
            showNext();
        }

        @Override
        public void onFingerprintSuccessful() {
            Toast.makeText(Dash.this, "Fingerprint successfull", Toast.LENGTH_SHORT).show();
            showNext();
        }

        @Override
        public void onPinLoginFailed() {
            Toast.makeText(Dash.this, "Pin failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFingerprintLoginFailed() {
            Toast.makeText(Dash.this, "Fingerprint failed", Toast.LENGTH_SHORT).show();
        }
    };

    private void showLockScreenFragment() {
        try {
            final boolean isPinExist = PFFingerprintPinCodeHelper.getInstance().isPinCodeEncryptionKeyExist();
            final PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration.Builder(this)
                    .setTitle(isPinExist ? "Unlock with your pin code or fingerprint" : "Create Code")
                    .setCodeLength(6)
                    .setLeftButton("Can't remeber", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .setUseFingerprint(true);
            PFLockScreenFragment fragment = new PFLockScreenFragment();

            builder.setMode(isPinExist
                    ? PFFLockScreenConfiguration.MODE_AUTH
                    : PFFLockScreenConfiguration.MODE_CREATE);
            if (isPinExist) {
                fragment.setEncodedPinCode(PreferencesSettings.getCode(this));
                fragment.setLoginListener(mLoginListener);
            }

            fragment.setConfiguration(builder.build());
            fragment.setCodeCreateListener(mCodeCreateListener);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_view, fragment).commit();

        } catch (PFSecurityException e) {
            e.printStackTrace();
            Toast.makeText(Dash.this, "Can not get pin code info", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private  void showNext() {
        Intent myIntent = new Intent(Dash.this, MainActivity.class);
        Dash.this.startActivity(myIntent);
    }
       



}
