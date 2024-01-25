package rouge_dungeon_game.collider;

import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class EnemySpawnCollider extends Collider{
    static final long serialVersionUID = 20008;
    private String texture;
    public EnemySpawnCollider(Point position, Point size, String texture) {
        super(position, size);
        this.texture = texture;
    }

    public EnemySpawnCollider(Rectangle size) {
        super(size);}

    @Override
    public EnemySpawnCollider copy() {
        return new EnemySpawnCollider(this.size);
    }

    @Override
    public Color getColor() {
        return Color.MAGENTA;
    }

    public ArrayList<Point> getEnemyWalkLine() {
        ArrayList<Point> points = new ArrayList<>();
        if (this.size.w() >= this.size.h()) { // horizontally
            points.add(new Point(this.size.x(), this.size.y() + (this.size.h()/2)));
            points.add(new Point(this.size.x() + this.size.w(), this.size.y() + (this.size.h()/2)));
        } else { // vertical
            points.add(new Point(this.size.x() + (this.size.w()/2), this.size.y()));
            points.add(new Point(this.size.x() + (this.size.w()/2), this.size.y() + this.size.h()));
        }
        return points;
    }

    @Override
    public boolean collideable() {
        return false;
    }

    public String getTexture() {
        return texture;
    }
}
