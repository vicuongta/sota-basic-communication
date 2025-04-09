package AS4;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.Task.Status;

import AS4.behaviors.*;
import AS4.conditions.*;

public class Main {

    void run() {
        RobotBlackboard bb = new RobotBlackboard(); // shared blackboard
    
        BehaviorTree<RobotBlackboard> quizTree = QuizGameBehavior.createQuizTree(bb);
        BehaviorTree<RobotBlackboard> faceDetectTree = DetectFaceBehavior.createDetectFaceTree(bb);
    
        @SuppressWarnings("unchecked")
        BehaviorTree<RobotBlackboard> tree = new BehaviorTree<>(
                new Selector<RobotBlackboard>(
                        new HasFaceDetected(),
                        new SubTreeTask<>(faceDetectTree)
                )
        );
        tree.setObject(bb);
    
        final int MAX_TICKS = 10;
        int ticks = 0;
    
        while (ticks < MAX_TICKS) {
            System.out.println("\nTick: " + ticks + " Start");
    
            tree.step(); // Step the behavior tree
    
            // Since the root of tree is a Selector, get it
            Selector<RobotBlackboard> selector = (Selector<RobotBlackboard>) tree.getChild(0);
    
            Status firstChildStatus = selector.getChild(0).getStatus();
            Status secondChildStatus = selector.getChild(1).getStatus();
    
            System.out.println("First child (HasFaceDetected) status: " + firstChildStatus);
            System.out.println("Second child (SubTreeTask for faceDetectTree) status: " + secondChildStatus);
    
            Status status = tree.getStatus();
            System.out.println("Tick: " + ticks + " status: " + status);
    
            try {
                Thread.sleep(1000); // slow down the loop
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ticks++;
        }
    }
    

    public static void main(String[] args) {
        Main main = new Main();
        main.run(); // run the behavior tree
    }
}
