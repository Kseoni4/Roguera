package kseoni.ch.roguera.graphics.ui.layout;

import kseoni.ch.roguera.base.Position;

/**
 * Виртуальная камера, преобразующая world-координаты карты в screen-координаты
 * внутри своего viewport (region на экране). Поддерживает edge-scroll: пока
 * цель находится в центральной deadzone, центр камеры не двигается; при выходе
 * за неё камера сдвигается так, чтобы цель снова попала в deadzone.
 *
 * scaleX — сколько колонок терминала занимает одна world-ячейка по горизонтали
 * (для рогалика обычно 2, чтобы клетка была "квадратной").
 */
public class Camera {

    private final Region viewport;
    private final int scaleX;

    /** Размер viewport в world-ячейках. */
    private final int viewCellsW;
    private final int viewCellsH;

    /** Размер deadzone в world-ячейках (по умолчанию — четверть viewport). */
    private final int deadzoneRadiusX;
    private final int deadzoneRadiusY;

    private Position worldCenter;

    public Camera(Region viewport, int scaleX, Position initialCenter) {
        this.viewport = viewport;
        this.scaleX = scaleX;
        this.viewCellsW = viewport.width() / scaleX;
        this.viewCellsH = viewport.height();
        this.deadzoneRadiusX = Math.max(1, viewCellsW / 4);
        this.deadzoneRadiusY = Math.max(1, viewCellsH / 4);
        this.worldCenter = initialCenter;
    }

    public void centerOn(Position worldPos) {
        this.worldCenter = worldPos;
    }

    /** Edge-scroll: подвинуть камеру, если цель вышла за пределы deadzone. */
    public void follow(Position worldPos) {
        int dx = worldPos.getX() - worldCenter.getX();
        int dy = worldPos.getY() - worldCenter.getY();
        int cx = worldCenter.getX();
        int cy = worldCenter.getY();
        if (dx >  deadzoneRadiusX) cx = worldPos.getX() - deadzoneRadiusX;
        if (dx < -deadzoneRadiusX) cx = worldPos.getX() + deadzoneRadiusX;
        if (dy >  deadzoneRadiusY) cy = worldPos.getY() - deadzoneRadiusY;
        if (dy < -deadzoneRadiusY) cy = worldPos.getY() + deadzoneRadiusY;
        worldCenter = new Position(cx, cy);
    }

    public Position worldToScreen(Position world) {
        int sx = viewport.x() + (world.getX() - worldCenter.getX() + viewCellsW / 2) * scaleX;
        int sy = viewport.y() + (world.getY() - worldCenter.getY() + viewCellsH / 2);
        return new Position(sx, sy);
    }

    /** Проверка, что точка попадает в viewport (полуоткрытый интервал). */
    public boolean isVisible(int sx, int sy) {
        return viewport.contains(sx, sy);
    }
}
