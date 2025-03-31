package AS4.behaviors;

import com.badlogic.gdx.ai.btree.*;
import com.badlogic.gdx.ai.btree.branch.*;

import AS4.RobotBlackboard;
import AS4.tasks.*;
import jp.vstone.RobotLib.*;

public class MainBehavior {
    static final String TAG = "MainBehavior";   // set this to support the Sota logging system
	static final String RESOURCES = "../resources/";
	static final String SOUNDS = RESOURCES+"sound/";

    public static BehaviorTree<RobotBlackboard> makeTree(RobotBlackboard bb) {
        @SuppressWarnings("unchecked") // just to suppress the annoying message
        BehaviorTree<RobotBlackboard> tree = new BehaviorTree<RobotBlackboard>(
            // new Selector<RobotBlackboard>(
            //     new DetectFace(),
            //     new GreetUser()
            // )
        );
        tree.setObject(bb);
        return tree;
    }

    void run() {
        // Shared data structure to communicate within the tree
        RobotBlackboard blackboard = new RobotBlackboard();

        // Create a behavior tree manually
        // the BehaviorTree generic type is the blackboard type its expects
        BehaviorTree<RobotBlackboard> tree = makeTree(blackboard);

        final int MAX_TICKS = 10;
        int ticks = 0;
        while (ticks < MAX_TICKS) {

        }
    }

    public static void main(String args[]) {
        MainBehavior a4 = new MainBehavior();
        a4.run();
    }    
}
