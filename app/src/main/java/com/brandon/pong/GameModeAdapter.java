package com.brandon.pong;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by Brandon on 11/11/16.
 */

class GameModeAdapter extends RecyclerView.Adapter<GameModeAdapter.ViewHolder> {

    private List<String> gameModes;
    GameModeAdapter(ArrayList<String> gameModes){
        this.gameModes = gameModes;
    }

    @Override
    public GameModeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.game_mode_card, parent, false);
        return new GameModeAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(GameModeAdapter.ViewHolder holder, final int position) {
        TextView textView = (TextView)holder.cardView.findViewById(R.id.game_mode_text);
        textView.setText(gameModes.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                switch (position){
                    case 0:
                        intent.putExtra(MainActivity.GAME_TYPE, MainActivity.SINGLE_PLAYER);
                        break;
                    case 1:
                        intent.putExtra(MainActivity.GAME_TYPE, MainActivity.MONKEY);
                        break;
                    case 2:
                        intent.putExtra(MainActivity.GAME_TYPE, MainActivity.TWO_BALL);
                        break;
                    case 3:
                        intent.putExtra(MainActivity.GAME_TYPE, MainActivity.HARDCORE);
                        break;
                }
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameModes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;

        ViewHolder(CardView v) {
            super(v);
            this.cardView = v;
        }
    }
}
