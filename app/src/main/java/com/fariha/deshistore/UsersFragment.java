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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnRefresh;
    private UserListAdapter adapter;
    private List<User> userList;
    private DatabaseReference mDatabase;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnRefresh.setOnClickListener(v -> loadUsers());

        loadUsers();

        return view;
    }

    private void loadUsers() {
        mDatabase.child("users").orderByChild("userType").equalTo("User")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User user = new User();
                            user.setUserId(userSnapshot.getKey());
                            user.setFullName(userSnapshot.child("fullName").getValue(String.class));
                            user.setEmail(userSnapshot.child("email").getValue(String.class));
                            user.setPhoneNumber(userSnapshot.child("phoneNumber").getValue(String.class));
                            user.setCity(userSnapshot.child("city").getValue(String.class));
                            userList.add(user);
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Loaded " + userList.size() + " users", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error loading users", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
