package vn.mran.simplenote.view;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import vn.mran.simplenote.R;
import vn.mran.simplenote.util.AnimationUtil;

/**
 * Created by MrAn on 19-Aug-16.
 */
public class FloatingAdd {
    public FloatingActionButton btnAdd;
    public FloatingActionButton btnAddFolder;
    public FloatingActionButton btnAddNote;
    public boolean isVisible = false;
    private Context context;

    public FloatingAdd(View view) {
        this.context = view.getContext();
        btnAdd = (FloatingActionButton) view.findViewById(R.id.btnAdd);
        btnAddFolder = (FloatingActionButton) view.findViewById(R.id.btnAddFolder);
        btnAddNote = (FloatingActionButton) view.findViewById(R.id.btnAddNote);
        init();
    }

    private void init() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAdd.startAnimation(AnimationUtil.fadeIn(context));
                if (isVisible) {
                    hideAll(true);
                } else {
                    showAll();
                }
            }
        });
    }

    public void showAll() {
        isVisible = true;
        btnAddFolder.setVisibility(View.VISIBLE);
        btnAddNote.setVisibility(View.VISIBLE);
        btnAddFolder.startAnimation(AnimationUtil.slideInBottom(context));
        btnAddNote.startAnimation(AnimationUtil.slideInBottom(context));
        btnAdd.setImageResource(R.drawable.ic_remove_white_24dp);
    }

    public void hideAll(boolean isAnimate) {
        isVisible = false;
        if(isAnimate){
            btnAddFolder.startAnimation(AnimationUtil.slideOutBottom(context));
            btnAddNote.startAnimation(AnimationUtil.slideOutBottom(context));
        }
        btnAddFolder.setVisibility(View.GONE);
        btnAddNote.setVisibility(View.GONE);
        btnAdd.setImageResource(R.drawable.ic_add_white_24dp);
    }

    public void showAdd() {
        btnAdd.startAnimation(AnimationUtil.slideInBottom(context));
        btnAdd.setVisibility(View.VISIBLE);
    }

    public void hideAdd() {
        btnAdd.startAnimation(AnimationUtil.slideOutBottom(context));
        btnAdd.setVisibility(View.GONE);
    }

    public void toggleBtnAdd(boolean show) {
        if (!show) {
            hideAdd();
        } else {
            btnAdd.setImageResource(R.drawable.ic_add_white_24dp);
            showAdd();
        }
    }
}
