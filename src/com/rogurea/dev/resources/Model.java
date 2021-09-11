package com.rogurea.dev.resources;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;

import java.io.Serializable;

public class Model implements Serializable {
    String modelName = "unnamed_model";

    char modelSymbol = '?';

    String modelColor = Colors.GREY;

    TextCharacter _model = new TextCharacter(' ');

    public TextColor getTextColor(String modelColor){
        return Colors.GetTextColor(modelColor);
    }

    private void settingModel(){
        this._model = _model.withCharacter(modelSymbol).withForegroundColor(getTextColor(modelColor));
    }

    public Model(String modelName, String modelColor, char modelSymbol){
        this.modelName = modelName;
        this.modelSymbol = modelSymbol;
        this.modelColor = modelColor;
        settingModel();
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

    public Model changeColor(String modelColor){
        this.modelColor = modelColor;
        settingModel();
        return this;
    }

    public Model changeModel(char modelSymbol){
        this.modelSymbol = modelSymbol;
        settingModel();
        return this;
    }

    public TextCharacter get(){
        return this._model;
    }
}
