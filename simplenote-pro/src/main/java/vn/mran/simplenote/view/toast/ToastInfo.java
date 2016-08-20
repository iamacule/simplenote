package vn.mran.simplenote.view.toast;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import vn.mran.simplenote.R;


/**
 * Created by MrAn on 04-May-16.
 */
public class ToastInfo {
    private Toast toast;

    public ToastInfo(Activity activity, String message) {
        create(activity,message);
    }

    public void cancel()
    {
        toast.cancel();
    }

    public void show()
    {
        toast.show();
    }

    private void create(Activity activity, String message){
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) activity.findViewById(R.id.toast_parent));
        TextView text = (TextView) layout.findViewById(R.id.toast_message);
        text.setText(message);

        toast = new Toast(activity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
    }
}
