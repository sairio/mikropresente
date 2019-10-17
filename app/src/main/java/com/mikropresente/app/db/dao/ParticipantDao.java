package com.mikropresente.app.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.mikropresente.app.db.entity.Participant;
import static androidx.room.OnConflictStrategy.REPLACE;
import java.util.List;

@Dao
public interface ParticipantDao {
    @Query("SELECT * FROM participant")
    LiveData<List<Participant>> getAll();

    @Query("SELECT * FROM participant WHERE uid IN (:participantsIds)")
    LiveData<List<Participant>> loadAllByIds(int[] participantsIds);

    @Query("SELECT * FROM participant WHERE name LIKE :first LIMIT 1")
    LiveData<Participant> findByName(String first);

    @Query("SELECT * FROM participant WHERE code LIKE :first LIMIT 1")
    LiveData<Participant> findByCode(String first);

    @Query("SELECT * FROM participant WHERE uid =:Id LIMIT 1")
    LiveData<Participant> find(int Id);

    @Insert(onConflict = REPLACE)
    void insert(Participant participant);

    @Insert
    void insertAll(Participant... participants);

    @Delete
    void delete(Participant participant);
}
