/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.resources;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;

import java.io.Serializable;

public class Model implements Serializable {

    public static final Model BLANK = new Model("", ' ');

    String modelName = "unnamed_model";

    char modelSymbol = '?';

    String modelColor = Colors.GREY;

    String modelBColor =  Colors.B_GREYSCALE_237;

    TextCharacter _model = new TextCharacter(' ');

    public TextColor getTextColor(String modelColor){
        return Colors.GetTextColor(modelColor);
    }

    private void settingModel(){
        this._model = _model.withCharacter(modelSymbol).withForegroundColor(getTextColor(modelColor));
    }

    private void settingModelWithBColor(){
        this._model = _model.withCharacter(modelSymbol).withForegroundColor(getTextColor(modelColor)).withBackgroundColor(getTextColor(modelBColor));
    }

    public void resetBColor(){
        this._model = _model.withCharacter(modelSymbol).withForegroundColor(getTextColor(modelColor)).withBackgroundColor(TextColor.ANSI.BLACK);
    }

    public Model(String modelName, String modelColor, char modelSymbol){
        this.modelName = modelName;
        this.modelSymbol = modelSymbol;
        this.modelColor = modelColor;
        settingModel();
    }

    public Model(String modelName, String modelFColor, String modelBColor, char modelSymbol){
        this(modelName, modelSymbol);
        this.modelColor = modelFColor;
        this.modelBColor = modelBColor;
        settingModelWithBColor();
    }

    public Model(String modelName, char modelSymbol){
        this.modelName = modelName;
        this.modelSymbol = modelSymbol;
        settingModel();
    }

    public Model(){}

    public String getModelName(){
        return this.modelName;
    }

    public String getModelColorName() {
        return this.modelColor + this.modelName + Colors.R;
    }

    public Model changeColor(String modelColor){
        this.modelColor = modelColor;
        settingModel();
        return this;
    }

    public Model changeBColor(String modelBColor){
        this.modelBColor = modelBColor;
        settingModelWithBColor();
        return this;
    }

    public Model changeModel(char modelSymbol){
        this.modelSymbol = modelSymbol;
        settingModel();
        return this;
    }

    public String getModelColor(){
       return this.modelColor;
    }

    public TextCharacter get(){
        return this._model;
    }

    @Override
    public String toString(){
        return ""+this.modelColor+this._model.getCharacter()+Colors.R;
    }
}
