package com.example.aquaticanimals.quiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aquaticanimals.R;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.MyViewHolder> {
    private Context mContext;
    private List<Quiz> quizList;

    public QuizAdapter(Context mContext, List<Quiz> quizList) {
        this.mContext = mContext;
        this.quizList = quizList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_quiz, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Quiz quiz = quizList.get(position);

        holder.quizName.setText(quiz.getQuizName());
        holder.numOfQues.setText(quiz.getNumOfQues() + " questions");
        holder.timeLimit.setText(quiz.getTimeLimit() + " minutes");
        //holder.date.setText(quiz.getDate());

        Glide.with(mContext)
                .load("https://source.unsplash.com/9gz3wfHr65U")
                .centerCrop()
                .circleCrop()
                .into(holder.image);

        /*holder.navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView image;
        public TextView quizName, numOfQues, timeLimit, date;
        public Button takeQuizBtn;

        public MyViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.image);
            quizName = view.findViewById(R.id.quizName);
            numOfQues = view.findViewById(R.id.numOfQues);
            timeLimit = view.findViewById(R.id.timeLimit);
            takeQuizBtn = view.findViewById(R.id.takeQuizBtn);
            //date = view.findViewById(R.id.date);
        }
    }
}
