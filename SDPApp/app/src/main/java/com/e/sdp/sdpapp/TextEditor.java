package com.e.sdp.sdpapp;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TextEditor extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.text_editor_back_btn) Button backBtn;
    @Bind(R.id.text_editor_edtxtview) EditText textEditorEditText;
    @Bind(R.id.text_editor_save_btn) Button saveBtn;


    private static final String CLASSKEY = "classKey";


    private String classId;

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/helpsNote";
    private String pathToTextFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);
        ButterKnife.bind(this);
        backBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

        classId = getIntent().getStringExtra(CLASSKEY);

        pathToTextFile = path + "/" + classId + ".txt";

        //each past booking is unique
        //with the combination of class id and booking id
        File file = new File(pathToTextFile);
        if(file.exists()) {
            loadText();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.text_editor_back_btn) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        //save to file
        else {
            saveText();
        }
    }

    private void saveText() {
        File folder = new File(path);

        if(!folder.exists()) {
            if(!folder.mkdir()){
                Toast.makeText(TextEditor.this, "fail to create file", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        File file = new File(pathToTextFile);
        String[] saveText = String.valueOf(textEditorEditText.getText()).split(System.getProperty("line.separator"));
        save(file, saveText);

    }

    private void loadText() {
        File file = new File(pathToTextFile);
        String[] loadText = load(file);

        String finalText = "";

        for(int i = 0; i < loadText.length; i++) {
            finalText += loadText[i] + System.getProperty("line.separator");
        }

        textEditorEditText.setText(finalText);

    }


    private void save(File file, String[] data)
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        try
        {
            try
            {
                for (int i = 0; i<data.length; i++)
                {
                    fos.write(data[i].getBytes());
                    if (i < data.length-1)
                    {
                        fos.write("\n".getBytes());
                    }
                }
            }
            catch (IOException e) {e.printStackTrace();}
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch (IOException e) {e.printStackTrace();}
        }

    }

    private String[] load(File file)
    {
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        String test;
        int anzahl=0;
        try
        {
            while ((test=br.readLine()) != null)
            {
                anzahl++;
            }
        }
        catch (IOException e) {e.printStackTrace();}

        try
        {
            fis.getChannel().position(0);
        }
        catch (IOException e) {e.printStackTrace();}

        String[] array = new String[anzahl];

        String line;
        int i = 0;
        try
        {
            while((line=br.readLine())!=null)
            {
                array[i] = line;
                i++;
            }
        }
        catch (IOException e) {e.printStackTrace();}
        return array;
    }
}
