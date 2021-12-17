package com.example.afinal.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.afinal.Model.Card;
import com.example.afinal.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainPage extends AppCompatActivity {
    AnimatorSet front_anim, back_anim;
    float xDown = 0, yDown = 0;
    List<Integer> listZLayer;
    boolean checkMove = false;
    long pressStartTime;
    ArrayList<Card> Cards;
    Button main_shuffle, main_back;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findId();
        Cards = new ArrayList<>();
        ArrayList<Integer> drawables = getimage();
        addLayout(drawables);


    }
    private void findId() {
        main_shuffle = findViewById(R.id.main_shuffle);
        main_back = findViewById(R.id.main_back);
        bindButton();
    }
    private void bindButton() {
        main_back.setOnClickListener(v -> startActivity(new Intent(MainPage.this, LoginPage.class)));
        main_shuffle.setOnClickListener(v -> {
            int i = 0;
            Collections.shuffle(listZLayer);
            for(Card card : Cards) {
                RelativeLayout view = card.getLayout();
                view.setX(0);
                view.setY(0);
                View front = view.getChildAt(0);
                View back = view.getChildAt(1);
                front.setAlpha(0);
                back.setAlpha(1);
                card.setFront(false);
                view.setTranslationZ(listZLayer.get(i));
                i++;
            }
        });
    }
    public void addLayout(ArrayList<Integer> drawables) {
        AbsoluteLayout main_layout = findViewById(R.id.main_layout);
        int i = 0;
        listZLayer = buildList(78);
        for(Integer image : drawables) {
            RelativeLayout view = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.card_layout, null);
            view.setId(i);
            main_layout.addView(view);
            view.setTranslationZ(listZLayer.get(i));
            Cards.add(new Card(view, false));
            bindView(view, image);
            i++;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void bindView(RelativeLayout view, Integer Image) {
        View front = view.getChildAt(0);
        View back = view.getChildAt(1);
        front.setBackgroundResource(Image);
        float scale = this.getResources().getDisplayMetrics().density;
        front.setCameraDistance(8000 * scale);
        back.setCameraDistance(8000 * scale);
        // set animation
        front_anim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.front_animator);
        back_anim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.back_animator);

        view.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                {
                    checkMove = false;
                    float movedX, movedY;
                    movedX = event.getX();
                    movedY = event.getY();

                    float distanceX = movedX - xDown;
                    float distanceY = movedY - yDown;
                    view.setX(view.getX() + distanceX);
                    view.setY(view.getY() + distanceY);
                    break;
                }
                case MotionEvent.ACTION_DOWN:
                {
                    pressStartTime = System.currentTimeMillis();
                    if(!Cards.get(view.getId()).isFront()) {
                        checkMove = false;
                        xDown = event.getX();
                        yDown = event.getY();
                    }
                    break;
                }
                case MotionEvent.ACTION_UP:
                {
                    long Endtime = System.currentTimeMillis() - pressStartTime;
                    if(!checkMove && Endtime < 100) {
                        if(!Cards.get(view.getId()).isFront()) {
                            front_anim.setTarget(back);
                            back_anim.setTarget(front);
                            front_anim.start();
                            back_anim.start();
                            Cards.get(view.getId()).setFront(true);
                        }
                    }
                    break;
                }
            }
            return true;
        });
    }
    public ArrayList<Integer> getimage() {
        Field[] drawablesFields = R.drawable.class.getFields();
        ArrayList<Integer> drawables = new ArrayList<>();
        for(Field field : drawablesFields) {
            try {
                if(field.getName().length()<=3) {
                    if(!field.getName().equals("bb")){
                        drawables.add(field.getInt(null));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return drawables;
    }

    private List<Integer> buildList(int maximum) {
        List<Integer> list = new ArrayList<>();

        for(int j = 0; j< maximum;j++) {
            list.add(j);
        }
        Collections.shuffle(list);
        return list;
    }

}