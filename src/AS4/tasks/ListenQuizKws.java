package AS4.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import AS4.RobotBlackboard;
import jp.vstone.RobotLib.CPlayWave;
import pocketsphinx.PocketSphinx;
import pocketsphinx.RecognitionResult;

import javax.sound.sampled.*;

public class ListenQuizKws extends LeafTask<RobotBlackboard> {
    final static String SPHINX_ROOT = "/home/root/sotaprograms/resources/sphinxmodel/";
    final static int ms = 128;  // window size to process at a time. longer makes more delay but less processing. 
    final static int SAMPLERATE = 16000; // 8000, 16000, 22050, 44100, stick to 16k unless you know what you are doing
    final static int BITRATE = 16; // keep at 16
    final static int BUFFER_SIZE = ms*SAMPLERATE*2/1000; // Samplerate x 2 bytes per sample (16 bit) /1000 ms per second
    final static int CHANNELS = 1; // mono

    final static String PHRASE = "/home/root/sotaprograms/resources/sound/a4-sound/quiz/questions/Qphrases.wav"; // path to the sphinx model

    @Override
    public Status execute() {
        RobotBlackboard bb = getObject();
        try {
            PocketSphinx sphinx = new PocketSphinx();

            // Initialize PocketSphinx
            long decoderPtr = sphinx.initialize_kws(
                SPHINX_ROOT + "en-us/en-us",
                SPHINX_ROOT + "keyphrases.txt",
                SPHINX_ROOT + "en-us/cmudict-en-us.dict"
            );
            if (decoderPtr == 0) {
                System.err.println("Failed to initialize PocketSphinx");
                return Status.FAILED;
            }

            // Configure microphone
            TargetDataLine microphone = null;
            try {
                AudioFormat format = new AudioFormat(SAMPLERATE, BITRATE, CHANNELS, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                microphone = (TargetDataLine) AudioSystem.getLine(info);
                microphone.open(format);
                microphone.start();
                System.out.println("Microphone started... Speak now!");
                CPlayWave.PlayWave_wait(PHRASE);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failed to initialize Microphone");
                return Status.FAILED;
            }
        
            // Start listening -- you call start and stop listening each time you want to reset and try a new utterance
            sphinx.startListening(decoderPtr);
            System.out.println("Decoder Started");

            boolean keywordDetected = false;
            byte[] buffer = new byte[BUFFER_SIZE];

            // Set a timeout for the user to respond 5 seconds
            int maxTimeMs = 5000; 
            int elapsed = 0;
            
            
            if (elapsed > maxTimeMs) {
                System.out.println("User takes too long to answer, stop listening.");
                return Status.FAILED;
            }
            else {
                while (!keywordDetected) {

                    int bytesRead = microphone.read(buffer, 0, buffer.length);
                    sphinx.processAudio(decoderPtr, buffer, bytesRead);
                    
                    RecognitionResult result = sphinx.getRecognitionHypothesis(decoderPtr);
                    System.out.println("Result: " + result.result);
                    if (!result.result.isEmpty()) {
                        keywordDetected = true;
                        bb.userAnswerQuiz = result.result; // store the result in the blackboard
                        System.out.println("Keyword detected: " + result.result);
    
                        microphone.stop();
                        System.out.println("Microphone stopped.");
                        sphinx.stopListening(decoderPtr);  // call get recognition after stop listening again to do final result
    
                        // Cleanup
                        sphinx.cleanup(decoderPtr);
                        microphone.close();
                        return Status.SUCCEEDED;
                    }    
                } 
                return Status.RUNNING; // keep running until keyword is detected   
            }            
        } catch (Exception e) {
            e.printStackTrace();
            return Status.FAILED;
        }
    }

    @Override
    protected Task<RobotBlackboard> copyTo(Task<RobotBlackboard> task) {
        return new ListenQuizKws();
    }
}
