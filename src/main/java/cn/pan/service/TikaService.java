package cn.pan.service;

import cn.pan.model.UserDoc;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class TikaService {

    /**
     * 解析文档内容
     *
     * @param file
     * @return 文件内容
     */
    public UserDoc parserExtraction(File file) {

        String fileContent = "";//接收文档内容
        BodyContentHandler handler = new BodyContentHandler();
        Parser parser = new AutoDetectParser();//自动解析器接口
        Metadata metadata = new Metadata();
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            ParseContext context = new ParseContext();
            parser.parse(inputStream, handler, metadata, context);
            fileContent = handler.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        }
        return new UserDoc(file.getName(), fileContent);
    }

}
