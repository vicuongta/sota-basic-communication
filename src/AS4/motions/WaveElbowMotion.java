package AS4.motions;

import java.awt.Color;

import jp.vstone.RobotLib.*;

public class WaveElbowMotion {
    static final String TAG = "ElbowWave";   // set this to support the Sota logging system
    static final String RESOURCES = "../resources/";
    static final String SOUNDS = RESOURCES + "sound/a4-sound/greet/greeting.wav";
    String greet;
    
    public WaveElbowMotion(String greet) {
        this.greet = greet;
    }
    public void run() {
        System.out.println("ElbowWave running...");

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

            pose.setLED_Sota(Color.BLACK, Color.BLACK, 255, Color.BLACK);
            motion.play(pose, 1000);

            pose.setLED_Sota(Color.WHITE, Color.WHITE, 255, Color.WHITE);
            motion.play(pose, 1000);

            CPlayWave.PlayWave(this.greet); // play greeting sound
            
            pose = new CRobotPose();
            pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                new Short[]{0, -714, -659, -267, 480, 20, -161, 8}
            ); // initial pose
            motion.play(pose, 1000);
            motion.waitEndinterpAll();   // also async public boolean isEndInterpAll()
            
            boolean wave = true;
            for (int i = 0; i < 5; i++) {
                if (wave) {
                    pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                        new Short[]{0, -714, -659, -267, 250, 20, -161, 8}
                    ); // initial pose
                }
                else {
                    pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                        new Short[]{0, -714, -659, -267, 480, 20, -161, 8}
                    ); // initial pose
                }
                wave = !wave;
                motion.play(pose, 1000);
                motion.waitEndinterpAll();
            }
        
            pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                new Short[]{0, -714, -659, -267, 480, 20, -161, 8}
            ); // initial pose

            motion.play(pose, 1000);
            motion.waitEndinterpAll();

            CRobotUtil.wait(500);   //pause the program / current thread

            motion.ServoOff();
            pose = new CRobotPose();
            pose.setLED_Sota(Color.BLACK, Color.BLACK, 0, Color.BLACK);
            pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 
                new Short[]{0, -791, -666, 757, 718, 13, -36, 7}
            ); // initial pose
            motion.play(pose, 500);
            motion.waitEndinterpAll();
        }
    }
}
