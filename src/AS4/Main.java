package AS4;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.Task.Status;

import AS4.behaviors.*;
import AS4.conditions.HasAnswerDetected;

public class Main {

    void run() {
        RobotBlackboard bb = new RobotBlackboard();  // shared blackboard

        BehaviorTree<RobotBlackboard> quizTree = QuizGameBehavior.createQuizTree(bb);

        @SuppressWarnings("unchecked") // just to suppress the annoying message
        BehaviorTree<RobotBlackboard> tree = new BehaviorTree<>(
            new Selector<RobotBlackboard>(
                new HasAnswerDetected(),  // check if the user has answered the quiz question
                new SubTreeTask<>(quizTree)  // if not, run the quiz game behavior tree
            )
        );
        tree.setObject(bb);  // set the blackboard for the tree

        final int MAX_TICKS = 10;
        int ticks = 0;

        while (ticks < MAX_TICKS) {
            // System.out.print("\033[H");  // move to origin

            System.out.println("\nTick: "+ticks+" Start");
            tree.step();  // step the behavior tree

            Status status = tree.getChild(0).getStatus();  // get the status of the tree
            System.out.println("Tick: "+ticks+" status: "+status.toString());

            try {
                Thread.sleep(1000);  // slow down the loop a little
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ticks++;
        }
    }
    public static void main(String[] args) {
        Main main = new Main();
        main.run();  // run the behavior tree
    }
}

