package jmt.com.myapplication.helpers;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoColor {
    private static final List<MyColor> colors = new ArrayList<MyColor>(Arrays.asList(
            new MyColor("Red", "#e53935", "#ab000d", "#ff6f60"),
            new MyColor("Blue", "#1e88e5", "#005cb2", "#6ab7ff"),
            new MyColor("Green", "#43a047", "#00701a", "#76d275"),
            new MyColor("Yellow", "#fbc02d", "#c49000", "#fff263")
    ));

    public static MyColor COLOR(String mainColor) {
        for (MyColor myColor : colors) {
            if (myColor.Code.equals(mainColor)) return myColor;
        }
        return null;
    }

    public static class MyColor {
        private String Code;
        private String Main;
        private String Dark;
        private String Light;

        MyColor(String code, String main, String dark, String light) {
            Code = code;
            Main = main;
            Dark = dark;
            Light = light;
        }

        public String Code() {
            return Code;
        }

        public void setCode(String code) {
            Code = code;
        }

        public int Main() {
            return Color.parseColor(Main);
        }

        public void setMain(String main) {
            Main = main;
        }

        public int Dark() {
            return Color.parseColor(Dark);
        }

        public void setDark(String dark) {
            Dark = dark;
        }

        public int Light() {
            return Color.parseColor(Light);
        }

        public void setLight(String light) {
            Light = light;
        }
    }
}
