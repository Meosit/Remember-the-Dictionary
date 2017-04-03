package by.mksn.rememberthedictionary.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import by.mksn.rememberthedictionary.model.Phrase;

public final class FileParserUtil {

    public static List<Phrase> readDOCX(File file) throws IOException, XmlPullParserException {
        ZipFile docxFile = new ZipFile(file);
        ZipEntry sharedStringXML = docxFile.getEntry("word/document.xml");
        InputStream inputStream = docxFile.getInputStream(sharedStringXML);
        XmlPullParser xmlParser = Xml.newPullParser();
        xmlParser.setInput(inputStream, "utf-8");
        int evtType = xmlParser.getEventType();
        boolean isReadEnd = false;
        boolean isInTable = false;
        boolean isInTableRow = false;
        boolean isFirstTableRowCell = false;
        boolean isInTableRowCell = false;
        String readString = "";
        Phrase phrase = null;
        List<Phrase> phrases = new ArrayList<>();
        while (evtType != XmlPullParser.END_DOCUMENT && !isReadEnd) {
            switch (evtType) {
                case XmlPullParser.START_TAG:
                    String tag = xmlParser.getName().toLowerCase();
                    switch (tag) {
                        case "tbl":
                            isInTable = true;
                            break;
                        case "tr":
                            isInTableRow = true;
                            isFirstTableRowCell = true;
                            phrase = new Phrase();
                            break;
                        case "tc":
                            isInTableRowCell = true;
                            readString = "";
                            break;
                        case "t":
                            if (isInTable && isInTableRow && isInTableRowCell) {
                                readString += xmlParser.nextText();
                            }
                            break;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tag = xmlParser.getName().toLowerCase();
                    switch (tag) {
                        case "tbl":
                            isReadEnd = true;
                            break;
                        case "tr":
                            phrases.add(phrase);
                            break;
                        case "tc":
                            if (isFirstTableRowCell) {
                                phrase.setPhrase(readString);
                            } else {
                                phrase.setTranslation(readString);
                            }
                            isFirstTableRowCell = false;
                            break;
                        case "t":
                            break;
                    }
                    break;
                default:
                    break;
            }
            evtType = xmlParser.next();
        }
        return phrases;
    }


}
