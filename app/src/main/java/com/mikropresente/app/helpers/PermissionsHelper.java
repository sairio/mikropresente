package com.mikropresente.app.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class PermissionsHelper {
    //region permissions
    private final String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };

    //endregion

    //region methods

    public String[] getPermissions() { return this.permissions; }

    public boolean[] permissionCheck(Context _context) { return this.permissionCheck(_context, this.permissions); }

    public boolean permissionCheck(Activity _activity, int _requestID) { return this.permissionCheck(_activity, this.permissions, _requestID); }

    public boolean[] permissionCheck(Context _context, String[] _permissions) {
        boolean[] results = new boolean[_permissions.length];
        for(int index=0; index<_permissions.length; index++)
            results[index] = ContextCompat.checkSelfPermission(_context, _permissions[index]) == PackageManager.PERMISSION_GRANTED;
        return results;
    }
    public boolean permissionCheck(Activity _activity, String[] _permissionsID, int _requestID) {
        ArrayList<String> aList = new ArrayList<>();
        for(String permission : _permissionsID)
            if(ContextCompat.checkSelfPermission(_activity, permission) == PackageManager.PERMISSION_DENIED)
                aList.add(permission);
        if(aList.size()>0)
            ActivityCompat.requestPermissions(_activity, aList.toArray(new String[]{""}), _requestID);
        return aList.size()==0;
    }
    //endregion
}
