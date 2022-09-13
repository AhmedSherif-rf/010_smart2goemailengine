package com.ntg.engine.jobs;

import com.ntg.common.NTGMessageOperation;
import com.ntg.engine.entites.GenericObject;
import com.ntg.engine.entites.UDAsWithValues;
import com.ntg.engine.exceptions.NTGException;
import com.ntg.engine.repository.customImpl.SqlHelperDaoImpl;
import com.ntg.engine.service.CommonCachingFunction;
import com.ntg.engine.service.JobService;
import com.ntg.engine.util.LoginSettings;
import com.ntg.engine.util.Utils;
import com.sun.mail.util.BASE64DecoderStream;
import org.apache.commons.io.IOUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecieveMailJop implements Job {

    @Autowired
    SqlHelperDaoImpl sqlHelperDao;

    @Autowired
    private CommonCachingFunction commonCachingFun;

    @Value("${NTG.WFEngine.PendingTasks.ReadThreads}")
    String ReadThreads;

    @Value("${addObjectUrl}")
    String addObjectUrl;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private JobService jobService;

    @Autowired
    private LoginSettings loginSettings;


    JobDataMap JobData;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = context.getJobDetail().getKey().toString().split("\\.")[1];

        try {
            JobUtil.Debug("===============================>Mail Scanning Job #" + jobName
                    + "<================================");

            JobData = context.getJobDetail().getJobDataMap();
            EngineMainJob.RunninJobs.put("R" + JobData.get("recid"), JobData);
            String username = JobData.get("email_user").toString();
            String password = JobData.get("password").toString();
            String companyName = (Utils.isNotEmpty(JobData.get("company_name"))) ? JobData.get("company_name").toString() : "NTG";
            int port = (int) JobData.get("port");
            String host = JobData.get("host").toString();
            int readingType = (JobData.get("email_reading_type") == null) ? -1 : (int) JobData.get("email_reading_type");

            long typeId = (long) JobData.get("type_id");

            Long mapSenderNameToUdaId = JobUtil.convertBigDecimalToLong(JobData.get("map_sender_name_to_udaid"));
            Long mapBodyToUdaId = JobUtil.convertBigDecimalToLong(JobData.get("map_body_to_udaid"));
            Long mapSubjectToUdaId = JobUtil.convertBigDecimalToLong(JobData.get("map_subject_to_udaid"));
            Long mapOtherEmailsToUdaId = JobUtil.convertBigDecimalToLong(JobData.get("map_other_recipients_to_udaid"));
            boolean isSSL = JobData.get("is_ssl") != null;

            String GetUDAList = "SELECT * FROM adm_types_uda where" + " adm_types_uda.type_id=" + typeId;

            List<Map<String, Object>> UDAsMap = sqlHelperDao.queryForList(GetUDAList);
            List<UDAsWithValues> UDAs = getQuickUDAValueFromRowMap(UDAsMap);
            UDAsWithValues mapSender = null;
            UDAsWithValues mapBody = null;
            UDAsWithValues mapSubject = null;
            UDAsWithValues mapRecipients = null;
            if (UDAs != null) {
                mapSender = FindUDA(UDAs, mapSenderNameToUdaId);
                mapBody = FindUDA(UDAs, mapBodyToUdaId);
                mapSubject = FindUDA(UDAs, mapSubjectToUdaId);
                if (mapOtherEmailsToUdaId != null)
                    mapRecipients = FindUDA(UDAs, mapOtherEmailsToUdaId);
            }
            int NMessages = 0;
            while (JobData.get("Break") == null && (mapSender != null || mapSubject != null || mapBody != null) && NMessages > -50) {
                {
                    //the option NMessages > -10 mean the system loops 50 times and didn't find messages then will break the loop and main job will resume laster

                    NMessages += fetch(host, username, password, port, typeId, mapSender, mapSubject, mapBody, mapRecipients, isSSL, readingType,
                            UDAs, companyName);
                    Thread.sleep(100);
                }
            }
        } catch (Exception e) {
            System.out.println("War: execute : Email Listing : " + e.getMessage());
            //NTGMessageOperation.PrintErrorTrace(e);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }

        }

        jobService.deleteJob(jobName);
        EngineMainJob.RunninJobs.remove("R" + JobData.get("recid"));
        JobUtil.Debug("==============End Mail Scanning Job ============== <" + jobName + "> Break Request is " + JobData.get("Break"));

    }

    private UDAsWithValues FindUDA(List<UDAsWithValues> udAs, long UDA_ID) {
        int size = udAs.size();
        for (int i = 0; i < size; i++) {
            UDAsWithValues item = udAs.get(i);
            if (item.getRecId() == UDA_ID) {
                return item;
            }
        }
        return null;
    }


    public int fetch(String host, String user, String password, int port, Long typeId, UDAsWithValues mapSubject,
                     UDAsWithValues mabBody, UDAsWithValues mapSender, UDAsWithValues mapRecipients, boolean isSSL, int readingType,
                     List<UDAsWithValues> UDAs, String companyName) {
        int re = -1;
        Folder emailFolder = null;
        Store store = null;


        try {
            // initi Defualt values
            if (UDAs != null) {
                for (int i = 0; i < UDAs.size(); i++) {
                    UDAsWithValues item = UDAs.get(i);
                    item.setUdaValue(item.getDefaultValue());
                }
            }

            Properties properties = new Properties();
            setEmailProperties(properties, isSSL, port, host);
            Session emailSession = Session.getDefaultInstance(properties);
            store = emailSession.getStore("imaps");
            System.out.print("Try " + user + "@" + host);
            store.connect(host, user, password);
            System.out.print(",Connected ");
            emailFolder = store.getFolder("INBOX");

            while (JobData.get("Break") == null) {
                //yghandor keep listing to Messages all the time as it hang out after while by exit the jobs and reinit again many times

                emailFolder.open(Folder.READ_WRITE);

                Message[] messages = (readingType == 1) ? emailFolder.search(new FlagTerm(new Flags(Flags.Flag.DELETED), false)) :
                        emailFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

                // loop on messegs to create new ticket on same type
                if (messages != null && messages.length > 0) {
                    re = messages.length;
                    System.out.println("Found " + messages.length + " Msgs");
                    ProcessingMessages(messages, typeId, mapSubject,
                            mabBody, mapSender, mapRecipients, readingType,
                            UDAs, companyName, user);
                    emailFolder.close(true);
                } else {
                    //value to be put for last process time and main thread check if job is dead or not(hang)
                    JobData.put("LastCheckTime", System.currentTimeMillis());

                    re += -1;
                    System.out.println("No Messages Found, check after Minute --> " + re);
                    emailFolder.close(true);
                    Thread.sleep(1000); //wait a second to try fetch messages latter
                }

            }

        } catch (Exception e) {
            //NTGMessageOperation.PrintErrorTrace(e);
            System.out.println("War fetch: " + e.getMessage());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        if (emailFolder != null) {
            try {
                emailFolder.close(true);
            } catch (Exception e) {
                //NTGMessageOperation.PrintErrorTrace(e);
            }
        }
        if (store != null) {
            try {
                store.close();
            } catch (Exception e) {
                //NTGMessageOperation.PrintErrorTrace(e);
            }
        }
        return re;
    }

    private void setEmailProperties(Properties properties, boolean isSSL, int port, String host) {
        properties.setProperty("mail.store.protocol", "imap");
        properties.setProperty("mail.imap.host", host);
        properties.setProperty("mail.imap.port", Integer.toString(port));
        properties.setProperty("mail.imap.connectiontimeout", "10000");
        properties.setProperty("mail.imaps.connectiontimeout", "10000");
        properties.setProperty("mail.imaps.timeout", "5000");
        properties.setProperty("mail.imap.timeout", "5000");
        if (isSSL) {
            properties.setProperty("mail.imap.auth", "true");
            properties.setProperty("mail.imap.starttls.required", "true");
            properties.setProperty("mail.imap.starttls.enable", "true");
            properties.setProperty("mail.imap.ssl.enable", "true");

        }
    }

    @SuppressWarnings("deprecation")
    void createGenericObject(List<UDAsWithValues> udas, Long typeId, Message[] messages, int index, int readingType,
                             String subject, String companyName) throws Exception {

        GenericObject genericObject = new GenericObject();
        genericObject.setUdasValues(udas);
        genericObject.setName(subject);
        genericObject.setRecId(-1);
        genericObject.setTypeId(typeId);
        // Why Statis =1
        // genericObject.setStatusId(1);
        genericObject.setCreatedBy("Email Listner");
        genericObject.setCreatedDate(new Date());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String loginToken = commonCachingFun.BackEndLogin(companyName);
        headers.set("SessionToken", loginToken);
        headers.set("User-Agent", "Smart2GoEmailEngine");
        headers.set("Host", loginSettings.getHost());

        if (loginToken == null || loginToken.length() == 0) {
            throw new NTGException("SessionToken", "Token is not retrieved or Something wrong happened");
        }

        HttpEntity<Object> entitySms = new HttpEntity<Object>(genericObject, headers);
        @SuppressWarnings("unused")
        ResponseEntity<?> response = restTemplate.exchange(addObjectUrl, HttpMethod.POST, entitySms, String.class);
        String re = response.getBody().toString();
        if (re != null && re.indexOf("\"restException\":") > 0) {
            System.out.println("Create Issue : " + re);
            messages[index].setFlag(Flags.Flag.SEEN, false);
        } else if (readingType == 1) {
            messages[index].setFlag(Flags.Flag.DELETED, true);
            System.out.println(", Msg DELETED: " + messages[index].getFlags().contains(Flags.Flag.SEEN));

        } else {
            messages[index].setFlag(Flags.Flag.SEEN, true);
            System.out.println(", Msg SEEN: " + messages[index].getFlags().contains(Flags.Flag.SEEN));

        }
    }

    public List<UDAsWithValues> getQuickUDAValueFromRowMap(List<Map<String, Object>> mapSenderUda2) {
        List<UDAsWithValues> mappedUdas = new ArrayList<>();
        for (Map<String, Object> temprow : mapSenderUda2) {
            UDAsWithValues udas = new UDAsWithValues();
            udas.setUdaName((String) temprow.get("uda_name"));
            udas.setRecId(JobUtil.convertBigDecimalToLong(temprow.get("recid")));
            udas.setUdaCaption((String) (temprow.get("uda_panel_caption")));
            udas.setUdaTableName((String) temprow.get("uda_table_name"));
            udas.setUdaPanelID(JobUtil.convertBigDecimalToLong(temprow.get("uda_panel_id")));
            udas.setUdaPanelCaption((String) temprow.get("uda_panel_caption"));
            udas.setUdaType(JobUtil.convertBigDecimalToLong(temprow.get("uda_type")));
            if (udas.getUdaType() == 19) {
                udas.setAutoIDPreFix((String) temprow.get("auto_id_prefix"));
                udas.setAutoIDNumDigits((Long) temprow.get("auto_id_num_digits"));
            }
            Object v = temprow.get("default_value");
            udas.setDefaultValue((v == null) ? null : v.toString());
            mappedUdas.add(udas);
        }
        return mappedUdas;
    }

    public void writePart(Part p, List<Part> parts) throws Exception {

//        System.out.println("----------------------------");
//        System.out.println("CONTENT-TYPE: " + p.getContentType());
        // check if the content is plain text
        if (p.isMimeType("text/plain")) {
//            System.out.println("This is plain text");
//            System.out.println("---------------------------");
//            System.out.println((String) p.getContent());
            parts.add(p);
//            System.out.println("------------Part Added---------------");
        }
        // check if the content has attachment
        else if (p.isMimeType("multipart/*")) {
//            System.out.println("This is a Multipart");
//            System.out.println("---------------------------");
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();
            // parts array
            for (int i = 0; i < count; i++)
                writePart(mp.getBodyPart(i), parts);
        }
        // check if the content is a nested message
        else if (p.isMimeType("message/rfc822")) {
//            System.out.println("This is a Nested Message");
//            System.out.println("---------------------------");
            writePart((Part) p.getContent(), parts);
        } else {
            Object o = p.getContent();
            if (o instanceof String) {
//                System.out.println("This is a string");
//                System.out.println("---------------------------");
//                System.out.println((String) o);
                parts.add(p);
//                System.out.println("------------Part Added---------------");
            } else if (o instanceof InputStream) {
                parts.add(p);
            } else {
                System.out.println("War : unknown CONTENT-TYPE --> " + o);
//                System.out.println("---------------------------");
            }
        }
    }

    final static String[] supportedImgTypes = new String[]{"png", "jpg", "jpeg", "gif"};

    void ProcessingMessages(Message[] messages, Long typeId, UDAsWithValues mapSubject,
                            UDAsWithValues mabBody, UDAsWithValues mapSender, UDAsWithValues mapRecipients, int readingType,
                            List<UDAsWithValues> UDAs, String companyName, String user) throws Exception {
        for (int index = 0, TotalMessages = messages.length; index < TotalMessages; index++) {
            try {

                //value to be put for last process time and main thread check if job is dead or not(hang)
                JobData.put("LastCheckTime", System.currentTimeMillis());


                System.out.print("Message # " + (index + 1) + "/" + TotalMessages + ": " + messages[index].getSubject());
                System.out.print(",From: " + messages[index].getFrom()[0]);
                String subject = messages[index].getSubject();
                String sender = messages[index].getFrom()[0].toString();

                Set<String> recipients = new HashSet<>();
                for (Address recipient : messages[index].getAllRecipients()) {
                    String rec = recipient.toString();
                    if (!rec.toLowerCase().contains(user.toLowerCase())) {
                        int index1 = rec.lastIndexOf("<");
                        int index2 = rec.lastIndexOf(">");
                        if (index1 > -1 && index2 > -1) {
                            rec = rec.substring(index1 + 1, index2);
                        }
                        if (rec.indexOf("@") > -1) {
                            recipients.add(rec);
                        }
                    }
                }
                if (messages[index].isMimeType("multipart/*")) {
                    Multipart mp = (Multipart) messages[index].getContent();
                    // parts array

                    List<Part> parts = new ArrayList<Part>();
                    int count = mp.getCount();

                    for (int i = 0; i < count; i++)
                        writePart(mp.getBodyPart(i), parts);

                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    StringBuilder htmlBuilder = new StringBuilder();
                    List<String> imagedEncoded = new ArrayList<>();

                    for (Part part : parts) {
                        if (part.getContentType().toLowerCase().contains("image/") || part.isMimeType("image/*")) {
                            if (part.getContent() instanceof BASE64DecoderStream) {
                                boolean foundType = false;
                                for (String type : supportedImgTypes) {
                                    if (part.getContentType().toLowerCase().contains(type)) {
                                        BASE64DecoderStream base64DecoderStream = (BASE64DecoderStream) part.getContent();
                                        byte[] byteArray = IOUtils.toByteArray(base64DecoderStream);
                                        byte[] encodeBase64 = Base64.getEncoder().encode(byteArray);
                                        String outputStringValue = new String(encodeBase64, "UTF-8");

                                        outputStringValue = "<img src='data:image/" + type + ";base64," + outputStringValue
                                                + "'/>";
                                        imagedEncoded.add(outputStringValue);
                                        break;
                                    }
                                }
                                if (!foundType) {
                                    imagedEncoded.add("<div>Attachments:" + part.getContentType() + "</div>");
                                }
                            }
                        } else {
                            if (part.getContentType().toUpperCase().contains("TEXT/HTML")) {
                                htmlBuilder.append(part.getContent().toString());
                            }
                        }
                    }

                    // function to replace HTML img of email body with the encoded img
                    String fullHTML = getImgTags(htmlBuilder.toString(), imagedEncoded);
                    output.write(fullHTML.getBytes());

                    mabBody.setHtextValue(output.toByteArray());
                    mapSender.setUdaValue(sender);
                    mapSubject.setUdaValue(subject);
                    mapSubject.setUdaValue(String.join(",", recipients));
                } else {
                    Object msg = messages[index].getContent();
                    if (msg != null && msg.toString() != null && msg.toString().getBytes().length > 0) {
                        if (mabBody != null) {
                            mabBody.setHtextValue(msg.toString().getBytes());
                        } else {
                            System.out.println("War:No Body UDA");
                        }
                        if (mapSender != null) {
                            mapSender.setUdaValue(sender);
                        } else {
                            System.out.println("War:No Sender UDA");
                        }
                        if (mapSubject != null) {
                            mapSubject.setUdaValue(subject);
                        } else {
                            System.out.println("War:No Subject UDA");
                        }
                        if (mapRecipients != null) {
                            mapRecipients.setUdaValue(String.join(",", recipients));
                        } else {
                            System.out.println("War:No Other Emails UDA");
                        }
                    }
                }

                createGenericObject(UDAs, typeId, messages, index, readingType, subject, companyName);

            } catch (Exception e) {
                try {
                    if (messages != null && index < messages.length) {
                        messages[index].setFlag(Flags.Flag.SEEN, false);
                    }
                } catch (Exception messagingException) {
                    System.out.println("War:" + messagingException.getMessage());
                }
                NTGMessageOperation.PrintErrorTrace(e);
                System.out.println("War: ProcessingMessages " + e.getMessage());
                Thread.sleep(10000);
            }

        }
    }

    private String getImgTags(String htmlContent, List<String> encodedImagesList) throws Exception {

        if (Utils.isNotEmpty(encodedImagesList) && !Utils.isEmptyString(htmlContent)) {

            Pattern pattern = Pattern.compile("<img\\s+[^>]*>");
            Matcher matcher = pattern.matcher(htmlContent);
            while (matcher.find() && encodedImagesList.size() > 0) {
                htmlContent = htmlContent.replace(matcher.group(), encodedImagesList.get(0));
                encodedImagesList.remove(0);
            }
        }

        return htmlContent;
    }

}
