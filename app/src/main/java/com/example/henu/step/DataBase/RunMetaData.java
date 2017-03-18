package com.example.henu.step.DataBase;

import android.provider.BaseColumns;

/**
 * Created by 宋浩祥 on 2017/3/7.
 */

public final class RunMetaData {
    private  RunMetaData() {
    }

    public static abstract class RunTable implements BaseColumns{
        public static final String TABLE_NAME = "run";
        public static final String TELEPHONE = "telephone";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String TOTAL_LENGTH = "length";
        public static final String TOTAL_TIME = "duration";
        public static final String CONSUME = "consume";
        public static final String POINTS = "points";
        public static final String ISUPDATE = "is_update";
    }
}
