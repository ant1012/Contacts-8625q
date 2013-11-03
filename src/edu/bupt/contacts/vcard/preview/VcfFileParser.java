package edu.bupt.contacts.vcard.preview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class VcfFileParser {
    private static String BEGIN = "BEGIN:VCARD";
    private static String END = "END:VCARD";

    /**
     * "VERSION", "N", "FN", "TEL", "PHOTO"
     */
    private static String[] keys = new String[] { "VERSION", "N", "FN", "TEL", "PHOTO" };

    public String fileName;
    public List<String> list = new ArrayList<String>();

    public List<Vcard> getVcards() {
        return vcards;
    }

    public List<Vcard> vcards = new ArrayList<Vcard>();

    public VcfFileParser(String fileName) {
        this.fileName = fileName;
    }

    public void parse() throws Exception {
        File f = new File(fileName);
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String str = null;
        StringBuilder buf = new StringBuilder();
        int i = 0;
        while ((str = reader.readLine()) != null) {
            i++;
            if ("".equals(str)) {
                continue;
            } else if (BEGIN.equals(str)) {
                buf.delete(0, buf.length());
            } else if (END.equals(str)) {
                this.list.add(buf.toString());
            } else {
                buf.append(str + "\n");
            }
        }
        System.out.println("parse " + i + " lines");

        for (String s : list)
            vcards.add(parseVcard(s));
    }

    public Vcard parseVcard(String s) {
        String[] ss = s.split("\n");
        Vcard v = new Vcard();
        for (String tmp : ss) {
            if (tmp.startsWith(keys[0])) {
                v.version = tmp.substring(tmp.indexOf(":") + 1);
            } else if (tmp.startsWith(keys[1])) {
                if (tmp.indexOf("CHARSET") >= 0)
                    v.name = utf8_to_string(tmp.substring(tmp.lastIndexOf(":") + 2, tmp.length() - 3));
                else {
                    v.name = tmp.substring(tmp.indexOf(";") + 1, tmp.length() - 3);
                }
            } else if (tmp.startsWith(keys[2])) {
                if (tmp.indexOf("CHARSET") >= 0)
                    v.fname = utf8_to_string(tmp.substring(tmp.lastIndexOf(":") + 1));
                else {
                    v.fname = tmp.substring(tmp.indexOf(":") + 1);
                }
            } else if (tmp.startsWith(keys[3])) {
                v.tels.add(tmp.substring(tmp.lastIndexOf(":") + 1));
            } else if (tmp.startsWith(keys[4])) {
                // v.photo = tmp;

            }
        }
        return v;
    }

    public void output() {
        int i = 1;
        for (Vcard s : vcards)
            System.out.println(i++ + " " + s.toString());
    }

    class Vcard implements Comparable {
        String version;
        String name;
        String fname;
        List<String> tels = new ArrayList<String>();
        String photo;

        public String toString() {
            return "version=" + version + ", name=" + name + ", fname=" + fname + ", tel=" + tels();
            // ", photo=" + photo;

        }

        private String tels() {
            if (tels.size() == 0)
                return "";
            String str = "";
            for (String t : tels)
                str = str + t + ",";
            return str.substring(0, str.length() - 1);
        }

        public boolean equals(Object o) {
            if (o instanceof Vcard) {
                Vcard tmp = (Vcard) o;
                return this.name.equals(tmp.name) && this.fname.equals(tmp.fname) && this.tels.equals(tmp.tels);
            }
            return false;
        }

        @Override
        public int compareTo(Object o) {
            return this.name.compareToIgnoreCase(((Vcard) o).name);
        }
    }

    public static String utf8_to_string(String str) {
        str = str.replace(";", "");
        if (str.startsWith("="))
            str = str.substring(1);
        try {
            String[] ss = str.split("=");
            byte[] bs = new byte[ss.length];
            // {0xE4,0xBD,0x95,0xE4,0xBC,0x9F,0xE9,0x9D,0x99};

            for (int i = 0; i < bs.length; i++)
                bs[i] = (byte) Integer.parseInt(ss[i], 16);
            return new String(bs, "utf-8");
        } catch (Exception e) {
            System.err.println("Error: " + str);
            return str;
        }
    }

    public static void main(String[] args) {
        List<Vcard> l = new ArrayList<Vcard>();
        for (int i = 1; i <= 8; i++) {
            String filename = "c:/0000" + i + ".vcf";
            System.out.println(filename);
            VcfFileParser parser = new VcfFileParser(filename);
            try {
                parser.parse();
                // parser.output();

            } catch (Exception e) {
                e.printStackTrace();
            }
            l.addAll(parser.getVcards());
        }
        List<Vcard> l2 = new ArrayList<Vcard>();
        for (Vcard s : l) {
            if (l2.contains(s)) {
                System.out.println("s has equals " + s.toString());
            } else {
                l2.add(s);
            }
        }

        int i = 1;

        System.out.println("Both: ");
        for (Vcard s : l2) {
            System.out.println(i++ + " " + s.toString());
        }

        System.out.println("Short Num: ");
        i = 1;
        for (Vcard s : l2) {
            for (String t : s.tels) {
                if (t.length() < 4)
                    System.out.println(i++ + " " + s.toString());
            }
            if (!s.name.equals(s.fname))
                System.out.println(i++ + " " + s.toString());
        }
    }
}