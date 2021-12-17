package com.example.afinal.Model;

import android.widget.RelativeLayout;

public class Card {
    private RelativeLayout layout;
    private boolean isFront;

    public Card(RelativeLayout layout, boolean isFront) {
        this.layout = layout;
        this.isFront = isFront;
    }

    public RelativeLayout getLayout() {
        return layout;
    }

    public void setLayout(RelativeLayout layout) {
        this.layout = layout;
    }

    public boolean isFront() {
        return isFront;
    }

    public void setFront(boolean front) {
        isFront = front;
    }
}
