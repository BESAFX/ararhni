package com.besafx.app.controller;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileUploadController {

    private static final String ACCESS_TOKEN = "lwXbn73MQTAAAAAAAAAACtvJCtgSD7Rp5hwd7V8jM2V4O9I8c9javetzqM49b1-Y";

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file) throws JRException, IOException {

        // Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").withUserLocale("en_US").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        try {
            client.files().uploadBuilder("/".concat(file.getOriginalFilename())).uploadAndFinish(file.getInputStream());
        } catch (DbxException e) {
            e.printStackTrace();
        }

        return getPathLower("/".concat(file.getOriginalFilename()));
    }

    @RequestMapping(value = "/uploadFileAndGetShared", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String uploadFileAndGetShared(@RequestParam("file") MultipartFile file) throws JRException, IOException {

        // Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").withUserLocale("en_US").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        try {
            client.files().uploadBuilder("/".concat(file.getOriginalFilename())).uploadAndFinish(file.getInputStream());
        } catch (DbxException e) {
            e.printStackTrace();
        }

        return getSharedLink(getPathLower("/".concat(file.getOriginalFilename())));
    }

    @RequestMapping(value = "/getSharedLink", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getSharedLink(@RequestParam(value = "path") String path) {

        // Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").withUserLocale("en_US").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        SharedLinkMetadata metadata = null;
        String link = null;
        try {
            metadata = client.sharing().createSharedLinkWithSettings(path);
            link = metadata.getUrl().replaceAll("dl=0", "raw=1");
        } catch (DbxException e) {
            System.out.println(e.getMessage());
            try {
                link = client
                        .sharing()
                        .listSharedLinksBuilder()
                        .withPath(path)
                        .withDirectOnly(true).start().getLinks().get(0).getUrl().replaceAll("dl=0", "raw=1");
            } catch (DbxException e1) {
                System.out.println(e.getMessage());
            }
        }

        return link;
    }

    @RequestMapping(value = "/getPathLower", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getPathLower(String url) {

        // Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").withUserLocale("en_US").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        SharedLinkMetadata metadata = null;
        String link = null;
        try {
            metadata = client.sharing().createSharedLinkWithSettings(url);
            link = metadata.getPathLower();
        } catch (DbxException e) {
            System.out.println(e.getMessage());
            try {
                link = client
                        .sharing()
                        .listSharedLinksBuilder()
                        .withPath(url)
                        .withDirectOnly(true).start().getLinks().get(0).getPathLower();
            } catch (DbxException e1) {
                System.out.println(e.getMessage());
            }
        }

        return link;
    }
}
