package AS4.motions;

import java.awt.Color;

import jp.vstone.RobotLib.*;

public class ComfortMotion {

    static final String TAG = "ComfortMotion";   // set this to support the Sota logging system
    static final String RESOURCES = "../resources/";
    static final String SOUNDS = RESOURCES + "sound/a4-sound/quiz/answer/comfort";
    String comfort;
    String wrong;
    String answer;

    public ComfortMotion(String comfort, String wrong, String answer) {
        this.comfort = comfort;
        this.wrong = wrong;
        this.answer = answer;
    }

    public void run() {
        System.out.println("ComfortMotion running...");

        CRobotUtil.Log(TAG, "Start " + TAG);

        CRobotPose pose = new CRobotPose();  // classes to manage robot pose information
        CRobotMem mem = new CRobotMem(); // connector for the Sota's information system (VSMD), connects via internal socket.
        CSotaMotion motion = new CSotaMotion(mem);

        if (mem.Connect()) {
            motion.InitRobot_Sota();  // initialize the Sota VSMD
            motion.ServoOn();

            pose.setLED_Sota(Color.ORANGE, Color.ORANGE, 255, Color.ORANGE);
            motion.play(pose, 1000);

            CPlayWave.PlayWave(this.comfort);
            
            pose = new CRobotPose();
            pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                new Short[]{0, -41, -921, 36, 897, 20, 111, 8}
            ); // initial pose
            motion.play(pose, 1000);
            motion.waitEndinterpAll();   // also async public boolean isEndInterpAll()
            
            boolean wave = true;
            for (int i = 0; i < 6; i++) {
                if (wave) {
                    pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                        new Short[]{-250, -41, -921, 36, 897, 20, 111, 8}
                    ); // initial pose
                }
                else {
                    pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                        new Short[]{250, -41, -921, 36, 897, 20, 111, 8}
                    ); // initial pose
                }
                wave = !wave;
                motion.play(pose, 1000);
                motion.waitEndinterpAll();
            }
            

            pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                new Short[]{0, -791, -666, 757, 718, 13, -36, 7}
            ); // initial pose

            motion.play(pose, 1000);
            CRobotUtil.wait(2000);   //pause the program / current thread
            motion.waitEndinterpAll();

            CPlayWave.PlayWave_wait(this.wrong);
            // CRobotUtil.wait(3000);   //pause the program / current thread

            CPlayWave.PlayWave_wait(this.answer);
            CRobotUtil.wait(500);   //pause the program / current thread

            motion.ServoOff();
            pose = new CRobotPose();
            pose.setLED_Sota(Color.WHITE, Color.WHITE, 0, Color.WHITE);
            motion.play(pose, 500);
            motion.waitEndinterpAll();
        }
    }
}
