package com.fariha.deshistore;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MigrationUtils {

    public interface MigrationCallback {
        void onProgress(int migrated, int total);
        void onComplete(int migrated);
        void onError(String message);
    }

    public static void migrateRealtimeUsersToFirestore(Context context, MigrationCallback callback) {
        FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot == null) {
                            if (callback != null) callback.onError("No users found in Realtime DB");
                            return;
                        }
                        int total = (int) snapshot.getChildrenCount();
                        int[] migrated = {0};
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                        if (total == 0) {
                            if (callback != null) callback.onComplete(0);
                            return;
                        }

                        for (DataSnapshot child : snapshot.getChildren()) {
                            String uid = child.getKey();
                            Map<String, Object> userData = new HashMap<>();
                            for (DataSnapshot field : child.getChildren()) {
                                userData.put(field.getKey(), field.getValue());
                            }

                            if (uid == null) {
                                continue;
                            }

                            firestore.collection("users").document(uid)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        migrated[0]++;
                                        if (callback != null) callback.onProgress(migrated[0], total);
                                        if (migrated[0] == total) {
                                            if (callback != null) callback.onComplete(migrated[0]);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("MigrationUtils", "Failed to migrate user " + uid, e);
                                        if (callback != null) callback.onError("Failed to migrate user " + uid + ": " + e.getMessage());
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        if (callback != null) callback.onError("Realtime DB read cancelled: " + error.getMessage());
                    }
                });
    }
}
