package com.example.activedash.quest;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.activedash.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectQuestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectQuestFragment extends Fragment {
    RecyclerView questList;
    View rootView;
    DatabaseReference questRef;

    public SelectQuestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     * @return A new instance of fragment SelectQuestFragment.
     */
    public static SelectQuestFragment newInstance() {
        SelectQuestFragment fragment = new SelectQuestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       rootView = inflater.inflate(R.layout.fragment_select_quest, container, false);
       questList =  rootView.findViewById(R.id.questRecyclerView);

       questRef = FirebaseDatabase.getInstance().getReference().child("quest");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        questList.setHasFixedSize(true);
        questList.setLayoutManager(layoutManager);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Quest, QuestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Quest, QuestViewHolder>(
                Quest.class,
                R.layout.single_quest,
                QuestViewHolder.class,
                questRef
        ) {
            @Override
            protected void populateViewHolder(QuestViewHolder questViewHolder, final Quest quest, int i) {
                final String keyId = getRef(i).getKey();
                    questViewHolder.setTitle(quest.getName());
                    questViewHolder.setDescription(quest.getDescription(),quest.getPoint(), quest.getSteps());
                    questViewHolder.playButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            QuestScoreCalculationFragment questScoreCalculationFragment= QuestScoreCalculationFragment.newInstance(keyId,quest.getName(),quest.getDescription(), quest.getSteps(),quest.getPoint());
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.questContainerFragment, questScoreCalculationFragment);
                            fragmentTransaction.commit();
                        }
                    });
            }
        };
        questList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class QuestViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView titlenameTv, descriptionTv;
        ImageButton playButton;
        public QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            titlenameTv = itemView.findViewById(R.id.titleTextView);
            descriptionTv = itemView.findViewById(R.id.descriptionTextView);
            playButton = itemView.findViewById(R.id.playButton);
        }

        public void setTitle(String titleName){
            titlenameTv.setText(titleName);
        }

        public void setDescription(String description, int coin, int steps){
            String text = description+" \n Coins: "+coin+" \n Steps: "+steps;
            descriptionTv.setText(text);
        }
    }
}
