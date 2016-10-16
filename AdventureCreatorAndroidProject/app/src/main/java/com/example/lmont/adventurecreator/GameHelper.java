package com.example.lmont.adventurecreator;

import android.content.ContentValues;
import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

/**
 * Created by lmont on 10/14/2016.
 */

public class GameHelper {

    private static GameHelper instance;

    private APIHelper apiHelper;
    private AdventureDBHelper dbHelper;
    private Context context;

    public static GameHelper getInstance(Context context) {
        if (instance == null)
            instance = new GameHelper(context.getApplicationContext());

        return instance;
    }

    private GameHelper(Context context) {
        this.context = context;

        apiHelper = APIHelper.getInstance(context);
        dbHelper = AdventureDBHelper.getInstance(context);
    }

    public void addStory(final Models.Story story, final Response.Listener<Models.Story> listener) {
        apiHelper.addStory(
                story,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Gson gson = new Gson();
                        Models.StoryResponse storyResponse = gson.fromJson(response.toString(), Models.StoryResponse.class);
                        ContentValues cv = new ContentValues();
                        cv.put("title", story.title);
                        cv.put("description", story.description);
                        cv.put("genre", story.genre);
                        cv.put("tags", story.tags);
                        cv.put("type", story.type);
                        cv.put("_id", storyResponse.story._id);
                        dbHelper.getWritableDatabase().insert(dbHelper.STORY_TABLE_NAME, null, cv);
                        listener.onResponse(storyResponse.story);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
    }

    public void addChapter(final Models.Chapter chapter, final Response.Listener<Models.Chapter> listener) {
        apiHelper.addChapter(
                chapter,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Gson gson = new Gson();
                        Models.ChapterResponse chapterResponse = gson.fromJson(response.toString(), Models.ChapterResponse.class);
                        ContentValues cv = new ContentValues();
                        cv.put("storyID", chapter.storyID);
                        cv.put("title", chapter.title);
                        cv.put("summary", chapter.summary);
                        cv.put("type", chapter.type);
                        cv.put("_id", chapterResponse.chapter._id);
                        dbHelper.getWritableDatabase().insert(dbHelper.CHAPTER_TABLE_NAME, null, cv);
                        listener.onResponse(chapterResponse.chapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
    }

    public void addScene (final Models.Scene scene, final Response.Listener<Models.Scene> listener) {
        apiHelper.addScene(
                scene,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Gson gson = new Gson();
                        Models.SceneResponse sceneResponse = gson.fromJson(response.toString(), Models.SceneResponse.class);
                        ContentValues cv = new ContentValues();
                        cv.put("chapterID", scene.chapterID);
                        cv.put("title", scene.title);
                        cv.put("body", scene.body);
                        cv.put("journalText", scene.journalText);
                        cv.put("flagModifiers", scene.flagModifiers);
                        cv.put("_id", sceneResponse.scene._id);
                        dbHelper.getWritableDatabase().insert(dbHelper.SCENES_TABLE_NAME, null, cv);
                        listener.onResponse(sceneResponse.scene);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
    }

    public void addTransition(final Models.Transition transition, final Response.Listener<Models.Transition> listener) {
        apiHelper.addTransition(
                transition,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Gson gson = new Gson();
                        Models.TransitionResponse transitionResponse = gson.fromJson(response.toString(), Models.TransitionResponse.class);
                        ContentValues cv = new ContentValues();
                        cv.put("fromSceneID", transition.fromSceneID);
                        cv.put("toSceneID", transition.toSceneID);
                        cv.put("type", transition.type);
                        cv.put("verb", transition.verb);
                        cv.put("flag", transition.flag);
                        cv.put("attribute", transition.attribute);
                        cv.put("comparator", transition.comparator);
                        cv.put("challengeLevel", transition.challengeLevel);
                        cv.put("_id", transitionResponse.transition._id);
                        dbHelper.getWritableDatabase().insert(dbHelper.TRANSITIONS_TABLE_NAME, null, cv);
                        listener.onResponse(transitionResponse.transition);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
    }

    public Models.Story[] getAllStories() {
        return dbHelper.getAllStories();
    }

    public Models.Chapter[] getChaptersForStory(String gameID) {
        return dbHelper.getChaptersForStory(gameID);
    }

    public Models.Scene[] getScenesForChapter(String chapterID) {
        return dbHelper.getScenesForChapter(chapterID);
    }

    public Models.Transition[] getTransitionsForScenes(String fromSceneID) {
        return dbHelper.getTransitionsForScene(fromSceneID);
    }

    public Models.Story getFullStory(String gameID) {
        Models.Story story = dbHelper.getStory(gameID);
        // Do dis

        return story;
    }
}