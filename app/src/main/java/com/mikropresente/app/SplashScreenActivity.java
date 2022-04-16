package com.mikropresente.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mikropresente.app.db.entity.Participant;
import com.mikropresente.app.db.repository.ParticipantRepository;
import com.mikropresente.app.helpers.CSVReader;
import com.mikropresente.app.helpers.Constants;
import com.mikropresente.app.helpers.PermissionsHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        tvMessage = findViewById(R.id.splash_text_view);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Comprobacion de permisos para poder continuar. Necesario para versiones >= a Android 6.0
        try {
            if (!new PermissionsHelper().permissionCheck(this, Constants.ACTIVITY_RESULT_PERMISSIONS)) {
                Toast.makeText(this, R.string.please_accept_permissions, Toast.LENGTH_SHORT);
            }
            checkDbData();
        } catch (Exception ex) {
            tvMessage.setText(getResources().getString(R.string.please_accept_permissions));
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void checkDbData() {
        try {
            final ParticipantRepository repository = new ParticipantRepository(getApplicationContext());
            repository.getAll().observe(SplashScreenActivity.this, new Observer<List<Participant>>() {
                @Override
                public void onChanged(List<Participant> participants) {
                    if (participants.size() > 0) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        tvMessage.setText(getResources().getString(R.string.no_data_in_db));
                        loadAndSaveData(repository);
                    }
                }
            });
        } catch (Exception ex) {
            tvMessage.setText(getResources().getString(R.string.error_verifing_db));
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadAndSaveData(ParticipantRepository repository) {
        List<String[]> rows = new ArrayList<>();
        CSVReader csvReader = new CSVReader(SplashScreenActivity.this, Constants.CSVFILENAME, ";");
        try {
            rows = csvReader.readCSV();
        } catch (IOException e) {
            e.printStackTrace();
            tvMessage.setText(getResources().getString(R.string.error_reading_data));
        }
        tvMessage.setText(getResources().getString(R.string.saving_data));
        try {
            for (int i = 0; i < rows.size(); i++) {
                Participant participant = new Participant();
                participant.name =rows.get(i)[0].trim() + " " + rows.get(i)[1].trim();
                participant.email = rows.get(i)[2].trim();
                participant.position = rows.get(i)[3].trim();
                participant.code = rows.get(i)[5].trim();

                repository.insert(participant);
            }
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }catch(Exception ex) {
            ex.printStackTrace();
            tvMessage.setText(getResources().getString(R.string.error_reading_data));
        }
    }
}
