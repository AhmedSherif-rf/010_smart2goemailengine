package com.ntg.engine.service;

import com.ntg.common.NTGEHCacher;
import com.ntg.engine.jobs.JobUtil;
import com.ntg.engine.repository.customImpl.SqlHelperDaoImpl;
import com.ntg.engine.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service()
public class CommonCachingFunction {

    static NTGEHCacher<String, Object> CachingCommonData;

    static {
        CachingCommonData = new NTGEHCacher<String, Object>(Setting.CacheEmployeeInfoFor);
    }

    @Value("${wf_GetEmployee}")
    String wf_GetEmployee;

    @Value("${crm_GetEmployeeMangers}")
    String GetEmployeeMangers;

    @Value("${wf_GetEmployeesEmails}")
    String wf_GetEmployeesEmails;

    @Value("${wf_GetGroupEmail}")
    String wf_GetGroupEmail;

    @Value("${GroupMembers}")
    String GroupMembers;

    @Value("${checkAssignmentRules}")
    String checkAssignmentRule;

    @Value("${wf_loadObject}")
    String wf_loadObject;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SqlHelperDaoImpl sqlHelperDao;

    @Autowired
    private LoginSettings loginSettings;

    public EmployeeFullInfoResponse getEmployeeFullInfo(Long EmploeeID) {
        String key = "Emp_" + EmploeeID;
        EmployeeFullInfoResponse response = (EmployeeFullInfoResponse) CachingCommonData.get(key);
        if (response == null) {
            EmployeeFullInfoRequest req = new EmployeeFullInfoRequest();
            req.empId = EmploeeID;

            ResponseEntity<EmployeeFullInfoResponse> responseEntity = restTemplate.postForEntity(wf_GetEmployee, req,
                    EmployeeFullInfoResponse.class);
            if (responseEntity.getBody() != null) {
                response = responseEntity.getBody();
                CachingCommonData.put(key, response);
            }
        }
        return response;
    }

    public EmployeeInfo[] GetManagerInfo(Long EmployeeId, Long ManagerLevel /* one mean direct manager */) {
        String key = "Manager_" + EmployeeId + "_" + ManagerLevel;
        EmployeeInfo[] managerInfo = (EmployeeInfo[]) CachingCommonData.get(key);
        if (Utils.isEmpty(managerInfo)) {
            Map<String, Long> objMap = new HashMap<String, Long>();
            objMap.put("empId", EmployeeId);
            objMap.put("managerLevel", ManagerLevel);

            ResponseEntity<UserResponse> responseEntity = restTemplate.postForEntity(GetEmployeeMangers, objMap,
                    UserResponse.class);
            UserResponse userResponse = responseEntity.getBody();
            if (userResponse != null) {
                managerInfo = userResponse.returnValue;
                CachingCommonData.put(key, managerInfo);
            }
        }
        return managerInfo;
    }

    public List<String> getEmployeesEmails(List<Long> employeesIds) {
        String key = "Emp_Email";
        for (int i = 0; i < employeesIds.size(); i++) {
            key += '_' + employeesIds.get(i);
        }
        List<String> employeesEmails = (List<String>) CachingCommonData.get(key);
        if (Utils.isEmpty(employeesEmails)) {
            ResponseEntity<GetEmailsResponse> responseEntity = restTemplate.postForEntity(wf_GetEmployeesEmails,
                    employeesIds, GetEmailsResponse.class);
            GetEmailsResponse emails = responseEntity.getBody();
            if (Utils.isNotEmpty(emails)) {
                employeesEmails = emails.getEmails();
                CachingCommonData.put(key, employeesEmails);
            }
        }
        return employeesEmails;
    }

    public String getGroupEmail(Long groupId) {
        String key = "Group_Email_" + groupId;
        String groupEmail = (String) CachingCommonData.get(key);
        if (Utils.isEmpty(groupEmail)) {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(wf_GetGroupEmail, groupId, String.class);
            if (Utils.isNotEmpty(responseEntity.getBody())) {
                groupEmail = responseEntity.getBody();
                CachingCommonData.put(key, groupEmail);
            }
        }
        return groupEmail;
    }

