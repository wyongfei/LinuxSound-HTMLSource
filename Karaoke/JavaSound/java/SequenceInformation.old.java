

import javax.sound.midi.*;
import java.util.Vector;

public class SequenceInformation {

    private static Sequence sequence = null;
    private static Vector<LyricLine> lyricLines = null;
    private static Vector<DurationNote> melodyNotes = null;
    private static int lang = -1;
    private static String title = null;
    private static String performer = null;
    private static int maxNote = -1;
    private static int minNote = -1;

    private static int melodyChannel = 0;// default for Songken

    public static void setSequence(Sequence seq) {
	if (sequence != seq) {
	    sequence = seq;
	    lyricLines = null;
	    melodyNotes = null;
	    lang = -1;
	    title = null;
	    performer = null;
	    maxNote = -1;
	    minNote = -1;
	}
    }

    public static int getLanguage() {
	return lang;
    }

    public static String getTitle() {
	return title;
    }

    public static String getPerformer() {
	return performer;
    }

    /**
     * Build a vector of lyric lines
     * Each line has a start and an end tick
     * and a string for the lyrics in that line
     */
    public static Vector<LyricLine> getLyrics() {
	if (lyricLines != null) {
	    return lyricLines;
	}

	lyricLines = new Vector<LyricLine> ();
	LyricLine nextLyricLine = new LyricLine();
	StringBuffer buff = new StringBuffer();

	Track[] tracks = sequence.getTracks();
	for (int nTrack = 0; nTrack < tracks.length; nTrack++) {
	    for (int n = 0; n < tracks[nTrack].size(); n++) {
		MidiEvent evt = tracks[nTrack].get(n);
		MidiMessage msg = evt.getMessage();
		System.out.println("Got message");
		long ticks = evt.getTick();

		if (msg instanceof MetaMessage) {
		    System.out.println("Got a meta mesg in seq");
		    if (((MetaMessage) msg).getType() == Constants.MIDI_TEXT_TYPE) {
			System.out.println("Got a text mesg in seq");
			MetaMessage message = (MetaMessage) msg;

			byte[] data = message.getData();
			String str = new String(data);

			if (ticks == 0) {
			    if (str.startsWith("@L")) {
				    lang = decodeLang(str.substring(2));
			    } else if (str.startsWith("@T")) {
				if (title == null) {
				    title = str.substring(2);
				} else {
				    performer = str.substring(2);
				}
			    }
				
			}
			if (ticks > 0) {
			    //if (str.equals("\r") || str.equals("\n")) {
			    if ((data[0] == '/') || (data[0] == '\\')) {
				if (buff.length() == 0) {
				    // blank line -  maybe at start of song
				    // fix start time from NO_TICK
				    nextLyricLine.startTick = ticks;
				} else {
				    nextLyricLine.line = buff.toString();
				    nextLyricLine.endTick = ticks;
				    lyricLines.add(nextLyricLine);
				    buff.delete(0, buff.length());
				    // System.out.println("Lyric line is: " + nextLyricLine.line);
				    
				    nextLyricLine = new LyricLine();
				}
				buff.append(str.substring(1));
			    } else {
			    if (data[0] == 13) {
				if (buff.length() == 0) {
				    // blank line -  maybe at end of song
				    // fix start time from NO_TICK
				    nextLyricLine.startTick = ticks;
				}

				nextLyricLine.line = buff.toString();
				nextLyricLine.endTick = ticks;
				lyricLines.add(nextLyricLine);
				buff.delete(0, buff.length());
				// System.out.println("Lyric line is: " + nextLyricLine.line);

				nextLyricLine = new LyricLine();
				// if it was \ or /, add in the line following the \ or /
				if ((data[0] == '/') || (data[0] == '\\')) {
				    buff.append(str.substring(1));
				}
			    } else {
				if (nextLyricLine.startTick == Constants.NO_TICK) {
				    nextLyricLine.startTick = ticks;
				}
				buff.append(str);
			    }
			    }
			}
		    }
		}
	    }
	}
	return lyricLines;
    }

    public static Vector<DurationNote> getMelodyNotes() {
	if (melodyNotes != null) {
	    return melodyNotes;
	}

	melodyNotes = new Vector<DurationNote> ();
	Vector<DurationNote> unresolvedNotes = new Vector<DurationNote> ();
   
	Track[] tracks = sequence.getTracks();
	for (int nTrack = 0; nTrack < tracks.length; nTrack++) {
	    for (int n = 0; n < tracks[nTrack].size(); n++) {
		MidiEvent evt = tracks[nTrack].get(n);
		MidiMessage msg = evt.getMessage();
		long ticks = evt.getTick();

		if (msg instanceof ShortMessage) {
		    ShortMessage smsg= (ShortMessage) msg;
		    if (smsg.getChannel() == 0) {
			int note = smsg.getData1();
			if (note < Constants.MIDI_NOTE_A0 || note > Constants.MIDI_NOTE_C8) {
			    continue;
			}

			if (smsg.getCommand() == Constants.MIDI_NOTE_ON) {
			    // note on
			    DurationNote dnote = new DurationNote(ticks, note);
			    melodyNotes.add(dnote);
			    unresolvedNotes.add(dnote);

			} else if (smsg.getCommand() == Constants.MIDI_NOTE_OFF) {
			    // note off
			    for (int m = 0; m < unresolvedNotes.size(); m++) {
				DurationNote dnote = unresolvedNotes.elementAt(m);
				if (dnote.note == note) {
				    dnote.duration = ticks - dnote.startTick;
				    dnote.endTick = ticks;
				    unresolvedNotes.remove(m);
				}
			    }
				    
			}

		    }
		}
	    }
	}
	return melodyNotes;
    }

    public static int getMaxMelodyNote() {
	if (maxNote == -1) {
	    getMaxMin();
	}
	return maxNote;
    }

    public static int getMinMelodyNote() {
	if (maxNote == -1) {
	    getMaxMin();
	}
	return minNote;
    }

   private static void getMaxMin() {
	StringBuffer buff = new StringBuffer();

	Track[] tracks = sequence.getTracks();
	for (int nTrack = 0; nTrack < tracks.length; nTrack++) {
	    for (int n = 0; n < tracks[nTrack].size(); n++) {
		MidiEvent evt = tracks[nTrack].get(n);
		MidiMessage msg = evt.getMessage();
		long ticks = evt.getTick();

		if (msg instanceof ShortMessage) {
		    ShortMessage smsg= (ShortMessage) msg;
		    if (smsg.getChannel() == melodyChannel) {
			if (smsg.getCommand() != Constants.MIDI_NOTE_OFF) {
			    // not note on
			    continue;
			}
			int note = smsg.getData1();
			if (note < 21 || note > 107) {
			    continue;
			}
			//Debug.println("Note is " + note);
			if (note > maxNote) {
			    Debug.println("Setting max to "+ note);
			    maxNote = note;
			} else if (note < minNote) {
			    minNote = note;
			}
		    }
		}
	    }
	}
    }

    private static int decodeLang(String str) {
	if (str.equals("ENG")) {
	    return SongInformation.ENGLISH;
	}
	if (str.equals("CHI")) {
	    return SongInformation.CHINESE1;
	}
	return -1;
    }
}