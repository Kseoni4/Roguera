package com.rogurea.dev.resources;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;

import java.io.Serializable;
import java.util.Arrays;

public class Model implements Serializable {
    String modelName;

    char modelSymbol;

    String modelColor;

    TextCharacter _model = new TextCharacter(' ');

    public TextColor getTextColor(String modelColor){
        return Colors.GetTextColor(modelColor, "\u001b[38;5;");
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

    public String getModelName(){
        return this.modelName;
    }

    public void changeColor(String modelColor){
        this.modelColor = modelColor;
        settingModel();
    }

    public void changeModel(char modelSymbol){
        this.modelSymbol = modelSymbol;
        settingModel();
    }

    public TextCharacter get(){
        return this._model;
    }
}
