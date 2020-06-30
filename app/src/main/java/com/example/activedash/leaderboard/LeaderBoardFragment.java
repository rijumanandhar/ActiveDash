package com.example.activedash.leaderboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.activedash.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LeaderBoardFragment extends Fragment {
    RecyclerView leaderBoardList;
    DatabaseReference leaderBoardRef;

    public LeaderBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leader_board, container, false);
        leaderBoardList = rootView.findViewById(R.id.leaderboardRecyclerView);
        leaderBoardRef = FirebaseDatabase.getInstance().getReference().child("leaderboard");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        leaderBoardList.setHasFixedSize(true);
        leaderBoardList.setLayoutManager(layoutManager);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<LeaderBoard, LeaderBoardViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<LeaderBoard, LeaderBoardViewHolder>(
                LeaderBoard.class,
                R.layout.single_leaderboard,
                LeaderBoardViewHolder.class,
                leaderBoardRef
        ) {
            @Override
            protected void populateViewHolder(LeaderBoardViewHolder leaderBoardViewHolder, LeaderBoard leader, int i) {
                leaderBoardViewHolder.setUsername(leader.getUsername());
                leaderBoardViewHolder.setPoint(leader.getPoint());
                leaderBoardViewHolder.setStep(leader.getStepcount());
            }
        };
        leaderBoardList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class LeaderBoardViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView usernameTv, pointsTv, stepTv ;
        public LeaderBoardViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            usernameTv = itemView.findViewById(R.id.leaderBoardUsername);
            pointsTv = itemView.findViewById(R.id.leaderBoardPoint);
            stepTv = itemView.findViewById(R.id.leaderBoardStep);
        }

        public void setUsername(String username){
            usernameTv.setText(username);
        }

        public void setPoint(int point){
            pointsTv.setText(point+"");
        }

        public void setStep(int step){
            stepTv.setText(step+"");
        }
    }
}
