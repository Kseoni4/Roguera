/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.resources;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;

import java.io.Serializable;

public class Model implements Serializable {

    public static final Model BLANK = new Model("", ' ');

    String modelName = "unnamed_model";

    char modelSymbol = '?';

    String modelColor = Colors.GREY;

    String modelBColor =  "";

    public TextColor getTextColor(String modelColor){
        return Colors.GetTextColor(modelColor);
    }

    private TextCharacter settingModel(){
       return new TextCharacter(modelSymbol).withForegroundColor(getTextColor(modelColor));
    }

    private TextCharacter settingModelWithBColor(){
        return new TextCharacter(modelSymbol).withForegroundColor(getTextColor(modelColor)).withBackgroundColor(getTextColor(modelBColor));
    }

    public void resetBColor(){
        changeBColor(TextColor.ANSI.BLACK.toString());
    }

    public Model(String modelName, String modelColor, char modelSymbol){
        this.modelName = modelName;
        this.modelSymbol = modelSymbol;
        this.modelColor = modelColor;
    }

    public Model(String modelName, String modelFColor, String modelBColor, char modelSymbol){
        this(modelName, modelSymbol);
        this.modelColor = modelFColor;
        this.modelBColor = modelBColor;
    }

    public Model(String modelName, char modelSymbol){
        this.modelName = modelName;
        this.modelSymbol = modelSymbol;
    }

    public Model changeName(String modelName){
        this.modelName = modelName;
        return this;
    }

    public Model(){}

    public Model(Model model){
        this(model.getModelName(), model.getModelColor(), model.modelBColor, model.modelSymbol);
    }

    public String getModelName(){
        return this.modelName;
    }

    public String getModelColorName() {
        return this.modelColor + this.modelName + Colors.R;
    }

    public Model changeColor(String modelColor){
        this.modelColor = modelColor;
        return this;
    }

    public Model changeBColor(String modelBColor){
        this.modelBColor = modelBColor;
        return this;
    }

    public Model changeModel(char modelSymbol){
        this.modelSymbol = modelSymbol;
        return this;
    }

    public String getModelColor(){
       return this.modelColor;
    }

    public TextCharacter get(){
        if(this.modelBColor.equals("")){
            return settingModel();
        } else {
            return settingModelWithBColor();
        }
    }

    @Override
    public String toString(){
        return ""+this.modelColor+get().getCharacter()+Colors.R;
    }
}
