package az;

import com.assemblyai.api.AssemblyAI;
import com.assemblyai.api.resources.transcripts.types.Transcript;
import com.assemblyai.api.resources.transcripts.types.TranscriptOptionalParams;
import com.assemblyai.api.resources.transcripts.types.TranscriptStatus;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        File audioFile = Recorder.recordVoice(5);

        if (audioFile != null) {
            AssemblyAI client = AssemblyAI.builder()
                    .apiKey("bc21291133f040788890d26dc2d4b6b0")
                    .build();

            var params = TranscriptOptionalParams.builder()
                    .speakerLabels(false)
                    .build();

            Transcript transcript = client.transcripts().transcribe(audioFile, params);

            if (transcript.getStatus().equals(TranscriptStatus.ERROR)) {
                System.err.println("Xeta: " + transcript.getError().get());
                System.exit(1);
            }

            System.out.println("Ses kaydi metni: " + transcript.getText().get());
        } else {
            System.out.println("Ses kaydedilmedi.");
        }
    }
}

class Recorder {
    private static final AudioFormat format = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, false);

    public static File recordVoice(int durationInSeconds) {
        try {
            TargetDataLine microphone;
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Mikrofon desteklenmir.");
                return null;
            }

            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            System.out.println("Ses yazilir... (" + durationInSeconds + " saniye)");

            File wavFile = new File("audio.wav");
            AudioInputStream audioStream = new AudioInputStream(microphone);

            new Thread(() -> {
                try {
                    AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, wavFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            Thread.sleep(durationInSeconds * 1000);

            microphone.stop();
            microphone.close();
            System.out.println("Kaydetme tamamlandı: " + wavFile.getAbsolutePath());
            return wavFile;

        } catch (LineUnavailableException  | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
