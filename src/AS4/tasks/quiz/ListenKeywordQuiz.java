package AS4.tasks.quiz;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import AS4.RobotBlackboard;
import pocketsphinx.PocketSphinx;
import pocketsphinx.RecognitionResult;

import javax.sound.sampled.*;

public class ListenKeywordQuiz extends LeafTask<RobotBlackboard> {
    final static String SPHINX_ROOT = "/home/root/sotaprograms/resources/sphinxmodel/";
    final static int SAMPLERATE = 16000;
    final static int BITRATE = 16;
    final static int CHANNELS = 1;
    final static int BUFFER_SIZE = 128 * SAMPLERATE * 2 / 1000;

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
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failed to initialize Microphone");
                return Status.FAILED;
            }
        
            // Start listening -- you call start and stop listening each time you want to reset and try a new utterance
            sphinx.startListening(decoderPtr);
            byte[] buffer = new byte[BUFFER_SIZE];
            boolean heard = false;
            int maxTimeMs = 5000; 
            int elapsed = 0;

            while (!heard && elapsed < maxTimeMs) { // detect keyword within 5 seconds
                int bytesRead = microphone.read(buffer, 0, buffer.length);
                sphinx.processAudio(decoderPtr, buffer, bytesRead);
                RecognitionResult result = sphinx.getRecognitionHypothesis(decoderPtr);
                if (!result.result.isEmpty()) {
                    bb.lastKeyword = result.result.toLowerCase();
                    bb.keywordDetected = true;
                    heard = true;
                }
                Thread.sleep(100);
                elapsed += 100;
            }

            // Stop listening
            microphone.stop();
            sphinx.stopListening(decoderPtr);

            RecognitionResult result = sphinx.getRecognitionHypothesis(decoderPtr);
            System.out.println("Final: " + result.result);

            // Clean up
            sphinx.cleanup(decoderPtr);            
            microphone.close();

            return heard ? Status.SUCCEEDED : Status.FAILED;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.FAILED;
        }
    }

    @Override
    protected Task<RobotBlackboard> copyTo(Task<RobotBlackboard> task) {
        return new ListenKeywordQuiz();
    }
}
