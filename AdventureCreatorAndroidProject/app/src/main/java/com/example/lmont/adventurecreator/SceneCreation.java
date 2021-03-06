package com.example.lmont.adventurecreator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;

import java.util.ArrayList;


public class SceneCreation extends AppCompatActivity {

    String storyId;
    String chapterId;

    EditText chapterTitleEditText;
    EditText chapterGoalEditText;
    EditText chapterSummaryEditText;

    String chapterTitle;
    String chapterGoal;
    String chapterSummary;

    Button addSceneButton;
    ListView sceneNodeListView;

    Models.Scene[] allScenesArray;
    ArrayList<String> allSceneTitles;
    ArrayList<String> allSceneIds;

    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_creation);

//        TODOne: Update db with changes to Title, Goal and Summary when exiting this event (or with new button?)
        chapterTitleEditText = (EditText) findViewById(R.id.chapterTitleEditText);
        chapterGoalEditText = (EditText) findViewById(R.id.chapterGoalEditText);
        chapterSummaryEditText = (EditText) findViewById(R.id.chapterSummaryEditText);

        addSceneButton = (Button) findViewById(R.id.addSceneButton);
        sceneNodeListView = (ListView) findViewById(R.id.sceneNodeListView);

        allSceneTitles = new ArrayList<>();
        allSceneIds = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allSceneTitles);

//        TODOne: Add new scene and pull for ID.
        addSceneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllTitlesAndIds();
                Models.Scene newScene = new Models.Scene(
                        "Scene " + (allScenesArray.length + 1),
                        "Journal Text",
                        "Flag Modifiers",
                        "Scene Body Text",
                        chapterId);
                addScene(newScene);
            }
        });

        sceneNodeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent intent = new Intent(SceneCreation.this, SceneEditor.class);

                intent.putExtra("storyId", storyId);
                intent.putExtra("chapterId", chapterId);
                intent.putExtra("selectedSceneId", allSceneIds.get(position));
////                TODO: Pass scene node type
////                intent.putExtra("sceneNodeType", ???);
//                intent.putExtra("selectedSceneTitle", allScenesArray[position].title);
//                intent.putExtra("selectedSceneJournalText", allScenesArray[position].journalText);
//                intent.putExtra("selectedSceneModifiers", allScenesArray[position].flagModifiers);
//                intent.putExtra("selectedSceneBodyText", allScenesArray[position].body);

                readChapterFormFields();
                Models.Chapter updatedChapter = new Models.Chapter(chapterTitle, chapterSummary, chapterGoal, storyId);
                updatedChapter._id = chapterId;

                updateChapter(updatedChapter);

                startActivity(intent);
            }
        });

        getChapterDetails();
        getAllFormFields();

        getAllTitlesAndIds();
        sceneNodeListView.setAdapter(arrayAdapter);
        updateListViewHeight(sceneNodeListView);
    }

    //        TODOne: Get data and populate list view with Scene titles
    public void getChapterDetails() {
        Intent chapterIntent = getIntent();
        storyId = chapterIntent.getStringExtra("storyId");
        chapterId = chapterIntent.getStringExtra("selectedChapterId");
//        chapterTitle = chapterIntent.getStringExtra("selectedChapterTitle");
//        chapterGoal = chapterIntent.getStringExtra("selectedChapterGoal");
//        chapterSummary = chapterIntent.getStringExtra("selectedChapterSummary");
        setChapterFormFields();
    }

    public void getAllTitlesAndIds() {
        allScenesArray = GameHelper.getInstance(this).getScenesForChapter(chapterId);

        allSceneIds.removeAll(allSceneIds);
        allSceneTitles.removeAll(allSceneTitles);

        for (int i = 0; i < allScenesArray.length; i++) {
            Models.Scene sceneAtI = allScenesArray[i];
            allSceneTitles.add(sceneAtI.title);
            allSceneIds.add(sceneAtI._id);
        }
    }

    //    TODOne: Use this to update the database with user edits
    public void readChapterFormFields() {
        chapterTitle = chapterTitleEditText.getText().toString();
        chapterGoal = chapterGoalEditText.getText().toString();
        chapterSummary = chapterSummaryEditText.getText().toString();
    }

    public void setChapterFormFields() {
        chapterTitleEditText.setText(chapterTitle);
        chapterGoalEditText.setText(chapterGoal);
        chapterSummaryEditText.setText(chapterSummary);
    }

    public void addScene(Models.Scene scene) {
        GameHelper.getInstance(SceneCreation.this).addScene(scene, new Response.Listener<Models.Scene>() {
            @Override
            public void onResponse(Models.Scene response) {
                getAllTitlesAndIds();
                updateListViewHeight(sceneNodeListView);
                arrayAdapter.notifyDataSetChanged();
                Toast.makeText(SceneCreation.this, "Scene Created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateChapter(Models.Chapter chapter) {
        GameHelper.getInstance(SceneCreation.this).updateChapter(chapter, new Response.Listener<Models.Chapter>() {
            @Override
            public void onResponse(Models.Chapter response) {
//            TODO: Add call back functionality
                Toast.makeText(getBaseContext(), "Chapter Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllFormFields() {
//        TODO: Get a single chapter as a Models.Chapter object
        Models.Chapter selectedChapter = GameHelper.getInstance(this).getChapter(chapterId);
//        Models.Chapter selectedChapter = allChaptersArray[???];
        chapterTitle = selectedChapter.title;
        chapterGoal = selectedChapter.type;
        chapterSummary = selectedChapter.summary;
        setChapterFormFields();
    }

    @Override
    public void onBackPressed() {

        readChapterFormFields();
        Models.Chapter updatedChapter = new Models.Chapter(chapterTitle, chapterSummary, chapterGoal, storyId);
        updatedChapter._id = chapterId;

        //TODO: Disable Buttons, show loading bar

        GameHelper.getInstance(SceneCreation.this).updateChapter(updatedChapter, new Response.Listener<Models.Chapter>() {
            @Override
            public void onResponse(Models.Chapter response) {
//            TODO: Add call back functionality
                finish();
            }
        });
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllTitlesAndIds();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allSceneTitles);
        sceneNodeListView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
//        Toast.makeText(this, "Scene Updated", Toast.LENGTH_SHORT).show();
    }

    public static void updateListViewHeight(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            return;
        }
        //get listview height
        int totalHeight = 0;
        int adapterCount = myListAdapter.getCount();
        for (int size = 0; size < adapterCount ; size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //Change Height of ListView
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (adapterCount - 1));
        myListView.setLayoutParams(params);
    }

}
