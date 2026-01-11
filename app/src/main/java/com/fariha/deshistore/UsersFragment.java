package com.fariha.deshistore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnRefresh;
    private UserListAdapter adapter;
    private List<User> userList;
    private FirebaseFirestore mDatabase;
    private ListenerRegistration usersListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        android.util.Log.d("UsersFragment", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        android.util.Log.d("UsersFragment", "Views found: recyclerView=" + (recyclerView != null) + ", btnRefresh=" + (btnRefresh != null));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        adapter = new UserListAdapter(userList);
        recyclerView.setAdapter(adapter);

        mDatabase = FirebaseFirestore.getInstance();
        android.util.Log.d("UsersFragment", "Firestore initialized: " + (mDatabase != null));

        btnRefresh.setOnClickListener(v -> loadUsers());

        loadUsers();

        return view;
    }

    private void loadUsers() {
        android.util.Log.d("UsersFragment", "Loading users from Firestore...");
        
        // Check authentication state
        com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        android.util.Log.d("UsersFragment", "Current user: " + (currentUser != null ? currentUser.getEmail() : "NULL - NOT AUTHENTICATED"));
        
        if (currentUser == null) {
            Toast.makeText(getContext(), "Error: Not authenticated!", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Remove old listener if exists
        if (usersListener != null) {
            usersListener.remove();
        }
        
        // First, load ALL users to debug what userType values exist
        mDatabase.collection("users")
                .get()
                .addOnSuccessListener(allDocs -> {
                    android.util.Log.d("UsersFragment", "=== ALL USERS DEBUG ===");
                    android.util.Log.d("UsersFragment", "Total documents in users collection: " + allDocs.size());
                    for (QueryDocumentSnapshot doc : allDocs) {
                        String userType = doc.getString("userType");
                        String email = doc.getString("email");
                        android.util.Log.d("UsersFragment", "Doc ID: " + doc.getId() + ", userType: '" + userType + "', email: " + email);
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("UsersFragment", "Failed to get all users: " + e.getMessage(), e);
                });
        
        // Use real-time listener for automatic updates from Firestore
        usersListener = mDatabase.collection("users")
                .whereEqualTo("userType", "User")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        android.util.Log.e("UsersFragment", "Error loading users: " + error.getMessage(), error);
                        Toast.makeText(getContext(), "Error loading users: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    
                    userList.clear();
                    if (queryDocumentSnapshots != null) {
                        android.util.Log.d("UsersFragment", "Found " + queryDocumentSnapshots.size() + " users with userType='User'");
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            User user = new User();
                            user.setUserId(document.getId());
                            user.setFullName(document.getString("fullName"));
                            user.setEmail(document.getString("email"));
                            user.setPhoneNumber(document.getString("phoneNumber"));
                            user.setCity(document.getString("city"));
                            userList.add(user);
                            android.util.Log.d("UsersFragment", "User: " + user.getFullName() + " Email: " + user.getEmail());
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Loaded " + userList.size() + " users", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove listener to prevent memory leaks
        if (usersListener != null) {
            usersListener.remove();
        }
    }
}
