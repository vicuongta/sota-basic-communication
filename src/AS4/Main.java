package AS4;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import AS4.behaviors.QuizGameBehavior;

public class Main {
    public static void main(String[] args) {
        RobotBlackboard bb = new RobotBlackboard();  // shared blackboard

        BehaviorTree<RobotBlackboard> tree = QuizGameBehavior.createQuizTree(bb);

        final int MAX_TICKS = 20;
        int tick = 0;
        while (tick < MAX_TICKS) {
            System.out.println("Tick " + tick);
            tree.step();  // step the behavior tree
            try {
                Thread.sleep(500);  // slow down the loop a little
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tick++;
        }
    }
}
