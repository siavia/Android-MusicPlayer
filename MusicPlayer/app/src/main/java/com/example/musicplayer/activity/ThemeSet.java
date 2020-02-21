package com.example.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;

public class ThemeSet extends AppCompatActivity {

    private Button btn_submit;
    private RadioGroup group;
    private RadioButton button_blue, button_pink, button_green;
    private String string_theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_setting);

        group = this.findViewById(R.id.group);
        button_blue = (RadioButton) group.getChildAt(0);
        button_pink = (RadioButton) group.getChildAt(1);
        button_green = (RadioButton) group.getChildAt(2);
        btn_submit = (Button) this.findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (button_blue.isChecked()) {
                    intent.putExtra("theme","blue");
                    setResult(1,intent);
                    finish();
                }
                else if (button_pink.isChecked()) {
                    intent.putExtra("theme","pink");
                    setResult(1,intent);
                    Log.i("themeset", "onClick: 已经选择粉色");
                    finish();
                }
                else if (button_green.isChecked()) {
                    intent.putExtra("theme","green");
                    setResult(1,intent);
                    finish();
                }
                else{
                    Toast.makeText(ThemeSet.this, "请选择主题颜色", Toast.LENGTH_SHORT).show();
                }

            }
        });

//
//        btn_blue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                MainActivity.string_theme = "blue";
////                Intent intent = new Intent(ThemeSet.this,MainActivity.class);
////                startActivity(intent);
//                Intent intent = new Intent();
//                intent.putExtra("theme","blue");
//                setResult(1,intent);
//            }
//        });
//        btn_pink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                MainActivity.string_theme = "pink";
////                Intent intent = new Intent(ThemeSet.this,MainActivity.class);
////                startActivity(intent);
//                Intent intent = new Intent();
//                intent.putExtra("theme","pink");
//                setResult(1,intent);
//            }
//        });
//        btn_blue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                MainActivity.string_theme = "green";
////                Intent intent = new Intent(ThemeSet.this,MainActivity.class);
////                startActivity(intent);
//                Intent intent = new Intent();
//                intent.putExtra("theme","green");
//                setResult(1,intent);
//            }
//        });


    }


}
