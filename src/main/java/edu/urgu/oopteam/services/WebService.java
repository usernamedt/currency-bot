package edu.urgu.oopteam.services;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Executable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@Service
public class WebService {
    public static String getPageAsString(String pageAddress, String codePage, List<Pair<String,String>> requestHeaders) throws IOException {
        StringBuilder sb = new StringBuilder();
        URL pageURL = new URL(pageAddress);
        URLConnection uc = pageURL.openConnection();
        if (!requestHeaders.isEmpty()){
            for(var header: requestHeaders){
                uc.setRequestProperty(header.getFirst(), header.getSecond());
            }
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        uc.getInputStream(), codePage))) {
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
        }
        return sb.toString();
    }


    public static String getPageAsString(String pageAddress, String codePage) throws Exception{
        return getPageAsString(pageAddress, codePage, new ArrayList<>());
    }
}
