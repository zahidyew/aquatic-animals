package com.example.aquaticanimals.quiz;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aquaticanimals.R;
import com.example.aquaticanimals.questions.QuestionPage;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.MyViewHolder> {
    private Context mContext;
    private List<Quiz> quizList;
    private Activity activity;
    private String username;

    public QuizAdapter(Activity activity, Context mContext, List<Quiz> quizList) {
        this.activity = activity;
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
        String imageURL;

        if(position % 3 == 0) {
            imageURL = "https://source.unsplash.com/3Xd5j9-drDA";
        } else if(position % 2 == 0) {
            imageURL = "https://source.unsplash.com/Me7ySkVmWcw";
        } else {
            imageURL = "https://source.unsplash.com/pCMsbkittX8";
        }

        holder.quizName.setText(quiz.getQuizName());
        holder.numOfQues.setText(quiz.getNumOfQues() + " questions");
        holder.timeLimit.setText(quiz.getTimeLimit() + " minutes");
        //holder.date.setText(quiz.getDate());

        Glide.with(mContext)
                .load(imageURL)
                .centerCrop()
                .circleCrop()
                .into(holder.image);

        // set clickListener for the button & pass the quiz's id,name,etc to the next page
        holder.takeQuizBtn.setOnClickListener(v -> {
            promptForUsername(quiz.getQuizId(), quiz.getQuizName(), quiz.getNumOfQues(), quiz.getTimeLimit()).show();
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // public CardView cardView;
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

    public Dialog promptForUsername(int quizId, String quizName, int numOfQues, int timeLimit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogBox = inflater.inflate(R.layout.dialog_box_name, null); // Pass null as the parent view because its going in the dialog
        EditText uName = dialogBox.findViewById(R.id.username);

        builder.setView(dialogBox)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        username = uName.getText().toString();

                        Intent intent = new Intent(mContext, QuestionPage.class);
                        intent.putExtra("quizId", quizId);
                        intent.putExtra("quizName", quizName);
                        intent.putExtra("numOfQues", numOfQues);
                        intent.putExtra("timeLimit", timeLimit);
                        intent.putExtra("username", username);

                        mContext.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing here. Clicking Cancel make Dialog Box disappear.
                    }
                });

        return builder.create();
    }
}
