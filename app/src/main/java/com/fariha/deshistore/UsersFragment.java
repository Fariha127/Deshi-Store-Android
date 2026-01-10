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
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        btnRefresh = view.findViewById(R.id.btnRefresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        adapter = new UserListAdapter(userList);
        recyclerView.setAdapter(adapter);

        mDatabase = FirebaseFirestore.getInstance();

        btnRefresh.setOnClickListener(v -> loadUsers());

        loadUsers();

        return view;
    }

    private void loadUsers() {
        // Remove old listener if exists
        if (usersListener != null) {
            usersListener.remove();
        }
        
        // Use real-time listener for automatic updates from Firestore
        usersListener = mDatabase.collection("users")
                .whereEqualTo("userType", "User")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error loading users", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    userList.clear();
                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            User user = new User();
                            user.setUserId(document.getId());
                            user.setFullName(document.getString("fullName"));
                            user.setEmail(document.getString("email"));
                            user.setPhoneNumber(document.getString("phoneNumber"));
                            user.setCity(document.getString("city"));
                            userList.add(user);
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
