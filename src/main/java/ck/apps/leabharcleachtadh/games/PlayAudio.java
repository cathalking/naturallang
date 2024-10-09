package ck.apps.leabharcleachtadh.games;

import ck.apps.leabharcleachtadh.audio.AudioPlayer;
import ck.apps.leabharcleachtadh.audio.SpeechLookup;
import ck.apps.leabharcleachtadh.games.domain.UserInput;

import java.util.Scanner;

public class PlayAudio {

    static int promptColour = 36;

    public static void main(String[] args) {
        if (args.length > 0) {
            promptColour = Integer.parseInt(args[0]);
        }

        PlayAudio practice = new PlayAudio();
        practice.runSession();
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


}
