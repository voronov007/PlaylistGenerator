package com.playlist.playlist_generator;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by PC_4i_7 on 11/18/13.
 */
public class PL_FileManager_Activity extends ListActivity {
    private File currentDirectory = new File("/");
    private ArrayList<DirectoryList> directoryEntries = new ArrayList<DirectoryList>();
    private BoxAdapter boxAdapter;
    MainActivity MainSample = new MainActivity();

    //when new activity starts it uses some layout
    @Override
    public void onCreate(Bundle iNew) {
        super.onCreate(iNew);
        setContentView(R.layout.activity_file_manager);
        //browse to root directory
        browseTo(new File("/"));
    }

    //browse to file or directory
    private void browseTo(final File aDirectory){
        //if we want to browse directory
        if (aDirectory.isDirectory()){
            //fill list with files from this directory
            if (aDirectory.canRead()){
                this.currentDirectory = aDirectory;
                fill(aDirectory.listFiles());

                //set titleManager text
                TextView titleManager = (TextView) findViewById(R.id.titleManager);
                titleManager.setText(aDirectory.getAbsolutePath());
                boxAdapter = new BoxAdapter(this, directoryEntries);
                // настраиваем список
                ListView lvMain = (ListView) findViewById(android.R.id.list);
                lvMain.setAdapter(boxAdapter);
            }
        }
    }
    //fill list
    private void fill(File[] files) {
        //clear list
        directoryEntries.clear();

        if (this.currentDirectory.getParent() != null){
            directoryEntries.add(new DirectoryList("..","..", R.drawable.ic_launcher, false));
        }
        //add every file into list
        for (File file : files) {
            if (file.canRead()){
                if(file.isFile()){
                    if(MainSample.trackChecker(file.getName())){
                        directoryEntries.add(new DirectoryList(file.getName(),file.getAbsolutePath(),R.drawable.ic_launcher, false));
                    }
                }
                else if(file.isDirectory() && file.canRead()){
                        directoryEntries.add(new DirectoryList(file.getName(),file.getAbsolutePath(),R.drawable.ic_launcher, false));
                }
            }
        }
    }

    //browse to parent directory
    private void upOneLevel(){
        if(this.currentDirectory.getParent() != null) {
            browseTo(currentDirectory.getParentFile());
        }
    }
    //When you click OK Button
    public void AddPathToList(View v){
        ArrayList<String> FoldersList = new ArrayList<String>();
        for(DirectoryList dl : boxAdapter.getBox()){
            //If item is checked then boolean wasChecked = true
            if(dl.wasChecked){
                FoldersList.add(dl.getPath());
            }
        }
        if (FoldersList.size()!=0){
            Intent MainIntent = new Intent();
            MainIntent.putExtra("ArrayMusicDirList", FoldersList);
            setResult(RESULT_OK, MainIntent);
            this.finish();

        }
        else{
            /*
            create dialog when no one where checked, but button OK was pressed
            It means to take current folder
            listener when OK button clicked
            */
            DialogInterface.OnClickListener OkMusicButtonListener = new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface arg0, int arg1) {
                    ArrayList<String> NewFoldersList = new ArrayList<String>();
                    TextView titleManager;
                    String FolderPath;

                    titleManager = (TextView) findViewById(R.id.titleManager);
                    FolderPath = titleManager.getText().toString();
                    NewFoldersList.add(FolderPath);

                    Intent MainIntent = new Intent();
                    MainIntent.putExtra("ArrayMusicDirList", NewFoldersList);
                    setResult(RESULT_OK, MainIntent);
                    finish();

                }
            };
            //listener when NO button clicked
            DialogInterface.OnClickListener CancelMusicButtonListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    //do nothing
                    //or add something you want
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please, choose folder..."); //title
            builder.setMessage("Do you want to add to a list current folder?"); //message
            builder.setPositiveButton("Yes", OkMusicButtonListener); //positive button
            builder.setNegativeButton("No", CancelMusicButtonListener); //negative button
            builder.show(); //show dialog
        }
    }

    //when you clicked onto item
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //get selected file name
        DirectoryList selectedFileString= directoryEntries.get(position);

        //if we select ".." then go upper
        if(selectedFileString.getPath().equals("..")){
            upOneLevel();
        } else {
            //browse to clicked file or directory using browseTo()
            File clickedFile = null;
            clickedFile = new File(selectedFileString.getPath());
            if (clickedFile != null)
                browseTo(clickedFile);
        }
    }
}