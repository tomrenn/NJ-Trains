package com.tomrenn.njtrains.data;

import android.content.Context;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;
import com.tomrenn.njtrains.Injector;
import com.tomrenn.njtrains.data.api.TransitService;
import com.tomrenn.njtrains.data.api.models.TransitInfo;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit2.Response;
import timber.log.Timber;

/**
 *
 */
public class TaskService extends GcmTaskService {
    @Inject
    TransitService transitService;

    enum TaskAction {
        UPDATE
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        Timber.d("onRunTask");
        String tag = taskParams.getTag();
        TaskAction action = TaskAction.valueOf(tag);
        switch (action) {
            case UPDATE:
                return handleUpdate();
        }
        return 0;
    }

    int handleUpdate() {
        Injector.obtain(getApplicationContext()).inject(this);
        try {
            Response<Map<String, TransitInfo>> res = transitService.getTransitInfo().execute();
            if (res.isSuccess()) {
                Map<String, TransitInfo> transitInfoMap = res.body();
                TransitInfo transitInfo = transitInfoMap.get("rail");
                Timber.v("Transit info checksum %s", transitInfo.getChecksum());
                return GcmNetworkManager.RESULT_SUCCESS;
            } else {
                return GcmNetworkManager.RESULT_RESCHEDULE;
            }
        } catch (IOException error) {
            Timber.e(error, "Failed obtaining transit info");
            return GcmNetworkManager.RESULT_RESCHEDULE;
        }
    }

    public static void scheduleTasks(Context context) {
        long hours12 = TimeUnit.HOURS.toSeconds(12);

        PeriodicTask task = new PeriodicTask.Builder()
                .setService(TaskService.class)
                .setTag(TaskAction.UPDATE.name())
                .setPeriod(hours12)
                .setFlex(3600)
                .setRequiresCharging(true)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .build();

        GcmNetworkManager gcmNetworkManager = GcmNetworkManager.getInstance(context);
        gcmNetworkManager.schedule(task);
    }
}
