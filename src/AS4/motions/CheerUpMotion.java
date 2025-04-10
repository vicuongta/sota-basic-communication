package AS4.motions;

import java.awt.Color;

import jp.vstone.RobotLib.*;

public class CheerUpMotion {
    static final String TAG = "CheerUpMotion";   // set this to support the Sota logging system
    static final String RESOURCES = "../resources/";
    static final String SOUNDS = RESOURCES + "sound/a4-sound/mode/quiz_mode";
    String correct;
    String answer;

    public CheerUpMotion(String correct, String answer) {
        this.correct = correct;
        this.answer = answer;
    }

    public void run() {
        System.out.println("CheerUpMotion running...");

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

            pose.setLED_Sota(Color.GREEN, Color.GREEN, 255, Color.GREEN);
            motion.play(pose, 1000);

            CPlayWave.PlayWave(this.correct);

            // Initial pose
            pose = new CRobotPose();
            pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                new Short[]{0, -791, -666, 757, 718, 13, -36, 7}
            ); 
            motion.play(pose, 1000);
            // motion.waitEndinterpAll();   // also async public boolean isEndInterpAll()
                    
            // Answer correct 
            boolean raise = true;
            for (int i = 0; i < 5; i++) {
                if (raise) {
                    pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                        new Short[]{-100, 407, -478, 218, 724, 19, -148, 8}
                    ); 
                }
                else {
                    pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                        new Short[]{100, -371, -801, -357, 542, 13, -149, 7}
                    ); 
                }
                raise = !raise;
                motion.play(pose, 1000);
                motion.waitEndinterpAll();
            }
            // Play cheering pose
            
            CRobotUtil.wait(500);   //pause the program / current thread
            CPlayWave.PlayWave(this.answer);
            CRobotUtil.wait(500);   //pause the program / current thread
            
            // Reset pose
            pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                new Short[]{0, -791, -666, 757, 718, 13, -36, 7}
            ); // initial pose

            motion.play(pose, 1000);
            motion.waitEndinterpAll();

            CRobotUtil.wait(500);   //pause the program / current thread

            motion.ServoOff();
            motion.waitEndinterpAll();
        }
    }
}
