package com.rogurea.dev.view;

public interface IViewBlock {

    IViewBlock[] empty = new IViewBlock[]{};

    void Init();

    void Draw();

    void Reset();

}
