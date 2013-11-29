package com.playlist.playlist_generator;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PL_FileManager_Activity extends ListActivity {
    private File currentDirectory = new File("/");
    private ArrayList<DirectoryList> directoryEntries = new ArrayList<DirectoryList>();
    private PL_FM_BoxAdapter boxAdapter;
    private String PathToPL;
    MainActivity MainSample = new MainActivity();

    //when new activity starts it uses some layout
    @Override
    public void onCreate(Bundle iNew) {
        super.onCreate(iNew);
        setContentView(R.layout.activity_pl_file_manager);
        PathToPL = getIntent().getStringExtra("PathToPL");
        if (PathToPL.equals("False")){
            //browse to root directory
            browseTo(new File("/"));
        }
        else{
            //browse to last chosen directory
            browseTo(new File(PathToPL));
        }
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
                boxAdapter = new PL_FM_BoxAdapter(this, directoryEntries);
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
        TextView TitleManager = (TextView)findViewById(R.id.titleManager);
        PathToPL = TitleManager.getText().toString();
        Intent MainIntent = new Intent();

        MainIntent.putExtra("PathToPL",PathToPL);
        setResult(RESULT_OK, MainIntent);
        finish();
     }

    //when you clicked onto item
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //get selected file name
        DirectoryList selectedFileString = directoryEntries.get(position);

        //if we select ".." then go upper
        if(selectedFileString.getPath().equals("..")){
            upOneLevel();
        }
        else {
            //browse to clicked file or directory using browseTo()
            File clickedFile;
            clickedFile = new File(selectedFileString.getPath());
            if (clickedFile != null)
                browseTo(clickedFile);

        }
    }
}
