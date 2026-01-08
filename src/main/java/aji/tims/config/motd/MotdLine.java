package aji.tims.config.motd;

import aji.tims.util.MutableStringUtil;
import aji.tims.mutable.MutableString;
import aji.tims.mutable.MutableStringType;

import java.util.LinkedList;

public class MotdLine {
    private LinkedList<MutableString> elements;
    private boolean isParsed;

    public MotdLine(String string){
        try {
            this.elements = MutableStringUtil.parseStringToLinked(string, MutableStringType.MOTD);
            isParsed = true;
        } catch (Exception e) {
            isParsed = false;
        }
    }

    public String parse(){
        return isParsed ? MutableStringUtil.parseLinkedToString(elements, null) : "";
    }
}
