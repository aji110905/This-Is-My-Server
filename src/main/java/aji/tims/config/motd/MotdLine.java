package aji.tims.config.motd;

import aji.tims.util.MutableStringUtil;
import aji.tims.mutable.MutableString;
import aji.tims.mutable.MutableStringType;

import java.util.LinkedList;

public class MotdLine {
    private final String string;
    private final LinkedList<MutableString> elements;

    public MotdLine(String string){
        this.string = string;
        this.elements = MutableStringUtil.parseStringToLinked(string, MutableStringType.MOTD);
    }

    public String parse(){
        return MutableStringUtil.parseLinkedToString(elements, null);
    }

    public String getString(){
        return string;
    }
}
