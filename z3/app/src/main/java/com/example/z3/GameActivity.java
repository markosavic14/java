package com.example.z3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameActivity extends AppCompatActivity {

    final int NUMBER_OF_ROWS = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button[] button = new Button[NUMBER_OF_ROWS];
        ImageView[] iv = new ImageView[NUMBER_OF_ROWS];


        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            button[i] = findViewById(getResources().getIdentifier("button" + i, "id", getPackageName()));
            iv[i] = findViewById(getResources().getIdentifier("iv" + i + i, "id", getPackageName()));
            button[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO find lowest not activated imageview and set its fill to its colour
                    iv[0].setBackgroundColor(getResources().getColor(R.color.black));
                }
            });
        }
    }

    private int ge_lowest_disk(){
        return 0;
    }
}