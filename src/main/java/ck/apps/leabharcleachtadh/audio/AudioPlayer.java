package ck.apps.leabharcleachtadh.audio;

import ck.apps.leabharcleachtadh.audio.SpeechLookup.Speed;
import ck.apps.leabharcleachtadh.games.domain.UserInput;
import org.apache.commons.io.FileUtils;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class AudioPlayer {

    public static void main(String[] args) {
        AudioPlayer practice = new AudioPlayer();
//        practice.runSession();
        final File file = new File("/var/tmp/audio.wav");
        practice.play(file);
    }

    private void runSession() {
        Scanner scanner = new Scanner(System.in);
        AudioPlayer audioPlayer = new AudioPlayer();

        System.out.print("Commands: Quit 'q'\n");

        while (true) {
            String input = scanner.nextLine();
            if (input.length() == 1) {
                UserInput userInput = UserInput.from(input);
                boolean keepGoing = true;
                if (userInput == UserInput.QUIT) {
                    System.out.print("Quitting...\n");
                    keepGoing = false;
                }
                if (!keepGoing) {
                    return;
                }
            } else {
                audioPlayer.playSentence(input, SpeechLookup.Speed.SLOWER);
            }
        }
    }

    public void playSentence(String s, Speed speed) {
        try {
            final File file = new File("/var/tmp/audio.wav");
            // final File file = new File("/var/tmp/abair/" + s + ".mp3");
            if (!file.exists()) {
                String url = SpeechLookup.toSpeech(s, speed);
                if (null == url) {
                    return;
                }
                FileUtils.copyURLToFile(new URL(url), file);
            }
            play(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void play(File file) {
        try (final AudioInputStream in = getAudioInputStream(file)) {

            final AudioFormat outFormat = getOutFormat(in.getFormat());
            final Info info = new Info(SourceDataLine.class, outFormat);

            try (final SourceDataLine line =
                         (SourceDataLine) AudioSystem.getLine(info)) {

                if (line != null) {
                    line.open(outFormat);
                    line.start();
                    stream(getAudioInputStream(outFormat, in), line);
                    line.drain();
                    line.stop();
                }
            }

        } catch (UnsupportedAudioFileException
                | LineUnavailableException
                | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void stream(AudioInputStream in, SourceDataLine line)
            throws IOException {
        final byte[] buffer = new byte[65536];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }
}