    public Employee[] getAllGroupEmployees(Long groupId) {
        String key = "Group_Emps_" + groupId;
        Employee[] employees = (Employee[]) CachingCommonData.get(key);

        if (Utils.isEmpty(employees)) {
            GetGroupMembersReq req = new GetGroupMembersReq();
            req.RecID = groupId;

            ResponseEntity<GetGroupMembersRes> responseEntity = restTemplate.postForEntity(GroupMembers, req,
                    GetGroupMembersRes.class);
            GetGroupMembersRes messagesRes = responseEntity.getBody();
            if (messagesRes != null) {
                employees = messagesRes.returnValue;
                CachingCommonData.put(key, employees);
            }
        }
        return employees;
    }


    public String BackEndLogin(String companyName) throws InterruptedException {

        String key = "Login_" + companyName + "_" + loginSettings.getUserName();

        String userSessionToken = (String) CachingCommonData.get(key);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Smart2GoEmailEngine");
        headers.set("Host", loginSettings.getHost());
        if (loginSettings.getUrl().contains("/rest/")) {
            loginSettings.setUrl(loginSettings.getUrl().replaceAll("/rest/", ""));
        }

        if (userSessionToken != null) {
            //test the session is not expire
            try {
                headers.set("SessionToken", userSessionToken);
                headers.set("User-Agent", "Smart2GoEmailEngine");
                headers.set("Host", loginSettings.getHost());

                HttpEntity<String> entity = new HttpEntity<String>(null, headers);

                String TestSesisonTokenUrl = loginSettings.getUrl() + "/rest/MainFunciton/TestSesisonToken/";

                ResponseEntity<String> res = restTemplate.exchange(TestSesisonTokenUrl, HttpMethod.GET, entity,
                        String.class);
                String reomteToken = null;
                if (Utils.isNotEmpty(res.getBody())) {
                    reomteToken = res.getBody();
                }
                if (reomteToken == null || reomteToken.contains(userSessionToken) == false) {
                    JobUtil.Debug("War:Issue in Test Session Token : " + reomteToken);
                    userSessionToken = null;//relogin again
                }
            } catch (Exception ex) {
                JobUtil.Debug("War:Issue in Test Session Token : " + ex.getMessage());
                userSessionToken = null;//relogin again
            }


        }


        while (userSessionToken == null) {
            try {
                LoginUserRequest loginUser = new LoginUserRequest();
                headers.set("SessionToken", companyName);
                headers.set("User-Agent", "Smart2GoEmailEngine");
                headers.set("Host", loginSettings.getHost());

                SessionInfo sInfo = new SessionInfo();
                sInfo.loginUserName = loginSettings.getUserName();
                sInfo.companyName = companyName;

                JobUtil.Debug("Login URL : ===>" + loginSettings.getUrl() + "/rest/MainFunciton/login/ , C : " + companyName + " ,u :" + loginSettings.getUserName());

                loginUser.Password = loginSettings.getPassword();
                loginUser.LoginUserInfo = sInfo;

                HttpEntity<LoginUserRequest> entity = new HttpEntity<LoginUserRequest>(loginUser, headers);
                // Added By Mahmoud To fix loaded url

                String getMainMethodUrl = loginSettings.getUrl() + "/rest/MainFunciton/login/";
                ResponseEntity<LoginUser> res = restTemplate.exchange(getMainMethodUrl, HttpMethod.POST, entity,
                        LoginUser.class);
                if (Utils.isNotEmpty(res.getBody())) {
                    LoginUser user = res.getBody();
                    userSessionToken = user.UserSessionToken;
                    CachingCommonData.put(key, userSessionToken);
                    if (userSessionToken != null) {
                        JobUtil.Debug("User Session Tocken Is  : ===> Fetched Ok");
                    }
                }
            } catch (Exception ex) {
                JobUtil.Debug("War:Issue in Fetch Session Token : " + ex.getMessage());
                userSessionToken = null;
            }
            if (userSessionToken == null) {
                JobUtil.Debug("War:Sleep Try to get Session Token After 1000MSec");
                Thread.sleep(1000);
            }
        }
        return userSessionToken;
    }
}
