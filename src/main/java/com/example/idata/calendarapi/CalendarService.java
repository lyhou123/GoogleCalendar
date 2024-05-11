package com.example.idata.calendarapi;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class CalendarService {
    private static final String APPLICATION_NAME = "idata";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/calendar.readonly");

    private static final String CREDENTIALS_FILE_PATH = "json1.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    public List<Event> getEvents() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        if (events == null || events.getItems() == null) {
            throw new IllegalStateException("No events found or received a null response from API.");
        }
        return events.getItems();
    }

//    private Credential getCredentials( NetHttpTransport HTTP_TRANSPORT) throws IOException {
//        if(HTTP_TRANSPORT == null){
//            throw new IllegalStateException("HTTP_TRANSPORT is null");
//        }
//        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
//                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
//        return credential;
//    }

    private Credential getCredentials(NetHttpTransport HTTP_TRANSPORT) throws IOException {
        if (HTTP_TRANSPORT == null) {
            throw new IllegalStateException("HTTP_TRANSPORT is null");
        }

        // Define the file path
        String CREDENTIALS_FILE_PATH = "D:\\ITE_CSTAD\\finalProject\\google\\calendar_api\\src\\main\\resources\\json1.json";

        try {
            // Attempt to open the file input stream
            FileInputStream fileInputStream = new FileInputStream(CREDENTIALS_FILE_PATH);

            // Create GoogleCredential from the input stream
            GoogleCredential credential = GoogleCredential.fromStream(fileInputStream)
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

            // Close the file input stream
            fileInputStream.close();

            return credential;
        } catch (FileNotFoundException e) {
            // Handle file not found error
            System.err.println("Error: Credentials file not found at " + CREDENTIALS_FILE_PATH);
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            // Handle other I/O errors
            System.err.println("Error reading credentials file: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }





    public Event createEvent(Event eventDetails) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        return service.events().insert("primary", eventDetails).execute();
    }


}
