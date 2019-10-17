package com.mikropresente.app.helpers;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    Context ctx;
    String fileName;
    List<String[]> rows = new ArrayList<>();
    String csvSplitBy;

    public CSVReader(Context ctx, String fileName) {
        this.ctx = ctx;
        this.fileName = fileName;
        csvSplitBy = ",";
    }

    public CSVReader(Context ctx, String fileName, String csvSplitBy) {
        this.ctx = ctx;
        this.fileName = fileName;
        this.csvSplitBy = csvSplitBy;
    }

    public List<String[]> readCSV() throws IOException {
        InputStream inputStream = ctx.getAssets().open(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;

        bufferedReader.readLine();
        while((line = bufferedReader.readLine()) != null) {
            String[] row = line.split(csvSplitBy);
            rows.add(row);
        }
        return rows;
    }
}
