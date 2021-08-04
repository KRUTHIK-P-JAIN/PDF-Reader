package com.example.pdfreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
//import com.pspdfkit.ui.PdfActivity

public class ViewPDF extends AppCompatActivity {

    PDFView pdfView;
    int position = -1;
    TextView name;
    File file;
    TextToSpeech textToSpeech;

    Boolean paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        getSupportActionBar().hide();
        /*View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rulme, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);*/

        pdfView = findViewById(R.id.pdfView);
        position = getIntent().getIntExtra("position", -1);

        final ImageView back = findViewById(R.id.back);
        ImageView share = findViewById(R.id.share);
        ImageView txt_sph = findViewById(R.id.txt_sph);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                // if No error is found then only it will run
                if (i != TextToSpeech.ERROR) {
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.US);
                    //Toast.makeText(ViewPDF.this, "good", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if (MainActivity.searchText.isEmpty()) {
            file = MainActivity.fileList.get(position);
        } else file = MainActivity.fileList1.get(position);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        txt_sph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    PdfDocument pdfDocument = new PdfDocument(file.getAbsolutePath(),null);
                }*/

                //Toast.makeText(ViewPDF.this, "Txt-Sph", Toast.LENGTH_SHORT).show();

                if (!textToSpeech.isSpeaking()) {
                    try {

                        Toast.makeText(ViewPDF.this, "starting...", Toast.LENGTH_LONG).show();

                        // creating a variable for pdf reader
                        // and passing our PDF file in it.
                        PdfReader reader = new PdfReader(file.toString());

                        // below line is for getting number
                        // of pages of PDF file.
                        int n = reader.getNumberOfPages();
                        String extractedText = "";

                        // running a for loop to get the data from PDF
                        // we are storing that data inside our string.
                        TextView tv = findViewById(R.id.tv);
                        for (int i = 0; i < n; i++) {
                            extractedText = PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";
                            // to extract the PDF content from the different pages
                            //tv.setText(extractedText);
                            textToSpeech.speak(extractedText, TextToSpeech.QUEUE_FLUSH, null);
                            // below line is used for closing reader.
                            reader.close();

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    textToSpeech.stop();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //File file = new File(MainActivity.fileList.get(position).toString());

                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("appliction/pdf"); // bcoz according to type sharing app options will display
                intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.toString()));
                startActivity(Intent.createChooser(intentShare, "Share the file..."));
            }
        });

        name = findViewById(R.id.name);

        displayPDF();
    }

    private void talk(String substring) {
        textToSpeech.speak(substring, TextToSpeech.QUEUE_FLUSH, null);
    }


    private void displayPDF() {
        name.setText(file.getName().replace(".pdf", ""));
        pdfView.fromFile(file)
                .enableSwipe(true)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (textToSpeech.isSpeaking())
            textToSpeech.stop();
    }
}
