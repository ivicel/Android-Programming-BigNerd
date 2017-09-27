package info.ivicel.photogallery;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by Ivicel on 25/09/2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    private static final String TAG = "MyJobService";
    
    private static final int JOB_ID = 1;
    
    @Override
    public boolean onStartJob(JobParameters params) {
        new JobTask().execute(params);
        return true;
    }
    
    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
    
    private class JobTask extends AsyncTask<JobParameters, Void, Void> {
        
        @Override
        protected Void doInBackground(JobParameters... params) {
            JobParameters param = params[0];
            String query = QueryPreferences.getStoredQuery(MyJobService.this);
            String lastResultId = QueryPreferences.getLastResultId(MyJobService.this);
            List<GalleryItem> items;
    
            if (query == null) {
                items = new FlickrFetchr().fetchRecentPhotos();
            } else {
                items = new FlickrFetchr().searchPhotos(query);
            }
    
            if (items.size() == 0) {
                return null;
            }
    
            String resutlId = items.get(0).getId();
            if (resutlId == null || !resutlId.equals(lastResultId)) {
                Resources resources = getResources();
                Intent i = PhotoGalleryActivity.newIntent(MyJobService.this);
                PendingIntent pi = PendingIntent.getActivity(MyJobService.this, 0, i, 0);
        
                Notification notification = new NotificationCompat.Builder(MyJobService.this)
                        .setTicker(resources.getString(R.string.new_pictures_title))
                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .setContentTitle(resources.getString(R.string.new_pictures_title))
                        .setContentText(resources.getString(R.string.new_pictures_text))
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .build();
                NotificationManagerCompat notificatinoManager = NotificationManagerCompat.from(MyJobService.this);
                notificatinoManager.notify(0, notification);
        
                QueryPreferences.setLastResultId(MyJobService.this, resutlId);
            }
    
            jobFinished(param, false);
            return null;
        }
    }
    
    public static void setJobService(Context context, boolean isOn) {
        if (isOn) {
            ComponentName component = new ComponentName(context, MyJobService.class);
            /*
             * after Nougat, the mininum interval is returned from getMinPeriodMills()
             * all lower than this will be replaced by system
             */
            JobInfo job = new JobInfo.Builder(JOB_ID, component)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(1000 * 60 * 15)
                    .setPersisted(true)
                    .build();
            JobScheduler jobScheduler =
                    (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(job);
        } else {
            stopJobService(context);
        }
    }
    
    public static void stopJobService(Context context) {
        JobScheduler jobScheduler =
                (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
    }
    
    public static boolean isJobStarted(Context context) {
        JobScheduler jobScheduler =
                (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        List<JobInfo> jobs = jobScheduler.getAllPendingJobs();
        for (JobInfo job : jobs) {
            if (job.getId() == JOB_ID) {
                return true;
            }
        }
        return false;
    }
}
