package com.example.pdfreader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ListView pdfList;
    public static ArrayList<File> fileList = new ArrayList<>();
    public static ArrayList<File> fileList1;
    PDF_Adapter pdf_adapter, pdf_adapter1;
    public static int REQUEST_PERMISSION = 1;
    boolean permission;
    File directory; // file manager
    TextView sample;
    String str;
    Boolean renamed = false;
    Boolean deleted = false;
    public static String searchText = "";
    SwipeRefreshLayout swipeRefresh;
    ProgressBar refresh;
    LinearLayout splash;
    String newNamee;
    //File listFile[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pdfList = findViewById(R.id.pdfList);
        splash = findViewById(R.id.splash);
        refresh = findViewById(R.id.refresh);
        sample = findViewById(R.id.sample);
        directory = new File(Environment.getExternalStorageDirectory().toString()); // to access content stored on shared/external storage (file manager)
        //sample.setText(directory.getName());
        swipeRefresh = findViewById(R.id.swiperefresh);

        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ask_permissions();
                splash.setVisibility(View.GONE);
                getSupportActionBar().show();
            }
        }, 1000);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refresh.setVisibility(View.VISIBLE);
                //ask_permissions();
                swipeRefresh.setRefreshing(false);
                refresh.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "refreshing...", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (refresh.getVisibility() == View.VISIBLE) {
                            ask_permissions();
                            refresh.setVisibility(View.GONE);
                        }
                    }
                }, 1000);
            }
        });

        pdfList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ViewPDF.class);
                intent.putExtra("position", position);
                startActivity(intent);

            }
        });



        /*EditText search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                MainActivity.this.pdf_adapter.getFilter().filter(charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
        final SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(this);
        //pdfList.setAdapter(pdf_adapter);

        pdfList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v, final int index, long arg3) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final View dialog_box = getLayoutInflater().inflate(R.layout.rename_delete, null); //creating reference of dialog box(layout file) using inflater
                builder.setView(dialog_box); //setting up referenced view on builder

                final File oldFile = new File(pdfList.getItemAtPosition(index).toString());

                final AlertDialog dialog = builder.create();
                CardView rename = dialog_box.findViewById(R.id.rename);
                rename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

                        final AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        final View dialog_box1 = getLayoutInflater().inflate(R.layout.rename, null); //creating reference of dialog box(layout file) using inflater
                        builder1.setView(dialog_box1); //setting up referenced view on builder

                        final TextInputEditText newName = dialog_box1.findViewById(R.id.newName);
                        newName.setText(oldFile.getName().replace(".pdf", ""));

                        final AlertDialog dialog1 = builder1.create();
                        dialog_box1.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (!newName.getText().toString().trim().isEmpty()) {
                                    newNamee = newName.getText().toString();
                                    dialog1.dismiss();
                                    dialog.dismiss();
                                    refresh.setVisibility(View.VISIBLE);

                                    Boolean rename = true;
                                    String d = "";
                                    try {
                                        File check[] = new File(oldFile.getParent()).listFiles();
                                        for (int i = 0; i < check.length; i++) {
                                            //d += check[i].getName() + " ,  ";
                                            if (check[i].getName().equals(newNamee + ".pdf")) {
                                                rename = false;
                                                break;
                                            }
                                        }

                                        if (rename) {
                                            renamed = oldFile.renameTo(new File(oldFile.getCanonicalPath().replace(oldFile.getName(), "") + newNamee + ".pdf"));

                                            if (renamed) {
                                                //dialog_box1.setVisibility(View.GONE);
                                                /*fileList.clear();
                                                if(getFile(directory)) {
                                                    pdf_adapter = new PDF_Adapter(getApplicationContext(), fileList);
                                                    pdfList.setAdapter(pdf_adapter);*/

                                                //refresh.setVisibility(View.VISIBLE);
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ask_permissions();
                                                        searchView.onActionViewCollapsed();
                                                        Toast.makeText(MainActivity.this, oldFile.getName() + " renamed successfully to " + newNamee + ".pdf", Toast.LENGTH_LONG).show();
                                                        refresh.setVisibility(View.GONE);
                                                    }
                                                },1000);
                                                //}
                                            } else
                                                Toast.makeText(MainActivity.this, "File not renamed.", Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(MainActivity.this, "This file already exist.", Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //sample.setText(d + " " + rename);
                                }
                                //refresh.setVisibility(View.GONE);
                            }
                        });

                        //builder1.setNegativeButton("Cancel", null);
                        dialog_box1.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog1.dismiss();
                                dialog.dismiss();
                            }
                        });


                        dialog1.show();

                    }
                });

                CardView delete = dialog_box.findViewById(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                        builder2.setMessage("Are you sure?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        refresh.setVisibility(View.VISIBLE);
                                        if (oldFile.delete()) {
                                            /*fileList.clear();
                                            if(getFile(directory)) {
                                                pdf_adapter = new PDF_Adapter(getApplicationContext(), fileList);
                                                pdfList.setAdapter(pdf_adapter);*/
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ask_permissions();
                                                    searchView.onActionViewCollapsed();
                                                    Toast.makeText(MainActivity.this, "" + oldFile.getName() + " successfully deleted", Toast.LENGTH_SHORT).show();
                                                    refresh.setVisibility(View.GONE);
                                                }
                                            },1000);
                                            deleted = true;
                                        }
                                    }
                                })
                                .setNegativeButton("No", null)
                                .create().show();
                    }
                });


                CardView close = dialog_box.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                //builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                return true;
            }
        });
        //registerForContextMenu(pdfList);

    }

    private void ask_permissions() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        } else {
            permission = true;
            if (!fileList.isEmpty())
                fileList.clear();
            getFile(directory);
            pdf_adapter = new PDF_Adapter(getApplicationContext(), fileList);
            pdfList.setAdapter(pdf_adapter);
            //refresh.setVisibility(View.GONE);
            //swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permission = true;
                getFile(directory);
                pdf_adapter = new PDF_Adapter(getApplicationContext(), fileList);
                pdfList.setAdapter(pdf_adapter);
            } else {
                Toast.makeText(this, "Please Allow the Permission", Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }

            if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                /*permission = true;
                getFile(directory);
                pdf_adapter = new PDF_Adapter(getApplicationContext(), fileList);
                pdfList.setAdapter(pdf_adapter);*/
            } else {
                Toast.makeText(this, "Please Allow the Permission", Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        }
    }

    public boolean getFile(File directory) {
        String a = "";
        File[] listFile = directory.listFiles();
        //Arrays.sort(listFile);
        /*
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) // checks is it a folder
                    getFile(listFile[i]);

                else {
                    boolean booleanPdf = false;
                    if (listFile[i].getName().endsWith(".pdf")) {
                        for (int j = 0; j < fileList.size(); j++) {
                            if (fileList.get(j).getName().equals(listFile[j].getName()))
                                booleanPdf = true;
                            else {
                            }

                        }
                        if (booleanPdf)
                            booleanPdf = false;

                        else
                            fileList.add(listFile[i]);
                    }
                }
            }
        }*/

        //return fileList;

        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) { // checks is it a folder
                    getFile(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".pdf")) {
                        fileList.add(listFile[i]);
                        //Arrays.sort(new ArrayList[]{fileList});
                    }
                }
            }
        }

        String arr[] = new String[fileList.size()];
        for (int i = 0; i < fileList.size(); i++)
            arr[i] = fileList.get(i).getName();
        Arrays.sort(arr);
        for (int i = 0; i < fileList.size(); i++)
            a += arr[i] + " , ";

        // sorting
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < fileList.size(); j++) {
                if (fileList.get(j).getName().equals(arr[i])) {
                    fileList.add(i, fileList.get(j));
                    fileList.remove(j);
                    arr[j].replace(arr[j], "");
                    break;
                }
            }
        }
        sample.setText("Total PDF files: " + fileList.size());
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        searchText = s;
        //sample.setText(s);
        File fileName;
        fileList1 = new ArrayList<>(); // created new bcoz in fileList have all pdf or getFile() gives all pdf in given directory which we don't need,
        // here we need pdf name starts with searchText
        for (int i = 0; i < fileList.size(); i++) {
            fileName = fileList.get(i);
            if (fileName.getName().toLowerCase().startsWith(s.toLowerCase()))
                fileList1.add(fileName);
        }
        pdf_adapter1 = new PDF_Adapter(getApplicationContext(), fileList1);
        pdfList.setAdapter(pdf_adapter1);


        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refreshh) {
            refresh.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "refreshing...", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (refresh.getVisibility() == View.VISIBLE) {
                        ask_permissions();
                        refresh.setVisibility(View.GONE);
                    }
                }
            }, 1000);
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rename_delete, menu);
        menu.setHeaderTitle(Html.fromHtml("<h1>Select Action</h1> "v.findViewById(R.id.pdfName).getTag().toString()));

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.rename) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            // dialog box on MainActivity

            builder.setMessage("Rename File"); //displays message

            // set the custom layout
            View dialog_box = getLayoutInflater().inflate(R.layout.rename, null); //creating reference of dialog box(layout file) using inflater
            builder.setView(dialog_box); //setting up referenced view on builder

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    TextInputEditText newName = findViewById(R.id.newName);
                    newName.getText().toString();
                }
            });

            builder.setNegativeButton("Cancel", null);

            builder.create().show();

        } else if (item.getItemId() == R.id.delete) {

        } else
            return false;

        return true;
    }*/
}
