package vn.mran.simplenote.view;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuPopup;
import com.shehabic.droppy.animations.DroppyFadeInAnimation;

import vn.mran.simplenote.R;
import vn.mran.simplenote.mvp.view.ToolAddNotesView;
import vn.mran.simplenote.view.effect.TouchEffect;

/**
 * Created by MrAn on 19-Aug-16.
 */
public class ToolAddNote {
    public LinearLayout btnClear;
    public LinearLayout btnSave;
    public LinearLayout btnMore;
    public LinearLayout btnEdit;
    public TextView txtDate;
    public TextView txtFolder;
    private ToolAddNotesView toolAddNotesView;
    private Activity activity;

    public ToolAddNote(View view, Activity activity) {
        this.activity = activity;
        this.toolAddNotesView = (ToolAddNotesView) activity;
        btnClear = (LinearLayout) view.findViewById(R.id.btnClear);
        btnSave = (LinearLayout) view.findViewById(R.id.btnSave);
        btnEdit = (LinearLayout) view.findViewById(R.id.btnEdit);
        btnMore = (LinearLayout) view.findViewById(R.id.btnMore);
        txtDate = (TextView) view.findViewById(R.id.txtDate);
        txtFolder = (TextView) view.findViewById(R.id.txtFolder);
        TouchEffect.addAlpha(btnClear);
        TouchEffect.addAlpha(btnSave);
        TouchEffect.addAlpha(btnMore);
        init();
    }

    private void init() {
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMore.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
                DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(activity, btnMore);
                DroppyMenuPopup droppyMenu = droppyBuilder.fromMenu(R.menu.tool_popup)
                        .triggerOnAnchorClick(false)
                        .setOnClick(new DroppyClickCallbackInterface() {
                            @Override
                            public void call(View v, int id) {
                                btnMore.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
                                switch (id) {
                                    case R.id.btnPhoto:
                                        toolAddNotesView.onPhoto();
                                        break;
                                    case R.id.btnVoice:
                                        toolAddNotesView.onVoice();
                                        break;
                                    case R.id.btnCamera:
                                        toolAddNotesView.onCamera();
                                        break;
                                    case R.id.btnStyleColor:
                                        toolAddNotesView.onStyle();
                                        break;
                                }
                            }
                        })
                        .setOnDismissCallback(new DroppyMenuPopup.OnDismissCallback() {
                            @Override
                            public void call() {
                                btnMore.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
                            }
                        })
                        .setPopupAnimation(new DroppyFadeInAnimation())
                        .setYOffset(15)
                        .build();
                droppyMenu.show();
            }
        });
    }
}
