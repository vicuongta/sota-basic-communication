package AS4.motions;

import java.awt.Color;

import jp.vstone.RobotLib.*;

public class QuizTimeMotion {

    static final String TAG = "QuizTime";   // set this to support the Sota logging system
    static final String RESOURCES = "../resources/";
    static final String SOUNDS = RESOURCES + "sound/a4-sound/greet/start_quiz.wav";
    
    String initiateQuiz;

    public QuizTimeMotion(String initiateQuiz) {
        this.initiateQuiz = initiateQuiz;
    }

    public void run() {
        System.out.println("QuizTime running...");

        CRobotUtil.Log(TAG, "Start " + TAG);

        CRobotPose pose = new CRobotPose();  // classes to manage robot pose information
        CRobotMem mem = new CRobotMem(); // connector for the Sota's information system (VSMD), connects via internal socket.
        CSotaMotion motion = new CSotaMotion(mem);

        if (mem.Connect()) {
            CRobotUtil.Log(TAG, "connect " + TAG);
            motion.InitRobot_Sota();  // initialize the Sota VSMD
            CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());
            
            CRobotUtil.Log(TAG, "Servo On");
            motion.ServoOn();

            pose.setLED_Sota(Color.CYAN, Color.CYAN, 255, Color.CYAN);
            motion.play(pose, 1000);

            CPlayWave.PlayWave(this.initiateQuiz);
            
            pose = new CRobotPose();
            pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                new Short[]{0, -791, -666, 757, 718, 13, -36, 7}
            ); // initial pose
            motion.play(pose, 1000);
            motion.waitEndinterpAll();   // also async public boolean isEndInterpAll()
            
            boolean raise = true;
            for (int i = 0; i < 5; i++) {
                if (raise) {
                    pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                        new Short[]{11, -307, -860, 222, 857, 14, -149, 8}
                    ); // initial pose
                }
                else {
                    pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                        new Short[]{-2, 303, -480, -325, 415, 19, -148, 8}
                    ); // initial pose
                }
                raise = !raise;
                motion.play(pose, 1000);
                motion.waitEndinterpAll();
            }
        
            pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                new Short[]{0, -791, -666, 757, 718, 13, -36, 7}
            ); // initial pose

            motion.play(pose, 1000);
            motion.waitEndinterpAll();

            CRobotUtil.wait(500);   //pause the program / current thread

            motion.ServoOff();
            pose = new CRobotPose();
            pose.setLED_Sota(Color.WHITE, Color.WHITE, 0, Color.WHITE);
            motion.play(pose, 500);
            motion.waitEndinterpAll();
        }
    }

    public static void main(String[] args) {
        QuizTimeMotion quiz = new QuizTimeMotion(SOUNDS);
        quiz.run();
    }
}
