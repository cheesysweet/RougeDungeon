package rouge_dungeon_game.entity;


import rouge_dungeon_game.Options;
import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.collider.*;



public class EnemyHandling {

    private Enemy enemy;

    public EnemyHandling(EnemySpawnCollider enemySpawn) {
        enemy = new LogEnemy(Options.logSize,
                Options.charDamage,
                enemySpawn.getTexture(),
                enemySpawn.getEnemyWalkLine());

        enemy.setPosition(new Point(enemySpawn.getEnemyWalkLine().get(0).x() - (Options.logSize.w()/2),
                (int) (enemySpawn.getEnemyWalkLine().get(0).y() - (Options.logSize.h()*0.8))));

        enemy.damageCollider = new DamageCollider(new Rectangle(
                enemy.character.x(), enemy.character.y(),
                enemy.character.w() - 12, enemy.character.h() - 6
        ),5);

    }

    public Enemy getEnemy() {
        return enemy;
    }
}
