package com.example.rtlpropagation.controller;
import com.example.rtlpropagation.config.JenkinsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

@RestController
public class JenkinsController {

    @Autowired
    private JenkinsConfig jenkinsConfig;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/triggerjob")
    public ResponseEntity<String> triggerJenkinsJob() {
        try {

            // Step 1: Get Jenkins Crumb
            System.out.println("jenkinsConfig.getJenkinsToken() = " + jenkinsConfig.getJenkinsToken());
            HttpHeaders crumbHeaders = new HttpHeaders();
            crumbHeaders.setBasicAuth(jenkinsConfig.getJenkinsUsername(), jenkinsConfig.getJenkinsToken());
            HttpEntity<String> crumbEntity = new HttpEntity<>(crumbHeaders);
            ResponseEntity<String> crumbResponse = new RestTemplate().exchange(
                    jenkinsConfig.getJenkinsUrl() + "/crumbIssuer/api/xml",
                    HttpMethod.GET,
                    crumbEntity,
                    String.class
            );

            System.out.println("crumbResponse.getBody() --> " + crumbResponse.getBody());

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(crumbResponse.getBody().getBytes()));

            doc.getDocumentElement().normalize();

            Element crumbElement = (Element) doc.getElementsByTagName("crumb").item(0);

            String crumb = crumbElement.getTextContent();

            // Step 2: Trigger Jenkins Job with Crumb
            HttpHeaders jobHeaders = new HttpHeaders();
            jobHeaders.setBasicAuth(jenkinsConfig.getJenkinsUsername(), jenkinsConfig.getJenkinsToken());
            jobHeaders.set("Jenkins-Crumb", crumb);
            HttpEntity<String> jobEntity = new HttpEntity<>(jobHeaders);

            ResponseEntity<String> jobResponse = new RestTemplate().exchange(
                    jenkinsConfig.getJenkinsUrl() + "/job/" + "RTLPropagation" + "/build",
                    HttpMethod.POST,
                    jobEntity,
                    String.class
            );
            return ResponseEntity.ok("Jenkins job triggered successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error triggering Jenkins job.");
        }
    }
}

