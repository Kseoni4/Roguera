package com.rogurea.main.resources;

import com.googlecode.lanterna.TextCharacter;

import java.io.Serializable;

public class Model implements Serializable {
    String modelName;

    char modelSymbol;

    String modelColor;

    TextCharacter _model = new TextCharacter(' ');

    private void settingModel(){
        this._model = _model.withCharacter(modelSymbol).withForegroundColor(Colors.GetTextColor(modelColor, "\u001b[38;5;"));
    }

    public Model(String modelName, String modelColor, char modelSymbol){
        this.modelName = modelName;
        this.modelSymbol = modelSymbol;
        this.modelColor = modelColor;
        settingModel();
    }

    public void changeColor(String modelColor){
        this.modelColor = modelColor;
        settingModel();
    }

    public TextCharacter get(){
        return this._model;
    }
}
