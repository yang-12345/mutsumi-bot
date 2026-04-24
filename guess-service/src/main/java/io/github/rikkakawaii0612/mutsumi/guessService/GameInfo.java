package io.github.rikkakawaii0612.mutsumi.guessService;

import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger("GuessService");

    private final ObjectData/*User*/ user;
    private final List<ObjectData/*Beatmap*/> beatmaps;
    private final List<Character> openedCharacters;
    private final List<Character> implicitOpenedCharacters;
    private final List<Boolean> decrypted;
    private final boolean showArtist;
    private final boolean unicode;
    private final String mode;

    public GameInfo(ObjectData user,
                    List<ObjectData> beatmaps,
                    String mode,
                    boolean showArtist,
                    boolean unicode) {
        this.user = user;
        this.beatmaps = beatmaps;
        this.openedCharacters = new ArrayList<>();
        this.implicitOpenedCharacters = new ArrayList<>();
        this.decrypted = new ArrayList<>();
        for (int i = 0; i < this.beatmaps.size(); i++) {
            this.decrypted.add(false);
        }
        this.showArtist = showArtist;
        this.unicode = unicode;
        this.mode = mode;
    }

    public ObjectData getUser() {
        return this.user;
    }

    public boolean doesShowArtist() {
        return this.showArtist;
    }

    public boolean isUnicode() {
        return this.unicode;
    }

    public String getMode() {
        return this.mode;
    }

    public int getSongCount() {
        return this.beatmaps.size();
    }

    public boolean open(char character) {
        char c = Character.toLowerCase(character);
        if (character == ' ' || this.implicitOpenedCharacters.contains(c)) {
            return false;
        }
        this.openedCharacters.add(c);
        this.openedCharacters.sort(Character::compareTo);
        this.implicitOpenedCharacters.add(c);
        switch (c) {
            case 'a' -> this.implicitOpenedCharacters.addAll(List.of('あ', 'ア'));
            case 'i' -> this.implicitOpenedCharacters.addAll(List.of('い', 'イ'));
            case 'u' -> this.implicitOpenedCharacters.addAll(List.of('う', 'ウ'));
            case 'e' -> this.implicitOpenedCharacters.addAll(List.of('え', 'エ'));
            case 'o' -> this.implicitOpenedCharacters.addAll(List.of('お', 'オ'));
            case 'k' -> this.implicitOpenedCharacters.addAll(List.of('か', 'カ', 'き', 'キ', 'く', 'ク', 'け', 'ケ', 'こ', 'コ'));
            case 's' -> this.implicitOpenedCharacters.addAll(List.of('さ', 'サ', 'し', 'シ', 'す', 'ス', 'せ', 'セ', 'そ', 'ソ'));
            case 't' -> this.implicitOpenedCharacters.addAll(List.of('た', 'タ', 'ち', 'チ', 'つ', 'ツ', 'て', 'テ', 'と', 'ト'));
            case 'n' -> this.implicitOpenedCharacters.addAll(List.of('な', 'ナ', 'に', 'ニ', 'ぬ', 'ヌ', 'ね', 'ネ', 'の', 'ノ', 'ん', 'ン'));
            case 'h' -> this.implicitOpenedCharacters.addAll(List.of('は', 'ハ', 'ひ', 'ヒ', 'ふ', 'フ', 'へ', 'ヘ', 'ほ', 'ホ'));
            case 'm' -> this.implicitOpenedCharacters.addAll(List.of('ま', 'マ', 'み', 'ミ', 'む', 'ム', 'め', 'メ', 'も', 'モ'));
            case 'y' -> this.implicitOpenedCharacters.addAll(List.of('や', 'ヤ', 'ゆ', 'ユ', 'よ', 'ヨ'));
            case 'r' -> this.implicitOpenedCharacters.addAll(List.of('ら', 'ラ', 'り', 'リ', 'る', 'ル', 'れ', 'レ', 'ろ', 'ロ'));
            case 'w' -> this.implicitOpenedCharacters.addAll(List.of('わ', 'ワ', 'を', 'ヲ'));
            case 'g' -> this.implicitOpenedCharacters.addAll(List.of('が', 'ガ', 'ぎ', 'ギ', 'ʲ', 'ぐ', 'グ', 'げ', 'ゲ', 'ご', 'ゴ'));
            case 'z' -> this.implicitOpenedCharacters.addAll(List.of('ざ', 'ザ', 'じ', 'ジ', 'ʒ', 'ず', 'ズ', 'ぜ', 'ゼ', 'ぞ', 'ゾ'));
            case 'd' -> this.implicitOpenedCharacters.addAll(List.of('だ', 'ダ', 'ぢ', 'ヂ', 'ʒ', 'づ', 'ヅ', 'で', 'デ', 'ど', 'ド'));
            case 'b' -> this.implicitOpenedCharacters.addAll(List.of('ば', 'バ', 'び', 'ビ', 'ぶ', 'ブ', 'べ', 'ベ', 'ぼ', 'ボ'));
            case 'p' -> this.implicitOpenedCharacters.addAll(List.of('ぱ', 'パ', 'ぴ', 'ピ', 'ぷ', 'プ', 'ぺ', 'ペ', 'ぽ', 'ポ'));
        }
        return true;
    }

    public ObjectData getBeatmap(int index) {
        return this.beatmaps.get(index);
    }

    public List<ObjectData> getBeatmaps() {
        return this.beatmaps;
    }

    public String encrypt(String text) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = Character.toLowerCase(chars[i]);
            if (!this.implicitOpenedCharacters.contains(c) && c != ' ') {
                chars[i] = '_';
            }
        }
        return new String(chars);
    }

    public List<Character> getOpenedCharacters() {
        return this.openedCharacters;
    }

    public boolean isDecrypted(int index) {
        return this.decrypted.get(index);
    }

    public boolean guess(int index, String text) {
        if (this.decrypted.get(index)) {
            return false;
        }

        GuessModule module = GuessModule.getInstance();

        ObjectData beatmap = this.beatmaps.get(index);
        String title = beatmap.getString("title");
        Set<String> set = new HashSet<>(module.aliasSystem.getAliases(title));
        set.add(title);
        set.add(beatmap.getString("titleUnicode"));
        for (String s : set) {
            if (matches(s, text)) {
                this.decrypted.set(index, true);
                return true;
            }
        }
        return false;
    }

    private static boolean matches(String title, String text) {
        String str = title.toLowerCase(Locale.ROOT).replaceAll(" ", "");
        char[] chars = text.toLowerCase(Locale.ROOT).replaceAll(" ", "").toCharArray();
        int matches = 0, len = str.length();
        if (chars.length > 1.5D * len) {
            return false;
        }
        for (char c : chars) {
            int j = str.indexOf(c);
            if (j == -1) continue;
            matches++;
            str = str.substring(j + 1);
        }
        return matches >= Math.min(len, 4.0D * Math.cbrt(len));
    }

    public boolean isFinished() {
        return !this.decrypted.contains(false);
    }

    public List<String> query() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < this.beatmaps.size(); i++) {
            ObjectData/*Beatmap*/ beatmap = this.beatmaps.get(i);
            String artist = beatmap.getString(this.unicode ? "artistUnicode" : "artist");
            String title = beatmap.getString(this.unicode ? "titleUnicode" : "title");
            if (this.decrypted.get(i)) {
                list.add(artist + " - " + title);
            } else {
                String str = this.encrypt(title);
                if (this.showArtist) {
                    str = this.encrypt(artist) + " - " + str;
                }
                list.add(str);
            }
        }
        return list;
    }

    public void decryptAll() {
        Collections.fill(this.decrypted, true);
    }
}
