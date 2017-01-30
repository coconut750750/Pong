package com.brandon.pong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class GameModesFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    GameModeAdapter gameModeAdapter;
    Button menuButton;

    public GameModesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_modes, container, false);

        //getting recycler view and adapter
        mRecyclerView = (RecyclerView) view.findViewById(R.id.modes_recycler);
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        gameModeAdapter = new GameModeAdapter(new ArrayList<String>(){{
            add("Classic");
            add("Monkey in the Middle");
            add("Twice the Fun");
            add("Hardcore");
        }});

        mRecyclerView.setAdapter(gameModeAdapter);

        gameModeAdapter.notifyDataSetChanged();

        menuButton = (Button)view.findViewById(R.id.button_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(GameModesFragment.this).commit();
            }
        });

        return view;
    }
}
