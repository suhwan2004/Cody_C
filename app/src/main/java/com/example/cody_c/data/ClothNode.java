package com.example.cody_c.data;

public class ClothNode {
    private final int style;
    private String grade;

    public ClothNode(int style, String grade){
        this.style = style;
        this.grade = grade;
    }

    public int getStyle()
    {
        return this.style;
    }

    public String getGrade()
    {
        return this.grade;
    }
}
