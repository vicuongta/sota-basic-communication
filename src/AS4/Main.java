package AS4;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.btree.Task.Status;

import AS4.behaviors.*;
import AS4.conditions.*;
import AS4.tasks.DetectFaceFromMotion;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.camera.CRoboCamera;

public class Main {

    private static CRoboCamera cam;

    // Initialize camera and motion here safely
    static {
        try {
            CRobotMem mem = new CRobotMem();
            CSotaMotion motion = new CSotaMotion(mem);
            if (mem.Connect()) {
                motion.InitRobot_Sota();
                cam = new CRoboCamera("/dev/video0", motion);
                CRobotUtil.wait(1000); // Give time to stabilize
            } else {
                System.err.println("Failed to connect to Sota memory!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void run() {
        RobotBlackboard bb = new RobotBlackboard(); // shared blackboard
    
        BehaviorTree<RobotBlackboard> quizTree = QuizGameBehavior.createQuizTree(bb);
    
        @SuppressWarnings("unchecked")
        BehaviorTree<RobotBlackboard> tree = new BehaviorTree<>(
            new Sequence<RobotBlackboard>(
                // new Selector<RobotBlackboard>( 
                //     new HasFaceDetected(),
                //     new DetectFaceFromMotion(cam)
                // ),
                new Selector<RobotBlackboard> (
                    new HasQuizFinished(),
                    new SubTreeTask<>(quizTree)
                )
            )    
        );
        tree.setObject(bb);
    
        final int MAX_TICKS = 20;
        int ticks = 0;
    
        while (ticks < MAX_TICKS) {
            System.out.println("\nTick: " + ticks + " Start");
    
            tree.step(); // Step the behavior tree
    
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
