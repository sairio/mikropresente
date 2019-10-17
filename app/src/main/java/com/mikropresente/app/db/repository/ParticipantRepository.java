package com.mikropresente.app.db.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import com.mikropresente.app.db.AppDatabase;
import com.mikropresente.app.db.entity.Participant;
import com.mikropresente.app.helpers.Constants;

import java.util.List;

public class ParticipantRepository {

    private AppDatabase appDatabase;
    private Context ctx;

    public ParticipantRepository(Context ctx) {
        this.ctx = ctx;
        appDatabase = Room.databaseBuilder(ctx, AppDatabase.class,
                Constants.dbName)
                .build();
    }

    public void insert(final Participant participant) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                appDatabase.participanDao().insert(participant);
                return null;
            }
        }.execute();
    }

    public void delete(final Participant participant) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                appDatabase.participanDao().delete(participant);
                return null;
            }
        }.execute();
    }

    public LiveData<Participant> find(int id) {
        return appDatabase.participanDao().find(id);
    }

    public LiveData<Participant> findByName(String name) {
        return appDatabase.participanDao().findByName(name);
    }

    public LiveData<Participant> findByCode(String code) {
        return appDatabase.participanDao().findByCode(code);
    }

    public LiveData<List<Participant>> getAll() {
        return appDatabase.participanDao().getAll();
    }

}
