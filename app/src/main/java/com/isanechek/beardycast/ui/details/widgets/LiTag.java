package com.isanechek.beardycast.ui.details.widgets;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by radiationx on 03.09.16.
 */
public class LiTag extends BaseTag {
    public LiTag(Context context) {
        super(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(px1, 0, 0, px1);

        setLayoutParams(params);
    }
}
