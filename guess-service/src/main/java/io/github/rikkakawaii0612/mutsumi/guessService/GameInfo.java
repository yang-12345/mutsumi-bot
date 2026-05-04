package io.github.rikkakawaii0612.mutsumi.guessService;

import io.github.rikkakawaii0612.mutsumi.osuApi.data.Beatmap;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.Beatmapset;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.PlayMode;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.User;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.*;

public class GameInfo {
    private static final HanyuPinyinOutputFormat PINYIN_FORMAT = new HanyuPinyinOutputFormat();
    private static final Map<Character, Character> JAPANESE_MAP = new HashMap<>();

    private final User user;
    private final List<Beatmap> beatmaps;
    private final List<Character> openedCharacters;
    private final List<Boolean> decrypted;
    private final boolean showArtist;
    private final boolean unicode;
    private final PlayMode mode;

    public GameInfo(User user,
                    List<Beatmap> beatmaps,
                    PlayMode mode,
                    boolean showArtist,
                    boolean unicode) {
        this.user = user;
        this.beatmaps = beatmaps;
        this.openedCharacters = new ArrayList<>();
        this.decrypted = new ArrayList<>();
        for (int i = 0; i < this.beatmaps.size(); i++) {
            this.decrypted.add(false);
        }
        this.showArtist = showArtist;
        this.unicode = unicode;
        this.mode = mode;
    }

    public User getUser() {
        return this.user;
    }

    public boolean doesShowArtist() {
        return this.showArtist;
    }

    public boolean isUnicode() {
        return this.unicode;
    }

    public PlayMode getMode() {
        return this.mode;
    }

    public int getSongCount() {
        return this.beatmaps.size();
    }

    public boolean open(char character) {
        char c = Character.toLowerCase(character);
        if (this.isInOpenedCharacters(c)) {
            return false;
        }
        this.openedCharacters.add(c);
        this.openedCharacters.sort(Character::compareTo);
        return true;
    }

    public Beatmap getBeatmap(int index) {
        return this.beatmaps.get(index);
    }

    public List<Beatmap> getBeatmaps() {
        return this.beatmaps;
    }

    public String encrypt(String text) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!this.isInOpenedCharacters(chars[i])) {
                chars[i] = '_';
            }
        }
        return new String(chars);
    }

    private boolean isInOpenedCharacters(char character) {
        char c = Character.toLowerCase(character);
        if (c == ' ' || this.openedCharacters.contains(c)) {
            return true;
        }

        // 检查汉字, 只要拼音首字母对得上就行
        try {
            String[] arr = PinyinHelper.toHanyuPinyinStringArray(c, PINYIN_FORMAT);
            if (arr != null) {
                for (String str : arr) {
                    if (this.openedCharacters.contains(str.charAt(0))) {
                        return true;
                    }
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination _) {
        }

        // 检查日文 (包括平假名, 片假名), 罗马音首字母对得上就行
        return JAPANESE_MAP.containsKey(c) && this.openedCharacters.contains(
                JAPANESE_MAP.get(c));
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

        Beatmap beatmap = this.beatmaps.get(index);
        String title = beatmap.beatmapset.title;
        Set<String> set = new HashSet<>(AliasSystem.getAliases(title));
        set.add(title);
        set.add(beatmap.beatmapset.titleUnicode);
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
            Beatmapset beatmapset = this.beatmaps.get(i).beatmapset;
            String artist = this.unicode ? beatmapset.artistUnicode : beatmapset.artist;
            String title = this.unicode ? beatmapset.titleUnicode : beatmapset.title;
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

    public void decrypt(int index) {
        this.decrypted.set(index, true);
    }



    static {
        PINYIN_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        PINYIN_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        List.of('あ', 'ア').forEach(c -> JAPANESE_MAP.put(c, 'a'));
        List.of('い', 'イ').forEach(c -> JAPANESE_MAP.put(c, 'i'));
        List.of('う', 'ウ').forEach(c -> JAPANESE_MAP.put(c, 'u'));
        List.of('え', 'エ').forEach(c -> JAPANESE_MAP.put(c, 'e'));
        List.of('お', 'オ').forEach(c -> JAPANESE_MAP.put(c, 'o'));
        List.of('か', 'カ', 'き', 'キ', 'く', 'ク', 'け', 'ケ', 'こ', 'コ').forEach(c -> JAPANESE_MAP.put(c, 'k'));
        List.of('さ', 'サ', 'し', 'シ', 'す', 'ス', 'せ', 'セ', 'そ', 'ソ').forEach(c -> JAPANESE_MAP.put(c, 's'));
        List.of('た', 'タ', 'ち', 'チ', 'つ', 'ツ', 'て', 'テ', 'と', 'ト').forEach(c -> JAPANESE_MAP.put(c, 't'));
        List.of('な', 'ナ', 'に', 'ニ', 'ぬ', 'ヌ', 'ね', 'ネ', 'の', 'ノ', 'ん', 'ン').forEach(c -> JAPANESE_MAP.put(c, 'n'));
        List.of('は', 'ハ', 'ひ', 'ヒ', 'ふ', 'フ', 'へ', 'ヘ', 'ほ', 'ホ').forEach(c -> JAPANESE_MAP.put(c, 'h'));
        List.of('ま', 'マ', 'み', 'ミ', 'む', 'ム', 'め', 'メ', 'も', 'モ').forEach(c -> JAPANESE_MAP.put(c, 'm'));
        List.of('や', 'ヤ', 'ゆ', 'ユ', 'よ', 'ヨ').forEach(c -> JAPANESE_MAP.put(c, 'y'));
        List.of('ら', 'ラ', 'り', 'リ', 'る', 'ル', 'れ', 'レ', 'ろ', 'ロ').forEach(c -> JAPANESE_MAP.put(c, 'r'));
        List.of('わ', 'ワ', 'を', 'ヲ').forEach(c -> JAPANESE_MAP.put(c, 'w'));
        List.of('が', 'ガ', 'ぎ', 'ギ', 'ʲ', 'ぐ', 'グ', 'げ', 'ゲ', 'ご', 'ゴ').forEach(c -> JAPANESE_MAP.put(c, 'g'));
        List.of('ざ', 'ザ', 'じ', 'ジ', 'ʒ', 'ず', 'ズ', 'ぜ', 'ゼ', 'ぞ', 'ゾ').forEach(c -> JAPANESE_MAP.put(c, 'z'));
        List.of('だ', 'ダ', 'ぢ', 'ヂ', 'ʒ', 'づ', 'ヅ', 'で', 'デ', 'ど', 'ド').forEach(c -> JAPANESE_MAP.put(c, 'd'));
        List.of('ば', 'バ', 'び', 'ビ', 'ぶ', 'ブ', 'べ', 'ベ', 'ぼ', 'ボ').forEach(c -> JAPANESE_MAP.put(c, 'b'));
        List.of('ぱ', 'パ', 'ぴ', 'ピ', 'ぷ', 'プ', 'ぺ', 'ペ', 'ぽ', 'ポ').forEach(c -> JAPANESE_MAP.put(c, 'p'));
    }
}
