package com.ntg.engine.jobs;

import com.ntg.common.NTGMessageOperation;
import com.ntg.engine.repository.customImpl.SqlHelperDaoImpl;
import com.ntg.engine.service.CommonCachingFunction;
import com.ntg.engine.service.JobService;
import com.ntg.engine.util.LoginSettings;
import com.ntg.engine.util.LoginUser;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@DisallowConcurrentExecution
public class EngineMainJob implements Job {


    @Autowired
    private JobService jobService;

    @Autowired
    SqlHelperDaoImpl sqlHelperDao;

    @Value("${NTG.WFEngine.PendingTasks.ReadThreads}")
    String ReadThreads;

    @Value("${addObjectUrl}")
    String addObjectUrl;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private CommonCachingFunction CachingFunction;

    static int CallNo = 0;

    @Value("${postModuleVersion}")
    String postModuleVersionURL;

    @Value("${pom.version}")
    public String emailEngineVersion;

    static boolean IsVersionPosted = false;
    static HashMap<String, JobDataMap> RunninJobs = new HashMap<>();

    @Autowired
    private LoginSettings loginSettings;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (0 == CallNo) {
            JobUtil.Debug("===============================>Start Main Job Started First Time<=================");
        }
        CallNo++;
        String getTasksSQL = "SELECT \n" +
                "a.app_name, o.recid ObjectID , t.recid TypeID,t.type_name,o.object_name \n" +
                ",\n" +
                "c.* \n" +
                "FROM adm_email_configuration c join adm_types t on t.recid = c.type_id\n" +
                "join adm_objects o on o.recid = t.objectid\n" +
                "join adm_apps a on a.recid = (Select max(m.app_ID) from adm_menu m where m.obj_ID =o.recid )\n" +
                "where  (a.is_deleted   = '0' or  a.is_deleted  is null) and (o.is_deleted = '0' or o.is_deleted is null)\n" +
                "and (t.is_deleted = '0'  or t.is_deleted is null)\n" +
                "and (c.map_sender_name_to_udaid >0 or c.map_body_to_udaid >0 or c.map_subject_to_udaid > 0 )\n" +
                "and c.host is not null and c.port is not null \n" +
                "and c.password is not null\n" +
                "and c.email_user is not null and c.disabled = '0'";
        List<Map<String, Object>> emailsConfiguration = sqlHelperDao.queryForList(getTasksSQL);
        int mailsLength = emailsConfiguration.size();
        Object[] keys = RunninJobs.keySet().toArray();
        for (Object key : keys) {
            JobDataMap job = RunninJobs.get(key);
            job.put("ToBeCheck", Boolean.TRUE);
            Object JobLastActivityTime = job.get("LastCheckTime");
            if (JobLastActivityTime != null) {
                long lastTime = Long.valueOf(JobLastActivityTime.toString());
                long IdealTime = System.currentTimeMillis() - lastTime;

                if (IdealTime > 900000) { // for 10 M not doing anything will be delete
                    System.out.println("Job : " + job.get("jobName") + "-->");
                    System.out.println("Is Ideal From " + IdealTime);
                    //delete the job
                    job.put("Break", Boolean.TRUE);
                    jobService.stopJob((String) job.get("jobName"));
                    jobService.deleteJob((String) job.get("jobName"));
                    RunninJobs.remove(key);
                    System.out.println("Job Deleted: " + job.get("jobName") + "-->");
                }
            } else {
                System.out.println("Job : " + job.get("jobName") + "Is Not Doing AnyThing Yet! ");
                job.put("LastCheckTime", System.currentTimeMillis());
            }
        }
        for (int i = 0; i < mailsLength; i++) {
            Map<String, Object> info = emailsConfiguration.get(i);
            long Recid = JobUtil.convertBigDecimalToLong(info.get("recid"));
            String JobName = "MailScan #" + Recid + " For " + info.get("app_name") + "/" + info.get("object_name")
                    + "/" + info.get("type_name") + "/" + info.get("company_name");

            if (!jobService.isJobWithNamePresent(JobName)) {
                JobDataMap data = new JobDataMap();
                data.put("jobName", JobName);
                //fill job data from mail cfg
                keys = info.keySet().toArray();
                for (int k = 0; k < keys.length; k++) {
                    data.put((String) keys[k], info.get(keys[k]));
                }

                jobService.scheduleOneTimeJob(JobName, JobUtil.getJobClassByName("RecieveMailJop"), new Date(),
                        data);

            } else if (RunninJobs.get("R" + Recid) != null) {

                RunninJobs.get("R" + Recid).put("ToBeCheck", Boolean.FALSE);


                //check jobs data if changed or not
                JobDataMap Olddata = RunninJobs.get("R" + Recid);
                if (Olddata != null) {
                    boolean DataIsIdentical = true;
                    keys = info.keySet().toArray();
                    for (int k = 0; k < keys.length; k++) {
                        Object OldValue = Olddata.get(keys[k]);
                        if (OldValue == null) {
                            OldValue = "";
                        }
                        Object NewValue = info.get(keys[k]);
                        if (NewValue == null) {
                            NewValue = "";
                        }
                        if (!OldValue.toString().equals(NewValue.toString())) {
                            DataIsIdentical = false;
                        }
                    }
                    if (DataIsIdentical == false) {
                        System.out.println("Request Breack Job --> " + JobName);
                        Olddata.put("Break", Boolean.TRUE);
                    }
                }
            }


        }
        //Delete Jobs which not in DB any More
        keys = RunninJobs.keySet().toArray();
        for (Object key : keys) {
            Object v = RunninJobs.get(key).get("ToBeCheck");
            if (v != null && v == Boolean.TRUE) {
                RunninJobs.get(key).put("Break", Boolean.TRUE);
            }
        }
        sendModuleVersion();

    }

    void sendModuleVersion() {
        if (!IsVersionPosted) {
            try {
                String loginToken = this.CachingFunction.BackEndLogin(loginSettings.getCompanyName());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                headers.set("SessionToken", loginToken);
                headers.set("User-Agent", "Smart2GoEmailEngine");
                headers.set("Host", loginSettings.getHost());

                HttpEntity entity = new HttpEntity<>(void.class, headers);
                String url = this.postModuleVersionURL + "/Smart2Go_Email_Listener_Engine" + "/" + this.emailEngineVersion;
                ResponseEntity res = restTemplate.exchange(url, HttpMethod.POST, entity, LoginUser.class);
                System.out.println("Post Version --> " + res.getStatusCodeValue());
                IsVersionPosted = true;
            } catch (Exception e) {
                NTGMessageOperation.PrintErrorTrace(e);
            }
        }
    }

}
