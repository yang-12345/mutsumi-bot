package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BeatmapContent {
    private double bpm;
    private final List<HitObject> hitObjects = new ArrayList<>();
    private double od;
    private double hp;

    public BeatmapContent() {
    }

    public void setBpm(double bpm) {
        this.bpm = bpm;
    }

    public void setOd(double od) {
        this.od = od;
    }

    public void setHp(double hp) {
        this.hp = hp;
    }

    public void addObject(HitObject hitObject) {
        this.hitObjects.add(hitObject);
    }

    public String toFileContent() {
        this.hitObjects.sort(Comparator.comparingInt(HitObject::getTime));
        StringBuilder builder = new StringBuilder();
        String title = "Beatmap@" + Integer.toHexString(this.hashCode());
        builder.append(String.format("""
                osu file format v14
                
                [General]
                AudioFilename: audio.mp3
                AudioLeadIn: 0
                PreviewTime: -1
                Countdown: 0
                SampleSet: Normal
                StackLeniency: 0.7
                Mode: 3
                LetterboxInBreaks: 0
                SpecialStyle: 0
                WidescreenStoryboard: 0
                
                [Editor]
                DistanceSpacing: 0.8
                BeatDivisor: 8
                GridSize: 32
                TimelineZoom: 3.099999
                
                [Metadata]
                Title:%s
                TitleUnicode:%s
                Artist:Mutsumi
                ArtistUnicode:Mutsumi
                Creator:Mutsumi
                Version:OvO
                Source:Mutsumi
                Tags:random
                BeatmapID:0
                BeatmapSetID:-1
                
                [Difficulty]
                HPDrainRate:%.1f
                CircleSize:4
                OverallDifficulty:%.1f
                ApproachRate:5
                SliderMultiplier:1.4
                SliderTickRate:1
                
                [Events]
                //Background and Video events
                //Break Periods
                //Storyboard Layer 0 (Background)
                //Storyboard Layer 1 (Fail)
                //Storyboard Layer 2 (Pass)
                //Storyboard Layer 3 (Foreground)
                //Storyboard Layer 4 (Overlay)
                //Storyboard Sound Samples
                
                [TimingPoints]
                %d,%f,4,1,0,100,1,0
                
                
                [HitObjects]
                """,
                title, title, this.hp, this.od, this.hitObjects.getFirst().getTime(), 60000.0D / this.bpm));
        this.hitObjects.forEach(hitObject -> builder.append(hitObject.toSyntax()).append('\n'));
        return builder.toString();
    }
}
