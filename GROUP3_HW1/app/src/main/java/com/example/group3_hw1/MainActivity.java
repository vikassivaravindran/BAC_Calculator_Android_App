package com.example.group3_hw1;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public double weight;
    public double genderValue;
    public int drinkSize;
    public double alcoholQuantity;
    public double bacLevel = 0.00;
    EditText weight_Input;
    RadioGroup alcoholGroup;
    RadioButton alcoholContent;
    SeekBar getAlcoholContent;
    TextView showProgress;
    int progressRange = 5;
    public final double bacConstant = 6.24;
    TextView BacLevelStatus;
    TextView resultStatus;
    public static final String message_safe = "You're Safe";
    public static final String message_careful = "Be careful";
    public static final String message_limit = "Over the limit!";
    int progressBarValue;
    Button resetButton;
    Button saveButton;
    Button addDrinkButton;
    Switch genderSwitch;
    ProgressBar getProgress;
    boolean status = false;
    boolean enableaddDrink =false;
    List<Integer> drinkListSize = new ArrayList<>();
    List<Double> alcoholContentList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.drinks);
        setTitle(R.string.app_name);


        addDrinkButton = findViewById(R.id.add_drink);
        getProgress = findViewById(R.id.progressBar);
        genderSwitch = (Switch) findViewById(R.id.Toggle_Switch);
        saveButton = findViewById(R.id.save_button);
        weight_Input = findViewById(R.id.weight_Input);
        resultStatus = findViewById(R.id.result_Value);
        resultStatus.setBackgroundColor(Color.rgb(0, 128, 0));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableaddDrink = true;

                if(genderSwitch.isChecked()){
                    genderValue = 0.68;
                }else{
                    genderValue = 0.55;
                }
                if (weight_Input.getText().length() == 0) {
                    weight_Input.setError("Please enter a positive weight value");
                    enableaddDrink = false;
                } else {
                    try {
                        weight = Double.parseDouble(weight_Input.getText().toString());
                        Log.d("Weight - Save Button",""+weight);
                        if (weight < 0) {
                            weight_Input.setError("Enter the weight in lbs");
                        }
                    } catch (Exception e) {
                        weight_Input.setError("Enter the weight in lbs");
                        enableaddDrink = false;
                    }
                }

                    //addDrinkButton.performClick();
                    recalculateBac();

            }
        });

        getAlcoholContent = (SeekBar)findViewById(R.id.seekBar_alcohol);
        getAlcoholContent.setProgress(5);
        showProgress = findViewById(R.id.show_Progress);

        getAlcoholContent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = ((int)Math.round(progress/progressRange))*progressRange;
                showProgress.setText(String.valueOf(progress)+"%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        addDrinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enableaddDrink) {
                    //status = true;
                    boolean flag = false;

                    if (weight_Input.getText().length() == 0) {
                        weight_Input.setError("Please enter a positive weight value");
                        flag = true;
                    } else {
                        try {
                            weight = Double.parseDouble(weight_Input.getText().toString());
                            Log.d("Weight - Add Drink",""+weight);
                            if (weight < 0) {
                                weight_Input.setError("Enter the weight in lbs");
                            }
                        } catch (Exception e) {
                            weight_Input.setError("Enter the weight in lbs");
                            flag = true;
                        }
                    }
                    if (!flag) {
                        Log.d("Running inside","!flag");
                        alcoholGroup = findViewById(R.id.radio_group);
                        int selectedChoice = alcoholGroup.getCheckedRadioButtonId();
                        alcoholContent = (RadioButton) findViewById(selectedChoice);

                        drinkSize = Integer.parseInt(alcoholContent.getText().toString().replace("Oz", "").trim());
                        alcoholQuantity = Double.parseDouble(showProgress.getText().toString().replace("%", "").trim());
                        drinkListSize.add(drinkSize);
                        alcoholContentList.add(alcoholQuantity);
                        Log.d("Gender", "" + genderValue);
                        bacLevel = bacLevel + (drinkSize * (alcoholQuantity / 100) * bacConstant) / (weight * genderValue);
                        bacLevel = Math.round(bacLevel * 100.00) / 100.0;
                        BacLevelStatus = findViewById(R.id.Bac_Level);


                        BacLevelStatus.setText("BAC Level: " + bacLevel);


                        progressBarValue = (int) ((bacLevel * Math.pow(10, 4)) / 100);
                        Log.d("Values are", "" + progressBarValue);
                        getProgress.setProgress(progressBarValue);


                        if (bacLevel <= 0.08) {
                            resultStatus.setText(message_safe);
                            resultStatus.setBackgroundColor(Color.rgb(0, 128, 0));
                        } else if (bacLevel > 0.08 && bacLevel < 0.20) {
                            resultStatus.setText(message_careful);
                            resultStatus.setBackgroundColor(Color.rgb(255, 165, 0));
                        } else if (bacLevel >= 0.20 && bacLevel < 0.25) {
                            resultStatus.setText(message_limit);
                            resultStatus.setBackgroundColor(Color.rgb(255, 0, 0));
                        } else if (bacLevel >= 0.25) {
                            resultStatus.setText(message_limit);
                            resultStatus.setBackgroundColor(Color.rgb(255, 0, 0));
                            Toast.makeText(getApplicationContext(), "No more drinks for you", Toast.LENGTH_SHORT).show();
                            saveButton.setEnabled(false);
                            addDrinkButton.setEnabled(false);
                        }
                    }
                }
            }
        });

        resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bacLevel == 0.00){
                    return;
                }
                setDefaultValues();
            }
        });
    }

    public void recalculateBac(){


        bacLevel = 0.00;
        Log.d("Weight - Recalculate",""+weight+" "+bacLevel);
        for(int i=0;i<alcoholContentList.size();i++) {
            bacLevel = bacLevel + (drinkListSize.get(i) * (alcoholContentList.get(i) / 100) * bacConstant) / (weight * genderValue);
        }
        bacLevel = Math.round(bacLevel * 100.00) / 100.0;
        BacLevelStatus = findViewById(R.id.Bac_Level);



        BacLevelStatus.setText("BAC Level: " + bacLevel);


        progressBarValue = (int) ((bacLevel * Math.pow(10, 4)) / 100);
        Log.d("Values are", "" + progressBarValue);
        getProgress.setProgress(progressBarValue);


        if (bacLevel <= 0.08) {
            resultStatus.setText(message_safe);
            resultStatus.setBackgroundColor(Color.rgb(0, 128, 0));
        } else if (bacLevel > 0.08 && bacLevel < 0.20) {
            resultStatus.setText(message_careful);
            resultStatus.setBackgroundColor(Color.rgb(255, 165, 0));
        } else if (bacLevel >= 0.20 && bacLevel < 0.25) {
            resultStatus.setText(message_limit);
            resultStatus.setBackgroundColor(Color.rgb(255, 0, 0));
        } else if (bacLevel >= 0.25) {
            resultStatus.setText(message_limit);
            resultStatus.setBackgroundColor(Color.rgb(255, 0, 0));
            Toast.makeText(getApplicationContext(), "No more drinks for you", Toast.LENGTH_SHORT).show();
            saveButton.setEnabled(false);
            addDrinkButton.setEnabled(false);
        }

    }

    public void setDefaultValues(){
        bacLevel = 0.00;
        status = false;
        BacLevelStatus.setText(R.string.BAC_Level);
        resultStatus.setText(R.string.result_Value);
        resultStatus.setBackgroundColor(Color.rgb(0,128,0));
        weight_Input.setText("");
        genderSwitch.setChecked(true);
        getProgress.setProgress(0);
        alcoholContent = findViewById(R.id.radioButton4);
        alcoholContent.setChecked(true);
        getAlcoholContent.setProgress(5);
        saveButton.setEnabled(true);
        addDrinkButton.setEnabled(true);
        drinkListSize = new ArrayList<>();
        alcoholContentList = new ArrayList<>();
    }
}
